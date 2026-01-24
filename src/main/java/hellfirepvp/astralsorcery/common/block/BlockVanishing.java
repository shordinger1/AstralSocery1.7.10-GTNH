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

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.tile.TileVanishing;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockVanishing
 * Created by HellFirePvP
 * Date: 30.07.2017 / 17:13
 */
public class BlockVanishing extends BlockContainer {

    public BlockVanishing() {
        super(Material.rock);
        setBlockUnbreakable();
        setStepSound(Block.soundTypeMetal);
        setCreativeTab(null);
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, ArrayList<ItemStack> items) {}

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1; // Render in alpha pass (like CUTOUT)
    }

    @Override
    public int getRenderType() {
        return -1; // Invisible render type
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        for (int i = 0; i < 4; i++) {
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(x + rand.nextFloat(), y + 0.7 + rand.nextFloat() * 0.3, z + rand.nextFloat());
            p.gravity(0.004)
                .scale(0.3F + rand.nextFloat() * 0.2F)
                .setMaxAge(45 + rand.nextInt(20));
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                .setAlphaMultiplier(1F);
            p.motion(0, -rand.nextFloat() * 0.001, 0);
            if (rand.nextInt(6) == 0) {
                p.setColor(Color.WHITE);
                p.scale(0.1F + rand.nextFloat() * 0.1F);
            } else {
                p.setColor(new Color(0x0C1576));
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null; // 1.7.10: Items.AIR doesn't exist
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVanishing();
    }
}
