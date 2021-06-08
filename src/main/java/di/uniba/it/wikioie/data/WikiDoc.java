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
public class WikiDoc {
    
    private String id;
    
    private String title;
    
    private String text;

    /**
     *
     * @param id
     * @param title
     * @param text
     */
    public WikiDoc(String id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
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
    
    
    
}
