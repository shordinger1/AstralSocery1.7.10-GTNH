/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * BinaryOperator interface for two-argument operators (replaces Java 8's java.util.function.BinaryOperator)
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * Represents an operation upon two operands of the same type, producing a result of the same type.
 * This replaces Java 8's java.util.function.BinaryOperator for 1.7.10 compatibility.
 */
public interface BinaryOperator<T> {

    /**
     * Applies this operator to the given operands.
     */
    T apply(T left, T right);
}
