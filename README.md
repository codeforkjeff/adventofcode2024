
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

### 12/6/2024

So far I've been making good use of the functional programming features in Java.
But as the problems get more complicated (I'm at day 6), it's starting to get difficult to achieve
purity. Some pain points:

- Records are great, but they are not as full-featured as Scala's case classes. I miss
methods that help with [derived record creation](https://openjdk.org/jeps/468).

- Performant FP requires not just streams and lambdas, but also functional data structures. e.g.
efficient immutable lists that you can transform into another with an added or removed element. Using
the standard structures like ArrayLists and HashMaps without mutating them ends up looking super awkward
in the code and does a lot of copying in memory.

- Some conveniences in streams are missing, like "foldLeft" and "mapWithIndex".

### 12/9/2024

In the course of being a bit irritated by trying to do pure FP in Java, I discovered [vavr](https://vavr.io/),
a library of functional data structures that seems largely inspired by Scala. People have strong opinions about 
it, both pro and con. As a concept, it looks pretty fantastic to me. I don't think I'll use it for AoC, at least
not this time around, but it's a nice discovery.

I almost always have to rewrite/refactor the part 1 solution to answer part 2 of puzzles. For day 10, however,
I had already written code to find all the unique trails for part 1, so I literally didn't need to do anything
except count them for part 2. I'll take that win!

### 12/11/2024

Day 11 part 2 is the first problem I had to sleep on. Initially I thought it was one of those where you don't actually
need to iterate, you could pre-calculate the resulting number of stones at a given evolution. I'm still not sure if that
could actually work. In the end, I simply figured out a more efficient representation for iteration. It worked nicely.

### 12/13/2024

I've been dreading the type of problem that came up in Day 13, where the only viable solution isn't based on
time- or spacing-saving algorithms, but on calculation. I won't spoil it here, but I'll just say it took me 
a little while to write out the equation.

I often declare records as members of the class when 1) they're only used by that class, 2) there's a bunch of them
that are related and work together, 3) it's nice to see them all in one place instead of in separate files.
Today I randomly discovered
[local classes](https://docs.oracle.com/javase/tutorial/java/javaOO/localclasses.html) and
[records](https://docs.oracle.com/en/java/javase/17/language/records.html). This allows you to scope, say, an intermediate record for a stream transformation, to a single method
and avoid clutter at the class level. Pretty cool!

### 12/15/2024

Day 14 Part 2 had me stumped, at least for now. Moving on to Day 15.

### 12/18/2024

This is the part of AoC when I start to fall behind. =P

Day 16 took me several days to do, when I could find time to work on it. I did actually intuit
what the best solution for part 1 probably was, but I was lazy and wasted time banging out a
naive solution, hoping it would be good enough. It wasn't; it worked for the sample data, but it
would run out of memory on the real input. So in the long run, it cost me more time than if I
went with the right solution from the start.

The biggest stumbling block was adapting the well-known algorithm (I won't give it away here,
it's in the code) to handle the direction of the reindeer in the maze, and accounting for that
in the cost of the weighted edges of the graph.

### 12/19/2024

Day 17 part 2 was... oof. I skipped it immediately, since I didn't want to get stuck 
on how to "reverse" those operations, which I think is how I should approach it. It'll require
a longer block of time on another day.

Considering how hard the problems are getting, I solved days 18 and 19 pretty damn quickly. 
Both of those were the ideal programming puzzles, in my view. Apply the right algorithms for
both parts and you're done. No tricks in the input data, no having to be clever about what 
the rules imply and calculating a "shortcut" solution. Just nice efficient coding.

### 12/20/2024

Day 20 was the first puzzle where a recursive method caused a StackOverflowError
when running it on the real input. This is yet another limit of trying to do pure FP in Java:
it doesn't have [tail call optimization](https://en.wikipedia.org/wiki/Tail_call).

### 12/26/2024

Unsurprisingly, I didn't finish AoC by or on Christmas. I do think this December's progress has
been better than my last attempt in 2018, at least. We'll see how long it takes me to wrap this all up. 

Day 23 was one of those problems where I took an entirely different approach in part 2 than part 1.
Usually when this happens, I "align" the two solutions so they re-use the same code. I started to do that
after finishing part 2 but the approaches are so algorithmically different that I decided it wasn't worth
the energy. 

(later that day)

Well, I've finished a solid pass through all the problems. Here's what I need to return to:
 
```
Day 14 part 2 = finding the christmas tree
Day 17 part 2 = quine-ish problem 
Day 21 part 2 = keypad problem
Day 24 part 2 = find 4 swaps of logic gates to make addition work
```

I think I can do 14 and 21, if I put my mind to it. Honestly, I'm not sure about the other two.
For those, I may ask my friend Rob who's also doing AoC this year, or look at reddit, instead of beating my
head against a wall. Which I did for WEEKS for some problems during AoC 2018. Which was not healthy.

(still later that day)

Hey I figured out day 14!

### 1/3/2025

I returned to day 21 and finished part 2. My initial naive attempt tried to generate the full strings of
keypad presses, which, unsurprisingly, caused out of memory errors. Then I had the idea to represent the
press sequences in a way that used less memory. After adding a shortcut after each robot's loop
iteration, it finishes in about 2 minutes on my laptop.

I suspect this is one of those puzzles where there's a pattern in the growth of the sequences, and you can
calculate the minimum size at n iterations without actually having to loop through them. I'm not that clever,
sadly.

I'm still proud of my solution. It was tricky to model properly, and there were lots of hairy one-to-many
transformations. I think it's pretty readable and easy to understand what's happening.

I'm running out of steam, so when I get around to it, I'm going to cheat for days 17 and 24 and see how someone
else solved them. Implementation is still helpful for learning!
