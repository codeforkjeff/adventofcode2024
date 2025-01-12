package com.codefork.aoc2024.day17;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var computer = Computer.parse(data).withA(35184372088831L);
        while(true) {
            var result = computer.run(); //UntilMatch();
//            if(result.isPresent()) {
//                return result.get().getOutputAsString();
//            }
            if(result.program().equals(result.output())) {
                return String.valueOf(computer.a());
            }
            System.out.println(computer.a() + " = " + result);
            computer = computer.clearOutput().withA(computer.a() + 2000000);
//            if(computer.a() % 1000000 == 0) {
//                System.out.println(computer.a() + " = " + result);
//            }
        }

// -------------------------------
        // output with 16 numbers begins at a=35184372088832,
        // 17 numbers begins at 281474976710656.
        // brute force would need to scan 246290604621824 numbers.

//        var computer = Computer.parse(data).withA(35184372088831L);
//
//        //search(computer);
//
//        while(true) {
//            var result = computer.run();
//            System.out.println(computer.a() + " = " + result);
//            if(result.output().equals(result.program())) {
//                return String.valueOf(computer.a());
//            }
//            //computer = computer.withA((computer.a() * 2) - 1);
//            computer = computer.clearOutput().withA(computer.a() +1);
//        }
    }

    public void search(Computer computer) {
        var lower = 0L;
        var upper = Long.MAX_VALUE;
        long mid = 0L;
        var found = false;

        while (!found) {
            mid = (lower + upper) / 2;
            var result = computer.withA(mid).run();
            System.out.println(computer.a() + " = " + result);
            if(result.output().size() < result.program().size()) {
                upper = mid;
            } else if(result.output().size() > result.program().size()) {
                lower = mid;
            } else {
                System.out.println("finished? " + mid);
                found = true;
            }
        }
    }

    @Override
    public String solve() {
        //Assert.assertEquals("0,3,5,4,3,0", solve(getFileAsStream("sample2")));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
