/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * NEI Configuration - Hides items and configures NEI integration
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.nei;

import net.minecraft.item.ItemStack;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;

/**
 * NEI Configuration for Astral Sorcery
 * <p>
 * Implements IConfigureNEI to:
 * - Hide certain items/blocks from NEI
 * - Register mod identification
 * - Configure NEI display settings
 * <p>
 * Items to hide:
 * - Blocks without creative tabs (structural, fake tree, etc.)
 * - Technical items used internally
 * <p>
 * NOTE: Due to a GTNH/FastUtil incompatibility, item hiding may be disabled.
 * See: https://github.com/GTNewHorizons/GTNewHorizons/issues/XXXX
 */
@SideOnly(Side.CLIENT)
public class NEI_Config implements IConfigureNEI {

    /** Flag to enable/disable NEI item hiding (default: true) */
    private static final boolean ENABLE_NEI_HIDING = Boolean
        .parseBoolean(System.getProperty("astralsorcery.nei.enableHiding", "true"));

    @Override
    public void loadConfig() {
        System.out.println("[AstralSorcery NEI] Config loaded - registering recipe handlers");

        // Register independent handler classes for each altar level
        // Each handler class has its own recipe pool in NEI
        registerHandler(new ASNEIAltarDiscoveryHandler(), "Discovery Altar");
        registerHandler(new ASNEIAltarAttunementHandler(), "Attunement Altar");
        registerHandler(new ASNEIAltarConstellationHandler(), "Constellation Altar");
        registerHandler(new ASNEIAltarTraitHandler(), "Trait Altar");
        registerHandler(new ASNEIAltarBrillianceHandler(), "Brilliance Altar");

        // Register catalysts (altars that show recipes when clicked in NEI)
        // BlockAltar has 5 metadata variants (0-4), each should show its own recipes
        registerCatalystSafeMeta(BlocksAS.blockAltar, 0, "astralsorcery.altar.discovery");
        registerCatalystSafeMeta(BlocksAS.blockAltar, 1, "astralsorcery.altar.attunement");
        registerCatalystSafeMeta(BlocksAS.blockAltar, 2, "astralsorcery.altar.constellation_craft");
        registerCatalystSafeMeta(BlocksAS.blockAltar, 3, "astralsorcery.altar.trait_craft");
        registerCatalystSafeMeta(BlocksAS.blockAltar, 4, "astralsorcery.altar.brilliance");

        // Also register the standalone attunement altar
        registerCatalystSafe(BlocksAS.attunementAltar, "astralsorcery.altar.attunement");

        // NOTE: We don't hide items here to avoid FastUtil hash collisions during GTNH NEI init
        // The error "Index 386 out of bounds for length 257" occurs when GT's OreDictUnificator
        // processes ItemStacks during recipe cache building. To work around this, we delay
        // item hiding to a later event handler.
    }

    /**
     * Register a NEI handler
     */
    private void registerHandler(codechicken.nei.recipe.TemplateRecipeHandler handler, String handlerName) {
        try {
            API.registerRecipeHandler(handler);
            API.registerUsageHandler(handler);
            System.out.println("[AstralSorcery NEI] Registered handler: " + handlerName);
        } catch (Exception e) {
            System.err.println("[AstralSorcery NEI] Failed to register handler " + handlerName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Safely register a recipe catalyst
     */
    private void registerCatalystSafe(net.minecraft.block.Block block, String handlerName) {
        if (block == null) {
            return;
        }

        try {
            ItemStack catalyst = new ItemStack(block, 1, 0);
            API.addRecipeCatalyst(catalyst, handlerName);
            System.out.println("[AstralSorcery NEI] Registered catalyst: " + handlerName);
        } catch (Exception e) {
            System.err
                .println("[AstralSorcery NEI] Failed to register catalyst " + handlerName + ": " + e.getMessage());
        }
    }

    /**
     * Safely register a recipe catalyst with specific metadata
     * <p>
     * NOTE: This is only used for BlockAltar variants (metadata 0-4).
     * These are blocks, not items, so metadata represents block variants, not durability.
     * <p>
     * For ITEM tools where metadata = durability, we would need to check
     * if the item has subtypes (getHasSubtypes()), but crystal tools use setMaxDamage(0)
     * and store durability in NBT, so they don't have this issue.
     */
    private void registerCatalystSafeMeta(net.minecraft.block.Block block, int metadata, String handlerName) {
        if (block == null) {
            return;
        }

        try {
            // Register the block variant with specific metadata
            // BlockAltar has 5 variants (0-4), each shows different altar level recipes
            ItemStack catalyst = new ItemStack(block, 1, metadata);
            API.addRecipeCatalyst(catalyst, handlerName);
            System.out.println("[AstralSorcery NEI] Registered catalyst: " + handlerName + " (meta=" + metadata + ")");
        } catch (Exception e) {
            System.err.println(
                "[AstralSorcery NEI] Failed to register catalyst " + handlerName
                    + " (meta="
                    + metadata
                    + "): "
                    + e.getMessage());
        }
    }

    /**
     * Hide blocks that shouldn't appear in NEI
     * <p>
     * Called from CommonProxy.postInit() after NEI has fully initialized.
     * This avoids FastUtil hash collisions during GTNH initialization.
     * <p>
     * Defensively hide blocks with try-catch to prevent FastUtil hash collisions.
     */
    public static void hideTechnicalBlocksLate() {
        if (!ENABLE_NEI_HIDING) {
            System.out.println("[AstralSorcery NEI] Item hiding disabled via system property");
            return;
        }

        System.out.println("[AstralSorcery NEI] Hiding technical blocks (late init)...");

        // These blocks have no creative tab and are for internal use
        // Wrap each hideItem call in try-catch to prevent cascading failures
        hideBlockSafe(BlocksAS.blockStructural, "blockStructural");
        hideBlockSafe(BlocksAS.blockFakeTree, "blockFakeTree");
        hideBlockSafe(BlocksAS.blockVanishing, "blockVanishing");
        hideBlockSafe(BlocksAS.ritualLink, "ritualLink");
        hideBlockSafe(BlocksAS.treeBeacon, "treeBeacon");
        hideBlockSafe(BlocksAS.translucentBlock, "translucentBlock");

        System.out.println("[AstralSorcery NEI] Finished hiding technical blocks");
    }

    /**
     * Safely hide a block with full exception handling
     */
    private static void hideBlockSafe(net.minecraft.block.Block block, String name) {
        if (block == null) {
            return;
        }

        try {
            ItemStack stack = new ItemStack(block, 1, 0);
            API.hideItem(stack);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            // FastUtil hash collision - log and continue
            System.err.println("[AstralSorcery NEI] FastUtil hash collision hiding " + name + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[AstralSorcery NEI] Failed to hide " + name + ": " + e.getMessage());
        }
    }

    /**
     * Hide items that shouldn't appear in NEI
     */
    private void hideTechnicalItems() {
        // TODO: Add items to hide if needed
        // Example:
        // if (ItemsAS.technicalItem != null) {
        // API.hideItem(new ItemStack(ItemsAS.technicalItem, 1));
        // }
    }

    @Override
    public String getName() {
        return "Astral Sorcery";
    }

    @Override
    public String getVersion() {
        return "1.7.10";
    }

    /**
     * NEI configuration priority
     * Lower numbers = higher priority
     */
    @Override
    public int hashCode() {
        return 12345; // Random unique hash
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NEI_Config;
    }
}
