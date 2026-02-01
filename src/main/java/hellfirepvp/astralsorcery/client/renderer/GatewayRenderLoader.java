/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Gateway Render Loader - Loads model and registers renderers
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import hellfirepvp.astralsorcery.client.renderer.item.GatewayItemRenderer;
import hellfirepvp.astralsorcery.client.renderer.tile.TESRCelestialGateway;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Render Loader for Celestial Gateway
 * <p>
 * Following Twist-Space-Technology pattern:
 * 1. Load OBJ model
 * 2. Create TESR with model
 * 3. Register ItemRenderer
 * <p>
 * Call this from ClientProxy.init() or RegistryRender.init()
 */
public class GatewayRenderLoader {

    /** Static model reference (shared between TESR and ItemRenderer) */
    public static IModelCustom MODEL_GATEWAY = null;

    /** Texture location */
    private static final ResourceLocation TEXTURE_GATEWAY = new ResourceLocation(
        "astralsorcery",
        "textures/models/celestialgateway/platform.png");

    /**
     * Initialize the Gateway rendering system
     * Call this from RegistryRender.init() or ClientProxy.init()
     */
    public static void init() {
        LogHelper.entry("GatewayRenderLoader.init");
        LogHelper.info("=== Initializing Celestial Gateway Rendering ===");

        // Step 1: Load OBJ model
        loadModel();

        // Step 2: Register TESR (world rendering)
        registerTileEntityRenderer();

        // Step 3: Register ItemRenderer (item/inventory rendering)
        registerItemRenderer();

        LogHelper.info("=== Celestial Gateway Rendering Complete ===");
        LogHelper.exit("GatewayRenderLoader.init");
    }

    /**
     * Load the OBJ model using Forge's AdvancedModelLoader
     */
    private static void loadModel() {
        LogHelper.info("[GatewayRenderLoader] Loading OBJ model...");

        try {
            ResourceLocation modelLocation = new ResourceLocation(
                "astralsorcery",
                "models/obj/block/celestial_gateway.obj");
            LogHelper.info("  Model path: " + modelLocation);

            MODEL_GATEWAY = AdvancedModelLoader.loadModel(modelLocation);

            if (MODEL_GATEWAY != null) {
                LogHelper.info("  ✓ Successfully loaded celestial_gateway.obj");
                LogHelper.info(
                    "  Model class: " + MODEL_GATEWAY.getClass()
                        .getName());
            } else {
                LogHelper.error("  ✗ Model loaded but is NULL!");
            }
        } catch (Exception e) {
            LogHelper.error("  ✗ Failed to load celestial_gateway.obj", e);
            MODEL_GATEWAY = null;
        }
    }

    /**
     * Register TileEntitySpecialRenderer for world rendering
     */
    private static void registerTileEntityRenderer() {
        LogHelper.info("[GatewayRenderLoader] Registering TESR...");

        if (MODEL_GATEWAY == null) {
            LogHelper.error("  ✗ Cannot register TESR - model is null!");
            return;
        }

        try {
            // Create TESR with model and texture
            // TESR constructor will register itself
            new TESRCelestialGateway(MODEL_GATEWAY, TEXTURE_GATEWAY);

            LogHelper.info("  ✓ TESR registered successfully");
        } catch (Exception e) {
            LogHelper.error("  ✗ Failed to register TESR", e);
        }
    }

    /**
     * Register ItemRenderer for item/inventory rendering
     */
    private static void registerItemRenderer() {
        LogHelper.info("[GatewayRenderLoader] Registering ItemRenderer...");

        if (MODEL_GATEWAY == null) {
            LogHelper.error("  ✗ Cannot register ItemRenderer - model is null!");
            return;
        }

        if (BlocksAS.celestialGateway == null) {
            LogHelper.error("  ✗ Cannot register ItemRenderer - BlocksAS.celestialGateway is null!");
            return;
        }

        try {
            // Get the Item for the block
            Item gatewayItem = Item.getItemFromBlock(BlocksAS.celestialGateway);

            if (gatewayItem == null) {
                LogHelper.error("  ✗ Failed to get Item for BlockCelestialGateway");
                return;
            }

            // Create ItemRenderer with model and texture
            GatewayItemRenderer itemRenderer = new GatewayItemRenderer(MODEL_GATEWAY, TEXTURE_GATEWAY);

            // Register the ItemRenderer
            MinecraftForgeClient.registerItemRenderer(gatewayItem, itemRenderer);

            LogHelper.info("  ✓ ItemRenderer registered successfully for: " + gatewayItem.getUnlocalizedName());
        } catch (Exception e) {
            LogHelper.error("  ✗ Failed to register ItemRenderer", e);
        }
    }
}
