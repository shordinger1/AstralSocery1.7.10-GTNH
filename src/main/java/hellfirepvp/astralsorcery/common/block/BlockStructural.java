/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IBlockState;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.tile.TileStructuralConnector;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructural
 * Created by HellFirePvP
 * Date: 30.07.2016 / 21:50
 */
public class BlockStructural extends BlockContainer implements BlockCustomName, BlockVariants {

    private static boolean effectLoop = false;
    public static PropertyEnum<BlockType> BLOCK_TYPE = PropertyEnum.create("blocktype", BlockType.class);

    private BlockStateContainer blockState;

    public BlockStructural() {
        super(Material.rock);
        setBlockUnbreakable();
        setStepSound(Block.soundTypeGlass);
        this.blockState = new BlockStateContainer(this, BLOCK_TYPE);
    }

    public IBlockState getDefaultState() {
        return this.blockState.getBaseState()
            .withProperty(BLOCK_TYPE, BlockType.TELESCOPE_STRUCT);
    }

    protected void setDefaultState(IBlockState state) {
        // In 1.7.10, default state is tracked separately
    }

    @Override
    public String getHarvestTool(int metadata) {
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        return type.getSupportedState()
            .getHarvestTool(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer manager) {
        if (effectLoop) return false;
        effectLoop = true;
        int metadata = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                Block blockBelow = world.getBlock(x, y - 1, z);
                Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(x, y - 1, z, blockBelow, 0);
                effectLoop = false;
                return true;
            default:
                break;
        }
        effectLoop = false;
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockAccess world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                return AxisAlignedBB.getBoundingBox(x + 0, y - 1, z + 0, x + 1, y + 1, z + 1);
            default:
                break;
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                this.setBlockBounds(0, -1, 0, 1, 1, 1);
                break;
            default:
                this.setBlockBounds(0, 0, 0, 1, 1, 1);
                break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (BlockType bt : BlockType.values()) {
            list.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        int metadata = worldIn.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .onBlockActivated(worldIn, x, y - 1, z, playerIn, side, hitX, hitY, hitZ);
            default:
                break;
        }
        return super.onBlockActivated(worldIn, x, y, z, playerIn, side, hitX, hitY, hitZ);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                drops.add(BlockMachine.MachineType.TELESCOPE.asStack());
                break;
            default:
                break;
        }
        return drops;
    }

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        int metadata = worldIn.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getBlockHardness(worldIn, x, y - 1, z);
            default:
                break;
        }
        return super.getBlockHardness(worldIn, x, y, z);
    }

    @Override
    public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        int metadata = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getExplosionResistance(exploder, world, x, y - 1, z, explosionX, explosionY, explosionZ);
            default:
                break;
        }
        return super.getExplosionResistance(exploder, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getPickBlock(target, world, x, y - 1, z);
            default:
                break;
        }
        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockIn) {
        int metadata = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                if (world.isAirBlock(x, y - 1, z)) {
                    world.setBlockToAir(x, y, z);
                }
                break;
            default:
                break;
        }
    }

    public int getMetaFromState(IBlockState state) {
        BlockType type = state.getValue(BLOCK_TYPE);
        return type.ordinal();
    }

    public IBlockState getStateFromMeta(int meta) {
        return meta < BlockType.values().length ? getDefaultState().withProperty(BLOCK_TYPE, BlockType.values()[meta])
            : getDefaultState();
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_TYPE);
    }

    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(int meta) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        int metadata = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.values()[metadata >= BlockType.values().length ? 0 : metadata];
        switch (type) {
            case TELESCOPE_STRUCT:
                // 1.7.10: isSideSolid requires ForgeDirection parameter
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .isSideSolid(null, 0, 0, 0, ForgeDirection.UP);
            default:
                break;
        }
        return false;
    }

    @Override
    public int getRenderType() {
        return -1; // Invisible
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlockType mt = getStateFromMeta(meta).getValue(BLOCK_TYPE);
        return mt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> li = new ArrayList<>(BlockType.values().length);
        for (BlockType bt : BlockType.values()) {
            li.add(getDefaultState().withProperty(BLOCK_TYPE, bt));
        }
        return li;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(BLOCK_TYPE)
            .getName();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStructuralConnector();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    public static enum BlockType implements IStringSerializable {

        TELESCOPE_STRUCT(BlocksAS.blockMachine);

        private final Block supportedBlock;

        private BlockType(Block supportedBlock) {
            this.supportedBlock = supportedBlock;
        }

        public Block getSupportedState() {
            return supportedBlock;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
