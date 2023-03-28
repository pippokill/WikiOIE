/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.vectors.FileVectorReader;
import di.uniba.it.wikioie.vectors.MemoryVectorReader;
import di.uniba.it.wikioie.vectors.ObjectVector;
import di.uniba.it.wikioie.vectors.ReverseObjectVectorComparator;
import di.uniba.it.wikioie.vectors.Vector;
import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 *
 * @author pierpaolo
 */
public class TripleVectorIndex {

    private VectorReader vectorReader;

    private double simTh = 0.7;

    public enum RES_TYPE {
        PRED, SUBJ, OBJ
    };

    public enum STORE_TYPE {
        LUCENE, MEM, FILE
    }

    public class TripleVectorResult implements Comparable<TripleVectorResult> {

        private int docid;

        private RES_TYPE type;

        private float score;

        public TripleVectorResult() {
        }

        public TripleVectorResult(int docid, RES_TYPE type) {
            this.docid = docid;
            this.type = type;
        }

        public TripleVectorResult(int docid, RES_TYPE type, float score) {
            this.docid = docid;
            this.type = type;
            this.score = score;
        }

        public int getDocid() {
            return docid;
        }

        public void setDocid(int docid) {
            this.docid = docid;
        }

        public RES_TYPE getType() {
            return type;
        }

        public void setType(RES_TYPE type) {
            this.type = type;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + this.docid;
            hash = 23 * hash + Objects.hashCode(this.type);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TripleVectorResult other = (TripleVectorResult) obj;
            if (this.docid != other.docid) {
                return false;
            }
            return this.type == other.type;
        }

        @Override
        public int compareTo(TripleVectorResult o) {
            return Float.compare(score, o.score);
        }

    }

    public void open(File file, STORE_TYPE type) throws IOException {
        if (vectorReader != null) {
            vectorReader.close();
            vectorReader = null;
        }
        if (null == type) {
            vectorReader = new FileVectorReader(file);
            vectorReader.init();
        } else {
            switch (type) {
                case LUCENE:
                    vectorReader = new LuceneVectorReader(file);
                    vectorReader.init();
                    break;
                case MEM:
                    vectorReader = new MemoryVectorReader(file);
                    vectorReader.init();
                    break;
                default:
                    vectorReader = new FileVectorReader(file);
                    vectorReader.init();
                    break;
            }
        }
    }

    public List<TripleVectorResult> findSimilarPredicate(Vector predicate) throws IOException {
        return findSimilarPredicate(predicate, Integer.MAX_VALUE);
    }

    public List<TripleVectorResult> findSimilarPredicate(Vector predicate, int topn) throws IOException {
        return findSimilar(predicate, "_pred", topn);
    }

    public List<TripleVectorResult> findSimilarSubject(Vector subject) throws IOException {
        return findSimilarSubject(subject, Integer.MAX_VALUE);
    }

    public List<TripleVectorResult> findSimilarSubject(Vector subject, int topn) throws IOException {
        return findSimilar(subject, "_subj", topn);
    }

    public List<TripleVectorResult> findSimilarObject(Vector object) throws IOException {
        return findSimilarObject(object, Integer.MAX_VALUE);
    }

    public List<TripleVectorResult> findSimilarObject(Vector object, int topn) throws IOException {
        return findSimilar(object, "_obj", topn);
    }

    private List<TripleVectorResult> findSimilar(Vector q, String suf, int topn) throws IOException {
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = vectorReader.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            if (ov.getKey().endsWith(suf) && !ov.getVector().isZeroVector()) {
                double overlap = ov.getVector().measureOverlap(q);
                if (overlap >= simTh) {
                    ov.setScore(overlap);
                    if (queue.size() <= topn) {
                        queue.offer(ov);
                    } else {
                        queue.poll();
                        queue.offer(ov);
                    }
                }
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        List<TripleVectorResult> results = new ArrayList<>();
        for (ObjectVector ov : list) {
            TripleVectorResult r = new TripleVectorResult();
            r.setDocid(Integer.parseInt(ov.getKey().substring(0, ov.getKey().indexOf("_"))));
            r.setScore((float) ov.getScore());
            switch (suf) {
                case "pred":
                    r.setType(RES_TYPE.PRED);
                    break;
                case "subj":
                    r.setType(RES_TYPE.SUBJ);
                    break;
                default:
                    r.setType(RES_TYPE.OBJ);
            }
            results.add(r);
        }
        return results;
    }

    public double getSimTh() {
        return simTh;
    }

    public void setSimTh(double simTh) {
        this.simTh = simTh;
    }

}
