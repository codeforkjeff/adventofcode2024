package com.codefork.aoc2024.day16;

import com.codefork.aoc2024.util.WithIndex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Maze(Set<Position> positions, Position start, Position end) {

    public Maze withStart(Position newStart) {
        return new Maze(positions, newStart, end);
    }

    public Maze withEnd(Position newEnd) {
        return new Maze(positions, start, newEnd);
    }

    public boolean isWall(Position pos) {
        return !positions().contains(pos);
    }

    /**
     * get all the intersections, and the maze's end point, if you start from pos and move in dirToTry, until you hit a wall
     */
    public List<Position> getIntersectionsInDir(Position pos, Direction dirToTry) {
        var current = pos.move(dirToTry);
        //System.out.println("moving " + dirItem + " to " + current);
        var intersections = new ArrayList<Position>();
        while (!isWall(current)) {
            var isIntersection = switch (dirToTry) {
                case Direction.North, Direction.South ->
                        positions().contains(current.decX()) || positions().contains(current.incX());
                case Direction.East, Direction.West ->
                        positions().contains(current.decY()) || positions().contains(current.incY());
            };
            if (isIntersection || current.equals(end())) {
                intersections.add(current);
            }
            current = current.move(dirToTry);
        }
        return intersections;
    }

    /**
     * return all the possible directions from which it's possible to approach this position
     */
    public Set<Direction> getPossibleApproaches(Position pos) {
        var possible = new HashSet<Direction>();
        if (positions().contains(pos.decX())) {
            possible.add(Direction.East);
        }
        if (positions().contains(pos.incX())) {
            possible.add(Direction.West);
        }
        if (positions().contains(pos.decY())) {
            possible.add(Direction.South);
        }
        if (positions().contains(pos.incY())) {
            possible.add(Direction.North);
        }
        return possible;
    }

    /**
     * build edges for every pair of connected intersections in the maze,
     * taking into account starting and ending direction
     */
    public Set<Edge> buildGraph() {
        var edges = new HashSet<Edge>();
        var intersections = new Stack<Reindeer>();
        var visited = new HashSet<Position>();

        intersections.add(new Reindeer(start, Direction.East));

        // walk algorithm:
        // pop an intersection from the stack: use this as current pos
        // find ALL the other intersections along the X and Y axis of current pos
        // add all possible edges, taking into consideration starting/ending directions, to Edges
        // push the new intersections on the stack

        while (!intersections.isEmpty()) {
            var current = intersections.pop();
            if (visited.contains(current.pos())) {
                continue;
            }
            //System.out.println("visiting " + current);
            for (var dirToTry : Direction.values()) {
                var intersectionsForCurrent = getIntersectionsInDir(current.pos(), dirToTry);
//                    if(!intersectionsForCurrent.isEmpty()) {
//                        System.out.println(current.pos() + " " + dirToTry + " intersections=" + intersectionsForCurrent);
//                    }
                for (var i : intersectionsForCurrent.reversed()) {
                    var target = new Reindeer(i, dirToTry);
                    // generate every Edge possibility for different ways to approach current,
                    // including current's dir
                    var approaches = getPossibleApproaches(current.pos());
                    approaches.add(current.dir());
                    for (var approach : approaches) {
                        var edge = new Edge(new Reindeer(current.pos(), approach), target);
                        edges.add(edge);
                        //System.out.println("added edge=" + edge);
                    }
                    intersections.push(target);
                }
            }
            visited.add(current.pos());
        }
        return edges;
    }

    public static Maze parse(Stream<String> data) {
        return data
                .map(WithIndex.indexed())
                .filter(lineWithIndex -> !lineWithIndex.value().isEmpty())
                .collect(foldLeft(
                        () -> new Maze(new HashSet<>(), new Position(-1, -1), new Position(-1, -1)),
                        (acc, lineWithIndex) -> {
                            var y = lineWithIndex.index();
                            var line = lineWithIndex.value();

                            return line.chars()
                                    .mapToObj(ch -> String.valueOf((char) ch))
                                    .map(WithIndex.indexed())
                                    .collect(foldLeft(
                                            () -> acc,
                                            (acc2, charWithIndex) -> {
                                                var x = charWithIndex.index();
                                                var ch = charWithIndex.value();
                                                var pos = new Position(x, y);
                                                if (".".equals(ch)) {
                                                    acc2.positions().add(pos);
                                                    return acc2;
                                                } else if ("S".equals(ch)) {
                                                    acc2.positions().add(pos);
                                                    return acc2.withStart(pos);
                                                } else if ("E".equals(ch)) {
                                                    acc2.positions().add(pos);
                                                    return acc2.withEnd(pos);
                                                }
                                                return acc2;
                                            })
                                    );
                        })
                );
    }

}
