/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.geolosys;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGeolosysSampleCluster
 * Created by HellFirePvP
 * Date: 03.10.2017 / 17:21
 */
public class BlockGeolosysSampleCluster extends BlockContainer {

    public static AxisAlignedBB bbStage2 = AxisAlignedBB.getBoundingBox(0.1, 0.0, 0.1, 0.9, 0.5, 0.9);

    public BlockGeolosysSampleCluster() {
        super(Material.rock);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(30.0F);
        setLightLevel(0F);
        setStepSound(Block.soundTypePiston);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, ArrayList<ItemStack> items) {}

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return bbStage2;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return bbStage2;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.1F, 0F, 0.1F, 0.9F, 0.5F, 0.9F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, ParticleManager manager) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        boolean replaceable = super.canPlaceBlockAt(worldIn, x, y, z);
        if (replaceable) {
            if (!worldIn.isSideSolid(x, y - 1, z, 1)) replaceable = false; // UP = 1
        }
        return replaceable;
    }

    @Override
    public boolean isTopSolid(Block block) {
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return super.getPickBlock(target, world, x, y, z); // Waila fix. wtf. why waila. why.
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = Lists.newLinkedList();
        drops.add(ItemCraftingComponent.MetaType.STARDUST.asStack());
        return drops;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isSideSolid(x, y - 1, z, 1)) { // UP = 1
            dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        TileGeolosysSampleCluster te = MiscUtils.getTileAt(worldIn, x, y, z, TileGeolosysSampleCluster.class, true);
        if (te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(
                PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_BURST,
                x,
                y,
                z);
            PacketChannel.CHANNEL.sendToAllAround(event, PacketChannel.pointFromPos(worldIn, x, y, z, 32));
        }
        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 3; // EntityBlock renderer
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
        return new TileGeolosysSampleCluster();
    }

}
