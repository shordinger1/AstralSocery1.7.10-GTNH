/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tuple - Generic key-value pair
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

/**
 * Tuple - Key-Value pair (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Simple immutable key-value pair</li>
 * <li>Generic type support</li>
 * <li>Proper equals() and hashCode() implementation</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * Tuple&lt;String, Integer&gt; tuple = new Tuple&lt;&gt;("age", 25);
 * String key = tuple.getKey(); // "age"
 * Integer value = tuple.getValue(); // 25
 * </pre>
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class Tuple<K, V> {

    public final K key;
    public final V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple = (Tuple) o;
        return (key == null ? tuple.key == null : key.equals(tuple.key))
            && (value == null ? tuple.value == null : value.equals(tuple.value));
    }

    @Override
    public int hashCode() {
        int result = key == null ? 0 : key.hashCode();
        result = 31 * result + (value == null ? 0 : value.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Tuple{" + "key=" + key + ", value=" + value + '}';
    }
}
