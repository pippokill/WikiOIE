/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.training;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pierpaolo
 */
public class Instance {

    private final Map<Integer, Float> features = new HashMap<>();
    
    private int label;

    public Map<Integer, Float> getFeatures() {
        return features;
    }

    public void setFeature(int id, float value) {
        features.put(id, value);
    }

    public float getFeature(int id) {
        Float v = features.get(id);
        if (v == null) {
            return 0;
        } else {
            return v;
        }
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }
    
    

}
