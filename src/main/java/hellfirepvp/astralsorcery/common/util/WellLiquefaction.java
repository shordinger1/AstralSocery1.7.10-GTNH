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
 * 
 * <pre>
 * // Register a liquefaction recipe
 * WellLiquefaction.registerRecipe(
 *     new ItemStack(Items.ghast_tear),
 *     FluidRegistry.getFluid("liquid_starlight"),
 *     1.0, // productionMultiplier
 *     0.01 // shatterMultiplier
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
     * Matching 1.12.2 Astral Sorcery recipes
     */
    public static void init() {
        if (initialized) {
            LogHelper.info("[WellLiquefaction] Already initialized, skipping");
            return;
        }

        LogHelper.info("[WellLiquefaction] Initializing WellLiquefaction recipes...");

        // === LIQUID STARLIGHT RECIPES ===

        // Aquamarine → Liquid Starlight (0.4x, 12 shatter)
        LogHelper.info("[WellLiquefaction] Registering Aquamarine recipe...");
        hellfirepvp.astralsorcery.common.item.ItemCraftingComponent cc = new hellfirepvp.astralsorcery.common.item.ItemCraftingComponent();
        registerRecipe(
            new ItemStack(
                cc,
                1,
                hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.AQUAMARINE.ordinal()),
            "astralsorcery.liquidstarlight",
            0.4F,
            12F,
            new java.awt.Color(0x00, 0x88, 0xDD));

        // Resonance Gem → Liquid Starlight (0.6x, 18 shatter)
        LogHelper.info("[WellLiquefaction] Registering Resonance Gem recipe...");
        registerRecipe(
            new ItemStack(
                cc,
                1,
                hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.RESO_GEM.ordinal()),
            "astralsorcery.liquidstarlight",
            0.6F,
            18F,
            new java.awt.Color(0x00, 0x88, 0xDD));

        // Tuned Celestial Crystal → Liquid Starlight (1.0x, 100 shatter)
        LogHelper.info("[WellLiquefaction] Registering Tuned Celestial Crystal recipe...");
        registerRecipe(
            new ItemStack(hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.tunedCelestialCrystal),
            "astralsorcery.liquidstarlight",
            1.0F,
            100F,
            null // Will use celestial crystal color
        );

        // Celestial Crystal → Liquid Starlight (0.9x, 50 shatter)
        LogHelper.info("[WellLiquefaction] Registering Celestial Crystal recipe...");
        registerRecipe(
            new ItemStack(hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.celestialCrystal),
            "astralsorcery.liquidstarlight",
            0.9F,
            50F,
            null // Will use celestial crystal color
        );

        // Tuned Rock Crystal → Liquid Starlight (0.8x, 70 shatter)
        LogHelper.info("[WellLiquefaction] Registering Tuned Rock Crystal recipe...");
        registerRecipe(
            new ItemStack(hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.tunedRockCrystal),
            "astralsorcery.liquidstarlight",
            0.8F,
            70F,
            null // Will use rock crystal color
        );

        // Rock Crystal → Liquid Starlight (0.7x, 30 shatter)
        LogHelper.info("[WellLiquefaction] Registering Rock Crystal recipe...");
        registerRecipe(
            new ItemStack(hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.rockCrystalSimple),
            "astralsorcery.liquidstarlight",
            0.7F,
            30F,
            null // Will use rock crystal color
        );

        // === WATER RECIPES ===

        // Ice → Water (1.0x, 15 shatter)
        LogHelper.info("[WellLiquefaction] Registering Ice recipe...");
        registerRecipe(
            new ItemStack(net.minecraft.init.Blocks.ice),
            "water",
            1.0F,
            15F,
            new java.awt.Color(0x53, 0x69, 0xFF));

        // Packed Ice → Water (1.0x, 15 shatter)
        LogHelper.info("[WellLiquefaction] Registering Packed Ice recipe...");
        registerRecipe(
            new ItemStack(net.minecraft.init.Blocks.packed_ice),
            "water",
            1.0F,
            15F,
            new java.awt.Color(0x53, 0x69, 0xFF));

        // Snow → Water (1.5x, 15 shatter)
        LogHelper.info("[WellLiquefaction] Registering Snow recipe...");
        registerRecipe(
            new ItemStack(net.minecraft.init.Blocks.snow),
            "water",
            1.5F,
            15F,
            new java.awt.Color(0x53, 0x69, 0xFF));

        // === LAVA RECIPES ===

        // NOTE: Magma block doesn't exist in 1.7.10 (added in 1.10)

        // Netherrack → Lava (0.5x, 0.1 shatter - very durable)
        LogHelper.info("[WellLiquefaction] Registering Netherrack recipe...");
        registerRecipe(
            new ItemStack(net.minecraft.init.Blocks.netherrack),
            "lava",
            0.5F,
            0.1F,
            new java.awt.Color(0xFF, 0x35, 0x0C));

        initialized = true;
        LogHelper.info("[WellLiquefaction] WellLiquefaction recipes initialized: " + recipes.size() + " recipes");
    }

    /**
     * Register a liquefaction recipe
     *
     * @param input                The input item
     * @param fluidName            The output fluid name (e.g., "liquid_starlight")
     * @param productionMultiplier Production rate multiplier (higher = faster)
     * @param shatterMultiplier    Catalyst shatter chance per tick (higher = more likely to break)
     */
    public static void registerRecipe(ItemStack input, String fluidName, double productionMultiplier,
        double shatterMultiplier) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            LogHelper.warn(
                "[WellLiquefaction] Failed to register liquefaction recipe: Fluid '" + fluidName + "' not found!");
            return;
        }

        String key = getItemStackKey(input);
        LiquefactionEntry entry = new LiquefactionEntry(
            input,
            new FluidStack(fluid, 1), // Amount doesn't matter, productionMultiplier controls rate
            productionMultiplier,
            shatterMultiplier);

        recipes.put(key, entry);
        LogHelper.info(
            "[WellLiquefaction] Registered recipe: key='%s', displayName='%s' → %s (mult: %.2f, shatter: %.4f)",
            key,
            input.getDisplayName(),
            fluidName,
            productionMultiplier,
            shatterMultiplier);
    }

    /**
     * Register a liquefaction recipe with custom color
     *
     * @param input                The input item
     * @param fluidName            The output fluid name
     * @param productionMultiplier Production rate multiplier
     * @param shatterMultiplier    Catalyst shatter chance
     * @param catalystColor        Custom color for catalyst effect
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
        LogHelper.info(
            "[WellLiquefaction] Registered recipe with color: key='%s', displayName='%s' → %s (mult: %.2f, shatter: %.4f)",
            key,
            input.getDisplayName(),
            fluidName,
            productionMultiplier,
            shatterMultiplier);
    }

    /**
     * Get liquefaction entry for an item
     *
     * @param stack The item stack
     * @return The liquefaction entry, or null if not found
     */
    public static LiquefactionEntry getLiquefactionEntry(ItemStack stack) {
        if (stack == null) {
            LogHelper.debug("[WellLiquefaction] getLiquefactionEntry: stack is null");
            return null;
        }

        String key = getItemStackKey(stack);
        LogHelper
            .debug("[WellLiquefaction] Looking for recipe: key='%s', displayName='%s'", key, stack.getDisplayName());
        LogHelper.debug("[WellLiquefaction] Total recipes: %d", recipes.size());

        LiquefactionEntry entry = recipes.get(key);
        if (entry == null) {
            LogHelper.debug("[WellLiquefaction] Recipe not found for key: %s", key);
            // Log all available keys for debugging
            if (!recipes.isEmpty()) {
                LogHelper.debug("[WellLiquefaction] Available recipe keys: %s", recipes.keySet());
            }
        } else {
            LogHelper.debug("[WellLiquefaction] Recipe FOUND for: %s", key);
        }

        return entry;
    }

    /**
     * Check if an item can be liquefied
     *
     * @param stack The item stack
     * @return true if the item has a liquefaction recipe
     */
    public static boolean canLiquefy(ItemStack stack) {
        if (stack == null) {
            LogHelper.debug("[WellLiquefaction] canLiquefy: stack is null");
            return false;
        }
        boolean result = getLiquefactionEntry(stack) != null;
        LogHelper.debug("[WellLiquefaction] canLiquefy(%s): %s", stack.getDisplayName(), result);
        return result;
    }

    /**
     * Get the key for an ItemStack
     * Format: "ItemClassName:meta" to avoid registry timing issues
     */
    private static String getItemStackKey(ItemStack stack) {
        Item item = stack.getItem();
        // Use class name instead of registry name to avoid timing issues
        String itemName = item != null ? item.getClass()
            .getName() : "unknown";
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
