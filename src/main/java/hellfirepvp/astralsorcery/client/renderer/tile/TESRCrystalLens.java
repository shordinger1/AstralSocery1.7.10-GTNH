/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRCrystalLens - Crystal Lens TileEntitySpecialRenderer
 *
 * Renders the crystal lens with starlight transmission beam visualization
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.tile.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Crystal Lens
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Render lens model</li>
 * <li>Starlight transmission beams</li>
 * <li>Glow effect when transmitting</li>
 * <li>Direction indicator</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class TESRCrystalLens extends TileEntitySpecialRenderer {

    // Static storage - shared across all instances (1.7.10 TESR pattern)
    private static net.minecraftforge.client.model.IModelCustom staticModel;
    private static net.minecraft.util.ResourceLocation staticTexture;

    private final net.minecraftforge.client.model.IModelCustom model;
    private final net.minecraft.util.ResourceLocation texture;

    /**
     * Default constructor - Minecraft calls this when creating TESR instances
     */
    public TESRCrystalLens() {
        this.model = staticModel;
        this.texture = staticTexture;

        if (this.model == null) {
            LogHelper.warn("[TESRCrystalLens] Model is NULL in constructor!");
        }
    }

    /**
     * Set static model and texture (called from AstralRenderLoader)
     */
    public static void setModelAndTexture(net.minecraftforge.client.model.IModelCustom model,
        net.minecraft.util.ResourceLocation texture) {
        TESRCrystalLens.staticModel = model;
        TESRCrystalLens.staticTexture = texture;

        if (model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalLens.class, new TESRCrystalLens());
                LogHelper.info("[TESRCrystalLens] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRCrystalLens] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) return;
        if (!(tile instanceof TileCrystalLens)) return;

        TileCrystalLens lens = (TileCrystalLens) tile;

        GL11.glPushMatrix();

        try {
            GL11.glTranslated(x, y, z);

            // Render base lens model
            if (this.model != null) {
                renderLensModel(lens, partialTicks);
            }

            // Render transmission beams
            renderTransmissionBeams(lens, partialTicks);

            // Render directional indicator
            renderDirectionIndicator(lens, partialTicks);

        } catch (Exception e) {
            LogHelper.error("[TESRCrystalLens] Error during render", e);
        } finally {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render the lens OBJ model
     */
    private void renderLensModel(TileCrystalLens lens, float partialTicks) {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Bind texture
        this.bindTexture(this.texture);

        // Set brightness based on transmission
        float brightness = 0.5F + ((float) lens.getBufferedStarlight() / 100.0F) * 0.5F;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f * brightness, 240f * brightness);

        // Render the model
        this.model.renderAll();
    }

    /**
     * Render starlight transmission beams
     */
    private void renderTransmissionBeams(TileCrystalLens lens, float partialTicks) {
        float bufferedStarlight = (float) lens.getBufferedStarlight();

        if (bufferedStarlight <= 0.1F) {
            return; // No transmission
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        float time = lens.getTicksExisted() + partialTicks;

        // Render input beam (from source)
        renderBeam(time, bufferedStarlight, true);

        // Render output beam (to destination)
        renderBeam(time, bufferedStarlight, false);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Render a single transmission beam
     */
    private void renderBeam(float time, float intensity, boolean isInput) {
        float beamLength = 1.0F; // Max transmission range
        float beamWidth = 0.05F * intensity;
        float alpha = 0.6F * intensity;

        // Direction: input from opposite side, output to facing side
        float direction = isInput ? -1 : 1;
        float startY = 0.5F;
        float endY = startY + direction * beamLength;

        // Color shift over time
        float r = 0.6F + (float) Math.sin(time * 0.05F) * 0.2F;
        float g = 0.8F;
        float b = 1.0F;

        GL11.glColor4f(r, g, b, alpha);

        // Render beam as series of quads along the length
        int segments = 20;
        for (int i = 0; i < segments; i++) {
            float t1 = (float) i / segments;
            float t2 = (float) (i + 1) / segments;

            float y1 = startY + (endY - startY) * t1;
            float y2 = startY + (endY - startY) * t2;

            // Taper beam at ends
            float taper = 1.0F;
            if (t1 < 0.2F) taper = t1 * 5;
            if (t2 > 0.8F) taper = (1.0F - t2) * 5;

            float width = beamWidth * taper;

            GL11.glBegin(GL11.GL_QUADS);
            // Quad 1
            GL11.glVertex3f(0.5F - width, y1, 0.5F - width);
            GL11.glVertex3f(0.5F + width, y1, 0.5F - width);
            GL11.glVertex3f(0.5F + width, y2, 0.5F - width);
            GL11.glVertex3f(0.5F - width, y2, 0.5F - width);
            // Quad 2 (perpendicular)
            GL11.glVertex3f(0.5F - width, y1, 0.5F + width);
            GL11.glVertex3f(0.5F - width, y1, 0.5F - width);
            GL11.glVertex3f(0.5F - width, y2, 0.5F - width);
            GL11.glVertex3f(0.5F - width, y2, 0.5F + width);
            GL11.glEnd();
        }

        // Render particle effects along beam
        renderBeamParticles(time, intensity, startY, endY, isInput);
    }

    /**
     * Render particles traveling along the beam
     */
    private void renderBeamParticles(float time, float intensity, float startY, float endY, boolean isInput) {
        int particleCount = (int) (3 * intensity);

        for (int i = 0; i < particleCount; i++) {
            float offset = (i * 0.7F);
            float particleTime = (time * 0.1F + offset) % 1.0F;
            float dir = isInput ? -1 : 1;

            float y = startY + dir * particleTime;

            float size = 0.08F * intensity;
            float alpha = 0.8F * intensity;

            GL11.glColor4f(0.8F, 0.9F, 1.0F, alpha);

            float halfSize = size / 2;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3f(0.5F - halfSize, y, 0.5F - halfSize);
            GL11.glVertex3f(0.5F + halfSize, y, 0.5F - halfSize);
            GL11.glVertex3f(0.5F + halfSize, y, 0.5F + halfSize);
            GL11.glVertex3f(0.5F - halfSize, y, 0.5F + halfSize);
            GL11.glEnd();
        }
    }

    /**
     * Render directional indicator (arrow showing output direction)
     */
    private void renderDirectionIndicator(TileCrystalLens lens, float partialTicks) {
        float bufferedStarlight = (float) lens.getBufferedStarlight();

        if (bufferedStarlight <= 0.1F) {
            return;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(2F);

        float time = lens.getTicksExisted() + partialTicks;
        float pulse = (float) Math.sin(time * 0.1F) * 0.5F + 0.5F;
        float alpha = 0.3F + pulse * 0.3F;

        GL11.glColor4f(0.6F, 0.8F, 1.0F, alpha);

        // Draw arrow pointing in output direction
        float arrowLength = 0.4F;
        float arrowWidth = 0.1F;
        float y = 0.5F;

        GL11.glBegin(GL11.GL_LINES);
        // Arrow shaft
        GL11.glVertex3f(0.5F, y - arrowLength / 2, 0.5F);
        GL11.glVertex3f(0.5F, y + arrowLength / 2, 0.5F);
        // Arrow head
        GL11.glVertex3f(0.5F - arrowWidth, y + arrowLength / 2 - 0.1F, 0.5F);
        GL11.glVertex3f(0.5F, y + arrowLength / 2, 0.5F);
        GL11.glVertex3f(0.5F + arrowWidth, y + arrowLength / 2 - 0.1F, 0.5F);
        GL11.glVertex3f(0.5F, y + arrowLength / 2, 0.5F);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
