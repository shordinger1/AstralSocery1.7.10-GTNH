/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialGateway
 * Created by HellFirePvP
 * Date: 16.04.2017 / 18:46
 */
public class BlockCelestialGateway extends BlockContainer implements BlockStructureObserver {

    private static final AxisAlignedBB box = AxisAlignedBB
        .getBoundingBox(1D / 16D, 0D / 16D, 1D / 16D, 15D / 16D, 1D / 16D, 10D / 15D);

    public BlockCelestialGateway() {
        super(Material.rock);
        setStepSound(Block.soundTypePiston);
        setHardness(4F);
        setResistance(40F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return box;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        TileCelestialGateway gateway = MiscUtils.getTileAt(worldIn, x, y, z, TileCelestialGateway.class);
        if (gateway != null) {
            if (stack.hasDisplayName()) {
                gateway.setGatewayName(stack.getDisplayName());
            }
            if (placer instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) placer)) {
                gateway.setPlacedBy(placer.getUniqueID());
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        super.breakBlock(worldIn, x, y, z, block, meta);

        GatewayCache cache = WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.GATEWAY_DATA);
        cache.removePosition(worldIn, new BlockPos(x, y, z));
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCelestialGateway();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileCelestialGateway();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }
}
