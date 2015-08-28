/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.api;

/**
 * Simple implementation of comparison of resources used by Unloadables by their
 * references.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractUnloadable<T> implements Unloadable {
    protected final T data;

    public AbstractUnloadable(T data) {
        this.data = data;
    }


    @Override
    public T getResource() {
        return data;
    }

    @Override
    public boolean isSameResource(Unloadable other) {
        // this was a bad idea, as two different objects might return
        // true from .equals method
        //return Objects.equals(this, other);

        return this.getResource() == other.getResource();
    }

}
