package com.codefork.aoc2024.day20;

import com.codefork.aoc2024.Problem;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data, boolean printSummary) {
        var racetrack = Racetrack.create(data);

        var cheats = racetrack.findCheats(2);
        if(printSummary) {
            Racetrack.printCheatsSummary(cheats);
        }

        var count = cheats.stream()
                .filter(cheat -> cheat.saved() >= 100)
                .count();

        return String.valueOf(count);
    }

    @Override
    public String solve() {
        //return solve(getSampleInput(), true);
        return solve(getInput(), false);
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
