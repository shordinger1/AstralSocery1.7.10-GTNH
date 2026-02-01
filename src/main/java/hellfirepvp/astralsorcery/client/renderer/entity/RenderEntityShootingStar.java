/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntityShootingStar - Shooting star renderer with trail effect
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityShootingStar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * RenderEntityShootingStar - Shooting star renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Glowing star head</li>
 * <li>Particle trail effect</li>
 * <li>Rotating animation</li>
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
public class RenderEntityShootingStar extends Render {

    /**
     * Star core color - white/yellow
     */
    private static final Color CORE_COLOR = new Color(0xFFFFE0);

    /**
     * Trail color - cyan/blue
     */
    private static final Color TRAIL_COLOR = new Color(0x69B5FF);

    /**
     * Outer glow color - purple
     */
    private static final Color GLOW_COLOR = new Color(0xAA66FF);

    /**
     * Trail length (number of segments)
     */
    private static final int TRAIL_LENGTH = 15;

    /**
     * Trail history for smooth rendering
     */
    private static class TrailHistory {

        final long seed;
        final Vector3 position;
        final float partialTick;

        TrailHistory(long seed, Vector3 position, float partialTick) {
            this.seed = seed;
            this.position = position;
            this.partialTick = partialTick;
        }
    }

    /**
     * Thread-local trail history to avoid concurrency issues
     */
    private static final ThreadLocal<List<TrailHistory>> TRAIL_HISTORY = ThreadLocal.withInitial(LinkedList::new);

    public RenderEntityShootingStar() {
        this.shadowSize = 0.0F; // Shooting stars don't cast shadows
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityShootingStar)) {
            return;
        }

        EntityShootingStar star = (EntityShootingStar) entity;

        // Bind entity texture
        this.bindEntityTexture(entity);

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable additive blending for glow effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // Render trail
        renderTrail(star, x, y, z, partialTicks);

        // Render star head
        renderStarHead(star, x, y, z, partialTicks);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        // Update trail history
        updateTrailHistory(star, partialTicks);
    }

    /**
     * Render shooting star trail
     */
    private void renderTrail(EntityShootingStar star, double x, double y, double z, float partialTicks) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        List<TrailHistory> history = TRAIL_HISTORY.get();

        if (history.isEmpty()) {
            return;
        }

        // Get motion vector
        Vector3 motion = new Vector3(star.motionX, star.motionY, star.motionZ);
        double speed = motion.length();
        if (speed < 0.01) {
            motion = new Vector3(0, -1, 0); // Default downward
        } else {
            motion = motion.normalize();
        }

        // Render trail as series of fading quads
        GL11.glPushMatrix();

        // Calculate rotation to face player
        net.minecraft.entity.Entity renderViewEntity = net.minecraft.client.Minecraft.getMinecraft().renderViewEntity;
        if (renderViewEntity != null) {
            float yaw = renderViewEntity.prevRotationYaw
                + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * partialTicks;
            float pitch = renderViewEntity.prevRotationPitch
                + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * partialTicks;

            GL11.glRotatef(-yaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        }

        int historyIndex = 0;
        int maxHistory = Math.min(history.size(), TRAIL_LENGTH);

        for (int i = 0; i < maxHistory; i++) {
            TrailHistory hist = history.get(history.size() - 1 - i);

            // Calculate trail position
            double trailX = x - motion.getX() * i * 0.5;
            double trailY = y - motion.getY() * i * 0.5;
            double trailZ = z - motion.getZ() * i * 0.5;

            // Calculate alpha based on position in trail
            float alpha = 1.0F - (i / (float) maxHistory);
            alpha *= 0.6F; // Max trail alpha

            // Calculate scale based on position
            float scale = 0.3F * (1.0F - i / (float) TRAIL_LENGTH);

            // Render trail segment
            renderTrailQuad(tess, trailX, trailY, trailZ, scale, TRAIL_COLOR, alpha);
        }

        GL11.glPopMatrix();
    }

    /**
     * Render a single trail quad (billboard)
     */
    private void renderTrailQuad(net.minecraft.client.renderer.Tessellator tess, double x, double y, double z,
        float scale, Color color, float alpha) {
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        tess.startDrawing(GL11.GL_QUADS);
        tess.setColorRGBA_F(r, g, b, alpha);

        float hs = scale / 2.0F; // Half size

        // Billboard quad
        tess.addVertex(x - hs, y - hs, z);
        tess.addVertex(x + hs, y - hs, z);
        tess.addVertex(x + hs, y + hs, z);
        tess.addVertex(x - hs, y + hs, z);

        tess.draw();
    }

    /**
     * Render shooting star head
     */
    private void renderStarHead(EntityShootingStar star, double x, double y, double z, float partialTicks) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        // Move to star position
        GL11.glTranslated(x, y, z);

        // Rotate star
        float rotation = (star.ticksExisted + partialTicks) * 5.0F;
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotation * 0.3F, 1.0F, 0.0F, 0.0F);

        // Render outer glow (largest, most transparent)
        float glowSize = 0.4F;
        float glowAlpha = 0.3F;
        renderStarGlow(tess, glowSize, GLOW_COLOR, glowAlpha);

        // Render middle layer
        float midSize = 0.25F;
        float midAlpha = 0.5F;
        renderStarGlow(tess, midSize, TRAIL_COLOR, midAlpha);

        // Render core (smallest, brightest)
        float coreSize = 0.15F;
        float coreAlpha = 1.0F;
        renderStarCore(tess, coreSize, CORE_COLOR, coreAlpha);
    }

    /**
     * Render star glow layer
     */
    private void renderStarGlow(net.minecraft.client.renderer.Tessellator tess, float size, Color color, float alpha) {
        tess.startDrawing(GL11.GL_TRIANGLES);

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        tess.setColorRGBA_F(r, g, b, alpha);

        // Render octahedron
        renderOctahedron(tess, size);

        tess.draw();
    }

    /**
     * Render star core
     */
    private void renderStarCore(net.minecraft.client.renderer.Tessellator tess, float size, Color color, float alpha) {
        tess.startDrawing(GL11.GL_TRIANGLES);

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        tess.setColorRGBA_F(r, g, b, alpha);

        // Render smaller octahedron for core
        renderOctahedron(tess, size);

        tess.draw();
    }

    /**
     * Render octahedron geometry
     */
    private void renderOctahedron(net.minecraft.client.renderer.Tessellator tess, float size) {
        // Top pyramid (4 faces)
        addTriangle(tess, 0, size, 0, size, 0, 0, 0, 0, size);
        addTriangle(tess, 0, size, 0, 0, 0, size, -size, 0, 0);
        addTriangle(tess, 0, size, 0, -size, 0, 0, 0, 0, -size);
        addTriangle(tess, 0, size, 0, 0, 0, -size, size, 0, 0);

        // Bottom pyramid (4 faces)
        addTriangle(tess, 0, -size, 0, 0, 0, size, size, 0, 0);
        addTriangle(tess, 0, -size, 0, -size, 0, 0, 0, 0, size);
        addTriangle(tess, 0, -size, 0, 0, 0, -size, -size, 0, 0);
        addTriangle(tess, 0, -size, 0, size, 0, 0, 0, 0, -size);
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

    /**
     * Update trail history
     */
    private void updateTrailHistory(EntityShootingStar star, float partialTicks) {
        List<TrailHistory> history = TRAIL_HISTORY.get();

        // Add current position to history
        Vector3 pos = new Vector3(star.posX, star.posY, star.posZ);
        long seed = star.getEffectSeed();
        history.add(new TrailHistory(seed, pos, partialTicks));

        // Trim history to max length
        while (history.size() > TRAIL_LENGTH) {
            history.remove(0);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // Procedural rendering, no texture needed
        return ResourceLocationRegister.getEntityShootingStar();
    }

    /**
     * Check if this renderer should render the entity
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render shooting stars if they're within extended render distance
        return true;
    }
}
