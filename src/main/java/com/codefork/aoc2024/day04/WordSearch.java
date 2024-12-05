package com.codefork.aoc2024.day04;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WordSearch {

    /**
     * generates a list of diagonal sequences on the "forward slash" direction.
     * this is completely insane and I wrote/troubleshooted it empirically by
     * adjusting the indices calculations through trial and error.
     */
    public static <T> List<List<T>> rowsToDiagonals(List<List<T>> rows) {
        final var numCols = rows.get(0).size();
        final var numRows = rows.size();
        // create first set of diagonals by moving across columns, and for each column, iterating along diagonal
        final var diags = IntStream
                .range(0, numCols)
                .mapToObj(colCounter -> {
                    final var seq = IntStream
                            .range(0, colCounter + 1)
                            .mapToObj(rowCounter -> {
                                var rowIdx = rowCounter;
                                var colIdx = colCounter - rowCounter;
                                //System.out.printf("(%s, %s) ", colIdx, rowIdx);
                                return rows.get(rowIdx).get(colIdx);
                            })
                            .toList();
                    //System.out.println("= \"" + charListToStr(s) + "\"");
                    return seq;
                })
                .toList();
        // create second set of diagonals by moving across rows on the right edge, and for each row, iterating along diagonal
        final var diags2 = IntStream
                .range(1, numRows)
                .mapToObj(rowCounter -> {
                    final var seq = IntStream
                            .iterate(numCols - 1, colIdx -> colIdx >= numRows - (numRows - rowCounter), colIdx -> colIdx - 1)
                            .mapToObj(colCounter -> {
                                var rowIdx = rowCounter + (numCols - colCounter) - 1;
                                var colIdx = colCounter;
                                //System.out.printf("(%s, %s) ", colIdx, rowIdx);
                                return rows.get(rowIdx).get(colIdx);
                            })
                            .toList();
                    //System.out.println("= \"" + charListToStr(s) + "\"");
                    return seq;
                })
                .toList();
        return Stream.concat(diags.stream(), diags2.stream()).toList();
    }

}
