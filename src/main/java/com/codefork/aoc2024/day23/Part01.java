package com.codefork.aoc2024.day23;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var edges = Network.parseEdges(data);
        var grower = new NetworkGrower(edges, 3);
        var networks = grower.grow();
        return String.valueOf(networks.size());
    }

    @Override
    public String solve() {
        Assert.assertEquals("7", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
