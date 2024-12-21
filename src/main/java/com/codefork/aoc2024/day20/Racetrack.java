package com.codefork.aoc2024.day20;

import com.codefork.aoc2024.util.Assert;
import com.codefork.aoc2024.util.Grid;
import com.codefork.aoc2024.util.WithIndex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Racetrack(int height, int width,
                        Position start, Position end,
                        Set<Position> track, Set<Position> walls,
                        List<Position> course) {

    public static Racetrack create(Stream<String> data) {
        var racetrack = Grid.parse(
                data,
                () -> new Racetrack(-1, -1, Position.UNSET, Position.UNSET, new HashSet<>(), new HashSet<>(), new ArrayList<>()),
                (acc, x, y, ch) -> {
                    var pos = new Position(x, y);
                    var newAcc = acc.withHeight(y + 1).withWidth(x + 1);
                    // just mutate track and walls here :(
                    if (".".equals(ch)) {
                        newAcc.track().add(pos);
                        return newAcc;
                    } else if ("#".equals(ch)) {
                        newAcc.walls().add(pos);
                        return newAcc;
                    } else if ("S".equals(ch)) {
                        newAcc.track().add(pos);
                        return newAcc.withStart(pos);
                    } else if ("E".equals(ch)) {
                        newAcc.track().add(pos);
                        return newAcc.withEnd(pos);
                    }
                    throw new RuntimeException("unhandled character: " + ch);
                });
        return racetrack.withCourse(racetrack.walk());
    }

    public Racetrack withStart(Position newStart) {
        return new Racetrack(height, width, newStart, end, track, walls, course);
    }

    public Racetrack withEnd(Position newEnd) {
        return new Racetrack(height, width, start, newEnd, track, walls, course);
    }

    public Racetrack withHeight(int newHeight) {
        return new Racetrack(newHeight, width, start, end, track, walls, course);
    }

    public Racetrack withWidth(int newWidth) {
        return new Racetrack(height, newWidth, start, end, track, walls, course);
    }

    public Racetrack withCourse(List<Position> newCourse) {
        return new Racetrack(height, width, start, end, track, walls, newCourse);
    }

    public Set<Position> getTrackNeighbors(Position pos) {
        return Stream.of(pos.decX(), pos.incX(), pos.decY(), pos.incY())
                .filter(p -> track().contains(p))
                .collect(Collectors.toSet());
    }

    /**
     * find all the positions at distance d from pos.
     * some of these may be invalid positions (i.e. negative coordinates);
     * we filter those out at a separate step
     **/
    public Set<Position> getPositionsFromDist(Position pos, int d) {
        // this is a diamond shape
        return IntStream.range(pos.x() - d, pos.x() + d + 1)
                .boxed()
                .flatMap(x -> {
                    var xDist = pos.x() > x ? pos.x() - x : x - pos.x();
                    var yDist = d - xDist;
                    if(yDist > 0) {
                        return Stream.of(
                                new Position(x, pos.y() + yDist),
                                new Position(x, pos.y() - yDist)
                        );
                    } else {
                        return Stream.of(new Position(x, pos.y()));
                    }
                })
                .collect(Collectors.toSet());
    }

    /**
     * Walk the racetrack, building a path. includes start and end.
     * This recursive version causes a stack overflow.
     */
    @SuppressWarnings("unused")
    public List<Position> walkRecursive(Position current, List<Position> acc) {
        var last = !acc.isEmpty() ? acc.getLast() : null;
        acc.add(current);
        if (current.equals(end)) {
            return acc;
        }
        var neighbors = getTrackNeighbors(current).stream()
                .filter(neighbor -> !neighbor.equals(last))
                .toList();
        Assert.assertEquals(1, neighbors.size());
        return walkRecursive(neighbors.getFirst(), acc);
    }

    public List<Position> walkIterative(Position current, List<Position> acc) {
        var keepGoing = true;
        while (keepGoing) {
            var last = !acc.isEmpty() ? acc.getLast() : null;
            acc.add(current);
            if (!current.equals(end)) {
                var neighbors = getTrackNeighbors(current).stream()
                        .filter(neighbor -> !neighbor.equals(last))
                        .toList();
                Assert.assertEquals(1, neighbors.size());
                current = neighbors.getFirst();
            } else {
                keepGoing = false;
            }
        }
        return acc;
    }

    public List<Position> walk() {
        return walkIterative(start, new ArrayList<>());
    }

    /**
     * find all the cheats if you're allowed exactly picoseconds to walk through walls
     */
    public Set<Cheat> findCheats(int picoseconds) {
        var regularCourseTime = course.size() - 1;
        //System.out.println("regularCourseTime=" + regularCourseTime);
        return course.stream()
                .map(WithIndex.indexed())
                .flatMap(posWithIndex -> {
                    var i = posWithIndex.index();
                    var pos = posWithIndex.value();

                    // find all the positions that actually intersect with
                    // the portion of the course still ahead of us
                    var courseAhead = new HashSet<>(course.subList(i + 1, course.size()));

                    var possibleTrackPositions = getPositionsFromDist(pos, picoseconds);
                    return possibleTrackPositions.stream()
                            .filter(courseAhead::contains)
                            .map(p -> new Cheat(pos, p, -1));
                })
                .map(cheat -> {
                    // populate "saved"
                    var cheatStartIndex = course.indexOf(cheat.start());
                    var cheatEndIndex = course.indexOf(cheat.end());

                    //  we don't need to generate the actual shortened course, just its length
                    var shortenedCourseLength = cheatStartIndex + 1
                            + picoseconds - 1
                            + (course.size() - cheatEndIndex);

                    var saved = regularCourseTime - (shortenedCourseLength - 1);

                    return cheat.withSaved(saved);
                })
                .collect(Collectors.toSet());
    }

    /**
     * prints out a summary in the format shown in the puzzle instructions.
     * helpful for verifying/debugging
     */
    public static void printCheatsSummary(Set<Cheat> cheats) {
        var grouped = cheats.stream().collect(Collectors.groupingBy(Cheat::saved));

        var sortedEntries = grouped.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .toList();

        for (var entry : sortedEntries) {
            var count = entry.getValue().size();
            var saved = entry.getKey();
            System.out.println("there are " + count + " cheats that save " + saved + " picoseconds");
        }
    }
}
