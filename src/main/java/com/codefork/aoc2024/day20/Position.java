package com.codefork.aoc2024.day20;

public record Position(int x, int y) {

    public static final Position UNSET = new Position(-1, -1);

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
