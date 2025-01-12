package com.codefork.aoc2024.day17;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var computer = Computer.parse(data);
        var finalState = computer.run();
        return finalState.getOutputAsString();
    }

    @Override
    public String solve() {
        Assert.assertEquals("4,6,3,5,6,3,5,2,1,0", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
