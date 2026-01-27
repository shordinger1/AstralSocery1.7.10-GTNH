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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import hellfirepvp.astralsorcery.common.block.network.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.migration.ModelResourceLocation;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeacon;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTreeBeacon
 * Created by HellFirePvP
 * Date: 30.12.2016 / 13:26
 */
public class BlockTreeBeacon extends BlockStarlightNetwork implements BlockDynamicStateMapper.Festive, BlockVariants {

    private static final AxisAlignedBB box = AxisAlignedBB
        .getBoundingBox(3D / 16D, 0D, 3D / 16D, 13D / 16D, 1D, 13D / 16D);

    public BlockTreeBeacon() {
        super(Material.rock);
        setHardness(1.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setStepSound(Block.soundTypeGrass);
        setLightLevel(0.7F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }


    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);

        TileTreeBeacon ttb = MiscUtils.getTileAt(worldIn, x, y, z, TileTreeBeacon.class);
        if (ttb != null && !worldIn.isRemote
            && placer instanceof EntityPlayerMP
            && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) placer)) {
            ttb.setPlacedBy((EntityPlayer) placer);
        }
    }

    @Override
    public List<Block> getValidStates() {
        // BlockTreeBeacon has no variants, return itself once
        List<Block> ret = new LinkedList<>();
        ret.add(this);
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        return "";
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return box;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTreeBeacon();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    @Override
    public Map<Integer, ModelResourceLocation> getModelLocations(Block blockIn) {
        return new java.util.HashMap<>();
    }
}
