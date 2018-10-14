package com.github.technus.dbAdditions.functionalInterfaces;

@FunctionalInterface
public interface ISupplier<V> {
    V get() throws Exception;
}
