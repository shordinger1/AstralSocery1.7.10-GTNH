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

    // In 1.7.10, default state is represented by default metadata (0)
    // MarbleBlockType.RAW has meta 0, so no special handling needed

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

    // In 1.7.10, getActualState doesn't exist - pillar state is determined by metadata
    // PILLAR (2), PILLAR_TOP (2), PILLAR_BOTTOM (2) all use meta 2
    // This logic would need to be handled during block placement or updates

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    // In 1.7.10, getLightOpacity() doesn't take state parameter
    // Use default behavior from Block class

    // 1.7.10: isOpaqueCube() doesn't take parameters, can't be state-aware
    // Use default behavior - marble is generally opaque except pillars
    @Override
    public boolean isOpaqueCube() {
        return true; // Default to opaque
    }

    // In 1.7.10, isFullCube() doesn't take parameters
    // Use default behavior

    // In 1.7.10, isFullBlock() doesn't exist
    // Use default behavior

    public boolean doesSideBlockRendering(int metadata, IBlockAccess world, int x, int y, int z, EnumFacing face) {
        MarbleBlockType marbleType = MarbleBlockType.values()[metadata >= MarbleBlockType.values().length ? 0
            : metadata];

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

    // In 1.7.10, isTopSolid() doesn't exist or take state parameter
    // Use default behavior

    @Override
    public String getIdentifierForMeta(int meta) {
        MarbleBlockType mt = MarbleBlockType.values()[meta >= MarbleBlockType.values().length ? 0 : meta];
        return mt.getName();
    }

    // In 1.7.10, getMetaFromState and getStateFromMeta don't exist
    // Metadata is handled directly

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (MarbleBlockType type : MarbleBlockType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        MarbleBlockType type = MarbleBlockType.values()[metadata >= MarbleBlockType.values().length ? 0 : metadata];
        return type.getName() + (handleRegisterStateMapper() ? "_festive" : "");
    }

    @Override
    public Map<Integer, ModelResourceLocation> getModelLocations(Block blockIn) {
        // Use the block's unlocalizedName as the resource location
        String blockName = blockIn.getUnlocalizedName()
            .replace("tile.", "");
        ResourceLocation rl = new ResourceLocation("astralsorcery", blockName + "_festive");
        Map<Integer, ModelResourceLocation> out = Maps.newHashMap();
        for (MarbleBlockType type : MarbleBlockType.values()) {
            // For 1.7.10, map metadata to model locations
            out.put(type.getMeta(), new ModelResourceLocation(rl, "marbletype=" + type.getName()));
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

        // In 1.7.10, asBlock() just returns the block instance
        // Metadata is handled separately
        public Block asBlock() {
            return BlocksAS.blockMarble;
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
