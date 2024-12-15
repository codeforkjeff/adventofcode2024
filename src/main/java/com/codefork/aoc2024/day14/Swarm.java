package com.codefork.aoc2024.day14;

import com.codefork.aoc2024.util.Maps;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codefork.aoc2024.util.FoldLeft.foldLeft;

public record Swarm(List<Robot> robots, int width, int height) {

    public record Position(int x, int y) {

    }

    public record Velocity(int dx, int dy) {

    }

    public record Robot(Position position, Velocity v) {
    }

    private final static Pattern pat = Pattern.compile("p=([0-9]+),([0-9]+) v=([0-9\\-]+),([0-9\\-]+)");

    public static Swarm create(Stream<String> data, int width, int height) {
        var robots = data
                .map(line -> {
                    var matcher = pat.matcher(line);
                    if (!matcher.find()) {
                        throw new RuntimeException("couldn't parse line");
                    }
                    var parts = IntStream.range(1, 5)
                            .mapToObj(matcher::group)
                            .map(Integer::valueOf)
                            .toList();
                    return new Robot(new Position(parts.get(0), parts.get(1)), new Velocity(parts.get(2), parts.get(3)));
                })
                .toList();
        return new Swarm(robots, width, height);
    }

    public Swarm doMoves(int n) {
        var finalRobots = IntStream.range(0, n)
                .boxed()
                .collect(foldLeft(
                        () -> robots,
                        (acc, i) -> {
                            return acc.stream().map(this::move).toList();
                        }
                ));
        return new Swarm(finalRobots, width, height);
    }

    public Robot move(Robot robot) {
        var changedX = robot.position().x() + robot.v().dx();
        var newX = changedX >= width ? changedX - width : (changedX < 0 ? changedX + width : changedX);
        var changedY = (robot.position().y() + robot.v().dy()) % height;
        var newY = changedY >= height ? changedY - height : (changedY < 0 ? changedY + height : changedY);
        return new Robot(new Position(newX, newY), robot.v());
    }

    public void print() {
        var byPos = robots().stream().collect(Collectors.toMap(
                Robot::position,
                List::of,
                Maps::listConcat));
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                var list = byPos.getOrDefault(new Position(x, y), List.of());
                var count = list.size();
                var ch = count > 0 ? String.valueOf(count) : ".";
                System.out.print(ch);
            }
            System.out.println();
        }
    }

    private static int DISCARD = -1;

    /**
     * quadrants are numbered in order from left to right, then top to bottom, like reading English
     * @return
     */
    public Map<Integer, Integer> getQuadrantCounts() {
        var byQuadrant = robots.stream()
                .collect(Collectors.groupingBy(
                        (robot) -> {
                            if (robot.position().y() < height / 2) {
                                if (robot.position().x() < width / 2) {
                                    return 0;
                                } else if (robot.position().x() > width / 2) {
                                    return 1;
                                }
                            } else if (robot.position().y() > height / 2) {
                                if (robot.position().x() < width / 2) {
                                    return 2;
                                } else if (robot.position().x() > width / 2) {
                                    return 3;
                                }
                            }
                            // put the robots on the center lines in a separate group
                            // that we'll discard
                            return DISCARD;
                        })
                );
        return byQuadrant.entrySet().stream()
                .filter(entry -> entry.getKey() != DISCARD)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size(),
                        Integer::sum
                ));
    }

    /**
     * testing for part 2: try smaller sections
     */
    public Map<Integer, Integer> getCustomSectionCounts() {
        var byQuadrant = robots.stream()
                .collect(Collectors.groupingBy(
                        (robot) -> {
                            if (robot.position().y() < height / 3) {
                                if (robot.position().x() < width / 2) {
                                    return 0;
                                } else if (robot.position().x() > width / 2) {
                                    return 1;
                                }
                            } else if (robot.position().y() > height / 3 && robot.position().y() < (height / 3) * 2) {
                                if (robot.position().x() < width / 2) {
                                    return 2;
                                } else if (robot.position().x() > width / 2) {
                                    return 3;
                                }
                            } else if (robot.position().y() > (height / 3) * 2) {
                                if (robot.position().x() < width / 2) {
                                    return 4;
                                } else if (robot.position().x() > width / 2) {
                                    return 5;
                                }
                            }
                            // put the robots on the center lines in a separate group
                            // that we'll discard
                            return DISCARD;
                        })
                );
        return byQuadrant.entrySet().stream()
                .filter(entry -> entry.getKey() != DISCARD)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            // count occupied positions rather than robots, since they can be stacked, and we only
                            // care about what they look like from above
                            record Pos(int x, int y) {
                            }
                            var list = entry.getValue();
                            var occupiedPositions = list.stream()
                                    .map(robot -> new Pos(robot.position().x(), robot.position().y()))
                                    .collect(Collectors.toSet());
                            return occupiedPositions.size();
                        },
                        Integer::sum
                ));
    }

    public int getSafetyFactor() {
        return getQuadrantCounts().entrySet().stream()
                .reduce(1, (acc, entry) -> acc * entry.getValue(), Integer::sum);
    }
}
