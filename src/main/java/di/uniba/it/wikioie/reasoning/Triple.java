/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.reasoning;

import java.util.Objects;

/**
 *
 * @author Alessia
 */
public class Triple implements Comparable<Triple> {

    private String subject = "";
    private String predicate = "";
    private String object = "";
    private double score;

    public Triple() {
    }

    public Triple(String sub, String pred, String obj) {
        this.subject = sub;
        this.predicate = pred;
        this.object = obj;
    }

    public void setSub(String sub) {
        this.subject = sub;
    }

    public String getSub() {
        return this.subject;
    }

    public void setPred(String pred) {
        this.predicate = pred;
    }

    public String getPred() {
        return this.predicate;
    }

    public void setObj(String obj) {
        this.object = obj;
    }

    public String getObj() {
        return this.object;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void printTriple() {
        System.out.println("<" + this.subject + "> <" + this.predicate + "> <" + this.object + ">");
    }

    @Override
    public String toString() {
        return "<" + this.subject + "> <" + this.predicate + "> <" + this.object + ">\t" + score;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.subject);
        hash = 79 * hash + Objects.hashCode(this.predicate);
        hash = 79 * hash + Objects.hashCode(this.object);
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
        final Triple other = (Triple) obj;
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        if (!Objects.equals(this.predicate, other.predicate)) {
            return false;
        }
        return Objects.equals(this.object, other.object);
    }
    
    

    @Override
    public int compareTo(Triple o) {
        return Double.compare(score, o.score);
    }

}
