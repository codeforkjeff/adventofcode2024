package com.codefork.aoc2024.day17;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Instruction {
    adv {
        @Override
        public int opcode() {
            return 0;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Combo(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var operandEvaluated = operand.eval(computer);
            var result = computer.a() / (int) Math.pow(2, operandEvaluated);
            return computer.withA(result).advanceIp();
        }
    },
    bxl {
        @Override
        public int opcode() {
            return 1;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Literal(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var operandEvaluated = operand.eval(computer);
            var result = computer.b() ^ operandEvaluated;
            return computer.withB(result).advanceIp();
        }
    },
    bst {
        @Override
        public int opcode() {
            return 2;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Combo(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var operandEvaluated = operand.eval(computer);
            var result = operandEvaluated % 8;
            return computer.withB(result).advanceIp();
        }
    },
    jnz {
        @Override
        public int opcode() {
            return 3;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Literal(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var operandEvaluated = operand.eval(computer);
            if(computer.a() != 0) {
                return computer.withIp(operandEvaluated);
            }
            return computer.advanceIp();
        }
    },
    bxc {
        @Override
        public int opcode() {
            return 4;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Literal(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var result = computer.b() ^ computer.c();
            return computer.withB(result).advanceIp();
        }
    },
    out {
        @Override
        public int opcode() {
            return 5;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Combo(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var operandEvaluated = operand.eval(computer);
            var result = operandEvaluated % 8;
            return computer.appendOutput(result).advanceIp();
        }
    },
    bdv {
        @Override
        public int opcode() {
            return 6;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Combo(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var operandEvaluated = operand.eval(computer);
            var result = computer.a() / (int) Math.pow(2, operandEvaluated);
            return computer.withB(result).advanceIp();
        }
    },
    cdv {
        @Override
        public int opcode() {
            return 7;
        }

        @Override
        public Operand parseOperand(int operand) {
            return new Combo(operand);
        }

        @Override
        public Computer apply(Operand operand, Computer computer) {
            var operandEvaluated = operand.eval(computer);
            var result = computer.a() / (int) Math.pow(2, operandEvaluated);
            return computer.withC(result).advanceIp();
        }
    };

    public abstract int opcode();

    public abstract Operand parseOperand(int operand);

    public abstract Computer apply(Operand operand, Computer computer);

    public static Map<Integer, Instruction> opcodesToInstructions() {
        return Arrays.stream(Instruction.values())
                .collect(Collectors.toMap(Instruction::opcode, (i) -> i));

    }
}
