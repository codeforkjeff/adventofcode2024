package com.codefork.aoc2024.day25;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;
import com.codefork.aoc2024.util.Grid;
import com.codefork.aoc2024.util.WithIndex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part01 extends Problem {

    public record Position(int x, int y) {

    }

    public record Schematic(Set<Position> positions) {

        public List<Integer> getHeights() {
            return IntStream.range(0, 5)
                    .boxed()
                    .map(x -> (int) positions().stream().filter(p -> p.x() == x).count())
                    .toList();
        }

    }

    record LockAndKey(Schematic lock, Schematic key) {

    }

    public String solve(Stream<String> data) {
        var schematics = Grid.parse(data,
                () -> new ArrayList<Schematic>(),
                (acc, x, y, ch) -> {
                    y = y % 8;
                    if(x == 0 && y == 0) {
                        acc.add(new Schematic(new HashSet<>()));
                    }
                    var schematic = acc.getLast();
                    if("#".equals(ch)) {
                        schematic.positions().add(new Position(x, y));
                    }
                    return acc;
                });

        var keys = schematics.stream()
                .filter(s ->
                        s.positions().stream().anyMatch(p -> p.y() == 6)
                )
                .toList();

        var locks = schematics.stream()
                .filter(s ->
                        s.positions().stream().anyMatch(p -> p.y() == 0)
                )
                .toList();

        // note that we calculate height differently than in the problem description

        // all valid lock and key combos
        var combos = locks.stream()
                .flatMap(lock -> {
                    var lockHeights = lock.getHeights();
                    return keys.stream()
                            .filter(key -> {
                                var keyHeights = key.getHeights();
                                return keyHeights.stream()
                                        .map(WithIndex.indexed())
                                        .noneMatch(heightWithIdx -> {
                                            var i = heightWithIdx.index();
                                            var height = heightWithIdx.value();
                                            return lockHeights.get(i) + height > 7;
                                        });
                            })
                            .map(key -> new LockAndKey(lock, key));
                })
                .toList();

        return String.valueOf(combos.size());
    }

    @Override
    public String solve() {
        Assert.assertEquals("3", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
