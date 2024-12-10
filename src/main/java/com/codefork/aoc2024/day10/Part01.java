package com.codefork.aoc2024.day10;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var topoMap = new TopoMap(data);

        // build a map of trailheads (first pos in trail) to peaks (last pos in trail)
        var trailheadsToPeaks = topoMap.findTrails()
                .stream()
                //.peek(System.out::println)
                .collect(Collectors.toMap(
                        List::getFirst,
                        (trail) -> (List<Position>) new ArrayList<>(Collections.singletonList(trail.getLast())),
                        (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).toList()
                ));

        // score = count of unique peaks per trailhead
        var trailheadsToScores = trailheadsToPeaks
                .keySet()
                .stream()
                .collect(Collectors.toMap(
                        (pos) -> pos,
                        (pos) -> new HashSet<>(trailheadsToPeaks.get(pos)).size()
                ));

        var total = trailheadsToScores
                .values()
                .stream()
                .mapToInt(i -> i)
                .sum();

        return String.valueOf(total);
    }

    @Override
    public String solve() {
        Assert.assertEquals("36", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
