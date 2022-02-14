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

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.SolverType;
import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.data.Config;
import di.uniba.it.wikioie.data.Pair;
import di.uniba.it.wikioie.data.Passage;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.process.WikiITSimpleDepExtractor;
import di.uniba.it.wikioie.training.CoTraining;
import di.uniba.it.wikioie.training.TrainingSet;
import di.uniba.it.wikioie.udp.UDPParser;
import di.uniba.it.wikioie.udp.UDPSentence;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class WikiITSimpleDepSupervisedPassageProcessor implements PassageProcessor {

    private final WikiITSimpleDepExtractor wie;

    private final File trainingfile;

    private Model model = null;

    private final CoTraining tr = new CoTraining();

    private static final Logger LOG = Logger.getLogger(WikiITSimpleDepSupervisedPassageProcessor.class.getName());

    private TrainingSet ts;

    private final UDPParser parser;

    private final String solvername;

    private final double C;

    /**
     *
     * @param trainingfile
     * @param C
     * @param solvername
     */
    public WikiITSimpleDepSupervisedPassageProcessor(File trainingfile, Double C, String solvername) {
        this.wie = new WikiITSimpleDepExtractor();
        this.trainingfile = trainingfile;
        this.parser = new UDPParser(Config.getInstance().getValue("udp.address"), Config.getInstance().getValue("udp.model"));
        this.C = C;
        this.solvername = solvername;
    }

    /**
     *
     * @param passage
     * @return
     */
    @Override
    public Passage process(Passage passage) {
        try {
            if (model == null) {
                LOG.log(Level.INFO, "Load supervised model {0}", trainingfile);
                if (solvername.equalsIgnoreCase("SVC")) {
                    LOG.info("Solver: SVC.");
                    tr.setSolver(SolverType.L2R_L2LOSS_SVC);
                } else {
                    LOG.info("Solver: L2R.");
                    tr.setSolver(SolverType.L2R_LR);
                }
                ts = tr.generateFeatures(trainingfile, parser, wie);
                LOG.info("Training...");
                model = tr.train(ts, C);
            }
            UDPSentence sentence = new UDPSentence(passage.getId(), passage.getText(), passage.getConll());
            List<Token> tokens = UDPParser.getTokens(sentence);
            Graph<Token, String> graph = UDPParser.getGraph(tokens);
            sentence.setTokens(tokens);
            sentence.setGraph(graph);
            List<Triple> triples = wie.extract(graph);
            for (int i = triples.size() - 1; i >= 0; i--) {
                Set<String> fset = tr.generateFeatureSet(new Pair<>(sentence, triples.get(i)));
                Map<String, Integer> dict = ts.getDict();
                List<Feature> lf = new ArrayList<>();
                for (String v : fset) {
                    Integer fid = dict.get(v);
                    if (fid != null) {
                        lf.add(new FeatureNode(fid, 1));
                    }
                }
                int sid = dict.get("subj_score");
                lf.add(new FeatureNode(sid, triples.get(i).getSubject().getScore()));
                sid = dict.get("obj_score");
                lf.add(new FeatureNode(sid, triples.get(i).getObject().getScore()));
                sid = dict.get("t_score");
                lf.add(new FeatureNode(sid, triples.get(i).getScore()));
                double l = Linear.predict(model, lf.toArray(new Feature[lf.size()]));
                if (l == 0) {
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
        } catch (IOException ioex) {
            LOG.log(Level.SEVERE, "Error to classify passage", ioex);
            return passage;
        }
    }

}
