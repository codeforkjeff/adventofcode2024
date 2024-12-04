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
     * Returns contents of a resource file as a stream of line strings
     */
    public Stream<String> getFileAsStream(String filename) {
        var packageNameParts = this.getClass().getPackageName().split("\\.");
        var dayStr = packageNameParts[packageNameParts.length - 1];
        var path = String.format("/%s/%s", dayStr, filename);
        var stream = this.getClass().getResourceAsStream(path);
        if(stream != null) {
            return new BufferedReader(new InputStreamReader(stream)).lines();
        }
        throw new RuntimeException("File doesn't exist: " + path);
    }

    /**
     * Get the problem input as a Stream of line strings
     */
    public Stream<String> getInput() {
        return getFileAsStream("input");
    }

    /**
     * The "sample input" is what's in the problem statement.
     */
    public Stream<String> getSampleInput() {
        return getFileAsStream("sample");
    }

    /**
     * run the solution
     */
    public void run() {
        System.out.println(solve());
    }

}
