package com.codefork.aoc2024.day09;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {

        var line = data.toList().getFirst();

        var diskMap = new DiskMap(line);

//        var totalBlocks = diskMap.stream().map(BlockSegment::getNumBlocks).mapToLong(item -> (long) item).sum();
//
//        var totalDiskBlocks = diskMap.stream()
//                .filter(blockSegment -> blockSegment instanceof File)
//                .map(BlockSegment::getNumBlocks).mapToLong(item -> (long) item).sum();
//
//        System.out.println("totalBlocks=" + totalBlocks + ", totalDiskBlocks=" + totalDiskBlocks);
//
//        print(diskMap);

        var compacted = diskMap.compactByBlock();

        //print(compacted);

        var result = compacted.checksum();

        return String.valueOf(result);
    }

    @Override
    public String solve() {
        Assert.assertEquals("1928", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }
}
