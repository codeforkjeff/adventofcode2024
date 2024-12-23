package com.codefork.aoc2024.day21;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
//        return String.valueOf(ShipLock.calculateSumOfComplexities(data, 25));

        var navigator = new ShipLock.DirectionalKeypadNavigator("A");

        // see what happens to a single move through 25 generations.
        // it doesn't even get past 4.
//        var pressSeqList = List.of("^");
//        for(var i = 0; i < 25; i++) {
//            System.out.println("i=" + i);
//            var newPressSeqList = new ArrayList<String>();
//            for (var pressSeq : pressSeqList) {
//                var possible = navigator.getPossiblePressSequences(pressSeq);
//                newPressSeqList.addAll(possible);
//            }
//            System.out.println(newPressSeqList);
//            pressSeqList = newPressSeqList;
//        }

        return "UNFINISHED";
    }

    @Override
    public String solve() {
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
