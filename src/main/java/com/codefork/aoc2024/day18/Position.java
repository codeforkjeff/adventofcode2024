package com.codefork.aoc2024.day18;

public record Position(int x, int y) {
    public Position decX() {
        return new Position(x - 1, y);
    }

    public Position incX() {
        return new Position(x + 1, y);
    }

    public Position decY() {
        return new Position(x, y - 1);
    }

    public Position incY() {
        return new Position(x, y + 1);
    }
}
