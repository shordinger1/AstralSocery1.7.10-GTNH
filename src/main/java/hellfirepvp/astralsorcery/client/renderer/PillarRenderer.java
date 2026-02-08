/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Multi-texture OBJ Pillar Renderer - Handles marble and wood pillars
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Multi-texture OBJ Pillar Renderer
 * <p>
 * Renders pillar blocks with OBJ models and multi-texture support.
 * Handles metadata-based model selection for PILLAR, PILLAR_TOP, and PILLAR_BOTTOM states.
 * <p>
 * Features:
 * <ul>
 * <li>Selects correct model based on metadata (2=PILLAR, 3=PILLAR_TOP, 4=PILLAR_BOTTOM)</li>
 * <li>Multi-texture rendering: top, bottom, and side use different textures</li>
 * <li>Automatic pillar connection detection updates metadata</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class PillarRenderer implements ISimpleBlockRenderingHandler {

    private final int renderId;
    private final IModelCustom modelPillar;
    private final IModelCustom modelPillarTop;
    private final IModelCustom modelPillarBottom;
    private final ResourceLocation texTop;
    private final ResourceLocation texSide;
    private final ResourceLocation texBottom;
    private final ResourceLocation texInner;
    private final ResourceLocation texUpdown;
    private final String texturePrefix;

    /**
     * Create a pillar renderer
     *
     * @param renderId          Render ID from RenderingRegistry
     * @param modelPillar       Base pillar model (metadata 2)
     * @param modelPillarTop    Pillar top model (metadata 3)
     * @param modelPillarBottom Pillar bottom model (metadata 4)
     * @param texturePrefix     Texture prefix for group names (e.g., "marble_pillar", "black_marble_pillar",
     *                          "wood_column")
     * @param texTop            Top section side texture resource location
     * @param texSide           Middle pillar side texture resource location
     * @param texBottom         Bottom section side texture resource location
     * @param texInner          Inner end cap texture resource location
     * @param texUpdown         Top/bottom cap texture resource location
     */
    public PillarRenderer(int renderId, IModelCustom modelPillar, IModelCustom modelPillarTop,
        IModelCustom modelPillarBottom, String texturePrefix, ResourceLocation texTop, ResourceLocation texSide,
        ResourceLocation texBottom, ResourceLocation texInner, ResourceLocation texUpdown) {
        this.renderId = renderId;
        this.modelPillar = modelPillar;
        this.modelPillarTop = modelPillarTop;
        this.modelPillarBottom = modelPillarBottom;
        this.texturePrefix = texturePrefix;
        this.texTop = texTop;
        this.texSide = texSide;
        this.texBottom = texBottom;
        this.texInner = texInner;
        this.texUpdown = texUpdown;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // NOTE: Pillar variants (2, 3, 4) are handled by PillarItemRenderer
        // This method only handles non-pillar variants (0, 1, 5, 6, 7, 8)
        // When PillarItemRenderer.handleRenderType returns false for non-pillar variants,
        // Minecraft falls back to this ISimpleBlockRenderingHandler method

        if (metadata != 2 && metadata != 3 && metadata != 4) {
            // Non-pillar variants: use icon-based rendering
            renderIconInventoryBlock(block, metadata, renderer);
            return;
        }

        // Pillar variants should be handled by PillarItemRenderer, not here
        // But if we reach here (fallback), render with OBJ model
        IModelCustom model = getModelForMetadata(2); // Always use base pillar in inventory

        if (model == null) {
            // Fallback: don't render anything
            return;
        }

        // Setup transformations for inventory rendering
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Scale and rotate for better inventory display
        GL11.glScaled(0.5, 0.5, 0.5);
        GL11.glRotated(180, 0, 1, 0);
        GL11.glRotated(15, 1, 0, 0);

        // Render with multi-texture support
        renderModelWithTextures(model);

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // Get metadata
        int metadata = world.getBlockMetadata(x, y, z);

        // Only render pillar variants (2, 3, 4) with OBJ models
        // Other variants (0, 1, 5, 6, 7, 8) use standard icon-based rendering
        if (metadata != 2 && metadata != 3 && metadata != 4) {
            // Use standard Minecraft block rendering for non-pillar variants
            // This ensures proper lighting, transparency, and adjacent block rendering
            return renderer.renderStandardBlock(block, x, y, z);
        }

        // Get model based on metadata
        IModelCustom model = getModelForMetadata(metadata);

        if (model == null) {
            // Fallback to standard rendering
            return renderer.renderStandardBlock(block, x, y, z);
        }

        // For OBJ model rendering in world, position is already set by the rendering system
        // We only need to bind textures and render
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Render with multi-texture support
        renderModelWithTextures(model);

        GL11.glPopMatrix();

        return true;
    }

    /**
     * Render model with multi-texture support using Forge's WavefrontObject
     * <p>
     * Uses renderPart() method to render different groups with different textures.
     * Group names must match exactly (e.g., "marble_pillar_inner", "marble_pillar")
     */
    private void renderModelWithTextures(IModelCustom model) {
        // Render inner end cap texture (for pillar connections)
        net.minecraft.client.Minecraft.getMinecraft().renderEngine.bindTexture(texInner);
        try {
            model.renderPart(texturePrefix + "_inner");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render top/bottom cap texture (for PILLAR_TOP and PILLAR_BOTTOM models)
        net.minecraft.client.Minecraft.getMinecraft().renderEngine.bindTexture(texUpdown);
        try {
            model.renderPart(texturePrefix + "_updown");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render pillar side texture (main pillar body)
        net.minecraft.client.Minecraft.getMinecraft().renderEngine.bindTexture(texSide);
        try {
            model.renderPart(texturePrefix);
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render top section side texture (for PILLAR_TOP model)
        net.minecraft.client.Minecraft.getMinecraft().renderEngine.bindTexture(texTop);
        try {
            model.renderPart(texturePrefix + "_top");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render bottom section side texture (for PILLAR_BOTTOM model)
        net.minecraft.client.Minecraft.getMinecraft().renderEngine.bindTexture(texBottom);
        try {
            model.renderPart(texturePrefix + "_bottom");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render engraved texture (for PILLAR_BOTTOM model base)
        net.minecraft.client.Minecraft.getMinecraft().renderEngine.bindTexture(texBottom);
        try {
            model.renderPart("marble_engraved");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }
    }

    /**
     * Render non-pillar variants using standard icon-based rendering
     * <p>
     * This prevents infinite recursion when rendering non-pillar variants.
     * Since BlockMarble.getRenderType() returns PILLAR_RENDER_ID for all variants,
     * we need to manually render non-pillar variants (0,1,5,6,7,8) using their icons.
     *
     * @param block    The block to render
     * @param metadata Block metadata (variant)
     * @param renderer RenderBlocks instance
     */
    private void renderIconInventoryBlock(Block block, int metadata, RenderBlocks renderer) {
        // Directly use fallback method to avoid recursion
        // renderBlockAsItem() would call back to renderInventoryBlock() causing infinite loop
        renderIconInventoryBlockFallback(block, metadata);
    }

    /**
     * Fallback method for inventory rendering when standard method fails
     * <p>
     * This is a simplified icon-based renderer that directly renders the block
     * without going through RenderBlocks.renderBlockAsItem()
     *
     * @param block    The block to render
     * @param metadata Block metadata (variant)
     */
    private void renderIconInventoryBlockFallback(Block block, int metadata) {
        try {
            net.minecraft.util.IIcon icon = block.getIcon(0, metadata);
            if (icon == null) {
                return;
            }

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            // Standard inventory transformations
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(15F, 1.0F, 0.0F, 0.0F);

            // Use RenderBlocks utility to render a standard block
            RenderBlocks renderBlocks = new RenderBlocks();
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();

            // Bind texture map
            net.minecraft.client.Minecraft.getMinecraft().renderEngine
                .bindTexture(net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture);

            // Render all 6 faces using the block's icons
            for (int i = 0; i < 6; i++) {
                net.minecraft.util.IIcon faceIcon = block.getIcon(i, metadata);
                if (faceIcon == null) {
                    faceIcon = icon;
                }
                renderFaceStandard(tessellator, i, faceIcon);
            }

            tessellator.draw();
            GL11.glPopMatrix();
        } catch (Exception e) {
            // Silently fail - block won't show in inventory
        }
    }

    /**
     * Render a single face for inventory rendering
     * Uses vertex coordinates centered at (0.5, 0.5, 0.5) for proper inventory display
     *
     * @param tessellator Tessellator instance
     * @param face        Face index (0-5)
     * @param icon        Icon to render
     */
    private void renderFaceStandard(Tessellator tessellator, int face, net.minecraft.util.IIcon icon) {
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        switch (face) {
            case 0: // Bottom
                tessellator.addVertexWithUV(0.0, 0.0, 1.0, minU, maxV);
                tessellator.addVertexWithUV(1.0, 0.0, 1.0, maxU, maxV);
                tessellator.addVertexWithUV(1.0, 0.0, 0.0, maxU, minV);
                tessellator.addVertexWithUV(0.0, 0.0, 0.0, minU, minV);
                break;
            case 1: // Top
                tessellator.addVertexWithUV(0.0, 1.0, 0.0, minU, maxV);
                tessellator.addVertexWithUV(1.0, 1.0, 0.0, maxU, maxV);
                tessellator.addVertexWithUV(1.0, 1.0, 1.0, maxU, minV);
                tessellator.addVertexWithUV(0.0, 1.0, 1.0, minU, minV);
                break;
            case 2: // North
                tessellator.addVertexWithUV(0.0, 0.0, 0.0, minU, maxV);
                tessellator.addVertexWithUV(1.0, 0.0, 0.0, maxU, maxV);
                tessellator.addVertexWithUV(1.0, 1.0, 0.0, maxU, minV);
                tessellator.addVertexWithUV(0.0, 1.0, 0.0, minU, minV);
                break;
            case 3: // South
                tessellator.addVertexWithUV(1.0, 0.0, 1.0, minU, maxV);
                tessellator.addVertexWithUV(0.0, 0.0, 1.0, maxU, maxV);
                tessellator.addVertexWithUV(0.0, 1.0, 1.0, maxU, minV);
                tessellator.addVertexWithUV(1.0, 1.0, 1.0, minU, minV);
                break;
            case 4: // West
                tessellator.addVertexWithUV(0.0, 0.0, 1.0, minU, maxV);
                tessellator.addVertexWithUV(0.0, 0.0, 0.0, maxU, maxV);
                tessellator.addVertexWithUV(0.0, 1.0, 0.0, maxU, minV);
                tessellator.addVertexWithUV(0.0, 1.0, 1.0, minU, minV);
                break;
            case 5: // East
                tessellator.addVertexWithUV(1.0, 0.0, 0.0, minU, maxV);
                tessellator.addVertexWithUV(1.0, 0.0, 1.0, maxU, maxV);
                tessellator.addVertexWithUV(1.0, 1.0, 1.0, maxU, minV);
                tessellator.addVertexWithUV(1.0, 1.0, 0.0, minU, minV);
                break;
        }
    }

    /**
     * Get model for specific metadata
     * Metadata 2 = PILLAR (middle)
     * Metadata 3 = PILLAR_TOP (top section)
     * Metadata 4 = PILLAR_BOTTOM (bottom section)
     */
    private IModelCustom getModelForMetadata(int metadata) {
        switch (metadata) {
            case 2:
                return modelPillar;
            case 3:
                return modelPillarTop;
            case 4:
                return modelPillarBottom;
            default:
                return modelPillar; // Default to pillar
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true; // Render as 3D model in inventory
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
