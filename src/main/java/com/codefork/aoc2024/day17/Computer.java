package com.codefork.aoc2024.day17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Computer(long a, long b, long c, int ip, List<Integer> program, List<Integer> output) {

    private static final Map<Integer, Instruction> opcodesToInstructions = Instruction.opcodesToInstructions();

    private static final Pattern registerPat = Pattern.compile("Register ([ABC]): (\\d+)");
    private static final Pattern programPat = Pattern.compile("Program: ([\\d,]+)");

    public static Computer parse(Stream<String> data) {
        return data.collect(foldLeft(
                () -> new Computer(0, 0, 0, 0, Collections.emptyList(), Collections.emptyList()),
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

    public Computer withA(long newA) {
        return new Computer(newA, b, c, ip, program, output);
    }

    public Computer withB(long newB) {
        return new Computer(a, newB, c, ip, program, output);
    }

    public Computer withC(long newC) {
        return new Computer(a, b, newC, ip, program, output);
    }

    public Computer withIp(int newIp) {
        return new Computer(a, b, c, newIp, program, output);
    }

    public Computer withProgram(List<Integer> newProgram) {
        return new Computer(a, b, c, ip, newProgram, output);
    }

    public Computer clearOutput() {
        return new Computer(a, b, c, ip, program, Collections.emptyList());
    }

    // advance by 2, for "normal" instructions
    public Computer advanceIp() {
        return new Computer(a, b, c, ip + 2, program, output);
    }

    public Computer appendOutput(int item) {
        //var newOutput = output + (!output.isEmpty() ? "," : "") + item;
        var newOutput = new ArrayList<>(output);
        newOutput.add(item);
        return new Computer(a, b, c, ip, program, newOutput);
    }

    public String getOutputAsString() {
        return output.stream().map(i -> i.toString()).collect(Collectors.joining(","));
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

    /**
     * keep running until output matches the program; if it starts to mismatch at any point,
     * return an empty optional
     */
    public Optional<Computer> runUntilMatch() {
        var state = this;
        var mismatch = false;
        while(state.ip() <= state.program.size() - 2 && !mismatch) {
            var opcode = state.program().get(state.ip());
            var rawOperand = state.program().get(state.ip()+1);
            var instruction = opcodesToInstructions.get(opcode);
            var operand = instruction.parseOperand(rawOperand);
            state = instruction.apply(operand, state);
            //System.out.println(state);

            if(instruction.equals(Instruction.out)) {
                var sizeLimit = Math.min(state.output().size(), state.program().size());
//                System.out.println(state.output().size() + " " + state.program().size());
//                System.out.println("sizeLimit=" + sizeLimit);
                for(var i = 0; i < sizeLimit && !mismatch; i++) {
                    if(!state.output().get(i).equals(state.program().get(i))) {
                        mismatch = true;
                    }
                }
            }
            if(!mismatch && state.output().size() == state.program().size()) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}

