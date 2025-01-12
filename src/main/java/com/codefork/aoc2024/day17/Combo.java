package com.codefork.aoc2024.day17;

public final class Combo extends Operand {
    public Combo(long value) {
        super(value);
    }

    @Override
    public long eval(Computer computer) {
//        return switch (value) {
//            case 0L, 1L, 2L, 3L -> value;
//            case 4L -> computer.a();
//            case 5L -> computer.b();
//            case 6L -> computer.c();
//            default -> throw new RuntimeException("this should never happen");
//        };
        if (value == 0L || value == 1L || value == 2L || value == 3L) {
            return value;
        } else if (value == 4L) {
            return computer.a();
        } else if (value == 5L) {
            return computer.b();
        } else if (value == 6L) {
            return computer.c();
        } else {
            throw new RuntimeException("this should never happen");
        }
    }
}
