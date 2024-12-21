package com.codefork.aoc2024.util;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

/**
 * Class of functions to help with grid input
 */
public class Grid {

    public interface Reducer<T> {
        T apply(T t, int x, int y, String u);
    }

    /**
     * Parse a grid of string data.
     * @param data stream to consume
     * @param accInitializer lambda that provides the accumulator
     * @param reducer lambda that returns the same type as accumulator, with x, y, and ch values to process
     * @return the final accumulated object
     * @param <T>
     */
    public static <T> T parse(
            Stream<String> data,
            Supplier<T> accInitializer,
            Reducer<T> reducer) {
        return data
                .map(WithIndex.indexed())
                .filter(lineWithIndex -> !lineWithIndex.value().isEmpty())
                .collect(foldLeft(
                        accInitializer,
                        (acc, lineWithIndex) -> {
                            var y = lineWithIndex.index();
                            var line = lineWithIndex.value();

                            return line.chars()
                                    .boxed()
                                    .map(ch -> String.valueOf((char) ch.intValue()))
                                    .map(WithIndex.indexed())
                                    .collect(foldLeft(
                                            () -> acc,
                                            (acc2, chWithIndex) -> {
                                                var x = chWithIndex.index();
                                                var ch = chWithIndex.value();
                                                return reducer.apply(acc2, x, y, ch);
                                            }
                                    ));
                        })
                );
    }
}