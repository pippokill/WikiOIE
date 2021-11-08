/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.training;

/**
 *
 * @author pierpaolo
 */
public class FileInstance {

    private final int id;

    private String title;

    private String text;

    private String subject;

    private String predicate;

    private String object;

    private float score;

    /**
     *
     * @param id
     * @param title
     * @param text
     * @param subject
     * @param predicate
     * @param object
     * @param score
     */
    public FileInstance(int id, String title, String text, String subject, String predicate, String object, float score) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = score;
    }

    /**
     *
     * @param id
     * @param title
     * @param text
     * @param subject
     * @param predicate
     * @param object
     */
    public FileInstance(int id, String title, String text, String subject, String predicate, String object) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    /**
     *
     * @param id
     * @param text
     * @param subject
     * @param predicate
     * @param object
     */
    public FileInstance(int id, String text, String subject, String predicate, String object) {
        this.id = id;
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    /**
     *
     * @param id
     * @param text
     * @param subject
     * @param predicate
     * @param object
     * @param score
     */
    public FileInstance(int id, String text, String subject, String predicate, String object, float score) {
        this.id = id;
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = score;
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
    public String getSubject() {
        return subject;
    }

    /**
     *
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     *
     * @return
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     *
     * @param predicate
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     *
     * @return
     */
    public String getObject() {
        return object;
    }

    /**
     *
     * @param object
     */
    public void setObject(String object) {
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
    public int getId() {
        return id;
    }
    
    

}
