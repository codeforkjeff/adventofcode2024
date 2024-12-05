package com.codefork.aoc2024.day05;

/**
 * Represents the occurrence of two pages in a particular order,
 * not necessarily contiguous.
 * <p>
 * An update that consists of 1,2,3 has the ordered pages:
 * 1,2
 * 2,3
 * 1,3
 * <p>
 * An OrderedPages object can also be used to represent a rule.
 */
public record OrderedPages(int p1, int p2) {

    /**
     * switch around page 1 and page 2
     */
    public OrderedPages reverse() {
        return new OrderedPages(p2, p1);
    }
}
