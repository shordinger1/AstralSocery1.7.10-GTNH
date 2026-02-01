/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import java.awt.Color;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;

/**
 * RenderEntityFlare - Flare entity renderer (1.7.10)
 * <p>
 * Renders the EntityFlare as a glowing floating sprite that always faces the player.
 * Uses billboard rendering with a pulsing orange/yellow glow effect.
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Billboard quad that always faces the camera</li>
 * <li>Orange/Yellow color scheme</li>
 * <li>Pulsing glow effect based on entity age</li>
 * <li>Size: 0.5F scale</li>
 * <li>Additive blending for glow effect</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Implements {@link #doRender(Entity, double, double, double, float, float)}</li>
 * <li>Implements {@link #getEntityTexture(Entity)}</li>
 * <li>Uses {@link RenderingUtils#renderFacingQuad()} for billboard</li>
 * </ul>
 */
public class RenderEntityFlare extends Render {

    /**
     * Base color for the flare - orange/yellow
     */
    private static final Color FLARE_COLOR = new Color(255, 200, 50);

    /**
     * Secondary accent color - bright yellow
     */
    private static final Color FLARE_ACCENT = new Color(255, 255, 150);

    /**
     * Base scale for the flare sprite
     */
    private static final float BASE_SCALE = 0.5F;

    /**
     * Create a new RenderEntityFlare
     *
     * @param renderManager The render manager
     */
    public RenderEntityFlare() {
        // 1.7.10: Render has empty constructor, shadow handling is different
        this.shadowSize = 0.0F; // Flares don't cast shadows
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityFlare)) {
            return;
        }

        EntityFlare flare = (EntityFlare) entity;

        // Bind entity texture
        this.bindEntityTexture(entity);

        // Save current OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Disable lighting for self-illumination effect (critical for texture visibility)
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Enable additive blending for glow effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        // Disable depth write to prevent z-fighting with transparent pixels
        GL11.glDepthMask(false);

        // Calculate pulse based on entity age
        float pulse = calculatePulse(flare, partialTicks);

        // Calculate scale with pulse
        float scale = BASE_SCALE * (0.8F + pulse * 0.4F);

        // Calculate alpha based on pulse
        float alpha = 0.7F + pulse * 0.3F;

        // Rotate sprite over time
        float rotation = (flare.ticksExisted + partialTicks) * 2.0F;

        // Render the main glow layer (larger, more transparent)
        GL11.glPushMatrix();
        float glowScale = scale * 1.5F;
        float glowAlpha = alpha * 0.4F;
        renderFlareQuad(flare, x, y, z, partialTicks, glowScale, rotation, FLARE_COLOR, glowAlpha);
        GL11.glPopMatrix();

        // Render the core layer (smaller, brighter)
        GL11.glPushMatrix();
        float coreAlpha = alpha * 0.9F;
        renderFlareQuad(flare, x, y, z, partialTicks, scale, -rotation * 0.5F, FLARE_ACCENT, coreAlpha);
        GL11.glPopMatrix();

        // Render the bright center (smallest, most intense)
        GL11.glPushMatrix();
        float centerScale = scale * 0.5F;
        float centerAlpha = alpha;
        renderFlareQuad(flare, x, y, z, partialTicks, centerScale, rotation * 0.3F, Color.WHITE, centerAlpha);
        GL11.glPopMatrix();

        // Restore OpenGL state
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Calculate pulse value (0.0 to 1.0) based on entity age and time
     *
     * @param flare        The flare entity
     * @param partialTicks Partial tick time for smooth animation
     * @return Pulse value from 0.0 to 1.0
     */
    private float calculatePulse(EntityFlare flare, float partialTicks) {
        // Pulse speed for the flare animation
        float pulseSpeed = 0.1F; // Base pulse speed
        double time = flare.ticksExisted + partialTicks;

        // Sine wave pulse from 0 to 1
        return (float) (Math.sin(time * pulseSpeed) * 0.5 + 0.5);
    }

    /**
     * Render a single billboard quad for the flare
     *
     * @param flare        The flare entity
     * @param x            X position
     * @param y            Y position
     * @param z            Z position
     * @param partialTicks Partial tick time
     * @param scale        Scale of the quad
     * @param rotation     Rotation angle in degrees
     * @param color        Color of the quad
     * @param alpha        Alpha transparency (0.0 to 1.0)
     */
    private void renderFlareQuad(EntityFlare flare, double x, double y, double z, float partialTicks, float scale,
        float rotation, Color color, float alpha) {
        // Convert color to RGB float values
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        // Set color
        GL11.glColor4f(r, g, b, alpha);

        // Render facing quad using RenderingUtils
        // Using full texture coordinates (0,0 to 1,1) for a complete sprite
        RenderingUtils.renderFacingQuad(
            x,
            y + flare.height / 2.0,
            z, // Position (centered on entity)
            partialTicks, // Partial ticks for interpolation
            scale, // Scale
            rotation, // Rotation angle
            0.0,
            0.0, // U, V (texture coordinates start)
            1.0,
            1.0 // U length, V length (full texture)
        );
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // Flares use procedural rendering, no texture needed
        // Return a placeholder resource location
        return ResourceLocationRegister.getEntityFlare();
    }

    /**
     * Check if this renderer should render the entity at all
     * Useful for filtering entities based on distance or other conditions
     *
     * @param entity The entity to check
     * @param x      X position
     * @param y      Y position
     * @param z      Z position
     * @return true if the entity should be rendered
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render flares if they're within the normal render distance
        // Flares are important visual elements
        return true;
    }
}
