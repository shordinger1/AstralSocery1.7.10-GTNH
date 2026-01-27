/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCustomFlower
 * Created by HellFirePvP
 * Date: 28.03.2017 / 23:24
 */
public class BlockCustomFlower extends Block implements BlockCustomName, BlockVariants, IShearable {

    public static final PropertyEnum<FlowerType> FLOWER_TYPE = PropertyEnum.create("flower", FlowerType.class);
    private static final AxisAlignedBB box = AxisAlignedBB
        .getBoundingBox(1.5D / 16D, 0, 1.5D / 16D, 14.5D / 16D, 13D / 16D, 14.5D / 16D);
    private static final Random rand = new Random();

    private BlockStateContainer blockState;

    public BlockCustomFlower() {
        super(Material.plants);
        setLightLevel(0.2F);
        setStepSound(Block.soundTypeGrass);
        setTickRandomly(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, FLOWER_TYPE);
    }

//     public IBlockState getDefaultState() {
//         return this.blockState.getBaseState()
//             .withProperty(FLOWER_TYPE, FlowerType.GLOW_FLOWER);
//     }

//     protected void setDefaultState(IBlockState state) {
//         // In 1.7.10, default state is tracked separately
//     }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        FlowerType type = FlowerType.values()[metadata >= FlowerType.values().length ? 0 : metadata];
        switch (type) {
            case GLOW_FLOWER:
                int size = 1;
                for (int i = 0; i < fortune; i++) {
                    size += rand.nextInt(3) + 1;
                }
                for (int i = 0; i < size; i++) {
                    // 1.7.10: Items.glowstone_dust instead of Items.GLOWSTONE_DUST
                    ItemUtils.dropItemNaturally(world, x + 0.5, y + 0.1, z + 0.5, new ItemStack(Items.glowstone_dust));
                }
                break;
            default:
                break;
        }
        return drops;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return canBlockStay(worldIn, x, y, z);
    }

    protected void checkAndDropBlock(World worldIn, int x, int y, int z) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block blockIn) {
        this.checkAndDropBlock(worldIn, x, y, z);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        this.checkAndDropBlock(worldIn, x, y, z);
    }

    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        // 1.7.10: Use ForgeDirection.UP instead of EnumFacing.UP
        return worldIn.isSideSolid(x, y - 1, z, ForgeDirection.UP);
    }

    public boolean canSilkHarvest(World world, int x, int y, int z, int metadata) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public int getRenderType() {
        return 1; // Cross render type for flowers
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FLOWER_TYPE);
    }

//     public IBlockState getStateFromMeta(int meta) {
//         return getDefaultState().withProperty(FLOWER_TYPE, FlowerType.values()[meta]);
//     }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

//     public int getMetaFromState(IBlockState state) {
//         return state.getValue(FLOWER_TYPE)
//             .ordinal();
//     }

    @Override
    public String getIdentifierForMeta(int meta) {
        return getStateName(meta);
    }

//    @Override
//     public List<IBlockState> getValidStates() {
//         List<IBlockState> states = new LinkedList<>();
//         for (FlowerType type : FlowerType.values()) {
//             states.add(getDefaultState().withProperty(FLOWER_TYPE, type));
//         }
//         return states;
//     }

//    @Override
//     public String getStateName(IBlockState state) {
//         return state.getValue(FLOWER_TYPE)
//             .getName();
//     }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, int x, int y, int z,
        int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z)));
        return drops;
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (FlowerType type : FlowerType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        FlowerType type = FlowerType.values()[metadata >= FlowerType.values().length ? 0 : metadata];
        return type.getName();
    }

    public enum FlowerType implements IStringSerializable {

        GLOW_FLOWER;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public int getMeta() {
            return ordinal();
        }

    }

}
