package com.codefork.aoc2024;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class Problem {

    /**
     * Solve this problem
     */
    public abstract String solve();

    /**
     * Get the problem input as a Stream of line strings
     */
    public Stream<String> getInput() {
        var packageNameParts = this.getClass().getPackageName().split("\\.");
        var dayStr = packageNameParts[packageNameParts.length - 1];
        var path = String.format("/%s/input", dayStr);
        var stream = this.getClass().getResourceAsStream(path);
        if(stream != null) {
            return new BufferedReader(new InputStreamReader(stream)).lines();
        }
        throw new RuntimeException("File doesn't exist: " + path);
    }

    /**
     * run the solution
     */
    public void run() {
        System.out.println(solve());
    }

}
