/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.data;

/**
 *
 * @author pierpaolo
 */
public class Token {

    private int id;

    private String form;

    private String lemma;

    private String upostag;

    private String xpostag;

    private String feats;

    private int head;

    private String depRel;

    private String deps;

    private String misc;

    private int start;

    private int end;

    /**
     *
     * @param id
     */
    public Token(int id) {
        this.id = id;
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
    public String getForm() {
        return form;
    }

    /**
     *
     * @param form
     */
    public void setForm(String form) {
        this.form = form;
    }

    /**
     *
     * @return
     */
    public String getLemma() {
        return lemma;
    }

    /**
     *
     * @param lemma
     */
    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    /**
     *
     * @return
     */
    public String getUpostag() {
        return upostag;
    }

    /**
     *
     * @param upostag
     */
    public void setUpostag(String upostag) {
        this.upostag = upostag;
    }

    /**
     *
     * @return
     */
    public String getXpostag() {
        return xpostag;
    }

    /**
     *
     * @param xpostag
     */
    public void setXpostag(String xpostag) {
        this.xpostag = xpostag;
    }

    /**
     *
     * @return
     */
    public String getFeats() {
        return feats;
    }

    /**
     *
     * @param feats
     */
    public void setFeats(String feats) {
        this.feats = feats;
    }

    /**
     *
     * @return
     */
    public int getHead() {
        return head;
    }

    /**
     *
     * @param head
     */
    public void setHead(int head) {
        this.head = head;
    }

    /**
     *
     * @return
     */
    public String getDepRel() {
        return depRel;
    }

    /**
     *
     * @param depRel
     */
    public void setDepRel(String depRel) {
        this.depRel = depRel;
    }

    /**
     *
     * @return
     */
    public String getDeps() {
        return deps;
    }

    /**
     *
     * @param deps
     */
    public void setDeps(String deps) {
        this.deps = deps;
    }

    /**
     *
     * @return
     */
    public String getMisc() {
        return misc;
    }

    /**
     *
     * @param misc
     */
    public void setMisc(String misc) {
        this.misc = misc;
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
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.id;
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
        final Token other = (Token) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Token{" + "id=" + id + ", form=" + form + ", lemma=" + lemma + ", upostag=" + upostag + ", start=" + start + ", end=" + end + '}';
    }

}
