/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntityIlluminationSpark - Illumination spark renderer
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import java.awt.Color;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;

/**
 * RenderEntityIlluminationSpark - Illumination spark renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Glowing particle effect</li>
 * <li>Yellow/white/orange color scheme</li>
 * <li>Pulsing animation</li>
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
public class RenderEntityIlluminationSpark extends Render {

    /**
     * Spark colors (yellow/white/orange)
     */
    private static final Color[] SPARK_COLORS = new Color[] { Color.WHITE, new Color(0xFEFF9E), new Color(0xFFAA00) };

    /**
     * Base scale for the spark
     */
    private static final float BASE_SCALE = 0.3F;

    public RenderEntityIlluminationSpark() {
        this.shadowSize = 0.0F; // Sparks don't cast shadows
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityIlluminationSpark)) {
            return;
        }

        EntityIlluminationSpark spark = (EntityIlluminationSpark) entity;

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

        // Calculate pulse based on entity age
        float pulse = (float) Math.sin((spark.ticksExisted + partialTicks) * 0.3F) * 0.5F + 0.5F;

        // Calculate scale with pulse
        float scale = BASE_SCALE * (0.8F + pulse * 0.4F);

        // Calculate alpha based on pulse
        float alpha = 0.7F + pulse * 0.3F;

        // Render outer glow
        renderSparkGlow(scale, alpha, SPARK_COLORS[1]);

        // Render core
        renderSparkCore(scale * 0.5F, alpha, SPARK_COLORS[0]);

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
        tess.setColorRGBA_F(r, g, b, alpha * 0.4F);

        float size = 0.5F;
        float hs = size / 2.0F;

        // Billboard quad (simplified - will face player due to no rotation)
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
        tess.setColorRGBA_F(r, g, b, alpha * 0.9F);

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
        return ResourceLocationRegister.getEntitySparkIllumination();
    }

    /**
     * Check if this renderer should render the entity
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render sparks if they're within normal render distance
        return true;
    }
}
