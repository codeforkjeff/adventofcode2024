package com.codefork.aoc2024.day23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

/**
 * A set of computers that include every other member in its list of neighbors.
 */
public record Network(Set<String> computers, String last) {

    /**
     * adds a computer to this network if its neighbors includes every other computer already in set,
     * and returns the new set; if not in the network, just return the existing set
     */
    public Network addIfBelongs(String computerToAdd, Map<String, List<String>> edges) {
        var newComputers = new HashSet<>(computers);
        newComputers.add(computerToAdd);
        var neighbors = new HashSet<>(edges.get(computerToAdd));
        if (neighbors.containsAll(computers())) {
            return new Network(newComputers, computerToAdd);
        }
        return this;
    }

    public boolean contains(String computer) {
        return computers().contains(computer);
    }

    public int size() {
        return computers().size();
    }

    public Network withoutLast() {
        return new Network(computers, "DONE");
    }

    public static Map<String, List<String>> parseEdges(Stream<String> data) {
        return data.collect(foldLeft(
                HashMap::new,
                (acc, line) -> {
                    var parts = line.split("-");
                    if (!acc.containsKey(parts[0])) {
                        acc.put(parts[0], new ArrayList<>());
                    }
                    acc.get(parts[0]).add(parts[1]);
                    if (!acc.containsKey(parts[1])) {
                        acc.put(parts[1], new ArrayList<>());
                    }
                    acc.get(parts[1]).add(parts[0]);
                    return acc;
                })
        );
    }

    @Override
    public String toString() {
        return computers().stream().sorted().collect(Collectors.joining(","));
    }
}
