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
public class Span {

    private String span;

    private int start;

    private int end;

    private float score;

    /**
     *
     * @param span
     * @param start
     * @param end
     */
    public Span(String span, int start, int end) {
        this.span = span;
        this.start = start;
        this.end = end;
        this.score = 1;
    }

    /**
     *
     * @param span
     * @param start
     * @param end
     * @param score
     */
    public Span(String span, int start, int end, float score) {
        this.span = span;
        this.start = start;
        this.end = end;
        this.score = score;
    }
    
    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.span);
        hash = 89 * hash + this.start;
        hash = 89 * hash + this.end;
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
        final Span other = (Span) obj;
        if (this.start != other.start) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if (!Objects.equals(this.span, other.span)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public String getSpan() {
        return span;
    }

    /**
     *
     * @param span
     */
    public void setSpan(String span) {
        this.span = span;
    }

    /**
     *
     * @return
     */
    public int getStart() {
        return start;
    }

    /**
     *
     * @param start
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     *
     * @return
     */
    public int getEnd() {
        return end;
    }

    /**
     *
     * @param end
     */
    public void setEnd(int end) {
        this.end = end;
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
        return "Span{" + "span=" + span + ", start=" + start + ", end=" + end + '}';
    }

}
