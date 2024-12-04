package com.codefork.aoc2024.day04;

import com.codefork.aoc2024.Problem;

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
            var seqStr = charListToStr(seq);
            var revStr = charListToStr(seq.reversed());
            return acc + frequency(XMAS, seqStr) + frequency(XMAS, revStr);
        }, Integer::sum);
    }

    public static List<List<Character>> pivotRowsToColumns(List<List<Character>> rows) {
        var numCols = rows.get(0).size();
        return IntStream
                .range(0, numCols)
                .mapToObj(colIdx ->
                        rows.stream().map(row -> row.get(colIdx)).toList())
                .toList();
    }

    /**
     * generates a list of diagonal sequences on the "forward slash" direction.
     * this is completely insane and I wrote/troubleshooted it empirically by
     * adjusting the indices calculations through trial and error.
     */
    public static <T> List<List<T>> rowsToDiagonals(List<List<T>> rows) {
        var numCols = rows.get(0).size();
        var numRows = rows.size();
        // create first set of diagonals by moving across columns, and for each column, iterating along diagonal
        var diags = IntStream
                .range(0, numCols)
                .mapToObj(colCounter -> {
                    var seq = IntStream
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
        var diags2 = IntStream
                .range(1, numRows)
                .mapToObj(rowCounter -> {
                    var seq = IntStream
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

    @Override
    public String solve() {
        // generate machine-analyzable "views" of the word search grid,
        // where a view is a list of a list of characters

        // "normal" view: rows and columns
        var rows = getInput()
                .filter(line -> !line.isEmpty())
                .map(line -> line.chars().mapToObj(c -> (char) c).toList())
                .toList();

        // view by columns: this is a pivotted version of rows
        var columns = pivotRowsToColumns(rows);

        // view by diagonals: list of the diagonal sequences of varying lengths
        var diagonals = rowsToDiagonals(rows);

        // view by diagonals in the other direction:
        var otherDirectionDiagonals = rowsToDiagonals(
                rows.stream().map(row -> row.reversed().stream().toList()).toList()
        );

        return String.valueOf(frequencyInView(rows)
                + frequencyInView(columns)
                + frequencyInView(diagonals)
                + frequencyInView(otherDirectionDiagonals));
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}