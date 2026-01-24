/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.models.base.AStelescope;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRTelescope
 * Created by HellFirePvP
 * Date: 10.11.2016 / 22:29
 */
public class TESRTelescope extends TileEntitySpecialRenderer {

    private static final AStelescope modelTelescope = new AStelescope();
    private static final BindableResource texTelescope = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "base/telescope");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileTelescope)) return;
        TileTelescope tile = (TileTelescope) te;
        renderTileTelescope(tile, x, y, z, partialTicks);
    }

    private void renderTileTelescope(TileTelescope te, double x, double y, double z, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x + 0.5F), (float) (y + 1.28F), (float) (z + 0.5F));
        GL11.glRotatef(180F, 1F, 0F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);
        GL11.glRotatef(
            te.getRotation()
                .ordinal() * 45F,
            0F,
            1F,
            0F);
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);

        GL11.glPushMatrix();
        GL11.glRotatef(
            (te.getRotation()
                .ordinal()) * 45F + 152.0F,
            0.0F,
            1.0F,
            0.0F);
        GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();

        renderModel();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderModel() {
        texTelescope.bind();
        modelTelescope.render(null, 0, 0, 0, 0, 0, 1);
    }

}
