package com.codefork.aoc2024;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class Problem {

    /**
     * Solve this problem
     */
    public abstract String solve();

    public void checkEncrypted(String path) {
        var stream = this.getClass().getResourceAsStream(path);
        var headerSize = 9;
        var header = new byte[headerSize];
        var buf = new BufferedInputStream(stream);
        try {
            var numBytesRead = buf.read(header, 0, headerSize);
            if("\0GITCRYPT".equals(new String(header, "UTF-8"))) {
                throw new RuntimeException(
                        String.format("%s is encrypted with git-crypt, unlock the repo or replace " +
                                "that file with your own unencrypted copy. you'll need to rebuild the app.", path));
            }
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns contents of a resource file as a stream of line strings
     */
    public Stream<String> getFileAsStream(String filename) {
        var packageNameParts = this.getClass().getPackageName().split("\\.");
        var dayStr = packageNameParts[packageNameParts.length - 1];
        var path = String.format("/%s/%s", dayStr, filename);

        checkEncrypted(path);

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
