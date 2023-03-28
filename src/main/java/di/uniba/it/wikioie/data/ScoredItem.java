/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package di.uniba.it.wikioie.data;

import java.util.Objects;

/**
 *
 * @author pierpaolo
 */
public class ScoredItem<I> implements Comparable<ScoredItem<I>> {

    private I item;

    private float score;

    public ScoredItem() {
    }

    public ScoredItem(I item, float score) {
        this.item = item;
        this.score = score;
    }

    public ScoredItem(I item) {
        this.item = item;
    }

    public I getItem() {
        return item;
    }

    public void setItem(I item) {
        this.item = item;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.item);
        return hash;
    }

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
        final ScoredItem<?> other = (ScoredItem<?>) obj;
        return Objects.equals(this.item, other.item);
    }

    @Override
    public String toString() {
        return "ScoredItem{" + "item=" + item + ", score=" + score + '}';
    }

    @Override
    public int compareTo(ScoredItem<I> o) {
        return Float.compare(score, o.score);
    }

}
