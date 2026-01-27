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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.SwordSharpenHelper;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStoneMachine
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:11
 */
public class BlockMachine extends BlockContainer implements BlockCustomName, BlockVariants {

    private static final Random rand = new Random();

    public BlockMachine() {
        super(Material.rock);
        setHardness(3.0F);
        setStepSound(Block.soundTypePiston);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public String getHarvestTool(int meta) {
        MachineType t = MachineType.byMetadata(meta);
        switch (t) {
            case TELESCOPE:
                return "axe";
            case GRINDSTONE:
                return "pickaxe";
        }
        return super.getHarvestTool(meta);
    }

    @Override
    public int getHarvestLevel(int meta) {
        return 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        MachineType t = MachineType.byMetadata(meta);
        if (t == MachineType.TELESCOPE) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 2, z + 1);
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        MachineType t = MachineType.byMetadata(meta);
        if (t == MachineType.TELESCOPE) {
            this.setBlockBounds(0F, 0F, 0F, 1F, 2F, 1F);
        } else {
            this.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        MachineType t = MachineType.byMetadata(meta);
        if (t == MachineType.TELESCOPE) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 2, z + 1);
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
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
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (MachineType type : MachineType.values()) {
            list.add(type.asStack());
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        MachineType type = MachineType.byMetadata(meta);
        return type.provideTileEntity(worldIn, this);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        MachineType type = MachineType.byMetadata(meta);
        if (type == MachineType.GRINDSTONE) {
            TileGrindstone tgr = MiscUtils.getTileAt(worldIn, x, y, z, TileGrindstone.class);
            if (tgr != null) {
                ItemStack grind = tgr.getGrindingItem();
                if (grind != null && grind.stackSize > 0) {
                    ItemUtils.dropItemNaturally(worldIn, x + 0.5, y + 0.5, z + 0.5, grind);
                }
            }
        }
        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        MachineType type = MachineType.byMetadata(meta);
        if (type == MachineType.TELESCOPE) {
            if (worldIn.isRemote) {
                AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.TELESCOPE, player, worldIn, x, y, z);
            }
        }
        return true;
    }

    public boolean handleSpecificActivateEvent(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player instanceof EntityPlayerMP && MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            return false;
        }

        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;
        int meta = world.getBlockMetadata(x, y, z);
        MachineType type = MachineType.byMetadata(meta);

        switch (type) {
            case GRINDSTONE:
                TileGrindstone tgr = MiscUtils.getTileAt(world, x, y, z, TileGrindstone.class);
                if (tgr != null) {
                    if (!world.isRemote) {
                        ItemStack grind = tgr.getGrindingItem();
                        if (grind != null && grind.stackSize > 0) {
                            if (player.isSneaking()) {
                                player.inventory.addItemStackToInventory(grind);
                                tgr.setGrindingItem(null);
                            } else {
                                GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(grind);
                                if (recipe != null) {
                                    GrindstoneRecipe.GrindResult result = recipe.grind(grind);
                                    switch (result.getType()) {
                                        case SUCCESS:
                                            tgr.setGrindingItem(grind); // Update
                                            break;
                                        case ITEMCHANGE:
                                            tgr.setGrindingItem(result.getStack());
                                            break;
                                        case FAIL_BREAK_ITEM:
                                            tgr.setGrindingItem(null);
                                            // TODO: Play sound - needs 1.7.10 sound string
                                            world
                                                .playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 0.5F, 1.0F);
                                            break;
                                    }
                                    tgr.playWheelEffect();
                                } else if (SwordSharpenHelper.canBeSharpened(grind)) {
                                    if (rand.nextInt(40) == 0) {
                                        SwordSharpenHelper.setSwordSharpened(grind);
                                    }
                                    tgr.playWheelEffect();
                                }
                            }
                        } else {
                            ItemStack stack = player.getCurrentEquippedItem();
                            if (stack != null && stack.stackSize > 0) {
                                GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(stack);
                                if (recipe != null) {
                                    ItemStack toSet = stack.copy();
                                    toSet.stackSize = 1;
                                    tgr.setGrindingItem(toSet);
                                    // TODO: Play sound - needs 1.7.10 sound string
                                    world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.pop", 0.5F, 1.0F);

                                    if (!player.capabilities.isCreativeMode) {
                                        stack.stackSize--;
                                    }
                                } else if (SwordSharpenHelper.canBeSharpened(stack)
                                    && !SwordSharpenHelper.isSwordSharpened(stack)) {
                                        ItemStack toSet = stack.copy();
                                        toSet.stackSize = 1;
                                        tgr.setGrindingItem(toSet);
                                        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.pop", 0.5F, 1.0F);

                                        if (!player.capabilities.isCreativeMode) {
                                            stack.stackSize--;
                                        }
                                    } else if (player.isSneaking()) {
                                        return false;
                                    }
                            }
                        }
                    } else {
                        ItemStack grind = tgr.getGrindingItem();
                        if (grind != null && grind.stackSize > 0) {
                            GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(grind);
                            if (recipe != null) {
                                for (int j = 0; j < 8; j++) {
                                    world.spawnParticle(
                                        "crit",
                                        x + 0.5,
                                        y + 0.8,
                                        z + 0.4,
                                        (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                        (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                        (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3);
                                }
                            } else if (SwordSharpenHelper.canBeSharpened(grind)
                                && !SwordSharpenHelper.isSwordSharpened(grind)) {
                                    for (int j = 0; j < 8; j++) {
                                        world.spawnParticle(
                                            "crit",
                                            x + 0.5,
                                            y + 0.8,
                                            z + 0.4,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3);
                                    }
                                }
                        }
                    }
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        MachineType type = MachineType.byMetadata(meta);
        if (type == MachineType.TELESCOPE) {
            worldIn.setBlock(
                x,
                y + 1,
                z,
                BlocksAS.blockStructural,
                BlockStructural.BlockType.TELESCOPE_STRUCT.ordinal(),
                3);
        }
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        int meta = world.getBlockMetadata(x, y, z);
        MachineType type = MachineType.byMetadata(meta);
        if (type == MachineType.TELESCOPE) {
            if (world.isAirBlock(x, y + 1, z)) {
                world.setBlockToAir(x, y, z);
            }
        }
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        MachineType mt = MachineType.byMetadata(meta);
        return mt.getName();
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (MachineType type : MachineType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        MachineType type = MachineType.byMetadata(metadata);
        return type.getName();
    }

    public enum MachineType implements IVariantTileProvider {

        TELESCOPE,
        GRINDSTONE;

        @Override
        public TileEntity provideTileEntity(World world, Block state) {
            switch (this) {
                case TELESCOPE:
                    return new TileTelescope();
                case GRINDSTONE:
                    return new TileGrindstone();
            }
            return null;
        }

        public int getMeta() {
            return ordinal();
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockMachine, 1, getMeta());
        }

        public String getName() {
            return name().toLowerCase();
        }

        public static MachineType byMetadata(int meta) {
            MachineType[] values = values();
            return meta >= 0 && meta < values.length ? values[meta] : values[0];
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
