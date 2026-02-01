/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Evorsio root perk - Root perk for Evorsio constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

/**
 * Evorsio root perk - Root perk for Evorsio constellation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Movement-focused constellation</li>
 * <li>Grants XP from movement</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>TODO: Implement movement tracking</li>
 * </ul>
 */
public class EvorsioRootPerk extends RootPerk {

    public EvorsioRootPerk(IMajorConstellation constellation, int x, int y) {
        super(constellation, x, y);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // TODO: Register movement tracking
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // TODO: Unregister movement tracking
    }

}
