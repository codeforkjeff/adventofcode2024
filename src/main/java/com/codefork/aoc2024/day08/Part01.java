package com.codefork.aoc2024.day08;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {

        var antennaMap = AntennaMap.create(data, new Part1Locator());

        return String.valueOf(antennaMap.countUniquePositionsOfAntinodes());
    }

    @Override
    public String solve() {
        Assert.assertEquals("14", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
