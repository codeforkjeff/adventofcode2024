package com.codefork.aoc2024.day17;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Computer(int a, int b, int c, int ip, List<Integer> program, String output) {

    private static final Map<Integer, Instruction> opcodesToInstructions = Instruction.opcodesToInstructions();

    private static final Pattern registerPat = Pattern.compile("Register ([ABC]): (\\d+)");
    private static final Pattern programPat = Pattern.compile("Program: ([\\d,]+)");

    public static Computer parse(Stream<String> data) {
        return data.collect(foldLeft(
                () -> new Computer(0, 0, 0, 0, Collections.emptyList(), ""),
                (acc, line) -> {
                    var lineMatcher = registerPat.matcher(line);
                    var progMatcher = programPat.matcher(line);
                    if(lineMatcher.find()) {
                        var reg = lineMatcher.group(1);
                        var value = Integer.parseInt(lineMatcher.group(2));
                        return switch(reg) {
                            case "A" -> acc.withA(value);
                            case "B" -> acc.withB(value);
                            case "C" -> acc.withC(value);
                            default -> throw new RuntimeException("unrecognized register: " + reg);
                        };
                    } else if(progMatcher.find()) {
                        var programStr = progMatcher.group(1);
                        var program = Arrays.stream(programStr.split(",")).map(Integer::parseInt).toList();
                        return acc.withProgram(program);
                    } else {
                        return acc;
                    }
                })
        );
    }

    public Computer withA(int newA) {
        return new Computer(newA, b, c, ip, program, output);
    }

    public Computer withB(int newB) {
        return new Computer(a, newB, c, ip, program, output);
    }

    public Computer withC(int newC) {
        return new Computer(a, b, newC, ip, program, output);
    }

    public Computer withIp(int newIp) {
        return new Computer(a, b, c, newIp, program, output);
    }

    public Computer withProgram(List<Integer> newProgram) {
        return new Computer(a, b, c, ip, newProgram, output);
    }

    // advance by 2, for "normal" instructions
    public Computer advanceIp() {
        return new Computer(a, b, c, ip + 2, program, output);
    }

    public Computer appendOutput(int item) {
        var newOutput = output + (!output.isEmpty() ? "," : "") + item;
        return new Computer(a, b, c, ip, program, newOutput);
    }

    public Computer run() {
        var state = this;
        while(state.ip() <= state.program.size() - 2) {
            var opcode = state.program().get(state.ip());
            var rawOperand = state.program().get(state.ip()+1);
            var instruction = opcodesToInstructions.get(opcode);
            var operand = instruction.parseOperand(rawOperand);
            state = instruction.apply(operand, state);
        }
        return state;
    }
}
