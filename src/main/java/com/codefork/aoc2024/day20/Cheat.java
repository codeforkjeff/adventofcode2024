package com.codefork.aoc2024.day20;

public record Cheat(Position start, Position end, int saved) {
    public Cheat withSaved(int newSaved) {
        return new Cheat(start, end, newSaved);
    }
}
