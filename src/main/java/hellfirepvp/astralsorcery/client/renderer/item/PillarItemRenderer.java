/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Pillar Item Renderer - Handles pillar item rendering in inventory and hand
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * ItemRenderer for pillar blocks
 * <p>
 * Handles rendering of pillar variants (metadata 2, 3, 4) in inventory and when held.
 * Non-pillar variants (0, 1, 5, 6, 7, 8) use default icon-based rendering.
 * <p>
 * Uses multi-texture OBJ rendering for pillar items.
 */
@SideOnly(Side.CLIENT)
public class PillarItemRenderer implements IItemRenderer {

    private final Block block;
    private final net.minecraftforge.client.model.IModelCustom modelPillar;
    private final net.minecraftforge.client.model.IModelCustom modelPillarTop;
    private final net.minecraftforge.client.model.IModelCustom modelPillarBottom;
    private final ResourceLocation texTop;
    private final ResourceLocation texSide;
    private final ResourceLocation texBottom;
    private final ResourceLocation texInner;
    private final ResourceLocation texUpdown;
    private final String texturePrefix;

    /**
     * Create a pillar item renderer
     *
     * @param block             The block being rendered
     * @param modelPillar       Base pillar model (metadata 2)
     * @param modelPillarTop    Pillar top model (metadata 3)
     * @param modelPillarBottom Pillar bottom model (metadata 4)
     * @param texturePrefix     Texture prefix for group names
     * @param texTop            Top section side texture
     * @param texSide           Middle pillar side texture
     * @param texBottom         Bottom section side texture
     * @param texInner          Inner end cap texture
     * @param texUpdown         Top/bottom cap texture
     */
    public PillarItemRenderer(Block block, net.minecraftforge.client.model.IModelCustom modelPillar,
        net.minecraftforge.client.model.IModelCustom modelPillarTop,
        net.minecraftforge.client.model.IModelCustom modelPillarBottom, String texturePrefix, ResourceLocation texTop,
        ResourceLocation texSide, ResourceLocation texBottom, ResourceLocation texInner, ResourceLocation texUpdown) {
        this.block = block;
        this.modelPillar = modelPillar;
        this.modelPillarTop = modelPillarTop;
        this.modelPillarBottom = modelPillarBottom;
        this.texturePrefix = texturePrefix;
        this.texTop = texTop;
        this.texSide = texSide;
        this.texBottom = texBottom;
        this.texInner = texInner;
        this.texUpdown = texUpdown;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item == null || item.getItem() == null) {
            return false;
        }

        // Only handle ItemBlock for our block
        if (!(item.getItem() instanceof net.minecraft.item.ItemBlock)) {
            return false;
        }

        net.minecraft.item.ItemBlock itemBlock = (net.minecraft.item.ItemBlock) item.getItem();
        if (itemBlock.field_150939_a != this.block) {
            return false;
        }

        // Only render pillar variants (2, 3, 4)
        int meta = item.getItemDamage();

        // Debug logging
        boolean result = meta == 2 || meta == 3 || meta == 4;
        if (result) {
            hellfirepvp.astralsorcery.common.util.LogHelper.debug(
                "[PillarItemRenderer] handleRenderType: block=" + this.block.getClass()
                    .getSimpleName() + ", meta=" + meta + ", type=" + type + " -> TRUE");
        }

        return result;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        // Only handle pillar variants
        int meta = item.getItemDamage();
        if (meta != 2 && meta != 3 && meta != 4) {
            return false;
        }
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
        if (itemBlock.field_150939_a != this.block) {
            return;
        }

        int meta = item.getItemDamage();
        if (meta != 2 && meta != 3 && meta != 4) {
            return;
        }

        // Always show as base pillar in inventory/hand (metadata 2)
        // Get the correct model
        net.minecraftforge.client.model.IModelCustom model = getModelForMetadata(2);

        if (model == null) {
            return;
        }

        // Apply transformations based on render type
        switch (type) {
            case ENTITY:
                // Item dropped in world
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glTranslated(0.0, 0.5, 0.0);
                break;
            case EQUIPPED:
                // Item held in hand (third person)
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glTranslated(1.0, 0.5, 1.0);
                GL11.glRotated(90, 0, 1, 0);
                break;
            case EQUIPPED_FIRST_PERSON:
                // Item held in hand (first person)
                GL11.glScaled(0.3, 0.3, 0.3);
                GL11.glTranslated(-0.5, 0.5, 0.5);
                GL11.glRotated(90, 0, 1, 0);
                break;
            case INVENTORY:
                // Item in inventory GUI
                GL11.glTranslated(0.0, -0.1, 0.0);
                GL11.glScaled(0.4, 0.4, 0.4);
                GL11.glRotated(180, 0, 1, 0);
                GL11.glRotated(15, 1, 0, 0);
                break;
            default:
                GL11.glScaled(0.4, 0.4, 0.4);
                break;
        }

        // Setup OpenGL state
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Render with multi-texture support
        renderModelWithTextures(model);

        GL11.glPopMatrix();
    }

    /**
     * Render model with multi-texture support using Forge's WavefrontObject
     */
    private void renderModelWithTextures(net.minecraftforge.client.model.IModelCustom model) {
        Minecraft mc = Minecraft.getMinecraft();

        // Render inner end cap texture
        mc.renderEngine.bindTexture(texInner);
        try {
            model.renderPart(texturePrefix + "_inner");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render top/bottom cap texture
        mc.renderEngine.bindTexture(texUpdown);
        try {
            model.renderPart(texturePrefix + "_updown");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render pillar side texture (main body)
        mc.renderEngine.bindTexture(texSide);
        try {
            model.renderPart(texturePrefix);
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render top section side texture
        mc.renderEngine.bindTexture(texTop);
        try {
            model.renderPart(texturePrefix + "_top");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render bottom section side texture
        mc.renderEngine.bindTexture(texBottom);
        try {
            model.renderPart(texturePrefix + "_bottom");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }

        // Render engraved texture (for pillar bottom model base)
        mc.renderEngine.bindTexture(texBottom);
        try {
            model.renderPart("marble_engraved");
        } catch (Exception e) {
            // Group doesn't exist, continue
        }
    }

    /**
     * Get model for specific metadata
     * Metadata 2 = PILLAR (middle)
     * Metadata 3 = PILLAR_TOP (top section)
     * Metadata 4 = PILLAR_BOTTOM (bottom section)
     */
    private net.minecraftforge.client.model.IModelCustom getModelForMetadata(int metadata) {
        switch (metadata) {
            case 2:
                return modelPillar;
            case 3:
                return modelPillarTop;
            case 4:
                return modelPillarBottom;
            default:
                return modelPillar;
        }
    }
}
