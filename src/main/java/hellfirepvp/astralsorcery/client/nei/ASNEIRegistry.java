/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASNEIRegistry - NEI registration for Astral Sorcery
 *
 * 1.7.10: NEI integration
 *******************************************************************************/

package hellfirepvp.astralsorcery.client.nei;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * NEI Registry for Astral Sorcery
 * <p>
 * Registers NEI handlers for altar recipes.
 * <p>
 * 1.7.10 Implementation:
 * <li>Registers recipe handlers for each altar level</li>
 * <li>Sets up catalyst items (altar blocks)</li>
 */
public class ASNEIRegistry {

    /**
     * Register NEI handlers
     * Called during mod initialization
     * <p>
     * Note: AltarRecipeRegistry.init() is NOT called here because recipes
     * are registered in CommonProxy.postInit(), which happens AFTER init().
     * The registry will be initialized by CommonProxy.postInit() before recipes are added.
     */
    public static void registerNEI() {
        // DO NOT call AltarRecipeRegistry.init() here!
        // Recipes are registered in CommonProxy.postInit(), which is the correct time.
        // NEI handlers will dynamically fetch recipes when needed.

        // Register handler for each altar level
        // These will be registered when altar blocks are available
    }

    /**
     * Register NEI handler for a specific altar level
     *
     * @param altarLevel The altar level
     * @param altarBlock The altar block item (catalyst)
     */
    public static void registerAltarHandler(TileAltar.AltarLevel altarLevel, ItemStack altarBlock) {
        String handlerName = getHandlerName(altarLevel);
        ASNEIAltarHandler handler = new ASNEIAltarHandler(altarLevel, handlerName);

        String overlayId = handler.getOverlayIdentifier();

        // Send IMC message to NEI to register the handler
        FMLInterModComms.sendRuntimeMessage(
            Constants.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            Constants.MODID + "@" + handlerName + "@" + overlayId);
    }

    /**
     * Get the display name for an altar level handler
     */
    private static String getHandlerName(TileAltar.AltarLevel altarLevel) {
        return switch (altarLevel) {
            case DISCOVERY -> "Discovery Altar";
            case ATTUNEMENT -> "Attunement Altar";
            case CONSTELLATION_CRAFT -> "Constellation Altar";
            case TRAIT_CRAFT -> "Trait Altar";
            case BRILLIANCE -> "Brilliance Altar";
            default -> altarLevel.toString();
        };
    }

    private ASNEIRegistry() {
        // Private constructor
    }
}
