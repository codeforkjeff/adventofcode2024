package com.codefork.aoc2024.day08;

import com.codefork.aoc2024.util.Maps;
import com.codefork.aoc2024.util.WithIndex;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record AntennaMap(AntinodeLocator antinodeLocator, int width, int height,
                         Map<String, List<Antenna>> antennasByType) {


    public static AntennaMap create(Stream<String> data, AntinodeLocator antinodeLocator) {
        return data
                .map(WithIndex.indexed())
                .collect(foldLeft(
                        () -> new AntennaMap(antinodeLocator, -1, -1, Collections.emptyMap()),
                        (acc, lineWithIndex) -> {
                            var y = lineWithIndex.index();
                            var line = lineWithIndex.value();
                            var lineAntennasByType = line.chars()
                                    .boxed()
                                    .map(WithIndex.indexed())
                                    .filter(charWithIndex -> {
                                        var ch = charWithIndex.value();
                                        return (char) ch.intValue() != '.';
                                    })
                                    .map(charWithIndex -> {
                                        var x = charWithIndex.index();
                                        var ch = charWithIndex.value();
                                        return new Antenna(String.valueOf((char) ch.intValue()), new Position(x, y));
                                    })
                                    .collect(Collectors.groupingBy(Antenna::type));
                            // merge maps
                            var merged = Maps.merge(acc.antennasByType, lineAntennasByType, Maps::listConcat);
                            return new AntennaMap(antinodeLocator, line.length(), y + 1, merged);
                        })
                );
    }

    public int countUniquePositionsOfAntinodes() {
        var antinodes = antennasByType
                .values()
                .stream()
                .flatMap(antennaListForType -> {
                    // generate pairs of antennas
                    var combos = IntStream
                            .range(0, antennaListForType.size())
                            .boxed()
                            .flatMap(idx1 -> {
                                var a1 = antennaListForType.get(idx1);
                                return IntStream
                                        .range(idx1 + 1, antennaListForType.size())
                                        .boxed()
                                        .map(idx2 -> new Line(a1, antennaListForType.get(idx2)));
                            });

                    var antinodesForType = combos
                            .flatMap(line -> {
                                // for each combo, find possible antinodes, and filter out ones that are off the map
                                var antinodesInLine = antinodeLocator.getAntinodes(this, line)
                                        .stream()
                                        .filter(antinode ->
                                                antinode.pos().x() >= 0 && antinode.pos().x() < width &&
                                                        antinode.pos().y() >= 0 && antinode.pos().y() < height
                                        );
                                return antinodesInLine;
                            });

                    return antinodesForType;
                });

        // count number of unique positions
        var uniquePositions = antinodes.map(Antenna::pos).collect(Collectors.toSet());

        return uniquePositions.size();
    }
}
