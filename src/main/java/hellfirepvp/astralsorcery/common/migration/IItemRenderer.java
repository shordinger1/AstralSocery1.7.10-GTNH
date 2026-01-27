/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IItemRenderer interface for item rendering
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.item.ItemStack;

// In 1.7.10, use the Forge IItemRenderer directly
// This is just an alias for compatibility
public interface IItemRenderer extends net.minecraftforge.client.IItemRenderer {

    // All methods are inherited from net.minecraftforge.client.IItemRenderer
}
