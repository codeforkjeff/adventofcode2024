package com.codefork.aoc2024.day08;

import java.util.List;

/**
 * Locates 1 position each on either side of the line segment formed by two antennas
 */
public class Part1Locator implements AntinodeLocator {

    @Override
    public List<Antenna> getAntinodes(AntennaMap map, Line line) {
        return List.of(
                new Antenna(line.a1().type(), new Position(line.a1().pos().x() - line.deltaX(), line.a1().pos().y() - line.deltaY())),
                new Antenna(line.a1().type(), new Position(line.a2().pos().x() + line.deltaX(), line.a2().pos().y() + line.deltaY())));
    }
}
