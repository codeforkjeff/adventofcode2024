package com.codefork.aoc2024.day04;

import com.codefork.aoc2024.Problem;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codefork.aoc2024.day04.Part01.rowsToDiagonals;

/**
 * Strategy = store the input as Letter records containing an x, y coordinate.
 * We can reuse the diagonals code from part 1 to find all the occurrences of "MAS"
 * and store just the "A" which is the intersecting part. Then we cna find the intersection
 * of the A's found in both sets of diagonals (in each direction) to determine the X's.
 *
 * This isn't a great solution, but it builds off the strategy I used in part 1. Had I
 * foreseen where part 2 was going, I think I would have done part 1 differently.
 */
public class Part02 extends Problem {

    public static final String MAS = "MAS";

    /**
     * @param seq
     * @param s
     * @return true if the list of Letter records represents the string s
     */
    public static boolean letterListEquals(List<Letter> seq, String s) {
        var seqStr = seq.stream().map(letter -> String.valueOf(letter.ch())).collect(Collectors.joining());
        return s.equals(seqStr);
    }

    /**
     * returns the "A" part of "MAS"
     */
     public static List<Letter> findMasInSeq(List<Letter> seq) {
        return IntStream
                .range(0, seq.size() - MAS.length() + 1)
                .filter(i -> {
                    var subseq = seq.subList(i, i + 3);
                    return letterListEquals(subseq, MAS);
                })
                .mapToObj(i -> {
                    // return the 'A'
                    return seq.get(i+1);
                })
                .toList();
    }

    /**
     * Find occurrences of MAS, both forwards and backwards, in a view of the data, and returns
     * the list of A's
     * @param view
     * @return
     */
    public static List<Letter> findInView(List<List<Letter>> view) {
        return view.stream()
                .flatMap(seq -> {
                     return Stream.concat(
                             findMasInSeq(seq).stream(),
                             findMasInSeq(seq.reversed()).stream()
                     );
                })
                .toList();
    }

    @Override
    public String solve() {
        var rows = Letter.createLetterRows(getInput().toList());

        var diagonals = rowsToDiagonals(rows);

        var otherDirectionDiagonals = rowsToDiagonals(
            rows.stream().map(row -> row.reversed().stream().toList()).toList()
        );

        var set = new HashSet<>(findInView(diagonals));

        var intersection = findInView(otherDirectionDiagonals)
                .stream()
                .filter(letter -> set.contains(letter))
                .toList();

        return String.valueOf(intersection.size());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
