package com.codefork.aoc2024.day01;

import com.codefork.aoc2024.Problem;

import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Part01 extends Problem {

    record Location(int col, int value) {

    }

    @Override
    public String solve() {
        // transform input into a list of heaps, one for each "column" in the input file.
        // seems like there should be a more compact way to do this, while keeping to "pure" FP?

        // put each value location id into a Location object, which stores
        // its column index (i.e. list), and group by that column index
        final var byCol = getInput()
                .flatMap(line -> {
                    final var parts = line.split("\\s+");
                    return IntStream
                            .range(0, parts.length)
                            .mapToObj(i -> new Location(i, Integer.parseInt(parts[i])));
                })
                .collect(Collectors.groupingBy(Location::col));

        // make a list of PriorityQueues out of the grouped data
        final var lists = IntStream
                .range(0, byCol.size())
                .mapToObj(i ->
                        new PriorityQueue<Integer>(byCol.get(i).stream().map(Location::value).toList())
                )
                .toList();

        final var totalDistance = IntStream
                .range(0, lists.get(0).size())
                .reduce(0, (acc, i) ->
                        // remove() mutates the PriorityQueue. not sure if there's a more FP-ish way to do this
                        acc + Math.abs(lists.get(1).remove() - lists.get(0).remove())
                );

        return String.valueOf(totalDistance);
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
