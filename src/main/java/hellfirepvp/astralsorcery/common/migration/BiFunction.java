/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * BiFunction interface for two-argument functions (replaces Java 8's java.util.function.BiFunction)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * Represents a function that accepts two arguments and produces a result.
 * This replaces Java 8's java.util.function.BiFunction for 1.7.10 compatibility.
 */
public interface BiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     */
    R apply(T t, U u);
}
