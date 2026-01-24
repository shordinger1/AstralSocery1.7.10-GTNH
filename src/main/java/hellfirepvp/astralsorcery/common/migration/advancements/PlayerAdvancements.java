/*******************************************************************************
 * 1.7.10 Compatibility Stub - Advancement System
 * Advancements were introduced in Minecraft 1.12.
 * This is a stub for 1.7.10 compatibility.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration.advancements;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

/**
 * Stub for PlayerAdvancements (1.12+) for 1.7.10 compatibility.
 * In 1.7.10, achievements use a different system.
 */
public class PlayerAdvancements {

    private final EntityPlayerMP player;

    public PlayerAdvancements(EntityPlayerMP player) {
        this.player = player;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }

    // Stub methods - no-op in 1.7.10
    public void grantCriterion(ResourceLocation id) {
        // 1.7.10: Achievements use a different system
        // This is a no-op for compatibility
    }
}
