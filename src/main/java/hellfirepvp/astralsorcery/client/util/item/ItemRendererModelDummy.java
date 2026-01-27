/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Function;

import hellfirepvp.astralsorcery.common.migration.IBakedModel;
import hellfirepvp.astralsorcery.common.migration.IModel;
import hellfirepvp.astralsorcery.common.migration.IModelState;
import hellfirepvp.astralsorcery.common.migration.VertexFormat;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRendererModelDummy
 * Created by HellFirePvP
 * Date: 23.07.2016 / 16:21
 */
public class ItemRendererModelDummy implements IModel {

    private static final IModelState NO_STATE = new IModelState() {

        @Override
        public IModelState apply(Function<Object, IModelState> mappingContext) {
            return null;
        }
    };

    private ResourceLocation parent;

    public ItemRendererModelDummy(ResourceLocation parent) {
        this.parent = parent;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
        Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new DummyVanillaBakedModel();
    }

    @Override
    public IModelState getDefaultState() {
        return NO_STATE;
    }

}
