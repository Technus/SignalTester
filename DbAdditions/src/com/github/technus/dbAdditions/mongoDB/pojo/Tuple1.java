package com.github.technus.dbAdditions.mongoDB.pojo;

public class Tuple1<X> {
    public Tuple1() {}

    public Tuple1(X x) {
        this.x = x;
    }

    private X x;

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }
}
