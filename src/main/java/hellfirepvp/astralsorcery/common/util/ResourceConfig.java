/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Centralized resource configuration for items and blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized resource configuration
 * <p>
 * This class contains all icon mappings for items and blocks.
 * Add new items/blocks here instead of configuring them in individual classes.
 * <p>
 * Format:
 * - key: registry name (e.g., "itemcraftingcomponent")
 * - value: texture name or array of texture names (for metadata items)
 */
public final class ResourceConfig {

    private ResourceConfig() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ========== Item Icon Configuration ==========

    /**
     * Get icon configuration for an item
     *
     * @param itemName The item's registry name
     * @return IconConfig containing icon names, or null if not configured
     */
    public static IconConfig getItemIcons(String itemName) {
        return ITEM_ICONS.get(itemName);
    }

    /**
     * Check if an item has metadata variants
     *
     * @param itemName The item's registry name
     * @return true if the item has multiple icons
     */
    public static boolean itemHasVariants(String itemName) {
        IconConfig config = ITEM_ICONS.get(itemName);
        return config != null && config.isVariant();
    }

    // ========== Block Icon Configuration ==========

    /**
     * Get icon configuration for a block
     *
     * @param blockName The block's registry name
     * @return IconConfig containing icon names, or null if not configured
     */
    public static IconConfig getBlockIcons(String blockName) {
        return BLOCK_ICONS.get(blockName);
    }

    /**
     * Check if a block has metadata variants
     *
     * @param blockName The block's registry name
     * @return true if the block has multiple icons
     */
    public static boolean blockHasVariants(String blockName) {
        IconConfig config = BLOCK_ICONS.get(blockName);
        return config != null && config.isVariant();
    }

    // ========== Configuration Data ==========

    private static final Map<String, IconConfig> ITEM_ICONS = new HashMap<>();
    private static final Map<String, IconConfig> BLOCK_ICONS = new HashMap<>();

    static {
        // ==================== ITEMS ====================

        // Crafting Component (6 variants)
        ITEM_ICONS.put(
            "itemcraftingcomponent",
            new IconConfig(
                new String[] { "aquamarine", // 0
                    "starmetal_ingot", // 1
                    "stardust", // 2
                    "glass_lens", // 3
                    "reso_gem", // 4
                    "parchment" // 5
                }));

        // Crystal Tools
        ITEM_ICONS.put("itemcrystalpickaxe", new IconConfig("crystal_pickaxe"));
        ITEM_ICONS.put("itemcrystalsword", new IconConfig("crystal_sword"));
        ITEM_ICONS.put("itemcrystalshovel", new IconConfig("crystal_shovel"));
        ITEM_ICONS.put("itemcrystalaxe", new IconConfig("crystal_axe"));

        // Charged Crystal Tools (enhanced versions)
        ITEM_ICONS.put("itemchargedcrystalpickaxe", new IconConfig("crystal_pickaxe_s"));
        ITEM_ICONS.put("itemchargedcrystalaxe", new IconConfig("crystal_axe_s"));
        ITEM_ICONS.put("itemchargedcrystalshovel", new IconConfig("crystal_shovel_s"));
        ITEM_ICONS.put("itemchargedcrystalsword", new IconConfig("crystal_sword_s"));

        // Tuned Crystals
        ITEM_ICONS.put("itemtunedrockcrystal", new IconConfig("crystal_rock"));
        ITEM_ICONS.put("itemcelestialcrystal", new IconConfig("crystal_celestial"));
        ITEM_ICONS.put("itemtunedcelestialcrystal", new IconConfig("crystal_celestial"));

        // Simple Items
        ITEM_ICONS.put("itemrockcrystalsimple", new IconConfig("crystal_rock"));

        // Research Items
        ITEM_ICONS.put("itemjournal", new IconConfig("tome_astral"));
        ITEM_ICONS.put("itemconstellationpaper", new IconConfig("scroll_constellation"));
        ITEM_ICONS.put("itemknowledgefragment", new IconConfig("scroll_empty"));
        ITEM_ICONS.put("itemfragmentcapsule", new IconConfig("capsule_constellation_tip"));
        ITEM_ICONS.put("itemknowledgeshare", new IconConfig("scroll_expertise"));

        // Functional Items
        ITEM_ICONS.put("itemhandtelescope", new IconConfig("looking_glass"));

        // Wands
        ITEM_ICONS.put("itemwand", new IconConfig("wand_resonance"));
        ITEM_ICONS.put("itemarchitectwand", new IconConfig("wand_architecture"));
        ITEM_ICONS.put("itemexchangewand", new IconConfig("wand_exchange"));
        ITEM_ICONS.put("itemgrapplewand", new IconConfig("wand_grapple"));
        ITEM_ICONS.put("itemilluminationwand", new IconConfig("wand_illumination"));

        // Infused Glass (2 variants)
        ITEM_ICONS.put(
            "iteminfusedglass",
            new IconConfig(
                new String[] { "glass_infused", // 0 - normal
                    "glass_infused_engraved" // 1 - engraved/active
                }));

        // Colored Lens (all variants use same texture)
        ITEM_ICONS.put("itemcoloredlens", new IconConfig("glass_lens_coloured"));

        // Usable Dust (2 variants)
        ITEM_ICONS.put(
            "itemusabledust",
            new IconConfig(
                new String[] { "dust_light", // 0 - illumination
                    "dust_dark" // 1 - nocturnal
                }));

        // Perk Gem (3 constellation variants)
        ITEM_ICONS.put(
            "itemperkgem",
            new IconConfig(
                new String[] { "gem_sun", // 0 - day
                    "gem_moon", // 1 - night
                    "gem_sky" // 2 - sky
                }));

        // Perk Items
        ITEM_ICONS.put("itemperkseal", new IconConfig("seal"));

        // Shifting Star (7 variants)
        ITEM_ICONS.put(
            "itemshiftingstar",
            new IconConfig(
                new String[] { "shifting_star", // 0 - normal
                    "shifting_star_enhanced", // 1 - enhanced
                    "shifting_star_enhanced_aevitas", // 2
                    "shifting_star_enhanced_armara", // 3
                    "shifting_star_enhanced_discidia", // 4
                    "shifting_star_enhanced_evorsio", // 5
                    "shifting_star_enhanced_vicio" // 6
                }));

        // Cape (wearable)
        ITEM_ICONS.put("itemcape", new IconConfig("mantle"));

        // Enchantment Amulet
        ITEM_ICONS.put("itemenchantmentamulet", new IconConfig("amulet"));

        // Special Tools (Phase 5)
        ITEM_ICONS.put("itemrosebranchbow", new IconConfig("bow_rose"));
        ITEM_ICONS.put("itemlinkingtool", new IconConfig("linktool"));
        ITEM_ICONS.put("itemskyresonator", new IconConfig("resonator_starlight"));
        ITEM_ICONS.put("itemsextant", new IconConfig("sextant"));

        // ==================== BLOCKS ====================

        // Custom Ores (2 variants)
        BLOCK_ICONS.put(
            "blockcustomore",
            new IconConfig(
                new String[] { "ore_rockcrystal", // 0 - rock crystal
                    "ore_starmetal" // 1 - starmetal
                }));

        // Custom Sand Ore
        BLOCK_ICONS.put("blockcustomsandore", new IconConfig("ore_aquamarine"));

        // Custom Flower
        BLOCK_ICONS.put("blockcustomflower", new IconConfig("glowflower"));

        // Marble (7 variants)
        BLOCK_ICONS.put(
            "blockmarble",
            new IconConfig(
                new String[] { "marble_raw", // 0
                    "marble_bricks", // 1
                    "marble_pillar", // 2
                    "marble_arch", // 3
                    "marble_chiseled", // 4
                    "marble_engraved", // 5
                    "marble_runed" // 6
                }));

        // Black Marble (7 variants)
        BLOCK_ICONS.put(
            "blockblackmarble",
            new IconConfig(
                new String[] { "black_marble_raw", // 0
                    "black_marble_bricks", // 1
                    "black_marble_pillar", // 2
                    "black_marble_arch", // 3
                    "black_marble_chiseled", // 4
                    "black_marble_engraved", // 5
                    "black_marble_runed" // 6
                }));

        // Infused Wood (7 variants)
        BLOCK_ICONS.put(
            "blockinfusedwood",
            new IconConfig(
                new String[] { "wood_raw", // 0
                    "wood_planks", // 1
                    "wood_column", // 2
                    "wood_arch", // 3
                    "wood_engraved", // 4
                    "wood_enriched", // 5
                    "wood_infused" // 6
                }));

        // Light Blocks
        BLOCK_ICONS.put("blockflarelight", new IconConfig("core_edge"));

        // Marble Slab
        BLOCK_ICONS.put("blockmarbleslab", new IconConfig(new String[] { "marble_bricks", "marble_bricks" }));

        // Marble Stairs
        BLOCK_ICONS.put("blockmarblestairs", new IconConfig("marble_bricks"));

        // Special Blocks
        BLOCK_ICONS.put("blockstructural", new IconConfig("blockstructural"));
        BLOCK_ICONS.put("blockfaketree", new IconConfig("blockfaketree"));
        BLOCK_ICONS.put("blockvanishing", new IconConfig("vanishing_block"));
        BLOCK_ICONS.put("translucentblock", new IconConfig("translucentblock"));

        // Altar Blocks
        BLOCK_ICONS.put("blockaltar", new IconConfig("blockaltar"));
        BLOCK_ICONS.put("blockattunementaltar", new IconConfig("blockattunementaltar"));
        BLOCK_ICONS.put("blockwell", new IconConfig("blockwell"));
        BLOCK_ICONS.put("blockworldilluminator", new IconConfig("blockworldilluminator"));
        BLOCK_ICONS.put("blockmapdrawingtable", new IconConfig("blockmapdrawingtable"));
        BLOCK_ICONS.put("blockobservatory", new IconConfig("blockobservatory"));

        // Crystal Blocks
        BLOCK_ICONS.put("blockcollectorcrystal", new IconConfig("blockcollectorcrystal"));
        BLOCK_ICONS.put("blockcelestialcollectorcrystal", new IconConfig("crystal_celestial"));
        BLOCK_ICONS.put("blockattunementrelay", new IconConfig("block_attunement_relay"));
        BLOCK_ICONS.put("blockcelestialcrystals", new IconConfig("crystal_celestial"));

        // Optical Blocks
        BLOCK_ICONS.put("blocklens", new IconConfig("blocklens"));
        BLOCK_ICONS.put("blockprism", new IconConfig("blockprism"));

        // Ritual Blocks
        BLOCK_ICONS.put("blockritualpedestal", new IconConfig("blockritualpedestal"));
        BLOCK_ICONS.put("blockrituallink", new IconConfig("blockrituallink"));

        // Tree Blocks
        BLOCK_ICONS.put("blocktreebeacon", new IconConfig("blocktreebeacon"));
        BLOCK_ICONS.put("blockstarlightinfuser", new IconConfig("blockstarlightinfuser"));

        // Celestial Blocks
        BLOCK_ICONS.put("blockcelestialorrery", new IconConfig("blockcelestialorrery"));

        // Fountain Blocks
        BLOCK_ICONS.put("blockbore", new IconConfig("blockbore"));
        BLOCK_ICONS.put("blockborehead", new IconConfig("blockborehead"));

        // Gem Crystals (5 stages, all use same texture)
        BLOCK_ICONS.put(
            "blockgemcrystals",
            new IconConfig(
                new String[] { "rock_crystal", // All 5 stages use same texture
                    "rock_crystal", "rock_crystal", "rock_crystal", "rock_crystal" }));

        // Chalice
        BLOCK_ICONS.put("blockchalice", new IconConfig("blockchalice"));

        // Celestial Gateway
        BLOCK_ICONS.put("blockcelestialgateway", new IconConfig("block_celestial_gateway"));

        // Portal Node (invisible, no icon needed)
        // BLOCK_ICONS.put("blockportalnode", new IconConfig(""));

        // Machine Blocks (2 variants)
        BLOCK_ICONS.put(
            "blockmachine",
            new IconConfig(
                new String[] { "blocktelescope", // 0 - Telescope
                    "blockgrindstone" // 1 - Grindstone
                }));
    }

    // ========== Configuration Classes ==========

    /**
     * Icon configuration holder
     */
    public static class IconConfig {

        private final String[] icons;
        private final boolean isVariant;

        public IconConfig(String singleIcon) {
            this.icons = new String[] { singleIcon };
            this.isVariant = false;
        }

        public IconConfig(String[] multipleIcons) {
            this.icons = multipleIcons;
            this.isVariant = true;
        }

        public String[] getIcons() {
            return icons;
        }

        public String getIcon(int index) {
            if (index < 0 || index >= icons.length) {
                return icons[0]; // Default to first icon
            }
            return icons[index];
        }

        public int getIconCount() {
            return icons.length;
        }

        public boolean isVariant() {
            return isVariant;
        }

        public boolean hasIconAt(int index) {
            return index >= 0 && index < icons.length;
        }
    }
}
