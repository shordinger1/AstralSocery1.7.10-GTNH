/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import hellfirepvp.astralsorcery.common.config.PerkTreeConfig;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;

/**
 * Perk Tree configuration helpers
 * Replaces CraftTweaker-based PerkTree class
 *
 * This class now delegates to PerkTreeConfig
 *
 * Usage:
 * PerkTree.disablePerk("perk_registry_name");
 * PerkTree.removePerk("perk_registry_name");
 * PerkTree.modifyPerk("perk_registry_name", 2.0);
 */
public final class PerkTree {

    private PerkTree() {}

    /**
     * Disables a perk so it cannot be unlocked
     *
     * @param perkRegistryName The registry name of the perk to disable
     */
    public static void disablePerk(String perkRegistryName) {
        PerkTreeConfig.disablePerk(perkRegistryName);
    }

    /**
     * Removes a perk from the perk tree entirely
     *
     * @param perkRegistryName The registry name of the perk to remove
     */
    public static void removePerk(String perkRegistryName) {
        PerkTreeConfig.removePerk(perkRegistryName);
    }

    /**
     * Modifies a perk's multiplier
     *
     * @param perkRegistryName The registry name of the perk
     * @param multiplier       The multiplier to apply
     */
    public static void modifyPerk(String perkRegistryName, double multiplier) {
        PerkTreeConfig.modifyPerk(perkRegistryName, multiplier);
    }

    /**
     * Gets the multiplier for a perk
     *
     * @param perk The perk
     * @return The multiplier, or 1.0 if no modifier is set
     */
    public static double getMultiplier(AbstractPerk perk) {
        return PerkTreeConfig.getMultiplier(perk);
    }

    /**
     * Checks if a perk should be removed
     *
     * @param perk The perk to check
     * @return true if the perk should be removed
     */
    public static boolean shouldRemovePerk(AbstractPerk perk) {
        return PerkTreeConfig.shouldRemovePerk(perk);
    }

    /**
     * Checks if a perk should be disabled
     *
     * @param perk The perk to check
     * @return true if the perk should be disabled
     */
    public static boolean shouldDisablePerk(AbstractPerk perk) {
        return PerkTreeConfig.shouldDisablePerk(perk);
    }
}
