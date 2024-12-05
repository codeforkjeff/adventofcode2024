package com.codefork.aoc2024.day04;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part01 extends Problem {

    public static String XMAS = "XMAS";

    public static String charListToStr(List<Character> charList) {
        return charList.stream().map(String::valueOf).collect(Collectors.joining());
    }

    /**
     * returns frequency of token in a string s
     */
    public static int frequency(String token, String s) {
        return IntStream
                .range(0, s.length() - token.length() + 1)
                .reduce(0, (acc, i) ->
                        token.equals(s.substring(i, i + token.length())) ? acc + 1 : acc
                );
    }

    // look for all occurrences, forward and reverse, in a view
    public static int frequencyInView(List<List<Character>> view) {
        return view.stream().reduce(0, (acc, seq) -> {
            final var seqStr = charListToStr(seq);
            final var revStr = charListToStr(seq.reversed());
            return acc + frequency(XMAS, seqStr) + frequency(XMAS, revStr);
        }, Integer::sum);
    }

    public static List<List<Character>> pivotRowsToColumns(List<List<Character>> rows) {
        final var numCols = rows.get(0).size();
        return IntStream
                .range(0, numCols)
                .mapToObj(colIdx ->
                        rows.stream().map(row -> row.get(colIdx)).toList())
                .toList();
    }

    public String solve(Stream<String> data) {
        // generate machine-analyzable "views" of the word search grid,
        // where a view is a list of a list of characters

        // "normal" view: rows and columns
        final var rows = data
                .filter(line -> !line.isEmpty())
                .map(line -> line.chars().mapToObj(c -> (char) c).toList())
                .toList();

        // view by columns: this is a pivotted version of rows
        final var columns = pivotRowsToColumns(rows);

        // view by diagonals: list of the diagonal sequences of varying lengths
        final var diagonals = WordSearch.rowsToDiagonals(rows);

        // view by diagonals in the other direction:
        final var otherDirectionDiagonals = WordSearch.rowsToDiagonals(
                rows.stream().map(row -> row.reversed().stream().toList()).toList()
        );

        return String.valueOf(frequencyInView(rows)
                + frequencyInView(columns)
                + frequencyInView(diagonals)
                + frequencyInView(otherDirectionDiagonals));
    }

    @Override
    public String solve() {
        Assert.assertEquals("18", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
