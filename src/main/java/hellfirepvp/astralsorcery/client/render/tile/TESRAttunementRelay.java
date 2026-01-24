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
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.base.TileInventoryBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAttunementRelay
 * Created by HellFirePvP
 * Date: 27.03.2017 / 18:07
 */
public class TESRAttunementRelay extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileAttunementRelay)) return;
        TileAttunementRelay tile = (TileAttunementRelay) te;
        renderTileAttunementRelay(tile, x, y, z, partialTicks);
    }

    private void renderTileAttunementRelay(TileAttunementRelay te, double x, double y, double z, float partialTicks) {
        TileInventoryBase.ItemHandlerTile iht = te.getInventoryHandler();
        if (iht == null) return;
        ItemStack in = iht.getStackInSlot(0);
        if ((in == null || in.stackSize <= 0)) return;
        RenderingUtils.renderItemAsEntity(in, x, y - 0.5, z, partialTicks, te.getTicksExisted());
    }

}
