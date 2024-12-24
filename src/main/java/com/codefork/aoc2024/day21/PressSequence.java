package com.codefork.aoc2024.day21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a sequence of button actions to perform.
 * Order doesn't actually matter in a press sequence, only the moves do.
 */
public record PressSequence(Map<Move, Long> moves) {

    public static PressSequence create() {
        return new PressSequence(new HashMap<>());
    }

    /**
     * represent the passed-in seq as a set of moves.
     * this 'constructor' is only used for the initial sequence
     **/
    public static PressSequence create(Keypad keypad, String seq) {
        var moves = new HashMap<Move, Long>();
        char last = 'A';
        for (var i = 0; i < seq.length(); i++) {
            char ch = seq.charAt(i);
            var move = new Move(keypad.getButton(last), keypad.getButton(ch));
            if (!moves.containsKey(move)) {
                moves.put(move, 0L);
            }
            moves.put(move, moves.get(move) + 1);
            last = ch;
        }
        return new PressSequence(moves);
    }

//    /**
//     * returns list of new PressSequences that are the possibilities that can result from adding the passed-in move
//     *
//     * @param keypad used to get the Button objects for the symbols, and to get the possible paths between buttons.
//     */
//    public List<PressSequence> addMove(Keypad keypad, Move move, long multiplier) {
//        var results = new ArrayList<PressSequence>();
//        var paths = keypad.getMovesToPaths().get(move);
//
//        //System.out.println("from=" + _from + " to=" + _to + " paths=" + paths);
//
//        for (var path : paths) {
//            var copy = copy();
//
//            char first = path.charAt(0);
//
//            // note that we use dirKeypad here, since movements match up with the directional keypad
//
//            // add a transitional move from the last implicit "A"
//            if (path.length() > 1 && first != 'A') {
//                var transition = new Move(Keypad.dirKeypad.getButton("A"), Keypad.dirKeypad.getButton(first));
//                copy.moves().merge(transition, multiplier, Long::sum);
//            }
//
//            var from = first;
//            for (var i = 1; i < path.length(); i++) {
//                var to = path.charAt(i);
//
//                var moveInPath = new Move(Keypad.dirKeypad.getButton(from), Keypad.dirKeypad.getButton(to));
//                copy.moves().merge(moveInPath, multiplier, Long::sum);
//
//                from = to;
//            }
//
//            // this happens when a button repeats (i.e. moves from a button to itself)
//            if (path.length() == 1) {
//                var aButton = Keypad.dirKeypad.getButton("A");
//                var moveInPath = new Move(aButton, aButton);
//                copy.moves().merge(moveInPath, multiplier, Long::sum);
//            }
//            results.add(copy);
//        }
//        //System.out.println("addMove returning=" + results);
//
//        return results;
//    }
//
    public PressSequence copy() {
        return new PressSequence(new HashMap<>(moves));
    }

    public long length() {
        return moves().values().stream()
                .reduce(0L, Long::sum);
    }

    // for debugging, to verify whether a PressSequence matches with some of
    // the sample values in the puzzle description
    @SuppressWarnings("unused")
    public boolean represents(Keypad keypad, String seq) {
        // prepend "A"
        seq = "A" + seq;

        var copy = new HashMap<>(moves());
        for (var i = 1; i < seq.length(); i++) {
            var move = new Move(
                    keypad.getButton(seq.charAt(i - 1)),
                    keypad.getButton(seq.charAt(i))
            );
            if (copy.containsKey(move)) {
                var value = copy.get(move);
                if (value > 1) {
                    copy.put(move, value - 1);
                } else {
                    copy.remove(move);
                }
            } else {
                return false;
            }
        }
        return copy.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("len=");
        buf.append(length());
        buf.append(";");
        buf.append(moves().toString());
        return buf.toString();
    }
}
