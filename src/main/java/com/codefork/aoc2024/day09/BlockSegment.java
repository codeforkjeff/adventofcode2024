package com.codefork.aoc2024.day09;

public abstract sealed class BlockSegment permits File, Free {

    private final int numBlocks;

    // this is the absolute position on the disk
    private final int position;

    public BlockSegment(int position, int numBlocks) {
        this.position = position;
        this.numBlocks = numBlocks;
    }

    public int getNumBlocks() {
        return numBlocks;
    }

    public int getPosition() {
        return position;
    }
}
