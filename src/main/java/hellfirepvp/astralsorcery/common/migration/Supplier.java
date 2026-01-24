/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * Supplier interface for result-supplying functions (replaces Java 8's java.util.function.Supplier)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * Represents a supplier of results.
 * This replaces Java 8's java.util.function.Supplier for 1.7.10 compatibility.
 */
public interface Supplier<T> {

    /**
     * Gets a result.
     */
    T get();
}
