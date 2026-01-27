/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IModel interface for models
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Function;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.IModel
 * In 1.7.10: Different model system
 */
public interface IModel {

    IBakedModel bake(IModelState state, VertexFormat format,
        Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter);

    IModelState getDefaultState();
}
