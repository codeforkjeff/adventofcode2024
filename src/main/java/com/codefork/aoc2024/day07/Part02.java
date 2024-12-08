package com.codefork.aoc2024.day07;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.Arrays;
import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var total = Calc.createCalcs(data)
                .filter(calc -> {
                    var combos = calc.createCombos(Arrays.asList(Operator.values()));
                    return combos.stream().anyMatch(calc::testCombo);
                })
                .mapToLong(Calc::testValue)
                .sum();

        return String.valueOf(total);
    }

    @Override
    public String solve() {
        Assert.assertEquals("11387", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
