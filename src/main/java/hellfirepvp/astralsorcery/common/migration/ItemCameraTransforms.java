/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ItemCameraTransforms class for item camera transforms
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.ItemCameraTransforms
 * In 1.7.10: Different transform system
 */
public class ItemCameraTransforms {

    public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();

    public ItemCameraTransforms() {}
}
