/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.indexing;

import di.uniba.it.wikioie.data.Span;
import java.util.Objects;

/**
 *
 * @author pierpaolo
 */
public class SearchTriple implements Comparable<SearchTriple> {

    private int id;

    private String docid;

    private float searchScore;

    private Span subject;

    private Span predicate;

    private Span object;

    private float score;

    /**
     *
     * @param id
     * @param docid
     * @param subject
     * @param predicate
     * @param object
     * @param score
     */
    public SearchTriple(int id, String docid, Span subject, Span predicate, Span object, float score) {
        this.id = id;
        this.docid = docid;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = score;
    }

    /**
     *
     * @param id
     * @param docid
     */
    public SearchTriple(int id, String docid) {
        this.id = id;
        this.docid = docid;
    }
    
    /**
     *
     * @param docid
     * @param subject
     * @param predicate
     * @param object
     * @param score
     */
    public SearchTriple(String docid, Span subject, Span predicate, Span object, float score) {
        this.docid = docid;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = score;
    }

    /**
     *
     * @param docid
     */
    public SearchTriple(String docid) {
        this.docid = docid;
    }

    /**
     *
     * @param subject
     * @param predicate
     * @param object
     * @param score
     */
    public SearchTriple(Span subject, Span predicate, Span object, float score) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = score;
    }

    /**
     *
     * @param subject
     * @param predicate
     * @param object
     */
    public SearchTriple(Span subject, Span predicate, Span object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = 1;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *
     * @return
     */
    public String getDocid() {
        return docid;
    }

    /**
     *
     * @param docid
     */
    public void setDocid(String docid) {
        this.docid = docid;
    }

    /**
     *
     * @return
     */
    public float getSearchScore() {
        return searchScore;
    }

    /**
     *
     * @param searchScore
     */
    public void setSearchScore(float searchScore) {
        this.searchScore = searchScore;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.docid);
        hash = 83 * hash + Objects.hashCode(this.subject);
        hash = 83 * hash + Objects.hashCode(this.predicate);
        hash = 83 * hash + Objects.hashCode(this.object);
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
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
        final SearchTriple other = (SearchTriple) obj;
        if (!Objects.equals(this.docid, other.docid)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        if (!Objects.equals(this.predicate, other.predicate)) {
            return false;
        }
        if (!Objects.equals(this.object, other.object)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public Span getSubject() {
        return subject;
    }

    /**
     *
     * @param subject
     */
    public void setSubject(Span subject) {
        this.subject = subject;
    }

    /**
     *
     * @return
     */
    public Span getPredicate() {
        return predicate;
    }

    /**
     *
     * @param predicate
     */
    public void setPredicate(Span predicate) {
        this.predicate = predicate;
    }

    /**
     *
     * @return
     */
    public Span getObject() {
        return object;
    }

    /**
     *
     * @param object
     */
    public void setObject(Span object) {
        this.object = object;
    }

    /**
     *
     * @return
     */
    public float getScore() {
        return score;
    }

    /**
     *
     * @param score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "OutTriple{" + "subject=" + subject + ", predicate=" + predicate + ", object=" + object + ", score=" + score + '}';
    }

    /**
     *
     * @return
     */
    public String toSimpleString() {
        return subject.getSpan() + "\t" + predicate.getSpan() + "\t" + object.getSpan() + "\t" + score;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(SearchTriple o) {
        return Float.compare(searchScore, o.searchScore);
    }

}
