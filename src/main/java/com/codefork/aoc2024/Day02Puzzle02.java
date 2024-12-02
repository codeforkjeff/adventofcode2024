package com.codefork.aoc2024;

public class Day02Puzzle02 extends Problem {

    @Override
    public String solve() {
        var countSafe = getInput().reduce(0, (acc, line) -> acc + (Day02Puzzle01.isSafe(line, true) ? 1 : 0), Integer::sum);
        return String.valueOf(countSafe);
    }

    public static void main(String[] args) {
        new Day02Puzzle02().run();
    }

}
