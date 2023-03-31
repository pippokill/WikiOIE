/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.data.Counter;
import di.uniba.it.wikioie.vectors.VectorReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Alessia
 */
public class TripleSim {

    private final String dir;

    private IndexSearcher searcher;

    private final QueryParser queryParser = new QueryParser("pred", new StandardAnalyzer(CharArraySet.EMPTY_SET));

    private int maxdoc = 0;

    public TripleSim(String dir) {
        this.dir = dir;
    }

    public void open() throws IOException {
        FSDirectory fsdir = FSDirectory.open(new File(dir).toPath());
        searcher = new IndexSearcher(DirectoryReader.open(fsdir));
        maxdoc = searcher.getIndexReader().maxDoc();
    }

    public int indexDimension() throws IOException {
        return maxdoc;
    }

    // PIERPAOLO
    public List<Counter> discoverSimilPred(String pred, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        List<Triple> predTriples = searchTriple("pred:" + pred, n);
        Map<String, Counter> m = new HashMap<>();
        for (Triple t : predTriples) {
            if (t.getPred().equalsIgnoreCase(pred)) {
                List<Integer> rSO = searchSimSubjObj(t.getSub(), t.getObj(), vr, n, cosine_threshold);
                for (int docid : rSO) {
                    String p = searcher.doc(docid).get("pred").toLowerCase();
                    Counter<String> v = m.get(p);
                    if (v == null) {
                        m.put(p, new Counter<>(p, 1));
                    } else {
                        v.increment();
                    }
                }
            }
        }
        List<Counter> l = new ArrayList<>(m.values());
        Collections.sort(l, Collections.reverseOrder());
        return l;
    }

    public List<Counter> discoverSimilSubj(String subj, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        List<Triple> subjTriples = searchTriple("subj:" + subj, n);
        Map<String, Counter> m = new HashMap<>();
        for (Triple t : subjTriples) {
            if (t.getSub().equalsIgnoreCase(subj)) {
                List<Integer> rSO = searchSimPredObj(t.getPred(), t.getObj(), vr, n, cosine_threshold);
                for (int docid : rSO) {
                    String p = searcher.doc(docid).get("subj").toLowerCase();
                    Counter<String> v = m.get(p);
                    if (v == null) {
                        m.put(p, new Counter<>(p, 1));
                    } else {
                        v.increment();
                    }
                }
            }
        }
        List<Counter> l = new ArrayList<>(m.values());
        Collections.sort(l, Collections.reverseOrder());
        return l;
    }

    public List<Counter> discoverSimilObj(String obj, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        List<Triple> objTriples = searchTriple("obj:" + obj, n);
        Map<String, Counter> m = new HashMap<>();
        for (Triple t : objTriples) {
            if (t.getObj().equalsIgnoreCase(obj)) {
                List<Integer> rSO = searchSimSubjPred(t.getSub(), t.getPred(), vr, n, cosine_threshold);
                for (int docid : rSO) {
                    String p = searcher.doc(docid).get("obj").toLowerCase();
                    Counter<String> v = m.get(p);
                    if (v == null) {
                        m.put(p, new Counter<>(p, 1));
                    } else {
                        v.increment();
                    }
                }
            }
        }
        List<Counter> l = new ArrayList<>(m.values());
        Collections.sort(l, Collections.reverseOrder());
        return l;
    }

    public List<Triple> searchTriple(String query, int n) throws IOException, ParseException {
        MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[]{"subj", "pred", "obj"}, new StandardAnalyzer(CharArraySet.EMPTY_SET));
        Query q = qp.parse(query);
        TopDocs topdocs = searcher.search(q, n);
        List<Triple> rs = new ArrayList<>();
        for (ScoreDoc sd : topdocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            Triple t = new Triple(doc.get("subj"), doc.get("pred"), doc.get("obj"));
            t.setScore(sd.score);
            t.setDocid(sd.doc);
            rs.add(t);
        }
        return rs;
    }

    public List<Integer> searchSimSubj(String subj, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
        subj=QueryParser.escape(subj);
        Query q = queryParser.parse("subj:(" + subj + ")");
        TopDocs topdocs = searcher.search(q, n);
        List<Integer> rs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
            String subjT = searcher.doc(scoreDoc.doc).get("subj");
            if (CosineSim.sim(subj, subjT, vr) >= cosine_threshold) {
                rs.add(scoreDoc.doc);
            }
        }
        return rs;
    }

    public List<Integer> searchSimPred(String pred, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
        pred=QueryParser.escape(pred);
        Query q = queryParser.parse(pred);
        TopDocs topdocs = searcher.search(q, n);
        List<Integer> rs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
            String predT = searcher.doc(scoreDoc.doc).get("pred");
            if (CosineSim.sim(pred, predT, vr) >= cosine_threshold) {
                rs.add(scoreDoc.doc);
            }
        }
        return rs;
    }

    public List<Integer> searchSimSubjPred(String subj, String pred, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
        subj=QueryParser.escape(subj);
        Query q = queryParser.parse(pred + " subj:(" + subj + ")");
        TopDocs topdocs = searcher.search(q, n);
        List<Integer> rs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
            String subjT = searcher.doc(scoreDoc.doc).get("subj");
            String predT = searcher.doc(scoreDoc.doc).get("pred");
            if (CosineSim.sim(pred, predT, vr) >= cosine_threshold
                    && CosineSim.sim(subj, subjT, vr) >= cosine_threshold) {
                rs.add(scoreDoc.doc);
            }
        }
        return rs;
    }

    public List<Integer> searchSimPredObj(String pred, String obj, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
        obj=QueryParser.escape(obj);
        Query q = queryParser.parse(pred + " obj:(" + obj + ")");
        TopDocs topdocs = searcher.search(q, n);
        List<Integer> rs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
            String objT = searcher.doc(scoreDoc.doc).get("obj");
            String predT = searcher.doc(scoreDoc.doc).get("pred");
            if (CosineSim.sim(pred, predT, vr) >= cosine_threshold
                    && CosineSim.sim(obj, objT, vr) >= cosine_threshold) {
                rs.add(scoreDoc.doc);
            }
        }
        return rs;
    }

    public List<Integer> searchSimSubjObj(String subj, String obj, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
        subj=QueryParser.escape(subj);
        obj=QueryParser.escape(obj);
        Query q = queryParser.parse("subj:(" + subj + ") obj:(" + obj + ")");
        TopDocs topdocs = searcher.search(q, n);
        List<Integer> rs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
            String subjT = searcher.doc(scoreDoc.doc).get("subj");
            String objT = searcher.doc(scoreDoc.doc).get("obj");
            if (CosineSim.sim(subj, subjT, vr) >= cosine_threshold && CosineSim.sim(obj, objT, vr) >= cosine_threshold) {
                rs.add(scoreDoc.doc);
            }
        }
        return rs;
    }

    public List<Integer> searchSimSubjPredObj(String subj, String pred, String obj, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
        subj=QueryParser.escape(subj);
        pred=QueryParser.escape(pred);
        obj=QueryParser.escape(obj);
        Query q = queryParser.parse(pred + " subj:(" + subj + ") obj:(" + obj + ")");
        TopDocs topdocs = searcher.search(q, n);
        List<Integer> rs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
            String subjT = searcher.doc(scoreDoc.doc).get("subj");
            String predT = searcher.doc(scoreDoc.doc).get("pred");
            String objT = searcher.doc(scoreDoc.doc).get("obj");
            if (CosineSim.sim(pred, predT, vr) >= cosine_threshold
                    && CosineSim.sim(subj, subjT, vr) >= cosine_threshold && CosineSim.sim(obj, objT, vr) >= cosine_threshold) {
                rs.add(scoreDoc.doc);
            }
        }
        return rs;
    }

    public double getCondProbObjSubj(String subj, String obj, String pred, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
        List<Integer> searchSimPred = searchSimPred(pred, vr, n, cosine_threshold);
        List<Integer> searchSimSubjPred = searchSimSubjPred(subj, pred, vr, n, cosine_threshold);
        List<Integer> searchSimSubjPredObj = searchSimSubjPredObj(subj, pred, obj, vr, n, cosine_threshold);
        //List<Integer> searchSimSubjObj = searchSimSubjObj(subj, obj, vr, n, cosine_threshold);
        double pn = (double) searchSimSubjPredObj.size() / (double) searchSimPred.size();
        double pd = (double) searchSimSubjPred.size() / (double) searchSimPred.size();
        return pn / pd;
    }

    public Triple getTriple(int docid) throws IOException {
        Document doc = searcher.doc(docid);
        return new Triple(doc.get("subj"), doc.get("pred"), doc.get("obj"));
    }

}
