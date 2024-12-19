package com.codefork.aoc2024.day16;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;
import com.codefork.aoc2024.util.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part01 extends Problem {

    public void printPath(List<Edge> path) {
        for (var edge : path) {
            System.out.printf("%s; ", edge);
        }
        System.out.println();
    }

    public void printEdges(Set<Edge> edges) {
        var sorted = edges.stream()
                .sorted(Comparator.comparingInt(e -> e.from().pos().x()))
                .toList();
        for(var e : sorted) {
            System.out.println(e);
        }
    }

    /**
     * This was my first, naive attempt at a solution:
     * Walk the entire maze, finding every possible path from start to end.
     * This never completes for the puzzle input, even after hours; I'm not sure
     * if it's because there are mathematically too many combinations
     * or there's something amiss in this implementation.
     */
    @SuppressWarnings("unused")
    public String dfsSolution(Maze maze) {
        var edges = maze.buildGraph();
        System.out.println("there are " + edges.size() + " edges");

        var edgesMap = edges.stream()
                .collect(Collectors.toMap(
                        Edge::from,
                        List::of,
                        Maps::listConcat
                )
        );

        // create initial paths from start
        var seedPaths = edgesMap
                .get(new Reindeer(maze.start(), Direction.East))
                .stream()
                .map(List::of)
                .toList();

        var stack = new Stack<List<Edge>>();
        for (var path : seedPaths) {
            stack.push(path);
        }

        var minScore = Integer.MAX_VALUE;
        var i = 0;
        while (!stack.isEmpty()) {
            var path = stack.pop();
            //printPath(path);
            var last = path.getLast();
            if (!last.to().pos().equals(maze.end())) {
                var alreadyInPath = path.stream().flatMap(e -> Stream.of(e.from().pos(), e.to().pos())).collect(Collectors.toSet());
                var edgesToAdd = edgesMap.getOrDefault(last.to(), Collections.emptyList()).stream()
                        .filter(edgeToAdd -> !alreadyInPath.contains(edgeToAdd.to().pos()))
                        .toList();
                for (var edgeToAdd : edgesToAdd) {
                    var newPath = new ArrayList<>(path);
                    newPath.add(edgeToAdd);
                    stack.push(newPath);
                }
            } else {
                var score = Edge.calculateScore(path);
                if (score < minScore) {
                    minScore = score;
                    System.out.println("set minScore=" + minScore);
                }
            }
            i++;
            if (i % 100000 == 0) {
                System.out.println("iterations so far=" + i);
                System.out.println("stack size=" + stack.size());
            }
        }
        System.out.println("total iterations=" + i);
        return String.valueOf(minScore);
    }

    /**
     * Implemented using pseudocode from
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode">wikipedia</a>
     * <p>
     * This is tricky because we need to take into account not just the position,
     * but the direction the reindeer is facing, when looking up distance (scores).
     * This is why most of the data structures here store Reindeer rather than Positions,
     * because direction matters.
     */
    public String dijkstraSolution(Maze maze) {

        var edges = maze.buildGraph();
        //printEdges(edges);

        var dijkstra = Dijkstra.create(maze, edges);

        var paths = dijkstra.findBestPaths();

        var minScore = Reindeer.getPathScore(paths.getFirst());

        return String.valueOf(minScore);
    }

    public String solve(Stream<String> data) {
        var maze = Maze.parse(data);
        //return dfsSolution(maze);
        return dijkstraSolution(maze);
    }

    @Override
    public String solve() {
        Assert.assertEquals("7036", solve(getSampleInput()));
        Assert.assertEquals("11048", solve(getFileAsStream("sample2")));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
