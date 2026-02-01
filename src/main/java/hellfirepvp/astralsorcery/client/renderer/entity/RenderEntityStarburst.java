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
import hellfirepvp.astralsorcery.common.entity.EntityStarburst;

/**
 * RenderEntityStarburst - Starburst entity renderer (1.7.10)
 * <p>
 * Renders the EntityStarburst as an expanding explosion effect with rings and particles.
 * Creates a dramatic visual effect as the starburst projectile travels.
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Expanding ring/sphere effect</li>
 * <li>Gold/Yellow color scheme</li>
 * <li>Fades out over time based on entity lifetime</li>
 * <li>Multiple layered rings for depth</li>
 * <li>Additive blending for glow effect</li>
 * <li>Central bright core</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses {@link RenderingUtils#renderRing()} for ring effects</li>
 * <li>Uses {@link RenderingUtils#renderFacingQuad()} for core</li>
 * <li>Implements {@link #doRender(Entity, double, double, double, float, float)}</li>
 * <li>Implements {@link #getEntityTexture(Entity)}</li>
 * </ul>
 */
public class RenderEntityStarburst extends Render {

    /**
     * Primary starburst color - gold
     */
    private static final Color STARBURST_COLOR = new Color(255, 215, 0);

    /**
     * Secondary color - bright yellow
     */
    private static final Color STARBURST_ACCENT = new Color(255, 255, 100);

    /**
     * Tertiary color - orange
     */
    private static final Color STARBURST_ORANGE = new Color(255, 150, 0);

    /**
     * Maximum lifetime of the starburst (in ticks)
     */
    private static final int MAX_LIFETIME = 100;

    /**
     * Base scale for the starburst effect
     */
    private static final float BASE_SCALE = 0.5F;

    /**
     * Number of ring segments
     */
    private static final int RING_SEGMENTS = 32;

    /**
     * Create a new RenderEntityStarburst
     */
    public RenderEntityStarburst() {
        this.shadowSize = 0.0F; // Projectiles don't cast shadows
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityStarburst)) {
            return;
        }

        EntityStarburst starburst = (EntityStarburst) entity;

        // Bind entity texture
        this.bindEntityTexture(entity);

        // Calculate lifetime progress (0.0 at start, 1.0 at end)
        float lifetimeProgress = (float) starburst.ticksExisted / MAX_LIFETIME;
        lifetimeProgress = Math.min(lifetimeProgress, 1.0F);

        // Calculate fade alpha based on lifetime
        // Fade in quickly, stay bright, then fade out at end
        float alpha = calculateAlpha(lifetimeProgress);

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable additive blending for glow effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        // Disable depth write for proper transparency
        GL11.glDepthMask(false);

        // Disable lighting for self-illuminated effect
        GL11.glDisable(GL11.GL_LIGHTING);

        // Move to starburst position
        GL11.glTranslated(x, y, z);

        // Rotate to face direction of motion
        float yaw = (float) Math.toDegrees(Math.atan2(starburst.motionX, starburst.motionZ));
        float pitchAngle = (float) Math.toDegrees(
            Math.atan2(
                starburst.motionY,
                Math.sqrt(starburst.motionX * starburst.motionX + starburst.motionZ * starburst.motionZ)));
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-pitchAngle, 1.0F, 0.0F, 0.0F);

        // Render expanding rings
        renderExpandingRings(starburst, lifetimeProgress, alpha, partialTicks);

        // Render central core
        renderCentralCore(starburst, lifetimeProgress, alpha, partialTicks);

        // Render trailing particles
        renderTrailEffect(starburst, lifetimeProgress, alpha, partialTicks);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Calculate alpha based on lifetime progress
     * <p>
     * Effect fades in quickly (0% to 20% lifetime),
     * stays bright (20% to 80% lifetime),
     * then fades out (80% to 100% lifetime)
     *
     * @param progress Lifetime progress (0.0 to 1.0)
     * @return Alpha value (0.0 to 1.0)
     */
    private float calculateAlpha(float progress) {
        if (progress < 0.2F) {
            // Fade in
            return progress / 0.2F;
        } else if (progress < 0.8F) {
            // Full brightness
            return 1.0F;
        } else {
            // Fade out
            return 1.0F - ((progress - 0.8F) / 0.2F);
        }
    }

    /**
     * Render the expanding ring effect
     *
     * @param starburst        The starburst entity
     * @param lifetimeProgress Lifetime progress (0.0 to 1.0)
     * @param alpha            Alpha transparency
     * @param partialTicks     Partial tick time
     */
    private void renderExpandingRings(EntityStarburst starburst, float lifetimeProgress, float alpha,
        float partialTicks) {
        // Calculate expanding radius
        float baseRadius = BASE_SCALE * (1.0F + lifetimeProgress * 2.0F);
        float pulse = (float) Math.sin((starburst.ticksExisted + partialTicks) * 0.3) * 0.5F + 0.5F;

        // Render multiple layered rings
        for (int i = 0; i < 3; i++) {
            float ringRadius = baseRadius * (1.0F + i * 0.3F + pulse * 0.2F);
            float ringAlpha = alpha * (0.5F - i * 0.15F);
            float ringThickness = 0.05F + pulse * 0.03F;

            // Choose color for this ring
            Color ringColor;
            switch (i) {
                case 0:
                    ringColor = STARBURST_COLOR;
                    break;
                case 1:
                    ringColor = STARBURST_ACCENT;
                    break;
                default:
                    ringColor = STARBURST_ORANGE;
                    break;
            }

            // Render the ring
            GL11.glPushMatrix();

            // Rotate each ring for visual variety
            GL11.glRotatef(i * 30.0F + (starburst.ticksExisted + partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);

            RenderingUtils.renderRing(
                0.0,
                0.0,
                0.0, // Center position
                ringRadius - ringThickness, // Inner radius
                ringRadius + ringThickness, // Outer radius
                ringColor,
                ringAlpha,
                RING_SEGMENTS);

            GL11.glPopMatrix();
        }

        // Render a secondary ring on perpendicular plane
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((starburst.ticksExisted + partialTicks) * 15.0F, 0.0F, 0.0F, 1.0F);

        float secondaryRadius = baseRadius * 0.7F;
        RenderingUtils.renderRing(
            0.0,
            0.0,
            0.0,
            secondaryRadius - 0.03F,
            secondaryRadius + 0.03F,
            STARBURST_ACCENT,
            alpha * 0.3F,
            RING_SEGMENTS);

        GL11.glPopMatrix();
    }

    /**
     * Render the central bright core of the starburst
     *
     * @param starburst        The starburst entity
     * @param lifetimeProgress Lifetime progress
     * @param alpha            Alpha transparency
     * @param partialTicks     Partial tick time
     */
    private void renderCentralCore(EntityStarburst starburst, float lifetimeProgress, float alpha, float partialTicks) {
        float pulse = (float) Math.sin((starburst.ticksExisted + partialTicks) * 0.5) * 0.5F + 0.5F;
        float coreScale = BASE_SCALE * (0.8F + pulse * 0.4F) * (1.0F + lifetimeProgress);

        // Render multiple layers for the core

        // Outer glow (largest, most transparent)
        GL11.glPushMatrix();
        float outerScale = coreScale * 2.0F;
        float outerAlpha = alpha * 0.3F;
        renderCoreQuad(outerScale, outerAlpha, STARBURST_ORANGE, (starburst.ticksExisted + partialTicks) * 2.0F);
        GL11.glPopMatrix();

        // Middle glow (medium)
        GL11.glPushMatrix();
        float middleScale = coreScale * 1.2F;
        float middleAlpha = alpha * 0.6F;
        renderCoreQuad(middleScale, middleAlpha, STARBURST_COLOR, -(starburst.ticksExisted + partialTicks) * 3.0F);
        GL11.glPopMatrix();

        // Inner core (smallest, brightest)
        GL11.glPushMatrix();
        float innerScale = coreScale * 0.6F;
        float innerAlpha = alpha * 0.9F;
        renderCoreQuad(innerScale, innerAlpha, Color.WHITE, (starburst.ticksExisted + partialTicks) * 4.0F);
        GL11.glPopMatrix();
    }

    /**
     * Render a single quad for the core
     *
     * @param scale    Scale of the quad
     * @param alpha    Alpha transparency
     * @param color    Color of the quad
     * @param rotation Rotation angle
     */
    private void renderCoreQuad(float scale, float alpha, Color color, float rotation) {
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        GL11.glColor4f(r, g, b, alpha);

        // Convert rotation to radians
        float rotationRad = rotation * (float) Math.PI / 180.0F;

        RenderingUtils.renderFacingQuad(
            0.0,
            0.0,
            0.0, // Position (centered)
            0.0F, // partialTicks (not used for stationary center)
            scale,
            rotationRad,
            0.0,
            0.0,
            1.0,
            1.0 // Full texture
        );
    }

    /**
     * Render trailing particles behind the starburst
     *
     * @param starburst        The starburst entity
     * @param lifetimeProgress Lifetime progress
     * @param alpha            Alpha transparency
     * @param partialTicks     Partial tick time
     */
    private void renderTrailEffect(EntityStarburst starburst, float lifetimeProgress, float alpha, float partialTicks) {
        // The trail extends behind the starburst (negative Z in our rotated space)
        float trailLength = BASE_SCALE * 2.0F * (1.0F - lifetimeProgress);

        // Don't render trail if it's too short
        if (trailLength < 0.1F) {
            return;
        }

        int trailSegments = 8;
        for (int i = 0; i < trailSegments; i++) {
            float t = (float) (i + 1) / trailSegments;
            float offsetZ = -t * trailLength;
            float trailAlpha = alpha * (1.0F - t) * 0.5F;
            float trailScale = BASE_SCALE * (1.0F - t * 0.5F);

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, offsetZ);

            // Alternate colors
            Color trailColor = (i % 2 == 0) ? STARBURST_COLOR : STARBURST_ACCENT;

            renderCoreQuad(
                trailScale,
                trailAlpha,
                trailColor,
                (starburst.ticksExisted + partialTicks) * 5.0F + i * 45.0F);

            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // Starburst uses procedural rendering, no texture needed
        return ResourceLocationRegister.getEntityStarburst();
    }

    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render starbursts - they're important visual effects
        return true;
    }
}
