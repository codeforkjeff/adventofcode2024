package com.codefork.aoc2024.day21;

import com.codefork.aoc2024.Problem;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        System.out.println("TODO: This takes ~1.5 minutes to run, needs to be optimized");
        return String.valueOf(ShipLock.calculateSumOfComplexities(data, 25));
    }

    @Override
    public String solve() {
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
