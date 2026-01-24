/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import java.awt.*;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 16:25
 */
public class TESRWell extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileWell)) return;
        TileWell tile = (TileWell) te;
        renderTileWell(tile, x, y, z, partialTicks);
    }

    private void renderTileWell(TileWell te, double x, double y, double z, float partialTicks) {
        ItemStack catalyst = te.getInventoryHandler()
            .getStackInSlot(0);
        if (!(catalyst == null || catalyst.stackSize <= 0)) {
            RenderingUtils.renderItemAsEntity(catalyst, x, y, z, partialTicks, te.getTicksExisted());
        }
        if (te.getFluidAmount() > 0 && te.getHeldFluid() != null) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            Color c = new Color(
                te.getHeldFluid()
                    .getColor());
            GL11.glColor4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, c.getAlpha() / 255F);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            RenderHelper.disableStandardItemLighting();
            Vector3 offset = new Vector3(te).add(0.5D, 0.32D, 0.5D);
            offset.addY(te.getPercFilled() * 0.6);
            IIcon fluidIcon = te.getHeldFluid()
                .getIcon();
            if (fluidIcon == null) fluidIcon = te.getHeldFluid()
                .getStillIcon();

            TextureHelper.setActiveTextureToAtlasSprite();
            RenderingUtils.renderAngleRotatedTexturedRect(
                offset,
                Vector3.RotAxis.Y_AXIS,
                Math.toRadians(45),
                0.54,
                fluidIcon.getMinU(),
                fluidIcon.getMinV(),
                fluidIcon.getMaxU() - fluidIcon.getMinU(),
                fluidIcon.getMaxV() - fluidIcon.getMinV(),
                partialTicks);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
            TextureHelper.refreshTextureBindState();
        }
    }

}
