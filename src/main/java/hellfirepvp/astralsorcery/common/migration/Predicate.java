/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * Predicate interface for boolean-valued functions (replaces Java 8's java.util.function.Predicate)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * Represents a predicate (boolean-valued function) of one argument.
 * This replaces Java 8's java.util.function.Predicate for 1.7.10 compatibility.
 */
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     */
    boolean test(T t);
}
