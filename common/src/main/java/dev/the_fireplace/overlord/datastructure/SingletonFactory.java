package dev.the_fireplace.overlord.datastructure;

import java.util.function.Supplier;

public final class SingletonFactory<T>
{
    private final Supplier<T> supplier;
    private T instance = null;

    public SingletonFactory(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (instance == null) {
            instance = supplier.get();
        }

        return instance;
    }
}
