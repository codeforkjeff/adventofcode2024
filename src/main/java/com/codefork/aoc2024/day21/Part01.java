package com.codefork.aoc2024.day21;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        return String.valueOf(ShipLock.calculateSumOfComplexities(data, 2));
    }

    @Override
    public String solve() {
        Assert.assertEquals("126384", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
