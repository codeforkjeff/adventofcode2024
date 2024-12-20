package com.codefork.aoc2024.day19;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Onsen(Set<String> towels, Set<String> designs) {

    public static Onsen create(Stream<String> data) {
        return data.collect(foldLeft(
                () -> new Onsen(new HashSet<>(), new HashSet<>()),
                (acc, line) -> {
                    if (line.contains(",")) {
                        var parts = Arrays.stream(line.split(","))
                                .map(String::strip)
                                .collect(Collectors.toSet());
                        return new Onsen(parts, acc.designs());
                    } else if (!line.isEmpty()) {
                        acc.designs().add(line);
                        return acc;
                    } else {
                        return acc;
                    }
                })
        );
    }

    /**
     * this part 1 solution could be rewritten to use part 2's countTowelCombinations()
     * to unify the code, but it's nice to see how I approached it differently for the
     * two parts
     */
    public boolean isDesignPossible(String design) {
        if (design.isEmpty()) {
            return true;
        }
        var towelMatches = towels.stream()
                .filter(design::startsWith)
                .toList();
        return towelMatches.stream().anyMatch(towel ->
                isDesignPossible(design.substring(towel.length()))
        );
    }

    /**
     * returns the number of designs that are actually viable (that
     * can be achieved with some combination of towels)
     */
    public int countViableDesigns() {
        return (int) designs().stream()
                .filter(this::isDesignPossible)
                .count();
    }

    /**
     * count the number of towel combinations that exist for a design
     */
    public long countTowelCombinations(String design, Map<String, Long> cache) {
        if(cache.containsKey(design)) {
            return cache.get(design);
        }
        if (design.isEmpty()) {
            return 1;
        }
        var towelMatches = towels.stream()
                .filter(design::startsWith)
                .toList();
        return towelMatches.stream().reduce(0L,
                (acc, towel) -> {
                    var designRemainder = design.substring(towel.length());
                    // a lot of these calls will be repeats for the same designRemainder,
                    // so cache and re-use that count. without caching, this algorithm won't
                    // finish, there's too many combinations to actually compute
                    var count = countTowelCombinations(designRemainder, cache);
                    cache.put(designRemainder, count);
                    return acc + count;
                },
                Long::sum);
    }

    public long countTowelCombinationsForAllDesigns() {
        return designs().stream()
                .reduce(
                        0L,
                        (acc, design) ->
                                acc + countTowelCombinations(design, new HashMap<>()),
                        Long::sum);
    }

}
