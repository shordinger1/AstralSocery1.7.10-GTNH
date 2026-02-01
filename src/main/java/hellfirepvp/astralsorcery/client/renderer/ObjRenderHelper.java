/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * OBJ Render Helper - Helper for blocks to use OBJ rendering
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Helper class for blocks to use OBJ rendering
 * <p>
 * Blocks can call this in their constructor to get their OBJ render ID:
 * 
 * <pre>
 * public class MyBlock extends Block {
 *     private static final int RENDER_ID = ObjRenderHelper.getRenderId("models/obj/block/myblock.obj");
 *
 *     public MyBlock() {
 *         super(Material.rock);
 *         // ... other setup
 *     }
 *
 *     {@literal @}Override
 *     public int getRenderType() {
 *         return RENDER_ID;
 *     }
 * }
 * </pre>
 */
@SideOnly(Side.CLIENT)
public class ObjRenderHelper {

    private static final Map<String, Integer> renderIdMap = new HashMap<>();
    private static int baseRenderId = -1;

    /**
     * Get or create a render ID for a model path
     *
     * @param modelPath Path to the OBJ model (e.g., "models/obj/block/myblock.obj")
     * @return The render ID for this model
     */
    public static int getRenderId(String modelPath) {
        // Check if already registered
        if (renderIdMap.containsKey(modelPath)) {
            return renderIdMap.get(modelPath);
        }

        // Initialize base render ID if needed
        if (baseRenderId == -1) {
            baseRenderId = cpw.mods.fml.client.registry.RenderingRegistry.getNextAvailableRenderId();
        }

        // Get new render ID
        int renderId = baseRenderId + renderIdMap.size();

        // Register renderer
        ObjBlockRenderer renderer = new ObjBlockRenderer(renderId, modelPath);
        cpw.mods.fml.client.registry.RenderingRegistry.registerBlockHandler(renderer);

        // Store mapping
        renderIdMap.put(modelPath, renderId);

        return renderId;
    }

    /**
     * Check if a model path has been registered
     */
    public static boolean hasRenderId(String modelPath) {
        return renderIdMap.containsKey(modelPath);
    }
}
