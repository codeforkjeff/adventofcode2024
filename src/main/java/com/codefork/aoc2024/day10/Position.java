package com.codefork.aoc2024.day10;

public record Position(int x, int y, int height) {

    public boolean isAdjacent(Position other) {
        var xDiff = Math.abs(other.x() - x);
        var yDiff = Math.abs(other.y() - y);
        return xDiff + yDiff <= 1;
    }
}
