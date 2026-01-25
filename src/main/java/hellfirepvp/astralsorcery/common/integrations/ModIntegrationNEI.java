/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.integrations.mods.nei.*;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * NEI (Not Enough Items) Integration for 1.7.10
 *
 * In 1.7.10, NEI uses codechicken.nei API which is completely different from JEI.
 * This implementation provides full recipe integration for Astral Sorcery machines.
 */
@SideOnly(Side.CLIENT)
public class ModIntegrationNEI {

    private static boolean initialized = false;

    /**
     * Called during FML initialization to set up NEI integration
     */
    @Optional.Method(modid = "NotEnoughItems")
    public static void init() {
        if (!initialized) {
            registerNEIRecipeHandlers();
            hideNEIItems();
            initialized = true;
        }
    }

    /**
     * Register all Astral Sorcery recipe handlers with NEI
     */
    @Optional.Method(modid = "NotEnoughItems")
    private static void registerNEIRecipeHandlers() {
        try {
            // Register Light Well handler
            codechicken.nei.api.API.registerRecipeHandler(new LightWellRecipeHandler());
            codechicken.nei.api.API.registerUsageHandler(new LightWellRecipeHandler());

            // Register Grindstone handler
            codechicken.nei.api.API.registerRecipeHandler(new GrindstoneRecipeHandler());
            codechicken.nei.api.API.registerUsageHandler(new GrindstoneRecipeHandler());

            // Register Starlight Infuser handler
            codechicken.nei.api.API.registerRecipeHandler(new InfuserRecipeHandler());
            codechicken.nei.api.API.registerUsageHandler(new InfuserRecipeHandler());

            // Register Transmutation handler
            codechicken.nei.api.API.registerRecipeHandler(new TransmutationRecipeHandler());
            codechicken.nei.api.API.registerUsageHandler(new TransmutationRecipeHandler());

            // Register Altar handlers for each tier
            for (TileAltar.AltarLevel level : TileAltar.AltarLevel.values()) {
                AltarRecipeHandler handler = new AltarRecipeHandler(level);
                codechicken.nei.api.API.registerRecipeHandler(handler);
                codechicken.nei.api.API.registerUsageHandler(handler);
            }

            // Register catalysts (items that show recipe categories in NEI)
            registerCatalysts();

        } catch (Exception e) {
            hellfirepvp.astralsorcery.AstralSorcery.log
                .warn("Failed to register NEI recipe handlers: " + e.getMessage());
        }
    }

    /**
     * Register catalyst items that show recipe categories when clicked in NEI
     */
    private static void registerCatalysts() {
        try {
            // Light Well catalyst
            codechicken.nei.api.API.addRecipeCatalyst(new ItemStack(BlocksAS.blockWell), "astralsorcery.lightwell");

            // Grindstone catalyst
            codechicken.nei.api.API.addRecipeCatalyst(
                hellfirepvp.astralsorcery.common.block.BlockMachine.MachineType.GRINDSTONE.asStack(),
                "astralsorcery.grindstone");

            // Starlight Infuser catalyst
            codechicken.nei.api.API.addRecipeCatalyst(new ItemStack(BlocksAS.starlightInfuser), "astralsorcery.infuser");

            // Transmutation catalysts (lens items)
            codechicken.nei.api.API.addRecipeCatalyst(new ItemStack(BlocksAS.lens), "astralsorcery.lightTransmutation");
            codechicken.nei.api.API.addRecipeCatalyst(new ItemStack(BlocksAS.lensPrism), "astralsorcery.lightTransmutation");

            // Altar catalysts for each tier
            codechicken.nei.api.API.addRecipeCatalyst(
                new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()),
                "astralsorcery.altar.discovery");
            codechicken.nei.api.API.addRecipeCatalyst(
                new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()),
                "astralsorcery.altar.attunement");
            codechicken.nei.api.API.addRecipeCatalyst(
                new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()),
                "astralsorcery.altar.constellation");
            codechicken.nei.api.API.addRecipeCatalyst(
                new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()),
                "astralsorcery.altar.trait");

        } catch (Exception e) {
            hellfirepvp.astralsorcery.AstralSorcery.log.warn("Failed to register NEI catalysts: " + e.getMessage());
        }
    }

    /**
     * Hide items from NEI using IMC
     */
    private static void hideNEIItems() {
        // Hide items from NEI using IMC
        hideItem(new ItemStack(ItemsAS.knowledgeFragment));
        hideItem(new ItemStack(ItemsAS.fragmentCapsule));
        hideItem(new ItemStack(BlocksAS.blockFakeTree));
        hideItem(new ItemStack(BlocksAS.translucentBlock));
        hideItem(new ItemStack(BlocksAS.blockVanishing));
        hideItem(new ItemStack(BlocksAS.blockStructural));
        hideItem(new ItemStack(BlocksAS.blockPortalNode));

        // Hide the T4 altar (metadata 4)
        hideItemWithMeta(new ItemStack(BlocksAS.blockAltar, 1, 4), 4);
    }

    private static void hideItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return;

        try {
            String itemName = stack.getItem()
                .getUnlocalizedName();
            // Send IMC to NEI to hide this item
            FMLInterModComms.sendMessage("NotEnoughItems", "hide", itemName);
        } catch (Exception e) {
            // Silently fail if item can't be hidden
        }
    }

    private static void hideItemWithMeta(ItemStack stack, int meta) {
        if (stack == null || stack.getItem() == null) return;

        try {
            String itemName = stack.getItem()
                .getUnlocalizedName();
            // Send IMC to NEI to hide this item with metadata
            FMLInterModComms.sendMessage("NotEnoughItems", "hide", itemName + ":" + meta);
        } catch (Exception e) {
            // Silently fail if item can't be hidden
        }
    }

    /**
     * Check if NEI is loaded
     */
    public static boolean isNEILoaded() {
        try {
            Class.forName("codechicken.nei.api.API");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * NEI Compatibility wrapper
     */
    public static class NEICompat {

        @Optional.Method(modid = "NotEnoughItems")
        public static void init() {
            if (!initialized && isNEILoaded()) {
                ModIntegrationNEI.init();
            }
        }
    }

}
