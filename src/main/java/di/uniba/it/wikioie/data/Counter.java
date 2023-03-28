/**
 * Copyright (c) 2021, the WikiOIE AUTHORS.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Bari nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 *
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

    @Override
    public String toString() {
        return "Counter{" + "item=" + item + ", count=" + count + '}';
    }
    
    

}
