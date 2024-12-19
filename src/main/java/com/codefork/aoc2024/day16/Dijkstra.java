package com.codefork.aoc2024.day16;

import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Dijkstra(Maze maze, Map<Reindeer, Integer> dist, Map<Reindeer, Set<Reindeer>> prev) {

    private static int INFINITY = Integer.MAX_VALUE;

    public static Dijkstra create(Maze maze, Set<Edge> edges) {
        // intermediate record
        record ReindeerDist(Reindeer r, int d) {

        }

        // all the reindeer found in "from" part of edges
        var allReindeer = edges.stream()
                .map(Edge::from)
                .collect(Collectors.toSet());

        // distances of all reindeer from start
        Map<Reindeer, Integer> dist = new HashMap<>();
        for (Reindeer r : allReindeer) {
            dist.put(r, INFINITY);
        }
        dist.put(new Reindeer(maze.start(), Direction.East), 0);

        // The prev array contains pointers to previous-hop nodes on
        // the shortest path from source to the given vertex
        Map<Reindeer, Set<Reindeer>> prev = new HashMap<>();

        List<Reindeer> q = new ArrayList<>(allReindeer);
        while (!q.isEmpty()) {
            // find a position that has minimum value in dist
            var u = q.stream()
                    .map(r -> new ReindeerDist(r, dist.get(r)))
                    .min(Comparator.comparingInt(ReindeerDist::d))
                    .orElseThrow()
                    .r();
            q.remove(u);

            //System.out.println("doing " + u);

            var edgesFromU = edges.stream()
                    .filter(e -> e.from().equals(u))
                    .toList();

            // each neighbor of u that's still in Q
            var neighbors = edgesFromU.stream()
                    .map(Edge::to)
                    .filter(q::contains)
                    .toList();

            //System.out.println("neighbors for " + u + " = " + neighbors);

            for (var v : neighbors) {
                var joiningEdges = edgesFromU.stream()
                        .filter(e -> e.to().equals(v))
                        .toList();
                Assert.assertEquals(1, joiningEdges.size());
                var joiningEdge = joiningEdges.getFirst();

                // sanity check
//                if(dist.get(u).equals(INFINITY)) {
//                    throw new RuntimeException("dist[u] should never be infinity here, but it is");
//                }

                var alt = dist.get(u) + joiningEdge.score();
                // modification of original algorithm: instead of checking if alt < dist[v],
                // we check if it's <= to build all shortest paths, not just one. accordingly,
                // prev is a Map<Reindeer, Set<Reindeer>> instead of a Map<Reindeer, Reindeer>
                if (alt <= dist.get(v)) {
                    dist.put(v, alt);
                    //prev.put(v, u);
                    if(!prev.containsKey(v)) {
                        prev.put(v, new HashSet<>());
                    }
                    prev.get(v).add(u);
                }
            }
        }
        return new Dijkstra(maze, dist, prev);
    }

    /**
     * This is slightly complicated: technically, there is more than one possible "end" point
     * to the maze, depending on the direction of the approach. we try all possible approaches,
     * find the best score out of all of them, and return all the paths matching that best score.
     */
    public List<List<Reindeer>> findBestPaths() {
        // there can be different ways to reach the end (approaching from diff directions)
        var reindeerEnds = maze
                .getPossibleApproaches(maze.end())
                .stream()
                .map(d -> new Reindeer(maze.end(), d))
                .toList();

        record PathAndScore(List<Reindeer> path, int score) {

        }

        // find all the paths for different Reindeer ends
        var pathsAndScores = reindeerEnds.stream()
                .flatMap(u -> {
                    // construct a path by working backwards from maze end
                    List<Reindeer> path = new LinkedList<>();
                    if (prev.containsKey(u) || u.pos().equals(maze.start())) {
                        while (u != null) {
                            path.addFirst(u);
                            if(prev.containsKey(u)) {
                                // arbitarily pick the first item, doesn't matter
                                // since we just want any shortest path
                                u = prev.get(u).stream().findFirst().orElseThrow();
                            } else {
                                u = null;
                            }
                        }
                    }
                    if (!path.isEmpty()) {
                        return Stream.of(new PathAndScore(path, Reindeer.getPathScore(path)));
                    }
                    return Stream.empty();
                }).toList();

        // find the lowest score, and just return the path(s) with that score

        var minScore = pathsAndScores.stream()
                .min(Comparator.comparingInt(PathAndScore::score))
                .orElseThrow()
                .score();

        return pathsAndScores.stream()
                .filter(pas -> pas.score() == minScore)
                .map(PathAndScore::path)
                .toList();
    }

    /**
     * count all the tiles on every shortest path to end
     */
    public int countTilesOnEveryShortestPathToEnd(Reindeer reindeerEnd) {
        record Pair(Reindeer start, Reindeer end) {

        }

        // walk back from end to the start of the maze,
        // grouping reindeer into pairs so we can calculate
        // the tile positions we've walked

        var visited = new HashSet<Reindeer>();

        var tiles = new HashSet<Position>();
        var toProcess = new LinkedList<Pair>();
        toProcess.add(new Pair(reindeerEnd, reindeerEnd));
        while (!toProcess.isEmpty()) {
            var pair = toProcess.removeFirst();
            var prevReindeer = pair.end();
            tiles.addAll(pair.start().tiles(prevReindeer));
            // if we've seen this Reindeer already, we don't need to walk it further
            if(visited.contains(prevReindeer)) {
                continue;
            }
            if(prev.containsKey(prevReindeer)) {
                // create new Pairs for ALL the previous reindeer, to the queue.
                // this ensures this walk reaches the tiles on any shortest path.
                for (var prevItem : prev.get(prevReindeer)) {
                    toProcess.add(new Pair(prevReindeer, prevItem));
                }
            }
            visited.add(prevReindeer);
        }
        return tiles.size();
    }

}
