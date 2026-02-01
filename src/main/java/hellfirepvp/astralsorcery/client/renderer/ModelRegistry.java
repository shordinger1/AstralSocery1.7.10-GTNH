/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Model Registry - Registers OBJ models for blocks and items
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Model Registry for OBJ models
 * <p>
 * Registers OBJ models for blocks that need custom rendering.
 * <p>
 * Usage in ClientProxy.init():
 * 
 * <pre>
 * ModelRegistry.registerModels();
 * </pre>
 */
@SideOnly(Side.CLIENT)
public class ModelRegistry {

    /** Map of block to their render IDs */
    private static final Map<Block, Integer> renderIds = new HashMap<>();

    /** Map of block to their OBJ model paths */
    private static final Map<Block, String> modelPaths = new HashMap<>();

    /** Base render ID for OBJ models */
    private static int baseRenderId = -1;

    /**
     * Register all OBJ models
     * Call this in ClientProxy.init()
     * <p>
     * NOTE: OBJ model rendering is now available but requires block classes to:
     * 1. Call ObjRenderHelper.getRenderId(modelPath) in their constructor or static initializer
     * 2. Override getRenderType() to return the render ID
     * <p>
     * Example:
     * 
     * <pre>
     * public class MyBlock extends Block {
     *     private static final int RENDER_ID = ObjRenderHelper.getRenderId("models/obj/block/myblock.obj");
     *
     *     public MyBlock() {
     *         super(Material.rock);
     *     }
     *
     *     {@literal @}Override
     *     public int getRenderType() {
     *         return RENDER_ID;
     *     }
     * }
     * </pre>
     */
    public static void registerModels() {
        LogHelper.info("OBJ Model System initialized. To enable OBJ rendering for a block:");
        LogHelper
            .info("  1. Add: private static final int RENDER_ID = ObjRenderHelper.getRenderId(\"path/to/model.obj\");");
        LogHelper.info("  2. Override getRenderType() to return RENDER_ID");

        // OBJ models have been converted from JSON format
        // Location: src/main/resources/assets/astralsorcery/models/obj/
        // Total converted: 49 block models + 0 item models (183 simple parent references skipped)

        // Available OBJ models:
        // - Altars: crafting_altar_tier_1.obj, crafting_altar_tier_2.obj, crafting_altar_tier_3.obj,
        // crafting_altar_tier_4.obj
        // - Attunement: attunement_altar.obj, attunement_relay.obj
        // - Crystals: celestial_cluster_stage_1-5.obj, gem_stage_1-3_*.obj
        // - Structures: celestial_gateway.obj, ritual_pedestal.obj, ritual_link.obj, tree_beacon.obj
        // - Machines: bore.obj, bore_head_*.obj, grindstone.obj, observatory.obj, prism.obj,
        // starlight_infuser.obj, starlight_well.obj, telescope.obj, starmapping_table.obj
        // - Decorative: cave_illuminator.obj, chalice.obj, lens_full.obj, lens_tesr.obj
        // - Pillars: infused_wood_column*.obj, marble_pillar*.obj, marble_black_pillar*.obj
        // - Other: ore.obj

        LogHelper.info("OBJ models are ready to use in: assets/astralsorcery/models/obj/");
    }

    /**
     * Register a block with an OBJ model
     *
     * @param block     The block to register
     * @param modelPath Path to the OBJ model (relative to assets/astralsorcery/)
     */
    private static void registerBlockModel(Block block, String modelPath) {
        if (block == null) {
            LogHelper.warn("Attempted to register null block with model: " + modelPath);
            return;
        }

        // Get render ID for this model
        int renderId = baseRenderId + renderIds.size();

        // Register renderer
        ObjBlockRenderer renderer = new ObjBlockRenderer(renderId, modelPath);
        RenderingRegistry.registerBlockHandler(renderer);

        // Store mappings
        renderIds.put(block, renderId);
        modelPaths.put(block, modelPath);

        LogHelper.debug(
            "Registered OBJ model: " + block
                .getUnlocalizedName() + " -> " + modelPath + " (render ID: " + renderId + ")");
    }

    /**
     * Get the render ID for a block
     */
    public static Integer getRenderId(Block block) {
        return renderIds.get(block);
    }

    /**
     * Get the model path for a block
     */
    public static String getModelPath(Block block) {
        return modelPaths.get(block);
    }

    /**
     * Check if a block has an OBJ model registered
     */
    public static boolean hasObjModel(Block block) {
        return modelPaths.containsKey(block);
    }
}
