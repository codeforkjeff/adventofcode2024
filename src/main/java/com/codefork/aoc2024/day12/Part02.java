package com.codefork.aoc2024.day12;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var gardenPlots = new GardenPlots(data);
        return String.valueOf(gardenPlots.getFencingCost(true));
    }

    @Override
    public String solve() {
        Assert.assertEquals("1206", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
