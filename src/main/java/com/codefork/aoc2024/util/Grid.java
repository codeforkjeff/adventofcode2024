package com.codefork.aoc2024.util;

import com.codefork.aoc2024.day20.Part01;

import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public class Grid {

    public static <T> T parse(
            Stream<String> data,
            Supplier<T> accInitializer,
            BiFunction<T, String, T> reducer) {
        return data
                .map(WithIndex.indexed())
                .filter(lineWithIndex -> !lineWithIndex.value().isEmpty())
                .collect(foldLeft(
                        () -> accInitializer.get(),
                        (acc, lineWithIndex) -> {
                            var y = lineWithIndex.index();
                            var line = lineWithIndex.value();

                            return line.chars()
                                    .boxed()
                                    .map(ch -> String.valueOf((char) ch.intValue()))
                                    .collect(foldLeft(
                                            () -> acc,
                                            (acc2, ch) -> reducer.apply(acc2, ch)
                                    ));
                        })
                );
    }
}