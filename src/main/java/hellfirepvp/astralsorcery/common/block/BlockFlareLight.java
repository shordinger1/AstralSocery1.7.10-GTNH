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
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IBlockState;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFlareLight
 * Created by HellFirePvP
 * Date: 22.10.2016 / 14:36
 */
public class BlockFlareLight extends Block {

    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    private BlockStateContainer blockState;

    public BlockFlareLight() {
        super(Material.air);
        setLightLevel(1F);
        setBlockUnbreakable();
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, COLOR);
    }

    public IBlockState getDefaultState() {
        return this.blockState.getBaseState()
            .withProperty(COLOR, EnumDyeColor.YELLOW);
    }

    protected void setDefaultState(IBlockState state) {
        // In 1.7.10, default state is tracked separately
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List list) {}

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(int metadata, Random rand, int fortune) {
        return null; // 1.7.10: null doesn't exist, return null
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(COLOR)
            .ordinal();
    }

    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COLOR, EnumDyeColor.values()[meta % EnumDyeColor.values().length]);
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR);
    }

    @Override
    public int getRenderType() {
        return -1; // Invisible
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random rand) {
        int metadata = worldIn.getBlockMetadata(x, y, z);
        EnumDyeColor color = EnumDyeColor.values()[metadata % EnumDyeColor.values().length];

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x + 0.5, y + 0.2, z + 0.5)
            .gravity(0.004);
        p.offset(
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.4F + rand.nextFloat() * 0.1F)
            .setAlphaMultiplier(0.75F);
        p.motion(0, rand.nextFloat() * 0.02F, 0)
            .setMaxAge(50 + rand.nextInt(20));
        p.setColor(MiscUtils.flareColorFromDye(color));

        p = EffectHelper.genericFlareParticle(x + 0.5, y + 0.2, z + 0.5)
            .gravity(0.004);
        p.offset(
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.4F + rand.nextFloat() * 0.1F)
            .setAlphaMultiplier(0.75F);
        p.motion(0, rand.nextFloat() * 0.02F, 0)
            .setMaxAge(50 + rand.nextInt(20));
        p.setColor(MiscUtils.flareColorFromDye(color));

        if (rand.nextBoolean()) {
            p = EffectHelper.genericFlareParticle(x + 0.5, y + 0.3, z + 0.5)
                .gravity(0.004);
            p.offset(
                rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.1F + rand.nextFloat() * 0.05F)
                .setColor(Color.WHITE)
                .setMaxAge(25);
        }
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
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return true;
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

}
