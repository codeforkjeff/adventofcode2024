package com.codefork.aoc2024.day08;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Locates all the positions on the line formed by two antennas,
 * including the antennas themselves
 */
public class Part2Locator implements AntinodeLocator {
    @Override
    public List<Antenna> getAntinodes(AntennaMap map, Line line) {

        // find all the coordinates on the one side of line
        var xPossibleSide1 = IntStream
                .iterate(line.a1().pos().x(), (x -> x - line.deltaX()))
                .boxed()
                .takeWhile(x -> x >= 0 && x < map.width())
                .toList();

        var yPossibleSide1 = IntStream
                .iterate(line.a1().pos().y(), (y -> y - line.deltaY()))
                .boxed()
                .takeWhile(y -> y >= 0 && y < map.height())
                .toList();

        var sizeSide1 = Math.min(xPossibleSide1.size(), yPossibleSide1.size());

        var antennasDesc = IntStream
                .range(0, sizeSide1)
                .mapToObj(i ->
                        new Antenna(line.a1().type(), new Position(xPossibleSide1.get(i), yPossibleSide1.get(i)))
                ).toList();

        // find all the coordinates on the second side of line

        var xPossibleSide2 = IntStream
                .iterate(line.a2().pos().x(), (x -> x + line.deltaX()))
                .boxed()
                .takeWhile(x -> x >= 0 && x < map.width())
                .toList();

        var yPossibleSide2 = IntStream
                .iterate(line.a2().pos().y(), (y -> y + line.deltaY()))
                .boxed()
                .takeWhile(y -> y >= 0 && y < map.height())
                .toList();

        var sizeSide2 = Math.min(xPossibleSide2.size(), yPossibleSide2.size());

        var antennasAsc = IntStream
                .range(0, sizeSide2)
                .mapToObj(i ->
                        new Antenna(line.a1().type(), new Position(xPossibleSide2.get(i), yPossibleSide2.get(i)))
                ).toList();

        var results = new ArrayList<Antenna>();
        // include antennas in the line themselves
        results.addAll(List.of(line.a1(), line.a2()));
        results.addAll(antennasDesc);
        results.addAll(antennasAsc);
        return results;
    }

}
