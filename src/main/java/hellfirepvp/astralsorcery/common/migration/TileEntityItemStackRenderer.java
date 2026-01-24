/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * TileEntityItemStackRenderer class for TEISR
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.item.ItemStack;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
 * In 1.7.10: This class doesn't exist
 */
public class TileEntityItemStackRenderer {

    public TileEntityItemStackRenderer() {}

    public void renderByItem(ItemStack stack) {}
}
