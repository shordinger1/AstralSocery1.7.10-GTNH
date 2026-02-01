/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Prism Item Renderer
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.common.block.BlockPrism;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * ItemRenderer for Prism
 */
public class PrismItemRenderer implements IItemRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    public PrismItemRenderer(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;
        LogHelper.info("[PrismItemRenderer] Initialized with model and texture: " + texture);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item == null || item.getItem() == null) {
            return false;
        }

        if (item.getItem() instanceof net.minecraft.item.ItemBlock) {
            net.minecraft.item.ItemBlock itemBlock = (net.minecraft.item.ItemBlock) item.getItem();
            if (itemBlock.field_150939_a instanceof BlockPrism) {
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
        if (!(itemBlock.field_150939_a instanceof BlockPrism)) {
            return;
        }

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

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);

        if (this.model != null) {
            this.model.renderAll();
        } else {
            LogHelper.warn("[PrismItemRenderer] Model is null, cannot render!");
        }

        GL11.glPopMatrix();
    }
}
