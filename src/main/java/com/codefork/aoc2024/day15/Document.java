package com.codefork.aoc2024.day15;

import com.codefork.aoc2024.util.WithIndex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Document(Warehouse warehouse, List<Direction> moves) {

    private static final List<String> mapChars = List.of("O", "#", "@", ".");

    /**
     * @param data
     * @param width interpret walls and boxes to be as wide as this value
     * @return
     */
    public static Document parse(Stream<String> data, int width) {
        return data
                .map(WithIndex.indexed())
                .filter(lineWithIndex -> !lineWithIndex.value().isEmpty())
                .collect(foldLeft(
                        () -> new Document(new Warehouse(new HashSet<>(), new HashSet<>(), new Position(-1, -1)), new ArrayList<>()),
                        (acc, lineWithIndex) -> {
                            var y = lineWithIndex.index();
                            var line = lineWithIndex.value();
                            if (mapChars.stream().anyMatch(line::contains)) {
                                return line.chars()
                                        .boxed()
                                        .map(WithIndex.indexed())
                                        .collect(foldLeft(
                                                () -> acc,
                                                (acc2, chWithIndex) -> {
                                                    var x = chWithIndex.index() * width;
                                                    var ch = String.valueOf((char) chWithIndex.value().intValue());
                                                    var pos = new Position(x, y);
                                                    switch (ch) {
                                                        case "#" -> {
                                                            var newWalls = new HashSet<>(acc2.warehouse().walls());
                                                            for (var n = 0; n < width; n++) {
                                                                newWalls.add(new Position(pos.x() + n, pos.y()));
                                                            }
                                                            return new Document(acc2.warehouse().withWalls(newWalls), acc2.moves());
                                                        }
                                                        case "O" -> {
                                                            var newBoxes = new HashSet<>(acc2.warehouse().boxes());
                                                            newBoxes.add(new Box(pos, width));
                                                            return new Document(acc2.warehouse().withBoxes(newBoxes), acc2.moves());
                                                        }
                                                        case "@" -> {
                                                            return new Document(acc2.warehouse().withRobot(pos), acc2.moves());
                                                        }
                                                    }
                                                    return acc2;
                                                })
                                        );
                            } else {
                                return line.chars()
                                        .boxed()
                                        .collect(foldLeft(
                                                () -> acc,
                                                (acc2, charInt) -> {
                                                    var ch = String.valueOf((char) charInt.intValue());
                                                    var dir = Direction.parse(ch);
                                                    var newMoves = acc2.moves();
                                                    newMoves.add(dir);
                                                    return new Document(acc2.warehouse(), newMoves);
                                                })
                                        );
                            }
                        })
                );
    }

    /**
     * do all the moves and return the final state of the warehouse
     **/
    public Warehouse doMoves() {
        return moves().stream()
                .collect(foldLeft(
                        this::warehouse,
                        Warehouse::move)
                );
    }
}
