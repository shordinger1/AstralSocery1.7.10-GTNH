/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Vicio root perk - Root perk for Vicio constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

/**
 * Vicio root perk - Root perk for Vicio constellation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Ranged combat constellation</li>
 * <li>Grants XP from projectile attacks</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>TODO: Implement projectile event listeners</li>
 * </ul>
 */
public class VicioRootPerk extends RootPerk {

    public VicioRootPerk(IMajorConstellation constellation, int x, int y) {
        super(constellation, x, y);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // TODO: Register projectile event listeners
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // TODO: Unregister projectile event listeners
    }

}
