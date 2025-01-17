package com.codefork.aoc2024.day14;

import com.codefork.aoc2024.Problem;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(int width, int height, Stream<String> data) {
        // I tried a bunch of different strategies, including looking for unbalanced robot
        // counts in different quadrants, thinking that the quadrant stuff in part 1 was a hint.
        // I couldn't get that to work. after stepping away from it for over a week,
        // I thought to try a different approach: just look for clusters of adjacent robots,
        // since a picture is likely to have such clusters. at a low threshold, it displayed
        // a few false positives before yielding the answer, so it was just a matter of increasing
        // the threshold to something reasonable once I knew what the Christmas tree actually looked like.

        printTimeNotice("40s");
        var swarm = Swarm.create(data, width, height);
        var keepGoing = true;
        var i = 0;
        while(keepGoing) {
            var newSwarm = swarm.doMoves(1);

            var adjacentClusters = newSwarm.getAdjacentClusters();

            var hasLines = adjacentClusters.stream().anyMatch(cluster ->
                    cluster.size() > 20);

            if(hasLines) {
                newSwarm.print();
                keepGoing = false;
            } else {
                swarm = newSwarm;
                i++;
            }
        }

        return String.valueOf(i+1);
    }

    @Override
    public String solve() {
        return solve(101, 103, getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
