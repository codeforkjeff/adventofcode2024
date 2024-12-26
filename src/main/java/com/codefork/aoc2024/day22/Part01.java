package com.codefork.aoc2024.day22;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var result = MonkeyExchangeMarket.sum(data, 2000);
        return String.valueOf(result);
    }

    @Override
    public String solve() {
        Assert.assertEquals("37327623", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}

