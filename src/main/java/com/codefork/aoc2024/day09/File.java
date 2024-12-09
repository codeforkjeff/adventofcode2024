package com.codefork.aoc2024.day09;

public final class File extends BlockSegment {
    private final int id;

    public File(int id, int position, int numBlocks) {
        super(position, numBlocks);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
