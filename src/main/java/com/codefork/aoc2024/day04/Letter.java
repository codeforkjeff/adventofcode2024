package com.codefork.aoc2024.day04;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Letter(Character ch, int x, int y) {

    /**
     * creates a list of lists of Letter records from a list of strings
     */
    public static List<List<Letter>> createLetterRows(List<String> input) {
        return IntStream
                .range(0, input.size())
                .mapToObj(rowIdx -> {
                    final var row = input.get(rowIdx);
                    return IntStream
                            .range(0, row.length())
                            .mapToObj(colIdx ->
                                    new Letter(row.charAt(colIdx), colIdx, rowIdx))
                            .toList();
                })
                .toList();
    }

    /**
     * @param seq
     * @param s
     * @return true if the list of Letter records represents the string s
     */
    public static boolean letterListEquals(List<Letter> seq, String s) {
        final var seqStr = seq.stream().map(letter -> String.valueOf(letter.ch())).collect(Collectors.joining());
        return s.equals(seqStr);
    }

}
