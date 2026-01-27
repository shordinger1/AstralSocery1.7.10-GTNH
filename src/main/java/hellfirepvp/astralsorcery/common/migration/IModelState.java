/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IModelState interface for model states
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import javax.annotation.Nullable;

import com.google.common.base.Function;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.IModelState
 * In 1.7.10: This concept doesn't exist
 */
public interface IModelState {

    @Nullable
    IModelState apply(Function<Object, IModelState> mappingContext);
}
