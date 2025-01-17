package com.codefork.aoc2024.day20;

import com.codefork.aoc2024.Problem;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data, boolean printSummary) {
        var racetrack = Racetrack.create(data);

        // not sure how to optimize this. there is probably some short-circuiting
        // that's possible, but I can't see how

        var allCheats = IntStream.range(2, 21).boxed()
                .flatMap(n -> {
                    var start = System.currentTimeMillis();
                    var cheats = racetrack.findCheats(n);
//                    System.out.println("n=" + n + " took " + (System.currentTimeMillis() - start) +
//                            "ms to find " + cheats.size() + " cheats");
                    return cheats.stream();
                })
                .collect(Collectors.toSet());
        if (printSummary) {
            Racetrack.printCheatsSummary(allCheats);
        }

        var count = allCheats.stream()
                .filter(cheat -> cheat.saved() >= 100)
                .count();

        return String.valueOf(count);
    }

    @Override
    public String solve() {
        //return solve(getSampleInput(), true);
        printTimeNotice("25s");
        return solve(getInput(), false);
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
