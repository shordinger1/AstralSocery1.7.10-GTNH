/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Discidia root perk - Root perk for Discidia constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

/**
 * Discidia root perk - Root perk for Discidia constellation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Combat-focused constellation</li>
 * <li>Grants XP from combat</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>TODO: Implement combat event listeners</li>
 * </ul>
 */
public class DiscidiaRootPerk extends RootPerk {

    public DiscidiaRootPerk(IMajorConstellation constellation, int x, int y) {
        super(constellation, x, y);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // TODO: Register combat event listeners
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // TODO: Unregister combat event listeners
    }

}
