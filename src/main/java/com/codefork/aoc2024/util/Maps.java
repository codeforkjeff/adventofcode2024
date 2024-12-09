package com.codefork.aoc2024.util;

import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Maps {

    /**
     * merge two Maps in a functional style
     **/
    public static <K, V> Map<K, V> merge(Map<K, V> m1, Map<K, V> m2,
                                         BinaryOperator<V> mergeValues) {
        return Stream
                .concat(m1.entrySet().stream(), m2.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        mergeValues)
                );

    }

    /**
     * this can be used for the "mergeValues" param of merge()
     * when values are Lists
     */
    public static <V> List<V> listConcat(List<V> v1, List<V> v2) {
        return Stream.concat(v1.stream(), v2.stream()).toList();
    }

}
