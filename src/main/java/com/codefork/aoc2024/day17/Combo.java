package com.codefork.aoc2024.day17;

public final class Combo extends Operand {
    public Combo(int value) {
        super(value);
    }

    @Override
    public int eval(Computer computer) {
        return switch (value) {
            case 0, 1, 2, 3 -> value;
            case 4 -> computer.a();
            case 5 -> computer.b();
            case 6 -> computer.c();
            default -> throw new RuntimeException("this should never happen");
        };
    }
}
