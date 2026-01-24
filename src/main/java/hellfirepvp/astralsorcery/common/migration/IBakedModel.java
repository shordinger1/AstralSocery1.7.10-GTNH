/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IBakedModel interface for baked models
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.IBakedModel
 * In 1.7.10: Different model system
 */
public interface IBakedModel {

    List<?> getQuads(net.minecraft.block.Block state, EnumFacing side, long rand);

    // 1.7.10: Additional methods from 1.12.2 API - stub implementations
    default boolean isAmbientOcclusion() {
        return true;
    }

    default boolean isGui3d() {
        return true;
    }

    default boolean isBuiltInRenderer() {
        return false;
    }

    default TextureAtlasSprite getParticleTexture() {
        return null;
    }

    default ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    default ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
