/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
