package com.codefork.aoc2024.day15;

public record Position(int x, int y) {

    public Position withChangedX(int dX) {
        return new Position(x() + dX, y());
    }

    public Position withChangedY(int dY) {
        return new Position(x(), y() + dY);
    }

}
