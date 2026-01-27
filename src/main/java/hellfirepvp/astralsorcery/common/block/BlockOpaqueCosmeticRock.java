/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockOpaqueCosmetic
 * Created by HellFirePvP
 * Date: 12.05.2016 / 16:58
 */
public class BlockOpaqueCosmeticRock extends Block implements BlockCustomName {

    public static PropertyEnum<BlockType> BLOCK_TYPE = PropertyEnum.create("blocktype", BlockType.class);

    private BlockStateContainer blockState;

    public BlockOpaqueCosmeticRock() {
        super(Material.rock);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 3);
        setResistance(20.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, BLOCK_TYPE);
    }

//     public IBlockState getDefaultState() {
//         return this.blockState.getBaseState()
//             .withProperty(BLOCK_TYPE, BlockType.NONE);
//     }

//     protected void setDefaultState(IBlockState state) {
//         // In 1.7.10, default state is tracked separately
//     }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    public void getSubBlocks(Item item, CreativeTabs tab, ArrayList<ItemStack> list) {
        for (BlockType bt : BlockType.values()) {
            list.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

//     public IBlockState getStateFromMeta(int meta) {
//         return meta < BlockType.values().length ? getDefaultState().withProperty(BLOCK_TYPE, BlockType.values()[meta])
//             : getDefaultState();
//     }

//     public int getMetaFromState(IBlockState state) {
//         BlockType type = state.getValue(BLOCK_TYPE);
//         return type.ordinal();
//     }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_TYPE);
    }

    @Override
    public int getRenderType() {
        return 0; // Standard render type
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlockType mt = BlockType.values()[meta >= BlockType.values().length ? 0 : meta];
        return mt.getName();
    }

    public static enum BlockType implements IStringSerializable {

        NONE;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
