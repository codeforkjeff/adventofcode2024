package com.codefork.aoc2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Day01Puzzle02 extends Problem {

    @Override
    public String solve() {
        var list = new ArrayList<Integer>();
        var list2freq = new HashMap<Integer, Integer>();

        getInput().forEach(line -> {
            var parts = Arrays.stream(line.split("\\s+")).map(Integer::valueOf).toArray(Integer[]::new);
            list.add(parts[0]);
            list2freq.put(parts[1], list2freq.getOrDefault(parts[1], 0) + 1);
        });

        var similarity = list.stream().reduce(0, (acc, n) -> acc + (n * list2freq.getOrDefault(n, 0)));

        return String.valueOf(similarity);
    }

    public static void main(String[] args) {
        new Day01Puzzle02().run();
    }

}
