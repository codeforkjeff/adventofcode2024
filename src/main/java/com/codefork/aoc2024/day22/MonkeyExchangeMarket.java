package com.codefork.aoc2024.day22;

import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class MonkeyExchangeMarket {

    public static long mix(long a, long b) {
        return a ^ b;
    }

    public static long prune(long a) {
        return a % 16777216;
    }

    public static long getNextNumber(long n) {
        var r1 = prune(mix(n * 64, n));
        var r2 = prune(mix(r1 / 32, r1));
        var r3 = prune(mix(r2 * 2048, r2));
        return r3;
    }

    public static long sum(Stream<String> data, int generations) {
        return data.mapToLong(line -> {
            var initial = Long.parseLong(line);
            return LongStream
                    .range(0, generations)
                    .reduce(initial, (acc, i) -> getNextNumber(acc));
        }).sum();
    }

    public static int getOnesDigit(long n) {
        var nStr = String.valueOf(n);
        var onesDigitStr = nStr.substring(nStr.length() - 1);
        return Integer.parseInt(onesDigitStr);
    }

    /*
     * generate pseudorandom numbers starting from initial, for max iterations,
     * and return a map of 4-change sequences to the price at that sequence.
     */
    public static Map<List<Integer>, Integer> createChangesMap(long initial, int max) {
        var number = initial;
        var i = 0;
        var lastChanges = new LinkedList<Integer>();
        var changesToPrice = new HashMap<List<Integer>, Integer>();
        while(i < max) {
            var numberOnesDigit = getOnesDigit(number);
            var newNumber = getNextNumber(number);
            var newNumberOnesDigit = getOnesDigit(newNumber);
            var change = newNumberOnesDigit - numberOnesDigit;

            lastChanges.add(change);
            if (lastChanges.size() == 5) {
                lastChanges.removeFirst();
            }
            if (lastChanges.size() == 4) {
                var changesKey = new ArrayList<>(lastChanges);
                // only store it if this is the first time we've seen it
                if (!changesToPrice.containsKey(changesKey)) {
                    //System.out.println("adding key=" + changesKey + " value=" + newNumberOnesDigit);
                    changesToPrice.put(changesKey, newNumberOnesDigit);
                } else {
                    //System.out.println("already seen key=" + changesKey);
                }
            }
//            if (i == 0) {
//                System.out.println(number + ": " + numberOnesDigit);
//            }
//            System.out.println(newNumber + ": " + newNumberOnesDigit + " (" + change + ")");
            // update all our state variables
            number = newNumber;
            i++;
        }
        return changesToPrice;
    }

    public record Sequence(List<Integer> seq, int totalBananas) {

    }

    public static Sequence findSequence(Stream<String> data, int generations) {
        record Flat(List<Integer> changes, Integer price) {

        }

        var results = data
                .map(line -> createChangesMap(Long.parseLong(line), generations))
                .flatMap(changesMap ->
                        // transform key/value pairs in map to a list
                        changesMap.entrySet().stream().map(entry ->
                                new Flat(entry.getKey(), entry.getValue())
                        )
                )
                .toList();

        // group the Flats by changes
        var byChanges = results.stream()
                .collect(Collectors.groupingBy(Flat::changes));

        // sum the Flats in the values part of map
        var changesToSums = byChanges.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        (entry) ->
                            entry.getValue().stream().mapToInt(Flat::price).sum()
                        )
                );

        var highestTotal = changesToSums.values().stream().mapToInt(i -> i).max().orElseThrow();

        var changesFound = changesToSums.keySet().stream()
                .filter(changes -> Integer.valueOf(highestTotal).equals(changesToSums.get(changes)))
                .toList();

        Assert.assertEquals(1, changesFound.size());

        return new Sequence(changesFound.getFirst(), highestTotal);
    }

}
