package com.github.technus.dbAdditions.functionalInterfaces;

@FunctionalInterface
public interface IFunction<K,V>{
    V act(K k) throws Exception;
}
