/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeUtils;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;

/**
 * Utility class for Astral Sorcery recipe helpers
 * Replaces CraftTweaker-based Utils class
 *
 * Usage:
 *   ItemHandle handle = Utils.getCrystalHandle(false, false);
 */
public final class Utils {

    private Utils() {}

    /**
     * Creates an ItemHandle for crystal ingredients based on type and attunement requirements
     *
     * @param hasToBeCelestial Whether the crystal must be celestial
     * @param hasToBeAttuned   Whether the crystal must be attuned
     * @return An ItemHandle matching the specified criteria
     */
    public static ItemHandle getCrystalHandle(boolean hasToBeCelestial, boolean hasToBeAttuned) {
        return ASRecipeUtils.getCrystalHandle(hasToBeCelestial, hasToBeAttuned);
    }

    /**
     * Creates an ItemHandle for rock crystal
     */
    public static ItemHandle rockCrystal() {
        return ASRecipeUtils.rockCrystal();
    }

    /**
     * Creates an ItemHandle for celestial crystal
     */
    public static ItemHandle celestialCrystal() {
        return ASRecipeUtils.celestialCrystal();
    }

    /**
     * Creates an ItemHandle for tuned rock crystal
     */
    public static ItemHandle tunedRockCrystal() {
        return ASRecipeUtils.tunedRockCrystal();
    }

    /**
     * Creates an ItemHandle for tuned celestial crystal
     */
    public static ItemHandle tunedCelestialCrystal() {
        return ASRecipeUtils.tunedCelestialCrystal();
    }

    /**
     * Creates an ItemHandle for any crystal (no restrictions)
     */
    public static ItemHandle anyCrystal() {
        return ASRecipeUtils.anyCrystal();
    }

    /**
     * Creates an ItemHandle for any attuned crystal
     */
    public static ItemHandle anyAttunedCrystal() {
        return ASRecipeUtils.anyAttunedCrystal();
    }

    /**
     * Creates an ItemHandle for any celestial crystal
     */
    public static ItemHandle anyCelestialCrystal() {
        return ASRecipeUtils.anyCelestialCrystal();
    }

    /**
     * Creates an ItemHandle for any attuned celestial crystal
     */
    public static ItemHandle anyAttunedCelestialCrystal() {
        return ASRecipeUtils.anyAttunedCelestialCrystal();
    }

    /**
     * Creates an ItemHandle from a single ItemStack
     */
    public static ItemHandle handle(ItemStack stack) {
        return ASRecipeUtils.handle(stack);
    }

    /**
     * Creates an ItemHandle from an ore dictionary name
     */
    public static ItemHandle oreHandle(String oreDictName) {
        return ASRecipeUtils.oreHandle(oreDictName);
    }

    /**
     * Creates an ItemHandle from multiple ItemStacks
     */
    public static ItemHandle handle(ItemStack... stacks) {
        return ASRecipeUtils.handle(stacks);
    }
}
