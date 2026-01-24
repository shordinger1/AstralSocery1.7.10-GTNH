/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderWorldBuffer
 * Created by HellFirePvP
 * Date: 21.06.2018 / 20:50
 */
public class RenderWorldBuffer implements IBlockAccess {

    private Map<BlockPos, Block> blockRenderData = new HashMap<>();
    private BiomeGenBase defaultBiome;

    public RenderWorldBuffer(BiomeGenBase defaultBiome, BlockArray array) {
        this.defaultBiome = defaultBiome;
        this.blockRenderData.putAll(MiscUtils.remap(array.getPattern(), (bi) -> bi.state));
    }

    public void appendBlock(Block block, BlockPos offset) {
        this.blockRenderData.put(offset, block);
    }

    public void appendAll(Map<BlockPos, Block> states) {
        this.blockRenderData.putAll(states);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return null;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_) {
        return getCombinedLight(new BlockPos(p_72802_1_, p_72802_2_, p_72802_3_), p_72802_4_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockMetadata(int x, int y, int z) {
        return 0;
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
        return 0;
    }

    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return this.blockRenderData.getOrDefault(pos, Blocks.air);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return this.blockRenderData.getOrDefault(pos, Blocks.air) == Blocks.air;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return defaultBiome;
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
        Block block = getBlock(x, y, z);
        return block != Blocks.air;
    }

    // Helper methods for BlockPos access (compatibility with 1.12.2 code style)
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    @SideOnly(Side.CLIENT)
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return lightValue;
    }

    public Block getBlockState(BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isAirBlock(BlockPos pos) {
        return isAirBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public BiomeGenBase getBiome(BlockPos pos) {
        return defaultBiome;
    }

    public int getStrongPower(BlockPos pos, ForgeDirection direction) {
        return 0;
    }

    public boolean isSideSolid(BlockPos pos, ForgeDirection side, boolean _default) {
        return isSideSolid(pos.getX(), pos.getY(), pos.getZ(), side, _default);
    }

}
