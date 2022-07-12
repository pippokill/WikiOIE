/**
 * Copyright (c) 2021, the WikiOIE AUTHORS.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Bari nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 *
 */
package di.uniba.it.wikioie.indexing.post;

import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.data.Config;
import di.uniba.it.wikioie.data.Pair;
import di.uniba.it.wikioie.data.Passage;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.process.WikiITSimpleDepExtractor;
import di.uniba.it.wikioie.training.Instance;
import di.uniba.it.wikioie.training.TrainingSet;
import di.uniba.it.wikioie.training.XGboostCoTraining;
import di.uniba.it.wikioie.udp.UDPParser;
import di.uniba.it.wikioie.udp.UDPSentence;
import di.uniba.it.wikioie.vectors.VectorReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoostError;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class WikiITSimpleDepXGboostPassageProcessor implements PassageProcessor {

    private final WikiITSimpleDepExtractor wie;

    private final File trainingfile;

    private final XGboostCoTraining tr = new XGboostCoTraining();

    private static final Logger LOG = Logger.getLogger(WikiITSimpleDepXGboostPassageProcessor.class.getName());

    private TrainingSet ts;

    private final UDPParser parser;

    private final VectorReader vr;

    private final Map<String, Object> params;

    private final int round;

    private Booster booster;

    /**
     *
     * @param trainingfile
     * @param C
     * @param solvername
     * @param vr
     */
    public WikiITSimpleDepXGboostPassageProcessor(File trainingfile, VectorReader vr, Map<String, Object> params, int round) {
        this.wie = new WikiITSimpleDepExtractor();
        this.trainingfile = trainingfile;
        this.parser = new UDPParser(Config.getInstance().getValue("udp.address"), Config.getInstance().getValue("udp.model"));
        this.params = params;
        this.round = round;
        this.vr = vr;
    }

    /**
     *
     * @param trainingfile
     * @param C
     * @param solvername
     */
    public WikiITSimpleDepXGboostPassageProcessor(File trainingfile, Map<String, Object> params, int round) {
        this(trainingfile, null, params, round);
    }

    /**
     *
     * @param passage
     * @return
     */
    @Override
    public Passage process(Passage passage) {
        try {
            if (booster == null) {
                LOG.log(Level.INFO, "Load XGboost supervised model {0}", trainingfile);
                vr.init();
                ts = tr.generateFeatures(trainingfile, parser, wie, vr);
                LOG.info("Training...");
                booster = tr.train(ts, params, round);
            }
            UDPSentence sentence = new UDPSentence(passage.getId(), passage.getText(), passage.getConll());
            List<Token> tokens = UDPParser.getTokens(sentence);
            Graph<Token, String> graph = UDPParser.getGraph(tokens);
            sentence.setTokens(tokens);
            sentence.setGraph(graph);
            List<Triple> triples = wie.extract(graph);
            TrainingSet testSet = new TrainingSet(ts.getDict());
            int id = 0;
            for (Triple triple : triples) {
                Set<String> fset = tr.generateFeatureSet(new Pair<>(sentence, triple));
                Instance inst = new Instance(id);
                for (String v : fset) {
                    Integer fid = ts.getId(v);
                    if (fid != null) {
                        inst.setFeature(fid, 1);
                    }
                }
                int sid = ts.getId("subj_score");
                inst.setFeature(sid, triple.getSubject().getScore());
                sid = ts.getId("obj_score");
                inst.setFeature(sid, triple.getObject().getScore());
                sid = ts.getId("t_score");
                inst.setFeature(sid, triple.getScore());
                if (vr != null) {
                    inst.addDenseVector(Utils.getVectorFeature(sentence, triple.getSubject(), vr));
                    inst.addDenseVector(Utils.getVectorFeature(sentence, triple.getPredicate(), vr));
                    inst.addDenseVector(Utils.getVectorFeature(sentence, triple.getObject(), vr));
                }
                testSet.addInstance(inst);
                id++;
            }
            Pair<Utils.CSRSparseData, Integer> p = Utils.getSparseData(ts);
            DMatrix matrix = new DMatrix(p.getA().rowHeaders, p.getA().colIndex, p.getA().data,
                    DMatrix.SparseType.CSR, p.getB());
            float[][] predict = booster.predict(matrix);
            for (int i = triples.size() - 1; i >= 0; i--) {
                if (predict[i][0] < 0.5) {
                    triples.remove(i);
                }
            }
            // CHECK THIS!
            for (Triple t : triples) {
                Utils.invertTriple(sentence, t);
            }
            // ==============
            Passage r = new Passage(passage.getId(), passage.getTitle(), passage.getText(), passage.getConll(),
                    triples.toArray(new Triple[triples.size()]));
            return r;
        } catch (IOException | XGBoostError ioex) {
            LOG.log(Level.SEVERE, "Error to classify passage", ioex);
            return passage;
        }
    }

}
