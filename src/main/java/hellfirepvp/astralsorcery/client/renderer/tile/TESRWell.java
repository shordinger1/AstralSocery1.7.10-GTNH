/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRWell - Starlight Well TileEntitySpecialRenderer
 *
 * SKELETON VERSION - Simplified rendering for 1.7.10
 * Complex texture rendering deferred until utilities are migrated
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.renderer.AstralBaseTESR;
import hellfirepvp.astralsorcery.common.tile.TileWell;

/**
 * TESRWell - Starlight well renderer (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Render catalyst item</li>
 * <li>Render fluid level visualization</li>
 * <li>Fluid color based on type</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>render() → renderTileEntityAt()</li>
 * <li>TextureAtlasSprite - Different access method</li>
 * <li>getTextureMapBlocks() → getTextureMapBlocks() (same but verify)</li>
 * <li>RenderingUtils.renderItemAsEntity() - Not yet migrated</li>
 * <li>RenderingUtils.renderAngleRotatedTexturedRect() - Not yet migrated</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>RenderingUtils.renderItemAsEntity() - Item rendering</li>
 * <li>RenderingUtils.renderAngleRotatedTexturedRect() - Textured rectangle</li>
 * <li>TextureHelper.setActiveTextureToAtlasSprite() - Texture binding</li>
 * <li>Vector3 class - Position helper (partially migrated)</li>
 * </ul>
 */
public class TESRWell extends AstralBaseTESR {

    // Static storage - shared across all instances (1.7.10 TESR pattern)
    private static net.minecraftforge.client.model.IModelCustom staticModel;
    private static net.minecraft.util.ResourceLocation staticTexture;

    private final net.minecraftforge.client.model.IModelCustom model;
    private final net.minecraft.util.ResourceLocation texture;

    /**
     * Default constructor - Minecraft calls this when creating TESR instances
     * Uses static model/texture that were set during registration
     */
    public TESRWell() {
        this.model = staticModel;
        this.texture = staticTexture;

        if (this.model == null) {
            hellfirepvp.astralsorcery.common.util.LogHelper
                .warn("[TESRWell] Model is NULL in constructor! Static model not set.");
        }
    }

    /**
     * Set static model and texture (called from AstralRenderLoader)
     * Registers this TESR for TileWell
     */
    public static void setModelAndTexture(net.minecraftforge.client.model.IModelCustom model,
        net.minecraft.util.ResourceLocation texture) {
        TESRWell.staticModel = model;
        TESRWell.staticTexture = texture;

        if (model != null) {
            try {
                cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                    TileWell.class,
                    new TESRWell());
                hellfirepvp.astralsorcery.common.util.LogHelper
                    .info("[TESRWell] Registered TESR with OBJ model support (static storage)");
            } catch (Exception e) {
                hellfirepvp.astralsorcery.common.util.LogHelper.error("[TESRWell] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (te == null) {
            return;
        }

        if (!(te instanceof TileWell)) {
            return;
        }
        TileWell well = (TileWell) te;

        // Debug log to confirm TESR is being called
        hellfirepvp.astralsorcery.common.util.LogHelper.debug(
            "[TESRWell] renderTileEntityAt called at [" + well.xCoord + ", " + well.yCoord + ", " + well.zCoord + "]");
        hellfirepvp.astralsorcery.common.util.LogHelper.debug(
            "[TESRWell] Model is " + (this.model != null ? "NOT NULL" : "NULL"));

        // Render OBJ model if available
        if (this.model != null) {
            renderOBJModel(x, y, z);
        } else {
            hellfirepvp.astralsorcery.common.util.LogHelper.warn("[TESRWell] Model is NULL, cannot render!");
        }

        // Render catalyst item
        // TODO: getInventory() may not exist - use direct slot access
        // ItemStack catalyst = well.getInventory().getStackInSlot(0);
        // For now, skip catalyst rendering
        // if (catalyst != null && catalyst.stackSize > 0) {
        // renderItemSimplified(catalyst, x, y, z, well.ticksExisted);
        // }

        // Render fluid level
        int fluidAmount = well.getFluidAmount();
        Fluid heldFluid = well.getHeldFluid();

        if (fluidAmount > 0 && heldFluid != null) {
            renderFluid(well, x, y, z, partialTick, heldFluid, fluidAmount);
        }
    }

    /**
     * Render fluid visualization
     */
    private void renderFluid(TileWell well, double x, double y, double z, float partialTick, Fluid fluid, int amount) {
        saveState();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Enable blending
        enableBlend();
        setStandardBlend();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        // Get fluid color
        // TODO: 1.7.10 Fluid.getColor() may not accept World/Pos
        int color = fluid.getColor();
        float r = ((color >> 16) & 0xFF) / 255F;
        float g = ((color >> 8) & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;
        float a = ((color >> 24) & 0xFF) / 255F;
        if (a == 0) a = 1F;

        GL11.glColor4f(r, g, b, a);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        disableLighting();

        // Calculate fluid level position
        float percFilled = well.getPercFilled();
        double fluidY = y + 0.32D + (percFilled * 0.6);

        // Translate to fluid position
        translate((float) (x + 0.5), (float) fluidY, (float) (z + 0.5));

        // TODO: Re-enable after TextureHelper is migrated
        // Render textured rectangle showing fluid level
        // TextureHelper.setActiveTextureToAtlasSprite();
        // ResourceLocation still = fluid.getStill();
        // TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(still.toString());
        // if(tas == null) tas = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        // RenderingUtils.renderAngleRotatedTexturedRect(offset, Vector3.RotAxis.Y_AXIS.clone(),
        // Math.toRadians(45), 0.54, tas.getMinU(), tas.getMinV(),
        // tas.getMaxU() - tas.getMinU(), tas.getMaxV() - tas.getMinV(), partialTicks);

        // Simplified fluid level rendering (colored quad)
        renderFluidLevelQuad(percFilled);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        disableBlend();
        GL11.glPopAttrib();

        restoreState();
    }

    /**
     * Render OBJ model
     * Uses direct GL11 calls (not base class methods) to match TESRGrindstone pattern
     */
    private void renderOBJModel(double x, double y, double z) {
        GL11.glPushMatrix();

        try {
            GL11.glTranslated(x, y, z);

            // Enable depth test for proper occlusion
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            // Enable proper lighting and face culling
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);

            // Scale the model
            GL11.glScaled(1.0, 1.0, 1.0);

            // Set full brightness for glow effect
            net.minecraft.client.renderer.OpenGlHelper
                .setLightmapTextureCoords(net.minecraft.client.renderer.OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Bind texture
            this.bindTexture(this.texture);

            // Render the model
            this.model.renderAll();

        } finally {
            // Restore OpenGL state in finally block to ensure cleanup even on error
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render simplified fluid level quad
     * TODO: Replace with proper textured rendering when TextureHelper is migrated
     */
    private void renderFluidLevelQuad(float percFilled) {
        org.lwjgl.opengl.GL11.glBegin(GL11.GL_QUADS);

        double size = 0.54 * percFilled;
        double halfSize = size / 2;

        // Top face (fluid surface)
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3d(-halfSize, 0, -halfSize);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3d(halfSize, 0, -halfSize);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3d(halfSize, 0, halfSize);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3d(-halfSize, 0, halfSize);

        org.lwjgl.opengl.GL11.glEnd();
    }

    /**
     * Simplified item rendering
     * TODO: Replace with RenderingUtils.renderItemAsEntity() when available
     */
    private void renderItemSimplified(ItemStack stack, double x, double y, double z, int ticksExisted) {
        saveState();
        translate((float) (x + 0.5), (float) (y + 0.8), (float) (z + 0.5));

        // Rotate item
        float rotation = ticksExisted * 2;
        rotate(rotation, 0, 1, 0);
        rotate(30, 1, 0, 0);

        // Scale down
        scale(0.5F, 0.5F, 0.5F);

        // TODO: Render actual item model
        // net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        // Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
        // net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        restoreState();
    }
}
