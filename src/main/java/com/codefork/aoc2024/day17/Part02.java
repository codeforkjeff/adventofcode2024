package com.codefork.aoc2024.day17;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Adapted from these solutions:
 * https://github.com/robabrazado/adventofcode2024/blob/main/src/com/robabrazado/aoc2024/day17/Computer.java
 * https://git.sr.ht/~murr/advent-of-code/tree/master/item/2024/17/p2.c
 * <p>
 * Writing out the program by hand, you can see it's a single giant loop that
 * does the following:
 * - set register B = last 3 bits of register A
 * - update registers B and C with a bunch of operations, using all 3 registers
 * - right-shift 3 bits off of register A
 * - print the rightmost 3 bits of register B
 * - starts again from the beginning if A != 0
 * This means we don't have to care about the bunch of operations sandwiched
 * in the loop. Since the loop gradually right-shifts register A until it's 0,
 * we can work backwards:
 * - find the 3 bit values (there will be more than one) that will produce the last
 *   desired item
 * - left shift those values
 * - add another 3 bits to those values, to try to produce the last two desired items
 * - etc.
 */
public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var initialComputer = Computer.parse(data);
        var programSize = initialComputer.program().size();

        var i = programSize - 1;
        List<Integer> expectedOutput = new ArrayList<>();
        List<Long> candidates = new ArrayList<>();
        candidates.add(0L);

        while (i >= 0) {
            expectedOutput.addFirst(initialComputer.program().get(i));

            List<Long> newCandidates = new ArrayList<>();

            //System.out.println("looking for next expected output=" + expectedOutput);

            for(var candidate : candidates) {
                for(var threeBits = 0; threeBits < 8; threeBits++) {
                    var testA = (candidate << 3) + threeBits;
                    var testComputer = initialComputer.withA(testA);
                    var finalState = testComputer.run();
                    if(finalState.output().equals(expectedOutput)) {
                        newCandidates.add(testA);
                    }
                }
            }
            candidates = newCandidates;
            //System.out.println("candidates=" + candidates);
            i--;
        }

        var lowest = candidates.stream().mapToLong(n -> n).min().orElseThrow();
        return String.valueOf(lowest);
    }

    @Override
    public String solve() {
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
