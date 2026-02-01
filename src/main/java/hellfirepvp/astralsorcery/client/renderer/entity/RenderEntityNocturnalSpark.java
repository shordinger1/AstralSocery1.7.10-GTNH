/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntityNocturnalSpark - Nocturnal spark renderer
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import java.awt.Color;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark;

/**
 * RenderEntityNocturnalSpark - Nocturnal spark renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Dark purple/black particle effect</li>
 * <li>Flickering animation</li>
 * <li>Only renders at night (in gameplay)</li>
 * <li>Additive blending for glow</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses Tessellator for geometry</li>
 * <li>GL11 for OpenGL operations</li>
 * </ul>
 */
public class RenderEntityNocturnalSpark extends Render {

    /**
     * Nocturnal spark color - dark purple
     */
    private static final Color NOCTURNAL_COLOR = new Color(0x4B0082);

    /**
     * Accent color - magenta
     */
    private static final Color ACCENT_COLOR = new Color(0xFF00FF);

    /**
     * Base scale for the spark
     */
    private static final float BASE_SCALE = 0.3F;

    public RenderEntityNocturnalSpark() {
        this.shadowSize = 0.0F; // Sparks don't cast shadows
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityNocturnalSpark)) {
            return;
        }

        EntityNocturnalSpark spark = (EntityNocturnalSpark) entity;

        // Bind entity texture
        this.bindEntityTexture(entity);

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable additive blending for glow effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Move to spark position
        GL11.glTranslated(x, y, z);

        // Calculate flicker based on entity age and random
        float flicker = (float) Math.sin((spark.ticksExisted + partialTicks) * 0.5F) * 0.5F
            + (float) Math.cos((spark.ticksExisted + partialTicks) * 0.3F) * 0.3F;
        flicker = flicker * 0.5F + 0.5F; // Normalize to 0-1

        // Calculate scale with flicker
        float scale = BASE_SCALE * (0.7F + flicker * 0.6F);

        // Calculate alpha based on flicker
        float alpha = 0.5F + flicker * 0.5F;

        // Rotate spark
        float rotation = (spark.ticksExisted + partialTicks) * 2.0F;
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);

        // Render outer glow (purple)
        renderSparkGlow(scale, alpha * 0.6F, NOCTURNAL_COLOR);

        // Render inner accent (magenta)
        float innerScale = scale * 0.6F;
        renderSparkCore(innerScale, alpha * 0.8F, ACCENT_COLOR);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Render spark outer glow
     */
    private void renderSparkGlow(float scale, float alpha, Color color) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);

        tess.startDrawing(GL11.GL_QUADS);
        tess.setColorRGBA_F(r, g, b, alpha);

        float size = 0.5F;
        float hs = size / 2.0F;

        // Billboard quad
        tess.addVertex(-hs, -hs, hs);
        tess.addVertex(hs, -hs, hs);
        tess.addVertex(hs, hs, hs);
        tess.addVertex(-hs, hs, hs);

        tess.draw();
        GL11.glPopMatrix();
    }

    /**
     * Render spark core
     */
    private void renderSparkCore(float scale, float alpha, Color color) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);

        tess.startDrawing(GL11.GL_QUADS);
        tess.setColorRGBA_F(r, g, b, alpha);

        float size = 0.5F;
        float hs = size / 2.0F;

        // Billboard quad
        tess.addVertex(-hs, -hs, hs);
        tess.addVertex(hs, -hs, hs);
        tess.addVertex(hs, hs, hs);
        tess.addVertex(-hs, hs, hs);

        tess.draw();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // Procedural rendering, no texture needed
        return ResourceLocationRegister.getEntitySparkNocturnal();
    }

    /**
     * Check if this renderer should render the entity
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render sparks if they're within normal render distance
        return true;
    }
}
