package com.codefork.aoc2024.day11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

/**
 * For part 1, I implemented a naive solution, representing the complete list
 * of stones for each evolution. This worked fine for 25 evolutions, but for part 2,
 * 75 evolutions resulted in running out of memory: even a single stone will generate
 * tens of millions of stones at ~40 evolutions.
 * <p>
 * I realized it's not necessary to store the complete list for every evolution: for the purposes
 * of answering the questions, order doesn't matter. Examining the actual lists reveals that the
 * number of UNIQUE stone numbers is actually very small. So a more efficient representation of Stones
 * is a map of stone numbers to their counts. This results in a small map, with very high counts.
 * Evolving the "lists" of stones just means figuring out what the new stone numbers are,
 * and updating the counts.
 * </p>
 */
public class Stones {

    // a map of stone numbers to counts of each stone
    // i.e. key = 3, value = 4 means there are 4 stones with number 3
    private final Map<Long, Long> stones;

    public Stones(Stream<String> data) {
        this(data
                .findFirst()
                .map(line -> Arrays.stream(line.split(" "))
                        .map(Long::parseLong)
                        .toList())
                .orElseThrow()
                .stream()
                .collect(Collectors.toMap(
                        (stone) -> stone,
                        (stone) -> 1L,
                        Long::sum
                )));
    }

    public Stones(Map<Long, Long> stones) {
        this.stones = stones;
        //System.out.println(stones);
    }

    public static void addToMapCount(Map<Long, Long> map, Long stone, long increment) {
        map.put(stone, map.getOrDefault(stone, 0L) + increment);
    }

    public static void subFromMapCount(Map<Long, Long> map, Long stone, long decrement) {
        map.put(stone, map.get(stone) - decrement);
    }

    public Stones evolve() {
        var newStones = new HashMap<>(stones);

        // making copies of Maps in an FP solution is too expensive,
        // so just iterate and mutate
        for(var stone : stones.keySet()) {
            // update based on count of stones with this stone number
            var numStones = stones.get(stone);
            if(stone == 0L) {
                addToMapCount(newStones, 1L, numStones);
            } else if(String.valueOf(stone).length() % 2 == 0) {
                var s = String.valueOf(stone);
                var halfSize = s.length() / 2;
                var newStone1 = Long.parseLong(s.substring(0, halfSize));
                var newStone2 = Long.parseLong(s.substring(halfSize));
                addToMapCount(newStones, newStone1, numStones);
                addToMapCount(newStones, newStone2, numStones);
            } else {
                addToMapCount(newStones, stone * 2024L, numStones);
            }
            subFromMapCount(newStones, stone, numStones);
        }

        // remove stones where count = 0
        newStones.entrySet().removeIf(entry -> entry.getValue() == 0);

        return new Stones(newStones);
    }

    public Stones evolve(int iterations) {
        return IntStream.range(0, iterations)
                .boxed()
                .collect(foldLeft(
                        () -> this,
                        (acc, i) -> acc.evolve()
                ));
    }

    public long count() {
        return stones.values().stream().mapToLong(v -> v).sum();
    }
}
