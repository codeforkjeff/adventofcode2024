package com.codefork.aoc2024.day24;

import com.codefork.aoc2024.Problem;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        System.out.println("This takes ~35s to run");
        var device = Device.parse(data);
        return Device.formatAnswer(device.findSwapsToMakeAdder());
    }

    @Override
    public String solve() {
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
