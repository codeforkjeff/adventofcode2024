package com.codefork.aoc2024.day16;

import com.codefork.aoc2024.util.WithIndex;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

/**
 * awkwardly named, but a Reindeer sits at a position facing in a specific direction.
 */
public record Reindeer(Position pos, Direction dir) {

    // manhattan distance
    public int dist(Reindeer other) {
        return Math.abs(other.pos().x() - pos().x()) + Math.abs(other.pos().y() - pos().y());
    }

    public int score(Reindeer other) {
        return dist(other) + (!dir().equals(other.dir()) ? 1000 : 0);
    }

    public Set<Position> tiles(Reindeer other) {
        var xDiff = Math.abs(pos.x() - other.pos().x());
        var startX = Math.min(pos.x(), other.pos().x());
        var yDiff = Math.abs(pos.y() - other.pos().y());
        var startY = Math.min(pos.y(), other.pos().y());

        return IntStream.range(startX, startX + xDiff + 1).boxed().flatMap(x ->
                IntStream.range(startY, startY + yDiff + 1).boxed().map(y ->
                        new Position(x, y)
                )
        ).collect(Collectors.toSet());
    }

    public static int getPathScore(List<Reindeer> path) {
        return path.stream()
                .map(WithIndex.indexed())
                .collect(foldLeft(
                        () -> 0,
                        (acc, reindeerWithIndex) -> {
                            var i = reindeerWithIndex.index();
                            var r = reindeerWithIndex.value();
                            if (i == 0) {
                                return 0;
                            } else {
                                var prevR = path.get(i - 1);
                                return acc + r.score(prevR);
                            }
                        })
                );
    }

}
