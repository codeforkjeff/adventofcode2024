package com.codefork.aoc2024;

public class Day03Puzzle01 extends Problem {

    @Override
    public String solve() {
        return String.valueOf(Day03.sumInstructionResults(getInput(), false));
    }

    public static void main(String[] args) {
        new Day03Puzzle01().run();
    }

}
