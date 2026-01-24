/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.tile.TileTranslucent;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTranslucentBlock
 * Created by HellFirePvP
 * Date: 17.01.2017 / 03:44
 */
public class BlockTranslucentBlock extends BlockContainer {

    public BlockTranslucentBlock() {
        super(Material.rock);
        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(0.6F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random rand) {
        if (rand.nextInt(30) == 0) {
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(x + rand.nextFloat(), y + rand.nextFloat(), z + rand.nextFloat());
            p.motion(0, 0, 0);
            p.scale(0.45F)
                .setColor(Color.WHITE)
                .setMaxAge(65);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        Block fst = getFakedStateTile(worldIn, x, y, z);
        if (fst != null) {
            try {
                return fst.onBlockActivated(worldIn, x, y, z, playerIn, side, hitX, hitY, hitZ);
            } catch (Exception exc) {}
        }
        return super.onBlockActivated(worldIn, x, y, z, playerIn, side, hitX, hitY, hitZ);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    private Block getFakedStateTile(World world, int x, int y, int z) {
        TileTranslucent tt = MiscUtils.getTileAt(world, x, y, z, TileTranslucent.class);
        if (tt == null) return null;
        return tt.getFakedState();
    }

    @Override
    public int getRenderType() {
        return -1; // Invisible in 1.7.10
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        Block fst = getFakedStateTile(world, x, y, z);
        try {
            if (fst != null) {
                return fst.getPickBlock(target, world, x, y, z);
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTranslucent();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }
}
