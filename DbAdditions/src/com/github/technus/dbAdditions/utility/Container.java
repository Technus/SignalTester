package com.github.technus.dbAdditions.utility;

public class Container<T> implements IContainer<T> {
    private T t;

    public Container(){}

    public Container(T content){
        accept(content);
    }

    @Override
    public void accept(T content) {
        this.t=content;
    }

    @Override
    public T get() {
        return t;
    }
}
