/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * Function interface for single-argument functions (replaces Java 8's java.util.function.Function)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * Represents a function that accepts one argument and produces a result.
 * This replaces Java 8's java.util.function.Function for 1.7.10 compatibility.
 */
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     */
    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        return new Function<V, R>() {
            @Override
            public R apply(V v) {
                return Function.this.apply(before.apply(v));
            }
        };
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     */
    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        return new Function<T, V>() {
            @Override
            public V apply(T t) {
                return after.apply(Function.this.apply(t));
            }
        };
    }
}
