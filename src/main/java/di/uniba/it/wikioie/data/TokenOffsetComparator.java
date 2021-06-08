/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.data;

import java.util.Comparator;

/**
 *
 * @author pierpaolo
 */
public class TokenOffsetComparator implements Comparator<Token> {

    /**
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Token o1, Token o2) {
        return Integer.compare(o1.getStart(), o2.getStart());
    }
    
}
