package com.github.technus.dbAdditions.utility;

public class Container<T> implements IContainer<T> {
    private T t;

    @Override
    public void accept(T t) {
        this.t=t;
    }

    @Override
    public T get() {
        return t;
    }
}
