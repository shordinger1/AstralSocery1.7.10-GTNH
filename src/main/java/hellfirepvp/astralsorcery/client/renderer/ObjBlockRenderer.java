/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * OBJ Block Renderer - Renders blocks using OBJ models
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * OBJ Block Renderer for 1.7.10
 * <p>
 * Renders blocks using OBJ models instead of the standard Minecraft cube rendering.
 * <p>
 * Usage:
 * 
 * <pre>
 * // In ClientProxy.init()
 * RenderingRegistry.registerBlockHandler(new ObjBlockRenderer(renderId, "models/obj/block/altar.obj"));
 * </pre>
 */
@SideOnly(Side.CLIENT)
public class ObjBlockRenderer implements ISimpleBlockRenderingHandler {

    private final int renderId;
    private final String modelPath;
    private ObjModelLoader.ObjModel model;

    /**
     * Create an OBJ block renderer
     *
     * @param renderId  The render ID (obtained from RenderingRegistry.getNextAvailableRenderId())
     * @param modelPath Path to the OBJ model (e.g., "models/obj/block/altar.obj")
     */
    public ObjBlockRenderer(int renderId, String modelPath) {
        this.renderId = renderId;
        this.modelPath = modelPath;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // Load model if needed
        if (model == null) {
            model = ObjModelLoader.INSTANCE.loadModel(modelPath);
        }

        if (model == null) {
            // Fallback to standard rendering
            renderer.renderBlockAsItem(block, metadata, 1.0F);
            return;
        }

        // Render in inventory
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        model.render();
        tessellator.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // Load model if needed
        if (model == null) {
            model = ObjModelLoader.INSTANCE.loadModel(modelPath);
        }

        if (model == null) {
            // Fallback to standard rendering
            return false;
        }

        // Render in world
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        model.render();
        tessellator.draw();

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
