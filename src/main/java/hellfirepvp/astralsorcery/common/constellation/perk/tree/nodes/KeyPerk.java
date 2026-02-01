/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Key perk - Special key perk type
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

/**
 * Key perk - Special key perk type (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Key category classification</li>
 * <li>Extended from MajorPerk</li>
 * </ul>
 */
public class KeyPerk extends MajorPerk {

    /**
     * Create a new key perk
     *
     * @param name The perk name
     * @param x    X position in perk tree
     * @param y    Y position in perk tree
     */
    public KeyPerk(String name, int x, int y) {
        super(name, x, y);
        setCategory(CATEGORY_KEY);
    }

}
