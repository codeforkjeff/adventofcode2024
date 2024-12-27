package com.codefork.aoc2024.day23;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Solver for part 1. Grow an initial set of single t-computer networks,
 * following its neighbors, in a one-to-many iterative pattern, until we find all the networks
 * of a given size.
 */
public class NetworkGrower {

    private final Map<String, List<String>> edges;
    private final int size;

    private List<Network> networks;

    public NetworkGrower(Map<String, List<String>> edges, int size) {
        this.edges = edges;
        this.size = size;
        this.networks = edges.keySet().stream()
                .filter(s -> s.startsWith("t"))
                .map(s -> new Network(Set.of(s), s))
                .toList();
    }

    /**
     * returns true if networks contains any networks that are less than size
     */
    public boolean needsGrowing() {
        return networks.stream().anyMatch(set -> set.size() < size);
    }

    public List<Network> grow() {
        while (needsGrowing()) {
            networks = networks.stream().flatMap(network -> {
                if (network.size() == size) {
                    return Stream.of(network);
                }
                var neighbors = edges.get(network.last());
                return neighbors.stream()
                        .filter(neighbor -> !network.contains(neighbor))
                        .flatMap(neighbor -> {
                            var newNetwork = network.addIfBelongs(neighbor, edges);
                            if (!newNetwork.equals(network)) {
                                return Stream.of(newNetwork);
                            }
                            // couldn't be added to the network, so filter out
                            return Stream.empty();
                        });
            }).toList();
            networks = networks.stream()
                    .map(network -> (network.size() == size) ? network.withoutLast() : network)
                    .collect(Collectors.toSet())
                    .stream()
                    .toList();
//            System.out.println("networks=");
//            for(var set : newSet) {
//                System.out.println(set);
//            }
        }
        return networks;
    }

}
