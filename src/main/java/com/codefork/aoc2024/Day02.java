package com.codefork.aoc2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day02 {

    public static List<Integer> createReport(String line) {
        return Arrays.stream(line.split(" "))
                .map(Integer::valueOf)
                .toList();
    }

    /**
     * @return a list of difference values for each level change in the report
     */
    public static List<Integer> getLevelDiffs(List<Integer>levels) {
        var diffs = new ArrayList<Integer>();
        for (var i = 0; i < levels.size(); i++) {
            if (i > 0) {
                var diff = levels.get(i) - levels.get(i - 1);
                diffs.add(diff);
            }
        }
        return diffs;
    }

    public static boolean isSafe(String line, boolean tolerant) {
        var report = createReport(line);
        return isSafe(report, tolerant);
    }

    public static boolean isSafe(List<Integer> report, boolean tolerant) {
        var diffs = getLevelDiffs(report);
        var numDiffs = diffs.size();
        var countValidIncreasing = diffs.stream().filter(d -> d > 0 && d <= 3).count();
        var countValidDecreasing = diffs.stream().filter(d -> d < 0 && d >= -3).count();

        //System.out.printf("%s %s %s %s %s%n", tolerant, report, diffs, countValidIncreasing, countValidDecreasing);

        if(countValidIncreasing == numDiffs || countValidDecreasing == numDiffs) {
            return true;
        }

        if(tolerant) {
            // I tried a lot of shenanigans to try to intelligently remove a level
            // but both the selection logic and anticipating effects is trickier than it might seem.
            // so I ended up just brute-forcing it. ¯\_(ツ)_/¯
            for(var i = 0; i < report.size(); i++) {
                var reportCopy = new ArrayList<>(report);
                reportCopy.remove(i);
                if(isSafe(reportCopy, false)) {
                    return true;
                }
            }
        }
        return false;
    }

}
