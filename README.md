
# Advent of Code 2024

See https://adventofcode.com/2024

I've been doing Java on and off for 25 years (!). My goal with this year's AoC is to write the solutions
in "modern Java" as much as possible. This means using the newer APIs, streams, lambdas, functional programming
idioms, records, etc.

## How to run this

### Get the input files

The input files in `src/main/resources` are encrypted. If you're not me, you'll need
to download the relevant input files from the AoC website and replace the encrypted ones
in that directory.

### Run the code

You can run the Part* classes within IntelliJ interactively.

To run using the command line, install maven, then run `mvn package appassembler:assemble`
to build the application.

Run the main program, supplying the day and part you want to run as arguments:  

```sh
# runs day 2, part 1 
target/appassembler/bin/aoc2024 2 1
# runs day 3, both parts
target/appassembler/bin/aoc2024 3
# run everything
target/appassembler/bin/aoc2024 all
```

## Journal

12/6/2024 - So far I've been making good use of the functional programming features in Java.
But as the problems get more complicated (I'm at day 6), it's starting to get difficult to achieve
purity. Some pain points:

- Records are great, but they are not as full-featured as Scala's case classes. I miss
methods that help with [derived record creation](https://openjdk.org/jeps/468).

- Performant FP requires not just streams and lambdas, but also functional data structures. e.g.
efficient immutable lists that you can transform into another with an added or removed element. Using
the standard structures like ArrayLists and HashMaps without mutating them ends up looking super awkward
in the code and does a lot of copying in memory.

- Some conveniences in streams are missing, like "foldLeft" and "mapWithIndex".
