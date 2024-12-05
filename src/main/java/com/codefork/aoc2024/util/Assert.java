package com.codefork.aoc2024.util;

/**
 * assertions for runtime error checking
 */
public class Assert {

    public static void assertEquals(Object expected, Object actual) {
        if(!expected.equals(actual)) {
            throw new RuntimeException(String.format("assertion failed: expected=%s actual=%s", expected, actual));
        }
    }

}
