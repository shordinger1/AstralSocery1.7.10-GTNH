/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.registry;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;

/**
 * Utility class for Astral Sorcery recipe system
 * Replaces the CraftTweaker Utils class
 */
public final class ASRecipeUtils {

    private ASRecipeUtils() {}

    /**
     * Creates an ItemHandle for crystal ingredients based on type and attunement requirements
     * Replaces the getCrystalORIngredient method from CraftTweaker Utils
     *
     * @param hasToBeCelestial Whether the crystal must be celestial
     * @param hasToBeAttuned   Whether the crystal must be attuned
     * @return An ItemHandle matching the specified criteria
     */
    @Nonnull
    public static ItemHandle getCrystalHandle(boolean hasToBeCelestial, boolean hasToBeAttuned) {
        return ItemHandle.getCrystalVariant(hasToBeAttuned, hasToBeCelestial);
    }

    /**
     * Creates an ItemHandle from a single ItemStack
     *
     * @param stack The ItemStack
     * @return An ItemHandle for the stack
     */
    @Nonnull
    public static ItemHandle handle(ItemStack stack) {
        return new ItemHandle(stack);
    }

    /**
     * Creates an ItemHandle from an ore dictionary name
     *
     * @param oreDictName The ore dictionary name
     * @return An ItemHandle for the ore dictionary entry
     */
    @Nonnull
    public static ItemHandle oreHandle(String oreDictName) {
        return new ItemHandle(oreDictName);
    }

    /**
     * Creates an ItemHandle from multiple ItemStacks
     *
     * @param stacks The ItemStacks
     * @return An ItemHandle for the stacks
     */
    @Nonnull
    public static ItemHandle handle(ItemStack... stacks) {
        return new ItemHandle(stacks);
    }

    /**
     * Creates an ItemHandle for rock crystal
     *
     * @return An ItemHandle for rock crystal
     */
    @Nonnull
    public static ItemHandle rockCrystal() {
        return new ItemHandle(new ItemStack(ItemsAS.rockCrystal));
    }

    /**
     * Creates an ItemHandle for celestial crystal
     *
     * @return An ItemHandle for celestial crystal
     */
    @Nonnull
    public static ItemHandle celestialCrystal() {
        return new ItemHandle(new ItemStack(ItemsAS.celestialCrystal));
    }

    /**
     * Creates an ItemHandle for tuned rock crystal
     *
     * @return An ItemHandle for tuned rock crystal
     */
    @Nonnull
    public static ItemHandle tunedRockCrystal() {
        return new ItemHandle(new ItemStack(ItemsAS.tunedRockCrystal));
    }

    /**
     * Creates an ItemHandle for tuned celestial crystal
     *
     * @return An ItemHandle for tuned celestial crystal
     */
    @Nonnull
    public static ItemHandle tunedCelestialCrystal() {
        return new ItemHandle(new ItemStack(ItemsAS.tunedCelestialCrystal));
    }

    /**
     * Creates an ItemHandle for any crystal (no restrictions)
     *
     * @return An ItemHandle for any crystal
     */
    @Nonnull
    public static ItemHandle anyCrystal() {
        return getCrystalHandle(false, false);
    }

    /**
     * Creates an ItemHandle for any attuned crystal
     *
     * @return An ItemHandle for any attuned crystal
     */
    @Nonnull
    public static ItemHandle anyAttunedCrystal() {
        return getCrystalHandle(false, true);
    }

    /**
     * Creates an ItemHandle for any celestial crystal
     *
     * @return An ItemHandle for any celestial crystal
     */
    @Nonnull
    public static ItemHandle anyCelestialCrystal() {
        return getCrystalHandle(true, false);
    }

    /**
     * Creates an ItemHandle for any attuned celestial crystal
     *
     * @return An ItemHandle for any attuned celestial crystal
     */
    @Nonnull
    public static ItemHandle anyAttunedCelestialCrystal() {
        return getCrystalHandle(true, true);
    }

    /**
     * Helper to validate recipe inputs
     *
     * @param inputs The inputs to validate
     * @return true if all inputs are valid
     */
    public static boolean areInputsValid(ItemHandle... inputs) {
        if (inputs == null || inputs.length == 0) {
            return false;
        }
        for (ItemHandle input : inputs) {
            if (input == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper to validate recipe output
     *
     * @param output The output to validate
     * @return true if the output is valid
     */
    public static boolean isOutputValid(ItemStack output) {
        return output != null && output.stackSize > 0;
    }

    /**
     * Clamps a value between min and max
     *
     * @param value The value to clamp
     * @param min   The minimum value
     * @param max   The maximum value
     * @return The clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamps a value between min and max
     *
     * @param value The value to clamp
     * @param min   The minimum value
     * @param max   The maximum value
     * @return The clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamps a value between min and max
     *
     * @param value The value to clamp
     * @param min   The minimum value
     * @param max   The maximum value
     * @return The clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
