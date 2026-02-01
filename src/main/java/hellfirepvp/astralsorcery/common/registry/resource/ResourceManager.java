/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Centralized Resource Management
 *
 * This class provides a unified API for managing all mod resources including:
 * - Resource locations (blocks, items, entities)
 * - Texture paths (blocks, items, GUI, effects, etc.)
 * - Model paths (block models, item models, OBJ models)
 * - Sound paths
 * - Localization keys
 *
 * Instead of scattering hardcoded strings across the codebase, all resource
 * strings are defined here with type-safe accessors.
 *
 * Usage Example:
 *   // Get resource location for block
 *   ResourceLocation loc = ResourceManager.blocks().altar();
 *
 *   // Get texture path
 *   String texture = ResourceManager.textures().blockMarble();
 *
 *   // Get localization key
 *   String key = ResourceManager.localization().block("blockMarble");
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.resource;

import hellfirepvp.astralsorcery.common.lib.Constants;
import net.minecraft.util.ResourceLocation;

/**
 * Centralized resource manager for Astral Sorcery.
 * <p>
 * This class provides type-safe access to all resource strings and locations.
 * Benefits:
 * - Single source of truth for all resource strings
 * - Type-safe with compile-time checking
 * - Easy to find and update resources
 * - Supports IDE autocomplete
 * - Reduces typos and errors
 * - Facilitates bulk resource operations
 * <p>
 * Thread Safety: This class uses static final fields and is thread-safe.
 */
public class ResourceManager {

    // Mod ID is defined in Constants
    private static final String MOD_ID = Constants.MODID;
    private static final String RESOURCE_PREFIX = Constants.RESOURCE_ROOT;

    // ========================================================================
    // =                    RESOURCE LOCATION HELPER                        =
    // ========================================================================

    /**
     * Create a resource location for this mod
     */
    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /**
     * Create a resource location with full mod id prefix
     */
    public static ResourceLocation prefixedLocation(String path) {
        return new ResourceLocation(RESOURCE_PREFIX + path);
    }

    /**
     * Get a texture resource location
     */
    public static ResourceLocation texture(String path) {
        return location("textures/" + path + ".png");
    }

    /**
     * Get a model resource location
     */
    public static ResourceLocation model(String path) {
        return location("models/" + path + ".json");
    }

    /**
     * Get a blockstate resource location
     */
    public static ResourceLocation blockstate(String blockName) {
        return location("blockstates/" + blockName + ".json");
    }

    // ========================================================================
    // =                    BLOCK RESOURCES                                 =
    // ========================================================================

    /**
     * Block resource locations and names
     */
    public static class Blocks {

        // Block names (for registration)
        public static final String CUSTOM_ORE = "blockcustomore";
        public static final String CUSTOM_SAND_ORE = "blockcustomsandore";
        public static final String CUSTOM_FLOWER = "blockcustomflower";
        public static final String MARBLE = "blockmarble";
        public static final String BLACK_MARBLE = "blockblackmarble";
        public static final String INFUSED_WOOD = "blockinfusedwood";
        public static final String FLARE_LIGHT = "blockflarelight";
        public static final String MARBLE_SLAB = "blockmarbleslab";
        public static final String MARBLE_DOUBLE_SLAB = "blockmarbledoubleslab";
        public static final String MARBLE_STAIRS = "blockmarblestairs";
        public static final String STRUCTURAL = "blockstructural";
        public static final String FAKE_TREE = "blockfaketree";
        public static final String VANISHING = "blockvanishing";
        public static final String TRANSLUCENT = "blocktranslucent";
        public static final String ALTAR = "blockaltar";
        public static final String ATTUNEMENT_ALTAR = "blockattunementaltar";
        public static final String WELL = "blockwell";
        public static final String ILLUMINATOR = "blockworldilluminator";
        public static final String DRAWING_TABLE = "blockmapdrawingtable";
        public static final String OBSERVATORY = "blockobservatory";
        public static final String TELESCOPE = "blocktelescope";
        public static final String COLLECTOR_CRYSTAL = "blockcollectorcrystal";
        public static final String LENS = "blocklens";
        public static final String PRISM = "blockprism";
        public static final String CELESTIAL_COLLECTOR = "blockcelestialcollectorcrystal";
        public static final String ATTUNEMENT_RELAY = "blockattunementrelay";
        public static final String CELESTIAL_CRYSTALS = "blockcelestialcrystals";
        public static final String RITUAL_PEDESTAL = "blockritualpedestal";
        public static final String RITUAL_LINK = "blockrituallink";
        public static final String TREE_BEACON = "blocktreebeacon";
        public static final String STARLIGHT_INFUSER = "blockstarlightinfuser";
        public static final String CELESTIAL_ORRERY = "blockcelestialorrery";
        public static final String BORE = "blockbore";
        public static final String BORE_HEAD = "blockborehead";
        public static final String GEM_CRYSTALS = "blockgemcrystals";
        public static final String CHALICE = "blockchalice";
        public static final String CELESTIAL_GATEWAY = "blockcelestialgateway";
        public static final String PORTAL_NODE = "blockportalnode";
        public static final String MACHINE = "blockmachine";
        public static final String LIQUID_STARLIGHT = "blockliquidstarlight";

        // Resource locations
        public static ResourceLocation customOre() {
            return location(CUSTOM_ORE);
        }

        public static ResourceLocation customSandOre() {
            return location(CUSTOM_SAND_ORE);
        }

        public static ResourceLocation customFlower() {
            return location(CUSTOM_FLOWER);
        }

        public static ResourceLocation marble() {
            return location(MARBLE);
        }

        public static ResourceLocation blackMarble() {
            return location(BLACK_MARBLE);
        }

        public static ResourceLocation infusedWood() {
            return location(INFUSED_WOOD);
        }

        public static ResourceLocation flareLight() {
            return location(FLARE_LIGHT);
        }

        public static ResourceLocation altar() {
            return location(ALTAR);
        }

        public static ResourceLocation attunementAltar() {
            return location(ATTUNEMENT_ALTAR);
        }

        public static ResourceLocation well() {
            return location(WELL);
        }

        public static ResourceLocation illuminator() {
            return location(ILLUMINATOR);
        }

        public static ResourceLocation observatory() {
            return location(OBSERVATORY);
        }

        public static ResourceLocation telescope() {
            return location(TELESCOPE);
        }

        public static ResourceLocation collectorCrystal() {
            return location(COLLECTOR_CRYSTAL);
        }

        public static ResourceLocation lens() {
            return location(LENS);
        }

        public static ResourceLocation prism() {
            return location(PRISM);
        }

        public static ResourceLocation ritualPedestal() {
            return location(RITUAL_PEDESTAL);
        }

        public static ResourceLocation ritualLink() {
            return location(RITUAL_LINK);
        }

        public static ResourceLocation treeBeacon() {
            return location(TREE_BEACON);
        }

        public static ResourceLocation starlightInfuser() {
            return location(STARLIGHT_INFUSER);
        }

        public static ResourceLocation celestialGateway() {
            return location(CELESTIAL_GATEWAY);
        }

        /**
         * Get resource location by block name
         */
        public static ResourceLocation get(String blockName) {
            return location(blockName);
        }
    }

    // ========================================================================
    // =                    ITEM RESOURCES                                  =
    // ========================================================================

    /**
     * Item resource locations and names
     */
    public static class Items {

        // Item names (for registration)
        public static final String CRAFTING_COMPONENT = "craftingcomponent";
        public static final String ROCK_CRYSTAL_SIMPLE = "rockcrystalsimple";
        public static final String TUNED_ROCK_CRYSTAL = "tunedcrystalrock";
        public static final String CELESTIAL_CRYSTAL = "celestialcrystal";
        public static final String TUNED_CELESTIAL_CRYSTAL = "tunedcrystalcelestial";
        public static final String JOURNAL = "journal";
        public static final String CONSTELLATION_PAPER = "constellationpaper";
        public static final String KNOWLEDGE_FRAGMENT = "knowledgefragment";
        public static final String FRAGMENT_CAPSULE = "fragmentcapsule";
        public static final String KNOWLEDGE_SHARE = "knowledgeshare";
        public static final String HAND_TELESCOPE = "handtelescope";
        public static final String INFUSED_GLASS = "infusedglass";
        public static final String COLORED_LENS = "coloredlens";
        public static final String USABLE_DUST = "usabledust";
        public static final String PERK_GEM = "perkgem";
        public static final String PERK_SEAL = "perkseal";
        public static final String SHIFTING_STAR = "shiftingstar";
        public static final String WAND = "wand";
        public static final String ARCHITECT_WAND = "wandarchitect";
        public static final String EXCHANGE_WAND = "wandexchange";
        public static final String GRAPPLE_WAND = "wandgrapple";
        public static final String ILLUMINATION_WAND = "wandillumination";
        public static final String ROSE_BRANCH_BOW = "rosebranchbow";
        public static final String LINKING_TOOL = "linkingtool";
        public static final String SKY_RESONATOR = "skyresonator";
        public static final String SEXTANT = "sextant";
        public static final String CAPE = "cape";
        public static final String ENCHANTMENT_AMULET = "enchantmentamulet";

        // Tool item names
        public static final String CRYSTAL_PICKAXE = "chargedcrystalpickaxe";
        public static final String CRYSTAL_SWORD = "chargedcrystalsword";
        public static final String CRYSTAL_AXE = "chargedcrystalaxe";
        public static final String CRYSTAL_SHOVEL = "chargedcrystalshovel";

        // Resource locations
        public static ResourceLocation craftingComponent() {
            return location(CRAFTING_COMPONENT);
        }

        public static ResourceLocation rockCrystal() {
            return location(ROCK_CRYSTAL_SIMPLE);
        }

        public static ResourceLocation tunedRockCrystal() {
            return location(TUNED_ROCK_CRYSTAL);
        }

        public static ResourceLocation celestialCrystal() {
            return location(CELESTIAL_CRYSTAL);
        }

        public static ResourceLocation tunedCelestialCrystal() {
            return location(TUNED_CELESTIAL_CRYSTAL);
        }

        public static ResourceLocation journal() {
            return location(JOURNAL);
        }

        public static ResourceLocation constellationPaper() {
            return location(CONSTELLATION_PAPER);
        }

        public static ResourceLocation knowledgeFragment() {
            return location(KNOWLEDGE_FRAGMENT);
        }

        public static ResourceLocation fragmentCapsule() {
            return location(FRAGMENT_CAPSULE);
        }

        public static ResourceLocation knowledgeShare() {
            return location(KNOWLEDGE_SHARE);
        }

        public static ResourceLocation handTelescope() {
            return location(HAND_TELESCOPE);
        }

        public static ResourceLocation infusedGlass() {
            return location(INFUSED_GLASS);
        }

        public static ResourceLocation coloredLens() {
            return location(COLORED_LENS);
        }

        public static ResourceLocation usableDust() {
            return location(USABLE_DUST);
        }

        public static ResourceLocation perkGem() {
            return location(PERK_GEM);
        }

        public static ResourceLocation perkSeal() {
            return location(PERK_SEAL);
        }

        public static ResourceLocation shiftingStar() {
            return location(SHIFTING_STAR);
        }

        public static ResourceLocation wand() {
            return location(WAND);
        }

        public static ResourceLocation architectWand() {
            return location(ARCHITECT_WAND);
        }

        public static ResourceLocation exchangeWand() {
            return location(EXCHANGE_WAND);
        }

        public static ResourceLocation grappleWand() {
            return location(GRAPPLE_WAND);
        }

        public static ResourceLocation illuminationWand() {
            return location(ILLUMINATION_WAND);
        }

        public static ResourceLocation roseBranchBow() {
            return location(ROSE_BRANCH_BOW);
        }

        public static ResourceLocation linkingTool() {
            return location(LINKING_TOOL);
        }

        public static ResourceLocation skyResonator() {
            return location(SKY_RESONATOR);
        }

        public static ResourceLocation sextant() {
            return location(SEXTANT);
        }

        public static ResourceLocation cape() {
            return location(CAPE);
        }

        public static ResourceLocation enchantmentAmulet() {
            return location(ENCHANTMENT_AMULET);
        }

        /**
         * Get resource location by item name
         */
        public static ResourceLocation get(String itemName) {
            return location(itemName);
        }
    }

    // ========================================================================
    // =                    TEXTURE PATHS                                  =
    // ========================================================================

    /**
     * Texture paths for all textures
     */
    public static class Textures {

        // Block texture paths
        public static final String BLOCK_PREFIX = "blocks/";

        public static String blockMarble() {
            return BLOCK_PREFIX + "marble";
        }

        public static String blockBlackMarble() {
            return BLOCK_PREFIX + "blackmarble";
        }

        public static String blockInfusedWood() {
            return BLOCK_PREFIX + "infusedwood";
        }

        public static String blockAltar() {
            return BLOCK_PREFIX + "altar";
        }

        public static String blockWell() {
            return BLOCK_PREFIX + "lightwell";
        }

        public static String blockObservatory() {
            return BLOCK_PREFIX + "observatory";
        }

        public static String blockTelescope() {
            return BLOCK_PREFIX + "telescope";
        }

        public static String blockCollectorCrystal() {
            return BLOCK_PREFIX + "crystal/collector";
        }

        public static String blockLens() {
            return BLOCK_PREFIX + "lens";
        }

        public static String blockRitualPedestal() {
            return BLOCK_PREFIX + "ritual/pedestal";
        }

        public static String blockRitualLink() {
            return BLOCK_PREFIX + "ritual/link";
        }

        public static String blockCelestialGateway() {
            return BLOCK_PREFIX + "celestialgateway";
        }

        // Item texture paths
        public static final String ITEM_PREFIX = "items/";

        public static String itemRockCrystal() {
            return ITEM_PREFIX + "rockcrystal";
        }

        public static String itemCelestialCrystal() {
            return ITEM_PREFIX + "celestialcrystal";
        }

        public static String itemJournal() {
            return ITEM_PREFIX + "journal";
        }

        public static String itemConstellationPaper() {
            return ITEM_PREFIX + "constellationpaper";
        }

        public static String itemKnowledgeFragment() {
            return ITEM_PREFIX + "knowledgefragment";
        }

        public static String itemWand() {
            return ITEM_PREFIX + "wand";
        }

        public static String itemSextant() {
            return ITEM_PREFIX + "sextant";
        }

        // GUI texture paths
        public static final String GUI_PREFIX = "gui/";

        public static String guiAltar() {
            return GUI_PREFIX + "altar";
        }

        public static String guiObservatory() {
            return GUI_PREFIX + "observatory";
        }

        public static String guiCelestialGateway() {
            return GUI_PREFIX + "celestialgateway";
        }

        public static String guiTreeBeacon() {
            return GUI_PREFIX + "treebeacon";
        }

        // Effect texture paths
        public static final String EFFECT_PREFIX = "effect/";

        public static String effectStarlight() {
            return EFFECT_PREFIX + "starlight";
        }

        public static String effectConstellation() {
            return EFFECT_PREFIX + "constellation";
        }

        // Model texture paths (for OBJ models)
        public static final String MODEL_PREFIX = "models/";

        public static String modelAltar() {
            return MODEL_PREFIX + "altar";
        }

        public static String modelObservatory() {
            return MODEL_PREFIX + "observatory";
        }

        public static String modelTelescope() {
            return MODEL_PREFIX + "telescope";
        }

        /**
         * Get texture resource location
         */
        public static ResourceLocation getTexture(String texturePath) {
            return texture(texturePath);
        }
    }

    // ========================================================================
    // =                    MODEL PATHS                                    =
    // ========================================================================

    /**
     * Model paths and resource locations
     */
    public static class Models {

        // Block models
        public static String blockAltar() {
            return "block/altar";
        }

        public static String blockWell() {
            return "block/lightwell";
        }

        public static String blockObservatory() {
            return "block/observatory";
        }

        public static String blockTelescope() {
            return "block/telescope";
        }

        public static String blockCollectorCrystal() {
            return "block/collectorcrystal";
        }

        public static String blockLens() {
            return "block/lens";
        }

        public static String blockRitualPedestal() {
            return "block/ritualpedestal";
        }

        public static String blockCelestialGateway() {
            return "block/celestialgateway";
        }

        // Item models
        public static String itemRockCrystal() {
            return "item/rockcrystal";
        }

        public static String itemCelestialCrystal() {
            return "item/celestialcrystal";
        }

        public static String itemJournal() {
            return "item/journal";
        }

        public static String itemWand() {
            return "item/wand";
        }

        // OBJ models (custom 3D models)
        public static String objAltar() {
            return "obj/altar";
        }

        public static String objObservatory() {
            return "obj/observatory";
        }

        public static String objTelescope() {
            return "obj/telescope";
        }

        /**
         * Get model resource location
         */
        public static ResourceLocation getModel(String modelPath) {
            return model(modelPath);
        }

        /**
         * Get OBJ model resource location
         */
        public static ResourceLocation getObjModel(String modelName) {
            return location("obj/" + modelName + ".obj");
        }
    }

    // ========================================================================
    // =                    SOUND PATHS                                    =
    // ========================================================================

    /**
     * Sound paths and resource locations
     */
    public static class Sounds {

        public static final String SOUND_PREFIX = "astralsorcery:";

        public static String altarCraft() {
            return SOUND_PREFIX + "altar.craft";
        }

        public static String altarComplete() {
            return SOUND_PREFIX + "altar.complete";
        }

        public static String starlight() {
            return SOUND_PREFIX + "ambient.starlight";
        }

        public static String constellation() {
            return SOUND_PREFIX + "ambient.constellation";
        }

        public static String ritual() {
            return SOUND_PREFIX + "ritual.active";
        }

        /**
         * Get sound resource location
         */
        public static ResourceLocation getSound(String soundName) {
            return new ResourceLocation(SOUND_PREFIX + soundName);
        }
    }

    // ========================================================================
    // =                    LOCALIZATION KEYS                              =
    // ========================================================================

    /**
     * Localization keys for all translatable strings
     */
    public static class Localization {

        private static final String PREFIX = "astralsorcery.";

        // Block localization
        public static String block(String blockName) {
            return PREFIX + "block." + blockName + ".name";
        }

        public static String tile(String tileName) {
            return PREFIX + "tile." + tileName + ".name";
        }

        // Item localization
        public static String item(String itemName) {
            return PREFIX + "item." + itemName + ".name";
        }

        // Constellation localization
        public static String constellation(String constellationName) {
            return PREFIX + "constellation." + constellationName + ".name";
        }

        public static String constellationDesc(String constellationName) {
            return PREFIX + "constellation." + constellationName + ".desc";
        }

        // GUI localization
        public static String gui(String guiName) {
            return PREFIX + "gui." + guiName + ".name";
        }

        // General strings
        public static final String CREATIVE_TAB = PREFIX + "creativeTab";
        public static final String SUBTITLE = PREFIX + "subtitle";

        // Subtitles
        public static String subtitle(String subtitleName) {
            return SUBTITLE + "." + subtitleName;
        }
    }

    // ========================================================================
    // =                    ENTITY RESOURCES                               =
    // ========================================================================

    /**
     * Entity resource names
     */
    public static class Entities {

        public static final String ENTITY_CRYSTAL_TOOL = "EntityCrystalTool";
        public static final String ENTITY_CRYSTAL = "EntityCrystal";
        public static final String ENTITY_FLARE = "EntityFlare";
        public static final String ENTITY_GRAPPLING_HOOK = "EntityGrapplingHook";
        public static final String ENTITY_ILLUMINATION_SPARK = "EntityIlluminationSpark";
        public static final String ENTITY_ITEM_STARDUST = "EntityItemStardust";
        public static final String ENTITY_LIQUID_SPARK = "EntityLiquidSpark";
        public static final String ENTITY_NOCTURNAL_SPARK = "EntityNocturnalSpark";
        public static final String ENTITY_OBSERVATORY_HELPER = "EntityObservatoryHelper";
        public static final String ENTITY_SHOOTING_STAR = "EntityShootingStar";
        public static final String ENTITY_SPECTRAL_TOOL = "EntitySpectralTool";
        public static final String ENTITY_STARBURST = "EntityStarburst";
        public static final String ENTITY_ITEM_EXPLOSION_RESISTANT = "EntityItemExplosionResistant";
        public static final String ENTITY_ITEM_HIGHLIGHTED = "EntityItemHighlighted";
        public static final String ENTITY_STARLIGHT_REACTANT = "EntityStarlightReactant";
        public static final String ENTITY_TECHNICAL_AMBIENT = "EntityTechnicalAmbient";
    }

    // ========================================================================
    // =                    GUI RESOURCES                                  =
    // ========================================================================

    /**
     * GUI IDs and resource paths
     */
    public static class GUI {

        public static final int ALTAR = 0;
        public static final int OBSERVATORY = 1;
        public static final int TREE_BEACON = 2;
        public static final int CELESTIAL_GATEWAY = 3;

        /**
         * Get GUI texture path
         */
        public static String getTexture(int guiId) {
            switch (guiId) {
                case ALTAR:
                    return Textures.guiAltar();
                case OBSERVATORY:
                    return Textures.guiObservatory();
                case CELESTIAL_GATEWAY:
                    return Textures.guiCelestialGateway();
                case TREE_BEACON:
                    return Textures.guiTreeBeacon();
                default:
                    return Textures.GUI_PREFIX + "unknown";
            }
        }
    }

    // ========================================================================
    // =                    GLOBAL HELPERS                                 =
    // ========================================================================

    /**
     * Get the mod ID
     */
    public static String getModId() {
        return MOD_ID;
    }

    /**
     * Get the resource prefix
     */
    public static String getResourcePrefix() {
        return RESOURCE_PREFIX;
    }

    /**
     * Validate a resource string
     */
    public static boolean isValidResourceString(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // Check for invalid characters
        return str.matches("^[a-z0-9/_.-]+$");
    }

    /**
     * Sanitize a string for use as a resource name
     * Converts uppercase to lowercase and replaces invalid characters
     */
    public static String sanitizeResourceName(String name) {
        if (name == null || name.isEmpty()) {
            return "unknown";
        }
        return name.toLowerCase()
                   .replaceAll("[^a-z0-9/_-]", "_")
                   .replaceAll("_+", "_")
                   .replaceAll("^_|_$", "");
    }
}
