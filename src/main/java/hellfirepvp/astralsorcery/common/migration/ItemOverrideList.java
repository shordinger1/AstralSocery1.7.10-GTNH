/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ItemOverrideList class for item overrides
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.ItemOverrideList
 * In 1.7.10: Different override system
 */
public class ItemOverrideList {

    public static final ItemOverrideList NONE = new ItemOverrideList();

    public ItemOverrideList() {}
}
