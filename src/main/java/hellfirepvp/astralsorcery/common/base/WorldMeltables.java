/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.ChunkPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldMeltables
 * Created by HellFirePvP
 * Date: 31.10.2016 / 22:49
 */
public enum WorldMeltables implements MeltInteraction {

    COBBLE(new BlockStateCheck.Block(Blocks.cobblestone), Blocks.flowing_lava, 180),
    STONE(new BlockStateCheck.Block(Blocks.stone), Blocks.flowing_lava, 100),
    OBSIDIAN(new BlockStateCheck.Block(Blocks.obsidian), Blocks.flowing_lava, 75),
    NETHERRACK(new BlockStateCheck.Block(Blocks.netherrack), Blocks.flowing_lava, 40),
    NETHERBRICK(new BlockStateCheck.Block(Blocks.nether_brick), Blocks.flowing_lava, 60),
    // MAGMA(new BlockStateCheck.Block(Blocks.magma), Blocks.flowing_lava, 1), // 1.7.10: Magma block doesn't exist
    ICE(new BlockStateCheck.Block(Blocks.ice), Blocks.flowing_water, 1),
    // FROSTED_ICE(new BlockStateCheck.Block(Blocks.frosted_ice), Blocks.flowing_water, 1), // 1.7.10: Frosted ice
    // doesn't exist
    PACKED_ICE(new BlockStateCheck.Block(Blocks.packed_ice), Blocks.flowing_water, 2);

    private final BlockStateCheck meltableCheck;
    private final Block meltResult;
    private final int meltDuration;

    private WorldMeltables(BlockStateCheck meltableCheck, Block meltResult, int meltDuration) {
        this.meltableCheck = meltableCheck;
        this.meltResult = meltResult;
        this.meltDuration = meltDuration;
    }

    @Override
    public boolean isMeltable(World world, BlockPos pos, Block worldState) {
        return meltableCheck.isStateValid(worldState);
    }

    @Override
    @Nullable
    public Block getMeltResultState() {
        return meltResult;
    }

    @Override
    @Nonnull
    public ItemStack getMeltResultStack() {
        return null;
    }

    @Override
    public int getMeltTickDuration() {
        return meltDuration;
    }

    @Nullable
    public static MeltInteraction getMeltable(World world, BlockPos pos) {
        Block state = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        for (WorldMeltables melt : values()) {
            if (melt.isMeltable(world, pos, state)) return melt;
        }
        ItemStack stack = ItemUtils.createBlockStack(state);
        if (!(stack == null || stack.stackSize <= 0)) {
            ItemStack out = FurnaceRecipes.smelting()
                .getSmeltingResult(stack);
            if (!(out == null || out.stackSize <= 0)) {
                return new FurnaceRecipeInteraction(state, out);
            }
        }
        return null;
    }

    public static class ActiveMeltableEntry extends GenListEntries.CounterListEntry {

        public ActiveMeltableEntry(BlockPos pos) {
            super(pos);
        }

        public boolean isValid(World world, boolean forceLoad) {
            if (!forceLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(getPos()))) return true;
            return getMeltable(world) != null;
        }

        public MeltInteraction getMeltable(World world) {
            return WorldMeltables.getMeltable(world, getPos());
        }

    }

    public static class FurnaceRecipeInteraction implements MeltInteraction {

        private final ItemStack out;
        private final BlockStateCheck.Meta matchInState;

        public FurnaceRecipeInteraction(Block inState, ItemStack outStack) {
            // 1.7.10: Block doesn't have getMetaFromState()
            // Use metadata 0 as default - furnace recipes typically work by item type, not metadata
            this.matchInState = new BlockStateCheck.Meta(inState, 0);
            this.out = outStack;
        }

        @Override
        public boolean isMeltable(World world, BlockPos pos, Block state) {
            return matchInState.isStateValid(state);
        }

        @Nullable
        @Override
        public Block getMeltResultState() {
            return ItemUtils.createBlockState(out);
        }

        @Nonnull
        @Override
        public ItemStack getMeltResultStack() {
            return out.copy();
        }

    }

}
