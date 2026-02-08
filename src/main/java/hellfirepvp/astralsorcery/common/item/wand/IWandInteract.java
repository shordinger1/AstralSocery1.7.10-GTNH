/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Wand Interaction Interface - Blocks that interact with wands
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Interface for blocks that can interact with wands (1.7.10)
 * <p>
 * Blocks implementing this interface can be right-clicked with a wand
 * to trigger special interactions.
 * <p>
 * <b>Examples:</b>
 * <ul>
 * <li>BlockCollectorCrystal - Wand highlights crystal</li>
 * <li>BlockTranslucent - Wand toggles visibility</li>
 * <li>TileAltar - Wand opens recipe viewer</li>
 * </ul>
 */
public interface IWandInteract {

    /**
     * Called when a player right-clicks with a wand
     *
     * @param world    The world
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param player   The player using the wand
     * @param side     The side clicked
     * @param sneaking Whether the player is sneaking
     */
    void onInteract(World world, int x, int y, int z, net.minecraft.entity.player.EntityPlayer player,
        ForgeDirection side, boolean sneaking);
}
