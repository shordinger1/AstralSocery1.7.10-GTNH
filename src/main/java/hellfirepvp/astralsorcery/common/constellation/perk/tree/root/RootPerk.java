/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Root perk - Root constellation perk entry point
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;

/**
 * Root perk - Root constellation perk entry point (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Links to a constellation</li>
 * <li>Serves as entry point for perk tree</li>
 * <li>No special effects (placeholder)</li>
 * </ul>
 */
public class RootPerk extends AbstractPerk {

    private final IConstellation constellation;

    /**
     * Create a root perk for a constellation
     *
     * @param constellation The constellation this root represents
     * @param x             X position in perk tree
     * @param y             Y position in perk tree
     */
    public RootPerk(IConstellation constellation, int x, int y) {
        super(constellation.getUnlocalizedName() + "_root", x, y);
        this.constellation = constellation;
        setCategory(CATEGORY_ROOT);
    }

    /**
     * Get the constellation this root represents
     */
    public IConstellation getConstellation() {
        return constellation;
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // Root perks don't have special effects
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // Root perks don't have special effects
    }

}
