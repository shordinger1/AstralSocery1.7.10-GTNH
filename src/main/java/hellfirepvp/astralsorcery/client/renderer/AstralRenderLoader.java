/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Central Render Loader - Loads all OBJ models and registers renderers
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Central Render Loader for Astral Sorcery
 * <p>
 * Following TST pattern, this class:
 * 1. Loads all OBJ models at startup
 * 2. Registers TESRs for blocks
 * 3. Registers ItemRenderers for blocks
 * <p>
 * Call from RegistryRender.init() or ClientProxy.init()
 */
public class AstralRenderLoader {

    /** Static model storage - shared between TESR and ItemRenderer */
    private static final Map<String, IModelCustom> MODELS = new HashMap<>();

    /**
     * Initialize all renderers
     * Call this from RegistryRender.init()
     */
    public static void init() {
        LogHelper.entry("AstralRenderLoader.init");
        LogHelper.info("=== Initializing Astral Sorcery Rendering System ===");

        int loadedCount = 0;
        int registeredCount = 0;

        // Load all OBJ models
        loadedCount = loadAllModels();
        LogHelper.info("Loaded " + loadedCount + " OBJ models");

        // Register renderers for each block type
        // Note: These are called individually to allow error handling per block
        registeredCount += registerGatewayRenderer();
        registeredCount += registerAttunementAltarRenderer();
        registeredCount += registerAttunementRelayRenderer();
        registeredCount += registerStarlightWellRenderer();
        registeredCount += registerGrindstoneRenderer();
        registeredCount += registerObservatoryRenderer();
        // P1 priority renderers
        registeredCount += registerCraftingAltarRenderer();
        registeredCount += registerRitualPedestalRenderer();
        registeredCount += registerBoreRenderer();
        registeredCount += registerPrismRenderer();
        registeredCount += registerLensRenderer();
        // Missing renderers (just discovered)
        registeredCount += registerChaliceRenderer();
        registeredCount += registerAltarRenderer();

        // P0 TESR enhancements - register new TESR classes
        registeredCount += registerCollectorCrystalRenderer();
        registeredCount += registerCrystalLensTESR();
        registeredCount += registerStarlightInfuserRenderer();

        LogHelper.info("=== Rendering System Complete ===");
        LogHelper.info("Total: " + loadedCount + " models loaded, " + registeredCount + " renderers registered");
        LogHelper.exit("AstralRenderLoader.init");
    }

    /**
     * Load all OBJ models
     */
    private static int loadAllModels() {
        int count = 0;
        String[] models = {
            // Core blocks
            "celestial_gateway", "attunement_altar", "attunement_relay", "starlight_well", "grindstone", "observatory",

            // Altars (P1)
            "crafting_altar_tier_1", "crafting_altar_tier_2", "crafting_altar_tier_3", "crafting_altar_tier_4",

            // Ritual blocks (P1)
            "ritual_pedestal", "ritual_link", "tree_beacon",

            // Machines (P1/P2)
            "prism", "lens_tesr", "starlight_infuser", "telescope", "starmapping_table", "cave_illuminator",

            // Bore (P1)
            "bore", "bore_head_liquid", "bore_head_ore", "bore_head_vortex",

            // Decorative
            "chalice", "lens_full",

            // Collector crystal (P0 - starlight network)
            "collector_crystal",
        };

        for (String modelName : models) {
            if (loadModel(modelName)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Load a single OBJ model
     */
    private static boolean loadModel(String modelName) {
        try {
            ResourceLocation modelLoc = new ResourceLocation("astralsorcery", "models/obj/block/" + modelName + ".obj");
            IModelCustom model = AdvancedModelLoader.loadModel(modelLoc);

            if (model != null) {
                MODELS.put(modelName, model);
                LogHelper.info("[AstralRenderLoader] ✓ Loaded model: " + modelName + ".obj");
                return true;
            } else {
                LogHelper.warn("[AstralRenderLoader] ✗ Model is null: " + modelName + ".obj");
                return false;
            }
        } catch (Exception e) {
            LogHelper.error("[AstralRenderLoader] ✗ Failed to load model: " + modelName + ".obj", e);
            return false;
        }
    }

    /**
     * Get a loaded model
     */
    public static IModelCustom getModel(String modelName) {
        return MODELS.get(modelName);
    }

    /**
     * Register Celestial Gateway renderer
     */
    private static int registerGatewayRenderer() {
        try {
            IModelCustom model = getModel("celestial_gateway");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation(
                "astralsorcery",
                "textures/models/celestialgateway/platform.png");

            new hellfirepvp.astralsorcery.client.renderer.tile.TESRCelestialGateway(model, texture);

            // ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.GatewayItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.GatewayItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.celestialGateway);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered CelestialGateway renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register CelestialGateway renderer", e);
            return 0;
        }
    }

    /**
     * Register Attunement Altar renderer
     */
    private static int registerAttunementAltarRenderer() {
        try {
            IModelCustom model = getModel("attunement_altar");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation(
                "astralsorcery",
                "textures/models/altar/altar_attunement.png");

            // Create TESR with model and texture
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRAttunementAltar(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.AttunementAltarItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.AttunementAltarItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.attunementAltar);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered AttunementAltar renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register AttunementAltar renderer", e);
            return 0;
        }
    }

    /**
     * Register Attunement Relay renderer
     */
    private static int registerAttunementRelayRenderer() {
        try {
            IModelCustom model = getModel("attunement_relay");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation(
                "astralsorcery",
                "textures/models/attunement_relay/attunement_relay.png");

            // Create TESR with model and texture
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRAttunementRelay(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.AttunementRelayItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.AttunementRelayItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.attunementRelay);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered AttunementRelay renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register AttunementRelay renderer", e);
            return 0;
        }
    }

    /**
     * Register Starlight Well renderer
     */
    private static int registerStarlightWellRenderer() {
        try {
            IModelCustom model = getModel("starlight_well");
            if (model == null) return 0;

            // Use correct texture path (matching 1.12.2 JSON model)
            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/lightwell/lightwell.png");

            // Set static model/texture and register TESR (1.7.10 pattern)
            hellfirepvp.astralsorcery.client.renderer.tile.TESRWell.setModelAndTexture(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.WellItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.WellItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockWell);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered StarlightWell renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register StarlightWell renderer", e);
            return 0;
        }
    }

    /**
     * Register Grindstone renderer (also handles Telescope)
     */
    private static int registerGrindstoneRenderer() {
        try {
            IModelCustom grindstoneModel = getModel("grindstone");
            IModelCustom telescopeModel = getModel("telescope");
            if (grindstoneModel == null) return 0;

            // Use correct texture path from JSON model
            ResourceLocation grindstoneTexture = new ResourceLocation("astralsorcery", "textures/models/grindstone/grindstone.png");
            ResourceLocation telescopeTexture = new ResourceLocation("astralsorcery", "textures/models/base/telescope.png");

            // Create TESR with both models (Grindstone + Telescope)
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRGrindstone(grindstoneModel, grindstoneTexture, telescopeModel, telescopeTexture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.GrindstoneItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.GrindstoneItemRenderer(
                grindstoneModel,
                grindstoneTexture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockMachine);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered Grindstone + Telescope renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register Grindstone renderer", e);
            return 0;
        }
    }

    /**
     * Register Observatory renderer
     */
    private static int registerObservatoryRenderer() {
        try {
            IModelCustom model = getModel("observatory");
            if (model == null) return 0;

            // Observatory uses dual textures
            ResourceLocation textureBase = new ResourceLocation("astralsorcery", "textures/models/observatory/observatory_base.png");
            ResourceLocation textureSeatTube = new ResourceLocation("astralsorcery", "textures/models/observatory/observatory_seat_tube.png");

            // Create TESR with model and dual textures
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRObservatory(model, textureBase, textureSeatTube);

            // Create and register ItemRenderer (uses base texture only)
            hellfirepvp.astralsorcery.client.renderer.item.ObservatoryItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.ObservatoryItemRenderer(
                model,
                textureBase);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockObservatory);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered Observatory renderer (dual-texture: base + seat_tube)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register Observatory renderer", e);
            return 0;
        }
    }

    /**
     * Register Crafting Altar renderer (all 4 tiers)
     */
    private static int registerCraftingAltarRenderer() {
        try {
            IModelCustom modelTier1 = getModel("crafting_altar_tier_1");
            IModelCustom modelTier2 = getModel("crafting_altar_tier_2");
            IModelCustom modelTier3 = getModel("crafting_altar_tier_3");
            IModelCustom modelTier4 = getModel("crafting_altar_tier_4");

            if (modelTier1 == null || modelTier2 == null || modelTier3 == null || modelTier4 == null) {
                return 0;
            }

            // Create multi-texture TESR for all tiers
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRCraftingAltar(
                modelTier1, modelTier2, modelTier3, modelTier4);

            // Create and register ItemRenderer for all 4 tiers
            hellfirepvp.astralsorcery.client.renderer.item.CraftingAltarItemRenderer itemRenderer =
                new hellfirepvp.astralsorcery.client.renderer.item.CraftingAltarItemRenderer(
                    modelTier1, modelTier2, modelTier3, modelTier4,
                    new ResourceLocation("astralsorcery", "textures/models/altar/altar_1_side.png"));
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockAltar);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered CraftingAltar renderer (4 tiers, multi-texture)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register CraftingAltar renderer", e);
            return 0;
        }
    }

    /**
     * Register Ritual Pedestal renderer
     */
    private static int registerRitualPedestalRenderer() {
        try {
            IModelCustom model = getModel("ritual_pedestal");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation(
                "astralsorcery",
                "textures/models/ritual/ritual_pedestal.png");

            // Register TESR for TileRitualPedestal
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRRitualPedestal(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.RitualPedestalItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.RitualPedestalItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.ritualPedestal);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered RitualPedestal renderer (TESR + ItemRenderer)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register RitualPedestal renderer", e);
            return 0;
        }
    }

    /**
     * Register Bore renderer
     */
    private static int registerBoreRenderer() {
        try {
            IModelCustom model = getModel("bore");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/bore/bore_side.png");

            // Create TESR with model and texture
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRBore(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.BoreItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.BoreItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockBore);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered Bore renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register Bore renderer", e);
            return 0;
        }
    }

    /**
     * Register Prism renderer
     */
    private static int registerPrismRenderer() {
        try {
            IModelCustom model = getModel("prism");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/prism/prism_side.png");

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.PrismItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.PrismItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.lensPrism);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered Prism renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register Prism renderer", e);
            return 0;
        }
    }

    /**
     * Register Lens renderer
     */
    private static int registerLensRenderer() {
        try {
            IModelCustom model = getModel("lens_tesr");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/lens/lens.png");

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.LensItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.LensItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.lens);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered Lens renderer");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register Lens renderer", e);
            return 0;
        }
    }

    /**
     * Register Chalice renderer
     */
    private static int registerChaliceRenderer() {
        try {
            IModelCustom model = getModel("chalice");
            if (model == null) {
                LogHelper.warn("[AstralRenderLoader] ✗ Chalice model is null, skipping TESR registration");
                return 0;
            }

            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/chalice/base.png");

            // Register TESR for TileChalice
            new hellfirepvp.astralsorcery.client.renderer.tile.TESRChalice(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.ChaliceItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.ChaliceItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockChalice);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered Chalice renderer (TESR + ItemRenderer)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register Chalice renderer", e);
            return 0;
        }
    }

    /**
     * Register Discovery Altar renderer
     */
    private static int registerAltarRenderer() {
        try {
            IModelCustom model = getModel("crafting_altar_tier_1");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation(
                "astralsorcery",
                "textures/models/altar/altar_1_side.png");

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.AltarItemRenderer itemRenderer = new hellfirepvp.astralsorcery.client.renderer.item.AltarItemRenderer(
                model,
                texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockAltar);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered Altar renderer (Discovery Altar)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register Altar renderer", e);
            return 0;
        }
    }

    /**
     * Register Collector Crystal renderer
     */
    private static int registerCollectorCrystalRenderer() {
        try {
            IModelCustom model = getModel("collector_crystal");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/crystal/celestial_cluster_stage_5.png");

            // Set static model/texture and register TESR (1.7.10 pattern)
            hellfirepvp.astralsorcery.client.renderer.tile.TESRCollectorCrystal.setModelAndTexture(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.CollectorCrystalItemRenderer itemRenderer =
                new hellfirepvp.astralsorcery.client.renderer.item.CollectorCrystalItemRenderer(model, texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.collectorCrystal);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered CollectorCrystal renderer (TESR + ItemRenderer)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register CollectorCrystal renderer", e);
            return 0;
        }
    }

    /**
     * Register Crystal Lens TESR
     * Note: registerLensRenderer() only registers ItemRenderer, this adds the TESR
     */
    private static int registerCrystalLensTESR() {
        try {
            IModelCustom model = getModel("lens_tesr");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/lens/lens.png");

            // Set static model/texture and register TESR (1.7.10 pattern)
            hellfirepvp.astralsorcery.client.renderer.tile.TESRCrystalLens.setModelAndTexture(model, texture);

            LogHelper.info("✓ Registered CrystalLens TESR (ItemRenderer already registered)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register CrystalLens TESR", e);
            return 0;
        }
    }

    /**
     * Register Starlight Infuser renderer
     */
    private static int registerStarlightInfuserRenderer() {
        try {
            IModelCustom model = getModel("starlight_infuser");
            if (model == null) return 0;

            ResourceLocation texture = new ResourceLocation("astralsorcery", "textures/models/infuser/infuser.png");

            // Set static model/texture and register TESR (1.7.10 pattern)
            hellfirepvp.astralsorcery.client.renderer.tile.TESRStarlightInfuser.setModelAndTexture(model, texture);

            // Create and register ItemRenderer
            hellfirepvp.astralsorcery.client.renderer.item.StarlightInfuserItemRenderer itemRenderer =
                new hellfirepvp.astralsorcery.client.renderer.item.StarlightInfuserItemRenderer(model, texture);
            Item item = Item.getItemFromBlock(hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.starlightInfuser);
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);

            LogHelper.info("✓ Registered StarlightInfuser renderer (TESR + ItemRenderer)");
            return 1;
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register StarlightInfuser renderer", e);
            return 0;
        }
    }
}
