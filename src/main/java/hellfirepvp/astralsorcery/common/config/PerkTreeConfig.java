/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;

/**
 * Configuration for Perk Tree modifications
 * Replaces the CraftTweaker PerkTree class
 * This is a code-based configuration system instead of script-based
 */
public class PerkTreeConfig {

    private static List<String> removedPerks = Lists.newLinkedList();
    private static List<String> disabledPerks = Lists.newLinkedList();
    private static Map<String, Double> perkModifiers = Maps.newHashMap();

    private PerkTreeConfig() {}

    /**
     * Disables a perk so it cannot be unlocked
     *
     * @param perkRegistryName The registry name of the perk to disable
     */
    public static void disablePerk(String perkRegistryName) {
        disabledPerks.add(perkRegistryName);
    }

    /**
     * Removes a perk from the perk tree entirely
     *
     * @param perkRegistryName The registry name of the perk to remove
     */
    public static void removePerk(String perkRegistryName) {
        removedPerks.add(perkRegistryName);
    }

    /**
     * Modifies a perk's multiplier
     *
     * @param perkRegistryName The registry name of the perk
     * @param multiplier       The multiplier to apply
     */
    public static void modifyPerk(String perkRegistryName, double multiplier) {
        perkModifiers.put(perkRegistryName, multiplier);
    }

    /**
     * Checks if a perk should be removed
     *
     * @param perk The perk to check
     * @return true if the perk should be removed
     */
    public static boolean shouldRemovePerk(AbstractPerk perk) {
        return removedPerks.contains(
            perk.getRegistryName()
                .toString());
    }

    /**
     * Checks if a perk should be disabled
     *
     * @param perk The perk to check
     * @return true if the perk should be disabled
     */
    public static boolean shouldDisablePerk(AbstractPerk perk) {
        return disabledPerks.contains(
            perk.getRegistryName()
                .toString());
    }

    /**
     * Gets the multiplier for a perk
     *
     * @param perk The perk
     * @return The multiplier, or 1.0 if no modifier is set
     */
    public static double getMultiplier(AbstractPerk perk) {
        return perkModifiers.getOrDefault(
            perk.getRegistryName()
                .toString(),
            1.0D);
    }

    /**
     * Clears all configuration
     */
    public static void clear() {
        removedPerks.clear();
        disabledPerks.clear();
        perkModifiers.clear();
    }

    /**
     * Event handler for perk removal
     */
    public static class EventHandler {

        @SubscribeEvent
        public void onPerkRemoval(APIRegistryEvent.PerkPostRemove event) {
            if (shouldRemovePerk(event.getPerk())) {
                event.setRemoved(true);
            }
        }

        @SubscribeEvent
        public void onPerkDisable(APIRegistryEvent.PerkDisable event) {
            if (shouldDisablePerk(event.getPerk())) {
                event.setPerkDisabled(true);
            }
        }
    }
}
