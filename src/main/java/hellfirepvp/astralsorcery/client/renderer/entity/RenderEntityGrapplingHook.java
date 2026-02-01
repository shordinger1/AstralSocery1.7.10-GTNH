/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntityGrapplingHook - Grappling hook renderer with rope visualization
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityGrapplingHook;

/**
 * RenderEntityGrapplingHook - Grappling hook renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Rope line from player to hook</li>
 * <li>Glowing hook head</li>
 * <li>Animated rope based on pullFactor</li>
 * <li>Simplified implementation without RenderingUtils.interpolatePosition()</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses Tessellator for rope geometry</li>
 * <li>GL11 for OpenGL operations</li>
 * </ul>
 */
public class RenderEntityGrapplingHook extends Render {

    /**
     * Rope color - cyan/teal
     */
    private static final float ROPE_COLOR[] = { 0.0F, 0.8F, 1.0F };

    /**
     * Hook glow color - bright cyan
     */
    private static final float HOOK_COLOR[] = { 0.3F, 0.9F, 1.0F };

    public RenderEntityGrapplingHook() {
        this.shadowSize = 0.0F; // Hook doesn't cast shadow
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityGrapplingHook)) {
            return;
        }

        EntityGrapplingHook hook = (EntityGrapplingHook) entity;

        // Bind entity texture
        this.bindEntityTexture(entity);

        // Check despawn for fade effect
        float despawnPct = hook.despawning != -1 ? hook.despawnPercentage(partialTicks) : 0.0F;
        if (despawnPct >= 1.0F) {
            return; // Fully despawned, don't render
        }

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable blending for glow effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Calculate alpha based on despawn
        float alpha = 1.0F - despawnPct;

        // Render rope from player to hook
        renderRope(hook, partialTicks, alpha);

        // Render hook head
        renderHookHead(hook, x, y, z, partialTicks, alpha);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Render rope from player to hook
     */
    private void renderRope(EntityGrapplingHook hook, float partialTicks, float alpha) {
        if (hook.getThrower() == null) {
            return;
        }

        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        // Get positions
        double playerX = hook.getThrower().posX;
        double playerY = hook.getThrower().posY + hook.getThrower().height / 2.0;
        double playerZ = hook.getThrower().posZ;

        double hookX = hook.posX;
        double hookY = hook.posY;
        double hookZ = hook.posZ;

        // Calculate distance
        double dx = hookX - playerX;
        double dy = hookY - playerY;
        double dz = hookZ - playerZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist < 0.01) {
            return; // Too close, don't render
        }

        // Normalize direction
        double dirX = dx / dist;
        double dirY = dy / dist;
        double dirZ = dz / dist;

        // Render rope as line segments
        GL11.glLineWidth(2.0F);
        GL11.glColor4f(ROPE_COLOR[0], ROPE_COLOR[1], ROPE_COLOR[2], alpha * 0.8F);

        tess.startDrawing(GL11.GL_LINE_STRIP);

        // Number of segments based on distance
        int segments = Math.min(50, (int) (dist * 5));
        segments = Math.max(5, segments);

        for (int i = 0; i <= segments; i++) {
            float t = i / (float) segments;

            // Base position along line
            double px = playerX + dx * t;
            double py = playerY + dy * t;
            double pz = playerZ + dz * t;

            // Add wave effect based on pullFactor
            if (hook.isPulling()) {
                float wave = (float) Math.sin(t * Math.PI * 4 + hook.ticksExisted * 0.2F) * hook.pullFactor * 0.3F;
                // Perpendicular offset (simplified)
                py += wave;
            }

            tess.addVertex(px, py, pz);
        }

        tess.draw();
        GL11.glLineWidth(1.0F);
    }

    /**
     * Render hook head as glowing sphere
     */
    private void renderHookHead(EntityGrapplingHook hook, double x, double y, double z, float partialTicks,
        float alpha) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        // Move to hook position
        GL11.glTranslated(x, y, z);

        // Rotate based on ticks
        float rotation = (hook.ticksExisted + partialTicks) * 2.0F;
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);

        // Outer glow (larger, more transparent)
        tess.startDrawing(GL11.GL_TRIANGLES);
        tess.setColorRGBA_F(HOOK_COLOR[0], HOOK_COLOR[1], HOOK_COLOR[2], alpha * 0.3F);

        float outerSize = 0.15F;
        renderOctahedron(tess, outerSize);

        tess.draw();

        // Inner core (smaller, brighter)
        tess.startDrawing(GL11.GL_TRIANGLES);
        tess.setColorRGBA_F(HOOK_COLOR[0] * 1.2F, HOOK_COLOR[1] * 1.2F, HOOK_COLOR[2] * 1.2F, alpha * 0.8F);

        float innerSize = 0.08F;
        renderOctahedron(tess, innerSize);

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

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // Procedural rendering, no texture needed
        return ResourceLocationRegister.getEntityGrapplingHook();
    }
}
