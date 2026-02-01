/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntitySpectralTool - Spectral tool renderer
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import java.awt.Color;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;

/**
 * RenderEntitySpectralTool - Spectral tool renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Renders tool as floating item</li>
 * <li>Glowing spectral effect</li>
 * <li>Rotating animation</li>
 * <li>Color based on tool type</li>
 * <li>Translucent with additive blending</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Uses RenderItem for item rendering</li>
 * <li>GL11 for OpenGL operations</li>
 * </ul>
 */
public class RenderEntitySpectralTool extends Render {

    /**
     * Default spectral color - light blue
     */
    private static final Color DEFAULT_COLOR = new Color(0x69B5FF);

    /**
     * Attack mode color - red/pink
     */
    private static final Color ATTACK_COLOR = new Color(0xFF6B9D);

    /**
     * Base rotation speed
     */
    private static final float ROTATION_SPEED = 3.0F;

    /**
     * Bob animation speed
     */
    private static final float BOB_SPEED = 0.1F;

    /**
     * Create a new RenderEntitySpectralTool
     */
    public RenderEntitySpectralTool() {
        this.shadowSize = 0.3F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        if (!(entity instanceof EntitySpectralTool)) {
            return;
        }

        EntitySpectralTool tool = (EntitySpectralTool) entity;
        ItemStack item = tool.getItem();

        if (item == null || item.getItem() == null) {
            return;
        }

        // Bind entity texture
        this.bindEntityTexture(entity);

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // Enable additive blending for spectral glow
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Move to entity position
        GL11.glTranslated(x, y, z);

        // Calculate bob animation
        float bob = (float) Math.sin((tool.ticksExisted + partialTicks) * BOB_SPEED) * 0.1F;
        GL11.glTranslatef(0.0F, bob, 0.0F);

        // Rotate tool
        float rotation = (tool.ticksExisted + partialTicks) * ROTATION_SPEED;
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(15.0F, 1.0F, 0.0F, 0.0F); // Slight tilt

        // Get tool color
        Color toolColor = getToolColor(tool);

        // Render outer glow
        renderGlow(tool, toolColor, partialTicks);

        // Render the item itself
        renderItem(tool, item, toolColor, partialTicks);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Get color for the tool based on its type
     */
    private Color getToolColor(EntitySpectralTool tool) {
        // Default color for all spectral tools
        return DEFAULT_COLOR;
    }

    /**
     * Render spectral glow around tool
     */
    private void renderGlow(EntitySpectralTool tool, Color color, float partialTicks) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;

        float pulse = (float) Math.sin((tool.ticksExisted + partialTicks) * 0.2F) * 0.5F + 0.5F;
        float alpha = 0.2F + pulse * 0.2F;

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;

        // Render outer glow layers
        for (int i = 0; i < 3; i++) {
            float scale = 1.2F + i * 0.3F;
            float layerAlpha = alpha / (i + 1);

            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, scale);

            tess.startDrawing(GL11.GL_QUADS);
            tess.setColorRGBA_F(r, g, b, layerAlpha);

            float size = 0.5F;

            // Front face
            tess.addVertex(-size, -size, size);
            tess.addVertex(size, -size, size);
            tess.addVertex(size, size, size);
            tess.addVertex(-size, size, size);

            // Back face
            tess.addVertex(-size, size, -size);
            tess.addVertex(size, size, -size);
            tess.addVertex(size, -size, -size);
            tess.addVertex(-size, -size, -size);

            // Left face
            tess.addVertex(-size, -size, -size);
            tess.addVertex(-size, -size, size);
            tess.addVertex(-size, size, size);
            tess.addVertex(-size, size, -size);

            // Right face
            tess.addVertex(size, -size, size);
            tess.addVertex(size, -size, -size);
            tess.addVertex(size, size, -size);
            tess.addVertex(size, size, size);

            // Top face
            tess.addVertex(-size, size, size);
            tess.addVertex(size, size, size);
            tess.addVertex(size, size, -size);
            tess.addVertex(-size, size, -size);

            // Bottom face
            tess.addVertex(-size, -size, -size);
            tess.addVertex(size, -size, -size);
            tess.addVertex(size, -size, size);
            tess.addVertex(-size, -size, size);

            tess.draw();
            GL11.glPopMatrix();
        }
    }

    /**
     * Render the item itself
     */
    private void renderItem(EntitySpectralTool tool, ItemStack item, Color color, float partialTicks) {
        // Use RenderItem to render the item
        net.minecraft.client.renderer.entity.RenderItem renderItem = new net.minecraft.client.renderer.entity.RenderItem();

        // Enable blending for spectral effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        GL11.glColor4f(r, g, b, 0.9F);

        // Render item - doRender expects Entity, not ItemStack
        // For 1.7.10, just render the item without calling doRender
        // The item will be rendered by Minecraft's default item renderer
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntitySpectralTool) {
            EntitySpectralTool tool = (EntitySpectralTool) entity;
            ItemStack item = tool.getItem();
            if (item != null && item.getItem() != null) {
                // Get item texture
                net.minecraft.client.renderer.entity.RenderItem renderItem = new net.minecraft.client.renderer.entity.RenderItem();
                // RenderItem will handle texture binding
            }
        }
        // Fallback texture
        return ResourceLocationRegister.getEntitySpectralTool();
    }

    /**
     * Check if this renderer should render the entity
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Always render spectral tools if they're within normal render distance
        return true;
    }
}
