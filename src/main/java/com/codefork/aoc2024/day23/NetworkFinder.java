package com.codefork.aoc2024.day23;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Solver for part 2. This uses an entirely different strategy from part 1.
 * <p></p>
 * Start with an initial set of networks, each consisting of a t-computer and one
 * of its known neighbors. After this seeding, we have a set of known good networks: we can
 * visit "outward" from these in the graph, adding computers to the known networks as appropriate,
 * and merging networks as we go.
 * <p></p>
 * Because the Network record was originally created for part 1, its holds the "last" computer added
 * to the network as part of that data structure. We don't use that field here, so we just use a placeholder.
 */
public record NetworkFinder(Map<String, List<String>> edges, List<Network> networks, Set<String> visited, Set<String> toVisit) {

    static String UNUSED = "UNUSED";

    /**
     * Initialize with a list of networks, each consisting of a t-computer
     * and one its known neighbors.
     */
    public static NetworkFinder seed(Map<String, List<String>> edges) {
        var initial = edges.keySet().stream()
                .filter(s -> s.startsWith("t"))
                .collect(Collectors.toSet());

        var networks = initial.stream()
                .flatMap(s -> edges.get(s).stream().map(target ->
                    new Network(Set.of(s, target), UNUSED)
                ))
                .toList();

        var toVisit = initial.stream()
                .flatMap(s -> edges.get(s).stream())
                .collect(Collectors.toSet());

        return new NetworkFinder(edges, networks, initial, toVisit);
    }

    public List<Network> findAllNetworks() {
        var walker = this;
        while(!walker.toVisit().isEmpty()) {
            var computer = walker.toVisit().iterator().next();

            if(walker.visited().contains(computer)) {
                var newVisited = new HashSet<>(walker.visited());
                newVisited.add(computer);

                var newToVisit = new HashSet<>(walker.toVisit());
                newToVisit.remove(computer);

                walker = new NetworkFinder(walker.edges(), walker.networks(), newVisited, newToVisit);

                continue;
            }

            //System.out.println(walker.networks());

            // add the computer to all possible networks
            var newNetworks = walker.networks().stream().map(network -> {
                var newNetwork = network.addIfBelongs(computer, edges);
                if(!newNetwork.equals(network)) {
                    return newNetwork;
                }
                return network;
            }).toList();

            // merge newNetworks
            var grouped = newNetworks.stream().collect(Collectors.toMap(
                    Network::computers,
                    (network) -> 1,
                    (v1, v2) -> 1
            ));
            newNetworks = grouped.keySet().stream().map(networkSet -> {
                return new Network(networkSet, UNUSED);
            }).toList();

            var newVisited = new HashSet<>(walker.visited());
            newVisited.add(computer);

            var newToVisit = new HashSet<>(walker.toVisit());
            newToVisit.addAll(edges.get(computer));
            newToVisit.remove(computer);

            walker = new NetworkFinder(edges(), newNetworks, newVisited, newToVisit);
        }
        return walker.networks();
    }

    public Network findLargestNetwork() {
        return findAllNetworks().stream()
                .max(Comparator.comparingInt(Network::size))
                .orElseThrow();
    }

}
