package com.codefork.aoc2024.day03;

import com.codefork.aoc2024.Problem;

public class Part02 extends Problem {

    @Override
    public String solve() {
        return String.valueOf(Computer.sumInstructionResults(getInput(), true));
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
