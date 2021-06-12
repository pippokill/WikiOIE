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
