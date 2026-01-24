/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ItemMeshDefinition interface for item mesh definitions
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.item.ItemStack;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.ItemMeshDefinition
 * In 1.7.10: Different model system
 */
public interface ItemMeshDefinition {

    ModelResourceLocation getModelLocation(ItemStack stack);
}
