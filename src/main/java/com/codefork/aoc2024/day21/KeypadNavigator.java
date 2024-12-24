package com.codefork.aoc2024.day21;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class representing a robot/person who interacts with a keypad.
 * Subclasses specify the particular kind of keypad.
 */
public class KeypadNavigator {

    private final Keypad keypad;

    public KeypadNavigator(Keypad keypad) {
        this.keypad = keypad;
    }

    public Keypad getKeypad() {
        return keypad;
    }

    /**
     * Transforms a PressSequence into its next possible iteration(s)
     */
    public Set<PressSequence> getPossiblePressSequences(PressSequence seq) {
        List<PressSequence> results = new ArrayList<>();
        results.add(PressSequence.create());

        //System.out.println("getting possible sequences for seq=" + seq);

        // examine each move and append the possibilities to results
        for (var entry : seq.moves().entrySet()) {
            var newResults = new ArrayList<PressSequence>();
            var move = entry.getKey();
            var moveCount = entry.getValue();

            //System.out.println("handling " + move + " (count=" + moveCount + ")");

            for (var result : results) {
                var possibilities = addMove(result, move, moveCount);
                newResults.addAll(possibilities);
            }
            results = newResults;
        }

        return new HashSet<>(results);
    }

    /**
     * returns list of new PressSequences that are the possibilities that can result from adding the passed-in move
     *
     * @param keypad used to get the Button objects for the symbols, and to get the possible paths between buttons.
     */
    public List<PressSequence> addMove(PressSequence seq, Move move, long multiplier) {
        var results = new ArrayList<PressSequence>();
        var paths = getKeypad().getMovesToPaths().get(move);

        //System.out.println("from=" + _from + " to=" + _to + " paths=" + paths);

        for (var path : paths) {
            var copy = seq.copy();

            char first = path.charAt(0);

            // note that we use dirKeypad here, since movements match up with the directional keypad

            // add a transitional move from the last implicit "A"
            if (path.length() > 1 && first != 'A') {
                var transition = new Move(Keypad.dirKeypad.getButton("A"), Keypad.dirKeypad.getButton(first));
                copy.moves().merge(transition, multiplier, Long::sum);
            }

            var from = first;
            for (var i = 1; i < path.length(); i++) {
                var to = path.charAt(i);

                var moveInPath = new Move(Keypad.dirKeypad.getButton(from), Keypad.dirKeypad.getButton(to));
                copy.moves().merge(moveInPath, multiplier, Long::sum);

                from = to;
            }

            // this happens when a button repeats (i.e. moves from a button to itself)
            if (path.length() == 1) {
                var aButton = Keypad.dirKeypad.getButton("A");
                var moveInPath = new Move(aButton, aButton);
                copy.moves().merge(moveInPath, multiplier, Long::sum);
            }
            results.add(copy);
        }
        //System.out.println("addMove returning=" + results);

        return results;
    }

}
