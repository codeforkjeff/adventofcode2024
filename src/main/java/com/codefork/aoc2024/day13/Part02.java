package com.codefork.aoc2024.day13;

import com.codefork.aoc2024.Problem;

import java.util.Optional;
import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var machines = Machine.consume(data.iterator(), 10000000000000L);
        var tokens = machines.stream()
                //.peek(System.out::println)
                .map(Machine::findSolution)
                .filter(Optional::isPresent)
                //.peek(System.out::println)
                .map(Optional::orElseThrow)
                .map(solution -> (solution.countA() * 3) + solution.countB())
                .mapToLong(i-> i)
                .sum();

        return String.valueOf(tokens);
    }

    @Override
    public String solve() {
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
