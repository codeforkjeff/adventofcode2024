package com.codefork.aoc2024.day04;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codefork.aoc2024.day04.Part01.rowsToDiagonals;

/**
 * Strategy = store the input as Letter records containing an x, y coordinate.
 * We can reuse the diagonals code from part 1 to find all the occurrences of "MAS"
 * and store just the "A" which is the intersecting part. Then we can find the intersection
 * of the A's found in both sets of diagonals (in each direction) to determine the X's.
 *
 * This isn't a great solution, but it builds off the strategy I used in part 1. Had I
 * foreseen where part 2 was going, I think I would have done part 1 differently.
 */
public class Part02 extends Problem {

    public static final String MAS = "MAS";

    /**
     * returns all the "A" parts of "MAS" occurrences
     */
    public static List<Letter> findMasInSeq(List<Letter> seq) {
        return IntStream
                .range(0, seq.size() - MAS.length() + 1)
                .filter(i -> {
                    var subseq = seq.subList(i, i + 3);
                    return Letter.letterListEquals(subseq, MAS);
                })
                .mapToObj(i -> {
                    // return the 'A'
                    return seq.get(i+1);
                })
                .toList();
    }

    /**
     * Find occurrences of MAS, both forwards and backwards, in a view of the data, and returns
     * the list of the A's
     * @param view
     * @return
     */
    public static List<Letter> findMasInView(List<List<Letter>> view) {
        return view.stream()
                .flatMap(seq -> {
                    return Stream.concat(
                            findMasInSeq(seq).stream(),
                            findMasInSeq(seq.reversed()).stream()
                    );
                })
                .toList();
    }

    public String solve(Stream<String> data) {
        var rows = Letter.createLetterRows(data.toList());

        var diagonals = rowsToDiagonals(rows);

        var otherDirectionDiagonals = rowsToDiagonals(
            rows.stream().map(row -> row.reversed().stream().toList()).toList()
        );

        var set = new HashSet<>(findMasInView(diagonals));

        // the intersection of the A's in the two sets of diagonals
        // will tell us where the X's are
        var intersection = findMasInView(otherDirectionDiagonals)
                .stream()
                .filter(set::contains)
                .toList();

        return String.valueOf(intersection.size());
    }

    @Override
    public String solve() {
        Assert.assertEquals("9", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
