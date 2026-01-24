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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockObservatory
 * Created by HellFirePvP
 * Date: 26.05.2018 / 14:32
 */
public class BlockObservatory extends BlockContainer {

    public BlockObservatory() {
        super(Material.rock);
        setHardness(3.5F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(Block.soundTypePiston);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = 0; yy <= 3; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    if (!world.isAirBlock(x + xx, y + yy, z + zz) && !world.isAirBlock(x + xx, y + yy, z + zz)) {
                        // 1.7.10: Simplified check, isReplaceable doesn't exist in the same way
                        Block blk = world.getBlock(x + xx, y + yy, z + zz);
                        if (blk != null && !blk.isReplaceable(world, x + xx, y + yy, z + zz)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            TileObservatory to = MiscUtils.getTileAt(world, x, y, z, TileObservatory.class);
            if (to != null && to.isUsable() && !player.isSneaking()) {
                Entity e = to.findRideableObservatoryEntity();
                if (e != null) {
                    if (player.ridingEntity == null) {
                        player.mountEntity(e);
                    } else if (!player.ridingEntity.equals(e)) {
                        return true;
                    }
                    player.openGui(AstralSorcery.instance, CommonProxy.EnumGuiId.OBSERVATORY.ordinal(), world, x, y, z);
                }
            }
        }
        return true;
    }

    @Override
    public int getRenderType() {
        return 3; // EntityBlock renderer
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileObservatory();
    }

}
