package com.codefork.aoc2024.day22;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Part02 extends Problem {

    public String seqToString(List<Integer> seq) {
        return seq.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public MonkeyExchangeMarket.Sequence solve(Stream<String> data) {
        return MonkeyExchangeMarket.findSequence(data, 2000);
    }

    @Override
    public String solve() {
        var sampleSeq = solve(getFileAsStream("sample2"));
        Assert.assertEquals("-2,1,-1,3", seqToString(sampleSeq.seq()));
        Assert.assertEquals(23, sampleSeq.totalBananas());
        return String.valueOf(solve(getInput()).totalBananas());
    }

    public static void main(String[] args) {
        new Part02().run();
    }

}

