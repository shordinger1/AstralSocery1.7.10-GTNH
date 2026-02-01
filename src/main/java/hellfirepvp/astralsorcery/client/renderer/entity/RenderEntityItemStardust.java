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
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityItemStardust;

/**
 * RenderEntityItemStardust - Stardust item entity renderer (1.7.10)
 * <p>
 * Renders EntityItemStardust with special glowing particle trail effects.
 * Extends the default item renderer and adds cyan/blue sparkle effects.
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Default item rendering (uses RenderItem)</li>
 * <li>Glowing cyan/blue particle trail</li>
 * <li>Additive blending for sparkle effects</li>
 * <li>Pulsing glow animation</li>
 * <li>Particles spawn around the floating item</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses {@link RenderItem} for base item rendering</li>
 * <li>Uses {@link EffectHelper} for particle effects</li>
 * <li>Implements {@link #doRender(Entity, double, double, double, float, float)}</li>
 * <li>Implements {@link #getEntityTexture(Entity)}</li>
 * <li>Additive blending ({@link GL11#GL_BLEND} with {@link GL11#GL_ONE})</li>
 * </ul>
 */
public class RenderEntityItemStardust extends Render {

    /**
     * Primary stardust color - cyan/blue
     */
    private static final Color STARDUST_COLOR = new Color(100, 200, 255);

    /**
     * Secondary sparkle color - bright white-blue
     */
    private static final Color SPARKLE_COLOR = new Color(200, 230, 255);

    /**
     * Sparkle color variant 1 - deep blue
     */
    private static final Color SPARKLE_COLOR_2 = new Color(50, 100, 255);

    /**
     * Number of particles to spawn per tick
     */
    private static final int PARTICLES_PER_TICK = 2;

    /**
     * Particle spawn chance (0.0 to 1.0)
     */
    private static final float PARTICLE_SPAWN_CHANCE = 0.8F;

    /**
     * Create a new RenderEntityItemStardust
     *
     * @param renderItem The base item renderer
     */
    public RenderEntityItemStardust(RenderItem renderItem) {
        super();
        this.itemRenderer = renderItem;
        this.shadowSize = 0.15F; // Small shadow for floating item
        this.shadowOpaque = 0.3F; // Semi-transparent shadow
    }

    /** The item renderer instance */
    private final RenderItem itemRenderer;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityItemStardust)) {
            return;
        }

        EntityItemStardust stardust = (EntityItemStardust) entity;

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Render the base item first
        if (stardust.getEntityItem() != null) {
            // Use item renderer to render the item
            this.itemRenderer.doRender(stardust, x, y, z, pitch, partialTicks);
        }

        // Restore state and add effects
        GL11.glPopAttrib();

        // Spawn sparkle particles around the stardust
        if (entity.worldObj.rand.nextFloat() < PARTICLE_SPAWN_CHANCE) {
            spawnStardustParticles(stardust);
        }

        // Render additional glow effect
        renderStardustGlow(stardust, x, y, z, partialTicks);
    }

    /**
     * Spawn sparkle particles around the stardust entity
     *
     * @param stardust The stardust entity
     */
    private void spawnStardustParticles(EntityItemStardust stardust) {
        // Only spawn on client side
        if (stardust.worldObj.isRemote) {
            // Spawn particles around the item
            for (int i = 0; i < PARTICLES_PER_TICK; i++) {
                // Random position around the entity
                double offsetX = (stardust.worldObj.rand.nextDouble() - 0.5) * 0.3;
                double offsetY = (stardust.worldObj.rand.nextDouble() - 0.5) * 0.3;
                double offsetZ = (stardust.worldObj.rand.nextDouble() - 0.5) * 0.3;

                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(
                    stardust.posX + offsetX,
                    stardust.posY + offsetY + stardust.height / 2.0,
                    stardust.posZ + offsetZ);

                // Random motion
                particle.motion(
                    (stardust.worldObj.rand.nextDouble() - 0.5) * 0.02,
                    stardust.worldObj.rand.nextDouble() * 0.03, // Upward bias
                    (stardust.worldObj.rand.nextDouble() - 0.5) * 0.02);

                // Set gravity
                particle.gravity(0.01);

                // Random scale
                float scale = 0.1F + stardust.worldObj.rand.nextFloat() * 0.2F;
                particle.scale(scale);

                // Random color choice
                int colorChoice = stardust.worldObj.rand.nextInt(4);
                switch (colorChoice) {
                    case 0:
                        particle.setColor(STARDUST_COLOR);
                        break;
                    case 1:
                        particle.setColor(SPARKLE_COLOR);
                        break;
                    case 2:
                        particle.setColor(SPARKLE_COLOR_2);
                        break;
                    case 3:
                        particle.setColor(Color.WHITE);
                        break;
                }

                // Set lifetime
                particle.setMaxAge(20 + stardust.worldObj.rand.nextInt(20));
            }
        }
    }

    /**
     * Render an additional glow effect around the stardust
     *
     * @param stardust     The stardust entity
     * @param x            X position
     * @param y            Y position
     * @param z            Z position
     * @param partialTicks Partial tick time
     */
    private void renderStardustGlow(EntityItemStardust stardust, double x, double y, double z, float partialTicks) {
        // Save OpenGL state
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Enable additive blending for glow
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        // Disable depth write
        GL11.glDepthMask(false);

        // Disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);

        // Calculate pulse
        float pulse = (float) Math.sin((stardust.ticksExisted + partialTicks) * 0.2) * 0.5F + 0.5F;
        float alpha = 0.2F + pulse * 0.15F;

        // Set color
        float r = STARDUST_COLOR.getRed() / 255.0F;
        float g = STARDUST_COLOR.getGreen() / 255.0F;
        float b = STARDUST_COLOR.getBlue() / 255.0F;

        GL11.glColor4f(r, g, b, alpha);

        // Render glow sprite
        float scale = 0.4F + pulse * 0.2F;
        float rotation = (stardust.ticksExisted + partialTicks) * 3.0F;

        // Use RenderingUtils to render a facing quad
        hellfirepvp.astralsorcery.client.util.RenderingUtils
            .renderFacingQuad(x, y + stardust.height / 2.0, z, partialTicks, scale, rotation, 0.0, 0.0, 1.0, 1.0);

        // Restore state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityItem) {
            EntityItem item = (EntityItem) entity;
            ItemStack stack = item.getEntityItem();

            if (stack != null && stack.getItem() != null) {
                // Use the item's texture
                return net.minecraft.client.renderer.texture.TextureMap.locationItemsTexture;
            }
        }

        // Fallback texture
        return ResourceLocationRegister.getItemStardust();
    }

    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render stardust - it's an important crafting component
        return true;
    }
}
