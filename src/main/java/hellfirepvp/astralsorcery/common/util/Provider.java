/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Simple provider interface for lazy evaluation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

/**
 * Provider - Simple functional interface (1.7.10)
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * Provider&lt;String&gt; nameProvider = new Provider&lt;String&gt;() {
 * 
 *     public String provide() {
 *         return "Hello";
 *     }
 * };
 * String name = nameProvider.provide();
 * </pre>
 * <p>
 * This is used for:
 * <ul>
 * <li>Lazy initialization</li>
 * <li>Conditional visibility</li>
 * <li>Dynamic GUI creation</li>
 * <li>Callback-based systems</li>
 * </ul>
 *
 * @param <T> The type of value provided
 */
public interface Provider<T> {

    /**
     * Provide a value
     *
     * @return The provided value
     */
    T provide();
}
