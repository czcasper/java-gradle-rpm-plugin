/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.trigonic.gradle.plugins.helpers;

import java.util.concurrent.Callable;

/**
 *
 * @author czcasper
 * @param <T>
 */
public class CallableContainer<T> implements Callable<T> {

    protected T value;

    public CallableContainer(T value) {
        this.value = value;
    }    
    
    @Override
    public T call() throws Exception {
        return value;
    }
    
}
