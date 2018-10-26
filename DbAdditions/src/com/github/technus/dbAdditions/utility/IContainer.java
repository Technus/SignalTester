package com.github.technus.dbAdditions.utility;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IContainer<T> extends Supplier<T>, Consumer<T> {
     Class<? extends IContainer> DEFAULT_IMPLEMENTATION = ContainerImpl.class;
}
