/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.BlockAttunementRelay;
import hellfirepvp.astralsorcery.common.block.BlockCustomName;
import hellfirepvp.astralsorcery.common.block.BlockVariants;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltar
 * Created by HellFirePvP
 * Date: 01.08.2016 / 20:52
 */
public class BlockAltar extends BlockStarlightNetwork
    implements BlockCustomName, BlockVariants, BlockStructureObserver {

    public BlockAltar() {
        super(Material.rock);
        setHardness(3.0F);
        setStepSound(Block.soundTypePiston);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileAltar ta = MiscUtils.getTileAt(worldIn, x, y, z, TileAltar.class);
            if (ta != null) {
                switch (ta.getAltarLevel()) {
                    case DISCOVERY:
                        AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.ALTAR_DISCOVERY, playerIn, worldIn, x, y, z);
                        return true;
                    case ATTUNEMENT:
                        AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.ALTAR_ATTUNEMENT, playerIn, worldIn, x, y, z);
                        return true;
                    case CONSTELLATION_CRAFT:
                        AstralSorcery.proxy
                            .openGui(CommonProxy.EnumGuiId.ALTAR_CONSTELLATION, playerIn, worldIn, x, y, z);
                        return true;
                    case TRAIT_CRAFT:
                        AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.ALTAR_TRAIT, playerIn, worldIn, x, y, z);
                        return true;
                    case BRILLIANCE:
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (AltarType type : AltarType.values()) {
            if (type == AltarType.ALTAR_5) continue;
            list.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        startSearchForRelayUpdate(worldIn, x, y, z);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        super.breakBlock(worldIn, x, y, z, block, meta);
        startSearchForRelayUpdate(worldIn, x, y, z);
    }

    public static void startSearchForRelayUpdate(World world, int x, int y, int z) {
        final hellfirepvp.astralsorcery.common.util.BlockPos pos = new hellfirepvp.astralsorcery.common.util.BlockPos(
            x,
            y,
            z);
        Thread searchThread = new Thread(new Runnable() {

            @Override
            public void run() {
                BlockArray relaysAndAltars = BlockDiscoverer
                    .searchForBlocksAround(world, pos, 16, new BlockStateCheck.Block(BlocksAS.attunementRelay));
                for (Map.Entry<hellfirepvp.astralsorcery.common.util.BlockPos, BlockArray.BlockInformation> entry : relaysAndAltars
                    .getPattern()
                    .entrySet()) {
                    BlockAttunementRelay.startSearchRelayLinkThreadAt(world, entry.getKey(), false);
                }
            }
        });
        searchThread.setName("AttRelay UpdateFinder at " + x + "," + y + "," + z);
        searchThread.start();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        AltarType type = AltarType.byMetadata(meta);
        AxisAlignedBB box = type.getBox();
        if (box != null) {
            this.setBlockBounds(
                (float) box.minX,
                (float) box.minY,
                (float) box.minZ,
                (float) box.maxX,
                (float) box.maxY,
                (float) box.maxZ);
        } else {
            this.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        AltarType type = AltarType.byMetadata(meta);
        AxisAlignedBB box = type.getBox();
        if (box != null) {
            return AxisAlignedBB
                .getBoundingBox(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ);
        }
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        AltarType type = AltarType.byMetadata(meta);
        AxisAlignedBB box = type.getBox();
        if (box != null) {
            return AxisAlignedBB
                .getBoundingBox(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ);
        }
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        AltarType type = AltarType.byMetadata(meta);
        return type.provideTileEntity(worldIn, this);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int lvl = stack.getItemDamage();
        TileAltar ta = MiscUtils.getTileAt(worldIn, x, y, z, TileAltar.class);
        if (ta != null && lvl >= 0 && lvl < TileAltar.AltarLevel.values().length) {
            ta.onPlace(TileAltar.AltarLevel.values()[lvl]);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        // Drop with metadata preserved
        drops.add(new ItemStack(this, 1, metadata));
        return drops;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
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
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        AltarType mt = AltarType.byMetadata(meta);
        return mt.getName();
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new java.util.LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (AltarType type : AltarType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        AltarType type = AltarType.byMetadata(metadata);
        return type.getName();
    }

//    @Override
//     public List<IBlockState> getValidStates() {
//         List<IBlockState> ret = new LinkedList<>();
//         for (AltarType type : AltarType.values()) {
//             ret.add(new IBlockState(this, type.ordinal()));
//         }
//         return ret;
//     }



    public enum AltarType implements IStringSerializable, IVariantTileProvider {

        ALTAR_1(TileAltar.AltarLevel.DISCOVERY),
        ALTAR_2(TileAltar.AltarLevel.ATTUNEMENT),
        ALTAR_3(TileAltar.AltarLevel.CONSTELLATION_CRAFT),
        ALTAR_4(TileAltar.AltarLevel.TRAIT_CRAFT),
        ALTAR_5(TileAltar.AltarLevel.BRILLIANCE);

        private final TileAltar.AltarLevel level;

        AltarType(TileAltar.AltarLevel level) {
            this.level = level;
        }

        @Override
        public TileEntity provideTileEntity(World world, Block state) {
            return new TileAltar(level);
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static AltarType byMetadata(int meta) {
            AltarType[] values = values();
            return meta >= 0 && meta < values.length ? values[meta] : values[0];
        }

        @Override
        public String toString() {
            return getName();
        }

        public AxisAlignedBB getBox() {
            switch (this) {
                case ALTAR_1:
                    return AxisAlignedBB.getBoundingBox(0F, 0F, 0F, 1F, 1F, 1F);
                case ALTAR_2:
                    return AxisAlignedBB.getBoundingBox(0F, 0F, 0F, 1F, 1F, 1F);
                case ALTAR_3:
                    return AxisAlignedBB.getBoundingBox(0F, 0F, 0F, 1F, 1F, 1F);
                case ALTAR_4:
                    return null;
                case ALTAR_5:
                    return null;
            }
            return null;
        }
    }
}
