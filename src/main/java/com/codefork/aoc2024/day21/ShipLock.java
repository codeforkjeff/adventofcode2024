package com.codefork.aoc2024.day21;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShipLock {

    public static int getNumericPortion(String str) {
        return Integer.parseInt(str.chars()
                .boxed()
                .map(ch -> String.valueOf((char) ch.intValue()))
                .filter(s -> {
                    try {
                        Integer.parseInt(s);
                    } catch(NumberFormatException e) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.joining()));
    }

    public static long calculateSumOfComplexities(Stream<String> data, int numRobots) {
        record WithLength(PressSequence seq, long length) {
        }

        // create our layered navigators
        var navigators = new ArrayList<KeypadNavigator>();
        navigators.add(new DoorKeypadNavigator());
        for(var i = 0; i < numRobots; i++) {
            navigators.add(new DirectionalKeypadNavigator());
        }

        var complexities = data
                .map(seq -> {
                    // run each sequence through a list of navigators, using results from each iteration
                    // as input for the next one.
                    List<PressSequence> pressSeqs = new ArrayList<PressSequence>();
                    pressSeqs.add(PressSequence.create(navigators.getFirst().getKeypad(), seq));
                    for (var navigator : navigators) {
                        var newPressSeqSet = new HashSet<PressSequence>();
                        // one to many results
                        for (var pressSeq : pressSeqs) {
                            var possiblePresses = navigator.getPossiblePressSequences(pressSeq);
                            newPressSeqSet.addAll(possiblePresses);
                        }

                        // calculate lengths just once, since it's a slightly expensive operation
                        var withLengths = newPressSeqSet.stream()
                                .map(ps -> new WithLength(ps, ps.length()))
                                .toList();

                        var shortestLength = withLengths.stream()
                                .mapToLong(WithLength::length)
                                .min()
                                .orElseThrow();

                        // we don't need to carry over every result to the next iteration, only the shortest ones.
                        // this shortcut is necessary to get the solution to finish at all
                        var truncated = withLengths.stream()
                                .filter(item -> item.length() == shortestLength)
                                .map(WithLength::seq)
                                .toList();
                        pressSeqs = truncated;
                    }
                    var shortestLength = pressSeqs.getFirst().length();
                    //System.out.println("shortest press found is "+ shortestLength + " long");
                    var result = shortestLength * getNumericPortion(seq);
                    return result;
                })
                .toList();

        return complexities.stream().reduce(0L, Long::sum);
    }

}
