/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crafting Altar Item Renderer - Handles all 4 tiers
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.common.block.BlockAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * ItemRenderer for Crafting Altar (all tiers)
 * <p>
 * Supports 4 altar tiers with different models
 */
public class CraftingAltarItemRenderer implements IItemRenderer {

    private final IModelCustom modelTier1;
    private final IModelCustom modelTier2;
    private final IModelCustom modelTier3;
    private final IModelCustom modelTier4;
    private final ResourceLocation texture;

    public CraftingAltarItemRenderer(IModelCustom modelTier1, IModelCustom modelTier2, IModelCustom modelTier3,
        IModelCustom modelTier4, ResourceLocation texture) {
        this.modelTier1 = modelTier1;
        this.modelTier2 = modelTier2;
        this.modelTier3 = modelTier3;
        this.modelTier4 = modelTier4;
        this.texture = texture;
        LogHelper.info("[CraftingAltarItemRenderer] Initialized with 4 tier models and texture: " + texture);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item == null || item.getItem() == null) {
            return false;
        }

        if (item.getItem() instanceof net.minecraft.item.ItemBlock) {
            net.minecraft.item.ItemBlock itemBlock = (net.minecraft.item.ItemBlock) item.getItem();
            if (itemBlock.field_150939_a instanceof BlockAltar) {
                return true;
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

        if (!(item.getItem() instanceof net.minecraft.item.ItemBlock)) {
            return;
        }

        net.minecraft.item.ItemBlock itemBlock = (net.minecraft.item.ItemBlock) item.getItem();
        if (!(itemBlock.field_150939_a instanceof BlockAltar)) {
            return;
        }

        // Get model based on tier (metadata)
        IModelCustom model = getModelForTier(item.getItemDamage());

        if (model == null) {
            LogHelper.warn("[CraftingAltarItemRenderer] No model found for tier: " + item.getItemDamage());
            return;
        }

        // Apply transformations based on render type
        switch (type) {
            case ENTITY -> {
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glTranslated(0.0, 0.5, 0.0);
                break;
            }
            case EQUIPPED -> {
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glTranslated(1.0, 0.5, 1.0);
                GL11.glRotated(90, 0, 1, 0);
                break;
            }
            case EQUIPPED_FIRST_PERSON -> {
                GL11.glScaled(0.3, 0.3, 0.3);
                GL11.glTranslated(-0.5, 0.5, 0.5);
                GL11.glRotated(90, 0, 1, 0);
                break;
            }
            case INVENTORY -> {
                GL11.glTranslated(0.0, -0.1, 0.0);
                GL11.glScaled(0.4, 0.4, 0.4);
                GL11.glRotated(180, 0, 1, 0);
                GL11.glRotated(15, 1, 0, 0);
                break;
            }
            default -> {
                GL11.glScaled(0.4, 0.4, 0.4);
                break;
            }
        }

        // Render the model with multi-texture support
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Get tier and textures (BRILLIANCE/metadata 4 reuses tier 4 textures)
        int tier = Math.min(item.getItemDamage() + 1, 4); // Clamp: metadata 4 → tier 4
        ResourceLocation texTop = getTexture(tier, "top");
        ResourceLocation texSide = getTexture(tier, "side");
        ResourceLocation texBottom = getTexture(tier, "bottom");

        // Render each part with its texture
        Minecraft.getMinecraft().renderEngine.bindTexture(texSide);
        model.renderPart("pillar");

        Minecraft.getMinecraft().renderEngine.bindTexture(texBottom);
        model.renderPart("base");

        Minecraft.getMinecraft().renderEngine.bindTexture(texTop);
        model.renderPart("top");

        GL11.glPopMatrix();
    }

    /**
     * Get texture for tier and part
     */
    private ResourceLocation getTexture(int tier, String part) {
        return new ResourceLocation("astralsorcery", "textures/models/altar/altar_" + tier + "_" + part + ".png");
    }

    /**
     * Get model for specific tier
     * Metadata 0-4 maps to tiers 1-5 (BRILLIANCE/metadata 4 reuses tier 4 model)
     */
    private IModelCustom getModelForTier(int metadata) {
        return switch (metadata) {
            case 0 -> modelTier1;
            case 1 -> modelTier2;
            case 2 -> modelTier3;
            case 3 -> modelTier4;
            case 4 -> modelTier4; // BRILLIANCE reuses tier 4 model
            default -> modelTier1; // Default to tier 1
        };
    }
}
