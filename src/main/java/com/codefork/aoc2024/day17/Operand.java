package com.codefork.aoc2024.day17;

public abstract sealed class Operand permits Literal, Combo {
    protected final long value;

    public Operand(long value) {
        this.value = value;
    }

    public abstract long eval(Computer computer);
}
