package com.codefork.aoc2024.day17;

public final class Literal extends Operand {
    public Literal(long value) {
        super(value);
    }

    @Override
    public long eval(Computer computer) {
        return value;
    }
}
