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

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.block.network.IBlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.PropertyInteger;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialCrystals
 * Created by HellFirePvP
 * Date: 14.09.2016 / 23:42
 */
public class BlockCelestialCrystals extends BlockContainer
    implements IBlockStarlightRecipient, BlockCustomName, BlockVariants {

    private static final Random rand = new Random();

    public static AxisAlignedBB bbStage0 = AxisAlignedBB.getBoundingBox(0.1, 0.0, 0.1, 0.9, 0.3, 0.9);
    public static AxisAlignedBB bbStage1 = AxisAlignedBB.getBoundingBox(0.1, 0.0, 0.1, 0.9, 0.4, 0.9);
    public static AxisAlignedBB bbStage2 = AxisAlignedBB.getBoundingBox(0.1, 0.0, 0.1, 0.9, 0.5, 0.9);
    public static AxisAlignedBB bbStage3 = AxisAlignedBB.getBoundingBox(0.1, 0.0, 0.1, 0.9, 0.6, 0.9);
    public static AxisAlignedBB bbStage4 = AxisAlignedBB.getBoundingBox(0.1, 0.0, 0.1, 0.9, 0.7, 0.9);

    public static PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4);

    private BlockStateContainer blockState;

    public BlockCelestialCrystals() {
        super(Material.rock);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(30.0F);
        setLightLevel(0.4F);
        setStepSound(Block.soundTypePiston);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, STAGE);
    }

//     public IBlockState getDefaultState() {
//         return this.blockState.getBaseState()
//             .withProperty(STAGE, 0);
//     }

//     protected void setDefaultState(IBlockState state) {
//         // In 1.7.10, default state is tracked separately
//     }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        int stage = metadata >= 0 && metadata <= 4 ? metadata : 0;
        switch (stage) {
            case 0:
                this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.3F, 0.9F);
                break;
            case 1:
                this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.4F, 0.9F);
                break;
            case 2:
                this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.5F, 0.9F);
                break;
            case 3:
                this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.6F, 0.9F);
                break;
            case 4:
                this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.7F, 0.9F);
                break;
            default:
                this.setBlockBounds(0, 0, 0, 1, 1, 1);
                break;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        int stage = metadata >= 0 && metadata <= 4 ? metadata : 0;
        switch (stage) {
            case 0:
                return AxisAlignedBB.getBoundingBox(x + 0.1, y, z + 0.1, x + 0.9, y + 0.3, z + 0.9);
            case 1:
                return AxisAlignedBB.getBoundingBox(x + 0.1, y, z + 0.1, x + 0.9, y + 0.4, z + 0.9);
            case 2:
                return AxisAlignedBB.getBoundingBox(x + 0.1, y, z + 0.1, x + 0.9, y + 0.5, z + 0.9);
            case 3:
                return AxisAlignedBB.getBoundingBox(x + 0.1, y, z + 0.1, x + 0.9, y + 0.6, z + 0.9);
            case 4:
                return AxisAlignedBB.getBoundingBox(x + 0.1, y, z + 0.1, x + 0.9, y + 0.7, z + 0.9);
            default:
                return super.getCollisionBoundingBoxFromPool(world, x, y, z);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 5; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        boolean replaceable = super.canPlaceBlockAt(worldIn, x, y, z);
        if (replaceable) {
            if (!worldIn.isSideSolid(x, y - 1, z, ForgeDirection.UP)) replaceable = false;
        }
        return replaceable;
    }

    public boolean isTopSolid(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(ItemCraftingComponent.MetaType.STARDUST.asStack());
        int stage = metadata >= 0 && metadata <= 4 ? metadata : 0;
        switch (stage) {
            case 4:
                if (checkSafety(world, x, y, z)) {
                    if (fortune > 0 || rand.nextInt(2) == 0) {
                        drops.add(ItemCraftingComponent.MetaType.STARDUST.asStack());
                    }
                    Block down = world.getBlock(x, y - 1, z);
                    boolean hasStarmetal = false;
                    if (down instanceof BlockCustomOre) {
                        int downMeta = world.getBlockMetadata(x, y - 1, z);
                        hasStarmetal = downMeta == BlockCustomOre.OreType.STARMETAL.ordinal();
                    }

                    ItemStack celCrystal = ItemRockCrystalBase.createRandomCelestialCrystal();
                    if (hasStarmetal) {
                        CrystalProperties prop = CrystalProperties.getCrystalProperties(celCrystal);
                        int missing = 100 - prop.getPurity();
                        if (missing > 0) {
                            prop = new CrystalProperties(
                                prop.getSize(),
                                WrapMathHelper.clamp(prop.getPurity() + rand.nextInt(missing) + 1, 0, 100),
                                prop.getCollectiveCapability(),
                                prop.getFracturation(),
                                prop.getSizeOverride());
                            CrystalProperties.applyCrystalProperties(celCrystal, prop);
                        }
                    }
                    drops.add(celCrystal);
                    if (hasStarmetal && rand.nextInt(3) == 0) {
                        drops.add(ItemRockCrystalBase.createRandomCelestialCrystal());
                    }
                }
                break;
            default:
                break;
        }
        return drops;
    }

    private boolean checkSafety(World world, int x, int y, int z) {
        EntityPlayer player = world.getClosestPlayer(x, y, z, 10);
        return player != null && player.getDistanceSq(x, y, z) < 100;
    }

    @Override
    public void receiveStarlight(World world, Random rand, BlockPos pos, IWeakConstellation starlightType,
        double amount) {
        TileCelestialCrystals tile = MiscUtils
            .getTileAt(world, pos.getX(), pos.getY(), pos.getZ(), TileCelestialCrystals.class);
        if (tile != null) {
            tile.tryGrowth(0.5);
            Block down = world.getBlock(pos.getX(), pos.getY() - 1, pos.getZ());
            if (down instanceof BlockCustomOre) {
                int downMeta = world.getBlockMetadata(pos.getX(), pos.getY() - 1, pos.getZ());
                if (downMeta == BlockCustomOre.OreType.STARMETAL.ordinal()) {
                    tile.tryGrowth(0.3);
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockMetadataWithNotify(x, y, z, stack.getItemDamage(), 2);
    }

//     public int getMetaFromState(IBlockState state) {
//         return state.getValue(STAGE);
//     }

//     public IBlockState getStateFromMeta(int meta) {
//         return getDefaultState().withProperty(STAGE, meta);
//     }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block blockIn) {
        if (!worldIn.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
            dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            super.breakBlock(worldIn, x, y, z, worldIn.getBlock(x, y, z), worldIn.getBlockMetadata(x, y, z));
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        TileCelestialCrystals te = MiscUtils.getTileAt(worldIn, x, y, z, TileCelestialCrystals.class);
        if (te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(
                PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_BURST,
                x,
                y,
                z);
            // 1.7.10: pointFromPos takes (world, ChunkCoordinates, distance)
            PacketChannel.CHANNEL
                .sendToAllAround(event, PacketChannel.pointFromPos(worldIn, new BlockPos(x, y, z), 32));
        }
        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileCelestialCrystals();
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        return "stage_" + meta;
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, this block has 5 growth stages (0-4)
        // Return the block itself once for each variant type
        for (int stage = 0; stage <= 4; stage++) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        return "stage_" + metadata;
    }
}
