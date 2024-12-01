package com.codefork.aoc2024;

import java.util.ArrayList;
import java.util.Collections;

public class Day01Puzzle01 extends Problem {

    @Override
    public String solve() {
        var lists = new ArrayList<ArrayList<Integer>>();
        lists.add(new ArrayList<Integer>());
        lists.add(new ArrayList<Integer>());

        getInput().forEach(line -> {
            var parts = line.split("\\s+");
            for(var i =0; i < parts.length; i++) {
                lists.get(i).add(Integer.valueOf(parts[i]));
            }
        });

        Collections.sort(lists.get(0));
        Collections.sort(lists.get(1));

        var totalDistance = 0;
        for(var i = 0; i < lists.get(0).size(); i++) {
            totalDistance += Math.abs(lists.get(1).get(i) - lists.get(0).get(i));
        }

        return String.valueOf(totalDistance);
    }

    public static void main(String[] args) {
        new Day01Puzzle01().run();
    }

}
