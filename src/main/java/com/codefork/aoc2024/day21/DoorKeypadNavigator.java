package com.codefork.aoc2024.day21;

import java.util.List;

public class DoorKeypadNavigator extends KeypadNavigator {

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

    public DoorKeypadNavigator() {
        super(doorKeypad);
    }

}
