/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.RockCrystalHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IBlockState;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCrystalOre
 * Created by HellFirePvP
 * Date: 07.05.2016 / 18:03
 */
public class BlockCustomOre extends Block implements BlockCustomName, BlockVariants {

    public static boolean allowCrystalHarvest = false;
    private static final Random rand = new Random();

    public static PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("oretype", OreType.class);

    private BlockStateContainer blockState;

    public BlockCustomOre() {
        super(Material.rock);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 3);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, ORE_TYPE);
    }

    public IBlockState getDefaultState() {
        return this.blockState.getBaseState()
            .withProperty(ORE_TYPE, OreType.ROCK_CRYSTAL);
    }

    protected void setDefaultState(IBlockState state) {
        // In 1.7.10, default state is tracked separately
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        super.breakBlock(worldIn, x, y, z, this, meta);

        OreType type = OreType.values()[meta >= OreType.values().length ? 0 : meta];
        if (type.equals(OreType.ROCK_CRYSTAL)) {
            RockCrystalHandler.INSTANCE.removeOre(worldIn, new BlockPos(x, y, z), true);
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (OreType t : OreType.values()) {
            list.add(new ItemStack(this, 1, t.ordinal()));
        }
    }

    public int getMetaFromState(IBlockState state) {
        OreType type = state.getValue(ORE_TYPE);
        return type.getMeta();
    }

    public IBlockState getStateFromMeta(int meta) {
        return meta < OreType.values().length ? getDefaultState().withProperty(ORE_TYPE, OreType.values()[meta])
            : getDefaultState();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ORE_TYPE);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int metadata) {
        OreType type = OreType.values()[metadata >= OreType.values().length ? 0 : metadata];
        if (type != OreType.ROCK_CRYSTAL
            || (allowCrystalHarvest || (securityCheck(worldIn, player) && checkSafety(worldIn, x, y, z)))) {
            super.harvestBlock(worldIn, player, x, y, z, metadata);
        }
    }

    @Override
    public int getRenderType() {
        return 0; // Standard render type
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        OreType type = OreType.values()[metadata >= OreType.values().length ? 0 : metadata];
        switch (type) {
            case ROCK_CRYSTAL:
                if ((allowCrystalHarvest
                    || (checkSafety(world, x, y, z) && securityCheck(world, world.getClosestPlayer(x, y, z, 10))))) {
                    drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                    for (int i = 0; i < (fortune + 1); i++) {
                        if (world.rand.nextBoolean()) {
                            drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                        }
                    }
                    if (world.rand.nextBoolean()) {
                        drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                    }
                }
                break;
            case STARMETAL:
                drops.add(new ItemStack(this, 1, OreType.STARMETAL.ordinal()));
                break;
            default:
                break;
        }
        return drops;
    }

    private boolean securityCheck(World world, EntityPlayer player) {
        return !world.isRemote && player != null && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player);
    }

    private boolean checkSafety(World world, int x, int y, int z) {
        EntityPlayer player = world.getClosestPlayer(x, y, z, 10);
        return player != null && player.getDistanceSq(x, y, z) < 100;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    public boolean isTopSolid(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        OreType ot = getStateFromMeta(meta).getValue(ORE_TYPE);
        return ot.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        return singleEnumPropertyStates(getDefaultState(), ORE_TYPE, OreType.values());
    }

    public boolean canSilkHarvest(World world, int x, int y, int z, int metadata, EntityPlayer player) {
        OreType ot = OreType.values()[metadata >= OreType.values().length ? 0 : metadata];
        if (ot == OreType.ROCK_CRYSTAL) {
            if (Config.rockCrystalOreSilkTouchHarvestable) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(ORE_TYPE)
            .getName();
    }

    @SideOnly(Side.CLIENT)
    public static void playStarmetalOreEffects(PktParticleEvent event) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            event.getVec()
                .getX() + rand.nextFloat(),
            event.getVec()
                .getY() + rand.nextFloat(),
            event.getVec()
                .getZ() + rand.nextFloat());
        p.motion(0, rand.nextFloat() * 0.05, 0);
        p.scale(0.2F);
    }

    public static enum OreType implements IStringSerializable {

        ROCK_CRYSTAL(0),
        STARMETAL(1);

        private final int meta;

        private OreType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.customOre, 1, meta);
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
