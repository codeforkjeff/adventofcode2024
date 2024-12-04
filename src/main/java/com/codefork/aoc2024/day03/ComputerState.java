package com.codefork.aoc2024.day03;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record ComputerState(boolean enabled, boolean recognizeConditionals, int sum) {

    private static final Pattern mulPattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");

    /**
     * @param instruction instruction to execute
     * @return new state of the computer
     */
    public ComputerState execute(String instruction) {
        final var mulMatcher = mulPattern.matcher(instruction);
        if("do()".equals(instruction)) {
            final var newEnabled = recognizeConditionals ? true : enabled;
            return new ComputerState(newEnabled, recognizeConditionals, sum);
        } else if ("don't()".equals(instruction)) {
            final var newEnabled = recognizeConditionals ? false : enabled;
            return new ComputerState(newEnabled, recognizeConditionals, sum);
        }  else if(mulMatcher.find()) {
            final var newSum = sum + (enabled ? Integer.parseInt(mulMatcher.group(1)) * Integer.parseInt(mulMatcher.group(2)) : 0);
            return new ComputerState(enabled, recognizeConditionals, newSum);
        }
        throw new RuntimeException("instruction not handled: " + instruction);
    }

    /**
     * recursively consume the matcher, passing along a new ComputerState with each iteration.
     * returns the final ComputerState.
     */
    private static ComputerState consumeMatcher(Matcher matcher, String line, ComputerState current) {
        if (matcher.find()) {
            final var instruction = line.substring(matcher.start(), matcher.end());
            return consumeMatcher(matcher, line, current.execute(instruction));
        } else {
            return current;
        }
    }

    private static final Pattern pat = Pattern.compile("mul\\((\\d+),(\\d+)\\)|do\\(\\)|don't\\(\\)");

    /**
     * Run all the instructions found in the stream.
     * @param input
     * @param recognizeConditionals recognize do() and don't()
     * @return sum of all the results of mul() instructions found
     */
    public static int sumInstructionResults(Stream<String> input, boolean recognizeConditionals) {
        final var finalState = input.collect(foldLeft(
                () -> new ComputerState(true, recognizeConditionals, 0),
                (acc, line) -> {
                    var matcher = pat.matcher(line);
                    return consumeMatcher(matcher, line, acc);
                }));
        return finalState.sum;
    }

}
