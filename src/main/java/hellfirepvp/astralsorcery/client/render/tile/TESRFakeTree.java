/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import java.awt.*;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 21:13
 */
public class TESRFakeTree extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileFakeTree)) return;
        TileFakeTree tile = (TileFakeTree) te;
        renderTileFakeTree(tile, x, y, z, partialTicks);
    }

    private void renderTileFakeTree(TileFakeTree te, double x, double y, double z, float partialTicks) {
        if (te.getFakedState() == null) return;
        Block renderState = te.getFakedState();
        if (x * x + y * y + z * z >= 64 * 64) return;
        Color effect = null;
        if (te.getPlayerEffectRef() != null) {
            Collection<PatreonEffectHelper.PatreonEffect> effects = PatreonEffectHelper
                .getPatreonEffects(Side.CLIENT, te.getPlayerEffectRef());
            PatreonEffectHelper.PatreonEffect pe = null;
            for (PatreonEffectHelper.PatreonEffect eff : effects) {
                if (eff instanceof PtEffectTreeBeacon) {
                    pe = eff;
                    break;
                }
            }
            if (pe instanceof PtEffectTreeBeacon) {
                effect = new Color(((PtEffectTreeBeacon) pe).getColorTranslucentOverlay(), true);
            }
        }
        TESRTranslucentBlock.addForRender(effect, renderState, new BlockPos(te.xCoord, te.yCoord, te.zCoord));
    }

}
