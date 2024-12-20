package com.codefork.aoc2024.day18;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.List;
import java.util.stream.Stream;

public class Part02 extends Problem {

    public boolean isUnreachable(int height, int width, List<String> lines) {
        return MemoryMap
                .create(height, width, lines.stream())
                .countStepsOnShortestPath()
                .isEmpty();
    }

    public String solve(int height, int width, Stream<String> data, int start) {
        var lines = data.toList();

        // there are 2426 positions to try in the real input, which is very doable
        // using brute force, but it would take a few minutes. I opted to do a binary
        // search instead. this searches for the boundary between reachable -> unreachable
        // MemoryMaps, so we can find the first position that made it unreachable.
        var lower = start;
        var upper = lines.size();

        var lastUnreachableN = -1;
        int solutionN = -1;
        while (solutionN == -1) {
            var mid = (lower + upper) / 2;
            var unreachable = isUnreachable(height, width, lines.subList(0, mid + 1));
            if (unreachable) {
                // remember it, and update upper
                lastUnreachableN = mid;
                upper = mid;
            } else {
                // we found a solution: are we at the boundary where the next
                // entry would be unreachable? if so, we found the solution!
                // if not, increase lower and keep going
                if (mid + 1 == lastUnreachableN) {
                    solutionN = lastUnreachableN;
                } else {
                    lower = mid + (mid == lower ? 1 : 0);
                }
            }
        }
        return lines.get(solutionN);
    }

    @Override
    public String solve() {
        Assert.assertEquals("6,1", solve(7, 7, getSampleInput(), 12));
        return solve(71, 71, getInput(), 1024);
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
