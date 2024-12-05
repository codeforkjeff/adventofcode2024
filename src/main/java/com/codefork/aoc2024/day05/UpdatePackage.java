package com.codefork.aoc2024.day05;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An update package is the collection of rules and manual updates that are found
 * in the input file
 */
public record UpdatePackage(List<OrderedPages> rules, List<Update> updates) {

    /**
     * builds an UpdatePackage from a stream of input data
     */
    public static UpdatePackage create(Stream<String> data) {

        final var grouped = data.collect(Collectors.groupingBy(line -> {
            if(line.contains("|")) {
                return "rules";
            } else if (line.contains(",")) {
                return "updates";
            }
            return "unknown";
        }));

        final var rules = grouped.get("rules").stream().map(line -> {
            final var parts = line.split("\\|");
            return new OrderedPages(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }).toList();

        final var updates = grouped.get("updates").stream().map(line ->
            new Update(Arrays.stream(line.split(",")).map(Integer::parseInt).toList())
        ).toList();

        return new UpdatePackage(rules, updates);
    }

    /**
     * reverses the rules to produce a set of violation possibilities
     */
    public List<OrderedPages> getViolationPossibilities() {
        return rules.stream().map(OrderedPages::reverse).toList();
    }

    /**
     * @return true if the update is correctly ordered, according to this package's rules
     */
    private boolean isCorrectlyOrdered(Update update, List<OrderedPages> violationPossibilities) {
        // generate every combination of OrderedPages from the update's pages
        final var pairs = IntStream
                .range(0, update.pages().size())
                .boxed()
                .flatMap(idx1 -> IntStream
                        .range(idx1 + 1, update.pages().size())
                        .boxed()
                        .map(idx2 ->
                                new OrderedPages(update.pages().get(idx1), update.pages().get(idx2))))
                .collect(Collectors.toSet());
        return violationPossibilities.stream().noneMatch(pairs::contains);
    }

    public List<Update> getCorrectlyOrderedUpdates() {
        final var violationPossibilities = getViolationPossibilities();
        return updates.stream().filter(update -> isCorrectlyOrdered(update, violationPossibilities)).toList();
    }

    public List<Update> getIncorrectlyOrderedUpdates() {
        final var violationPossibilities = getViolationPossibilities();
        return updates.stream().filter(update -> !isCorrectlyOrdered(update, violationPossibilities)).toList();
    }

    /**
     * re-orders the pages in update, according to this package's rules.
     * this works by taking all the relevant rules that pertain to the pages in the update,
     * and sorting them by descending frequency of the first page in the rule.
     * this results in a list that satisfies all the rules, since the pages with the most rules about
     * what must come after them, will be towards the front.
     */
    public Update reorderUpdate(Update update) {
        final var pageSet = new HashSet<>(update.pages());

        // get all the rules that pertain to the pages in the update
        final var relevantRules = rules
                .stream()
                .filter(rule ->
                        pageSet.contains(rule.p1()) && pageSet.contains(rule.p2())
                ).toList();

        // calculate frequency of first page in each rule
        final var freqMap = relevantRules.stream().collect(Collectors.toMap(
                OrderedPages::p1,
                (item) -> 1,
                Integer::sum));

        // reverse sort by frequency: this will give us the sorted pages
        final var sortedPages = freqMap
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(Map.Entry::getKey);

        // lastly, append any pages that weren't in any rules: order for these doesn't matter
        final var remainders = pageSet.stream().filter(page ->
                relevantRules.stream().noneMatch(rule -> page.equals(rule.p1()))
        );

        final var newPages = Stream.concat(sortedPages, remainders).toList();
        //System.out.println(newPages);

        return new Update(newPages);
    }

}
