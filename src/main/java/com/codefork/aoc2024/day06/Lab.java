package com.codefork.aoc2024.day06;

import com.codefork.aoc2024.util.WithIndex;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

/**
 * This solution is very FP impure, but it was too painful to do otherwise.
 * <p>
 * I started out creating a complete representation of the map as it's visually shown,
 * but it isn't necessary to store the information about every single location
 * on the map, just the ones we care about.
 */
public class Lab {

    public static class DuplicateException extends Exception {
        Path path;

        public DuplicateException(Path path) {
            super();
            this.path = path;
        }
    }

    enum Direction {
        Up, Right, Down, Left;

        public Direction turn() {
            return switch (this) {
                case Up -> Right;
                case Right -> Down;
                case Down -> Left;
                case Left -> Up;
            };
        }
    }

    public record Coordinate(int x, int y) {
        public Coordinate decX() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate incX() {
            return new Coordinate(x + 1, y);
        }

        public Coordinate decY() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate incY() {
            return new Coordinate(x, y + 1);
        }
    }

    // a coordinate and the direction used to visit it
    public record Path(Coordinate coord, Direction dir) {

    }

    private static final Pattern obstaclePat = Pattern.compile("#");

    public static Lab create(Stream<String> data) {
        var lab = data.map(WithIndex.indexed())
                .collect(foldLeft(
                        Lab::new,
                        (acc, item) -> {
                            var lineIdx = item.index();
                            var line = item.value();
                            var matcher = obstaclePat.matcher(line);
                            while (matcher.find()) {
                                var obsX = matcher.start();
                                acc.obstacles.add(new Coordinate(obsX, lineIdx));
                            }

                            acc.width = line.length();
                            acc.height = lineIdx + 1;

                            var guardIdx = line.indexOf("^");
                            if (guardIdx != -1) {
                                acc.guard = new Path(new Coordinate(guardIdx, lineIdx), acc.guard.dir);
                            }
                            return acc;
                        }));
        lab.start = lab.guard;
        try {
            lab.markVisited();
        } catch (DuplicateException e) {
            throw new RuntimeException(e);
        }
        return lab;
    }

    private String name = "main:";

    private int width = -1;
    private int height = -1;

    private Path start;
    private Path guard = new Path(new Coordinate(-1, -1), Direction.Up);
    private Set<Coordinate> obstacles = new HashSet<>();
    private Set<Path> visited = new HashSet<>();
    private Set<Coordinate> obstructionsToCreateLoops = new HashSet<>();

    private boolean isObstacle(Coordinate coord) {
        return obstacles.contains(coord);
    }

    private boolean atEdge() {
        return guard.coord().x() == 0 || guard.coord().x() == width - 1 ||
                guard.coord().y() == 0 || guard.coord().y() == height - 1;
    }

    /**
     * My mistake on my first pass was to copy the state of the Lab, place an obstruction, and continue
     * walking the map. That doesn't work, because the obstruction must exist from the START of that walk,
     * since it affects the traversal thus far. So testing an obstruction requires that start the guard from
     * the beginning every time.
     *
     * @param obstruction
     * @return
     */
    // returns the guard's first overlapping Path in a test run when an obstruction is placed at pos
    public Optional<Path> testObstruction(Coordinate obstruction) {
        Lab testLab = new Lab();
        testLab.name = "testObstruction";
        testLab.height = height;
        testLab.width = width;
        testLab.start = start;
        testLab.guard = start;
        testLab.obstacles = new HashSet<>(obstacles);
        testLab.obstacles.add(obstruction);

        // move until we encounter an exception, which indicates a loop
        while (!testLab.atEdge()) {
            try {
                testLab.move(false);
            } catch (DuplicateException e) {
                return Optional.of(e.path);
            }
        }
        return Optional.empty();
    }

    private void move(boolean tryObstructions) throws DuplicateException {
        var nextPos = switch (guard.dir()) {
            case Direction.Up -> guard.coord().decY();
            case Direction.Right -> guard.coord().incX();
            case Direction.Down -> guard.coord().incY();
            case Direction.Left -> guard.coord().decX();
        };

        if (isObstacle(nextPos)) {
            guard = new Path(guard.coord(), guard.dir().turn());
        } else {
            if (tryObstructions && visited.size() >= 2) {
                var resultOpt = testObstruction(nextPos);
                resultOpt.ifPresent(result -> {
                    obstructionsToCreateLoops.add(nextPos);
                });
            }
            guard = new Path(nextPos, guard.dir());
        }

        markVisited();
    }

    /**
     * Mark the current position as visited. if the current position is already visited,
     * raises an exception containing that position.
     */
    private void markVisited() throws DuplicateException {
        if (!visited.contains(guard)) {
            visited.add(guard);
        } else {
            throw new DuplicateException(guard);
        }
    }

    /**
     * count the unique coordinates visited
     */
    public int countCoordinatesVisited() {
        return visited.stream().map(p -> p.coord).collect(Collectors.toSet()).size();
    }

    public int countObstructions() {
        return obstructionsToCreateLoops.size();
    }

    public void patrol(boolean tryObstructions) {
        while (!atEdge()) {
            try {
                move(tryObstructions);
            } catch (DuplicateException e) {
                throw new RuntimeException(e);
            }
        }
        //print();
    }

    public void print() {
        var visitedCoords = visited.stream().map(Path::coord).collect(Collectors.toSet());
        for (var j = 0; j < height; j++) {
            for (var i = 0; i < width; i++) {
                var coord = new Coordinate(i, j);
                if (obstacles.contains(coord)) {
                    System.out.print("#");
                } else if (visitedCoords.contains(coord)) {
                    System.out.print("X");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }
}
