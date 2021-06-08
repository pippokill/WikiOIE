/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.data;

import java.util.Objects;

/**
 *
 * @author pierpaolo
 * @param <I>
 */
public class Counter<I> implements Comparable<Counter<I>> {

    private I item;

    private int count = 1;

    /**
     *
     * @param item
     */
    public Counter(I item) {
        this.item = item;
    }

    /**
     *
     * @param item
     * @param count
     */
    public Counter(I item, int count) {
        this.item = item;
        this.count = count;
    }

    /**
     *
     * @return
     */
    public I getItem() {
        return item;
    }

    /**
     *
     * @param item
     */
    public void setItem(I item) {
        this.item = item;
    }

    /**
     *
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     *
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     *
     */
    public void increment() {
        this.count++;
    }

    /**
     *
     */
    public void decrement() {
        this.count--;
    }

    /**
     *
     * @param q
     */
    public void increment(int q) {
        this.count += q;
    }

    /**
     *
     * @param q
     */
    public void decrement(int q) {
        this.count -= q;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.item);
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
        final Counter<?> other = (Counter<?>) obj;
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Counter<I> o) {
        return Integer.compare(count, o.count);
    }

}
