package com.codefork.aoc2024.day23;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var edges = Network.parseEdges(data);
        var walker = NetworkFinder.seed(edges);
        var network = walker.findLargestNetwork();
        return network.toString();
    }

    @Override
    public String solve() {
        Assert.assertEquals("co,de,ka,ta", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
