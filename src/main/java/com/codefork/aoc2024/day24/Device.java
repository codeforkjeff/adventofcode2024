package com.codefork.aoc2024.day24;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Device(Map<String, Wire> namesToWires) {

    public sealed interface Wire permits And, Or, Xor, NamedValue, LiteralValue {
        int value(Device device);
    }

    public record LiteralValue(int value) implements Wire {

        @Override
        public int value(Device device) {
            return value;
        }
    }

    public record NamedValue(String name) implements Wire {

        @Override
        public int value(Device device) {
            return device.namesToWires().get(name).value(device);
        }
    }

    public record And(NamedValue op1, NamedValue op2) implements Wire {

        @Override
        public int value(Device device) {
            return op1.value(device) & op2.value(device);
        }
    }

    public record Or(NamedValue op1, NamedValue op2) implements Wire {

        @Override
        public int value(Device device) {
            return op1.value(device) | op2.value(device);
        }
    }

    public record Xor(NamedValue op1, NamedValue op2) implements Wire {

        @Override
        public int value(Device device) {
            return op1.value(device) ^ op2.value(device);
        }
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
                            if(literalValueMatcher.find()) {
                                var name = literalValueMatcher.group(1);
                                var value = Integer.parseInt(literalValueMatcher.group(2));
                                acc.put(name, new LiteralValue(value));
                                return acc;
                            }
                            var gateMatcher = gatePat.matcher(line);
                            if(gateMatcher.find()) {
                                var op1 = new NamedValue(gateMatcher.group(1));
                                var op = gateMatcher.group(2);
                                var op2 = new NamedValue(gateMatcher.group(3));
                                var name = gateMatcher.group(4);
                                var wire = switch(op) {
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

    public int getWire(String wireName) {
        return namesToWires.get(wireName).value(this);
    }
}
