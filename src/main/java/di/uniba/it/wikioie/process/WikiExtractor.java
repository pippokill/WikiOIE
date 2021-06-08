/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.process;

import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import java.util.List;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public interface WikiExtractor {

    /**
     *
     * @param text
     * @return
     */
    public List<Triple> extract(List<Graph<Token, String>> text);

    /**
     *
     * @param g
     * @return
     */
    public List<Triple> extract(Graph<Token, String> g);

}
