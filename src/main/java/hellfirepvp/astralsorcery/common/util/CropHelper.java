/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;

import com.google.common.collect.Lists;

import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.migration.IGrowable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CropHelper
 * Created by HellFirePvP
 * Date: 08.11.2016 / 13:05
 */
// Intended for mostly Server-Side use
public class CropHelper {

    @Nullable
    public static GrowablePlant wrapPlant(World world, BlockPos pos) {
        Block state = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        Block b = state;
        if (state instanceof IGrowable) {
            if (b instanceof BlockGrass) return null;
            if (b instanceof BlockTallGrass) return null;
            if (b instanceof BlockDoublePlant) return null;
            return new GrowableWrapper(pos);
        }
        if (state.equals(Blocks.reeds)) {
            if (isReedBase(world, pos)) {
                return new GrowableReedWrapper(pos);
            }
        }
        if (state.equals(Blocks.cactus)) {
            if (isCactusBase(world, pos)) {
                return new GrowableCactusWrapper(pos);
            }
        }
        if (state.equals(Blocks.nether_wart)) {
            return new GrowableNetherwartWrapper(pos);
        }
        return null;
    }

    @Nullable
    public static HarvestablePlant wrapHarvestablePlant(World world, BlockPos pos) {
        GrowablePlant growable = wrapPlant(world, pos);
        if (growable == null) return null; // Every plant has to be growable.
        BlockPos growablePos = growable.getPos();
        Block state = world.getBlock(growablePos.getX(), growablePos.getY(), growablePos.getZ());
        if (state.equals(Blocks.reeds) && growable instanceof GrowableReedWrapper) {
            return (GrowableReedWrapper) growable;
        }
        if (state.equals(Blocks.cactus) && growable instanceof GrowableCactusWrapper) {
            return (GrowableCactusWrapper) growable;
        }
        if (state.equals(Blocks.nether_wart) && growable instanceof GrowableNetherwartWrapper) {
            return (GrowableNetherwartWrapper) growable;
        }
        if (state instanceof IPlantable) {
            return new HarvestableWrapper(pos);
        }
        return null;
    }

    private static boolean isReedBase(World world, BlockPos pos) {
        BlockPos down = pos.add(0, -1, 0);
        return !world.getBlock(down.getX(), down.getY(), down.getZ())
            .equals(Blocks.reeds);
    }

    private static boolean isCactusBase(World world, BlockPos pos) {
        BlockPos down = pos.add(0, -1, 0);
        return !world.getBlock(down.getX(), down.getY(), down.getZ())
            .equals(Blocks.cactus);
    }

    public static interface GrowablePlant extends CEffectPositionListGen.CEffectGenListEntry {

        public boolean isValid(World world, boolean forceChunkLoad);

        public boolean canGrow(World world);

        public boolean tryGrow(World world, Random rand);

    }

    public static interface HarvestablePlant extends GrowablePlant {

        public boolean canHarvest(World world);

        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune);

    }

    public static class HarvestableWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public HarvestableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(World world) {
            Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (!(at instanceof IGrowable)) return false;
            // 1.7.10: Use migration IGrowable.canGrow() method
            return !((IGrowable) at).canGrow(world, pos, at, false);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            if (canHarvest(world)) {
                BlockPos pos = getPos();
                Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                if (at instanceof IPlantable) {
                    // 1.7.10: getDrops takes x, y, z, metadata, fortune
                    drops.addAll(
                        at.getDrops(
                            world,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()),
                            harvestFortune));
                    world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                    // 1.7.10: getPlant takes x, y, z coordinates, not BlockPos
                    world.setBlock(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        ((IPlantable) at).getPlant(world, pos.getX(), pos.getY(), pos.getZ()),
                        0,
                        3);
                }
            }
            return drops;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(getPos()))) return true; // We stall
            // until it's
            // loaded.
            HarvestablePlant plant = wrapHarvestablePlant(world, getPos());
            return plant != null && plant instanceof HarvestableWrapper;
        }

        @Override
        public boolean canGrow(World world) {
            Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            return at instanceof IGrowable && ((IGrowable) at).canGrow(world, pos, at, false);
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (at instanceof IGrowable) {
                if (((IGrowable) at).canGrow(world, pos, at, false)) {
                    // 1.7.10: Use migration IGrowable.grow() method
                    ((IGrowable) at).grow(world, rand, pos, at);
                    return true;
                }
            }
            return false;
        }

    }

    public static class GrowableNetherwartWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableNetherwartWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            // 1.7.10: Use lowercase block name
            return block.equals(Blocks.nether_wart);
        }

        @Override
        public boolean canGrow(World world) {
            Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            // 1.7.10: Use lowercase block name
            return at.equals(Blocks.nether_wart) && meta < 3;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            if (rand.nextBoolean()) {
                Block current = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
                if (meta < 3) {
                    return world.setBlock(pos.getX(), pos.getY(), pos.getZ(), current, meta + 1, 3);
                }
            }
            return false;
        }

        @Override
        public boolean canHarvest(World world) {
            Block current = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            // 1.7.10: Use lowercase block name
            return current.equals(Blocks.nether_wart) && meta >= 3;
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            Block current = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            // 1.7.10: getDrops takes x, y, z, metadata, fortune
            List<ItemStack> drops = current.getDrops(
                world,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()),
                harvestFortune);
            int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            // 1.7.10: Use lowercase block name
            world.setBlock(pos.getX(), pos.getY(), pos.getZ(), Blocks.nether_wart, meta != -1 ? meta : 0, 3);
            return drops;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

    }

    public static class GrowableCactusWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableCactusWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(World world) {
            BlockPos up = pos.add(0, 1, 0);
            Block block = world.getBlock(up.getX(), up.getY(), up.getZ());
            // 1.7.10: Use lowercase block name
            return block.equals(Blocks.cactus);
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            // 1.7.10: Use lowercase block name
            return block.equals(Blocks.cactus);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.add(0, i, 0);
                Block at = world.getBlock(bp.getX(), bp.getY(), bp.getZ());
                // 1.7.10: Use lowercase block name
                if (at.equals(Blocks.cactus)) {
                    MiscUtils.breakBlockWithoutPlayer((WorldServer) world, bp);
                }
            }
            return drops;
        }

        @Override
        public boolean canGrow(World world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                // 1.7.10: isAirBlock takes x, y, z coordinates
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                // 1.7.10: isAirBlock takes x, y, z coordinates
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    if (rand.nextBoolean()) {
                        // 1.7.10: Use lowercase block name
                        return world.setBlock(cache.getX(), cache.getY(), cache.getZ(), Blocks.cactus, 0, 3);
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}
    }

    public static class GrowableReedWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableReedWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(World world) {
            BlockPos up = pos.add(0, 1, 0);
            Block block = world.getBlock(up.getX(), up.getY(), up.getZ());
            // 1.7.10: Use lowercase block name
            return block.equals(Blocks.reeds);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.add(0, i, 0);
                Block at = world.getBlock(bp.getX(), bp.getY(), bp.getZ());
                // 1.7.10: Use lowercase block name
                if (at.equals(Blocks.reeds)) {
                    // 1.7.10: getDrops takes x, y, z, metadata, fortune
                    drops.addAll(
                        at.getDrops(
                            world,
                            bp.getX(),
                            bp.getY(),
                            bp.getZ(),
                            world.getBlockMetadata(bp.getX(), bp.getY(), bp.getZ()),
                            harvestFortune));
                    world.setBlockToAir(bp.getX(), bp.getY(), bp.getZ());
                }
            }
            return drops;
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            // 1.7.10: Use lowercase block name
            return block.equals(Blocks.reeds);
        }

        @Override
        public boolean canGrow(World world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                // 1.7.10: isAirBlock takes x, y, z coordinates
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                // 1.7.10: isAirBlock takes x, y, z coordinates
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    if (rand.nextBoolean()) {
                        // 1.7.10: Use lowercase block name
                        return world.setBlock(cache.getX(), cache.getY(), cache.getZ(), Blocks.reeds, 0, 3);
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

    }

    public static class GrowableWrapper implements GrowablePlant {

        private final BlockPos pos;

        public GrowableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            GrowablePlant res = wrapPlant(world, pos);
            return res != null && res instanceof GrowableWrapper;
        }

        @Override
        public boolean canGrow(World world) {
            Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            return at instanceof IGrowable && (((IGrowable) at).canGrow(world, pos, at, false)
                || (at instanceof BlockStem && !stemHasCrop(world)));
        }

        private boolean stemHasCrop(World world) {
            // 1.7.10: EnumFacing.Plane.HORIZONTAL doesn't exist, iterate manually
            EnumFacing[] horizontals = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST,
                EnumFacing.WEST };
            for (EnumFacing enumfacing : horizontals) {
                // 1.7.10: BlockPos.offset() doesn't work with EnumFacing, calculate manually
                BlockPos offset = new BlockPos(
                    pos.getX() + enumfacing.getFrontOffsetX(),
                    pos.getY() + enumfacing.getFrontOffsetY(),
                    pos.getZ() + enumfacing.getFrontOffsetZ());
                Block block = world.getBlock(offset.getX(), offset.getY(), offset.getZ());
                // 1.7.10: Use lowercase block names for melon and pumpkin
                if (block.equals(Blocks.melon_block) || block.equals(Blocks.pumpkin)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (at instanceof IGrowable) {
                if (((IGrowable) at).canGrow(world, pos, at, false)) {
                    if (!((IGrowable) at).canUseBonemeal(world, rand, pos, at)) {
                        if (world.rand.nextInt(20) != 0) return true; // Returning true to say it could've been
                        // potentially grown - So this doesn't invalidate
                        // caches.
                    }
                    // 1.7.10: Use migration IGrowable.grow() method
                    ((IGrowable) at).grow(world, rand, pos, at);
                    return true;
                }
                if (at instanceof BlockStem) {
                    for (int i = 0; i < 10; i++) {
                        // 1.7.10: Block.updateTick() takes (World, x, y, z, Random)
                        at.updateTick(world, pos.getX(), pos.getY(), pos.getZ(), rand);
                    }
                    return true;
                }
            }
            return false;
        }
    }

}
