package com.codefork.aoc2024.day24;

import com.codefork.aoc2024.Problem;
import com.codefork.aoc2024.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public class Part02 extends Problem {

    public String solve(Stream<String> data) {
        var device = Device.parse(data);

        // this is just a bunch of exploratory code at the moment

        var xyz = List.of("x", "y", "z");
        // group xyz wires by two-digit numeric suffix
        var byPos = device.namesToWires().keySet().stream()
                .filter(name -> xyz.contains(name.substring(0, 1)))
                .collect(Collectors.groupingBy((name) -> name.substring(1, 3)));

        var positions = byPos.keySet().stream().sorted().toList();

        var badZ = new ArrayList<String>();

        // look through each bit position, calculate what it should be, and flag it if it's not what we expect
        var carry = 0;
        for(var pos : positions) {
            var z = device.getWire("z" + pos);
            if(device.hasWire("x" + pos)) {
                var x = device.getWire("x" + pos);
                var y = device.getWire("y" + pos);
                var posSum = x ^ y;
                var expected = posSum ^ carry; // if x and y differ, you get the carry
                var flag = (z != expected) ? "FLAGGED" : "";
                if (z != expected) {
                    badZ.add("z" + pos);
                }
                System.out.printf("%s carry=%s x=%s y=%s z=%s expected=%s  %s%n", pos, carry, x, y, z, expected, flag);
                carry = (expected == 0 & carry == 1) || ((x & y) == 1) ? 1 : 0;
            } else {
                System.out.printf("%s z=%s%n", pos, z);
            }
        }

        // badZ and its dependencies = swap candidates
        var candidates = new HashSet<String>();
        for(var zName : badZ) {
            var deps = device.getGateDependencies(zName);
            System.out.println(zName + " dependencies = " + deps);
            candidates.addAll(deps);
        }
        // this reduces the candidate pool, but only by about 50. doesn't seem like the optimal way to do this.
        System.out.println("total candidates=" + candidates.size());

        // figure out how to test every combination of 4 pair swaps in that set of candidates?
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
