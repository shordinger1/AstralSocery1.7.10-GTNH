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
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileGemCrystals;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGemCrystals
 * Created by HellFirePvP
 * Date: 27.11.2018 / 18:57
 */
public class BlockGemCrystals extends BlockContainer implements BlockCustomName, BlockVariants {

    public static final PropertyEnum<GrowthStageType> STAGE = PropertyEnum.create("stage", GrowthStageType.class);

    private static final AxisAlignedBB boxStage0 = AxisAlignedBB.getBoundingBox(0.25, 0, 0.25, 0.75, 0.375, 0.75);
    private static final AxisAlignedBB boxStage1 = AxisAlignedBB.getBoundingBox(0.25, 0, 0.25, 0.75, 0.5, 0.75);
    private static final AxisAlignedBB boxStage2Night = AxisAlignedBB.getBoundingBox(0.25, 0, 0.25, 0.75, 0.5, 0.75);
    private static final AxisAlignedBB boxStage2Sky = AxisAlignedBB.getBoundingBox(0.25, 0, 0.25, 0.75, 0.5625, 0.75);
    private static final AxisAlignedBB boxStage2Day = AxisAlignedBB.getBoundingBox(0.25, 0, 0.25, 0.75, 0.5625, 0.75);

    private BlockStateContainer blockState;

    public BlockGemCrystals() {
        super(Material.rock);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(20.0F);
        setLightLevel(0.3F);
        setStepSound(Block.soundTypeGlass);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, STAGE);
    }

    // In 1.7.10, default state is represented by default metadata (0)
    // GrowthStageType.STAGE_0 has meta 0, so no special handling needed

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (GrowthStageType stageType : GrowthStageType.values()) {
            list.add(new ItemStack(this, 1, stageType.ordinal()));
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        boolean replaceable = super.canPlaceBlockAt(worldIn, x, y, z);
        if (replaceable) {
            if (!worldIn.isSideSolid(x, y - 1, z, ForgeDirection.UP)) replaceable = false;
        }
        return replaceable;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        GrowthStageType type = GrowthStageType.values()[metadata >= GrowthStageType.values().length ? 0 : metadata];
        switch (type) {
            case STAGE_0:
                this.setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.375F, 0.75F);
                break;
            case STAGE_1:
                this.setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case STAGE_2_SKY:
            case STAGE_2_DAY:
            case STAGE_2_NIGHT:
                this.setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5625F, 0.75F);
                break;
            default:
                this.setBlockBounds(0, 0, 0, 1, 1, 1);
                break;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        GrowthStageType type = GrowthStageType.values()[metadata >= GrowthStageType.values().length ? 0 : metadata];
        switch (type) {
            case STAGE_0:
                return AxisAlignedBB.getBoundingBox(x + 0.25, y, z + 0.25, x + 0.75, y + 0.375, z + 0.75);
            case STAGE_1:
                return AxisAlignedBB.getBoundingBox(x + 0.25, y, z + 0.25, x + 0.75, y + 0.5, z + 0.75);
            case STAGE_2_SKY:
            case STAGE_2_DAY:
            case STAGE_2_NIGHT:
                return AxisAlignedBB.getBoundingBox(x + 0.25, y, z + 0.25, x + 0.75, y + 0.5625, z + 0.75);
            default:
                return super.getCollisionBoundingBoxFromPool(world, x, y, z);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public boolean isTopSolid(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockMetadataWithNotify(
            x,
            y,
            z,
            WrapMathHelper.clamp(stack.getItemDamage(), 0, GrowthStageType.values().length - 1),
            2);
    }

    // In 1.7.10, getMetaFromState and getStateFromMeta don't exist
    // Metadata is handled directly with GrowthStageType.ordinal()

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        GrowthStageType type = GrowthStageType.values()[metadata >= GrowthStageType.values().length ? 0 : metadata];
        ItemStack gem = null;
        switch (type) {
            case STAGE_2_SKY:
                gem = ItemPerkGem.GemType.SKY.asStack();
                break;
            case STAGE_2_DAY:
                gem = ItemPerkGem.GemType.DAY.asStack();
                break;
            case STAGE_2_NIGHT:
                gem = ItemPerkGem.GemType.NIGHT.asStack();
                break;
        }
        if (!(gem == null || gem.stackSize <= 0)) {
            drops.add(gem);
        }
        return drops;
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block blockIn) {
        if (!worldIn.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
            dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            breakBlock(worldIn, x, y, z, worldIn.getBlock(x, y, z), worldIn.getBlockMetadata(x, y, z));
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        TileGemCrystals te = MiscUtils.getTileAt(worldIn, x, y, z, TileGemCrystals.class);
        if (te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(
                PktParticleEvent.ParticleEventType.GEM_CRYSTAL_BURST,
                x,
                y,
                z);
            event.setAdditionalDataLong(meta);
            PacketChannel.CHANNEL
                .sendToAllAround(event, PacketChannel.pointFromPos(worldIn, new BlockPos(x, y, z), 32));
        }
        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileGemCrystals();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileGemCrystals();
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        GrowthStageType type = GrowthStageType.values()[meta >= GrowthStageType.values().length ? 0 : meta];
        return type.getName();
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new ArrayList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (GrowthStageType type : GrowthStageType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        GrowthStageType type = GrowthStageType.values()[metadata >= GrowthStageType.values().length ? 0 : metadata];
        return type.getName();
    }

    public static enum GrowthStageType implements IStringSerializable {

        STAGE_0(0, Color.WHITE),
        STAGE_1(1, Color.WHITE),
        STAGE_2_SKY(2, new Color(0x2561B5)),
        STAGE_2_DAY(2, new Color(0xE04C02)),
        STAGE_2_NIGHT(2, new Color(0x808080));

        private final int growthStage;
        private final Color displayColor;

        GrowthStageType(int growthStage, Color displayColor) {
            this.growthStage = growthStage;
            this.displayColor = displayColor;
        }

        public Color getDisplayColor() {
            return displayColor;
        }

        public int getGrowthStage() {
            return growthStage;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
