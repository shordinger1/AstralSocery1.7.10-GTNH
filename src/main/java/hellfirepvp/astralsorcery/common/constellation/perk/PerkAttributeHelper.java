/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk attribute helper - Manages player attribute maps
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Perk attribute helper - Manages player attribute maps (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Manages client and server attribute maps</li>
 * <li>Creates attribute maps on demand</li>
 * <li>Cleans up maps when player logs out</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Uses UUID for player identification</li>
 * <li>Separate maps for client and server side</li>
 * </ul>
 */
public class PerkAttributeHelper {

    private static final Map<UUID, PlayerAttributeMap> clientMaps = new HashMap<>();
    private static final Map<UUID, PlayerAttributeMap> serverMaps = new HashMap<>();

    /**
     * Get or create attribute map for player
     *
     * @param player The player
     * @param side   The side (CLIENT or SERVER)
     * @return The player's attribute map
     */
    public static PlayerAttributeMap getOrCreateMap(EntityPlayer player, Side side) {
        UUID uuid = player.getUniqueID();
        Map<UUID, PlayerAttributeMap> maps = side.isClient() ? clientMaps : serverMaps;

        PlayerAttributeMap map = maps.get(uuid);
        if (map == null) {
            map = new PlayerAttributeMap(side);
            maps.put(uuid, map);
            LogHelper.debug("Created new attribute map for " + player.getCommandSenderName() + " on " + side);
        }
        return map;
    }

    /**
     * Get attribute map for player (doesn't create if missing)
     *
     * @param player The player
     * @param side   The side (CLIENT or SERVER)
     * @return The player's attribute map, or null if not found
     */
    public static PlayerAttributeMap getMap(EntityPlayer player, Side side) {
        UUID uuid = player.getUniqueID();
        Map<UUID, PlayerAttributeMap> maps = side.isClient() ? clientMaps : serverMaps;
        return maps.get(uuid);
    }

    /**
     * Remove attribute map for player
     *
     * @param player The player
     * @param side   The side (CLIENT or SERVER)
     */
    public static void removeMap(EntityPlayer player, Side side) {
        UUID uuid = player.getUniqueID();
        Map<UUID, PlayerAttributeMap> maps = side.isClient() ? clientMaps : serverMaps;
        PlayerAttributeMap removed = maps.remove(uuid);
        if (removed != null) {
            LogHelper.debug("Removed attribute map for " + player.getCommandSenderName() + " on " + side);
        }
    }

    /**
     * Clear all attribute maps on a side
     *
     * @param side The side to clear
     */
    public static void clearAll(Side side) {
        Map<UUID, PlayerAttributeMap> maps = side.isClient() ? clientMaps : serverMaps;
        int count = maps.size();
        maps.clear();
        LogHelper.debug("Cleared " + count + " attribute maps on " + side);
    }

    /**
     * Get the number of active maps on a side
     *
     * @param side The side
     * @return Number of active maps
     */
    public static int getMapCount(Side side) {
        Map<UUID, PlayerAttributeMap> maps = side.isClient() ? clientMaps : serverMaps;
        return maps.size();
    }

}
