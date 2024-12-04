package com.codefork.aoc2024.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Taken from <a href="https://event-driven.io/en/how_to_write_left_fold_collector_in_java/">this page</a>
 * <p>
 * This is a custom collector that implements foldLeft: essentially, reducing a stream to a
 * different type from the contents of that stream. The accumulator function can be non-associative.
 * </p>
 */
public class FoldLeft<Entity, Event> implements Collector<Event, AtomicReference<Entity>, Entity> {

    private final Supplier<Entity> getInitial;
    private final BiFunction<Entity, Event, Entity> evolve;

    public FoldLeft(Supplier<Entity> getInitial, BiFunction<Entity, Event, Entity> evolve) {
        this.getInitial = getInitial;
        this.evolve = evolve;
    }

    public static <Entity, Event> FoldLeft<Entity, Event> foldLeft(
            Supplier<Entity> getInitial,
            BiFunction<Entity, Event, Entity> evolve
    ) {
        return new FoldLeft<>(getInitial, evolve);
    }


    @Override
    public Supplier<AtomicReference<Entity>> supplier() {
        return () -> new AtomicReference<>(getInitial.get());
    }

    @Override
    public BiConsumer<AtomicReference<Entity>, Event> accumulator() {
        return (wrapper, event) -> wrapper.set(evolve.apply(wrapper.get(), event));
    }

    @Override
    public BinaryOperator<AtomicReference<Entity>> combiner() {
        return (left, right) -> {
            left.set(right.get());
            return left;
        };
    }

    @Override
    public Function<AtomicReference<Entity>, Entity> finisher() {
        return AtomicReference::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }
}