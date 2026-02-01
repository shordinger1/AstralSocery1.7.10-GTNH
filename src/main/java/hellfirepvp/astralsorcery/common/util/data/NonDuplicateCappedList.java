/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * NonDuplicateCappedList - List with size cap and no duplicates
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

/**
 * NonDuplicateCappedList - Capped list without duplicates (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Prevents duplicate entries</li>
 * <li>Has a maximum size cap</li>
 * <li>Random element selection</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * NonDuplicateCappedList&lt;String&gt; list = new NonDuplicateCappedList&lt;&gt;(10);
 * list.offerElement("test"); // Adds "test" if under cap and not duplicate
 * String random = list.getRandomElement();
 * </pre>
 *
 * @param <T> The element type
 */
public class NonDuplicateCappedList<T> implements Iterable<T> {

    protected static final Random rand = new Random();

    protected List<T> elements = new LinkedList<>();

    private int cap = 0;

    public NonDuplicateCappedList(int cap) {
        this.cap = cap;
    }

    /**
     * Add element if not duplicate and under cap
     *
     * @param element Element to add
     * @return true if added, false otherwise
     */
    public boolean offerElement(T element) {
        if (elements.size() + 1 > cap) return false;
        if (elements.contains(element)) return false;
        return elements.add(element);
    }

    /**
     * Get random element
     *
     * @return Random element, or null if empty
     */
    @Nullable
    public T getRandomElement() {
        if (elements.isEmpty()) return null;
        return elements.get(rand.nextInt(elements.size()));
    }

    /**
     * Get random element by chance
     * Lower fill level = higher chance to return null
     *
     * @param rand          Random instance
     * @param rngMultiplier Multiplier for chance calculation
     * @return Random element, or null
     */
    @Nullable
    public T getRandomElementByChance(Random rand, float rngMultiplier) {
        if (elements.isEmpty()) return null;
        if (Math.max(0, rand.nextInt(((int) ((cap - elements.size()) * rngMultiplier)) / 2 + 1)) == 0) {
            return getRandomElement();
        }
        return null;
    }

    /**
     * Remove element
     *
     * @param element Element to remove
     * @return true if removed
     */
    public boolean removeElement(T element) {
        return elements.remove(element);
    }

    /**
     * Clear all elements
     */
    public void clear() {
        this.elements.clear();
    }

    /**
     * Get current size
     *
     * @return Current size
     */
    public int getSize() {
        return elements.size();
    }

    /**
     * Get maximum cap
     *
     * @return Cap size
     */
    public int getCap() {
        return cap;
    }

    /**
     * Set maximum cap
     *
     * @param cap New cap size
     */
    public void setCap(int cap) {
        this.cap = cap;
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

}
