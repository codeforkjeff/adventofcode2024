
# Advent of Code 2024

See https://adventofcode.com/2024

I've been doing Java on and off for 25 years (!). My goal with this year's AoC is to write "modern Java"
as much as possible. This means using the newer APIs, streams, lambdas, functional programming idioms,
records, etc.

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
```
