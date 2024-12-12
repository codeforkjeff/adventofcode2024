package com.codefork.aoc2024.day11;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var stones = new Stones(data);
        var endState = stones.evolve(25);
        return String.valueOf(endState.count());
    }

    @Override
    public String solve() {
        Assert.assertEquals("55312", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
