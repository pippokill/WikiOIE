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

package di.uniba.it.wikioie.indexing;

import di.uniba.it.wikioie.data.Span;
import java.util.Objects;

/**
 *
 * @author pierpaolo
 */
public class SearchTripleSE implements Comparable<SearchTripleSE> {

    private int id;

    private String docid;
    
    private String fullDocId;
    
    private String title;
    
    private String text;

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
    public SearchTripleSE(int id, String docid, Span subject, Span predicate, Span object, float score) {
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
    public SearchTripleSE(int id, String docid) {
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
    public SearchTripleSE(String docid, Span subject, Span predicate, Span object, float score) {
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
    public SearchTripleSE(String docid) {
        this.docid = docid;
    }

    /**
     *
     * @param subject
     * @param predicate
     * @param object
     * @param score
     */
    public SearchTripleSE(Span subject, Span predicate, Span object, float score) {
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
    public SearchTripleSE(Span subject, Span predicate, Span object) {
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
        final SearchTripleSE other = (SearchTripleSE) obj;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFullDocId() {
        return fullDocId;
    }

    public void setFullDocId(String fullDocId) {
        this.fullDocId = fullDocId;
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
    public int compareTo(SearchTripleSE o) {
        return Float.compare(searchScore, o.searchScore);
    }

}
