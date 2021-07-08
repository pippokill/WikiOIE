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
    
    private String title;
    
    private String text;
    
    private String subject;
    
    private String predicate;
    
    private String object;
    
    private float score;

    public FileInstance(String title, String text, String subject, String predicate, String object, float score) {
        this.title = title;
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = score;
    }

    public FileInstance(String title, String text, String subject, String predicate, String object) {
        this.title = title;
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public FileInstance(String text, String subject, String predicate, String object) {
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }
    
    

    public FileInstance(String text, String subject, String predicate, String object, float score) {
        this.text = text;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    
    
}
