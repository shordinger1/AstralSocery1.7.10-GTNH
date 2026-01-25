/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IItemRenderer interface for item rendering
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.item.ItemStack;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: Item rendering is handled differently
 * In 1.7.10: ISimpleItemRenderingHandler / IItemRenderer exists
 */
public interface IItemRenderer {

    boolean handleRenderType(ItemStack item, net.minecraft.item.ItemRenderType type);

    boolean shouldUseRenderHelper(net.minecraft.item.ItemRenderType type, ItemStack item,
        net.minecraft.client.renderer.ItemRendererHelper helper);

    void renderItem(net.minecraft.item.ItemRenderType type, ItemStack item, Object... data);
}
