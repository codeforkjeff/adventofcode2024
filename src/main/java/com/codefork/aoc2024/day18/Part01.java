package com.codefork.aoc2024.day18;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

/**
 * This is similar to day 16 but less complicated
 */
public class Part01 extends Problem {

    public String solve(int height, int width, Stream<String> data) {
        var memoryMap = MemoryMap.create(height, width, data);
        var result = memoryMap.countStepsOnShortestPath();
        return String.valueOf(result.orElseThrow());
    }

    @Override
    public String solve() {
        Assert.assertEquals("22", solve(7, 7, getSampleInput().limit(12)));
        return solve(71, 71, getInput().limit(1024));
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
