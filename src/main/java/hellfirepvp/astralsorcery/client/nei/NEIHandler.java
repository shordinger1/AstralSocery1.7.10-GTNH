/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * NEI Handler - Registers recipes and catalysts to NEI
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.nei;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;

/**
 * NEI Handler for Astral Sorcery
 * <p>
 * Registers:
 * - Recipe handlers for altars and machines
 * - Catalyst items for recipe categories
 * - Custom recipe displays
 * <p>
 * Uses FMLInterModComms to send messages to NEI
 */
public class NEIHandler {

    /**
     * Register NEI handlers during pre-init
     */
    public static void registerNEIHandlers(FMLPreInitializationEvent event) {
        // Handlers are registered through IConfigureNEI
        // This method is for future use if needed
    }

    /**
     * Send IMC messages to NEI during post-init
     */
    public static void registerNEICatalysts(FMLPostInitializationEvent event) {
        try {
            // Register altar as recipe catalyst
            registerCatalyst(
                "altar", // Recipe handler name
                BlocksAS.blockAltar, // Block
                166, // Width
                135, // Height
                1 // Max recipes per page
            );

            // Register attunement altar as recipe catalyst
            registerCatalyst("attunement_altar", BlocksAS.attunementAltar, 166, 135, 1);

            // TODO: Register more machines as catalysts
            // registerCatalyst("lightwell", BlocksAS.blockWell, ...);
            // registerCatalyst("infuser", BlocksAS.starlightInfuser, ...);
            // registerCatalyst("celestial_orrery", BlocksAS.celestialOrrery, ...);
        } catch (Exception e) {
            hellfirepvp.astralsorcery.common.util.LogHelper
                .error("Failed to register NEI catalysts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Register a block as a recipe catalyst for a handler
     *
     * @param handlerName The recipe handler name
     * @param block       The block to register as catalyst
     * @param width       Recipe UI width
     * @param height      Recipe UI height
     * @param maxRecipes  Maximum recipes per page
     */
    private static void registerCatalyst(String handlerName, net.minecraft.block.Block block, int width, int height,
        int maxRecipes) {
        if (block == null) {
            return;
        }

        try {
            // Use explicit 3-parameter constructor with metadata=0 for 1.7.10
            ItemStack catalyst = new ItemStack(block, 1, 0);

            // Create NBT for catalyst registration
            NBTTagCompound catalystNBT = new NBTTagCompound();
            catalystNBT.setString("handler", handlerName);
            catalystNBT.setString("modName", Constants.MODNAME);
            catalystNBT.setString("modId", Constants.MODID);
            catalystNBT.setBoolean("modRequired", true);
            catalystNBT.setString(
                "itemName",
                net.minecraft.item.Item.getItemFromBlock(block)
                    .getUnlocalizedName(catalyst));

            // Send IMC message to NEI
            cpw.mods.fml.common.event.FMLInterModComms
                .sendMessage("NotEnoughItems", "registerCatalystInfo", catalystNBT);
        } catch (Exception e) {
            hellfirepvp.astralsorcery.common.util.LogHelper
                .warn("Failed to register catalyst for " + handlerName + ": " + e.getMessage());
        }
    }

    /**
     * Register a handler info to NEI
     *
     * @param handlerName  The recipe handler name
     * @param handlerClass The handler class name
     * @param width        Recipe UI width
     * @param height       Recipe UI height
     */
    private static void registerHandler(String handlerName, String handlerClass, int width, int height) {
        NBTTagCompound handlerNBT = new NBTTagCompound();
        handlerNBT.setString("handler", handlerName);
        handlerNBT.setString("modName", Constants.MODNAME);
        handlerNBT.setString("modId", Constants.MODID);
        handlerNBT.setBoolean("modRequired", true);

        // Send IMC message to NEI
        cpw.mods.fml.common.event.FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", handlerNBT);
    }

    /**
     * Register an item as recipe catalyst
     *
     * @param handlerName The recipe handler name
     * @param item        The item to register as catalyst
     */
    public static void addRecipeCatalyst(String handlerName, net.minecraft.item.Item item) {
        if (item == null) {
            return;
        }

        try {
            // Use explicit 3-parameter constructor with metadata=0 for 1.7.10
            ItemStack catalyst = new ItemStack(item, 1, 0);

            // Use reflection to call NEI API
            Class<?> neiAPI = Class.forName("codechicken.nei.api.API");
            java.lang.reflect.Method method = neiAPI.getMethod("addRecipeCatalyst", ItemStack.class, String.class);
            method.invoke(null, catalyst, handlerName);
        } catch (Exception e) {
            // NEI not installed or method not available
            hellfirepvp.astralsorcery.common.util.LogHelper.warn("Failed to register NEI catalyst: " + e.getMessage());
        }
    }
}
