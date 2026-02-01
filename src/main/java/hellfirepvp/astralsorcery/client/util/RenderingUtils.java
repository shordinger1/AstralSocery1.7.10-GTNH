/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.awt.Color;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 29.08.2016 / 16:51
 *
 * 1.7.10 Port:
 * - Core rendering utilities for particle effects
 * - Simplified version containing only particle-system-required methods
 */
public class RenderingUtils {

    /**
     * Interpolate between two double values
     */
    public static double interpolate(double oldP, double newP, float partialTicks) {
        if (oldP == newP) return oldP;
        return oldP + ((newP - oldP) * partialTicks);
    }

    /**
     * Render a quad that always faces the player (billboard)
     * 1.7.10: Different ActiveRenderInfo API
     */
    public static void renderFacingQuad(double px, double py, double pz, float partialTicks, float scale, float angle,
        double u, double v, double uLength, double vLength) {
        // 1.7.10: Get camera rotation from ActiveRenderInfo
        float arX = 1.0F; // Will be calculated from renderer
        float arZ = 1.0F;
        float arYZ = 0.0F;
        float arXY = 0.0F;
        float arXZ = 0.0F;

        // Get the render view entity (player in most cases)
        Entity e = net.minecraft.client.Minecraft.getMinecraft().renderViewEntity;
        if (e == null) {
            e = net.minecraft.client.Minecraft.getMinecraft().thePlayer;
        }

        // Calculate interpolated player position
        double iPX = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks;
        double iPY = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks;
        double iPZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks;

        // 1.7.10: Calculate rotation values manually
        float yaw = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks;
        float pitch = e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks;

        float yawRad = yaw * (float) Math.PI / 180F;
        float pitchRad = pitch * (float) Math.PI / 180F;

        float cosYaw = MathHelper.cos(yawRad);
        float sinYaw = MathHelper.sin(yawRad);
        float cosPitch = MathHelper.cos(pitchRad);
        float sinPitch = MathHelper.sin(pitchRad);

        // Calculate billboard vectors
        Vector3 v1 = new Vector3(
            -cosYaw * scale - sinYaw * sinPitch * scale,
            -sinPitch * scale,
            -sinYaw * scale - cosYaw * sinPitch * scale);
        Vector3 v2 = new Vector3(
            -cosYaw * scale + sinYaw * sinPitch * scale,
            sinPitch * scale,
            -sinYaw * scale + cosYaw * sinPitch * scale);
        Vector3 v3 = new Vector3(
            cosYaw * scale + sinYaw * sinPitch * scale,
            sinPitch * scale,
            sinYaw * scale + cosYaw * sinPitch * scale);
        Vector3 v4 = new Vector3(
            cosYaw * scale - sinYaw * sinPitch * scale,
            -sinPitch * scale,
            sinYaw * scale - cosYaw * sinPitch * scale);

        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec)
                .normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }

        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_QUADS);
        t.addVertexWithUV(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ, u, v + vLength);
        t.addVertexWithUV(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ, u + uLength, v + vLength);
        t.addVertexWithUV(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ, u + uLength, v);
        t.addVertexWithUV(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ, u, v);
        t.draw();
    }

    /**
     * Render a billboard quad to a vertex buffer
     * 1.7.10: Uses Tessellator instead of BufferBuilder
     */
    public static void renderFacingFullQuadVB(Tessellator tessellator, double px, double py, double pz,
        float partialTicks, float scale, float angle, float colorRed, float colorGreen, float colorBlue, float alpha) {
        renderFacingQuadVB(
            tessellator,
            px,
            py,
            pz,
            partialTicks,
            scale,
            angle,
            0,
            0,
            1,
            1,
            colorRed,
            colorGreen,
            colorBlue,
            alpha);
    }

    /**
     * Render a colored billboard quad to vertex buffer
     * 1.7.10: Adapted for Tessellator API
     */
    public static void renderFacingQuadVB(Tessellator tessellator, double px, double py, double pz, float partialTicks,
        float scale, float angle, double u, double v, double uLength, double vLength, float colorRed, float colorGreen,
        float colorBlue, float alpha) {
        Entity e = net.minecraft.client.Minecraft.getMinecraft().renderViewEntity;
        if (e == null) {
            e = net.minecraft.client.Minecraft.getMinecraft().thePlayer;
        }

        double iPX = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks;
        double iPY = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks;
        double iPZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks;

        // 1.7.10: Calculate rotation values
        float yaw = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks;
        float pitch = e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks;

        float yawRad = yaw * (float) Math.PI / 180F;
        float pitchRad = pitch * (float) Math.PI / 180F;

        float cosYaw = MathHelper.cos(yawRad);
        float sinYaw = MathHelper.sin(yawRad);
        float cosPitch = MathHelper.cos(pitchRad);
        float sinPitch = MathHelper.sin(pitchRad);

        Vector3 v1 = new Vector3(
            -cosYaw * scale - sinYaw * sinPitch * scale,
            -sinPitch * scale,
            -sinYaw * scale - cosYaw * sinPitch * scale);
        Vector3 v2 = new Vector3(
            -cosYaw * scale + sinYaw * sinPitch * scale,
            sinPitch * scale,
            -sinYaw * scale + cosYaw * sinPitch * scale);
        Vector3 v3 = new Vector3(
            cosYaw * scale + sinYaw * sinPitch * scale,
            sinPitch * scale,
            sinYaw * scale + cosYaw * sinPitch * scale);
        Vector3 v4 = new Vector3(
            cosYaw * scale - sinYaw * sinPitch * scale,
            -sinPitch * scale,
            sinYaw * scale - cosYaw * sinPitch * scale);

        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec)
                .normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }

        // 1.7.10: Tessellator API for colored vertices
        tessellator.setColorRGBA_F(colorRed, colorGreen, colorBlue, alpha);
        tessellator.addVertexWithUV(
            px + v1.getX() - iPX,
            py + v1.getY() - iPY,
            pz + v1.getZ() - iPZ,
            u + uLength,
            v + vLength);
        tessellator.addVertexWithUV(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ, u + uLength, v);
        tessellator.addVertexWithUV(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ, u, v);
        tessellator.addVertexWithUV(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ, u, v + vLength);
    }

    /**
     * Helper class for math functions
     * 1.7.10: MathHelper doesn't have all methods, so we provide them here
     */
    public static class MathHelper {

        public static float cos(float rad) {
            return (float) Math.cos(rad);
        }

        public static float sin(float rad) {
            return (float) Math.sin(rad);
        }

        public static int clamp(int value, int min, int max) {
            return value < min ? min : (value > max ? max : value);
        }

        public static float clamp(float value, float min, float max) {
            return value < min ? min : (value > max ? max : value);
        }

        public static double clamp(double value, double min, double max) {
            return value < min ? min : (value > max ? max : value);
        }

        public static int floor(double value) {
            int i = (int) value;
            return value < (double) i ? i - 1 : i;
        }
    }

    // ========== Light Beam Effects ==========

    /**
     * Render a vertical light beam from (x,y,z) to (x,y+height,z)
     *
     * @param x,     y, z Start position
     * @param height Beam height
     * @param width  Beam width
     * @param color  Beam color
     * @param alpha  Beam alpha (0.0-1.0)
     */
    public static void renderLightBeam(double x, double y, double z, double height, double width, Color color,
        float alpha) {
        renderLightBeam(x, y, z, x, y + height, z, width, color, alpha);
    }

    /**
     * Render a light beam from start to end position
     *
     * @param x1,   y1, z1 Start position
     * @param x2,   y2, z2 End position
     * @param width Beam width
     * @param color Beam color
     * @param alpha Beam alpha (0.0-1.0)
     */
    public static void renderLightBeam(double x1, double y1, double z1, double x2, double y2, double z2, double width,
        Color color, float alpha) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        tessellator.setColorRGBA_F(r, g, b, alpha);

        // Calculate direction and perpendicular vectors
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (length < 0.01) {
            return; // Too short
        }

        // Normalize direction
        dx /= length;
        dy /= length;
        dz /= length;

        // Find perpendicular vector
        double px = dz;
        double py = 0;
        double pz = -dx;

        double plength = Math.sqrt(px * px + py * py + pz * pz);
        if (plength < 0.01) {
            px = 1;
            pz = 0;
            plength = 1;
        }

        px /= plength;
        py /= plength;
        pz /= plength;

        // Draw beam segments
        int segments = Math.max(4, (int) (length * 4));

        for (int i = 0; i < segments; i++) {
            double t1 = (double) i / segments;
            double t2 = (double) (i + 1) / segments;

            double bx1 = x1 + dx * t1 * length;
            double by1 = y1 + dy * t1 * length;
            double bz1 = z1 + dz * t1 * length;

            double bx2 = x1 + dx * t2 * length;
            double by2 = y1 + dy * t2 * length;
            double bz2 = z1 + dz * t2 * length;

            double ox1 = px * width;
            double oz1 = pz * width;

            tessellator.addVertexWithUV(bx1 - ox1, by1, bz1 - oz1, 0, t1);
            tessellator.addVertexWithUV(bx2 - ox1, by2, bz2 - oz1, 0, t2);
            tessellator.addVertexWithUV(bx2 + ox1, by2, bz2 + oz1, 1, t2);
            tessellator.addVertexWithUV(bx1 + ox1, by1, bz1 + oz1, 1, t1);
        }

        tessellator.draw();
    }

    // ========== Circle and Ring Effects ==========

    /**
     * Render a horizontal ring on the XZ plane
     *
     * @param x,          y, z Center position
     * @param innerRadius Inner radius
     * @param outerRadius Outer radius
     * @param color       Ring color
     * @param alpha       Ring alpha
     * @param segments    Number of segments
     */
    public static void renderRing(double x, double y, double z, double innerRadius, double outerRadius, Color color,
        float alpha, int segments) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        tessellator.setColorRGBA_F(r, g, b, alpha);

        for (int i = 0; i < segments; i++) {
            double angle1 = (i * 2 * Math.PI) / segments;
            double angle2 = ((i + 1) * 2 * Math.PI) / segments;

            double x1_in = x + Math.cos(angle1) * innerRadius;
            double z1_in = z + Math.sin(angle1) * innerRadius;
            double x1_out = x + Math.cos(angle1) * outerRadius;
            double z1_out = z + Math.sin(angle1) * outerRadius;

            double x2_in = x + Math.cos(angle2) * innerRadius;
            double z2_in = z + Math.sin(angle2) * innerRadius;
            double x2_out = x + Math.cos(angle2) * outerRadius;
            double z2_out = z + Math.sin(angle2) * outerRadius;

            tessellator.addVertexWithUV(x1_in, y, z1_in, 0, 0);
            tessellator.addVertexWithUV(x1_out, y, z1_out, 1, 0);
            tessellator.addVertexWithUV(x2_out, y, z2_out, 1, 1);
            tessellator.addVertexWithUV(x2_in, y, z2_in, 0, 1);
        }

        tessellator.draw();
    }

    /**
     * Render a filled circle on the XZ plane
     */
    public static void renderCircle(double x, double y, double z, double radius, Color color, float alpha,
        int segments) {
        renderRing(x, y, z, 0, radius, color, alpha, segments);
    }

    /**
     * Render a glowing ring with pulsing animation
     */
    public static void renderGlowingRing(double x, double y, double z, double baseRadius, double thickness, Color color,
        double time, int segments) {
        double pulse = Math.sin(time * 0.05) * 0.5 + 0.5;
        float alpha = 0.3F + (float) (pulse * 0.4F);
        double radius = baseRadius * (0.9 + pulse * 0.2);

        renderRing(x, y, z, radius - thickness / 2, radius + thickness / 2, color, alpha, segments);
    }

    // ========== Spiral Effects ==========

    /**
     * Render a vertical spiral
     */
    public static void renderSpiral(double x, double y, double z, double height, double maxRadius, Color color,
        float alpha, double rotations, int segments) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        tessellator.setColorRGBA_F(r, g, b, alpha);

        int totalSegments = (int) (segments * rotations);

        for (int i = 0; i < totalSegments; i++) {
            double t1 = (double) i / totalSegments;
            double t2 = (double) (i + 1) / totalSegments;

            double angle1 = t1 * rotations * 2 * Math.PI;
            double angle2 = t2 * rotations * 2 * Math.PI;

            double r1 = t1 * maxRadius;
            double r2 = t2 * maxRadius;

            double y1 = y + t1 * height;
            double y2 = y + t2 * height;

            double x1 = x + Math.cos(angle1) * r1;
            double z1 = z + Math.sin(angle1) * r1;
            double x2 = x + Math.cos(angle2) * r2;
            double z2 = z + Math.sin(angle2) * r2;

            tessellator.addVertexWithUV(x1, y1, z1, 0, t1);
            tessellator.addVertexWithUV(x2, y2, z2, 1, t2);
            tessellator.addVertexWithUV(x2, y2 + 0.05, z2, 1, t2);
            tessellator.addVertexWithUV(x1, y1 + 0.05, z1, 0, t1);
        }

        tessellator.draw();
    }

    // ========== Cube Rendering ==========

    /**
     * Render a wireframe cube outline
     */
    public static void renderWireframeCube(double x, double y, double z, double width, double height, double depth,
        Color color, float alpha) {
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        GL11.glColor4f(r, g, b, alpha);
        GL11.glBegin(GL11.GL_LINES);

        double x2 = x + width;
        double y2 = y + height;
        double z2 = z + depth;

        // Bottom face
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x2, y, z);
        GL11.glVertex3d(x2, y, z);
        GL11.glVertex3d(x2, y, z2);
        GL11.glVertex3d(x2, y, z2);
        GL11.glVertex3d(x, y, z2);
        GL11.glVertex3d(x, y, z2);
        GL11.glVertex3d(x, y, z);

        // Top face
        GL11.glVertex3d(x, y2, z);
        GL11.glVertex3d(x2, y2, z);
        GL11.glVertex3d(x2, y2, z);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x, y2, z2);
        GL11.glVertex3d(x, y2, z2);
        GL11.glVertex3d(x, y2, z);

        // Vertical edges
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y2, z);
        GL11.glVertex3d(x2, y, z);
        GL11.glVertex3d(x2, y2, z);
        GL11.glVertex3d(x2, y, z2);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x, y, z2);
        GL11.glVertex3d(x, y2, z2);

        GL11.glEnd();
    }

    /**
     * Render a solid textured cube
     */
    public static void renderTexturedCube(double x, double y, double z, double width, double height, double depth,
        IIcon icon) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        double x2 = x + width;
        double y2 = y + height;
        double z2 = z + depth;

        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        // Front (Z+)
        tessellator.addVertexWithUV(x2, y, z2, maxU, minV);
        tessellator.addVertexWithUV(x, y, z2, minU, minV);
        tessellator.addVertexWithUV(x, y2, z2, minU, maxV);
        tessellator.addVertexWithUV(x2, y2, z2, maxU, maxV);

        // Back (Z-)
        tessellator.addVertexWithUV(x, y, z, maxU, minV);
        tessellator.addVertexWithUV(x2, y, z, minU, minV);
        tessellator.addVertexWithUV(x2, y2, z, minU, maxV);
        tessellator.addVertexWithUV(x, y2, z, maxU, maxV);

        // Left (X-)
        tessellator.addVertexWithUV(x, y, z, maxU, minV);
        tessellator.addVertexWithUV(x, y, z2, minU, minV);
        tessellator.addVertexWithUV(x, y2, z2, minU, maxV);
        tessellator.addVertexWithUV(x, y2, z, maxU, maxV);

        // Right (X+)
        tessellator.addVertexWithUV(x2, y, z2, maxU, minV);
        tessellator.addVertexWithUV(x2, y, z, minU, minV);
        tessellator.addVertexWithUV(x2, y2, z, minU, maxV);
        tessellator.addVertexWithUV(x2, y2, z2, maxU, maxV);

        // Top (Y+)
        tessellator.addVertexWithUV(x, y2, z2, minU, maxV);
        tessellator.addVertexWithUV(x2, y2, z2, maxU, maxV);
        tessellator.addVertexWithUV(x2, y2, z, maxU, minV);
        tessellator.addVertexWithUV(x, y2, z, minU, minV);

        // Bottom (Y-)
        tessellator.addVertexWithUV(x, y, z, minU, minV);
        tessellator.addVertexWithUV(x2, y, z, maxU, minV);
        tessellator.addVertexWithUV(x2, y, z2, maxU, maxV);
        tessellator.addVertexWithUV(x, y, z2, minU, maxV);

        tessellator.draw();
    }

    // ========== Color Utilities ==========

    /**
     * Convert integer color to RGB float array
     */
    public static float[] intToRGB(int color) {
        return new float[] { ((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F };
    }

    /**
     * Blend two colors
     */
    public static Color blendColors(Color c1, Color c2, double factor) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * factor);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * factor);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * factor);
        int a = (int) (c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * factor);
        return new Color(r, g, b, a);
    }

    // ========== Animation Helpers ==========

    /**
     * Calculate pulse value (sine wave 0 to 1)
     */
    public static double pulse(double time, double speed) {
        return Math.sin(time * speed) * 0.5 + 0.5;
    }

    /**
     * Linear interpolation
     */
    public static double lerp(double from, double to, double factor) {
        return from + (to - from) * factor;
    }

    /**
     * Clamp value between 0 and 1
     */
    public static double clamp01(double value) {
        return MathHelper.clamp(value, 0.0, 1.0);
    }

}
