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
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import com.google.common.collect.Maps;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IBlockState;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.ModelResourceLocation;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarble
 * Created by HellFirePvP
 * Date: 22.05.2016 / 16:13
 */
public class BlockMarble extends Block implements BlockCustomName, BlockVariants, BlockDynamicStateMapper.Festive {

    // private static final int RAND_MOSS_CHANCE = 10;

    public static PropertyEnum<MarbleBlockType> MARBLE_TYPE = PropertyEnum.create("marbletype", MarbleBlockType.class);

    private BlockStateContainer blockState;

    public BlockMarble() {
        super(Material.rock);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(Block.soundTypeStone);
        // setTickRandomly(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, MARBLE_TYPE);
    }

    public IBlockState getDefaultState() {
        return this.blockState.getBaseState()
            .withProperty(MARBLE_TYPE, MarbleBlockType.RAW);
    }

    protected void setDefaultState(IBlockState state) {
        // In 1.7.10, default state is tracked separately
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MarbleBlockType t : MarbleBlockType.values()) {
            if (!t.obtainableInCreative()) continue;
            list.add(new ItemStack(this, 1, t.getMeta()));
        }
    }

    /*
     * @Override
     * public void updateTick(World worldIn, BlockPos pos, Block state, Random rand) {
     * if (!worldIn.isRemote && worldIn.isRaining() && rand.nextInt(RAND_MOSS_CHANCE) == 0) {
     * MarbleBlockType type = state.getValue(MARBLE_TYPE);
     * if (type.canTurnMossy() && worldIn.isRainingAt(pos)) {
     * int newMeta = type.getMossyEquivalent().ordinal();
     * worldIn.setBlock(pos.getX(), pos.getY(), pos.getZ(), state, newMeta, 3);
     * }
     * }
     * }
     */

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, int x, int y, int z) {
        // return super.getActualState(state, worldIn, pos);
        if (state.getValue(MARBLE_TYPE)
            .isPillar()) {
            Block blockUp = worldIn.getBlock(x, y + 1, z);
            IBlockState stateUp = new IBlockState(blockUp, worldIn.getBlockMetadata(x, y + 1, z));
            boolean top = false;
            if (blockUp instanceof BlockMarble && stateUp.getValue(MARBLE_TYPE)
                .isPillar()) {
                top = true;
            }
            Block blockDown = worldIn.getBlock(x, y - 1, z);
            IBlockState stateDown = new IBlockState(blockDown, worldIn.getBlockMetadata(x, y - 1, z));
            boolean down = false;
            if (blockDown instanceof BlockMarble && stateDown.getValue(MARBLE_TYPE)
                .isPillar()) {
                down = true;
            }
            if (top && down) {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR);
            } else if (top) {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR_BOTTOM);
            } else if (down) {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR_TOP);
            } else {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR);
            }
        }
        return state;
    }

    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    public int getLightOpacity(IBlockState state, IBlockAccess world, int x, int y, int z) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        if (marbleType == MarbleBlockType.PILLAR_TOP || marbleType == MarbleBlockType.PILLAR
            || marbleType == MarbleBlockType.PILLAR_BOTTOM) {
            return 0;
        }
        return super.getLightOpacity();
    }

    // 1.7.10: isOpaqueCube() doesn't take parameters, can't be state-aware
    // Use default behavior - marble is generally opaque except pillars
    @Override
    public boolean isOpaqueCube() {
        return true; // Default to opaque
    }

    public boolean isFullCube(IBlockState state) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        return marbleType != MarbleBlockType.PILLAR && marbleType != MarbleBlockType.PILLAR_BOTTOM
            && marbleType != MarbleBlockType.PILLAR_TOP;
    }

    public boolean isFullBlock(IBlockState state) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        return marbleType != MarbleBlockType.PILLAR && marbleType != MarbleBlockType.PILLAR_BOTTOM
            && marbleType != MarbleBlockType.PILLAR_TOP;
    }

    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, int x, int y, int z, EnumFacing face) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);

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
        if (MiscUtils.isFluidBlock(other)
            && (marbleType == MarbleBlockType.PILLAR || marbleType == MarbleBlockType.PILLAR_BOTTOM
                || marbleType == MarbleBlockType.PILLAR_TOP)) {
            return false;
        }
        if (marbleType == MarbleBlockType.PILLAR_TOP) {
            return face == EnumFacing.UP;
        }
        if (marbleType == MarbleBlockType.PILLAR_BOTTOM) {
            return face == EnumFacing.DOWN;
        }
        return isOpaqueCube(); // 1.7.10: isOpaqueCube() doesn't take parameters
    }

    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        MarbleBlockType mt = getStateFromMeta(meta).getValue(MARBLE_TYPE);
        return mt.getName();
    }

    public int getMetaFromState(IBlockState state) {
        MarbleBlockType type = state.getValue(MARBLE_TYPE);
        return type.getMeta();
    }

    public IBlockState getStateFromMeta(int meta) {
        return meta < MarbleBlockType.values().length
            ? getDefaultState().withProperty(MARBLE_TYPE, MarbleBlockType.values()[meta])
            : getDefaultState();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MARBLE_TYPE);
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (MarbleBlockType type : MarbleBlockType.values()) {
            ret.add(getDefaultState().withProperty(MARBLE_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(MARBLE_TYPE)
            .getName() + (handleRegisterStateMapper() ? "_festive" : "");
    }

    @Override
    public Map<IBlockState, ModelResourceLocation> getModelLocations(Block blockIn) {
        // Use the block's unlocalizedName as the resource location
        String blockName = blockIn.getUnlocalizedName()
            .replace("tile.", "");
        ResourceLocation rl = new ResourceLocation("astralsorcery", blockName + "_festive");
        Map<IBlockState, ModelResourceLocation> out = Maps.newHashMap();
        for (IBlockState state : getValidStates()) {
            out.put(state, new ModelResourceLocation(rl, getPropertyString(state.getProperties())));
        }
        return out;
    }

    public static enum MarbleBlockType implements IStringSerializable {

        RAW(0),
        BRICKS(1),
        PILLAR(2),
        ARCH(3),
        CHISELED(4),
        ENGRAVED(5),
        RUNED(6),

        PILLAR_TOP(2),
        PILLAR_BOTTOM(2);

        // BRICKS_MOSSY,
        // PILLAR_MOSSY,
        // CRACK_MOSSY;

        private final int meta;

        private MarbleBlockType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockMarble, 1, meta);
        }

        public IBlockState asBlock() {
            return new IBlockState(BlocksAS.blockMarble, meta);
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

        /*
         * public boolean canTurnMossy() {
         * return this == BRICKS || this == PILLAR || this == CRACKED;
         * }
         * public MarbleBlockType getMossyEquivalent() {
         * if(!canTurnMossy()) return null;
         * switch (this) {
         * case BRICKS:
         * return BRICKS_MOSSY;
         * case PILLAR:
         * return PILLAR_MOSSY;
         * case CRACKED:
         * return CRACK_MOSSY;
         * }
         * return null;
         * }
         */
    }

}
