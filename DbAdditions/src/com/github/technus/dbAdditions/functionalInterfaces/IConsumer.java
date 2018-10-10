package com.github.technus.dbAdditions.functionalInterfaces;

@FunctionalInterface
public interface IConsumer<ARG> {
    void eat(ARG arg) throws Exception;
}
