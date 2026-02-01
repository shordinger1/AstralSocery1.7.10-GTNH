/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntityCrystalTool - Crystal tool renderer with growth effects
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import java.awt.Color;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntityCrystalTool;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;

/**
 * RenderEntityCrystalTool - Crystal tool renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Renders tool as floating item</li>
 * <li>Glowing crystal effect based on properties</li>
 * <li>Rotating animation</li>
 * <li>Growth progress visualization</li>
 * <li>Color based on crystal type</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses RenderItem for item rendering</li>
 * <li>GL11 for OpenGL operations</li>
 * </ul>
 */
public class RenderEntityCrystalTool extends Render {

    /**
     * Default crystal color - cyan
     */
    private static final Color DEFAULT_CRYSTAL_COLOR = new Color(0x00FFFF);

    /**
     * Celestial crystal color - purple
     */
    private static final Color CELESTIAL_CRYSTAL_COLOR = new Color(0xAA00FF);

    /**
     * Rock crystal color - green
     */
    private static final Color ROCK_CRYSTAL_COLOR = new Color(0x00FF88);

    /**
     * Base rotation speed
     */
    private static final float ROTATION_SPEED = 2.0F;

    /**
     * Bob animation speed
     */
    private static final float BOB_SPEED = 0.08F;

    public RenderEntityCrystalTool() {
        this.shadowSize = 0.3F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntityCrystalTool)) {
            return;
        }

        EntityCrystalTool tool = (EntityCrystalTool) entity;
        ItemStack item = tool.getEntityItem();

        if (item == null || item.stackSize <= 0) {
            return;
        }

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable blending for glow effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Move to entity position
        GL11.glTranslated(x, y, z);

        // Calculate bob animation
        float bob = (float) Math.sin((tool.age + partialTicks) * BOB_SPEED) * 0.1F;
        GL11.glTranslatef(0.0F, bob, 0.0F);

        // Rotate tool
        float rotation = (tool.age + partialTicks) * ROTATION_SPEED;
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F); // Slight tilt

        // Get crystal color
        Color crystalColor = getCrystalColor(tool);

        // Get growth progress
        float growthProgress = getGrowthProgress(tool);

        // Render glow based on growth
        renderGlow(growthProgress, crystalColor, partialTicks);

        // Render the item itself
        renderItem(tool, item, crystalColor, partialTicks);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Get color for the crystal tool
     */
    private Color getCrystalColor(EntityCrystalTool tool) {
        ItemStack item = tool.getEntityItem();
        if (item == null || item.stackSize <= 0) {
            return DEFAULT_CRYSTAL_COLOR;
        }

        // Try to get crystal properties
        CrystalProperties props = tool.getProperties();
        if (props != null) {
            // Use crystal properties to determine color
            // For now, return default based on crystal type
            return DEFAULT_CRYSTAL_COLOR;
        }

        return DEFAULT_CRYSTAL_COLOR;
    }

    /**
     * Get growth progress (0.0 to 1.0)
     */
    private float getGrowthProgress(EntityCrystalTool tool) {
        int mergeTick = tool.getInertMergeTick();
        return Math.min(1.0F, mergeTick / (float) EntityCrystalTool.TOTAL_MERGE_TIME);
    }

    /**
     * Render crystal glow based on growth progress
     */
    private void renderGlow(float growthProgress, Color color, float partialTicks) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        float pulse = (float) Math.sin((System.currentTimeMillis() / 1000.0F + partialTicks) * Math.PI * 2) * 0.5F
            + 0.5F;
        float alpha = (0.2F + growthProgress * 0.3F) * (0.8F + pulse * 0.2F);

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        // Render glow layers
        for (int i = 0; i < 2; i++) {
            float scale = 1.2F + i * 0.4F + growthProgress * 0.3F;
            float layerAlpha = alpha / (i + 1);

            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, scale);

            tess.startDrawing(GL11.GL_QUADS);
            tess.setColorRGBA_F(r, g, b, layerAlpha);

            float size = 0.5F;
            float hs = size / 2.0F;

            // Render faces
            tess.addVertex(-hs, -hs, hs);
            tess.addVertex(hs, -hs, hs);
            tess.addVertex(hs, hs, hs);
            tess.addVertex(-hs, hs, hs);

            tess.addVertex(-hs, hs, -hs);
            tess.addVertex(hs, hs, -hs);
            tess.addVertex(hs, -hs, -hs);
            tess.addVertex(-hs, -hs, -hs);

            tess.addVertex(-hs, -hs, -hs);
            tess.addVertex(-hs, -hs, hs);
            tess.addVertex(-hs, hs, hs);
            tess.addVertex(-hs, hs, -hs);

            tess.addVertex(hs, -hs, hs);
            tess.addVertex(hs, -hs, -hs);
            tess.addVertex(hs, hs, -hs);
            tess.addVertex(hs, hs, hs);

            tess.addVertex(-hs, hs, hs);
            tess.addVertex(hs, hs, hs);
            tess.addVertex(hs, hs, -hs);
            tess.addVertex(-hs, hs, -hs);

            tess.addVertex(-hs, -hs, -hs);
            tess.addVertex(hs, -hs, -hs);
            tess.addVertex(hs, -hs, hs);
            tess.addVertex(-hs, -hs, hs);

            tess.draw();
            GL11.glPopMatrix();
        }
    }

    /**
     * Render the item itself
     */
    private void renderItem(EntityCrystalTool tool, ItemStack item, Color color, float partialTicks) {
        // Use RenderItem to render the item
        RenderItem renderItem = new RenderItem();

        // Set color tint
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);

        // Render item
        renderItem.doRender(tool, 0, 0, 0, 0, 0);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // RenderItem will handle texture binding
        if (entity instanceof EntityCrystalTool) {
            EntityCrystalTool tool = (EntityCrystalTool) entity;
            ItemStack item = tool.getEntityItem();
            if (item != null && item.getItem() != null) {
                // Return item's texture
                String itemName = item.getItem()
                    .getUnlocalizedName();
                return new ResourceLocation("astralsorcery:textures/items/" + itemName + ".png");
            }
        }
        // Fallback texture
        return ResourceLocationRegister.getEntityCrystalTool();
    }

    /**
     * Check if this renderer should render the entity
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render crystal tools if they're within normal render distance
        return true;
    }
}
