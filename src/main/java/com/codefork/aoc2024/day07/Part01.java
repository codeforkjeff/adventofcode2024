package com.codefork.aoc2024.day07;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var total = Calc.createCalcs(data)
                .filter(calc -> {
                    var possibleOperators = new ArrayList<Operator>();
                    possibleOperators.add(Operator.Add);
                    possibleOperators.add(Operator.Multiply);
                    var combos = calc.createCombos(possibleOperators);
                    return combos.stream().anyMatch(calc::testCombo);
                })
                .mapToLong(Calc::testValue)
                .sum();

        return String.valueOf(total);
    }

    @Override
    public String solve() {
        Assert.assertEquals("3749", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
