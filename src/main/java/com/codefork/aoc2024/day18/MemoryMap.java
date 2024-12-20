package com.codefork.aoc2024.day18;

import com.codefork.aoc2024.util.Maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record MemoryMap(int height, int width, Set<Position> corrupted, Set<Position> empty) {

    public static MemoryMap create(int height, int width, Stream<String> data) {
        var corrupted = data
                .map(line -> {
                    var parts = Arrays.stream(line.split(","))
                            .map(Integer::valueOf)
                            .toList();
                    return new Position(parts.get(0), parts.get(1));
                })
                .collect(Collectors.toSet());

        var empty = IntStream.range(0, height).boxed()
                .flatMap(y ->
                        IntStream.range(0, width).boxed()
                                .map(x -> new Position(x, y))
                                .filter(pos -> !corrupted.contains(pos))
                )
                .collect(Collectors.toSet());

        return new MemoryMap(height, width, corrupted, empty);
    }

    public Set<Position> getNeighbors(Position pos) {
        return Stream.of(pos.decX(), pos.incX(), pos.decY(), pos.incY())
                .filter(p ->
                        !corrupted.contains(p)
                                && p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height)
                .collect(Collectors.toSet());
    }

    // Dijkstra's algorithm again
    public Optional<Integer> countStepsOnShortestPath() {
        record Edge(Position from, Position to) {

        }
        record PosWithDist(Position p, int d) {

        }
        var edges = empty().stream()
                .flatMap(pos ->
                        getNeighbors(pos).stream().map(n -> new Edge(pos, n)))
                .collect(Collectors.toMap(
                        Edge::from,
                        e -> List.of(e.to()),
                        Maps::listConcat));

        var source = new Position(0, 0);
        var target = new Position(height - 1, width - 1);

        var INFINITY = Integer.MAX_VALUE;
        Map<Position, Integer> dist = new HashMap<>();
        Map<Position, Position> prev = new HashMap<>();
        for (var v : empty()) {
            dist.put(v, INFINITY);
        }
        dist.put(source, 0);

        var q = new HashSet<>(empty());

        while (!q.isEmpty()) {
            var u = q.stream()
                    .map(pos -> new PosWithDist(pos, dist.get(pos)))
                    .min(Comparator.comparingInt(PosWithDist::d))
                    .orElseThrow()
                    .p();
            q.remove(u);

            var neighbors = edges
                    .getOrDefault(u, Collections.emptyList())
                    .stream()
                    .filter(q::contains)
                    .toList();

            for (var neighbor : neighbors) {
                var alt = dist.get(u) + 1;
                if (alt < dist.get(neighbor)) {
                    dist.put(neighbor, alt);
                    prev.put(neighbor, u);
                }
            }
        }

        // find shortest path
        var path = new LinkedList<Position>();
        var u = target;
        if (prev.containsKey(u) || u.equals(source)) {
            while (u != null) {
                path.addFirst(u);
                u = prev.get(u);
            }
        }

        // did we find a complete path back to the source position?
        if (path.getFirst().equals(source)) {
            // number of steps = number of nodes - 1
            return Optional.of(path.size() - 1);
        }
        return Optional.empty();
    }
}
