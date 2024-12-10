package com.codefork.aoc2024.day10;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var topoMap = new TopoMap(data);
        // findTrails() returns all the unique trails in the map,
        // so counting them is equivalent to finding all the unique trails per trailhead
        var numTrails = topoMap.findTrails().size();
        return String.valueOf(numTrails);
    }

    @Override
    public String solve() {
        Assert.assertEquals("81", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
