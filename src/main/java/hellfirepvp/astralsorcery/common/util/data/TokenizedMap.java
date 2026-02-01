/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TokenizedMap - HashMap with token-based values
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.HashMap;

/**
 * TokenizedMap - HashMap extension with token interface (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Extends HashMap with token-based value interface</li>
 * <li>Generic key and tokenized value types</li>
 * <li>Used for managing collections of tokenized objects</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * TokenizedMap&lt;String, MyToken&gt; map = new TokenizedMap&lt;&gt;();
 * map.put("key", new MyToken());
 * MyToken token = map.get("key");
 * MyValue value = token.getValue();
 * </pre>
 *
 * @param <K> The key type
 * @param <V> The value type, must extend MapToken
 */
public class TokenizedMap<K, V extends TokenizedMap.MapToken<?>> extends HashMap<K, V> {

    /**
     * Interface for tokenized values
     *
     * @param <V> The value type
     */
    public static interface MapToken<V> {

        /**
         * Get the underlying value
         *
         * @return The value
         */
        public V getValue();

    }

}
