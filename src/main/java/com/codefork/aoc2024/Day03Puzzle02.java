package com.codefork.aoc2024;

public class Day03Puzzle02 extends Problem {

    @Override
    public String solve() {
        return String.valueOf(Day03.sumInstructionResults(getInput(), true));
    }

    public static void main(String[] args) {
        new Day03Puzzle02().run();
    }

}
