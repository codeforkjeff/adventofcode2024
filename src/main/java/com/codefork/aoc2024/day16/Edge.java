package com.codefork.aoc2024.day16;

import java.util.List;
import java.util.stream.IntStream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Edge(Reindeer from, Reindeer to) {

    public static int calculateScore(List<Edge> path) {
        return IntStream.range(0, path.size()).boxed().collect(foldLeft(
                () -> 0,
                (acc, i) -> acc + path.get(i).score()
        ));
    }

    @Override
    public String toString() {
        return String.format("%s,%s %s -> %s,%s %s (score: %s)",
                from.pos().x(),
                from.pos().y(),
                from.dir(),
                to.pos().x(),
                to.pos().y(),
                to.dir(),
                score());
    }

    public int score() {
        return from.score(to);
    }
}
