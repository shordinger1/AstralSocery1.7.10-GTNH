/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * Collector interface for collecting stream results (replaces Java 8's java.util.stream.Collector)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Compatibility interface for 1.7.10.
 * Represents an operation that accumulates input elements into a mutable result container.
 * This replaces Java 8's java.util.stream.Collector for 1.7.10 compatibility.
 */
public interface Collector<T, A, R> {

    /**
     * Returns a function that creates a new result container.
     */
    Supplier<A> supplier();

    /**
     * Returns a function that folds a value into a mutable result container.
     */
    BiConsumer<A, T> accumulator();

    /**
     * Returns a function that accepts two partial results and merges them.
     */
    BinaryOperator<A> combiner();

    /**
     * Returns a function that transforms the intermediate result to the final result.
     */
    Function<A, R> finisher();

    /**
     * Returns a set of collector characteristics.
     */
    Set<Characteristics> characteristics();

    /**
     * Characteristics for collectors.
     */
    public enum Characteristics {
        IDENTITY_FINISH
    }
}
