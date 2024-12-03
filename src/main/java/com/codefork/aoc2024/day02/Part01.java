package com.codefork.aoc2024.day02;

import com.codefork.aoc2024.Problem;

public class Part01 extends Problem {

    @Override
    public String solve() {
        var countSafe = getInput().reduce(0, (acc, line) -> acc + (Reports.isSafe(line, false) ? 1 : 0), Integer::sum);
        return String.valueOf(countSafe);
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
