/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import hellfirepvp.astralsorcery.common.migration.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DummyVanillaBakedModel
 * Created by HellFirePvP
 * Date: 23.07.2016 / 16:24
 */
public class DummyVanillaBakedModel implements IBakedModel {

    private ItemCameraTransforms transforms;

    public DummyVanillaBakedModel() {
        this.transforms = null;
    }

    public DummyVanillaBakedModel(ItemCameraTransforms transforms) {
        this.transforms = transforms;
    }

    @Override
    public List<?> getQuads(@Nullable Block state, @Nullable EnumFacing side, long rand) {
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        // 1.7.10: Method name has a typo - getTextureExtry() not getTextureEntry()
        return Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getTextureExtry("");
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return transforms;
    }

    @Override
    public ItemOverrideList getOverrides() {
        // 1.7.10: Use NONE constant instead of constructor
        return ItemOverrideList.NONE;
    }
}
