package com.codefork.aoc2024.day12;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Region(String plantType, List<Plot> plots) {

    public boolean anyAdjacent(Plot other) {
        return this.plots.stream().anyMatch(p -> p.isAdjacent(other));
    }

    public int getArea() {
        return plots.size();
    }

    public int getPerimeter() {
        return plots.stream()
                .reduce(0L, (acc, plot) -> {
                    var countAdjacent = plots.stream()
                            .filter(possibleAdjacent ->
                                    possibleAdjacent.isAdjacent(plot)
                            )
                            .count();
                    return acc + (4 - countAdjacent);
                }, Long::sum).intValue();
    }

    /**
     * This finds the side group(s) that the passed-in side is adjacent to,
     * and merges them, returning a new list of lists.
     *
     * @param side the side to add to one of the lists of adjacent sides
     * @param groups list of lists of adjacent sides
     */
    public List<List<Side>> groupAdjacent(Side side, List<List<Side>> groups) {
        // the types are tricky here. would be nice to create a wrapper record/class
        // for List<Side> named AdjacentSides for clarity, the same way we have a Region
        // class, but this works fine
        var groupedByAdjacency = groups
                .stream()
                .collect(Collectors.groupingBy(
                        (group) -> group.stream()
                                .anyMatch(sideItem -> sideItem.isAdjacent(side))
                ));

        // this is a list of lists
        var nonAdjacentSides = groupedByAdjacency
                .getOrDefault(Boolean.FALSE, new ArrayList<>());

        // this is also a list of lists
        var mergedAdjacentSides = new ArrayList<>(groupedByAdjacency
                .getOrDefault(Boolean.TRUE, new ArrayList<>())
                .stream()
                .flatMap(Collection::stream)
                .toList());
        mergedAdjacentSides.add(side);

        return Stream.concat(
                        nonAdjacentSides.stream(),
                        Stream.of(mergedAdjacentSides))
                .toList();
    }

    /**
     * @return number of contiguous sections of sides of the plot (this is for part 2)
     */
    public int getSides() {
        // create a list of all the sides of this region
        var sides = plots.stream()
                .flatMap((plot) -> {
                    var edges = new HashSet<>(Arrays.stream(Edge.values()).toList());
                    // remove the edges shared by plots
                    var edgesToRemove = plots.stream()
                            .filter(plot::isAdjacent)
                            .map(plot::getAdjacentEdge)
                            .collect(Collectors.toSet());
                    edges.removeAll(edgesToRemove);
                    return edges.stream().map(edge -> new Side(plot, edge));
                })
                .toList();

        // count the contiguous sides
        var totalContigsCount = Arrays.stream(Edge.values())
                .collect(foldLeft(
                        () -> 0,
                        (countContigs, edge) -> {
                            // this combining fn finds the number of contiguous sides for a given edge,
                            // and adds it to countContigs

                            //System.out.println("doing edge=" + edge);

                            var sidesForEdge = sides.stream()
                                    .filter(side -> side.edge().equals(edge))
                                    .toList();

                            var groupedAdjacent = sidesForEdge.stream()
                                    .collect(foldLeft(
                                            () -> (List<List<Side>>) new ArrayList<List<Side>>(),
                                            (acc, side) -> groupAdjacent(side, acc)
                                            )
                                    );

                            //System.out.println("grouped=" + groupedAdjacent);

                            return countContigs + groupedAdjacent.size();
                        }
                        )
                );

        //System.out.println("total contiguous sides = " + totalContigsCount);

        return totalContigsCount;
    }
}
