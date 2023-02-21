/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.indexing.SearchTriple;
import di.uniba.it.wikioie.vectors.VectorReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Alessia
 */
public class IndexUtils {

    private final String dir;

    private IndexSearcher searcher;

    private final Random rand = new Random();

    private final QueryParser queryParser = new QueryParser("pred", new StandardAnalyzer(CharArraySet.EMPTY_SET));

    private int maxdoc = 0;

    public IndexUtils(String dir) {
        this.dir = dir;
    }

    public void open() throws IOException {
        FSDirectory fsdir = FSDirectory.open(new File(dir).toPath());
        searcher = new IndexSearcher(DirectoryReader.open(fsdir));
        maxdoc = searcher.getIndexReader().maxDoc();
    }

    public Triple randomTriple(int n) throws IOException {
        Triple first_triple = new Triple();
        Query query = new MatchAllDocsQuery();
        TopDocs topdocs = searcher.search(query, n);
        ScoreDoc[] sd = topdocs.scoreDocs;
        int i = rand.nextInt(sd.length);
        if (sd.length != 0) {
            first_triple.setSub(searcher.doc(sd[i].doc).get("subj"));
            first_triple.setPred(searcher.doc(sd[i].doc).get("pred"));
            first_triple.setObj(searcher.doc(sd[i].doc).get("obj"));
            System.out.println("First triple selected!");
        } else {
            System.out.println("Attention! Empty index.");
        }
        return first_triple;
    }

    public Triple searchSecondTriple(Triple first_triple, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        Triple second_triple = new Triple();
        QueryParser qp = new QueryParser("subj", new StandardAnalyzer(CharArraySet.EMPTY_SET));
        Query q = qp.parse(first_triple.getObj());
        TopDocs topdocs = searcher.search(q, n);
        for (ScoreDoc sd : topdocs.scoreDocs) {
            if (CosineSim.sim(first_triple.getObj(), searcher.doc(sd.doc).get("subj"), vr) >= cosine_threshold) {
                second_triple.setSub(searcher.doc(sd.doc).get("subj"));
                second_triple.setPred(searcher.doc(sd.doc).get("pred"));
                second_triple.setObj(searcher.doc(sd.doc).get("obj"));
                System.out.println("Second triple selected!");
                break;
            }
        }
        if (second_triple.getSub().isBlank()) {
            System.out.println("Cannot found a suitable triple.");
        }
        return second_triple;
    }

    public int indexDimension() throws IOException {
        return maxdoc;
    }

    //serve per il denominatore
    public List<Triple> similSP(String subj, String pred, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        Query query = new MatchAllDocsQuery();
        List<Triple> triples = new ArrayList<>();
        TopDocs topdocs = searcher.search(query, n);
        for (ScoreDoc sd : topdocs.scoreDocs) {
            if (CosineSim.sim(subj, (searcher.doc(sd.doc).get("subj")), vr) >= cosine_threshold) {
                if (CosineSim.sim(pred, (searcher.doc(sd.doc).get("pred")), vr) >= cosine_threshold) {
                    Triple tr = new Triple();
                    tr.setSub(searcher.doc(sd.doc).get("subj"));
                    tr.setPred(searcher.doc(sd.doc).get("pred"));
                    tr.setObj(searcher.doc(sd.doc).get("obj"));
                    triples.add(tr);
                }
            }
        }
        return triples;
    }

    //serve per il numeratore
    public List<Triple> similSPO(String subj, String pred, String obj, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        Query query = new MatchAllDocsQuery();
        List<Triple> triples = new ArrayList<>();
        TopDocs topdocs = searcher.search(query, n);
        for (ScoreDoc sd : topdocs.scoreDocs) {
            if (CosineSim.sim(subj, (searcher.doc(sd.doc).get("subj")), vr) >= cosine_threshold) {
                if (CosineSim.sim(pred, (searcher.doc(sd.doc).get("pred")), vr) >= cosine_threshold) {
                    if (CosineSim.sim(obj, (searcher.doc(sd.doc).get("obj")), vr) >= cosine_threshold) {
                        Triple tr = new Triple();
                        tr.setSub(searcher.doc(sd.doc).get("subj"));
                        tr.setPred(searcher.doc(sd.doc).get("pred"));
                        tr.setObj(searcher.doc(sd.doc).get("obj"));
                        triples.add(tr);
                    }
                }
            }
        }
        return triples;
    }

    // PIERPAOLO
    public Map<String, Integer> discoverSimilPred(String pred, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        List<Triple> predTriples = searchTriple("pred:" + pred, n);
        Map<String, Integer> m = new HashMap<>();
        for (Triple t : predTriples) {
            if (t.getPred().equalsIgnoreCase(pred)) {
                List<Integer> rSO = searchSimSubjObj(t.getSub(), t.getObj(), vr, n, cosine_threshold);
                for (int docid : rSO) {
                    String p = searcher.doc(docid).get("pred");
                    Integer v = m.get(p);
                    if (v == null) {
                        m.put(p, 1);
                    } else {
                        m.put(p, v + 1);
                    }
                }
            }
        }
        return m;
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
        //Collections.sort(rs, Collections.reverseOrder());
        return rs;
    }

    public List<Integer> searchSimSubj(String subj, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
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

    public List<Integer> searchSimSubjObj(String subj, String obj, VectorReader vr, int n, double cosine_threshold) throws ParseException, IOException {
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

    public List<Triple> discover(int docid, VectorReader vr, int n, double cosine_threshold) throws IOException, ParseException {
        Document doc = searcher.doc(docid);
        double p1 = getCondProbObjSubj(doc.get("subj"), doc.get("obj"), doc.get("pred"), vr, n, cosine_threshold);
        PriorityQueue<Triple> q = new PriorityQueue<>();
        List<Integer> drel = searchSimSubj(doc.get("obj"), vr, n, cosine_threshold);
        System.out.println("[DISCOVER] candidate triples: " + drel.size());
        Set<Triple> visitedTriples = new HashSet();
        for (int id : drel) {
            if (docid != id) {
                Document doc2 = searcher.doc(id);
                Triple t = new Triple(doc2.get("subj"), doc2.get("pred"), doc2.get("obj"));
                if (!visitedTriples.contains(t)) {
                    System.out.println("[DISCOVER] Compare with: " + t);
                    double p2 = getCondProbObjSubj(doc2.get("subj"), doc2.get("obj"), doc2.get("pred"), vr, n, cosine_threshold);
                    t.setScore(p1 * p2);
                    q.offer(t);
                    if (q.size() > 100) {
                        q.poll();
                    }
                    visitedTriples.add(t);
                }
            }
        }
        List<Triple> rs = new ArrayList<>(q);
        Collections.sort(rs, Collections.reverseOrder());
        return rs;
    }

}
