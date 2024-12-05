package com.codefork.aoc2024.day05;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.stream.Stream;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        final var updatePackage = UpdatePackage.create(data);

        final var total = updatePackage
                .getIncorrectlyOrderedUpdates()
                .stream()
                .map(updatePackage::reorderUpdate)
                .reduce(0, (acc, update) -> {
                    // make sure there's an odd number of pages
                    Assert.assertEquals(1, update.pages().size() % 2);
                    final var midIdx = (update.pages().size() - 1) / 2;
                    return acc + update.pages().get(midIdx);
                }, Integer::sum);

        return String.valueOf(total);
    }

    @Override
    public String solve() {
        Assert.assertEquals("123", solve(getSampleInput()));
        return solve(getInput());
    }

    public static void main(String[] args) {
        new Part02().run();
    }
}
