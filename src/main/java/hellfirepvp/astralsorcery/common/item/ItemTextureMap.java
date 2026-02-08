/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Texture name mapping for items
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps items to their actual texture file names
 * <p>
 * In 1.7.10, items need to register icons that match the actual PNG filenames
 * in assets/astralsorcery/textures/items/
 */
public final class ItemTextureMap {

    private static final Map<String, String> TEXTURE_MAP = new HashMap<>();

    static {
        // CraftingComponent
        TEXTURE_MAP.put("itemcraftingcomponent.aquamarine", "aquamarine");
        TEXTURE_MAP.put("itemcraftingcomponent.starmetal_ingot", "starmetal_ingot");
        TEXTURE_MAP.put("itemcraftingcomponent.stardust", "stardust");
        TEXTURE_MAP.put("itemcraftingcomponent.glass_lens", "glass_lens");
        TEXTURE_MAP.put("itemcraftingcomponent.reso_gem", "reso_gem");
        TEXTURE_MAP.put("itemcraftingcomponent.parchment", "parchment");

        // Crystal Tools
        TEXTURE_MAP.put("itemcrystalpickaxe", "crystal_pickaxe");
        TEXTURE_MAP.put("itemcrystalsword", "crystal_sword");
        TEXTURE_MAP.put("itemcrystalshovel", "crystal_shovel");
        TEXTURE_MAP.put("itemcrystalaxe", "crystal_axe");
        TEXTURE_MAP.put("itemrockcrystalsimple", "crystal_rock");

        // Charged Crystal Tools (enhanced versions)
        TEXTURE_MAP.put("itemchargedcrystalpickaxe", "crystal_pickaxe_s");
        TEXTURE_MAP.put("itemchargedcrystalaxe", "crystal_axe_s");
        TEXTURE_MAP.put("itemchargedcrystalshovel", "crystal_shovel_s");
        TEXTURE_MAP.put("itemchargedcrystalsword", "crystal_sword_s");

        // Crystal Tools with suffix
        TEXTURE_MAP.put("itemcrystalpickaxe.s", "crystal_pickaxe_s");
        TEXTURE_MAP.put("itemcrystalsword.s", "crystal_sword_s");
        TEXTURE_MAP.put("itemcrystalshovel.s", "crystal_shovel_s");
        TEXTURE_MAP.put("itemcrystalaxe.s", "crystal_axe_s");

        // Tuned Crystals
        TEXTURE_MAP.put("itemtunedrockcrystal", "crystal_rock");
        TEXTURE_MAP.put("itemcelestialcrystal", "crystal_celestial");
        TEXTURE_MAP.put("itemtunedcelestialcrystal", "crystal_celestial");

        // Special Items
        TEXTURE_MAP.put("itemjournal", "tome_astral");
        TEXTURE_MAP.put("itemconstellationpaper", "scroll_constellation");
        TEXTURE_MAP.put("itemknowledgefragment", "scroll_empty");
        TEXTURE_MAP.put("itemfragmentcapsule", "capsule_constellation_tip");
        TEXTURE_MAP.put("itemknowledgeshare", "scroll_expertise");

        // Wands
        TEXTURE_MAP.put("itemwand", "wand_resonance");
        TEXTURE_MAP.put("itemarchitectwand", "wand_architecture");
        TEXTURE_MAP.put("itemexchangewand", "wand_exchange");
        TEXTURE_MAP.put("itemgrapplewand", "wand_grapple");
        TEXTURE_MAP.put("itemilluminationwand", "wand_illumination");
        TEXTURE_MAP.put("itemhandtelescope", "looking_glass");

        // Wand overlays
        TEXTURE_MAP.put("itemwand.resonance", "wand_resonance");
        TEXTURE_MAP.put("itemwand.illumination", "wand_illumination");
        TEXTURE_MAP.put("itemwand.architecture", "wand_architecture");
        TEXTURE_MAP.put("itemwand.exchange", "wand_exchange");
        TEXTURE_MAP.put("itemwand.grapple", "wand_grapple");

        // Colored Lens
        TEXTURE_MAP.put("itemcoloredlens", "glass_lens_coloured");
        TEXTURE_MAP.put("itemcoloredlens.glass_lens", "glass_lens");

        // Shifting Star
        TEXTURE_MAP.put("itemshiftingstar", "shifting_star");
        TEXTURE_MAP.put("itemshiftingstar.enhanced", "shifting_star_enhanced");

        // Perks
        TEXTURE_MAP.put("itemenchantmentamulet", "amulet");
        TEXTURE_MAP.put("itemperkseal", "seal");
        TEXTURE_MAP.put("itemperkgem", "gem_sun"); // Default
        TEXTURE_MAP.put("itemperkgem.day", "gem_sun");
        TEXTURE_MAP.put("itemperkgem.night", "gem_moon");
        TEXTURE_MAP.put("itemperkgem.sky", "gem_sky");

        // Dust
        TEXTURE_MAP.put("itemusabledust.illumination", "dust_light");
        TEXTURE_MAP.put("itemusabledust.nocturnal", "dust_dark");

        // Cape
        TEXTURE_MAP.put("itemcape", "mantle");
        TEXTURE_MAP.put("itemcape.overlay", "mantle_overlay");

        // Other
        TEXTURE_MAP.put("iteminfusedglass", "glass_infused");
        TEXTURE_MAP.put("iteminfusedglass.active", "glass_infused_engraved");

        // === Missing Mappings ===
        // Linking Tool
        TEXTURE_MAP.put("itemlinkingtool", "linktool");

        // Rose Branch Bow
        TEXTURE_MAP.put("itemrosebranchbow", "bow_rose");

        // Sextant
        TEXTURE_MAP.put("itemsextant", "sextant");

        // Sky Resonator
        TEXTURE_MAP.put("itemskyresonator", "resonator_starlight");
    }

    /**
     * Get the texture name for an item
     *
     * @param itemUnlocalizedName Item's unlocalized name (e.g., "itemcraftingcomponent")
     * @param metadata            Item metadata (optional, use 0 for single-type items)
     * @return Texture filename without extension (e.g., "aquamarine")
     */
    public static String getTextureName(String itemUnlocalizedName, int metadata) {
        // Remove "item." prefix if present
        String key = itemUnlocalizedName;
        if (key.startsWith("item.")) {
            key = key.substring(5);
        }

        // For items with metadata, the key would be "itemcraftingcomponent.aquamarine"
        // But we registered as "itemcraftingcomponent" + metadata
        // So we need to handle this

        if (metadata > 0) {
            // Try to find a match with metadata suffix
            String metaKey = key + "." + metadata;
            if (TEXTURE_MAP.containsKey(metaKey)) {
                return TEXTURE_MAP.get(metaKey);
            }
        }

        return TEXTURE_MAP.getOrDefault(key, key);
    }

    /**
     * Get texture name for a simple item (no metadata)
     */
    public static String getTextureName(String itemUnlocalizedName) {
        return getTextureName(itemUnlocalizedName, 0);
    }

    /**
     * Check if a texture exists for this item
     */
    public static boolean hasTexture(String itemUnlocalizedName) {
        String texture = getTextureName(itemUnlocalizedName);
        return !texture.equals(itemUnlocalizedName.substring(5)); // Rough check
    }

    private ItemTextureMap() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
