/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntityLiquidSpark - Liquid spark renderer
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import java.awt.Color;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityLiquidSpark;

/**
 * RenderEntityLiquidSpark - Liquid spark renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Blue/cyan liquid particle effect</li>
 * <li>Dripping animation</li>
 * <li>Translucent with depth buffer</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses Tessellator for geometry</li>
 * <li>GL11 for OpenGL operations</li>
 * </ul>
 */
public class RenderEntityLiquidSpark extends Render {

    /**
     * Liquid color - cyan/blue
     */
    private static final Color LIQUID_COLOR = new Color(0x00BFFF);

    /**
     * Highlight color - white
     */
    private static final Color HIGHLIGHT_COLOR = new Color(0xFFFFFF);

    /**
     * Base scale for the spark
     */
    private static final float BASE_SCALE = 0.25F;

    public RenderEntityLiquidSpark() {
        this.shadowSize = 0.0F; // Sparks don't cast shadows
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityLiquidSpark)) {
            return;
        }

        EntityLiquidSpark spark = (EntityLiquidSpark) entity;

        // Bind entity texture
        this.bindEntityTexture(entity);

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable blending for translucent effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Move to spark position
        GL11.glTranslated(x, y, z);

        // Calculate wobble based on entity age
        float wobble = (float) Math.sin((spark.ticksExisted + partialTicks) * 0.4F) * 0.1F;
        GL11.glRotatef(wobble * 45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(wobble * 30.0F, 0.0F, 1.0F, 0.0F);

        // Calculate scale
        float scale = BASE_SCALE;

        // Render liquid drop
        renderLiquidDrop(scale, 0.7F);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Render liquid drop shape
     */
    private void renderLiquidDrop(float scale, float alpha) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);

        // Render main drop body (teardrop shape)
        tess.startDrawing(GL11.GL_TRIANGLES);

        float r = LIQUID_COLOR.getRed() / 255.0F;
        float g = LIQUID_COLOR.getGreen() / 255.0F;
        float b = LIQUID_COLOR.getBlue() / 255.0F;
        tess.setColorRGBA_F(r, g, b, alpha * 0.7F);

        // Teardrop shape - stretched sphere
        renderSphere(tess, 1.0F, 1.4F, 1.0F);

        tess.draw();

        // Render highlight (specular reflection)
        tess.startDrawing(GL11.GL_TRIANGLES);

        float hr = HIGHLIGHT_COLOR.getRed() / 255.0F;
        float hg = HIGHLIGHT_COLOR.getGreen() / 255.0F;
        float hb = HIGHLIGHT_COLOR.getBlue() / 255.0F;
        tess.setColorRGBA_F(hr, hg, hb, alpha * 0.9F);

        // Small highlight sphere
        GL11.glTranslatef(0.3F, 0.3F, 0.3F);
        renderSphere(tess, 0.3F, 0.3F, 0.3F);

        tess.draw();

        GL11.glPopMatrix();
    }

    /**
     * Render simplified sphere (octahedron with subdivision)
     */
    private void renderSphere(net.minecraft.client.renderer.Tessellator tess, float scaleX, float scaleY,
        float scaleZ) {
        // Render octahedron as base sphere
        float size = 0.5F;

        // Top pyramid
        addTriangle(tess, 0, size * scaleY, 0, size * scaleX, 0, 0, 0, 0, size * scaleZ);
        addTriangle(tess, 0, size * scaleY, 0, 0, 0, size * scaleZ, -size * scaleX, 0, 0);
        addTriangle(tess, 0, size * scaleY, 0, -size * scaleX, 0, 0, 0, 0, -size * scaleZ);
        addTriangle(tess, 0, size * scaleY, 0, 0, 0, -size * scaleZ, size * scaleX, 0, 0);

        // Bottom pyramid
        addTriangle(tess, 0, -size * scaleY, 0, 0, 0, size * scaleZ, size * scaleX, 0, 0);
        addTriangle(tess, 0, -size * scaleY, 0, -size * scaleX, 0, 0, 0, 0, size * scaleZ);
        addTriangle(tess, 0, -size * scaleY, 0, 0, 0, -size * scaleZ, -size * scaleX, 0, 0);
        addTriangle(tess, 0, -size * scaleY, 0, size * scaleX, 0, 0, 0, 0, -size * scaleZ);
    }

    /**
     * Add triangle to tessellator
     */
    private void addTriangle(net.minecraft.client.renderer.Tessellator t, float x1, float y1, float z1, float x2,
        float y2, float z2, float x3, float y3, float z3) {
        // Calculate normal
        float ux = x2 - x1;
        float uy = y2 - y1;
        float uz = z2 - z1;
        float vx = x3 - x1;
        float vy = y3 - y1;
        float vz = z3 - z1;

        float nx = uy * vz - uz * vy;
        float ny = uz * vx - ux * vz;
        float nz = ux * vy - uy * vx;

        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0) {
            nx /= length;
            ny /= length;
            nz /= length;
        }

        t.setNormal(nx, ny, nz);
        t.addVertex(x1, y1, z1);
        t.addVertex(x2, y2, z2);
        t.addVertex(x3, y3, z3);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // Procedural rendering, no texture needed
        return ResourceLocationRegister.getEntitySparkLiquid();
    }

    /**
     * Check if this renderer should render the entity
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render sparks if they're within normal render distance
        return true;
    }
}
