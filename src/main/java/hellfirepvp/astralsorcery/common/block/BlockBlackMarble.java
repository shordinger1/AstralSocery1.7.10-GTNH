/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IBlockState;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBlackMarble
 * Created by HellFirePvP
 * Date: 21.10.2016 / 23:02
 */
public class BlockBlackMarble extends Block implements BlockCustomName, BlockVariants {

    public static PropertyEnum<BlackMarbleBlockType> BLACK_MARBLE_TYPE = PropertyEnum
        .create("marbletype", BlackMarbleBlockType.class);

    private BlockStateContainer blockState;

    public BlockBlackMarble() {
        super(Material.rock);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(Block.soundTypePiston);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, BLACK_MARBLE_TYPE);
    }

    public IBlockState getDefaultState() {
        return this.blockState.getBaseState()
            .withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.RAW);
    }

    protected void setDefaultState(IBlockState state) {
        // In 1.7.10, default state is tracked separately
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlackMarbleBlockType t : BlackMarbleBlockType.values()) {
            if (!t.obtainableInCreative()) continue;
            list.add(new ItemStack(this, 1, t.ordinal()));
        }
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, int x, int y, int z) {
        if (state.getValue(BLACK_MARBLE_TYPE)
            .isPillar()) {
            Block blockUp = worldIn.getBlock(x, y + 1, z);
            IBlockState stateUp = new IBlockState(blockUp, worldIn.getBlockMetadata(x, y + 1, z));
            boolean top = false;
            if (blockUp instanceof BlockBlackMarble && stateUp.getValue(BLACK_MARBLE_TYPE)
                .isPillar()) {
                top = true;
            }
            Block blockDown = worldIn.getBlock(x, y - 1, z);
            IBlockState stateDown = new IBlockState(blockDown, worldIn.getBlockMetadata(x, y - 1, z));
            boolean down = false;
            if (blockDown instanceof BlockBlackMarble && stateDown.getValue(BLACK_MARBLE_TYPE)
                .isPillar()) {
                down = true;
            }
            if (top && down) {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR);
            } else if (top) {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR_BOTTOM);
            } else if (down) {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR_TOP);
            } else {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR);
            }
        }
        return state;
    }

    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    public int getLightOpacity(IBlockState state, IBlockAccess world, int x, int y, int z) {
        BlackMarbleBlockType marbleType = state.getValue(BLACK_MARBLE_TYPE);
        if (marbleType == BlackMarbleBlockType.PILLAR_TOP || marbleType == BlackMarbleBlockType.PILLAR
            || marbleType == BlackMarbleBlockType.PILLAR_BOTTOM) {
            return 0;
        }
        return super.getLightOpacity();
    }

    @Override
    public boolean isOpaqueCube() {
        // 1.7.10: No state parameter, always return true for base implementation
        // Pillar variations are handled by the block state wrapper
        return true;
    }

    public boolean isFullCube() {
        // 1.7.10: No state parameter, return true for base implementation
        return true;
    }

    public boolean isFullBlock() {
        // 1.7.10: No state parameter, return true for base implementation
        return true;
    }

    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, int x, int y, int z, EnumFacing face) {
        BlackMarbleBlockType marbleType = state.getValue(BLACK_MARBLE_TYPE);

        // Get offset for EnumFacing
        int dx = 0, dy = 0, dz = 0;
        switch (face) {
            case DOWN:
                dy = -1;
                break;
            case UP:
                dy = 1;
                break;
            case NORTH:
                dz = -1;
                break;
            case SOUTH:
                dz = 1;
                break;
            case WEST:
                dx = -1;
                break;
            case EAST:
                dx = 1;
                break;
        }

        Block other = world.getBlock(x + dx, y + dy, z + dz);
        if (marbleType == BlackMarbleBlockType.PILLAR) {
            return false;
        }
        if (marbleType == BlackMarbleBlockType.PILLAR_TOP) {
            return face == EnumFacing.UP;
        }
        if (marbleType == BlackMarbleBlockType.PILLAR_BOTTOM) {
            return face == EnumFacing.DOWN;
        }
        return true;
    }

    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlackMarbleBlockType mt = getStateFromMeta(meta).getValue(BLACK_MARBLE_TYPE);
        return mt.getName();
    }

    public int getMetaFromState(IBlockState state) {
        BlackMarbleBlockType type = state.getValue(BLACK_MARBLE_TYPE);
        return type.getMeta();
    }

    public IBlockState getStateFromMeta(int meta) {
        return meta < BlackMarbleBlockType.values().length
            ? getDefaultState().withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.values()[meta])
            : getDefaultState();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLACK_MARBLE_TYPE);
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (BlackMarbleBlockType type : BlackMarbleBlockType.values()) {
            ret.add(getDefaultState().withProperty(BLACK_MARBLE_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(BLACK_MARBLE_TYPE)
            .getName();
    }

    public static enum BlackMarbleBlockType implements IStringSerializable {

        RAW(0),
        BRICKS(1),
        PILLAR(2),
        ARCH(3),
        CHISELED(4),
        ENGRAVED(5),
        RUNED(6),

        PILLAR_TOP(2),
        PILLAR_BOTTOM(2);

        private final int meta;

        private BlackMarbleBlockType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockBlackMarble, 1, meta);
        }

        public IBlockState asBlock() {
            return new IBlockState(BlocksAS.blockBlackMarble, meta);
        }

        public boolean isPillar() {
            return this == PILLAR_BOTTOM || this == PILLAR || this == PILLAR_TOP;
        }

        public boolean obtainableInCreative() {
            return this != PILLAR_TOP && this != PILLAR_BOTTOM;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
