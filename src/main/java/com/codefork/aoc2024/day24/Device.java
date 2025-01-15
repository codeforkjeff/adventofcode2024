package com.codefork.aoc2024.day24;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Device(Map<String, Wire> namesToWires) {

    /**
     * Evaluates wires in a device. This has a cache, which makes evaluating
     * all the z wires in a device 74% faster!
     */
    public static class Evaluator {
        private final Map<String, Integer> values = new HashMap<>();
        private final Device device;

        public Evaluator(Device device) {
            this.device = device;
        }

        public int get(String wireName) {
            if (!values.containsKey(wireName)) {
                var value = device.namesToWires().get(wireName).value(this);
                values.put(wireName, value);
                return value;
            }
            return values.get(wireName);
        }
    }

    public sealed interface Wire permits And, Or, Xor, NamedValue, LiteralValue {
        int value(Evaluator device);
    }

    public record LiteralValue(int value) implements Wire {

        @Override
        public int value(Evaluator evaluator) {
            return value;
        }
    }

    public record NamedValue(String name) implements Wire {

        @Override
        public int value(Evaluator evaluator) {
            return evaluator.get(name);
        }
    }

    public record And(NamedValue op1, NamedValue op2) implements Wire {

        @Override
        public int value(Evaluator evaluator) {
            return op1.value(evaluator) & op2.value(evaluator);
        }
    }

    public record Or(NamedValue op1, NamedValue op2) implements Wire {

        @Override
        public int value(Evaluator evaluator) {
            return op1.value(evaluator) | op2.value(evaluator);
        }
    }

    public record Xor(NamedValue op1, NamedValue op2) implements Wire {

        @Override
        public int value(Evaluator evaluator) {
            return op1.value(evaluator) ^ op2.value(evaluator);
        }
    }

    public record Pair(String wire1, String wire2) {

    }

    private static final Pattern literalValuePat = Pattern.compile("(\\w{3}): (\\d)");
    private static final Pattern gatePat = Pattern.compile("(\\w{3}) (AND|OR|XOR) (\\w{3}) -> (\\w{3})");

    public static Device parse(Stream<String> data) {
        var namesToWires = data
                .filter(line -> !line.isEmpty())
                .collect(foldLeft(
                        () -> new HashMap<String, Wire>(),
                        (acc, line) -> {
                            var literalValueMatcher = literalValuePat.matcher(line);
                            if (literalValueMatcher.find()) {
                                var name = literalValueMatcher.group(1);
                                var value = Integer.parseInt(literalValueMatcher.group(2));
                                acc.put(name, new LiteralValue(value));
                                return acc;
                            }
                            var gateMatcher = gatePat.matcher(line);
                            if (gateMatcher.find()) {
                                var op1 = new NamedValue(gateMatcher.group(1));
                                var op = gateMatcher.group(2);
                                var op2 = new NamedValue(gateMatcher.group(3));
                                var name = gateMatcher.group(4);
                                var wire = switch (op) {
                                    case "AND" -> new And(op1, op2);
                                    case "OR" -> new Or(op1, op2);
                                    case "XOR" -> new Xor(op1, op2);
                                    default -> throw new RuntimeException("unrecognized gate: " + op);
                                };
                                acc.put(name, wire);
                                return acc;
                            }
                            throw new RuntimeException("Couldn't parse line: " + line);
                        })
                );
        return new Device(namesToWires);
    }

    public Device swap(String name1, String name2) {
        var newNamesToWires = new HashMap<>(namesToWires());
        var wire1 = newNamesToWires.get(name1);
        var wire2 = newNamesToWires.get(name2);
        newNamesToWires.put(name1, wire2);
        newNamesToWires.put(name2, wire1);
        return new Device(newNamesToWires);
    }

    /**
     * get only the gate dependencies (not the x and y wire values)
     */
    public Set<String> getGateDependencies(String name) {
        var toVisit = new ArrayList<String>();
        var visited = new HashSet<String>();
        toVisit.add(name);
        var dependencies = new HashSet<String>();
        while (!toVisit.isEmpty()) {
            var wireName = toVisit.getFirst();
            if (visited.contains(wireName)) {
                toVisit.removeFirst();
                continue;
            }
            if (wireName.startsWith("x") || wireName.startsWith("y")) {
                toVisit.removeFirst();
                visited.add(wireName);
                continue;
            }
            dependencies.add(wireName);
            var wire = namesToWires().get(wireName);
            switch (wire) {
                case Or w -> {
                    toVisit.add(w.op1.name());
                    toVisit.add(w.op2.name());
                }
                case And w -> {
                    toVisit.add(w.op1.name());
                    toVisit.add(w.op2.name());
                }
                case Xor w -> {
                    toVisit.add(w.op1.name());
                    toVisit.add(w.op2.name());
                }
                case NamedValue v -> {
                    // do nothing
                }
                case LiteralValue v -> {
                    // do nothing
                }
            }
            toVisit.removeFirst();
            visited.add(wireName);
        }
        return dependencies;
    }

    /**
     * return a map of dependencies for all the wires, exception x and y
     */
    public Map<String, Set<String>> getDependencies() {
        return namesToWires().keySet().stream()
                .filter(name -> !(name.startsWith("x") || name.startsWith("y")))
                .collect(Collectors.toMap(
                        name -> name,
                        this::getGateDependencies
                ));
    }

    /**
     * returns two-digit strings ("00", "01", etc.) for the wires with
     * the specific character prefix
     */
    public List<String> getIndices(char prefix) {
        return namesToWires().keySet().stream()
                .filter(name -> name.charAt(0) == prefix)
                .map(name -> name.substring(1, 3))
                .sorted()
                .toList();
    }

    /**
     * This performs the addition of x and y bits using Java ints
     * and returns a list of the z wires that don't match the expected result.
     * Returning an empty list means this device is performing the addition
     * correctly.
     *
     * @param debug show debug messages
     * @return
     */
    public List<String> validateAddition(boolean debug) {
        var eval = new Evaluator(this);
        long x = 0L;
        long y = 0L;
        for (var pos : getIndices('x').reversed()) {
            var xWire = eval.get("x" + pos);
            x = (x << 1) + xWire;
            var yWire = eval.get("y" + pos);
            y = (y << 1) + yWire;
        }
        var sum = x + y;
        if (debug) {
            System.out.printf("x=%s (%s)", x, Long.toBinaryString(x));
            System.out.printf(" y=%s (%s)", y, Long.toBinaryString(y));
            System.out.printf(" sum=%s (%s)%n", sum, Long.toBinaryString(sum));
        }

        return getIndices('z').stream()
                .flatMap(pos -> {
                    var z = eval.get("z" + pos);
                    var expected = (sum >> Integer.parseInt(pos)) & 1;
                    if (z != expected) {
                        return Stream.of("z" + pos);
                    }
                    return Stream.empty();
                })
                .toList();
    }

    public void writeGraphVizDotFile() {
        var evaluator = new Evaluator(this);
        var badZ = validateAddition(false);

        try (var writer = new FileWriter("day24.dot")) {
            writer.append("digraph D {\n");
            for (var name : namesToWires().keySet()) {
                var wire = namesToWires().get(name);
                var value = evaluator.get(name);

                var attrs = new HashMap<String, String>();
                attrs.put("label", String.format("\"%s (%s)\"", name, value));
                if (badZ.contains(name)) {
                    attrs.put("style", "filled");
                    attrs.put("color", "red");
                }

                writer.append(name).append(" [");
                writer.append(attrs.keySet().stream()
                        .map(attr -> String.format("%s=%s", attr, attrs.get(attr)))
                        .collect(Collectors.joining(",")));
                writer.append("]\n");

                switch (wire) {
                    case Device.Xor w -> {
                        writer.append(String.format("%s -> %s [label=\"xor\"]%n", w.op1().name(), name));
                        writer.append(String.format("%s -> %s [label=\"xor\"]%n", w.op2().name(), name));
                    }
                    case Device.And w -> {
                        writer.append(String.format("%s -> %s [label=\"and\"]%n", w.op1().name(), name));
                        writer.append(String.format("%s -> %s [label=\"and\"]%n", w.op2().name(), name));
                    }
                    case Device.Or w -> {
                        writer.append(String.format("%s -> %s [label=\"or\"]%n", w.op1().name(), name));
                        writer.append(String.format("%s -> %s [label=\"or\"]%n", w.op2().name(), name));
                    }
                    case Device.LiteralValue literalValue -> {
                    }
                    case Device.NamedValue namedValue -> {
                    }
                }
            }
            writer.append("}\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * returns a new Device with randomized values in the x and y wires
     */
    public Device randomizeXandY() {
        Map<String, Wire> newMap = new HashMap<>(namesToWires());
        var random = new Random();
        for (var pos : getIndices('x')) {
            newMap.put("x" + pos, new Device.LiteralValue(Math.abs(random.nextInt()) % 2));
            newMap.put("y" + pos, new Device.LiteralValue(Math.abs(random.nextInt()) % 2));
        }
        return new Device(newMap);
    }

    public static String formatAnswer(List<Pair> swaps) {
        return swaps.stream()
                .flatMap(pair -> Stream.of(pair.wire1, pair.wire2))
                .sorted()
                .collect(Collectors.joining(","));
    }

    public Device doSwaps(List<Pair> swapSet) {
        var testDevice = this;
        for (var swap : swapSet) {
            testDevice = testDevice.swap(swap.wire1, swap.wire2);
        }
        return testDevice;
    }

    public List<Pair> getPairs() {
        var allWires = namesToWires().keySet().stream().toList();

        Map<String, Set<String>> dependencies = getDependencies();

        // make all possible pairs, eliminating cycles
        // (where one wire in the pair is a dependent of the other wire)
        List<Pair> pairs = new ArrayList<>();
        for (var i = 0; i < allWires.size(); i++) {
            for (var j = i + 1; j < allWires.size(); j++) {
                var wire1 = allWires.get(i);
                var wire2 = allWires.get(j);
                var deps1 = dependencies.get(wire1);
                var deps2 = dependencies.get(wire2);
                if (deps1 != null & deps2 != null) {
                    if (!deps1.contains(wire2) && !deps2.contains(wire1)) {
                        pairs.add(new Pair(wire1, wire2));
                    }
                }
            }
        }
        return pairs;
    }

    /**
     * iteratively improve the list of swaps using a set of test devices, until we find a solution.
     * <p>
     * this is adapted from this solution:
     * https://www.reddit.com/r/adventofcode/comments/1hneuf0/comment/m41ms44/
     * <p>
     * This is better than naive brute force in a few ways:
     * - it eliminates the swaps that would result in cycles: this drastically reduces the search space
     * - it iterates on candidate sets of the "best" swaps until we find a solution, instead of iterating
     * exactly 4 times. I'm not sure why that worked in the code linked above, but since we're using test devices
     * with randomized values, order can vary in our discovery, so we need to try all possibilities.
     * - validateAddition() caches evaluation, speeding things up dramatically.
     * </p>
     */
    public List<Pair> findSwapsToMakeAdder() {

        // create test devices with randomized x and y values.
        // if number of devices is too low, we'll get misleading choices for "best" swaps
        List<Device> initialTestDevices = IntStream
                .range(0, 100)
                .mapToObj(i -> i == 0 ? this : randomizeXandY())
                .toList();

        // keep a running list of ever-improving swap sets
        List<List<Pair>> swapSets = new ArrayList<>();
        swapSets.add(new ArrayList<>());

        while (!swapSets.isEmpty()) {
            var swaps = swapSets.removeFirst();

            if (swaps.size() == 4) {
                throw new RuntimeException("Error: we reached 4 swaps while looking for the next best swap, something's gone wrong." +
                        " the number of test devices is probably too low.");
            }

            //System.out.println("finding best swap on test devices with these swaps so far=" + swaps);

            // set up test devices with swaps so far
            var testDevices = initialTestDevices.stream()
                    .map(device -> device.doSwaps(swaps))
                    .toList();

            // make all possible pairs, using any test device (they'll all have the same gate wires).
            var wiresInSwaps = swaps.stream()
                    .flatMap(pair -> Stream.of(pair.wire1(), pair.wire2()))
                    .collect(Collectors.toSet());

            var pairs = testDevices.getFirst().getPairs()
                    .stream()
                    .filter(pair ->
                            !(wiresInSwaps.contains(pair.wire1()) || wiresInSwaps.contains(pair.wire2()))
                    )
                    .toList();

            //System.out.println("Examining " + pairs.size() + " pairs");

            // count initial bad z wires
            var badZCountInitial = testDevices.stream()
                    .collect(Collectors.toMap(
                            device -> device,
                            device -> device.validateAddition(false).size()
                    ));

            // figure out cumulative improvement among the test devices for swaps
            record PairWithDiff(Pair pair, int diff) {
            }

            var start = System.currentTimeMillis();
            // parallelizing reduces runtime by a whopping 80% on my laptop with 16 virtual cores
            var cumulativeImprovement = pairs.parallelStream()
                    .map(pair -> {
                        var improvement = 0;
                        for (var testDevice : testDevices) {
                            var badZ = testDevice.swap(pair.wire1(), pair.wire2()).validateAddition(false);
                            var diff = badZCountInitial.get(testDevice) - badZ.size();
                            if (diff > 0) {
                                improvement += diff;
                            }
                        }
                        return new PairWithDiff(pair, improvement);
                    })
                    .collect(Collectors.toMap(PairWithDiff::pair, PairWithDiff::diff));

            //System.out.println("running all pairs through test devices took " + (System.currentTimeMillis() - start) + "ms");

            var mostImprovement = cumulativeImprovement.values().stream()
                    .mapToInt(n -> n)
                    .max()
                    .orElseThrow();

            var bestSwaps = cumulativeImprovement.keySet().stream()
                    .filter(pair -> cumulativeImprovement.get(pair).equals(mostImprovement))
                    .toList();

            //System.out.println("best swaps found (improvement " + mostImprovement + ") = " + bestSwaps);

            // see if any bestSwaps results in an answer that passes all test devices;
            // if so, we're done; if not, add them to our swapSets and iterate
            for (var aSwap : bestSwaps) {
                var hasFailure = testDevices.stream().anyMatch(device ->
                        !device.swap(aSwap.wire1(), aSwap.wire2()).validateAddition(false).isEmpty()
                );
                if (!hasFailure) {
                    swaps.add(aSwap);
                    return swaps;
                } else {
                    var newSwaps = new ArrayList<>(swaps);
                    newSwaps.add(aSwap);
                    swapSets.add(newSwaps);
                }
            }
        }
        throw new RuntimeException("we should never get here");
    }

}
