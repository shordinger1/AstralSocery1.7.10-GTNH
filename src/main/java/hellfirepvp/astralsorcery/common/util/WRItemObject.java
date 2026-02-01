/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * WRItemObject - Wrapper for WeightedRandom items
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.WeightedRandom;

/**
 * WRItemObject - WeightedRandom Item wrapper (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Wraps a value with a weight for WeightedRandom selection</li>
 * <li>Used for random selection with weights</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * WRItemObject&lt;String&gt; item = new WRItemObject&lt;&gt;(10, "test");
 * WRItemObject&lt;String&gt; selected = WeightedRandom.getRandomItem(rand, list);
 * String value = selected.getValue();
 * </pre>
 *
 * @param <T> The type of the wrapped value
 */
public class WRItemObject<T> extends WeightedRandom.Item {

    private T object;

    public WRItemObject(int itemWeightIn, T value) {
        super(itemWeightIn);
        this.object = value;
    }

    /**
     * Get the wrapped value
     *
     * @return The wrapped value
     */
    public T getValue() {
        return object;
    }

}
