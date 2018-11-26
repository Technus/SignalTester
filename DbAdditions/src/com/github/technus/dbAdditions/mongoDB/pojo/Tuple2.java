package com.github.technus.dbAdditions.mongoDB.pojo;

public class Tuple2<X,Y> extends Tuple1<X> {
    public Tuple2() {}

    public Tuple2(X x, Y y) {
        super(x);
        this.y = y;
    }

    private Y y;

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }
}
