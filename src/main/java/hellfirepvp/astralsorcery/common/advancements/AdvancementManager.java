package hellfirepvp.astralsorcery.common.advancements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import hellfirepvp.astralsorcery.common.migration.advancements.PlayerAdvancements;

/**
 * Helper class to manage PlayerAdvancements for 1.7.10 compatibility.
 * In 1.12+, PlayerAdvancements is managed by the server and accessed via EntityPlayerMP.getAdvancements().
 * In 1.7.10, we need to manage this manually.
 */
public class AdvancementManager {

    private static final Map<EntityPlayerMP, PlayerAdvancements> PLAYER_ADVANCEMENTS = new ConcurrentHashMap<>();

    /**
     * Get PlayerAdvancements for a player.
     * In 1.7.10, we create and cache these manually.
     */
    public static PlayerAdvancements getAdvancements(EntityPlayerMP player) {
        // 1.7.10: computeIfAbsent and method references not available
        if (!PLAYER_ADVANCEMENTS.containsKey(player)) {
            PLAYER_ADVANCEMENTS.put(player, new PlayerAdvancements(player));
        }
        return PLAYER_ADVANCEMENTS.get(player);
    }

    /**
     * Clean up when a player logs out.
     */
    public static void removePlayer(EntityPlayerMP player) {
        PLAYER_ADVANCEMENTS.remove(player);
    }

    /**
     * Get all players with advancements.
     */
    public static Map<EntityPlayerMP, PlayerAdvancements> getAll() {
        return PLAYER_ADVANCEMENTS;
    }
}
