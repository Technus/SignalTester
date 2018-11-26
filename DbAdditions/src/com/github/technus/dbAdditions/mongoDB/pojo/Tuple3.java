package com.github.technus.dbAdditions.mongoDB.pojo;

public class Tuple3<X,Y,Z> extends Tuple2<X,Y>{
    public Tuple3() {}

    public Tuple3(X x, Y y, Z z) {
        super(x, y);
        this.z = z;
    }

    private Z z;

    public Z getZ() {
        return z;
    }

    public void setZ(Z z) {
        this.z = z;
    }
}
