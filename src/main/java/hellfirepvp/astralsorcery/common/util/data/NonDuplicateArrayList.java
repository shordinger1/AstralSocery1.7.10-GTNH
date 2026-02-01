/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * NonDuplicateArrayList - ArrayList that prevents duplicate entries
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * NonDuplicateArrayList - ArrayList wrapper that prevents duplicates (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Wraps an ArrayList</li>
 * <li>Prevents duplicate entries on add()</li>
 * <li>Implements Collection interface</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * NonDuplicateArrayList&lt;String&gt; list = new NonDuplicateArrayList&lt;&gt;();
 * list.add("test"); // Adds "test"
 * list.add("test"); // Does nothing - already present
 * </pre>
 *
 * @param <E> The element type
 */
public class NonDuplicateArrayList<E> implements Collection<E> {

    private ArrayList<E> managed = new ArrayList<>();

    @Override
    public int size() {
        return managed.size();
    }

    @Override
    public boolean isEmpty() {
        return managed.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return managed.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return managed.iterator();
    }

    @Override
    public Object[] toArray() {
        return managed.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return managed.toArray(a);
    }

    @Override
    public boolean add(E e) {
        // Only add if not already present
        return !managed.contains(e) && managed.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return managed.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return managed.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            if (add(e)) changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return managed.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return managed.retainAll(c);
    }

    @Override
    public void clear() {
        managed.clear();
    }

}
