/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Aevitas root perk - Root perk for Aevitas constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

/**
 * Aevitas root perk - Root perk for Aevitas constellation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Grants XP when placing blocks</li>
 * <li>Tracks recent block placements</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without BlockEvent (for now)</li>
 * <li>TODO: Add event listener when event system is ready</li>
 * </ul>
 */
public class AevitasRootPerk extends RootPerk {

    public AevitasRootPerk(IMajorConstellation constellation, int x, int y) {
        super(constellation, x, y);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // TODO: Register block place event listener when event system is ready
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // TODO: Unregister block place event listener
    }

}
