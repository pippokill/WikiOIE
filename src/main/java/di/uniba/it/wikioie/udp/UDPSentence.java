/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.udp;

import di.uniba.it.wikioie.data.Token;
import java.util.List;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class UDPSentence {
    
    private String id;
    
    private String text;
    
    private String conll;
    
    private List<Token> tokens;
    
    private Graph<Token, String> graph;

    /**
     *
     */
    public UDPSentence() {
    }

    /**
     *
     * @param id
     */
    public UDPSentence(String id) {
        this.id = id;
    }  

    /**
     *
     * @param id
     * @param text
     * @param conll
     */
    public UDPSentence(String id, String text, String conll) {
        this.id = id;
        this.text = text;
        this.conll = conll;
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
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     *
     * @param tokens
     */
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     *
     * @return
     */
    public Graph<Token, String> getGraph() {
        return graph;
    }

    /**
     *
     * @param graph
     */
    public void setGraph(Graph<Token, String> graph) {
        this.graph = graph;
    }
    
}
