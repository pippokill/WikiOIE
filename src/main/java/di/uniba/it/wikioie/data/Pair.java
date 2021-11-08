/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.data;

/**
 *
 * @author pierpaolo
 * @param <A>
 * @param <B>
 */
public class Pair<A,B> {
    
    private A a;
    
    private B b;

    /**
     *
     * @param a
     * @param b
     */
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     *
     * @return
     */
    public A getA() {
        return a;
    }

    /**
     *
     * @param a
     */
    public void setA(A a) {
        this.a = a;
    }

    /**
     *
     * @return
     */
    public B getB() {
        return b;
    }

    /**
     *
     * @param b
     */
    public void setB(B b) {
        this.b = b;
    }
    
    
    
}
