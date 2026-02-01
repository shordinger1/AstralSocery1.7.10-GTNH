/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Major perk - Major perk type
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;

/**
 * Major perk - Major perk type (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Major category classification</li>
 * <li>No special logic (placeholder)</li>
 * </ul>
 */
public class MajorPerk extends AbstractPerk {

    /**
     * Create a new major perk
     *
     * @param name The perk name
     * @param x    X position in perk tree
     * @param y    Y position in perk tree
     */
    public MajorPerk(String name, int x, int y) {
        super(name, x, y);
        setCategory(CATEGORY_MAJOR);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // Override in subclasses
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // Override in subclasses
    }

}
