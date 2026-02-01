/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityCrystal;

/**
 * RenderEntityCrystal - Crystal entity renderer (1.7.10)
 * <p>
 * Renders the EntityCrystal as a 3D crystal shape with rotation and highlight effects.
 * The color is based on the crystal's properties (size, purity, etc.).
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>3D octahedron crystal geometry rendered with Tessellator</li>
 * <li>Color based on crystal type and highlight color</li>
 * <li>Slow rotation animation</li>
 * <li>Translucent rendering with depth sorting</li>
 * <li>Particle effects spawned by entity itself</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses {@link net.minecraft.client.renderer.Tessellator} for geometry</li>
 * <li>Implements {@link #doRender(Entity, double, double, double, float, float)}</li>
 * <li>Implements {@link #getEntityTexture(Entity)}</li>
 * </ul>
 */
public class RenderEntityCrystal extends Render {

    /**
     * Default crystal color (cyan/blue tint)
     */
    private static final float DEFAULT_CRYSTAL_COLOR[] = { 0.3F, 0.8F, 1.0F };

    /**
     * Celestial crystal color (purple/pink tint)
     */
    private static final float CELESTIAL_CRYSTAL_COLOR[] = { 0.8F, 0.3F, 1.0F };

    /**
     * Rock crystal color (green/teal tint)
     */
    private static final float ROCK_CRYSTAL_COLOR[] = { 0.2F, 0.9F, 0.6F };

    /**
     * Rotation speed for crystal animation
     */
    private static final float ROTATION_SPEED = 1.5F;

    /**
     * Create a new RenderEntityCrystal
     */
    public RenderEntityCrystal() {
        this.shadowSize = 0.0F; // Floating entities don't cast shadows
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityCrystal)) {
            return;
        }

        EntityCrystal crystal = (EntityCrystal) entity;

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable blending for translucent crystal
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Disable lighting for self-illuminated crystal
        GL11.glDisable(GL11.GL_LIGHTING);

        // Move to crystal position
        GL11.glTranslated(x, y, z);

        // Apply rotation
        float rotation = (crystal.ticksExisted + partialTicks) * ROTATION_SPEED;
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F); // Rotate around Y axis
        GL11.glRotatef(rotation * 0.3F, 1.0F, 0.0F, 0.0F); // Slight X rotation

        // Get crystal color
        float[] color = getCrystalColor(crystal);

        // Render the crystal geometry
        renderOctahedronCrystal(0.3F, color, 0.8F);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        // Note: Particle effects are spawned by EntityCrystal itself in spawnCraftingParticles()
    }

    /**
     * Render a crystal as an octahedron (8-faced diamond shape)
     *
     * @param size  Size of the crystal
     * @param color RGB color array (values 0.0 to 1.0)
     * @param alpha Alpha transparency (0.0 to 1.0)
     */
    private void renderOctahedronCrystal(float size, float[] color, float alpha) {
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.instance;

        // Render outer shell (more opaque)
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        tessellator.setColorRGBA_F(color[0], color[1], color[2], alpha * 0.6F);

        // Octahedron has 8 triangular faces
        // Top pyramid (4 faces)
        addTriangle(tessellator, 0, size, 0, size, 0, 0, 0, 0, size);
        addTriangle(tessellator, 0, size, 0, 0, 0, size, -size, 0, 0);
        addTriangle(tessellator, 0, size, 0, -size, 0, 0, 0, 0, -size);
        addTriangle(tessellator, 0, size, 0, 0, 0, -size, size, 0, 0);

        // Bottom pyramid (4 faces)
        addTriangle(tessellator, 0, -size, 0, 0, 0, size, size, 0, 0);
        addTriangle(tessellator, 0, -size, 0, -size, 0, 0, 0, 0, size);
        addTriangle(tessellator, 0, -size, 0, 0, 0, -size, -size, 0, 0);
        addTriangle(tessellator, 0, -size, 0, size, 0, 0, 0, 0, -size);

        tessellator.draw();

        // Render inner core (brighter, smaller)
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        tessellator.setColorRGBA_F(color[0] * 1.2F, color[1] * 1.2F, color[2] * 1.2F, alpha * 0.9F);

        float innerSize = size * 0.5F;

        // Top pyramid
        addTriangle(tessellator, 0, innerSize, 0, innerSize, 0, 0, 0, 0, innerSize);
        addTriangle(tessellator, 0, innerSize, 0, 0, 0, innerSize, -innerSize, 0, 0);
        addTriangle(tessellator, 0, innerSize, 0, -innerSize, 0, 0, 0, 0, -innerSize);
        addTriangle(tessellator, 0, innerSize, 0, 0, 0, -innerSize, innerSize, 0, 0);

        // Bottom pyramid
        addTriangle(tessellator, 0, -innerSize, 0, 0, 0, innerSize, innerSize, 0, 0);
        addTriangle(tessellator, 0, -innerSize, 0, -innerSize, 0, 0, 0, 0, innerSize);
        addTriangle(tessellator, 0, -innerSize, 0, 0, 0, -innerSize, -innerSize, 0, 0);
        addTriangle(tessellator, 0, -innerSize, 0, innerSize, 0, 0, 0, 0, -innerSize);

        tessellator.draw();
    }

    /**
     * Add a triangle to the Tessellator
     * Uses vertex normal calculation for proper lighting
     *
     * @param t   Tessellator instance
     * @param x1, y1, z1 First vertex
     * @param x2, y2, z2 Second vertex
     * @param x3, y3, z3 Third vertex
     */
    private void addTriangle(net.minecraft.client.renderer.Tessellator t, float x1, float y1, float z1, float x2,
        float y2, float z2, float x3, float y3, float z3) {
        // Calculate face normal for lighting
        float ux = x2 - x1;
        float uy = y2 - y1;
        float uz = z2 - z1;
        float vx = x3 - x1;
        float vy = y3 - y1;
        float vz = z3 - z1;

        // Cross product for normal
        float nx = uy * vz - uz * vy;
        float ny = uz * vx - ux * vz;
        float nz = ux * vy - uy * vx;

        // Normalize
        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0) {
            nx /= length;
            ny /= length;
            nz /= length;
        }

        // Add vertices with normals
        t.setNormal(nx, ny, nz);
        t.addVertex(x1, y1, z1);
        t.addVertex(x2, y2, z2);
        t.addVertex(x3, y3, z3);
    }

    /**
     * Get the color for the crystal based on its properties
     *
     * @param crystal The crystal entity
     * @return RGB color array (values 0.0 to 1.0)
     */
    private float[] getCrystalColor(EntityCrystal crystal) {
        ItemStack stack = crystal.getEntityItem();

        if (stack == null || stack.getItem() == null) {
            return DEFAULT_CRYSTAL_COLOR;
        }

        // Get highlight color from crystal if available
        try {
            java.awt.Color highlightColor = crystal.getHighlightColor();
            if (highlightColor != null) {
                return new float[] { highlightColor.getRed() / 255.0F, highlightColor.getGreen() / 255.0F,
                    highlightColor.getBlue() / 255.0F };
            }
        } catch (Exception e) {
            // Highlight color not available, use defaults
        }

        // Determine color based on crystal type
        String itemName = stack.getItem()
            .getUnlocalizedName();

        if (itemName != null) {
            if (itemName.contains("celestial")) {
                return CELESTIAL_CRYSTAL_COLOR;
            } else if (itemName.contains("rock")) {
                return ROCK_CRYSTAL_COLOR;
            }
        }

        return DEFAULT_CRYSTAL_COLOR;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // Crystals use procedural rendering, but we return a default texture
        // in case fallback rendering is needed
        return ResourceLocationRegister.getEntityCrystal();
    }

    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render crystals if they're within normal render distance
        return true;
    }
}
