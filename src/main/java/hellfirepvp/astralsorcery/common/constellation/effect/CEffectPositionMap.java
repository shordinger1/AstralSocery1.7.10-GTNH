/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;

import com.google.common.base.Function;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ILocatable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionMap
 * Created by HellFirePvP
 * Date: 01.11.2016 / 01:24
 */
public abstract class CEffectPositionMap<K extends NBTBase, V extends NBTBase>
    extends CEffectPositionListGen<GenListEntries.PosDefinedTuple<K, V>> {

    public CEffectPositionMap(@Nullable ILocatable origin, IMajorConstellation c, String cfgName, int maxCount,
        Verifier verifier) {
        // 1.7.10: Use Function<BlockPos, GenListEntries.PosDefinedTuple<K, V>> instead of IGenListFactory
        super(origin, c, cfgName, maxCount, verifier, new Function<BlockPos, GenListEntries.PosDefinedTuple<K, V>>() {

            @Override
            public GenListEntries.PosDefinedTuple<K, V> apply(BlockPos pos) {
                return new GenListEntries.PosDefinedTuple<>(pos);
            }
        });
    }

}
