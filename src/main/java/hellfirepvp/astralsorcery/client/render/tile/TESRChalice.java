/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;
// TODO: Forge fluid system - manual review needed

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 22:09
 */
public class TESRChalice extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileChalice)) return;
        TileChalice tile = (TileChalice) te;
        renderTileChalice(tile, x, y, z, partialTicks);
    }

    private void renderTileChalice(TileChalice te, double x, double y, double z, float partialTicks) {
        Fluid filled = te.getHeldFluid();
        if (filled != null && te.getFluidAmount() > 0) {
            TileChalice.DrawSize size = te.getDrawSize();
            FluidStack fs = te.getTank()
                .getFluid();
            if (fs != null) {
                TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(fs);
                Vector3 rot = getInterpolatedRotation(te, partialTicks);

                double ulength = tas.getMaxU() - tas.getMinU();
                double vlength = tas.getMaxV() - tas.getMinV();

                double uPart = ulength * (size.partTexture / 2);
                double vPart = vlength * (size.partTexture / 2);

                double uOffset = tas.getMinU() + ulength / 2D - uPart / 2D;
                double vOffset = tas.getMinV() + vlength / 2D - vPart / 2D;

                if (size == TileChalice.DrawSize.SMALL) {
                    uOffset = tas.getMinU();
                    vOffset = tas.getMinV();
                }

                TextureHelper.setActiveTextureToAtlasSprite();
                RenderHelper.enableGUIStandardItemLighting();

                RenderingUtils
                    .renderTexturedCubeCentral(new Vector3(0, 0, 0), size.partTexture, uOffset, vOffset, uPart, vPart);

            }
        }
    }

    private Vector3 getInterpolatedRotation(TileChalice tc, float percent) {
        return new Vector3(
            RenderingUtils.interpolate(tc.prevRotationDegreeAxis.getX(), tc.rotationDegreeAxis.getX(), percent),
            RenderingUtils.interpolate(tc.prevRotationDegreeAxis.getY(), tc.rotationDegreeAxis.getY(), percent),
            RenderingUtils.interpolate(tc.prevRotationDegreeAxis.getZ(), tc.rotationDegreeAxis.getZ(), percent));
    }

}
