package com.codefork.aoc2024.day09;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var line = data.toList().getFirst();

        var diskMap = new DiskMap(line);

        var compacted = diskMap.compactByFile();

        var result = compacted.checksum();

        return String.valueOf(result);
    }

    @Override
    public String solve() {
        Assert.assertEquals("2858", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}
