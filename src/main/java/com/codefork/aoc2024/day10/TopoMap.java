package com.codefork.aoc2024.day10;

import com.codefork.aoc2024.util.WithIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopoMap {

    private final Map<Integer, List<Position>> heightsToPositions;

    public TopoMap(Stream<String> data) {
        heightsToPositions = data
                .map(WithIndex.indexed())
                .flatMap(indexed -> {
                    var y = indexed.index();
                    var line = indexed.value();
                    return line
                            .chars()
                            .mapToObj(ch -> Integer.parseInt(String.valueOf((char) ch)))
                            .map(WithIndex.indexed())
                            .map(indexed2 -> {
                                var x = indexed2.index();
                                var height = indexed2.value();
                                return new Position(x, y, height);
                            });
                }).collect(Collectors.toMap(
                        Position::height,
                        List::of,
                        (p1, p2) -> Stream.concat(p1.stream(), p2.stream()).toList()
                ));
    }

    public List<List<Position>> findTrails() {
        return findTrails(0, new ArrayList<>());
    }

    /**
     * find trails recursively by creating new trails each time we encounter
     * an adjacent position that's +1
     */
    public List<List<Position>> findTrails(int height, List<List<Position>> trails) {
        if (height == 10) {
            return trails;
        }

        var positions = heightsToPositions.get(height);

        var newTrails = (height == 0) ?
                positions.stream().map(pos ->
                        (List<Position>) new ArrayList<>(Collections.singletonList(pos))
                ).toList()
                :
                trails.stream().flatMap(trail -> {
                    var lastPos = trail.getLast();
                    // trail becomes many trails by appending adjacent positions
                    return positions.stream()
                            .filter(pos -> pos.isAdjacent(lastPos))
                            .map(pos -> {
                                var newTrail = new ArrayList<>(trail);
                                newTrail.add(pos);
                                return (List<Position>) newTrail;
                            });
                }).toList();

        return findTrails(height + 1, newTrails);
    }
}
