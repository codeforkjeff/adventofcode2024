package com.codefork.aoc2024.day21;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record ShortestPaths(Button source, Map<Button, Integer> dist, Map<Button, Set<Button>> prev) {

    // Dijkstra, yet again.
    // this is probably overkill since there's not weighted edges in the graph
    // but I've already implemented it twice so far for AoC, so it's the easiest way
    // for me to create shortest paths at this point
    public static ShortestPaths create(List<Button> buttons, Map<Button, List<Button>> graph, Button source) {
        record ButtonWithDist(Button button, int dist) {

        }
        var dist = new HashMap<Button, Integer>();
        var prev = new HashMap<Button, Set<Button>>();
        var q = new HashSet<Button>();

        for (var button : buttons) {
            dist.put(button, Integer.MAX_VALUE);
            q.add(button);
        }
        dist.put(source, 0);

        while (!q.isEmpty()) {
            var u = q.stream()
                    .map(button -> new ButtonWithDist(button, dist.get(button)))
                    .min(Comparator.comparingInt(ButtonWithDist::dist))
                    .orElseThrow()
                    .button();
            q.remove(u);

            var neighborsInQ = graph.get(u).stream()
                    .filter(q::contains)
                    .toList();

            for (var v : neighborsInQ) {
                var alt = dist.get(u) + 1;
                if (alt <= dist.get(v)) {
                    dist.put(v, alt);
                    if (!prev.containsKey(v)) {
                        prev.put(v, new HashSet<>());
                    }
                    prev.get(v).add(u);
                }
            }
        }
        return new ShortestPaths(source, dist, prev);
    }

    /**
     * get all the possible paths for navigating from source to target
     */
    public List<List<Button>> getPaths(Button target) {
        var paths = new ArrayList<List<Button>>();
        paths.add(new LinkedList<>(List.of(target)));
        var finalPaths = new ArrayList<List<Button>>();
        while (!paths.isEmpty()) {
            var newPaths = new ArrayList<List<Button>>();
            for (var path : paths) {
                var u = path.getFirst();
                if (prev.containsKey(u)) {
                    var allPrev = prev.get(u);
                    for (var prevItem : allPrev) {
                        var newPath = new LinkedList<>(path);
                        newPath.addFirst(prevItem);
                        newPaths.add(newPath);
                    }

                } else if (u.equals(source)) {
                    finalPaths.add(path);
                }
            }
            paths = newPaths;
        }
        //System.out.println(source + "->" + target + ", returning " + finalPaths.size() + " finalPaths=" + finalPaths);
        return finalPaths;
    }

    /**
     * turns a path (list of Buttons from source to target) into a list of the directional
     * symbol sequences needed to get from source to target. This is probably not best represented
     * as a String, but that's how I implemented it early on and I didn't want to refactor it.
     */
    public List<String> getPathsAsStrings(Button target) {
        var paths = getPaths(target);
        return paths.stream()
                .map(path ->
                        IntStream.range(1, path.size()).boxed()
                                .collect(foldLeft(
                                        StringBuilder::new,
                                        (acc, i) -> {
                                            var button = path.get(i);
                                            var prevButton = path.get(i - 1);
                                            var action = prevButton.getAction(button);
                                            acc.append(action);
                                            return acc;
                                        })
                                )
                                .append("A")
                                .toString()
                )
                .toList();
    }
}
