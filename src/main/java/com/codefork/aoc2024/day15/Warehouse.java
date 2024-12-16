package com.codefork.aoc2024.day15;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Warehouse(Set<Position> walls, Set<Box> boxes, Position robot) {

    public Warehouse withWalls(Set<Position> newWalls) {
        return new Warehouse(newWalls, boxes, robot);
    }

    public Warehouse withBoxes(Set<Box> newBoxes) {
        return new Warehouse(walls, newBoxes, robot);
    }

    public Warehouse withRobot(Position newRobot) {
        return new Warehouse(walls, boxes, newRobot);
    }

    /**
     * return true if any of the positions are walls
     */
    public boolean areWalls(List<Position> positions) {
        return positions.stream().anyMatch(walls::contains);
    }

    public Warehouse moveVertical(int dY) {
        var boxWidth = boxes.stream().findFirst().orElseThrow().width();
        var foundEmpty = false;
        // order matters for boxesToMove, so it's a List
        var boxesToMove = new ArrayList<Box>();
        var boxesToCheck = IntStream.range(0, boxWidth).boxed().map(i ->
            new Position(robot.x() - i, robot.y() + dY)
        ).toList();
        var wallsToCheck = List.of(new Position(robot.x(), robot.y() + dY));
        while (!areWalls(wallsToCheck)) {
            // check if boxesToCheck are boxes
            final var _toCheck = boxesToCheck;
            var foundBoxes = boxes.stream().filter(box -> _toCheck.contains(box.pos())).toList();
            // no boxes found? we're done, we can move!
            if(foundBoxes.isEmpty()) {
                foundEmpty = true;
                break;
            }
            boxesToMove.addAll(foundBoxes);
            // set boxesToCheck for the boxes we just found
            boxesToCheck = foundBoxes.stream().flatMap(box -> {
                // TODO: I hard coded these possibilities because I'm tired,
                // but they should really be calculated based on boxWidth
                if(boxWidth == 1) {
                    return Stream.of(box.pos().withChangedY(dY));
                } else if(boxWidth == 2) {
                    return Stream.of(
                            box.pos().withChangedY(dY).withChangedX(-1),
                            box.pos().withChangedY(dY),
                            box.pos().withChangedY(dY).withChangedX(1)
                    );
                }
                throw new RuntimeException("can't handle boxWidth=" + boxWidth);
            }).toList();
            wallsToCheck = foundBoxes.stream().flatMap(box -> {
                // TODO: I hard coded these possibilities because I'm tired,
                // but they should really be calculated based on boxWidth
                if(boxWidth == 1) {
                    return Stream.of(box.pos().withChangedY(dY));
                } else if(boxWidth == 2) {
                    return Stream.of(box.pos().withChangedY(dY), box.pos().withChangedY(dY).withChangedX(1));
                }
                throw new RuntimeException("can't handle boxWidth=" + boxWidth);
            }).toList();
        }
        if (foundEmpty) {
            var newBoxes = new HashSet<>(boxes);
            for (var box : boxesToMove.reversed()) {
                newBoxes.remove(box);
                newBoxes.add(new Box(new Position(box.pos().x(), box.pos().y() + dY), boxWidth));
            }
            return new Warehouse(walls, newBoxes, new Position(robot.x(), robot.y() + dY));
        } else {
            return this;
        }
    }

    public Warehouse moveHorizontal(int dX) {
        var boxWidth = boxes.stream().findFirst().orElseThrow().width();
        var foundEmpty = false;
        // order matters for boxesToMove, so it's a List
        var boxesToMove = new ArrayList<Box>();
        var boxToCheck = dX < 0 ?
                new Box(new Position(robot.x() + (dX * boxWidth), robot.y()), boxWidth) :
                new Box(new Position(robot.x() + dX, robot.y()), boxWidth);
        var wallsToCheck = List.of(new Position(robot.x() + dX, robot.y()));
        while (!areWalls(wallsToCheck)) {
            var isBox = boxes.contains(boxToCheck);
            //  not a box? we found empty space, so we can move
            if(!isBox) {
                foundEmpty = true;
                break;
            }
            boxesToMove.add(boxToCheck);

            boxToCheck = new Box(new Position(boxToCheck.pos().x() + (dX * boxWidth), boxToCheck.pos().y()), boxWidth);
            // TODO: I hard coded these possibilities because I'm tired,
            // but they should really be calculated based on boxWidth
            if(boxWidth == 1) {
                wallsToCheck = List.of(boxToCheck.pos());
            } else if (boxWidth == 2) {
                wallsToCheck = dX < 0 ?
                        List.of(boxToCheck.pos().withChangedX(1)) :
                        List.of(boxToCheck.pos());
            }
        }
        if (foundEmpty) {
            var newBoxes = new HashSet<>(boxes);
            for (var box : boxesToMove.reversed()) {
                newBoxes.remove(box);
                var moved = new Box(new Position(box.pos().x() + dX, box.pos().y()), boxWidth);
                newBoxes.add(moved);
            }
            return new Warehouse(walls, newBoxes, new Position(robot.x() + dX, robot.y()));
        } else {
            return this;
        }
    }

    /**
     * move the robot in the specified direction and return the new state of the warehouse
     * @param direction
     * @return
     */
    public Warehouse move(Direction direction) {
        var newWarehouse = switch (direction) {
            case Direction.Up -> moveVertical(-1);
            case Direction.Down -> moveVertical(1);
            case Direction.Right -> moveHorizontal(1);
            case Direction.Left -> moveHorizontal(-1);
        };
        //System.out.println("moved " + direction + ":");
        //newWarehouse.print();
        return newWarehouse;
    }

    public int getSumBoxCoordinates() {
        return boxes.stream()
                .map(box -> 100 * box.pos().y() + box.pos().x())
                .mapToInt(i -> i)
                .sum();
    }

    public void print() {
        int height = walls.stream().map(Position::y).max(Integer::compareTo).orElseThrow() + 1;
        int width = walls.stream().map(Position::x).max(Integer::compareTo).orElseThrow() + 1;

        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                var pos = new Position(x, y);
                if (walls.contains(pos)) {
                    System.out.print("#");
                } else if (boxes.contains(new Box(pos, 1))) {
                    System.out.print("O");
                } else if (boxes.contains(new Box(pos, 2))) {
                    System.out.print("[]");
                    x += 1;
                } else if (pos.equals(robot)) {
                    System.out.print("@");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }
}
