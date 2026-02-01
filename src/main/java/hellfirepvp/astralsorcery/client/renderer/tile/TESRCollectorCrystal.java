/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRCollectorCrystal - Collector Crystal TileEntitySpecialRenderer
 *
 * Renders the starlight collector crystal with collection visualization
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Collector Crystal
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Render crystal model</li>
 * <li>Starlight collection particles</li>
 * <li>Glow effect when collecting</li>
 * <li>Night-only visibility</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class TESRCollectorCrystal extends TileEntitySpecialRenderer {

    // Static storage - shared across all instances (1.7.10 TESR pattern)
    private static net.minecraftforge.client.model.IModelCustom staticModel;
    private static net.minecraft.util.ResourceLocation staticTexture;

    private final net.minecraftforge.client.model.IModelCustom model;
    private final net.minecraft.util.ResourceLocation texture;

    /**
     * Default constructor - Minecraft calls this when creating TESR instances
     * Uses static model/texture that were set during registration
     */
    public TESRCollectorCrystal() {
        this.model = staticModel;
        this.texture = staticTexture;

        if (this.model == null) {
            LogHelper.warn("[TESRCollectorCrystal] Model is NULL in constructor! Static model not set.");
        }
    }

    /**
     * Set static model and texture (called from AstralRenderLoader)
     * Registers this TESR for TileCollectorCrystal
     */
    public static void setModelAndTexture(net.minecraftforge.client.model.IModelCustom model,
        net.minecraft.util.ResourceLocation texture) {
        TESRCollectorCrystal.staticModel = model;
        TESRCollectorCrystal.staticTexture = texture;

        if (model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileCollectorCrystal.class, new TESRCollectorCrystal());
                LogHelper.info("[TESRCollectorCrystal] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRCollectorCrystal] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            return;
        }

        if (!(tile instanceof TileCollectorCrystal)) {
            return;
        }

        TileCollectorCrystal crystal = (TileCollectorCrystal) tile;

        // Save OpenGL state
        GL11.glPushMatrix();

        try {
            // Translate to block position
            GL11.glTranslated(x, y, z);

            // Render base crystal model
            if (this.model != null) {
                renderCrystalModel(crystal, partialTicks);
            }

            // Render collection effects if collecting
            if (crystal.isCollecting()) {
                renderCollectionEffects(crystal, partialTicks);
            }

            // Render stored starlight indicator
            renderStarlightIndicator(crystal, partialTicks);

        } catch (Exception e) {
            LogHelper.error("[TESRCollectorCrystal] Error during render", e);
        } finally {
            // Restore OpenGL state
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render the crystal OBJ model
     */
    private void renderCrystalModel(TileCollectorCrystal crystal, float partialTicks) {
        // Enable proper lighting and face culling
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Scale the model slightly
        GL11.glScaled(1.0, 1.0, 1.0);

        // Bind texture
        this.bindTexture(this.texture);

        // Set full brightness for glow effect
        float brightness = calculateBrightness(crystal);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f * brightness, 240f * brightness);

        // Render the model
        this.model.renderAll();
    }

    /**
     * Render starlight collection effects
     */
    private void renderCollectionEffects(TileCollectorCrystal crystal, float partialTicks) {
        // Disable texture for particle effects
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Enable additive blending for glow
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        float time = crystal.getTicksExisted() + partialTicks;

        // Render rising particles
        renderRisingParticles(time, (float) crystal.getStarlightPercentage());

        // Render glow halo
        renderGlowHalo(time, (float) crystal.getStarlightPercentage());

        // Re-enable texture
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Render rising starlight particles
     */
    private void renderRisingParticles(float time, float fillPercentage) {
        int particleCount = 8 + (int) (fillPercentage * 8); // More particles when more full

        for (int i = 0; i < particleCount; i++) {
            float offset = (i * 0.8F);
            float particleTime = (time * 0.3F + offset) % 2.0F;

            if (particleTime < 0 || particleTime > 1.5F) continue;

            // Particle rises from crystal
            float height = particleTime * 2.0F;
            float size = 0.05F * (1.0F - particleTime * 0.5F);
            float alpha = 0.6F * (1.0F - particleTime * 0.6F);

            // Position around the crystal
            float angle = i * (float) Math.PI * 2 / particleCount;
            float radius = 0.3F + particleTime * 0.1F;
            float px = (float) Math.cos(angle) * radius;
            float pz = (float) Math.sin(angle) * radius;

            // Color: light blue to white
            GL11.glColor4f(0.7F, 0.9F, 1.0F, alpha);

            // Render particle as quad
            float halfSize = size / 2;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3f(px - halfSize, height, pz - halfSize);
            GL11.glVertex3f(px + halfSize, height, pz - halfSize);
            GL11.glVertex3f(px + halfSize, height, pz + halfSize);
            GL11.glVertex3f(px - halfSize, height, pz + halfSize);
            GL11.glEnd();
        }
    }

    /**
     * Render glow halo above crystal
     */
    private void renderGlowHalo(float time, float fillPercentage) {
        GL11.glLineWidth(2F);

        float baseRadius = 0.3F + fillPercentage * 0.2F;
        float pulse = (float) Math.sin(time * 0.1F) * 0.5F + 0.5F;
        float radius = baseRadius + pulse * 0.1F;
        float alpha = 0.4F + fillPercentage * 0.3F;

        GL11.glColor4f(0.6F, 0.8F, 1.0F, alpha);

        int segments = 32;
        float height = 1.2F + (float) Math.sin(time * 0.08F) * 0.1F; // Bobbing

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            GL11.glVertex3f(x + 0.5F, height, z + 0.5F);
        }
        GL11.glEnd();
    }

    /**
     * Render starlight storage indicator
     */
    private void renderStarlightIndicator(TileCollectorCrystal crystal, float partialTicks) {
        float fillPercentage = (float) crystal.getStarlightPercentage(); // 0.0 to 1.0

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Render fill indicator as a vertical bar
        float barWidth = 0.1F;
        float barHeight = 1.0F;
        float barX = 0.8F;
        float barZ = 0.5F;
        float fillHeight = barHeight * fillPercentage;

        // Background (dim)
        GL11.glColor4f(0.3F, 0.3F, 0.3F, 0.3F);
        GL11.glBegin(GL11.GL_QUADS);
        // Front
        GL11.glVertex3f(barX, 0, barZ);
        GL11.glVertex3f(barX + barWidth, 0, barZ);
        GL11.glVertex3f(barX + barWidth, barHeight, barZ);
        GL11.glVertex3f(barX, barHeight, barZ);
        // Back
        GL11.glVertex3f(barX, 0, barZ + barWidth);
        GL11.glVertex3f(barX, barHeight, barZ + barWidth);
        GL11.glVertex3f(barX + barWidth, barHeight, barZ + barWidth);
        GL11.glVertex3f(barX + barWidth, 0, barZ + barWidth);
        GL11.glEnd();

        // Fill (bright, colored by percentage)
        float r = 0.4F + fillPercentage * 0.4F;
        float g = 0.6F + fillPercentage * 0.2F;
        float b = 1.0F;
        GL11.glColor4f(r, g, b, 0.8F);

        GL11.glBegin(GL11.GL_QUADS);
        // Front
        GL11.glVertex3f(barX, 0, barZ);
        GL11.glVertex3f(barX + barWidth, 0, barZ);
        GL11.glVertex3f(barX + barWidth, fillHeight, barZ);
        GL11.glVertex3f(barX, fillHeight, barZ);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Calculate crystal brightness based on collection state
     */
    private float calculateBrightness(TileCollectorCrystal crystal) {
        float base = 0.5F;

        if (crystal.isCollecting()) {
            base += 0.3F;
        }

        // Add fill percentage bonus
        base += (float) crystal.getStarlightPercentage() * 0.2F;

        return Math.min(base, 1.0F);
    }
}
