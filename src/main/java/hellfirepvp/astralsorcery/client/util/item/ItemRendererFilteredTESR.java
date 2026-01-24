/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRendererFilteredTESR
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:20
 */
public class ItemRendererFilteredTESR implements IItemRenderer {

    private Map<Integer, TEISRProperties> renderMap = new HashMap<>();

    public void addRender(int stackMeta, TileEntitySpecialRenderer tesr, TileEntity renderTile) {
        renderMap.put(stackMeta, new TEISRProperties(tesr, renderTile));
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return renderMap.containsKey(item.getItemDamage());
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (renderMap.containsKey(item.getItemDamage())) {
            TEISRProperties prop = renderMap.get(item.getItemDamage());
            // 1.7.10: Use renderTileEntityAt() instead of render()
            // 1.7.10: timer is private, and partialTicks isn't passed in renderItem data
            // Use 1.0F (full interpolation) as a reasonable default for item rendering
            prop.tesr.renderTileEntityAt(prop.renderTile, 0.0, 0.0, 0.0, 1.0F);
        }
    }

    private static class TEISRProperties {

        private final TileEntitySpecialRenderer tesr;
        private final TileEntity renderTile;

        private TEISRProperties(TileEntitySpecialRenderer tesr, TileEntity renderTile) {
            this.tesr = tesr;
            this.renderTile = renderTile;
        }
    }

}
