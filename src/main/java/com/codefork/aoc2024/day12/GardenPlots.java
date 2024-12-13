package com.codefork.aoc2024.day12;

import com.codefork.aoc2024.util.WithIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

/**
 * In two places in the code, this solution makes use of a merging strategy to build groups of
 * adjacent things from an unordered collection. The logic is:
 * - check if an item is adjacent to any existing groups of adjacent items; if so,
 *   merge those groups together and add the item to the new group
 * - if item isn't adjacent, create a group for it
 * The key is that we can't know ahead of time which groups may need to be merged;
 * we just do it along the way.
 */
public class GardenPlots {

    // intermediate data structure
    private record PlotWithPlantType(String plantType, int x, int y) {

    }

    private final Map<String, List<Region>> regions;

    public GardenPlots(Stream<String> data) {
        var plots = data
                .map(WithIndex.indexed())
                .flatMap(lineIndexed -> {
                    var y = lineIndexed.index();
                    var line = lineIndexed.value();
                    return line.chars()
                            .boxed()
                            .map(WithIndex.indexed())
                            .map(charIndexed -> {
                                var x = charIndexed.index();
                                var ch = String.valueOf((char) charIndexed.value().intValue());
                                return new PlotWithPlantType(ch, x, y);
                            });
                })
                .toList();

        var byPlantType = plots
                .stream()
                .collect(Collectors.toMap(
                        (plot) -> plot.plantType,
                        Set::of,
                        (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toSet())
                ));

        this.regions = byPlantType.entrySet().stream().collect(
                foldLeft(
                        HashMap::new,
                        (acc, entry) -> {
                            var plantType = entry.getKey();
                            var plantTypePlots = entry.getValue();

                            for (var plantTypePlot : plantTypePlots) {
                                var plot = new Plot(plantTypePlot.x(), plantTypePlot.y());
                                if (!acc.containsKey(plantType)) {
                                    var newRegion = new Region(plantType, new ArrayList<>(List.of(plot)));
                                    acc.put(plantType, new ArrayList<>(List.of(newRegion)));
                                } else {
                                    var regions = acc.get(plantType);

                                    // figure out which regions are adjacent to plantTypePlot and which aren't
                                    var groupedByAdjacency = regions
                                            .stream()
                                            .collect(Collectors.groupingBy(
                                                    (region) -> region.anyAdjacent(plot)));

                                    var nonAdjacentRegions = groupedByAdjacency
                                            .getOrDefault(Boolean.FALSE, new ArrayList<>());

                                    // this handles no adjacent region (in which case one is created),
                                    // and one or more adjacent regions (in which case they're merged)
                                    var mergedAdjacentRegion = groupedByAdjacency
                                            .getOrDefault(Boolean.TRUE, new ArrayList<>())
                                            .stream()
                                            .collect(foldLeft(
                                                    () -> new Region(plantType, new ArrayList<>()),
                                                    (acc2, region) -> {
                                                        acc2.plots().addAll(region.plots());
                                                        return acc2;
                                                    }
                                            ));
                                    mergedAdjacentRegion.plots().add(plot);

                                    acc.put(plantType, Stream.concat(Stream.of(mergedAdjacentRegion),
                                            nonAdjacentRegions.stream()).toList());
                                }
                            }
                            return acc;
                        }
                )
        );
    }

    public Map<String, List<Region>> getRegions() {
        return regions;
    }

    public int getFencingCost(boolean bulkDiscount) {
        return this.regions.values().stream().reduce(0,
                (acc, regionList) ->
                        acc + regionList.stream().reduce(0, (acc2, region) ->
                                        acc2 + region.getArea() * (bulkDiscount ? region.getSides() : region.getPerimeter())
                                , Integer::sum)
                , Integer::sum);
    }

    public void print() {
        for (var entry : getRegions().entrySet()) {
            var plantType = entry.getKey();
            var regions = entry.getValue();
            System.out.println(plantType);
            for (var region : regions) {
                for (var plot : region.plots()) {
                    System.out.print(plot);
                }
                System.out.println();
                System.out.println(region.getArea() + " " + region.getSides());
            }
        }
    }

}
