/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.training;

import di.uniba.it.wikioie.data.Pair;
import java.util.Comparator;

/**
 *
 * @author pierpaolo
 */
public class PairBComparator implements Comparator<Pair<Integer, Integer>> {

    @Override
    public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
        return o1.getB().compareTo(o2.getB());
    }

}
