package com.codefork.aoc2024;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day03 {

    public static class Computer {
        private static final Pattern mulPattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
        private boolean enabled = true;
        private int sum = 0;
        private boolean recognizeConditionals = false;

        public void execute(String instruction) {
            var handled = false;
            var mulMatcher = mulPattern.matcher(instruction);
            if("do()".equals(instruction)) {
                enabled = true;
                handled = true;
            } else if ("don't()".equals(instruction)) {
                enabled = recognizeConditionals ? false : true;
                handled = true;
            }
            if(mulMatcher.find()) {
                if(enabled) {
                    var result = Integer.parseInt(mulMatcher.group(1)) * Integer.parseInt(mulMatcher.group(2));
                    sum += result;
                }
                handled = true;
            }
            if(!handled) {
                throw new RuntimeException("instruction not handled: " + instruction);
            }
        }

        public void setRecognizeConditionals(boolean recognizeConditionals) {
            this.recognizeConditionals = recognizeConditionals;
        }

        public int getSum() {
            return sum;
        }
    }

    public static int sumInstructionResults(Stream<String> input, boolean recognizeConditionals) {
        var pat = Pattern.compile("mul\\((\\d+),(\\d+)\\)|do\\(\\)|don't\\(\\)");
        var computer = new Computer();
        computer.setRecognizeConditionals(recognizeConditionals);
        input.forEach(line -> {
            var matcher = pat.matcher(line);
            while (matcher.find()) {
                var instruction = line.substring(matcher.start(), matcher.end());
                computer.execute(instruction);
            }
        });
        return computer.getSum();
    }

}
