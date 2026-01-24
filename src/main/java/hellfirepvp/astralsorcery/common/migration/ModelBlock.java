/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ModelBlock class for model blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.ModelBlock
 * In 1.7.10: Different model system
 */
public class ModelBlock {

    public static ModelBlock deserialize(String json) {
        return new ModelBlock();
    }

    public ModelBlock() {}
}
