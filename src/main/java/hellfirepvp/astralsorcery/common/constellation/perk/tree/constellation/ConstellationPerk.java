/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Constellation perk - Perk linked to a constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;

/**
 * Constellation perk - Perk linked to a constellation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Linked to a specific constellation</li>
 * <li>Uses KEY category</li>
 * <li>Custom tree point rendering</li>
 * </ul>
 */
public abstract class ConstellationPerk extends AttributeModifierPerk {

    private IConstellation constellation;

    /**
     * Create a constellation perk
     *
     * @param name The perk name
     * @param cst  The constellation
     * @param x    X position in perk tree
     * @param y    Y position in perk tree
     */
    public ConstellationPerk(String name, IConstellation cst, int x, int y) {
        super(name, x, y);
        setCategory(CATEGORY_KEY);
        this.constellation = cst;
    }

    /**
     * Get the constellation this perk belongs to
     *
     * @return The constellation
     */
    public IConstellation getConstellation() {
        return constellation;
    }

}
