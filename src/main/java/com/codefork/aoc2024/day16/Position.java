package com.codefork.aoc2024.day16;

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

    public Position move(Direction dir) {
        return switch (dir) {
            case Direction.North -> new Position(x, y - 1);
            case Direction.South -> new Position(x, y + 1);
            case Direction.East -> new Position(x + 1, y);
            case Direction.West -> new Position(x - 1, y);
        };
    }
}
