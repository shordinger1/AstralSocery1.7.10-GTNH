/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRChalice - Chalice TileEntitySpecialRenderer
 *
 * Renders chalice base model + fluid
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.renderer.AstralBaseTESR;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESRChalice - Chalice renderer (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Renders chalice base model (OBJ)</li>
 * <li>Renders fluid inside chalice</li>
 * <li>Rotation animation based on fill level</li>
 * <li>Fluid color visualization</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class TESRChalice extends AstralBaseTESR {

    private final IModelCustom model;
    private final ResourceLocation texture;

    /**
     * Default constructor (for legacy registration)
     */
    public TESRChalice() {
        this(null, null);
    }

    /**
     * Constructor with model and texture
     */
    public TESRChalice(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;

        // Auto-register if model is provided
        if (this.model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileChalice.class, this);
                LogHelper.info("[TESRChalice] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRChalice] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (te == null) {
            LogHelper.warn("[TESRChalice] TileEntity is null");
            return;
        }

        if (!(te instanceof TileChalice)) {
            LogHelper.warn("[TESRChalice] TileEntity is not a TileChalice: " + te.getClass().getName());
            return;
        }

        TileChalice chalice = (TileChalice) te;

        LogHelper.debug(String.format("[TESRChalice] Rendering at x=%.2f y=%.2f z=%.2f, model=%s, texture=%s",
            x, y, z, model != null ? "loaded" : "NULL", texture != null ? texture.toString() : "NULL"));

        // Render the base chalice model
        if (model != null) {
            renderChaliceModel(x, y, z);
        } else {
            LogHelper.warn("[TESRChalice] Model is null, cannot render base chalice!");
        }

        // Render fluid inside chalice
        Fluid heldFluid = chalice.getHeldFluid();
        if (heldFluid != null && chalice.getFluidAmount() > 0) {
            renderFluid(chalice, heldFluid, x, y, z, partialTick);
        }
    }

    /**
     * Render the chalice base model
     */
    private void renderChaliceModel(double x, double y, double z) {
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);

            // Enable proper lighting and face culling
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);

            // Set full brightness
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Bind texture and render
            bindTexture(texture);
            model.renderAll();

        } catch (Exception e) {
            LogHelper.error("[TESRChalice] Error rendering base model", e);
        } finally {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render fluid inside chalice
     */
    private void renderFluid(TileChalice chalice, Fluid fluid, double x, double y, double z, float partialTick) {
        TileChalice.DrawSize size = chalice.getDrawSize();

        // Get interpolated rotation
        float rotX = (float) interpolate(
            chalice.prevRotationDegreeAxis.getX(),
            chalice.rotationDegreeAxis.getX(),
            partialTick);
        float rotY = (float) interpolate(
            chalice.prevRotationDegreeAxis.getY(),
            chalice.rotationDegreeAxis.getY(),
            partialTick);
        float rotZ = (float) interpolate(
            chalice.prevRotationDegreeAxis.getZ(),
            chalice.rotationDegreeAxis.getZ(),
            partialTick);

        saveState();

        // Get fluid color
        int color = fluid.getColor();
        float r = ((color >> 16) & 0xFF) / 255F;
        float g = ((color >> 8) & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;

        setColor4f(r, g, b, 0.8F);

        // Translate to chalice position
        translate((float) (x + 0.5), (float) (y + 1.4), (float) (z + 0.5));

        // Apply rotation
        rotate(rotX, 1, 0, 0);
        rotate(rotY, 0, 1, 0);
        rotate(rotZ, 0, 0, 1);

        // Enable blending for transparency
        enableBlend();
        setStandardBlend();
        disableLighting();
        disableCullFace();

        // Scale based on fluid amount
        scale(0.9F, 0.9F, 0.9F);

        // Render fluid cube
        renderSimplifiedFluid(chalice, fluid, size);

        enableLighting();
        enableCullFace();
        disableBlend();

        restoreState();
    }

    /**
     * Render simplified fluid visualization
     */
    private void renderSimplifiedFluid(TileChalice chalice, Fluid fluid, TileChalice.DrawSize size) {
        // Render cube based on size
        double cubeSize = size.partTexture;
        double halfSize = cubeSize / 2;

        // Draw cube
        GL11.glBegin(GL11.GL_QUADS);

        // Front face
        GL11.glNormal3f(0, 0, 1);
        GL11.glVertex3d(-halfSize, -halfSize, halfSize);
        GL11.glVertex3d(halfSize, -halfSize, halfSize);
        GL11.glVertex3d(halfSize, halfSize, halfSize);
        GL11.glVertex3d(-halfSize, halfSize, halfSize);

        // Back face
        GL11.glNormal3f(0, 0, -1);
        GL11.glVertex3d(halfSize, -halfSize, -halfSize);
        GL11.glVertex3d(-halfSize, -halfSize, -halfSize);
        GL11.glVertex3d(-halfSize, halfSize, -halfSize);
        GL11.glVertex3d(halfSize, halfSize, -halfSize);

        // Left face
        GL11.glNormal3f(-1, 0, 0);
        GL11.glVertex3d(-halfSize, -halfSize, -halfSize);
        GL11.glVertex3d(-halfSize, -halfSize, halfSize);
        GL11.glVertex3d(-halfSize, halfSize, halfSize);
        GL11.glVertex3d(-halfSize, halfSize, -halfSize);

        // Right face
        GL11.glNormal3f(1, 0, 0);
        GL11.glVertex3d(halfSize, -halfSize, halfSize);
        GL11.glVertex3d(halfSize, -halfSize, -halfSize);
        GL11.glVertex3d(halfSize, halfSize, -halfSize);
        GL11.glVertex3d(halfSize, halfSize, halfSize);

        // Top face
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3d(-halfSize, halfSize, halfSize);
        GL11.glVertex3d(halfSize, halfSize, halfSize);
        GL11.glVertex3d(halfSize, halfSize, -halfSize);
        GL11.glVertex3d(-halfSize, halfSize, -halfSize);

        // Bottom face
        GL11.glNormal3f(0, -1, 0);
        GL11.glVertex3d(-halfSize, -halfSize, -halfSize);
        GL11.glVertex3d(halfSize, -halfSize, -halfSize);
        GL11.glVertex3d(halfSize, -halfSize, halfSize);
        GL11.glVertex3d(-halfSize, -halfSize, halfSize);

        GL11.glEnd();
    }
}
