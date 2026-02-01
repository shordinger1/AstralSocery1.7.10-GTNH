/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk event handler - Applies perk effects to players
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Perk event handler - Applies constellation perk effects (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Mining speed bonuses (PerkGelu) - See PlayerUtils.getBreakSpeed()</li>
 * <li>Ranged damage bonuses (PerkVorux)</li>
 * <li>Night vision re-application (PerkUlteria)</li>
 * </ul>
 * <p>
 * <b>Attribute Types:</b>
 * <ul>
 * <li>astralsorcery.miningspeed - Mining speed multiplier</li>
 * <li>astralsorcery.rangeddamage - Ranged damage multiplier</li>
 * </ul>
 * <p>
 * <b>Note:</b> In 1.7.10, BreakSpeed event doesn't exist.
 * Mining speed bonuses need to be applied through PlayerUtils.getBreakSpeed() hook.
 */
public class PerkEventHandler {

    private static final String ATTR_TYPE_MINING_SPEED = "astralsorcery.miningspeed";
    private static final String ATTR_TYPE_RANGED_DAMAGE = "astralsorcery.rangeddamage";

    // Track last night vision tick to avoid re-applying too frequently
    private static final int NIGHT_VISION_TICK_INTERVAL = 100; // Re-apply every 5 seconds
    private java.util.Map<UUID, Integer> lastNightVisionTick = new java.util.HashMap<>();

    /**
     * Get mining speed modifier for a player
     * <p>
     * This method should be called from PlayerUtils.getBreakSpeed() to apply the bonus.
     *
     * @param player The player
     * @return The mining speed multiplier (1.0 = no bonus)
     */
    public static float getMiningSpeedModifier(EntityPlayer player) {
        if (player == null || player.worldObj.isRemote) {
            return 1.0F;
        }

        PlayerAttributeMap attrMap = PerkAttributeHelper.getMap(player, cpw.mods.fml.relauncher.Side.SERVER);
        if (attrMap == null) {
            return 1.0F;
        }

        float modifier = attrMap.getModifier(player, ATTR_TYPE_MINING_SPEED);
        if (modifier != 1.0F) {
            LogHelper.debug("Mining speed modifier for " + player.getCommandSenderName() + ": " + modifier);
        }
        return modifier;
    }

    /**
     * Apply ranged damage bonus from PerkVorux
     * <p>
     * Event: ArrowLooseEvent - Called when a player fires an arrow
     * <p>
     * Note: In 1.7.10, ArrowLooseEvent.charge is named differently.
     * The actual damage modification needs to be done in a LivingHurtEvent handler.
     */
    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        if (event.entityPlayer.worldObj.isRemote) {
            return; // Don't process on client
        }

        EntityPlayer player = event.entityPlayer;

        // Get player attribute map
        PlayerAttributeMap attrMap = PerkAttributeHelper.getMap(player, cpw.mods.fml.relauncher.Side.SERVER);
        if (attrMap == null) {
            return;
        }

        // Get ranged damage modifier
        float modifier = attrMap.getModifier(player, ATTR_TYPE_RANGED_DAMAGE);
        if (modifier != 1.0F) {
            // Log that we detected ranged damage bonus
            LogHelper.debug("Ranged damage modifier active for " + player.getCommandSenderName() + ": " + modifier);
            // Note: Actual damage modification is handled in LivingHurtEvent or arrow entity creation
        }
    }

    /**
     * Re-apply night vision from PerkUlteria periodically
     * <p>
     * Event: PlayerTickEvent - Called every tick for each player
     */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.worldObj.isRemote) {
            return; // Don't process on client
        }

        if (event.phase != TickEvent.Phase.END) {
            return; // Only process at end of tick
        }

        EntityPlayer player = event.player;
        UUID uuid = player.getUniqueID();

        // Check if it's time to re-apply night vision
        Integer lastTick = lastNightVisionTick.get(uuid);
        int currentTick = (int) player.worldObj.getTotalWorldTime();

        if (lastTick == null || (currentTick - lastTick) >= NIGHT_VISION_TICK_INTERVAL) {
            // Check if player has PerkUlteria
            PlayerProgress progress = ResearchManager.getProgress(player);
            if (progress != null) {
                // Check for night vision perk (cst_ulteria)
                for (hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk perk : progress.getUnlockedPerkData().keySet()) {
                    if (perk.getRegistryName().equals("cst_ulteria")) {
                        // Re-apply night vision
                        perk.applyPerk(player, cpw.mods.fml.relauncher.Side.SERVER);
                        lastNightVisionTick.put(uuid, currentTick);
                        LogHelper.debug("Re-applied night vision for " + player.getCommandSenderName());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Clean up night vision tick tracker when player logs out
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player.worldObj.isRemote) {
            return; // Don't process on client
        }

        UUID uuid = event.player.getUniqueID();
        lastNightVisionTick.remove(uuid);
        LogHelper.debug("Cleaned up night vision tracker for " + event.player.getCommandSenderName());
    }

}
