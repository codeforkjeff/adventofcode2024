package com.codefork.aoc2024.day21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Keypad {

    // directional keypad buttons
    private static final List<Button> dirKeypadButtons = List.of(
            new Button("^", new Position(1, 0)),
            new Button("A", new Position(2, 0)),
            new Button("<", new Position(0, 1)),
            new Button("v", new Position(1, 1)),
            new Button(">", new Position(2, 1))
    );

    // directional keypad: this is used by both navigators
    public static final Keypad dirKeypad = new Keypad(dirKeypadButtons);

    private final List<Button> buttons;

    private final Map<String, Button> buttonsMap;

    // map of moves to every possible path for making that move
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
        for (var i = 0; i < buttons.size(); i++) {
            var button1 = buttons.get(i);
            for (var j = i; j < buttons.size(); j++) {
                var button2 = buttons.get(j);
                if (button1.isAdjacent(button2)) {
                    if (!graph.containsKey(button1)) {
                        graph.put(button1, new ArrayList<>());
                    }
                    graph.get(button1).add(button2);
                    if (!graph.containsKey(button2)) {
                        graph.put(button2, new ArrayList<>());
                    }
                    graph.get(button2).add(button1);
                }
            }
        }

        // generate shortest paths for every button to every other button
        for (var i = 0; i < buttons.size(); i++) {
            var button1 = buttons.get(i);
            var shortestPaths = ShortestPaths.create(buttons, graph, button1);
            var otherButtons = buttons.stream().filter(b -> !b.equals(button1)).toList();
            for (var button2 : otherButtons) {
                var presses = shortestPaths.getPathsAsStrings(button2);
                //System.out.println("move " + button1 + "->" + button2 + " adding presses=" + presses);
                result.put(new Move(button1, button2), presses);
            }
        }

        // path from a button to itself it just "A"
        for (Button button : buttons) {
            result.put(new Move(button, button), List.of("A"));
        }

        return result;
    }

    public Button getButton(String symbol) {
        return buttonsMap.get(symbol);
    }

    public Button getButton(char symbol) {
        return buttonsMap.get(String.valueOf(symbol));
    }

    public Map<Move, List<String>> getMovesToPaths() {
        return movesToPaths;
    }
}
