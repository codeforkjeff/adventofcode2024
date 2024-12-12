package com.codefork.aoc2024.day11;

import com.codefork.aoc2024.Problem;

public class Part02 extends Problem {

    @Override
    public String solve() {
        // part 2 doesn't say what the answer is for the sample input
        var stones = new Stones(getInput());
        var endState = stones.evolve(75);
        return String.valueOf(endState.count());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
