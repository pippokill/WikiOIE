/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pierpaolo
 */
public class TrainingSet {

    private Map<String, Integer> dict;

    private final List<Instance> set = new ArrayList<>();

    private int i = 1;

    /**
     *
     */
    public TrainingSet() {
        dict = new HashMap<>();
    }

    /**
     *
     * @param dict
     */
    public TrainingSet(Map<String, Integer> dict) {
        this.dict = dict;
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getDict() {
        return dict;
    }

    /**
     *
     * @return
     */
    public List<Instance> getSet() {
        return set;
    }

    /**
     *
     * @param fvalue
     * @return
     */
    public int addFeature(String fvalue) {
        Integer id = dict.get(fvalue);
        if (id == null) {
            id = i;
            dict.put(fvalue, id);
            i++;
        }
        return id;
    }

    /**
     *
     * @param instance
     */
    public void addInstance(Instance instance) {
        set.add(instance);
    }

    /**
     *
     * @param fvalue
     * @return
     */
    public Integer getId(String fvalue) {
        return dict.get(fvalue);
    }

}
