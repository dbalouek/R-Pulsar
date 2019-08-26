/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

/**
 * Implements a Pair and will be used in R-Pulsar
 * @author eduard
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {

    private final K element0;
    private final V element1;

    public static <K, V> Pair<K, V> createPair(K element0, V element1) {
        return new Pair<>(element0, element1);
    }

    /**
     * Create pair and init with the values passed
     * @param element0
     * @param element1
     */
    public Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    /**
     * Get Element0
     * @return Will return the element0 in the pair.
     */
    public K getElement0() {
        return element0;
    }

    /**
     * Get Element1
     * @return Will return the element1 in the pair.
     */
    public V getElement1() {
        return element1;
    }

}
