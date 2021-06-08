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
public class Passage {
    
    private String id;
    
    private String title;
    
    private String text;
    
    private String conll;
    
    private Triple[] triples;

    /**
     *
     * @param id
     */
    public Passage(String id) {
        this.id = id;
    }

    /**
     *
     * @param id
     * @param title
     */
    public Passage(String id, String title) {
        this.id = id;
        this.title = title;
    }
    
    /**
     *
     * @param id
     * @param title
     * @param text
     * @param conll
     * @param triples
     */
    public Passage(String id, String title, String text, String conll, Triple[] triples) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.conll = conll;
        this.triples = triples;
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
    public String getConll() {
        return conll;
    }

    /**
     *
     * @param conll
     */
    public void setConll(String conll) {
        this.conll = conll;
    }

    /**
     *
     * @return
     */
    public Triple[] getTriples() {
        return triples;
    }

    /**
     *
     * @param triples
     */
    public void setTriples(Triple[] triples) {
        this.triples = triples;
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
    
    
    
}
