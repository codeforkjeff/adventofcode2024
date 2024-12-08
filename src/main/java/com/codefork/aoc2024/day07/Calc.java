package com.codefork.aoc2024.day07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This is a very slow solution, taking 10s to run part 2.
 * TODO: I think this can be optimized by ordering the possible operators (in order of *, ||, +)
 * when generating combos, and short-circuiting if result of a combo is > testValue
 */
public record Calc(long testValue, List<Long> numbers) {

    public boolean testCombo(List<Operator> ops) {
        // TODO: having the reducer consume this iterator is gross
        var iter = ops.iterator();
        var result = numbers()
                .stream()
                .reduce((acc, num) -> {
                    return iter.next().execute(acc, num);
                });
        return result.orElse(-1L).equals(testValue());
    }

    /**
     * create all possible operator combos for the numbers in this calc
     */
    public List<List<Operator>> createCombos(List<Operator> possibleOperators) {
        var count = numbers().size() - 1;
        List<List<Operator>> initial = new ArrayList<>();
        initial.add(new ArrayList<>());
        return createCombos(count, possibleOperators, initial);
    }

    // recursively add one of each operator to each list in combos
    private static List<List<Operator>> createCombos(int count, List<Operator> possibleOperators, List<List<Operator>> combos) {
        if (count > 0) {
            var newLists = combos.stream().flatMap(list ->
                    //Arrays.stream(Operator.values())
                    possibleOperators.stream().map(op ->
                            Stream.concat(list.stream(), Stream.of(op)).toList()
                    )
            ).toList();
            return createCombos(count - 1, possibleOperators, newLists);
        }
        return combos;
    }

    public static Stream<Calc> createCalcs(Stream<String> data) {
        var calcs = data
                .filter(line -> !line.isEmpty())
                .map(line -> {
                    var parts = line.split(":");
                    var testValue = Long.parseLong(parts[0]);
                    var numbers = Arrays
                            .stream(parts[1].split(" "))
                            .filter(num -> !num.isEmpty())
                            .map(Long::parseLong).toList();
                    return new Calc(testValue, numbers);
                });
        return calcs;
    }
}
