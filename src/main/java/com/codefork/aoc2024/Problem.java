package com.codefork.aoc2024;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public abstract class Problem {

    /**
     * Solve this problem given the data in the input stream
     */
    public abstract String solve();

    /**
     * Get the problem input as a Stream
     */
    public Stream<String> getInput() {
        var name = this.getClass().getSimpleName().toLowerCase().substring(0, 5);
        var path = String.format("/%s/input", name);
        var stream = this.getClass().getResourceAsStream(path);
        if(stream != null) {
            return new BufferedReader(new InputStreamReader(stream)).lines();
        }
        throw new RuntimeException("File doesn't exist: " + path);
    }

    /**
     * run the solution for both the sample dataset and the real dataset,
     * if the file for the latter is available. Real datasets
     * are stored at this path: datasets/rosalind_[PROBLEM_ID].txt
     */
    public void run() {
        System.out.println(solve());
    }

}
