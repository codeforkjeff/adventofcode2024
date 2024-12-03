package com.codefork.aoc2024;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

    public static Map<String, Problem> getProblems() throws IOException {
        // adapted from https://stackoverflow.com/questions/1810614/getting-all-classes-from-a-package/1811120#1811120
        Map<String, Problem> problems = new HashMap<>();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null);

        StandardLocation location = StandardLocation.CLASS_PATH;
        String packageName = Main.class.getPackageName();
        Set<JavaFileObject.Kind> kinds = new HashSet<>();
        kinds.add(JavaFileObject.Kind.CLASS);
        boolean recurse = false;

        Iterable<JavaFileObject> list = fileManager.list(location, packageName,
                kinds, recurse);

        for (JavaFileObject classFile : list) {
            String name = classFile.getName().replaceAll(".*/|[.]class.*","");

            // skip inner classes
            if(name.contains("$"))
                continue;

            Class clazz;
            try {
                clazz = Class.forName(packageName + "." + name);
            } catch(ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // skip non-Problem classes
            if(Problem.class.equals(clazz) || !Problem.class.isAssignableFrom(clazz))
                continue;

            try {
                var problem = (Problem) clazz.getConstructors()[0].newInstance();
                problems.put(name, problem);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }
        return problems;
    }

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