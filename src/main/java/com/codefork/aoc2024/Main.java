package com.codefork.aoc2024;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ");
            System.out.println("    aoc2024 [DAY_NUMBER] [PART_NUMBER]");
            System.out.println("    aoc2024 all");
            System.exit(1);
        }
        final var runAll = "all".equals(args[0]);
        final var days = runAll
                ? IntStream.range(0, 25).boxed().toList()
                : List.of(Integer.parseInt(args[0]));
        final var parts = args.length > 1
                ? List.of(Integer.parseInt(args[1]))
                : Arrays.asList(1, 2);

        days.forEach(day -> {
            parts.forEach(part -> {
                final var className = Main.class.getPackageName() + String.format(".day%02d.Part%02d", day, part);

                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    if(runAll) {
                        return;
                    } else {
                        System.err.println("ERROR: Couldn't find class, does it exist?");
                        System.exit(1);
                    }
                }

                try {
                    System.out.println("Running " + className);
                    Problem problem = (Problem) clazz.getConstructors()[0].newInstance();
                    problem.run();
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            });
        });
    }

}