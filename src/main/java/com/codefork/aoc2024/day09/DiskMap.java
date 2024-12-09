package com.codefork.aoc2024.day09;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * lots of imperative code here, there's just too much state that's easier to deal with
 * by mutation rather than transformation
 */
public class DiskMap {

    private final Map<Integer, BlockSegment> posToSegments;

    public DiskMap(String line) {
        posToSegments = new HashMap<>();
        var pos = 0;
        for(var i = 0; i < line.length(); i++) {
            var numBlocks = Integer.parseInt(line.substring(i, i + 1));
            if(numBlocks > 0) {
                if (i % 2 == 0) {
                    posToSegments.put(pos, new File(i / 2, pos, numBlocks));
                } else {
                    posToSegments.put(pos, new Free(pos, numBlocks));
                }
                pos += numBlocks;
            }
        }
    }

    public DiskMap(Map<Integer, BlockSegment> posToSegments) {
        this.posToSegments = posToSegments;
    }

    public Long checksum() {
        return posToSegments.values().stream()
                .reduce(0L, (acc, blockSegment) -> {
                    switch (blockSegment) {
                        case File file -> {
                            var sumForFile = IntStream
                                    .range(0, file.getNumBlocks())
                                    .boxed()
                                    .reduce(0L, (acc2, i) -> {
                                        var pos = file.getPosition() + i;
                                        //System.out.printf("%s * %s%n", pos, file.getId());
                                        return acc2 + (pos * (long) file.getId());
                                    }, Long::sum);
                            return acc + sumForFile;
                        }
                        case Free free -> {
                            return acc;
                        }
                    }
                }, Long::sum);
    }

    public static List<Free> getFreeSegments(Map<Integer, BlockSegment> posToSegments) {
        return new ArrayList<>(getOrderedSegments(posToSegments)
                .stream()
                .filter(s -> s instanceof Free)
                .map(s -> (Free) s)
                .toList()
        );
    }

    public static List<BlockSegment> getOrderedSegments(Map<Integer, BlockSegment> posToSegments) {
        return posToSegments
                .entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    if(e1.getKey() < e2.getKey()) {
                        return -1;
                    } else if (e1.getKey() > e2.getKey()) {
                        return 1;
                    }
                    return 0;
                })
                .map(Map.Entry::getValue)
                .toList();
    }

    public List<BlockSegment> getOrderedSegments() {
        return getOrderedSegments(this.posToSegments);
    }

    public List<File> getReversedFiles() {
        return new ArrayList<>(getOrderedSegments()
                .reversed()
                .stream()
                .filter(blockSegment -> blockSegment instanceof File)
                .map(blockSegment -> (File) blockSegment)
                .toList()
        );
    }

    public DiskMap compactByBlock() {

        var segmentsCopy = new HashMap<>(this.posToSegments);

        var freeSegments = getFreeSegments(segmentsCopy);

        var reversedFiles = getReversedFiles();

        for(var file : reversedFiles) {

            var remainingBlocksToMove = file.getNumBlocks();

            while(remainingBlocksToMove > 0) {
                // always get the first segment in our running list of freeSegments
                var free = freeSegments.get(0);

                // if ths Free segment is to the right of the file, give up on trying to fill those positions
                if(free.getPosition() > file.getPosition()) {
                    break;
                }

                // number of blocks we can fill in this loop iteration
                var blocksToFill = Math.min(free.getNumBlocks(), file.getNumBlocks());

                // update freeSegments
                freeSegments.remove(0);
                var remainingFree = free.getNumBlocks() - blocksToFill;
                // effectively replace the Free segment if there's still space free in the block
                if (remainingFree > 0) {
                    var newFree = new Free(free.getPosition() + blocksToFill, remainingFree);
                    freeSegments.add(0, newFree);
                }

                // replace the Free object with a File object, and pad remaining space with another
                // Free object if necessary
                var fillFile = new File(file.getId(), free.getPosition(), blocksToFill);
                segmentsCopy.remove(free.getPosition());
                segmentsCopy.put(fillFile.getPosition(), fillFile);
                if (remainingFree > 0) {
                    var newFree = new Free(free.getPosition() + blocksToFill, remainingFree);
                    segmentsCopy.put(newFree.getPosition(), newFree);
                }

                remainingBlocksToMove = remainingBlocksToMove - blocksToFill;

                // if we still have blocks to move, update the File in segmentsCopy
                // and point our "file" variable to our (smaller) File; otherwise we're done,
                // so remove the file from segmentsCopy
                if(remainingBlocksToMove > 0) {
                    var newFile = new File(file.getId(), file.getPosition(), remainingBlocksToMove);
                    segmentsCopy.put(newFile.getPosition(), newFile);
                    // file changes as we iterate
                    file = newFile;
                } else {
                    segmentsCopy.remove(file.getPosition());
                }
            }
        }

        return new DiskMap(segmentsCopy);
    }

    public DiskMap compactByFile() {
        // represent the disk map as a Map of positions to segments, so we can easily
        // move segments (i.e. add/remove)

        var segmentsCopy = new HashMap<>(this.posToSegments);

        var freeSegments = getFreeSegments(segmentsCopy);

        var reversedFiles = getReversedFiles();

        for(var file : reversedFiles) {

            for(var i = 0; i < freeSegments.size(); i++) {
                var free = freeSegments.get(i);
                // if ths Free segment is to the right of the file, give up on trying to move it
                if(free.getPosition() > file.getPosition()) {
                    break;
                }
                if(free.getNumBlocks() >= file.getNumBlocks()) {
                    var movedFile = new File(file.getId(), free.getPosition(), file.getNumBlocks());

                    // update freeSegments
                    freeSegments.remove(i);
                    if (free.getNumBlocks() > file.getNumBlocks()) {
                        var remainingBlocks = free.getNumBlocks() - file.getNumBlocks();
                        freeSegments.add(i, new Free(movedFile.getPosition() + movedFile.getNumBlocks(), remainingBlocks));
                    }

                    // update segmentsCopy
                    segmentsCopy.remove(file.getPosition());
                    segmentsCopy.put(movedFile.getPosition(), movedFile);

                    // end for loop
                    break;
                }
            }
        }
        return new DiskMap(segmentsCopy);
    }

    public void print(Map<Integer, BlockSegment> posToSegments) {
        for(var blockSegment : getOrderedSegments(posToSegments)) {
            for (var i = 0; i < blockSegment.getNumBlocks(); i++) {
                switch (blockSegment) {
                    case File file -> {
                        System.out.print("[" + file.getId() + "]");
                    }
                    case Free free -> {
                        System.out.print(".");
                    }
                }
            }
        }
        System.out.println();
    }

}
