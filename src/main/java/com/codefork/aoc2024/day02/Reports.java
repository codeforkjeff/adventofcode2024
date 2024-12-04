package com.codefork.aoc2024.day02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Reports {

    public static List<Integer> createReport(String line) {
        return Arrays.stream(line.split(" "))
                .map(Integer::valueOf)
                .toList();
    }

    /**
     * @return a list of difference values for each level change in the report
     */
    public static List<Integer> getLevelDiffs(List<Integer>levels) {
        return IntStream
                .range(1, levels.size())
                .boxed()
                .map(i -> levels.get(i) - levels.get(i - 1)).toList();
    }

    public static boolean isSafe(String line, boolean tolerant) {
        var report = createReport(line);
        return isSafe(report, tolerant);
    }

    public static boolean isSafe(List<Integer> report, boolean tolerant) {
        final var diffs = getLevelDiffs(report);
        final var numDiffs = diffs.size();
        final var countValidIncreasing = diffs.stream().filter(d -> d > 0 && d <= 3).count();
        final var countValidDecreasing = diffs.stream().filter(d -> d < 0 && d >= -3).count();

        //System.out.printf("%s %s %s %s %s%n", tolerant, report, diffs, countValidIncreasing, countValidDecreasing);

        if(countValidIncreasing == numDiffs || countValidDecreasing == numDiffs) {
            return true;
        }

        if(tolerant) {
            // I tried a lot of shenanigans to try to intelligently remove a level
            // but both the selection logic and anticipating effects is trickier than it might seem.
            // so I ended up just brute-forcing it. ¯\_(ツ)_/¯

            // is there a safe report among all the possibilities of having a level removed?
            return IntStream
                    .range(0, report.size())
                    .anyMatch(i -> {
                        var reportCopy = new ArrayList<>(report);
                        reportCopy.remove(i);
                        return isSafe(reportCopy, false);
                    });
        }
        return false;
    }

}
