/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESR for Celestial Gateway Block - Simplified using TST pattern
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Celestial Gateway
 * <p>
 * Simplified implementation following Twist-Space-Technology pattern
 * Uses simple GL11 calls for rendering
 */
@SideOnly(Side.CLIENT)
public class TESRCelestialGateway extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    /**
     * Constructor - receives pre-loaded model and texture
     * Registers this TESR for TileCelestialGateway
     */
    public TESRCelestialGateway(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;

        LogHelper.info("[TESRCelestialGateway] Constructor called");
        LogHelper.info("  Model: " + (model != null ? "loaded" : "NULL"));
        LogHelper.info("  Texture: " + texture);

        try {
            LogHelper.debug("[TESRCelestialGateway] Registering TESR for TileCelestialGateway");
            ClientRegistry.bindTileEntitySpecialRenderer(TileCelestialGateway.class, this);
            LogHelper.info("[TESRCelestialGateway] ✓ Successfully registered TESR");
        } catch (Exception e) {
            LogHelper.error("[TESRCelestialGateway] ✗ Failed to register TESR", e);
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            return;
        }

        if (!(tile instanceof TileCelestialGateway)) {
            return;
        }

        TileCelestialGateway gateway = (TileCelestialGateway) tile;

        if (this.model == null) {
            LogHelper.warn(
                "[TESRCelestialGateway] Model is null, skipping render at [" + gateway.xCoord
                    + ", "
                    + gateway.yCoord
                    + ", "
                    + gateway.zCoord
                    + "]");
            return;
        }

        // Save OpenGL state
        GL11.glPushMatrix();

        try {
            // Translate to block position
            GL11.glTranslated(x, y, z);

            // Disable lighting for self-illumination
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);

            // Enable blending for transparency
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Scale the model (TST pattern: models are usually exported at different scales)
            GL11.glScaled(1.0, 1.0, 1.0); // Adjust if needed

            // Set full brightness for glow effect
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Bind texture
            this.bindTexture(this.texture);

            // Render the model
            this.model.renderAll();

        } catch (Exception e) {
            LogHelper.error(
                "[TESRCelestialGateway] Error during render at [" + gateway.xCoord
                    + ", "
                    + gateway.yCoord
                    + ", "
                    + gateway.zCoord
                    + "]",
                e);
        } finally {
            // Restore OpenGL state
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
}
