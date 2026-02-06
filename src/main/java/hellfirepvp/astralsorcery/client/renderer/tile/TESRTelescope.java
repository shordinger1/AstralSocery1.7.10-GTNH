/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRTelescope - TileEntitySpecialRenderer for Telescope
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
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Telescope (BlockMachine metadata 0)
 * <p>
 * Renders the telescope model with proper textures
 */
@SideOnly(Side.CLIENT)
public class TESRTelescope extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    public TESRTelescope(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;

        // NOTE: TESR is NOT registered here to avoid conflicts with TESRGrindstone
        // TESRGrindstone handles both Grindstone and Telescope rendering by checking block metadata
        // This class exists for reference and potential future separation
        // Original 1.12.2 had separate TESR registrations:
        // - TileGrindstone.class → TESRGrindstone
        // - TileTelescope.class → TESRTelescope
        // In 1.7.10, BlockMachine uses TileGrindstone for both variants, handled by TESRGrindstone
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            return;
        }

        // Render for both Telescope and Grindstone (they share the same TileEntity for now)
        // In the future, check for TileTelescope when it's implemented

        if (this.model == null) {
            return;
        }

        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE); // Enable proper face culling for solid model
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Bind texture and render all parts
            bindTexture(texture);
            model.renderAll();

        } catch (Exception e) {
            LogHelper.error("[TESRTelescope] Render error", e);
        } finally {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE); // Restore default
            GL11.glPopMatrix();
        }
    }
}
