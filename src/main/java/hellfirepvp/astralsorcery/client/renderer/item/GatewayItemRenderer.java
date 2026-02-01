/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Celestial Gateway Item Renderer
 * Handles rendering of Gateway item in inventory, hand, and as entity
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.common.block.BlockCelestialGateway;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * ItemRenderer for Celestial Gateway
 * <p>
 * Based on Twist-Space-Technology-Mod's PowerChairRenderer
 * Renders the Gateway OBJ model in:
 * - INVENTORY (creative/survival inventory)
 * - EQUIPPED (third-person)
 * - EQUIPPED_FIRST_PERSON (first-person)
 * - ENTITY (dropped item)
 */
public class GatewayItemRenderer implements IItemRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    public GatewayItemRenderer(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;
        LogHelper.info("[GatewayItemRenderer] Initialized with model and texture: " + texture);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item == null || item.getItem() == null) {
            return false;
        }

        // Check if this is a Celestial Gateway block
        if (item.getItem() instanceof net.minecraft.item.ItemBlock) {
            net.minecraft.item.ItemBlock itemBlock = (net.minecraft.item.ItemBlock) item.getItem();
            if (itemBlock.field_150939_a instanceof BlockCelestialGateway) {
                return true; // Handle all render types for gateway
            }
        }

        return false;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (item == null || item.getItem() == null) {
            return;
        }

        // Verify this is a Celestial Gateway
        if (!(item.getItem() instanceof net.minecraft.item.ItemBlock)) {
            return;
        }

        net.minecraft.item.ItemBlock itemBlock = (net.minecraft.item.ItemBlock) item.getItem();
        if (!(itemBlock.field_150939_a instanceof BlockCelestialGateway)) {
            return;
        }

        // Apply transformations based on render type
        switch (type) {
            case ENTITY -> {
                // Dropped item on ground
                GL11.glScaled(0.5, 0.5, 0.5); // Scale down
                GL11.glTranslated(0.0, 0.5, 0.0); // Lift up
                break;
            }
            case EQUIPPED -> {
                // Third-person view (on armor stand or player)
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glTranslated(1.0, 0.5, 1.0);
                GL11.glRotated(90, 0, 1, 0); // Rotate for better visibility
                break;
            }
            case EQUIPPED_FIRST_PERSON -> {
                // First-person view (in hand)
                GL11.glScaled(0.3, 0.3, 0.3); // Smaller scale
                GL11.glTranslated(-0.5, 0.5, 0.5);
                GL11.glRotated(90, 0, 1, 0);
                break;
            }
            case INVENTORY -> {
                // In GUI (creative/survival inventory)
                GL11.glTranslated(0.0, -0.1, 0.0); // Center
                GL11.glScaled(0.4, 0.4, 0.4); // Scale for inventory
                GL11.glRotated(180, 0, 1, 0); // Rotate to face forward
                GL11.glRotated(15, 1, 0, 0); // Slight tilt
                break;
            }
            default -> {
                // Unknown render type - use default
                GL11.glScaled(0.4, 0.4, 0.4);
                break;
            }
        }

        // Render the model
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); // Full opacity

        // Bind texture
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);

        // Render the model
        if (this.model != null) {
            this.model.renderAll();
        } else {
            LogHelper.warn("[GatewayItemRenderer] Model is null, cannot render!");
        }

        GL11.glPopMatrix();
    }
}
