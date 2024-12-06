package com.codefork.aoc2024.util;

import java.util.function.Function;

/**
 * Copied from <a href="https://stackoverflow.com/a/45976269">this page</a>
 * <p>
 * WithIndex.indexed() returns a function that can be passed to a stream's map()
 * to transform it into WithIndex wrapper records.
 */
public class WithIndex<T> {
    private int index;
    private T value;

    WithIndex(int index, T value) {
        this.index = index;
        this.value = value;
    }

    public int index() {
        return index;
    }

    public T value() {
        return value;
    }

    @Override
    public String toString() {
        return value + "(" + index + ")";
    }

    public static <T> Function<T, WithIndex<T>> indexed() {
        return new Function<T, WithIndex<T>>() {
            int index = 0;
            @Override
            public WithIndex<T> apply(T t) {
                return new WithIndex<>(index++, t);
            }
        };
    }
}