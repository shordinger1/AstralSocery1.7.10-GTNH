/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * WellLiquefaction - Item to liquid starlight conversion system
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.common.registry.reference.ItemsAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * WellLiquefaction System (1.7.10 Simplified Version)
 * <p>
 * <b>Purpose</b>: Defines which items can be converted to liquid starlight in wells
 * <p>
 * <b>Features</b>:
 * <ul>
 * <li>Item-to-fluid conversion recipes</li>
 * <li>Production rate multipliers</li>
 * <li>Catalyst shatter chance</li>
 * <li>Custom catalyst colors</li>
 * </ul>
 * <p>
 * <b>Usage</b>:
 * <pre>
 * // Register a liquefaction recipe
 * WellLiquefaction.registerRecipe(
 *     new ItemStack(Items.ghast_tear),
 *     FluidRegistry.getFluid("liquid_starlight"),
 *     1.0,  // productionMultiplier
 *     0.01  // shatterMultiplier
 * );
 *
 * // Get recipe for an item
 * LiquefactionEntry entry = WellLiquefaction.getLiquefactionEntry(itemStack);
 * </pre>
 */
public class WellLiquefaction {

    private static final Map<String, LiquefactionEntry> recipes = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initialize default liquefaction recipes
     */
    public static void init() {
        if (initialized) {
            return;
        }

        LogHelper.info("Initializing WellLiquefaction recipes...");

        // === Vanilla Items ===

        // Glowstone Dust → Liquid Starlight (basic recipe)
        registerRecipe(
            new ItemStack(net.minecraft.init.Items.glowstone_dust),
            "astralsorcery.liquidStarlight",
            0.8,   // 80% production efficiency
            0.01   // 1% shatter chance
        );

        // Glowstone → Liquid Starlight (better)
        registerRecipe(
            new ItemStack(net.minecraft.init.Blocks.glowstone),
            "astralsorcery.liquidStarlight",
            1.2,   // 120% production efficiency
            0.005  // 0.5% shatter chance
        );

        // Nether Star → Liquid Starlight (very high efficiency)
        registerRecipe(
            new ItemStack(net.minecraft.init.Items.nether_star),
            "astralsorcery.liquidStarlight",
            5.0,   // 500% production efficiency
            0.001, // 0.1% shatter chance (very durable)
            new java.awt.Color(0xFFFFAA) // Light yellow
        );

        // === Astral Sorcery Items ===

        // TODO: Uncomment when ItemsAS are fully registered
        /*
        // Rock Crystal → Liquid Starlight
        registerRecipe(
            new ItemStack(hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.rockCrystal),
            "astralsorcery.liquidStarlight",
            1.0,   // 100% production efficiency
            0.008  // 0.8% shatter chance
        );

        // Celestial Crystal → Liquid Starlight (high efficiency)
        registerRecipe(
            new ItemStack(hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.celestitalCrystal),
            "astralsorcery.liquidStarlight",
            1.5,   // 150% production efficiency
            0.003, // 0.3% shatter chance
            new java.awt.Color(0x89CFF0) // Celestial blue
        );

        // Resonant Crystal → Liquid Starlight (very high efficiency)
        registerRecipe(
            new ItemStack(hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.crystalResonant),
            "astralsorcery.liquidStarlight",
            2.0,   // 200% production efficiency
            0.002, // 0.2% shatter chance
            new java.awt.Color(0x00FFFF) // Cyan
        );
        */

        initialized = true;
        LogHelper.info("WellLiquefaction recipes initialized: " + recipes.size() + " recipes");
    }

    /**
     * Register a liquefaction recipe
     *
     * @param input               The input item
     * @param fluidName           The output fluid name (e.g., "liquid_starlight")
     * @param productionMultiplier Production rate multiplier (higher = faster)
     * @param shatterMultiplier   Catalyst shatter chance per tick (higher = more likely to break)
     */
    public static void registerRecipe(ItemStack input, String fluidName, double productionMultiplier,
        double shatterMultiplier) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            LogHelper.warn("Failed to register liquefaction recipe: Fluid '" + fluidName + "' not found!");
            return;
        }

        String key = getItemStackKey(input);
        LiquefactionEntry entry = new LiquefactionEntry(
            input,
            new FluidStack(fluid, 1), // Amount doesn't matter, productionMultiplier controls rate
            productionMultiplier,
            shatterMultiplier);

        recipes.put(key, entry);
        LogHelper.debug("Registered liquefaction recipe: " + input.getDisplayName() + " → " + fluidName);
    }

    /**
     * Register a liquefaction recipe with custom color
     *
     * @param input               The input item
     * @param fluidName           The output fluid name
     * @param productionMultiplier Production rate multiplier
     * @param shatterMultiplier   Catalyst shatter chance
     * @param catalystColor       Custom color for catalyst effect
     */
    public static void registerRecipe(ItemStack input, String fluidName, double productionMultiplier,
        double shatterMultiplier, Color catalystColor) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            LogHelper.warn("Failed to register liquefaction recipe: Fluid '" + fluidName + "' not found!");
            return;
        }

        String key = getItemStackKey(input);
        LiquefactionEntry entry = new LiquefactionEntry(
            input,
            new FluidStack(fluid, 1),
            productionMultiplier,
            shatterMultiplier,
            catalystColor);

        recipes.put(key, entry);
        LogHelper.debug("Registered liquefaction recipe with color: " + input.getDisplayName());
    }

    /**
     * Get liquefaction entry for an item
     *
     * @param stack The item stack
     * @return The liquefaction entry, or null if not found
     */
    public static LiquefactionEntry getLiquefactionEntry(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        String key = getItemStackKey(stack);
        return recipes.get(key);
    }

    /**
     * Check if an item can be liquefied
     *
     * @param stack The item stack
     * @return true if the item has a liquefaction recipe
     */
    public static boolean canLiquefy(ItemStack stack) {
        return getLiquefactionEntry(stack) != null;
    }

    /**
     * Get the key for an ItemStack
     * Format: "itemRegistryName:meta"
     */
    private static String getItemStackKey(ItemStack stack) {
        Item item = stack.getItem();
        String itemName = item != null ? Item.itemRegistry.getNameForObject(item)
            : "unknown";
        int meta = stack.getItemDamage();
        return itemName + ":" + meta;
    }

    /**
     * Liquefaction Entry
     * <p>
     * Contains all data for a single liquefaction recipe
     */
    public static class LiquefactionEntry {

        /** The input item (catalyst) */
        public final ItemStack input;

        /** The output fluid */
        public final FluidStack producing;

        /** Production rate multiplier */
        public final double productionMultiplier;

        /** Catalyst shatter chance per tick */
        public final double shatterMultiplier;

        /** Custom color for catalyst effect (null = default white) */
        public final Color catalystColor;

        /**
         * Create a liquefaction entry with default color
         */
        public LiquefactionEntry(ItemStack input, FluidStack producing, double productionMultiplier,
            double shatterMultiplier) {
            this(input, producing, productionMultiplier, shatterMultiplier, Color.WHITE);
        }

        /**
         * Create a liquefaction entry with custom color
         */
        public LiquefactionEntry(ItemStack input, FluidStack producing, double productionMultiplier,
            double shatterMultiplier, Color catalystColor) {
            this.input = input;
            this.producing = producing;
            this.productionMultiplier = productionMultiplier;
            this.shatterMultiplier = shatterMultiplier;
            this.catalystColor = catalystColor;
        }

        /**
         * Calculate production amount based on starlight buffer
         * <p>
         * Formula: sqrt(starlightBuffer) * productionMultiplier
         *
         * @param starlightBuffer Current starlight buffer value
         * @return Amount of fluid to produce (in mB)
         */
        public double calculateProduction(double starlightBuffer) {
            return Math.sqrt(starlightBuffer) * productionMultiplier;
        }

        /**
         * Calculate shatter chance
         * <p>
         * Formula: 1 / (1 + (1000 * shatterMultiplier))
         * Returns a value between 0 and 1 (chance per tick)
         *
         * @return Shatter chance (0-1)
         */
        public double calculateShatterChance() {
            return 1.0 / (1.0 + (1000.0 * shatterMultiplier));
        }

        /**
         * Check if catalyst should shatter this tick
         *
         * @param rand Random instance
         * @return true if catalyst should shatter
         */
        public boolean shouldShatter(java.util.Random rand) {
            double chance = calculateShatterChance();
            return rand.nextDouble() < chance;
        }
    }

    /**
     * Get all registered recipes
     *
     * @return Unmodifiable map of recipes
     */
    public static Map<String, LiquefactionEntry> getAllRecipes() {
        return java.util.Collections.unmodifiableMap(recipes);
    }

    /**
     * Clear all recipes (for testing/debugging)
     */
    public static void clearRecipes() {
        recipes.clear();
        LogHelper.info("Cleared all WellLiquefaction recipes");
    }

    /**
     * Check if initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
