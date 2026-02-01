/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRAltar - Multi-texture rendering for Crafting Altar
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Crafting Altar with multi-texture support
 */
@SideOnly(Side.CLIENT)
public class TESRCraftingAltar extends TileEntitySpecialRenderer {

    private final IModelCustom modelTier1;
    private final IModelCustom modelTier2;
    private final IModelCustom modelTier3;
    private final IModelCustom modelTier4;

    public TESRCraftingAltar(IModelCustom m1, IModelCustom m2, IModelCustom m3, IModelCustom m4) {
        this.modelTier1 = m1;
        this.modelTier2 = m2;
        this.modelTier3 = m3;
        this.modelTier4 = m4;

        if (m1 != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileAltar.class, this);
                LogHelper.info("[TESRCraftingAltar] Registered multi-texture TESR");
            } catch (Exception e) {
                LogHelper.error("[TESRCraftingAltar] Failed to register", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileAltar)) return;

        TileAltar altar = (TileAltar) tile;
        int tier = getAltarTier(altar);
        IModelCustom model = getModelForTier(tier);
        if (model == null) return;

        ResourceLocation texTop = getTexture(tier, "top");
        ResourceLocation texSide = getTexture(tier, "side");
        ResourceLocation texBottom = getTexture(tier, "bottom");

        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Render pillar (center column) with side texture
            bindTexture(texSide);
            model.renderPart("pillar");

            // Render base with bottom texture
            bindTexture(texBottom);
            model.renderPart("base");

            // Render top with top texture
            bindTexture(texTop);
            model.renderPart("top");

        } catch (Exception e) {
            LogHelper.error("[TESRCraftingAltar] Render error", e);
        } finally {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    private int getAltarTier(TileAltar altar) {
        switch (altar.getAltarLevel()) {
            case DISCOVERY: return 1;
            case ATTUNEMENT: return 2;
            case CONSTELLATION_CRAFT: return 3;
            case TRAIT_CRAFT: return 4;
            case BRILLIANCE: return 4; // BRILLIANCE reuses tier 4 model
            default: return 1;
        }
    }

    private IModelCustom getModelForTier(int tier) {
        switch (tier) {
            case 1: return modelTier1;
            case 2: return modelTier2;
            case 3: return modelTier3;
            case 4: return modelTier4;
            default: return modelTier1;
        }
    }

    private ResourceLocation getTexture(int tier, String part) {
        return new ResourceLocation("astralsorcery", "textures/models/altar/altar_" + tier + "_" + part + ".png");
    }
}
