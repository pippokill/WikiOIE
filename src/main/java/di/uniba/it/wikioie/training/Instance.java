/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.training;

import di.uniba.it.wikioie.vectors.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pierpaolo
 */
public class Instance {

    private final int id;

    /**
     *
     * @param id
     */
    public Instance(int id) {
        this.id = id;
    }

    private final Map<Integer, Float> features = new HashMap<>();

    private final List<Vector> denseFeatures = new ArrayList<>();

    private int label;

    /**
     *
     * @return
     */
    public Map<Integer, Float> getFeatures() {
        return features;
    }

    /**
     *
     * @param id
     * @param value
     */
    public void setFeature(int id, float value) {
        features.put(id, value);
    }

    /**
     *
     * @param id
     * @return
     */
    public float getFeature(int id) {
        Float v = features.get(id);
        if (v == null) {
            return 0;
        } else {
            return v;
        }
    }

    /**
     *
     * @return
     */
    public int getLabel() {
        return label;
    }

    /**
     *
     * @param label
     */
    public void setLabel(int label) {
        this.label = label;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instance other = (Instance) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public List<Vector> getDenseFeature() {
        return denseFeatures;
    }

    /**
     *
     * @param vector
     */
    public void addDenseVector(Vector vector) {
        denseFeatures.add(vector);
    }

}
