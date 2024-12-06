package com.codefork.aoc2024.day06;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

// TODO: 538 is too low
// 842 is too low
// 1876 is too high
// 1878 is wrong
public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var lab = Lab.create(data);
        lab.patrol(true);
        return String.valueOf(lab.countObstructions());
    }

    @Override
    public String solve() {
        Assert.assertEquals("6", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
