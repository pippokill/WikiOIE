/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.indexing.post;

import di.uniba.it.wikioie.data.Passage;

/**
 *
 * @author pierpaolo
 */
public interface PassageProcessor {
    
    /**
     *
     * @param passage
     * @return
     */
    public Passage process(Passage passage);
    
}
