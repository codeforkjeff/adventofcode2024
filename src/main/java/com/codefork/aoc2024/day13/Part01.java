package com.codefork.aoc2024.day13;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.Optional;
import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var machines = Machine.consume(data.iterator(), 0);
        var tokens = machines.stream()
                .map(Machine::findSolution)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .map(solution -> (solution.countA() * 3) + solution.countB())
                .mapToLong(i-> i)
                .sum();

        return String.valueOf(tokens);
    }

    @Override
    public String solve() {
        Assert.assertEquals("480", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
