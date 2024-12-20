package com.codefork.aoc2024.day19;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var onsen = Onsen.create(data);
        return String.valueOf(onsen.countViableDesigns());
    }

    @Override
    public String solve() {
        Assert.assertEquals("6", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
