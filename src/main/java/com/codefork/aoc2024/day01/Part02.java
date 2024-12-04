package com.codefork.aoc2024.day01;

import com.codefork.aoc2024.Problem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Part02 extends Problem {

    @Override
    public String solve() {
        final var lines = getInput().map(line ->
            Arrays.stream(line.split("\\s+")).map(Integer::valueOf).toList()
        ).toList();

        final var list2freq = lines.stream().map(line -> line.get(1)).collect(Collectors.toMap(
                (item) -> item,
                (item) -> 1,
                Integer::sum));

        final var list1 = lines.stream().map(List::getFirst);

        final var similarity = list1.reduce(0, (acc, n) -> acc + (n * list2freq.getOrDefault(n, 0)));

        return String.valueOf(similarity);
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
