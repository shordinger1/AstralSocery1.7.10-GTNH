/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.registry;

/**
 * Registry of all Astral Sorcery recipe maps
 * Central access point for all recipe types
 */
public final class ASRecipeMaps {

    private ASRecipeMaps() {}

    // Infusion Recipe Map
    public static final ASRecipeMap INFUSION = new ASRecipeMap(
        "astralsorcery.infusion",
        ASRecipe.Type.INFUSION,
        1);

    // Grindstone Recipe Map
    public static final ASRecipeMap GRINDSTONE = new ASRecipeMap(
        "astralsorcery.grindstone",
        ASRecipe.Type.GRINDSTONE,
        1);

    // Light Transmutation Recipe Map
    public static final ASRecipeMap LIGHT_TRANSMUTATION = new ASRecipeMap(
        "astralsorcery.transmutation",
        ASRecipe.Type.LIGHT_TRANSMUTATION,
        1);

    // Altar Recipe Maps - different tiers
    public static final ASRecipeMap ALTAR_DISCOVERY = new ASRecipeMap(
        "astralsorcery.altar.discovery",
        ASRecipe.Type.ALTAR_DISCOVERY,
        9); // SLOT_COUNT_T1

    public static final ASRecipeMap ALTAR_ATTUNEMENT = new ASRecipeMap(
        "astralsorcery.altar.attunement",
        ASRecipe.Type.ALTAR_ATTUNEMENT,
        13); // SLOT_COUNT_T2

    public static final ASRecipeMap ALTAR_CONSTELLATION = new ASRecipeMap(
        "astralsorcery.altar.constellation",
        ASRecipe.Type.ALTAR_CONSTELLATION,
        21); // SLOT_COUNT_T3

    public static final ASRecipeMap ALTAR_TRAIT = new ASRecipeMap(
        "astralsorcery.altar.trait",
        ASRecipe.Type.ALTAR_TRAIT,
        25); // SLOT_COUNT_T4

    // Lightwell Recipe Map
    public static final ASRecipeMap WELL = new ASRecipeMap(
        "astralsorcery.well",
        ASRecipe.Type.WELL,
        1);

    // Liquid Interaction Recipe Map
    public static final ASRecipeMap LIQUID_INTERACTION = new ASRecipeMap(
        "astralsorcery.liquid_interaction",
        ASRecipe.Type.LIQUID_INTERACTION,
        2);

    /**
     * Gets a recipe map by its unlocalized name
     *
     * @param name The unlocalized name of the recipe map
     * @return The recipe map, or null if not found
     */
    public static ASRecipeMap getMap(String name) {
        for (ASRecipeMap map : ASRecipeMap.ALL_RECIPE_MAPS) {
            if (map.getUnlocalizedName()
                .equals(name)) {
                return map;
            }
        }
        return null;
    }

    /**
     * Gets a recipe map by recipe type
     *
     * @param type The recipe type
     * @return The recipe map, or null if not found
     */
    public static ASRecipeMap getMap(ASRecipe.Type type) {
        for (ASRecipeMap map : ASRecipeMap.ALL_RECIPE_MAPS) {
            if (map.getRecipeType() == type) {
                return map;
            }
        }
        return null;
    }

    /**
     * Initializes all recipe maps
     * Called during mod initialization
     */
    public static void init() {
        // Recipe maps are already initialized via static fields
        // This method ensures class loading and can be extended for additional setup
    }
}
