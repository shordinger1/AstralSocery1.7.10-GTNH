/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AirBlockRenderWorld
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:32
 */
public class AirBlockRenderWorld implements IBlockAccess {

    private final BiomeGenBase globalBiomeGenBase;

    public AirBlockRenderWorld(BiomeGenBase globalBiomeGenBase) {
        this.globalBiomeGenBase = globalBiomeGenBase;
    }

    // 1.7.10 IBlockAccess methods (with x, y, z parameters)
    @Nullable
    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return null;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return Blocks.air;
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return true;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue) {
        return 0;
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        return 0;
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int direction) {
        return 0;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return globalBiomeGenBase;
    }

    @Override
    public int getHeight() {
        return 256;
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return _default;
    }

    // Helper methods for BlockPos access (compatibility with 1.12.2 code style)
    public int getCombinedLight(int x, int y, int z, int lightValue) {
        return getLightBrightnessForSkyBlocks(x, y, z, lightValue);
    }

    public int getStrongPower(int x, int y, int z, int direction) {
        return isBlockProvidingPowerTo(x, y, z, direction);
    }

    // Helper methods for BlockPos access (compatibility with 1.12.2 code style)
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    public int getCombinedLight(BlockPos pos, int lightValue) {
        return getLightBrightnessForSkyBlocks(pos.getX(), pos.getY(), pos.getZ(), lightValue);
    }

    public Block getBlockState(BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isAirBlock(BlockPos pos) {
        return isAirBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public BiomeGenBase getBiomeGenBase(BlockPos pos) {
        return getBiomeGenForCoords(pos.getX(), pos.getZ());
    }

    public int getStrongPower(BlockPos pos, ForgeDirection direction) {
        return isBlockProvidingPowerTo(pos.getX(), pos.getY(), pos.getZ(), direction.ordinal());
    }

    public boolean isSideSolid(BlockPos pos, ForgeDirection side, boolean _default) {
        return isSideSolid(pos.getX(), pos.getY(), pos.getZ(), side, _default);
    }

}
