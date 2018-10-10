package com.github.technus.dbAdditions.functionalInterfaces;

import java.util.Map;

@FunctionalInterface
public interface IQuery<Q,K,V> {
    /**
     * Used to ask for elements, should add new elements to collection
     * @param query whatever to be passed as query
     * @param collection actual collection
     * @return elements found new and old
     */
    Map<K,V> ask(Q query, Map<K, V> collection) throws Exception;
}
