package com.codefork.aoc2024.day14;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(int width, int height, Stream<String> data) {
        var swarm = Swarm.create(data, width, height);
        var finalSwarm = swarm.doMoves(100);
        return String.valueOf(finalSwarm.getSafetyFactor());
    }

    @Override
    public String solve() {
        Assert.assertEquals("12", solve(11, 7, getSampleInput()));
        return solve(101, 103, getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
