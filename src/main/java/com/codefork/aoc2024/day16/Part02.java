package com.codefork.aoc2024.day16;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {

        var maze = Maze.parse(data);

        var edges = maze.buildGraph();
        //printEdges(edges);

        var dijkstra = Dijkstra.create(maze, edges);

        // find the Reindeer for the end of the best path
        var bestPaths = dijkstra.findBestPaths();
        var bestPath = bestPaths.getFirst();
        var reindeerEnd = bestPath.getLast();

        var tileCount = dijkstra.countTilesOnEveryShortestPathToEnd(reindeerEnd);

        return String.valueOf(tileCount);
    }

    @Override
    public String solve() {
        Assert.assertEquals("45", solve(getSampleInput()));
        Assert.assertEquals("64", solve(getFileAsStream("sample2")));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
