package com.codefork.aoc2024.day01;

import com.codefork.aoc2024.Problem;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

public class Part01 extends Problem {

    @Override
    public String solve() {
        // transform input into a list of heaps, one for each "column" in the input file
        final var lists = getInput().collect(
                () -> new ArrayList<PriorityQueue<Integer>>(),
                (acc, line) -> {
                    var parts = line.split("\\s+");
                    for (var i = 0; i < parts.length; i++) {
                        if (i >= acc.size()) {
                            acc.add(new PriorityQueue<Integer>());
                        }
                        acc.get(i).add(Integer.valueOf(parts[i]));
                    }
                },
                ArrayList::addAll);

        final var totalDistance = IntStream
                .range(0, lists.get(0).size()).reduce(0, (acc, i) ->
                        acc + Math.abs(lists.get(1).remove() - lists.get(0).remove())
                );

        return String.valueOf(totalDistance);
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
