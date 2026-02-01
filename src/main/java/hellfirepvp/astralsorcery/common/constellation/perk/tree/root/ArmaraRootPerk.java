/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Armara root perk - Root perk for Armara constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

/**
 * Armara root perk - Root perk for Armara constellation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Grants XP when taking damage</li>
 * <li>Defensive constellation</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without LivingHurtEvent (for now)</li>
 * <li>TODO: Add event listener when event system is ready</li>
 * </ul>
 */
public class ArmaraRootPerk extends RootPerk {

    public ArmaraRootPerk(IMajorConstellation constellation, int x, int y) {
        super(constellation, x, y);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // TODO: Register living hurt event listener when event system is ready
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // TODO: Unregister living hurt event listener
    }

}
