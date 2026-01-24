/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRStarlightInfuser
 * Created by HellFirePvP
 * Date: 13.01.2017 / 12:17
 */
public class TESRStarlightInfuser extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileStarlightInfuser)) return;
        TileStarlightInfuser tile = (TileStarlightInfuser) te;
        renderTileStarlightInfuser(tile, x, y, z, partialTicks);
    }

    private void renderTileStarlightInfuser(TileStarlightInfuser te, double x, double y, double z, float partialTicks) {
        ItemStack in = te.getInputStack();
        if ((in == null || in.stackSize <= 0)) return;
        RenderingUtils.renderItemAsEntity(in, x, y, z, partialTicks, te.getTicksExisted());
    }
}
