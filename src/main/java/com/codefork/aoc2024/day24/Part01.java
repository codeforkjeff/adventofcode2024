package com.codefork.aoc2024.day24;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public class Part01 extends Problem {

    public String solve(Stream<String> data) {
        var device = Device.parse(data);
        var zNames = device.namesToWires().keySet().stream()
                .filter(name -> name.startsWith("z"))
                .sorted()
                .toList()
                .reversed();
        var result = zNames.stream().collect(
                foldLeft(
                        () -> 0L,
                        (acc, name) -> (acc << 1) | device.getWire(name)
                )
        );
        return String.valueOf(result);
    }

    @Override
    public String solve() {
        Assert.assertEquals("2024", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part01().run();
    }

}
