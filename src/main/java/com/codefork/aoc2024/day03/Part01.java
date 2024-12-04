package com.codefork.aoc2024.day03;

import com.codefork.aoc2024.Problem;

public class Part01 extends Problem {

    @Override
    public String solve() {
        return String.valueOf(ComputerState.sumInstructionResults(getInput(), false));
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
