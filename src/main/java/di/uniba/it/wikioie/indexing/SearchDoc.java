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

import java.util.Objects;

/**
 *
 * @author pierpaolo
 */
public class SearchDoc implements Comparable<SearchDoc> {

    private String id;

    private String wikiId;

    private String title;

    private String text;

    private float searchScore;

    /**
     *
     * @param id
     */
    public SearchDoc(String id) {
        this.id = id;
    }

    /**
     *
     * @param id
     * @param title
     * @param text
     */
    public SearchDoc(String id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    /**
     *
     * @param id
     * @param title
     * @param text
     * @param searchScore
     */
    public SearchDoc(String id, String title, String text, float searchScore) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.searchScore = searchScore;
    }

    /**
     *
     * @param id
     * @param wikiId
     * @param title
     * @param text
     */
    public SearchDoc(String id, String wikiId, String title, String text) {
        this.id = id;
        this.wikiId = wikiId;
        this.title = title;
        this.text = text;
    }

    /**
     *
     * @param id
     * @param wikiId
     * @param title
     * @param text
     * @param searchScore
     */
    public SearchDoc(String id, String wikiId, String title, String text, float searchScore) {
        this.id = id;
        this.wikiId = wikiId;
        this.title = title;
        this.text = text;
        this.searchScore = searchScore;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getWikiId() {
        return wikiId;
    }

    /**
     *
     * @param wikiId
     */
    public void setWikiId(String wikiId) {
        this.wikiId = wikiId;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
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
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final SearchDoc other = (SearchDoc) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(SearchDoc o) {
        return Float.compare(searchScore, o.searchScore);
    }

}
