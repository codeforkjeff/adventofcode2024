package com.codefork.aoc2024.day17;

public abstract sealed class Operand permits Literal, Combo {
    protected final int value;

    public Operand(int value) {
        this.value = value;
    }

    public abstract int eval(Computer computer);
}
