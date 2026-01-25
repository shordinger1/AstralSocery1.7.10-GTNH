/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.array;

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.structure.MatchableStructure;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternBlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:24
 */
public class PatternBlockArray extends BlockArray implements MatchableStructure {

    private final ResourceLocation registryName;

    public PatternBlockArray(ResourceLocation name) {
        this.registryName = name;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public boolean matches(World world, BlockPos center) {
        for (Map.Entry<BlockPos, BlockInformation> entry : pattern.entrySet()) {
            BlockInformation info = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            Block state = world.getBlock(at.posX, at.posY, at.posZ);
            if (!info.matcher.isStateValid(state)) {
                return false;
            }
        }
        return true;
    }

    public boolean matchesSlice(World world, BlockPos center, int slice) {
        for (Map.Entry<BlockPos, BlockInformation> entry : this.getPatternSlice(slice)
            .entrySet()) {
            BlockInformation info = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            Block state = world.getBlock(at.posX, at.posY, at.posZ);
            if (!info.matcher.isStateValid(state)) {
                return false;
            }
        }
        return true;
    }

    public boolean matchSingleBlockState(BlockPos offset, Block state) {
        if (!pattern.containsKey(offset)) return false;
        BlockInformation info = pattern.get(offset);
        return info.matcher.isStateValid(state);
    }

    public boolean matchSingleBlock(IBlockAccess world, BlockPos center, BlockPos offset) {
        if (!pattern.containsKey(offset)) return false;
        BlockInformation info = pattern.get(offset);
        BlockPos at = center.add(offset);
        Block state = world.getBlock(at.posX, at.posY, at.posZ);
        return info.matcher.isStateValid(state);
    }

}
