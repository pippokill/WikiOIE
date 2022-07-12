/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.training;

import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.data.*;
import di.uniba.it.wikioie.process.WikiExtractor;
import di.uniba.it.wikioie.process.WikiITSimpleDepExtractor;
import di.uniba.it.wikioie.udp.UDPParser;
import di.uniba.it.wikioie.udp.UDPSentence;
import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;

/**
 *
 * @author pierpaolo
 */
public class XGboostCoTraining {

    private double thPred = 0.85;

    private int ngram = 0;

    private static final Logger LOG = Logger.getLogger(XGboostCoTraining.class.getName());

    /**
     *
     */
    public enum BootstrappingHeaders {

        /**
         *
         */
        title,
        /**
         *
         */
        text,
        /**
         *
         */
        score,
        /**
         *
         */
        subject,
        /**
         *
         */
        predicate,
        /**
         *
         */
        object,
        /**
         *
         */
        label
    }

    /**
     *
     * @param P
     * @param R
     * @return
     */
    public double F(double P, double R) {
        return (P + R) == 0 ? 0 : 2 * P * R / (P + R);
    }

    private Pair<UDPSentence, Triple> ieprocessing(String text, UDPParser parser, WikiExtractor extractor, String subj, String pred, String obj) throws IOException {
        List<UDPSentence> sentences = parser.getSentences(text);
        for (UDPSentence s : sentences) {
            List<Triple> ts = extractor.extract(s.getGraph());
            for (Triple t : ts) {
                if (t.getSubject().getSpan().equals(subj) && t.getPredicate().getSpan().equals(pred) && t.getObject().getSpan().equals(obj)) {
                    return new Pair<>(s, t);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param pair
     * @return
     */
    public Set<String> generateFeatureSet(Pair<UDPSentence, Triple> pair) {
        Set<String> set = new HashSet<>();
        Triple triple = pair.getB();
        //PoS-tags into the subject
        Pair<String, Set<String>> pf = Utils.getPosFeature(pair.getA(), triple.getSubject());
        set.add("pos_subj" + pf.getA());
        for (String pos : pf.getB()) {
            set.add("pos_subj_t_" + pos);
        }
        //PoS-tags into the predicate
        pf = Utils.getPosFeature(pair.getA(), triple.getPredicate());
        set.add("pos_pred" + pf.getA());
        for (String pos : pf.getB()) {
            set.add("pos_pred_t_" + pos);
        }
        //PoS-tags into the object
        pf = Utils.getPosFeature(pair.getA(), triple.getObject());
        set.add("pos_obj" + pf.getA());
        for (String pos : pf.getB()) {
            set.add("pos_obj_t_" + pos);
        }
        //n-gram of the predicate
        set.add("pred_span_" + triple.getPredicate().getSpan().toLowerCase());
        //set of dependencies between subject and predicate
        Set<String> dependencies = Utils.getDependencies(pair.getA(), pair.getB().getSubject(), pair.getB().getPredicate());
        for (String f : dependencies) {
            set.add("S_" + f);
        }
        //set of dependencies between object and predicate
        dependencies = Utils.getDependencies(pair.getA(), pair.getB().getObject(), pair.getB().getPredicate());
        for (String f : dependencies) {
            set.add("O_" + f);
        }
        //PoS-tags into the sequence before the subject
        UDPSentence udp = pair.getA();
        Span subjSpan = triple.getSubject();
        int subjStart = subjSpan.getStart();
        List<Token> tokens = udp.getTokens();
        List<Token> prevTokens;
        String s = "";
        String posS = "";
        if (ngram < subjStart) {
            prevTokens = tokens.subList(subjStart - ngram, subjStart);
        } else {
            prevTokens = tokens.subList(0, subjStart);
        }
        for (Token t : prevTokens) {
            s = s.concat(t.getForm().toLowerCase() + "_");
            posS = posS.concat(t.getUpostag() + "_");
        }

        set.add("PrevS_" + s);
        set.add("PrevS_pos_" + posS);
        for (int i = 0; i < prevTokens.size(); i++) {
            set.add("PrevS_" + i + "_" + prevTokens.get(i).getForm().toLowerCase());
            set.add("PrevS_pos_" + i + "_" + prevTokens.get(i).getUpostag());
        }

        /*Span pre_subj;
        if (!prevTokens.isEmpty()) {
            pre_subj = new Span(s, prevTokens.get(0).getId(), prevTokens.get(prevTokens.size() - 1).getId());
        } else {
            pre_subj = new Span(s, 0, 0);
        }

        pf = Utils.getPosFeature(pair.getA(), pre_subj);
        set.add("pre_subj" + pf.getA());
        for (String pos : pf.getB()) {
            set.add("pre_subj_t_" + pos);
        }*/
        //PoS-tags into the sequence after the object
        Span objSpan = triple.getObject();
        int objEnd = objSpan.getEnd();
        List<Token> postTokens;
        int shift = tokens.size() - objEnd;
        if (ngram < shift) {
            postTokens = tokens.subList(objEnd, objEnd + ngram);
        } else {
            postTokens = tokens.subList(objEnd, tokens.get(tokens.size() - 1).getId());
        }
        s = "";
        posS = "";
        for (Token t : postTokens) {
            s = s.concat(t.getForm() + " ");
            posS = posS.concat(t.getUpostag() + "_");
        }

        set.add("PostO_" + s);
        set.add("PostO_pos_" + posS);
        for (int i = 0; i < postTokens.size(); i++) {
            set.add("PostO_" + i + "_" + postTokens.get(i).getForm().toLowerCase());
            set.add("PostO_pos_" + i + "_" + postTokens.get(i).getUpostag());
        }

        /*Span post_obj;
        if (!postTokens.isEmpty()) {
            post_obj = new Span(s, postTokens.get(0).getId(), postTokens.get(postTokens.size() - 1).getId());
        } else {
            post_obj = new Span(s, 0, 0);
        }

        pf = Utils.getPosFeature(pair.getA(), post_obj);
        set.add("post_obj" + pf.getA());
        for (String pos : pf.getB()) {
            set.add("post_obj_t_" + pos);
        }*/
        return set;
    }

    private List<FileInstance> loadUnlabelled(File inputFile) throws IOException {
        List<FileInstance> list = new ArrayList<>();
        Reader in = new FileReader(inputFile);
        Iterable<CSVRecord> records;
        if (inputFile.getName().endsWith(".tsv")) {
            records = CSVFormat.TDF.withFirstRecordAsHeader().parse(in);
        } else {
            records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        }
        int id = 0;
        for (CSVRecord record : records) {
            list.add(new FileInstance(id, record.get(BootstrappingHeaders.text),
                    record.get(BootstrappingHeaders.subject),
                    record.get(BootstrappingHeaders.predicate),
                    record.get(BootstrappingHeaders.object),
                    record.isSet(BootstrappingHeaders.score.toString()) ? Float.parseFloat(record.get(BootstrappingHeaders.score)) : 0));
            id++;
        }
        return list;
    }

    /**
     *
     * @param inputFile
     * @param parser
     * @param extractor
     * @param vr
     * @return
     * @throws IOException
     */
    public TrainingSet generateFeatures(File inputFile, UDPParser parser, WikiExtractor extractor, VectorReader vr) throws IOException {
        Reader in = new FileReader(inputFile);
        TrainingSet tr = new TrainingSet();
        Iterable<CSVRecord> records;
        if (inputFile.getName().endsWith(".tsv")) {
            records = CSVFormat.TDF.withFirstRecordAsHeader().parse(in);
        } else {
            records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        }
        int id = 0;
        for (CSVRecord record : records) {
            String text = record.get(BootstrappingHeaders.text);
            Pair<UDPSentence, Triple> pair = ieprocessing(text, parser, extractor, record.get(BootstrappingHeaders.subject), record.get(BootstrappingHeaders.predicate), record.get(BootstrappingHeaders.object));
            if (pair == null) {
                //LOG.warning("No triple");
            } else {
                Set<String> fset = generateFeatureSet(pair);
                Instance inst = new Instance(id);
                for (String v : fset) {
                    int fid = tr.addFeature(v);
                    inst.setFeature(fid, 1);
                }
                int sid = tr.addFeature("subj_score");
                inst.setFeature(sid, pair.getB().getSubject().getScore());
                sid = tr.addFeature("obj_score");
                inst.setFeature(sid, pair.getB().getObject().getScore());
                sid = tr.addFeature("t_score");
                inst.setFeature(sid, pair.getB().getScore());
                if (vr != null) {
                    inst.addDenseVector(Utils.getVectorFeature(pair.getA(), pair.getB().getSubject(), vr));
                    inst.addDenseVector(Utils.getVectorFeature(pair.getA(), pair.getB().getPredicate(), vr));
                    inst.addDenseVector(Utils.getVectorFeature(pair.getA(), pair.getB().getObject(), vr));
                }
                inst.setLabel(Integer.parseInt(record.get(BootstrappingHeaders.label)));
                tr.addInstance(inst);
            }
            id++;
        }
        in.close();
        return tr;
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public List<Integer> loadLabels(File file) throws IOException {
        List<Integer> labels = new ArrayList<>();
        FileReader in = new FileReader(file);
        Iterable<CSVRecord> records;
        if (file.getName().endsWith(".tsv")) {
            records = CSVFormat.TDF.withFirstRecordAsHeader().parse(in);
        } else {
            records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        }
        for (CSVRecord record : records) {
            labels.add(Integer.parseInt(record.get(BootstrappingHeaders.label)));
        }
        in.close();
        return labels;
    }

    /**
     *
     * @param map
     * @param ts
     * @param parser
     * @param extractor
     * @param vr
     * @return
     * @throws IOException
     */
    public TrainingSet generateTestFeatures(Map<String, Integer> map, List<FileInstance> ts, UDPParser parser, WikiExtractor extractor, VectorReader vr) throws IOException {
        TrainingSet tr = new TrainingSet(map);
        int id = 0;
        for (FileInstance finst : ts) {
            String text = finst.getText();
            Pair<UDPSentence, Triple> pair = ieprocessing(text, parser, extractor, finst.getSubject(), finst.getPredicate(), finst.getObject());
            if (pair == null) {
                Instance inst = new Instance(id);
                tr.addInstance(inst);
            } else {
                Set<String> fset = generateFeatureSet(pair);
                Instance inst = new Instance(id);
                for (String v : fset) {
                    Integer fid = tr.getId(v);
                    if (fid != null) {
                        inst.setFeature(fid, 1);
                    }
                }
                int sid = tr.getId("subj_score");
                inst.setFeature(sid, pair.getB().getSubject().getScore());
                sid = tr.getId("obj_score");
                inst.setFeature(sid, pair.getB().getObject().getScore());
                sid = tr.getId("t_score");
                inst.setFeature(sid, pair.getB().getScore());
                if (vr != null) {
                    inst.addDenseVector(Utils.getVectorFeature(pair.getA(), pair.getB().getSubject(), vr));
                    inst.addDenseVector(Utils.getVectorFeature(pair.getA(), pair.getB().getPredicate(), vr));
                    inst.addDenseVector(Utils.getVectorFeature(pair.getA(), pair.getB().getObject(), vr));
                }
                tr.addInstance(inst);
            }
            id++;
        }
        return tr;
    }

    /**
     *
     * @param ts
     * @param outputfile
     * @throws IOException
     */
    public void saveTr(TrainingSet ts, File outputfile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputfile));
        List<Integer> ids = new ArrayList<>(ts.getDict().values());
        Collections.sort(ids);
        for (Instance i : ts.getSet()) {
            writer.append(String.valueOf(i.getLabel()));
            for (Integer id : ids) {
                float v = i.getFeature(id);
                if (v != 0) {
                    writer.append(" ");
                    writer.append(id.toString()).append(":").append(String.valueOf(v));
                }
            }
            writer.newLine();
        }
        writer.close();
    }

    /**
     *
     * @param ts
     * @param params
     * @param round
     * @return
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public Booster train(TrainingSet ts, Map<String, Object> params, int round) throws XGBoostError {
        Pair<Utils.CSRSparseData, Integer> p = Utils.getSparseData(ts);
        DMatrix matrix = new DMatrix(p.getA().rowHeaders, p.getA().colIndex, p.getA().data,
                DMatrix.SparseType.CSR, p.getB());
        matrix.setLabel(p.getA().labels);
        Map<String, DMatrix> watches = new HashMap<String, DMatrix>() {
            {
                put("train", matrix);
            }
        };
        return XGBoost.train(matrix, params, round, watches, null, null);
    }

    /**
     *
     * @param trainFile
     * @param vr
     * @param k
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     * @throws java.io.IOException
     */
    public void kfold(File trainFile, int k, VectorReader vr) throws XGBoostError, IOException {
        UDPParser parser = new UDPParser(Config.getInstance().getValue("udp.address"), Config.getInstance().getValue("udp.model"));
        WikiExtractor ie = new WikiITSimpleDepExtractor();
        TrainingSet tr = generateFeatures(trainFile, parser, ie, vr);
        Pair<Utils.CSRSparseData, Integer> p = Utils.getSparseData(tr);
        DMatrix matrix = new DMatrix(p.getA().rowHeaders, p.getA().colIndex, p.getA().data,
                DMatrix.SparseType.CSR, p.getB());
        matrix.setLabel(p.getA().labels);
        Map<String, Object> params = new HashMap<>();
        params.put("eta", 0.4);
        params.put("max_depth", 12);
        params.put("verbosity", 1);
        params.put("seed", 42);
        params.put("objective", "binary:logistic");
        int round = 80;
        int[] depth = new int[]{15, 18};
        double[] etav = new double[]{0.1, 0.2, 0.3, 0.4};
        int[] roundv = new int[]{20, 40, 80, 100};
        for (int d : depth) {
            params.put("max_depth", d);
            for (double e : etav) {
                params.put("eta", e);
                for (int r : roundv) {
                    round = r;
                    String[] cv = XGBoost.crossValidation(matrix, params, round, k, new String[]{"auc"}, null, null);
                    System.out.println(d + "\t" + e + "\t" + r + "\t" + cv[cv.length - 2] + "\t" + cv[cv.length - 1]);
                }
            }
        }
    }

    /**
     *
     * @param booster
     * @param ts
     * @return
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public List<Pair<Integer, Integer>> testWithProb(Booster booster, TrainingSet ts) throws XGBoostError {
        List<Pair<Integer, Integer>> r = new ArrayList<>();
        Pair<Utils.CSRSparseData, Integer> p = Utils.getSparseData(ts);
        DMatrix matrix = new DMatrix(p.getA().rowHeaders, p.getA().colIndex, p.getA().data,
                DMatrix.SparseType.CSR, p.getB());
        float[][] predict = booster.predict(matrix);
        List<Instance> l = ts.getSet();
        for (int k = 0; k < l.size(); k++) {
            if (predict[k][0] >= 0.5) {
                if (Utils.map(predict[k][0], 0.5, 1, 0, 1) >= thPred) {
                    r.add(new Pair<>(l.get(k).getId(), 1));
                }
            } else {
                if (Utils.map(predict[k][0], 0, 0.5, 1, 0) >= thPred) {
                    r.add(new Pair<>(l.get(k).getId(), 0));
                }
            }
        }
        return r;
    }

    /**
     *
     * @param booster
     * @param ts
     * @return
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public List<Integer> test(Booster booster, TrainingSet ts) throws XGBoostError {
        Pair<Utils.CSRSparseData, Integer> p = Utils.getSparseData(ts);
        DMatrix matrix = new DMatrix(p.getA().rowHeaders, p.getA().colIndex, p.getA().data,
                DMatrix.SparseType.CSR, p.getB());
        float[][] predict = booster.predict(matrix);
        List<Instance> l = ts.getSet();
        List<Integer> r = new ArrayList<>();
        for (int k = 0; k < l.size(); k++) {
            //System.out.println(Arrays.toString(predict[k]));
            r.add(predict[k][0] < 0.5 ? 0 : 1);
        }
        return r;
    }

    /**
     *
     * @param booster
     * @param ts
     * @return
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public List<Pair<Integer, Integer>> testWithoutProb(Booster booster, TrainingSet ts) throws XGBoostError {
        List<Pair<Integer, Integer>> r = new ArrayList<>();
        Pair<Utils.CSRSparseData, Integer> p = Utils.getSparseData(ts);
        DMatrix matrix = new DMatrix(p.getA().rowHeaders, p.getA().colIndex, p.getA().data,
                DMatrix.SparseType.CSR, p.getB());
        float[][] predict = booster.predict(matrix);
        List<Instance> l = ts.getSet();
        for (int k = 0; k < l.size(); k++) {
            r.add(new Pair<>(l.get(k).getId(), predict[k][0] < 0.5 ? 0 : 1));
        }
        return r;
    }

    private double computeBalance(TrainingSet ts) {
        double n = 0;
        for (Instance inst : ts.getSet()) {
            if (inst.getLabel() == 1) {
                n++;
            }
        }
        return n / (double) ts.getSet().size();
    }

    private void balance(List<Pair<Integer, Integer>> r, double bal) {
        Collections.sort(r, new PairBComparator());
        double n1 = 0;
        for (Pair<Integer, Integer> p : r) {
            if (p.getB() == 1) {
                n1++;
            }
        }
        double newbal = n1 / (double) r.size();
        if (newbal > bal) {
            while (newbal > bal) {
                if (r.get(r.size() - 1).getB() == 1) {
                    r.remove(r.size() - 1);
                    n1--;
                    newbal = n1 / (double) r.size();
                } else {
                    break;
                }
            }
        } else {
            while (newbal < bal) {
                if (r.get(0).getB() == 0) {
                    r.remove(0);
                    newbal = n1 / (double) r.size();
                } else {
                    break;
                }
            }
        }
        System.out.println("New balance=" + newbal);
    }

    /**
     *
     * @param annotatedFile
     * @param dataFile
     * @param outputdir
     * @param vr
     * @param p
     * @param maxit
     * @param params
     * @param round
     * @throws IOException
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public void cotraining(File annotatedFile, File dataFile, String outputdir, VectorReader vr, int p, int maxit, Map<String, Object> params, int round) throws IOException, XGBoostError {
        UDPParser parser = new UDPParser(Config.getInstance().getValue("udp.address"), Config.getInstance().getValue("udp.model"));
        WikiExtractor ie = new WikiITSimpleDepExtractor();
        List<FileInstance> unlabelled = loadUnlabelled(dataFile);
        int it = 0;
        while (true) {
            // no enough unlabelled data
            if (unlabelled.size() < p) {
                break;
            }
            // shuffle unlabelled
            Collections.shuffle(unlabelled);
            // select p unlabelled data
            List<FileInstance> subList = unlabelled.subList(0, p);
            // build training
            TrainingSet ts = generateFeatures(annotatedFile, parser, ie, vr);
            Utils.saveDict(ts.getDict(), new File(outputdir + "/tr_" + it + ".dict"));
            double bal = computeBalance(ts);
            System.out.println("Balance=" + bal);
            LOG.info("Training...");
            // build model
            Booster booster = train(ts, params, round);
            LOG.info("Testing...");
            // classify unlabeled data
            List<Pair<Integer, Integer>> r;
            if (thPred > 0) {
                r = testWithProb(booster, generateTestFeatures(ts.getDict(), subList, parser, ie, vr));
            } else {
                r = testWithoutProb(booster, generateTestFeatures(ts.getDict(), subList, parser, ie, vr));
            }
            balance(r, bal);
            LOG.log(Level.INFO, "New examples {0}", r.size());
            // save new training set
            File newFile = new File(outputdir + "/tr_" + it + ".tsv");
            Utils.copyByLine(annotatedFile, newFile);
            FileWriter writer = new FileWriter(newFile, true);
            CSVPrinter printer = CSVFormat.TDF.print(writer);
            for (Pair<Integer, Integer> o : r) {
                FileInstance fi = unlabelled.get(o.getA());
                printer.printRecord("no title", fi.getText(), fi.getSubject(), fi.getPredicate(), fi.getObject(), o.getB().toString());
            }
            writer.close();
            annotatedFile = newFile;
            // remove
            Set<Integer> removeId = new HashSet<>();
            for (Pair<Integer, Integer> o : r) {
                removeId.add(o.getA());
            }
            for (int k = unlabelled.size() - 1; k >= 0; k--) {
                if (removeId.contains(unlabelled.get(k).getId())) {
                    unlabelled.remove(k);
                }
            }
            if (r.isEmpty()) {
                System.out.println("Interrupted...no new instances.");
                break;
            }
            it++;
            if (it >= maxit) {
                System.out.println("Interrupted...max iterations.");
                break;
            }
        }
    }

    /**
     *
     * @param annotatedFile
     * @param dataFile
     * @param outputdir
     * @param p
     * @param maxit
     * @param params
     * @param round
     * @throws IOException
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public void cotraining(File annotatedFile, File dataFile, String outputdir, int p, int maxit, Map<String, Object> params, int round) throws IOException, XGBoostError {
        cotraining(annotatedFile, dataFile, outputdir, null, p, maxit, params, round);
    }

    /**
     *
     * @param trainFile
     * @param testFile
     * @param vr
     * @param params
     * @param round
     * @throws IOException
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public void trainAndTest(File trainFile, File testFile, VectorReader vr, Map<String, Object> params, int round) throws IOException, XGBoostError {
        UDPParser parser = new UDPParser(Config.getInstance().getValue("udp.address"), Config.getInstance().getValue("udp.model"));
        WikiExtractor ie = new WikiITSimpleDepExtractor();
        TrainingSet tr = generateFeatures(trainFile, parser, ie, vr);
        Booster booster = train(tr, params, round);
        List<FileInstance> unlabelled = loadUnlabelled(testFile);
        List<Integer> labels = loadLabels(testFile);
        TrainingSet ts = generateTestFeatures(tr.getDict(), unlabelled, parser, ie, vr);
        List<Integer> pred = test(booster, ts);
        computeMetrics(labels, pred, null);
    }

    /**
     *
     * @param trainFile
     * @param testFile
     * @param params
     * @param round
     * @throws IOException
     * @throws ml.dmlc.xgboost4j.java.XGBoostError
     */
    public void trainAndTest(File trainFile, File testFile, Map<String, Object> params, int round) throws IOException, XGBoostError {
        trainAndTest(trainFile, testFile, null, params, round);
    }

    /**
     *
     * @param labels
     * @param predicted
     * @param metricsPath
     */
    public void computeMetrics(List<Integer> labels, List<Integer> predicted, String metricsPath) {
        // remove no predicted instances
        int rn = 0;
        for (int i = predicted.size() - 1; i >= 0; i--) {
            if (predicted.get(i) == null) {
                predicted.remove(i);
                labels.remove(i);
                rn++;
            }
        }
        LOG.log(Level.WARNING, "Removed {0} predictions.", rn);
        int[][] m = new int[2][2];
        for (int i = 0; i < labels.size(); i++) {
            m[labels.get(i)][predicted.get(i)]++;
        }
        System.out.println("\tPred.");
        System.out.println("     *--------*--------*");
        System.out.printf("     |%8d|%8d|%n", m[0][0], m[0][1]);
        System.out.println("Lab. *--------*--------*");
        System.out.printf("     |%8d|%8d|%n", m[1][0], m[1][1]);
        System.out.println("     *--------*--------*");
        double p0 = (m[0][0] + m[1][0]) == 0 ? 0 : (double) m[0][0] / (double) (m[0][0] + m[1][0]);
        double r0 = (m[0][0] + m[0][1]) == 0 ? 0 : (double) m[0][0] / (double) (m[0][0] + m[0][1]);
        double p1 = (m[0][1] + m[1][1]) == 0 ? 0 : (double) m[1][1] / (double) (m[0][1] + m[1][1]);
        double r1 = (m[1][0] + m[1][1]) == 0 ? 0 : (double) m[1][1] / (double) (m[1][0] + m[1][1]);
        System.out.println("P_0\t" + p0);
        System.out.println("R_0\t" + r0);
        System.out.println("P_1\t" + p1);
        System.out.println("R_1\t" + r1);
        System.out.println("F_0\t" + F(p0, r0));
        System.out.println("F_1\t" + F(p1, r1));
        System.out.println("F_M\t" + (F(p0, r0) + F(p1, r1)) / 2);
        System.out.println("acc.\t" + ((double) (m[0][0] + m[1][1]) / (double) (m[0][0] + m[0][1] + m[1][0] + m[1][1])));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            XGboostCoTraining ct = new XGboostCoTraining();
            VectorReader vr = new LuceneVectorReader(new File("/home/pierpaolo/data/fasttext/cc.it.300.vec.index"));
            vr.init();
            // set the learning algorithm
            //ct.setSolver(SolverType.L2R_LR);
            //ct.setSolver(SolverType.L2R_LR);
            // set the threshold used during self-training
            ct.setThPred(0.7);
            //ct.setThPred(0.0);   // in case of SVC
            ct.setNgram(3);
            Map<String, Object> params = new HashMap<>();
            params.put("eta", 0.4);
            params.put("max_depth", 12);
            params.put("verbosity", 1);
            params.put("seed", 42);
            params.put("objective", "binary:logistic");
            int round = 80;
            // start co-training. Paramters: annotated data, unlabelled data, unlabelled data added to each iteration, max iterations, C
            /*ct.cotraining(new File("/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/training_set.tsv"),
                    new File("/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/u_triples_dd.tsv"),
                    "/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/cotr_08",
                    200, 20);*/
            // evaluate the training set obtained by the self-training
            //ct.trainAndTest(new File("resources/bootstrapping/new_reg/tr_19"), new File("resources/bootstrapping/bootstrapping_test.csv"), 10);
            //ct.kfold(new File("/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/training_set.tsv"), 5, vr);
            /*ct.trainAndTest(new File("/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/cotr_08/tr_19.tsv"),
                    new File("/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/test_set.tsv"), vr);*/
            ct.trainAndTest(new File("/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/el/training_set_16.tsv"),
                    new File("/home/pierpaolo/Scaricati/temp/siap/oie/OIE_new/el/test_set.tsv"), vr, params, round);
            //ct.trainAndTest(new File("C:/Users/angel/Documents/OIE4PA/Dataset/L/training/training_set.tsv"),
            //new File("C:/Users/angel/Documents/OIE4PA/Dataset/L/test/test_set.tsv"),
            //vr, 2);
        } catch (IOException | XGBoostError ex) {
            Logger.getLogger(XGboostCoTraining.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public double getThPred() {
        return thPred;
    }

    /**
     *
     * @param thPred
     */
    public void setThPred(double thPred) {
        this.thPred = thPred;
    }

    /**
     *
     * @param n
     */
    public void setNgram(int n) {
        this.ngram = Math.abs(n);
    }

}
