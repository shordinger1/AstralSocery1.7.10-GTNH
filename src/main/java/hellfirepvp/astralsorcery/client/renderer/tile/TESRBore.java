/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESR for Bore Block
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
import hellfirepvp.astralsorcery.common.tile.TileBore;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Bore
 */
@SideOnly(Side.CLIENT)
public class TESRBore extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    public TESRBore() {
        this(null, null);
    }

    public TESRBore(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;

        if (this.model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileBore.class, this);
                LogHelper.info("[TESRBore] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRBore] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            return;
        }

        if (!(tile instanceof TileBore)) {
            return;
        }

        if (this.model == null) {
            return;
        }

        GL11.glPushMatrix();

        try {
            GL11.glTranslated(x, y, z);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            this.bindTexture(this.texture);
            this.model.renderAll();

        } catch (Exception e) {
            LogHelper.error("[TESRBore] Error during render", e);
        } finally {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }
}
