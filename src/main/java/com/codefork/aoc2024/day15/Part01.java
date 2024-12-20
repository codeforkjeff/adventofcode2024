package com.codefork.aoc2024.day15;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var document = Document.parse(data, 1);
        var finalWarehouse = document.doMoves();
        return String.valueOf(finalWarehouse.getSumBoxCoordinates());
    }

    @Override
    public String solve() {
        Assert.assertEquals("2028", solve(getFileAsStream("small_sample")));
        Assert.assertEquals("10092", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}