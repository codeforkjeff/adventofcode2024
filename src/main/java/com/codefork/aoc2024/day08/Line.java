package com.codefork.aoc2024.day08;

public record Line(Antenna a1, Antenna a2) {

    public int deltaX() {
        return a2.pos().x() - a1.pos().x();
    }

    public int deltaY() {
        return a2.pos().y() - a1.pos().y();
    }

}
