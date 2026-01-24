/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.ParticleManager;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAttunementAltar
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:20
 */
public class BlockAttunementAltar extends BlockContainer implements BlockStructureObserver {

    public static final AxisAlignedBB boxAttunementAlar = AxisAlignedBB
        .getBoundingBox(-2D / 16D, 0, -2D / 16D, 18D / 16D, 6D / 16D, 18D / 16D);

    public BlockAttunementAltar() {
        super(Material.rock);
        setHardness(3.0F);
        setStepSound(Block.soundTypePiston);
        setResistance(25.0F);
        setLightLevel(0.8F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    // 1.7.10: isTopSolid doesn't exist in BlockContainer, remove @Override
    public boolean isTopSolid(Block block) {
        return false;
    }

    // 1.7.10: addDestroyEffects doesn't override in 1.7.10, remove @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, ParticleManager manager) {
        // 1.7.10: playBlockBreakParticles takes BlockPos and Block
        RenderingUtils
            .playBlockBreakParticles(new hellfirepvp.astralsorcery.common.util.BlockPos(x, y, z), BlocksAS.blockMarble);
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return boxAttunementAlar;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return boxAttunementAlar;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(-2F / 16F, 0, -2F / 16F, 18F / 16F, 6F / 16F, 18F / 16F);
    }

    /*
     * @Override
     * public void onBlockPlacedBy(World worldIn, BlockPos pos, Block state, EntityLivingBase placer, ItemStack
     * stack) {
     * //worldIn.setBlockState((x, y + 1, z),
     * BlocksAS.blockStructural.getDefaultState().withProperty(BlockStructural.BLOCK_TYPE,
     * BlockStructural.BlockType.ATTUNEMENT_ALTAR_STRUCT));
     * TileAttunementAltar te = MiscUtils.getTileAt(worldIn, x, y, z, TileAttunementAltar.class, true);
     * if(te != null && !worldIn.isRemote) {
     * if(placer != null && placer instanceof EntityPlayer) {
     * te.setOwner(placer.getUniqueID());
     * }
     * }
     * super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
     * }
     */

    /*
     * @Override
     * public void neighborChanged(Block state, World world, BlockPos pos, Block neighbor) {
     * if(getWorld().isAirBlock((x, y + 1, z))) {
     * world.setBlockToAir(pos);
     * }
     * super.neighborChanged(state, world, pos, neighbor);
     * }
     * @Override
     * public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
     * if(!(world instanceof World)) {
     * super.onNeighborChange(world, pos, neighbor);
     * return;
     * }
     * if(getWorld().isAirBlock((x, y + 1, z))) {
     * ((World) world).setBlockToAir(pos);
     * }
     * }
     */

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileAttunementAltar();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAttunementAltar();
    }

}
