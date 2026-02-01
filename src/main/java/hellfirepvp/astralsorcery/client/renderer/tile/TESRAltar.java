/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRAltar - Altar TileEntitySpecialRenderer
 *
 * Renders the base altar model with multi-texture support + effects
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.renderer.AstralBaseTESR;
import hellfirepvp.astralsorcery.common.block.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESRAltar - Altar renderer (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Visual feedback for active crafting</li>
 * <li>Multiblock formation indicator</li>
 * <li>Starlight collection visualization (simplified)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>render() → renderTileEntityAt()</li>
 * <li>GlStateManager → GL11 direct calls</li>
 * <li>No generics - must cast TileEntity to specific type</li>
 * <li>RenderConstellation - Not yet migrated</li>
 * <li>RenderingUtils.renderLightRayEffects() - Not yet migrated</li>
 * <li>ClientScheduler - Use System.currentTimeMillis()</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>RenderConstellation - Constellation visualization</li>
 * <li>RenderingUtils - Light ray effects</li>
 * <li>ItemColorizationHelper - Item color extraction</li>
 * <li>ConstellationSkyHandler - Sky distribution calculation</li>
 * <li>TESRCollectorCrystal.renderCrystal() - Crystal rendering</li>
 * </ul>
 */
public class TESRAltar extends AstralBaseTESR {

    public TESRAltar() {
        super();
        LogHelper.info("[TESRAltar] TESRAltar instantiated");
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (te == null) {
            LogHelper.warn("[TESRAltar] renderTileEntityAt called with null tile");
            return;
        }

        if (!(te instanceof TileAltar)) {
            LogHelper.warn(
                "[TESRAltar] renderTileEntityAt called with wrong tile type: " + te.getClass()
                    .getName());
            return;
        }

        TileAltar altar = (TileAltar) te;

        // IMPORTANT: Render the base altar model first!
        renderBaseAltarModel(altar, x, y, z);

        // Then render effects based on altar level
        switch (altar.getAltarLevel()) {
            case BRILLIANCE:
            case TRAIT_CRAFT:
                renderTraitCraftAltar(altar, x, y, z, partialTick);
                break;
            case CONSTELLATION_CRAFT:
                renderConstellationCraftAltar(altar, x, y, z, partialTick);
                break;
            case ATTUNEMENT:
                renderAttunementAltar(altar, x, y, z, partialTick);
                break;
            case DISCOVERY:
            default:
                renderDiscoveryAltar(altar, x, y, z, partialTick);
                break;
        }

        // Render active crafting task
        ActiveCraftingTask task = altar.getActiveCraftingTask();
        if (task != null) {
            renderActiveCrafting(altar, x, y, z, partialTick, task);
        }
    }

    /**
     * Render the base altar model using Tessellator with icons from BlockAltar
     * This renders the 3D geometry of the altar (pillar, base, top)
     */
    private void renderBaseAltarModel(TileAltar altar, double x, double y, double z) {
        // Get the block and metadata
        int meta = altar.getBlockMetadata();
        net.minecraft.block.Block block = altar.getBlockType();

        if (!(block instanceof BlockAltar)) {
            return;
        }

        BlockAltar altarBlock = (BlockAltar) block;

        // Get icons for this altar tier
        IIcon iconBottom = altarBlock.getIcon(0, meta);  // bottom face
        IIcon iconTop = altarBlock.getIcon(1, meta);    // top face
        IIcon iconSide = altarBlock.getIcon(2, meta);   // side face

        if (iconBottom == null || iconTop == null || iconSide == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        Tessellator tessellator = Tessellator.instance;

        // Render all three components of the altar

        // Component 1: PILLAR (center column) - from [4, 2, 4] to [12, 9.5, 12]
        renderPillar(tessellator, 0.25, 0.125, 0.25, 0.75, 0.59375, 0.75, iconSide);

        // Component 2: BASE (bottom part) - from [2, 0, 2] to [14, 2, 14]
        renderBasePart(tessellator, 0.125, 0.0, 0.125, 0.875, 0.125, 0.875, iconBottom, iconSide);

        // Component 3: TOP (tabletop) - from [0, 9.5, 0] to [16, 15.5, 16]
        renderTopPart(tessellator, 0.0, 0.59375, 0.0, 1.0, 0.96875, 1.0, iconTop, iconSide, iconBottom);

        GL11.glPopMatrix();
    }

    /**
     * Render the center pillar
     */
    private void renderPillar(Tessellator t, double x1, double y1, double z1, double x2, double y2, double z2, IIcon icon) {
        t.startDrawingQuads();
        // North face
        t.setNormal(0, 0, -1);
        renderFace(t, x1, y1, z2, x2, y2, z2, icon, 0.25, 0.875, 0.75, 0.375);
        // South face
        t.setNormal(0, 0, 1);
        renderFace(t, x2, y1, z1, x1, y2, z1, icon, 0.25, 0.875, 0.75, 0.375);
        // West face
        t.setNormal(-1, 0, 0);
        renderFace(t, x1, y1, z1, x1, y2, z2, icon, 0.125, 0.875, 0.875, 0.375);
        // East face
        t.setNormal(1, 0, 0);
        renderFace(t, x2, y1, z2, x2, y2, z1, icon, 0.125, 0.875, 0.875, 0.375);
        t.draw();
    }

    /**
     * Render the base part
     */
    private void renderBasePart(Tessellator t, double x1, double y1, double z1, double x2, double y2, double z2, IIcon iconBottom, IIcon iconSide) {
        t.startDrawingQuads();
        // Bottom face
        t.setNormal(0, -1, 0);
        renderFace(t, x1, y1, z1, x2, y1, z2, iconBottom, 0.125, 0.875, 0.125, 0.875);
        // Top face
        t.setNormal(0, 1, 0);
        renderFace(t, x1, y2, z2, x2, y2, z1, iconBottom, 0.125, 0.875, 0.125, 0.875);
        // North
        t.setNormal(0, 0, -1);
        renderFace(t, x1, y1, z2, x2, y2, z2, iconSide, 0.125, 1.0, 0.875, 1.0);
        // South
        t.setNormal(0, 0, 1);
        renderFace(t, x2, y1, z1, x1, y2, z1, iconSide, 0.125, 1.0, 0.875, 1.0);
        // West
        t.setNormal(-1, 0, 0);
        renderFace(t, x1, y1, z1, x1, y2, z2, iconSide, 0.125, 1.0, 0.875, 1.0);
        // East
        t.setNormal(1, 0, 0);
        renderFace(t, x2, y1, z2, x2, y2, z1, iconSide, 0.125, 1.0, 0.875, 1.0);
        t.draw();
    }

    /**
     * Render the top part
     */
    private void renderTopPart(Tessellator t, double x1, double y1, double z1, double x2, double y2, double z2, IIcon iconTop, IIcon iconSide, IIcon iconBottom) {
        t.startDrawingQuads();
        // Top face (uses iconTop)
        t.setNormal(0, 1, 0);
        renderFace(t, x1, y2, z1, x2, y2, z2, iconTop, 0.0, 1.0, 0.0, 1.0);
        // Bottom face (uses iconBottom)
        t.setNormal(0, -1, 0);
        renderFace(t, x1, y1, z2, x2, y1, z1, iconBottom, 0.0, 1.0, 0.0, 1.0);
        // North
        t.setNormal(0, 0, -1);
        renderFace(t, x1, y1, z2, x2, y2, z2, iconSide, 0.0, 0.375, 1.0, 1.0);
        // South
        t.setNormal(0, 0, 1);
        renderFace(t, x2, y1, z1, x1, y2, z1, iconSide, 0.0, 0.375, 1.0, 1.0);
        // West
        t.setNormal(-1, 0, 0);
        renderFace(t, x1, y1, z1, x1, y2, z2, iconSide, 0.0, 0.375, 1.0, 1.0);
        // East
        t.setNormal(1, 0, 0);
        renderFace(t, x2, y1, z2, x2, y2, z1, iconSide, 0.0, 0.375, 1.0, 1.0);
        t.draw();
    }

    /**
     * Helper to render a face with UV coordinates
     */
    private void renderFace(Tessellator t, double x1, double y1, double z1, double x2, double y2, double z2, IIcon icon, double u1, double u2, double v1, double v2) {
        double minU = icon.getInterpolatedU(u1 * 16);
        double maxU = icon.getInterpolatedU(u2 * 16);
        double minV = icon.getInterpolatedV(v1 * 16);
        double maxV = icon.getInterpolatedV(v2 * 16);

        // Determine which face we're rendering and set vertices accordingly
        if (z1 == z2) { // X-Z plane (Y is varying)
            if (y1 == y2) { // Single Y value
                if (x1 == x2) { // Single X value too - edge case
                    t.addVertexWithUV(x1, y1, z1, minU, minV);
                    t.addVertexWithUV(x2, y2, z2, maxU, maxV);
                }
            }
        }

        // Standard quad rendering
        // This is a simplified version - for proper face orientation we need to check normal
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        double zDiff = z2 - z1;

        if (Math.abs(yDiff) < 0.001) { // Horizontal face (top/bottom)
            if (Math.abs(zDiff) > 0.001) { // X-Z plane with varying Z
                t.addVertexWithUV(x2, y1, z1, maxU, minV);
                t.addVertexWithUV(x1, y1, z1, minU, minV);
                t.addVertexWithUV(x1, y2, z2, minU, maxV);
                t.addVertexWithUV(x2, y2, z2, maxU, maxV);
            } else { // X-Z plane with varying X
                t.addVertexWithUV(x1, y1, z2, maxU, minV);
                t.addVertexWithUV(x1, y1, z1, minU, minV);
                t.addVertexWithUV(x2, y2, z1, minU, maxV);
                t.addVertexWithUV(x2, y2, z2, maxU, maxV);
            }
        } else if (Math.abs(xDiff) < 0.001) { // X face (west/east)
            t.addVertexWithUV(x1, y1, z1, maxU, minV);
            t.addVertexWithUV(x1, y1, z2, minU, minV);
            t.addVertexWithUV(x2, y2, z2, minU, maxV);
            t.addVertexWithUV(x2, y2, z1, maxU, maxV);
        } else { // Z face (north/south)
            t.addVertexWithUV(x2, y1, z1, maxU, minV);
            t.addVertexWithUV(x1, y1, z1, minU, minV);
            t.addVertexWithUV(x1, y2, z2, minU, maxV);
            t.addVertexWithUV(x2, y2, z2, maxU, maxV);
        }
    }

    /**
     * Render Discovery Altar (basic visualization)
     */
    private void renderDiscoveryAltar(TileAltar altar, double x, double y, double z, float partialTick) {
        if (!altar.getMultiblockState()) {
            return;
        }

        saveState();
        translateToCenter(x, y, z);

        // Enable additive blending for glow
        enableBlend();
        setAdditiveBlend();
        disableLighting();
        disableDepthMask();

        float time = getWorldTime(altar, partialTick);

        // Render rising starlight particles
        renderStarlightParticles(time, partialTick);

        // Render pulsing glow ring
        float pulse = (MathHelper.sin(time * 0.1F) * 0.5F + 0.5F) * 0.3F;
        renderGlowRing(0.6F, 0.8F, 1.0F, 0.3F + pulse, time);

        restoreState();
    }

    /**
     * Render starlight particles rising from altar
     */
    private void renderStarlightParticles(float time, float partialTick) {
        disableTexture();
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_QUADS);

        // Generate several particles
        for (int i = 0; i < 8; i++) {
            float offset = (i * 0.8F);
            float particleTime = (time * 0.5F + offset) % 2.0F; // 0 to 2 cycle

            if (particleTime < 0 || particleTime > 1.5F) continue;

            // Particle rises from altar
            float height = particleTime * 1.5F; // Rise up
            float size = 0.1F * (1.0F - particleTime * 0.5F); // Shrink as it rises
            float alpha = 0.8F * (1.0F - particleTime * 0.6F); // Fade out

            // Position around the altar
            float angle = i * (float) Math.PI * 2 / 8;
            float radius = 0.4F + particleTime * 0.2F;
            float px = (float) Math.cos(angle) * radius;
            float pz = (float) Math.sin(angle) * radius;

            // Color: light blue
            org.lwjgl.opengl.GL11.glColor4f(0.6F, 0.8F, 1.0F, alpha);

            // Render particle as quad
            float halfSize = size / 2;
            org.lwjgl.opengl.GL11.glVertex3f(px - halfSize, height, pz - halfSize);
            org.lwjgl.opengl.GL11.glVertex3f(px + halfSize, height, pz - halfSize);
            org.lwjgl.opengl.GL11.glVertex3f(px + halfSize, height, pz + halfSize);
            org.lwjgl.opengl.GL11.glVertex3f(px - halfSize, height, pz + halfSize);
        }

        org.lwjgl.opengl.GL11.glEnd();
        enableTexture();
    }

    /**
     * Render glowing ring effect
     */
    private void renderGlowRing(float r, float g, float b, float alpha, float time) {
        disableTexture();
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_LINE_LOOP);

        setColor4f(r, g, b, alpha);

        float radius = 0.5F + (float) Math.sin(time * 0.05F) * 0.1F;
        int segments = 32;

        for (int i = 0; i < segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            org.lwjgl.opengl.GL11.glVertex3f(x, 0.1F, z);
        }

        org.lwjgl.opengl.GL11.glEnd();
        enableTexture();
    }

    /**
     * Render Attunement Altar
     */
    private void renderAttunementAltar(TileAltar altar, double x, double y, double z, float partialTick) {
        if (!altar.getMultiblockState()) {
            return;
        }

        saveState();
        translateToCenter(x, y, z);

        enableBlend();
        setAdditiveBlend();
        disableLighting();
        disableDepthMask();

        float time = getWorldTime(altar, partialTick);

        // Multiple rotating rings for attunement altar
        for (int i = 0; i < 3; i++) {
            float ringTime = time * 0.05F * (i + 1);
            float ringRadius = 0.3F + i * 0.15F;
            float pulse = (MathHelper.sin(time * 0.15F + i) * 0.5F + 0.5F) * 0.4F;

            rotate(ringTime * 50, 0, 1, 0); // Rotate around Y axis
            renderGlowRing(0.4F, 0.6F, 1.0F, 0.4F + pulse - i * 0.1F, time);

            // Reset rotation for next ring
            rotate(-(ringTime * 50), 0, 1, 0);
        }

        // Center glow
        renderCenterGlow(0.4F, 0.6F, 1.0F, 0.6F, time);

        restoreState();
    }

    /**
     * Render Constellation Craft Altar
     */
    private void renderConstellationCraftAltar(TileAltar altar, double x, double y, double z, float partialTick) {
        if (!altar.getMultiblockState()) {
            return;
        }

        saveState();
        translateToCenter(x, y, z);

        enableBlend();
        setAdditiveBlend();
        disableLighting();
        disableDepthMask();

        float time = getWorldTime(altar, partialTick);
        float pulse = (MathHelper.sin(time * 0.2F) * 0.5F + 0.5F) * 0.5F;

        // Render constellation pattern flat on altar
        // Get active constellation from recipe (if any)
        hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask task = altar.getActiveCraftingTask();
        if (task != null) {
            hellfirepvp.astralsorcery.common.crafting.altar.ASAltarRecipe recipe = task.getRecipe();
            if (recipe != null) {
                String constellationName = recipe.getConstellation();
                if (constellationName != null && !constellationName.isEmpty()) {
                    hellfirepvp.astralsorcery.common.constellation.IConstellation constellation =
                        hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry.getConstellationByName(constellationName);
                    if (constellation != null) {
                        // Render constellation flat on altar surface
                        renderConstellationFlat(constellation, 0.8F, 0.6F, 1.0F, 0.5F + pulse, time);
                    }
                }
            }
        }

        // Outer glow ring
        renderGlowRing(0.8F, 0.6F, 1.0F, 0.5F + pulse, time);

        restoreState();
    }

    /**
     * Render constellation flat on altar surface
     */
    private void renderConstellationFlat(hellfirepvp.astralsorcery.common.constellation.IConstellation constellation,
                                          float r, float g, float b, float alpha, float time) {
        // Translate to altar top surface
        translate(0, 0.1F, 0);

        // Rotate 90 degrees to lay flat
        rotate(90, 1, 0, 0);

        disableTexture();
        org.lwjgl.opengl.GL11.glLineWidth(2F);

        // Render connections
        setColor4f(r, g, b, alpha * 0.8F);

        for (hellfirepvp.astralsorcery.common.constellation.star.StarConnection conn : constellation.getStarConnections()) {
            hellfirepvp.astralsorcery.common.constellation.star.StarLocation s1 = conn.from;
            hellfirepvp.astralsorcery.common.constellation.star.StarLocation s2 = conn.to;

            if (s1 != null && s2 != null) {
                // Map 31x31 grid to world coordinates (scale down to 0.5 block size)
                float scale = 0.5F / 30F;
                float x1 = (s1.x - 15F) * scale;
                float y1 = (s1.y - 15F) * scale;
                float x2 = (s2.x - 15F) * scale;
                float y2 = (s2.y - 15F) * scale;

                org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_LINES);
                org.lwjgl.opengl.GL11.glVertex3f(x1, y1, 0);
                org.lwjgl.opengl.GL11.glVertex3f(x2, y2, 0);
                org.lwjgl.opengl.GL11.glEnd();
            }
        }

        // Render stars
        setColor4f(r, g, b, alpha);
        for (hellfirepvp.astralsorcery.common.constellation.star.StarLocation star : constellation.getStars()) {
            float sx = (star.x - 15F) * (0.5F / 30F);
            float sy = (star.y - 15F) * (0.5F / 30F);
            float starSize = 0.03F;

            org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_QUADS);
            org.lwjgl.opengl.GL11.glVertex3f(sx - starSize, sy - starSize, 0);
            org.lwjgl.opengl.GL11.glVertex3f(sx + starSize, sy - starSize, 0);
            org.lwjgl.opengl.GL11.glVertex3f(sx + starSize, sy + starSize, 0);
            org.lwjgl.opengl.GL11.glVertex3f(sx - starSize, sy + starSize, 0);
            org.lwjgl.opengl.GL11.glEnd();
        }

        enableTexture();

        // Reset rotation
        rotate(-90, 1, 0, 0);
        translate(0, -0.1F, 0);
    }

    /**
     * Render center glow effect
     */
    private void renderCenterGlow(float r, float g, float b, float alpha, float time) {
        disableTexture();

        // Pulsing center
        float pulse = (MathHelper.sin(time * 0.1F) * 0.5F + 0.5F);
        float size = 0.2F + pulse * 0.1F;

        setColor4f(r, g, b, alpha);
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_QUADS);
        org.lwjgl.opengl.GL11.glVertex3f(-size, 0.2F, -size);
        org.lwjgl.opengl.GL11.glVertex3f(size, 0.2F, -size);
        org.lwjgl.opengl.GL11.glVertex3f(size, 0.2F, size);
        org.lwjgl.opengl.GL11.glVertex3f(-size, 0.2F, size);
        org.lwjgl.opengl.GL11.glEnd();

        enableTexture();
    }

    /**
     * Render Trait Craft Altar (most complex)
     */
    private void renderTraitCraftAltar(TileAltar altar, double x, double y, double z, float partialTick) {
        if (!altar.getMultiblockState()) {
            return;
        }

        saveState();
        translateToCenter(x, y, z);

        enableBlend();
        setAdditiveBlend();
        disableLighting();
        disableDepthMask();

        float time = getWorldTime(altar, partialTick);

        // Render complex multi-ring system
        for (int i = 0; i < 4; i++) {
            float ringTime = time * 0.03F * (i + 1);
            float ringRadius = 0.25F + i * 0.12F;
            float pulse = (MathHelper.sin(time * 0.1F + i * 0.5F) * 0.5F + 0.5F) * 0.5F;

            // Alternate rotation directions
            float direction = (i % 2 == 0) ? 1 : -1;
            rotate(ringTime * 40 * direction, 0, 1, 0);

            // Different colors for different rings
            float r = 0.8F - i * 0.1F;
            float g = 0.6F - i * 0.05F;
            float b = 1.0F;
            renderGlowRing(r, g, b, 0.3F + pulse, time);

            rotate(-(ringTime * 40 * direction), 0, 1, 0);
        }

        // Floating crystal indicator
        renderFloatingCrystal(0.8F, 0.7F, 1.0F, 0.8F, time);

        // Center constellation glow
        hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask task = altar.getActiveCraftingTask();
        if (task != null) {
            hellfirepvp.astralsorcery.common.crafting.altar.ASAltarRecipe recipe = task.getRecipe();
            if (recipe != null) {
                String constellationName = recipe.getConstellation();
                if (constellationName != null && !constellationName.isEmpty()) {
                    hellfirepvp.astralsorcery.common.constellation.IConstellation constellation =
                        hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry.getConstellationByName(constellationName);
                    if (constellation != null) {
                        renderConstellationFlat(constellation, 0.8F, 0.7F, 1.0F, 0.6F, time);
                    }
                }
            }
        }

        restoreState();

        // Render light rays to trait items
        // TODO: Implement after trait item positions are available
        // renderLightRaysToTraits(altar, x, y, z, partialTick);
    }

    /**
     * Render floating crystal above altar
     */
    private void renderFloatingCrystal(float r, float g, float b, float alpha, float time) {
        // Crystal floats above altar and rotates
        translate(0, 1.0F + (float) Math.sin(time * 0.05F) * 0.1F, 0); // Bobbing motion
        rotate(time * 2, 0, 1, 0); // Rotation

        disableTexture();

        // Draw crystal as diamond shape
        setColor4f(r, g, b, alpha);
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_TRIANGLES);

        float height = 0.3F;
        float width = 0.1F;

        // 4 triangular faces
        for (int i = 0; i < 4; i++) {
            float angle = (float) (i * Math.PI / 2);
            float x1 = (float) Math.cos(angle) * width;
            float z1 = (float) Math.sin(angle) * width;
            float x2 = (float) Math.cos(angle + Math.PI / 2) * width;
            float z2 = (float) Math.sin(angle + Math.PI / 2) * width;

            // Top to bottom face
            org.lwjgl.opengl.GL11.glVertex3f(0, height / 2, 0);
            org.lwjgl.opengl.GL11.glVertex3f(x1, -height / 2, z1);
            org.lwjgl.opengl.GL11.glVertex3f(x2, -height / 2, z2);
        }

        org.lwjgl.opengl.GL11.glEnd();
        enableTexture();

        rotate(-time * 2, 0, 1, 0);
        translate(0, -1.0F - (float) Math.sin(time * 0.05F) * 0.1F, 0);
    }

    /**
     * Render active crafting task
     */
    private void renderActiveCrafting(TileAltar altar, double x, double y, double z, float partialTick,
        ActiveCraftingTask task) {
        saveState();
        translateToCenter(x, y, z);

        enableBlend();
        setAdditiveBlend();
        disableLighting();
        disableDepthMask();

        float time = getWorldTime(altar, partialTick);

        // Calculate crafting progress (0.0 to 1.0)
        float progress = task.getProgress();
        float maxProgress = task.getRecipe() != null ? task.getRecipe().getCraftingTime() : 100F;
        float progressRatio = progress / maxProgress;

        // Render progress ring
        renderProgressRing(1.0F, 0.9F, 0.6F, progressRatio, time);

        // Render floating item being crafted (above altar)
        renderFloatingItem(task.getRecipe().getOutput(), time);

        restoreState();
    }

    /**
     * Render progress ring
     */
    private void renderProgressRing(float r, float g, float b, float progress, float time) {
        disableTexture();

        float radius = 0.4F;
        float thickness = 0.05F;
        int segments = 64;

        // Background ring (dim)
        setColor4f(r, g, b, 0.2F);
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);

            org.lwjgl.opengl.GL11.glVertex3f(cos * radius, 1.6F, sin * radius);
            org.lwjgl.opengl.GL11.glVertex3f(cos * (radius + thickness), 1.6F, sin * (radius + thickness));
        }
        org.lwjgl.opengl.GL11.glEnd();

        // Progress ring (bright, rotates)
        setColor4f(r, g, b, 0.8F);
        rotate(time * 10, 0, 1, 0); // Slow rotation

        int progressSegments = (int) (segments * progress);
        if (progressSegments > 0) {
            org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_QUAD_STRIP);
            for (int i = 0; i <= progressSegments; i++) {
                float angle = (float) (i * 2 * Math.PI / segments);
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);

                org.lwjgl.opengl.GL11.glVertex3f(cos * radius, 1.6F, sin * radius);
                org.lwjgl.opengl.GL11.glVertex3f(cos * (radius + thickness), 1.6F, sin * (radius + thickness));
            }
            org.lwjgl.opengl.GL11.glEnd();
        }

        rotate(-time * 10, 0, 1, 0);
        enableTexture();
    }

    /**
     * Render floating item above altar
     */
    private void renderFloatingItem(net.minecraft.item.ItemStack item, float time) {
        if (item == null) return;

        saveState();

        // Item floats and rotates above altar
        translate(0, 2.0F + (float) Math.sin(time * 0.08F) * 0.1F, 0);
        rotate(time * 1.5F, 0, 1, 0);

        // Render item - simplified for 1.7.10
        // Render simple colored quad representing the item
        disableTexture();
        setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_QUADS);
        float size = 0.2F;
        org.lwjgl.opengl.GL11.glVertex3f(-size, 0, -size);
        org.lwjgl.opengl.GL11.glVertex3f(size, 0, -size);
        org.lwjgl.opengl.GL11.glVertex3f(size, size * 2, -size);
        org.lwjgl.opengl.GL11.glVertex3f(-size, size * 2, -size);
        org.lwjgl.opengl.GL11.glEnd();
        enableTexture();

        restoreState();
    }

    // ========== Utility Methods ==========

    /**
     * Get world time for animation
     */
    private float getWorldTime(TileAltar altar, float partialTick) {
        return altar.getWorldObj()
            .getTotalWorldTime() + partialTick;
    }

    /**
     * Math helper for 1.7.10
     */
    private static class MathHelper {

        public static float sin(float value) {
            return (float) Math.sin(value);
        }

        public static float cos(float value) {
            return (float) Math.cos(value);
        }
    }
}
