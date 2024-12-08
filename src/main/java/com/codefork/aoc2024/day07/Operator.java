package com.codefork.aoc2024.day07;

public enum Operator {
    Add {
        @Override
        public Long execute(Long n1, Long n2) {
            return n1 + n2;
        }
    },
    Multiply {
        @Override
        public Long execute(Long n1, Long n2) {
            return n1 * n2;
        }
    },
    Concat {
        @Override
        public Long execute(Long n1, Long n2) {
            return Long.parseLong(String.format("%s%s", n1, n2));
        }
    };

    public Long execute(Long n1, Long n2) {
        throw new RuntimeException("not implemented");
    }
}
