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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockInfusedWood
 * Created by HellFirePvP
 * Date: 05.06.2018 / 16:15
 */
public class BlockInfusedWood extends Block implements BlockCustomName, BlockVariants {

    public static final PropertyEnum<WoodType> WOOD_TYPE = PropertyEnum.create("woodtype", WoodType.class);

    private BlockStateContainer blockState;

    public BlockInfusedWood() {
        super(Material.wood);
        setHardness(1.0F);
        setHarvestLevel("axe", 0);
        setResistance(3.0F);
        setStepSound(Block.soundTypeWood);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, WOOD_TYPE);
    }

//     public IBlockState getDefaultState() {
//         return this.blockState.getBaseState()
//             .withProperty(WOOD_TYPE, WoodType.RAW);
//     }

//     protected void setDefaultState(IBlockState state) {
//         // In 1.7.10, default state is tracked separately
//     }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (WoodType t : WoodType.values()) {
            if (!t.obtainableInCreative()) continue;
            list.add(new ItemStack(this, 1, t.getMeta()));
        }
    }

    @Override
    public int getRenderType() {
        return 0; // Standard model render type
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Make non-opaque for columns
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(net.minecraft.world.World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        WoodType type = WoodType.values()[meta >= WoodType.values().length ? 0 : meta];
        switch (type) {
            case COLUMN:
                return AxisAlignedBB.getBoundingBox(0.25, 0, 0.25, 0.75, 1, 0.75);
            case COLUMN_TOP:
            case COLUMN_BOTTOM:
                return AxisAlignedBB.getBoundingBox(0.125, 0, 0.125, 0.875, 1, 0.875);
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        WoodType type = WoodType.values()[meta >= WoodType.values().length ? 0 : meta];
        switch (type) {
            case COLUMN:
                this.setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 1F, 0.75F);
                break;
            case COLUMN_TOP:
            case COLUMN_BOTTOM:
                this.setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
                break;
            default:
                this.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
                break;
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        WoodType woodType = WoodType.values()[meta >= WoodType.values().length ? 0 : meta];
        if (woodType == WoodType.COLUMN_TOP || woodType == WoodType.COLUMN || woodType == WoodType.COLUMN_BOTTOM) {
            return 0;
        }
        return super.getLightOpacity(world, x, y, z);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        WoodType mt = WoodType.values()[meta >= WoodType.values().length ? 0 : meta];
        return mt.getName();
    }

//     public int getMetaFromState(IBlockState state) {
//         return state.getValue(WOOD_TYPE)
//             .getMeta();
//     }

//     public IBlockState getStateFromMeta(int meta) {
//         return getDefaultState()
//             .withProperty(WOOD_TYPE, WoodType.values()[meta >= WoodType.values().length ? 0 : meta]);
//     }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, WOOD_TYPE);
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (WoodType type : WoodType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        WoodType type = WoodType.values()[metadata >= WoodType.values().length ? 0 : metadata];
        return type.getName();
    }

    public enum WoodType implements IStringSerializable {

        RAW(0),
        PLANKS(1),
        COLUMN(2),
        ARCH(3),
        ENGRAVED(4),
        ENRICHED(5),
        INFUSED(6),

        COLUMN_TOP(2),
        COLUMN_BOTTOM(2);

        private final int meta;

        private WoodType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockInfusedWood, 1, meta);
        }

        public boolean isColumn() {
            return this == COLUMN_BOTTOM || this == COLUMN || this == COLUMN_TOP;
        }

        public boolean obtainableInCreative() {
            return this != COLUMN_TOP && this != COLUMN_BOTTOM;
        }

        public int getMeta() {
            return meta;
        }

        public String getName() {
            return name().toLowerCase();
        }
    }

}
