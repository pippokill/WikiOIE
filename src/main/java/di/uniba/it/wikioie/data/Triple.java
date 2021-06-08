/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.data;

import java.util.Objects;

/**
 *
 * @author pierpaolo
 */
public class Triple {

    private Span subject;

    private Span predicate;

    private Span object;

    private float score;

    /**
     *
     * @param subject
     * @param predicate
     * @param object
     * @param score
     */
    public Triple(Span subject, Span predicate, Span object, float score) {
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
    public Triple(Span subject, Span predicate, Span object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = 1;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.subject);
        hash = 31 * hash + Objects.hashCode(this.predicate);
        hash = 31 * hash + Objects.hashCode(this.object);
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
        final Triple other = (Triple) obj;
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

}
