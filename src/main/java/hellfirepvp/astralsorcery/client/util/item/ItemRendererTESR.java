/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRendererTESR
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:04
 */
public class ItemRendererTESR<T extends TileEntity> implements IItemRenderer {

    private final TileEntitySpecialRenderer tesr;
    private final T tile;

    public ItemRendererTESR(TileEntitySpecialRenderer tesr, T tile) {
        this.tesr = tesr;
        this.tile = tile;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        // 1.7.10: Use renderTileEntityAt() instead of render()
        // Use 1.0F (full interpolation) as a reasonable default for item rendering
        tesr.renderTileEntityAt(tile, 0.0, 0.0, 0.0, 1.0F);
    }

}
