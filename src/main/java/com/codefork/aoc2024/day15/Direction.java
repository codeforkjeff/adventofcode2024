package com.codefork.aoc2024.day15;

public enum Direction {
    Up, Right, Down, Left;

    public static Direction parse(String s) {
        return switch (s) {
            case "^" -> Up;
            case ">" -> Right;
            case "v" -> Down;
            case "<" -> Left;
            default -> throw new RuntimeException("unrecognized direction");
        };
    }
}
