/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base TileEntitySpecialRenderer class for all AstralSorcery TESRs
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * AstralBaseTESR - Base class for all AstralSorcery TileEntitySpecialRenderers
 * <p>
 * Provides common rendering functionality including:
 * - OpenGL state management
 * - Color management
 * - Transform methods
 * - Tessellator drawing helpers
 * - Animation interpolation helpers
 * <p>
 * All AstralSorcery TESRs should extend this class.
 * <p>
 * <b>NOTE:</b> 1.7.10 does not use generics for TESR like 1.12.2 does.
 * Subclasses should implement renderTileEntityAt() and cast the TileEntity parameter
 * to their specific type.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
@SideOnly(Side.CLIENT)
public abstract class AstralBaseTESR extends TileEntitySpecialRenderer {

    // ========== Constructor ==========

    public AstralBaseTESR() {
        super();
    }

    // ========== Must Implement ==========

    // /**
    // * Render the TileEntity
    // * @param te TileEntity instance
    // * @param x, y, z World coordinates
    // * @param partialTick Partial tick time (for animation interpolation)
    // */
    // public abstract void renderTileEntityAt(T te, double x, double y, double z, float partialTick){}

    // ========== Basic Render Tools ==========

    /**
     * Save OpenGL state
     * Must call restoreState() after
     */
    protected void saveState() {
        GL11.glPushMatrix();
    }

    /**
     * Restore OpenGL state
     */
    protected void restoreState() {
        GL11.glPopMatrix();
    }

    /**
     * Translate to TileEntity position
     */
    protected void translateToTileEntity(double x, double y, double z) {
        GL11.glTranslatef((float) x, (float) y, (float) z);
    }

    /**
     * Translate to TileEntity center
     */
    protected void translateToCenter(double x, double y, double z) {
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
    }

    /**
     * Bind texture
     * 
     * @param texture Resource location
     */
    protected void bindTexture(ResourceLocation texture) {
        super.bindTexture(texture);
    }

    /**
     * Bind texture (simplified)
     * 
     * @param modId       Mod ID
     * @param texturePath Texture path (relative to textures folder)
     */
    protected void bindTexture(String modId, String texturePath) {
        ResourceLocation location = new ResourceLocation(modId + ":" + texturePath);
        this.bindTexture(location);
    }

    // ========== OpenGL State Control ==========

    /**
     * Enable lighting
     */
    protected void enableLighting() {
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Disable lighting
     */
    protected void disableLighting() {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    /**
     * Enable blend
     */
    protected void enableBlend() {
        GL11.glEnable(GL11.GL_BLEND);
    }

    /**
     * Disable blend
     */
    protected void disableBlend() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Set standard blend mode
     */
    protected void setStandardBlend() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Set additive blend mode (glowing effect)
     */
    protected void setAdditiveBlend() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    }

    /**
     * Enable depth mask
     */
    protected void enableDepthMask() {
        GL11.glDepthMask(true);
    }

    /**
     * Disable depth mask
     */
    protected void disableDepthMask() {
        GL11.glDepthMask(false);
    }

    /**
     * Enable cull face
     */
    protected void enableCullFace() {
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * Disable cull face
     */
    protected void disableCullFace() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    /**
     * Enable rescale normal
     */
    protected void enableRescaleNormal() {
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    /**
     * Disable rescale normal
     */
    protected void disableRescaleNormal() {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    /**
     * Enable texture
     */
    protected void enableTexture() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Disable texture
     */
    protected void disableTexture() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    // ========== Color Setting ==========

    /**
     * Set color (RGB)
     * 
     * @param r, g, b Red/green/blue (0.0-1.0)
     */
    protected void setColor3f(float r, float g, float b) {
        GL11.glColor3f(r, g, b);
    }

    /**
     * Set color (RGBA)
     * 
     * @param r, g, b Red/green/blue (0.0-1.0)
     * @param a  Alpha (0.0-1.0)
     */
    protected void setColor4f(float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
    }

    /**
     * Set color (integer RGB)
     * 
     * @param rgb 0xRRGGBB
     */
    protected void setColorRGB(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        GL11.glColor3f(r, g, b);
    }

    /**
     * Set color (integer RGBA)
     * 
     * @param rgba 0xRRGGBBAA
     */
    protected void setColorRGBA(int rgba) {
        float r = ((rgba >> 24) & 0xFF) / 255.0F;
        float g = ((rgba >> 16) & 0xFF) / 255.0F;
        float b = ((rgba >> 8) & 0xFF) / 255.0F;
        float a = (rgba & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }

    /**
     * Reset color to white
     */
    protected void resetColor() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // ========== Transform Methods ==========

    /**
     * Translate
     */
    protected void translate(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }

    /**
     * Rotate
     * 
     * @param angle Angle (degrees)
     * @param x,    y, z Rotation axis
     */
    protected void rotate(float angle, float x, float y, float z) {
        GL11.glRotatef(angle, x, y, z);
    }

    /**
     * Scale
     */
    protected void scale(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    /**
     * Center and rotate
     */
    protected void centerAndRotate(float angle, float axisX, float axisY, float axisZ) {
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(angle, axisX, axisY, axisZ);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    // ========== Tessellator Drawing ==========

    /**
     * Start drawing quads
     */
    protected Tessellator startDrawingQuads() {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        return tessellator;
    }

    /**
     * Draw and finish
     */
    protected void draw() {
        Tessellator.instance.draw();
    }

    /**
     * Draw cube
     * 
     * @param x,     y, z Start position
     * @param width, height, depth Size
     */
    protected void drawCube(double x, double y, double z, double width, double height, double depth) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        double x2 = x + width;
        double y2 = y + height;
        double z2 = z + depth;

        // Front (Z+)
        tessellator.addVertexWithUV(x2, y, z2, 1, 0);
        tessellator.addVertexWithUV(x, y, z2, 0, 0);
        tessellator.addVertexWithUV(x, y2, z2, 0, 1);
        tessellator.addVertexWithUV(x2, y2, z2, 1, 1);

        // Back (Z-)
        tessellator.addVertexWithUV(x, y, z, 1, 0);
        tessellator.addVertexWithUV(x2, y, z, 0, 0);
        tessellator.addVertexWithUV(x2, y2, z, 0, 1);
        tessellator.addVertexWithUV(x, y2, z, 1, 1);

        // Left (X-)
        tessellator.addVertexWithUV(x, y, z, 1, 0);
        tessellator.addVertexWithUV(x, y, z2, 0, 0);
        tessellator.addVertexWithUV(x, y2, z2, 0, 1);
        tessellator.addVertexWithUV(x, y2, z, 1, 1);

        // Right (X+)
        tessellator.addVertexWithUV(x2, y, z2, 1, 0);
        tessellator.addVertexWithUV(x2, y, z, 0, 0);
        tessellator.addVertexWithUV(x2, y2, z, 0, 1);
        tessellator.addVertexWithUV(x2, y2, z2, 1, 1);

        // Top (Y+)
        tessellator.addVertexWithUV(x, y2, z2, 0, 1);
        tessellator.addVertexWithUV(x2, y2, z2, 1, 1);
        tessellator.addVertexWithUV(x2, y2, z, 1, 0);
        tessellator.addVertexWithUV(x, y2, z, 0, 0);

        // Bottom (Y-)
        tessellator.addVertexWithUV(x, y, z, 0, 0);
        tessellator.addVertexWithUV(x2, y, z, 1, 0);
        tessellator.addVertexWithUV(x2, y, z2, 1, 1);
        tessellator.addVertexWithUV(x, y, z2, 0, 1);

        tessellator.draw();
    }

    /**
     * Draw textured cube
     */
    protected void drawTexturedCube(double x, double y, double z, double width, double height, double depth,
        double minU, double maxU, double minV, double maxV) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        double x2 = x + width;
        double y2 = y + height;
        double z2 = z + depth;

        // Front
        tessellator.addVertexWithUV(x2, y, z2, maxU, minV);
        tessellator.addVertexWithUV(x, y, z2, minU, minV);
        tessellator.addVertexWithUV(x, y2, z2, minU, maxV);
        tessellator.addVertexWithUV(x2, y2, z2, maxU, maxV);

        // Back
        tessellator.addVertexWithUV(x, y, z, maxU, minV);
        tessellator.addVertexWithUV(x2, y, z, minU, minV);
        tessellator.addVertexWithUV(x2, y2, z, minU, maxV);
        tessellator.addVertexWithUV(x, y2, z, maxU, maxV);

        // Left
        tessellator.addVertexWithUV(x, y, z, maxU, minV);
        tessellator.addVertexWithUV(x, y, z2, minU, minV);
        tessellator.addVertexWithUV(x, y2, z2, minU, maxV);
        tessellator.addVertexWithUV(x, y2, z, maxU, maxV);

        // Right
        tessellator.addVertexWithUV(x2, y, z2, maxU, minV);
        tessellator.addVertexWithUV(x2, y, z, minU, minV);
        tessellator.addVertexWithUV(x2, y2, z, minU, maxV);
        tessellator.addVertexWithUV(x2, y2, z2, maxU, maxV);

        // Top
        tessellator.addVertexWithUV(x, y2, z2, minU, maxV);
        tessellator.addVertexWithUV(x2, y2, z2, maxU, maxV);
        tessellator.addVertexWithUV(x2, y2, z, maxU, minV);
        tessellator.addVertexWithUV(x, y2, z, minU, minV);

        // Bottom
        tessellator.addVertexWithUV(x, y, z, minU, minV);
        tessellator.addVertexWithUV(x2, y, z, maxU, minV);
        tessellator.addVertexWithUV(x2, y, z2, maxU, maxV);
        tessellator.addVertexWithUV(x, y, z2, minU, maxV);

        tessellator.draw();
    }

    /**
     * Draw horizontal ring
     */
    protected void drawRing(double centerX, double centerY, double centerZ, double innerRadius, double outerRadius,
        int segments) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        for (int i = 0; i < segments; i++) {
            double angle1 = (i * 2 * Math.PI) / segments;
            double angle2 = ((i + 1) * 2 * Math.PI) / segments;

            double x1_in = centerX + Math.cos(angle1) * innerRadius;
            double z1_in = centerZ + Math.sin(angle1) * innerRadius;
            double x1_out = centerX + Math.cos(angle1) * outerRadius;
            double z1_out = centerZ + Math.sin(angle1) * outerRadius;

            double x2_in = centerX + Math.cos(angle2) * innerRadius;
            double z2_in = centerZ + Math.sin(angle2) * innerRadius;
            double x2_out = centerX + Math.cos(angle2) * outerRadius;
            double z2_out = centerZ + Math.sin(angle2) * outerRadius;

            tessellator.addVertexWithUV(x1_in, centerY, z1_in, 0, 0);
            tessellator.addVertexWithUV(x1_out, centerY, z1_out, 1, 0);
            tessellator.addVertexWithUV(x2_out, centerY, z2_out, 1, 1);
            tessellator.addVertexWithUV(x2_in, centerY, z2_in, 0, 1);
        }

        tessellator.draw();
    }

    /**
     * Draw vertical cylinder
     */
    protected void drawCylinder(double x, double y, double z, double radius, double height, int segments) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        double y2 = y + height;

        for (int i = 0; i < segments; i++) {
            double angle1 = (i * 2 * Math.PI) / segments;
            double angle2 = ((i + 1) * 2 * Math.PI) / segments;

            double x1 = x + Math.cos(angle1) * radius;
            double z1 = z + Math.sin(angle1) * radius;
            double x2 = x + Math.cos(angle2) * radius;
            double z2 = z + Math.sin(angle2) * radius;

            tessellator.addVertexWithUV(x1, y, z1, 0, 0);
            tessellator.addVertexWithUV(x2, y, z2, 1, 0);
            tessellator.addVertexWithUV(x2, y2, z2, 1, 1);
            tessellator.addVertexWithUV(x1, y2, z1, 0, 1);
        }

        tessellator.draw();
    }

    // ========== Animation Helpers ==========

    /**
     * Linear interpolation
     * 
     * @param prev        Previous frame value
     * @param current     Current frame value
     * @param partialTick Partial tick time
     * @return Interpolated result
     */
    protected float interpolate(float prev, float current, float partialTick) {
        return prev + (current - prev) * partialTick;
    }

    /**
     * Linear interpolation (double)
     */
    protected double interpolate(double prev, double current, float partialTick) {
        return prev + (current - prev) * partialTick;
    }

    /**
     * Angle interpolation (handles wraparound)
     * 
     * @param prev        Previous angle (degrees)
     * @param current     Current angle (degrees)
     * @param partialTick Partial tick time
     * @return Interpolated angle
     */
    protected float interpolateAngle(float prev, float current, float partialTick) {
        float diff = current - prev;
        while (diff < -180.0F) diff += 360.0F;
        while (diff >= 180.0F) diff -= 360.0F;
        return prev + diff * partialTick;
    }

    /**
     * Get world time (with partialTick)
     */
    protected float getWorldTime(TileEntity te, float partialTick) {
        return te.getWorldObj()
            .getTotalWorldTime() + partialTick;
    }
}
