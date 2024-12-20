package com.codefork.aoc2024.day17;

public final class Literal extends Operand {
    public Literal(int value) {
        super(value);
    }

    @Override
    public int eval(Computer computer) {
        return value;
    }
}
