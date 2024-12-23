package com.codefork.aoc2024.day21;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public class Part01 extends Problem {

    public record Position(int x, int y) {

    }

    public record Button(String symbol, Position pos) {

        public boolean isAdjacent(Button other) {
            var xDiff = Math.abs(pos().x() - other.pos().x());
            var yDiff = Math.abs(pos().y() - other.pos().y());
            return xDiff + yDiff == 1;
        }

        /**
         * returns the action symbol when navigating from this button to (adjacent) target
         */
        public String getAction(Button target) {
            var xDiff = pos().x() - target.pos().x();
            var yDiff = pos().y() - target.pos().y();
            if(xDiff > 0) {
                return "<";
            } else if(xDiff < 0) {
                return ">";
            }
            if(yDiff > 0) {
                return "^";
            } else if (yDiff < 0) {
                return "v";
            }
            throw new RuntimeException("buttons aren't adjacent in getAction=" + this + "," + target);
        }

        public String toString() {
            return symbol + "(" + pos.x() + "," + pos.y() + ")";
        }
    }

    public record Move(Button from, Button to) {

    }

    public record ShortestPaths(Button source, Map<Button, Integer> dist, Map<Button, Set<Button>> prev) {

        // Dijkstra, yet again.
        // this is probably overkill since there's not weighted edges in the graph
        // but I've already implemented it twice so far for AoC, so it's the easiest way
        // for me to create shortest paths at this point
        private static ShortestPaths create(List<Button> buttons, Map<Button, List<Button>> graph, Button source) {
            record ButtonWithDist(Button button, int dist) {

            }
            var dist = new HashMap<Button, Integer>();
            var prev = new HashMap<Button, Set<Button>>();
            var q = new HashSet<Button>();

            for(var button : buttons) {
                dist.put(button, Integer.MAX_VALUE);
                q.add(button);
            }
            dist.put(source, 0);

            while(!q.isEmpty()) {
                var u = q.stream()
                        .map(button -> new ButtonWithDist(button, dist.get(button)))
                        .min(Comparator.comparingInt(ButtonWithDist::dist))
                        .orElseThrow()
                        .button();
                q.remove(u);

                var neighborsInQ = graph.get(u).stream()
                        .filter(q::contains)
                        .toList();

                for(var v : neighborsInQ) {
                    var alt = dist.get(u) + 1;
                    if (alt <= dist.get(v)) {
                        dist.put(v, alt);
                        if(!prev.containsKey(v)) {
                            prev.put(v, new HashSet<>());
                        }
                        prev.get(v).add(u);
                    }
                }
            }
            return new ShortestPaths(source, dist, prev);
        }

        /**
         * get all the possible paths for navigating from source to target
         */
        public List<List<Button>> getPaths(Button target) {
            var paths = new ArrayList<List<Button>>();
            paths.add(new LinkedList<>(List.of(target)));
            var finalPaths = new ArrayList<List<Button>>();
            while(!paths.isEmpty()) {
                var newPaths = new ArrayList<List<Button>>();
                for(var path : paths) {
                    var u = path.getFirst();
                    if(prev.containsKey(u)){
                        var allPrev = prev.get(u);
                        for(var prevItem : allPrev) {
                            var newPath = new LinkedList<>(path);
                            newPath.addFirst(prevItem);
                            newPaths.add(newPath);
                        }

                    } else if (u.equals(source)) {
                        finalPaths.add(path);
                    }
                }
                paths = newPaths;
            }
            //System.out.println(source + "->" + target + ", returning " + finalPaths.size() + " finalPaths=" + finalPaths);
            return finalPaths;
        }

        public List<String> getPossiblePressSequences(Button target) {
            var paths = getPaths(target);
            return paths.stream()
                    .map(path -> {
                        return IntStream.range(1, path.size()).boxed()
                                .collect(foldLeft(
                                        StringBuilder::new,
                                        (acc, i) -> {
                                            var button = path.get(i);
                                            var prevButton = path.get(i - 1);
                                            var action = prevButton.getAction(button);
                                            acc.append(action);
                                            return acc;
                                        })
                                )
                                .append("A")
                                .toString();
                        })
                    .toList();
        }
    }

    public static class Keypad {
        private final List<Button> buttons;

        private final Map<String, Button> buttonsMap;

        private final Map<Move, List<String>> movesToPaths;

        public Keypad(List<Button> buttons) {
            this.buttons = buttons;
            this.buttonsMap = buttons.stream()
                    .collect(Collectors.toMap(
                            Button::symbol,
                            b -> b));

            this.movesToPaths = createShortestPaths();
        }

        private Map<Move, List<String>> createShortestPaths() {
            Map<Move, List<String>> result = new HashMap<>();

            // generate graph of adjacent buttons
            var graph = new HashMap<Button, List<Button>>();
            for(var i=0; i < buttons.size(); i++) {
                var button1 = buttons.get(i);
                for(var j=i; j < buttons.size(); j++) {
                    var button2 = buttons.get(j);
                    if(button1.isAdjacent(button2)) {
                        if(!graph.containsKey(button1)) {
                            graph.put(button1, new ArrayList<>());
                        }
                        graph.get(button1).add(button2);
                        if(!graph.containsKey(button2)) {
                            graph.put(button2, new ArrayList<>());
                        }
                        graph.get(button2).add(button1);
                    }
                }
            }

            // generate shortest paths for every button to every other button
            for(var i=0; i < buttons.size(); i++) {
                var button1 = buttons.get(i);
                var shortestPaths = ShortestPaths.create(buttons, graph, button1);
                var otherButtons = buttons.stream().filter(b -> !b.equals(button1)).toList();
                for (var button2 : otherButtons) {
                    var presses = shortestPaths.getPossiblePressSequences(button2);
                    //System.out.println("move " + button1 + "->" + button2 + " adding presses=" + presses);
                    result.put(new Move(button1, button2), presses);
                }
            }
            return result;
        }

        public Map<String, Button> getButtonsMap() {
            return buttonsMap;
        }

        public Map<Move, List<String>> getMovesToPaths() {
            return movesToPaths;
        }
    }

    public static class KeypadNavigator {

        private final Keypad keypad;
        private final Button current;

        public KeypadNavigator(Keypad keypad, String current) {
            this.keypad = keypad;
            this.current = this.keypad.getButtonsMap().get(current);
        }

        public List<String> getPossiblePressSequences(String seq) {
            List<StringBuilder> pressSeqList = new ArrayList<>();
            pressSeqList.add(new StringBuilder());
            var c = current;
            while(!seq.isEmpty()) {
                var newPressSeqList = new ArrayList<StringBuilder>();
                var symbol = seq.substring(0, 1);
                var next = keypad.getButtonsMap().get(symbol);
                if(next.equals(c)) {
                    for(var pressSeq : pressSeqList) {
                        pressSeq.append("A");
                        newPressSeqList.add(pressSeq);
                    }
                } else {
                    var move = new Move(c, next);
                    var paths = keypad.getMovesToPaths().get(move);
                    for(var pressSeq : pressSeqList) {
                        for(var path : paths) {
                            var copy = new StringBuilder(pressSeq.toString());
                            copy.append(path);
                            //System.out.println("move " + move + ", appended " + path + ", copy is: " + copy);
                            newPressSeqList.add(copy);
                        }
                    }
                }
                pressSeqList = newPressSeqList;
                c = next;
                seq = seq.substring(1);
            }
            return pressSeqList.stream()
                    .map(StringBuilder::toString)
                    .toList();
        }

    }

    public static class DoorKeypadNavigator extends KeypadNavigator {

        private static final List<Button> doorButtons = List.of(
                new Button("7", new Position(0, 0)),
                new Button("8", new Position(1, 0)),
                new Button("9", new Position(2, 0)),
                new Button("4", new Position(0, 1)),
                new Button("5", new Position(1, 1)),
                new Button("6", new Position(2, 1)),
                new Button("1", new Position(0, 2)),
                new Button("2", new Position(1, 2)),
                new Button("3", new Position(2, 2)),
                new Button("0", new Position(1, 3)),
                new Button("A", new Position(2, 3))
        );

        private static final Keypad doorKeypad = new Keypad(doorButtons);

        public DoorKeypadNavigator(String startSymbol) {
            super(doorKeypad, startSymbol);
        }

    }

    public static class DirectionalKeypadNavigator extends KeypadNavigator {

        private static final List<Button> robotButtons = List.of(
                new Button("^", new Position(1, 0)),
                new Button("A", new Position(2, 0)),
                new Button("<", new Position(0, 1)),
                new Button("v", new Position(1, 1)),
                new Button(">", new Position(2, 1))
        );

        private static final Keypad robotKeypad = new Keypad(robotButtons);

        public DirectionalKeypadNavigator(String startSymbol) {
            super(robotKeypad, startSymbol);
        }

    }

    public int getNumericPortion(String str) {
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

    public String solve(Stream<String> data) {
        var navigators = List.of(
                new DoorKeypadNavigator("A"),
                new DirectionalKeypadNavigator("A"),
                new DirectionalKeypadNavigator("A")
        );

        var complexities = data
                .map(seq -> {
                    System.out.println("doing " + seq);
                    // run each sequence through a list of navigators, collecting results
                    var pressesList = List.of(seq);
                    for (var navigator : navigators) {
                        var newPressesList = new ArrayList<String>();
                        for (var presses : pressesList) {
                            var possiblePresses = navigator.getPossiblePressSequences(presses);
                            for (var p : possiblePresses) {
                                System.out.println(p);
                            }
                            newPressesList.addAll(possiblePresses);
                        }

//                        var lengthCounts = newPressesList.stream()
//                                .collect(Collectors.toMap(
//                                        String::length,
//                                        s -> 1,
//                                        Integer::sum
//                                ));
//                        for(var len : lengthCounts.keySet().stream().sorted(Integer::compareTo).toList()) {
//                            System.out.println("press sequences with len = " + len +
//                                    " occurred " + lengthCounts.get(len) + " times");
//                        }

                        // group press sequences by their "signature" (where/which elements repeat)


                        System.out.println(navigator + " found " + newPressesList.size() + " possible presses");

                        pressesList = newPressesList;
                    }
                    System.out.println("found " + pressesList.size() + " possible presses for last navigator");
                    // find the shortest path of the LAST navigator only
                    var shortestPress = pressesList.stream()
                            .min(Comparator.comparingInt(String::length))
                            .orElseThrow();
                    System.out.println("shortest press found is "+ shortestPress.length() + " long");
                    var result = shortestPress.length() * getNumericPortion(seq);
                    return result;
                })
                .toList();

        var total = complexities.stream().mapToInt(i -> i).sum();
        return String.valueOf(total);
    }

    @Override
    public String solve() {
        Assert.assertEquals("126384", solve(getSampleInput()));
        //return solve(getInput());
        return "";
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
