package com.codefork.aoc2024;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: aoc2024 DAY_NUMBER PART_NUMBER");
            System.exit(1);
        }
        var day = Integer.parseInt(args[0]);
        var parts = new ArrayList<Integer>();
        if(args.length > 1) {
            parts.add(Integer.parseInt(args[1]));
        } else {
            parts.add(1);
            parts.add(2);
        }

        for(var part : parts) {
            var className = Main.class.getPackageName() + String.format(".day%02d.Part%02d", day, part);
            System.out.println("Running " + className);

            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch(ClassNotFoundException e) {
                System.err.println("ERROR: Couldn't find class, does it exist?");
                System.exit(1);
            }

            Problem problem = null;
            try {
                problem = (Problem) clazz.getConstructors()[0].newInstance();
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
            problem.run();
       }
    }

}