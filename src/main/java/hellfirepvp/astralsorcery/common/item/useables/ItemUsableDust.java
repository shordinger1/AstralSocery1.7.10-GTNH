/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.useables;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.entities.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.entities.EntityNocturnalSpark;
import hellfirepvp.astralsorcery.common.item.base.IItemVariants;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUsableDust
 * Created by HellFirePvP
 * Date: 03.07.2017 / 11:27
 */
public class ItemUsableDust extends Item implements IItemVariants, IBehaviorDispenseItem {

    public ItemUsableDust() {
        setMaxStackSize(64);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        if (item == this) {
            for (DustType type : DustType.values()) {
                items.add(type.asStack());
            }
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World worldIn, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        DustType type = DustType.fromMeta(stack.getItemDamage());
        if ((stack == null || stack.stackSize <= 0) || worldIn.isRemote
            || !(stack.getItem() instanceof ItemUsableDust)
            || type == null) {
            return true;
        }
        BlockPos pos = new BlockPos(x, y, z);
        EnumFacing facing = EnumFacing.getFront(side);
        type.rightClickBlock(player, worldIn, pos, stack, facing);
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        DustType type = DustType.fromMeta(itemStackIn.getItemDamage());
        if ((itemStackIn == null || itemStackIn.stackSize <= 0) || worldIn.isRemote
            || !(itemStackIn.getItem() instanceof ItemUsableDust)
            || type == null) {
            return itemStackIn;
        }
        type.rightClickAir(worldIn, playerIn, itemStackIn);
        if (!playerIn.capabilities.isCreativeMode) {
            itemStackIn.stackSize--;
        }
        return itemStackIn;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Item i = stack.getItem();
        if (i instanceof ItemUsableDust) {
            ItemUsableDust.DustType type = ItemUsableDust.DustType.fromMeta(stack.getItemDamage());
            return super.getUnlocalizedName(stack) + "." + type.getUnlocalizedName();
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public String[] getVariants() {
        String[] sub = new String[DustType.values().length];
        DustType[] values = DustType.values();
        for (int i = 0; i < values.length; i++) {
            DustType mt = values[i];
            sub[i] = mt.getUnlocalizedName();
        }
        return sub;
    }

    @Override
    public int[] getVariantMetadatas() {
        int[] sub = new int[DustType.values().length];
        DustType[] values = DustType.values();
        for (int i = 0; i < values.length; i++) {
            DustType mt = values[i];
            sub[i] = mt.getMeta();
        }
        return sub;
    }

    @Override
    public ItemStack dispense(IBlockSource source, ItemStack stack) {
        DustType type = DustType.fromMeta(stack.getItemDamage());
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemUsableDust) || type == null) {
            return stack;
        }
        if (type.dispense(source)) {
            stack.stackSize--;
            if (stack.stackSize <= 0) {
                stack = null;
            }
            return stack;
        }
        return stack;
    }

    public static enum DustType {

        ILLUMINATION,
        NOCTURNAL;

        public boolean dispense(IBlockSource source) {
            World world = source.getWorld();
            int x = source.getXInt();
            int y = source.getYInt();
            int z = source.getZInt();
            Block block = world.getBlock(x, y, z);

            if (block != Blocks.dispenser) {
                return false;
            }

            int metadata = source.getBlockMetadata();
            EnumFacing rotation = EnumFacing.getFront(metadata);

            double posX = source.getX() + 0.7D * (double) rotation.getFrontOffsetX();
            double posY = source.getY() + 0.7D * (double) rotation.getFrontOffsetY();
            double posZ = source.getZ() + 0.7D * (double) rotation.getFrontOffsetZ();

            switch (this) {
                case ILLUMINATION:
                    EntityIlluminationSpark spark = new EntityIlluminationSpark(world, posX, posY, posZ);
                    spark.setVelocity(
                        rotation.getFrontOffsetX() * 0.7F,
                        rotation.getFrontOffsetY() * 0.7F + 0.1F,
                        rotation.getFrontOffsetZ() * 0.7F);
                    world.spawnEntityInWorld(spark);
                    return true;
                case NOCTURNAL:
                    EntityNocturnalSpark nocSpark = new EntityNocturnalSpark(world, posX, posY, posZ);
                    nocSpark.setVelocity(
                        rotation.getFrontOffsetX() * 0.7F,
                        rotation.getFrontOffsetY() * 0.7F + 0.1F,
                        rotation.getFrontOffsetZ() * 0.7F);
                    world.spawnEntityInWorld(nocSpark);
                    return true;
                default:
                    break;
            }
            return false;
        }

        public void rightClickAir(World worldIn, EntityPlayer player, ItemStack dustStack) {
            switch (this) {
                case ILLUMINATION:
                    worldIn.spawnEntityInWorld(new EntityIlluminationSpark(worldIn, player));
                    break;
                case NOCTURNAL:
                    worldIn.spawnEntityInWorld(new EntityNocturnalSpark(worldIn, player));
                    break;
                default:
                    break;
            }
        }

        public void rightClickBlock(EntityPlayer playerIn, World worldIn, BlockPos pos, ItemStack dustStack,
            EnumFacing facing) {
            switch (this) {
                case ILLUMINATION:
                    Block block = worldIn.getBlock(pos.getX(), pos.getY(), pos.getZ());
                    if (!block.isReplaceable(worldIn, pos.getX(), pos.getY(), pos.getZ())) {
                        pos = new BlockPos(
                            pos.getX() + facing.getFrontOffsetX(),
                            pos.getY() + facing.getFrontOffsetY(),
                            pos.getZ() + facing.getFrontOffsetZ());
                    }
                    if (playerIn.canPlayerEdit(pos.getX(), pos.getY(), pos.getZ(), facing.ordinal(), dustStack)
                        && worldIn.canPlaceEntityOnSide(
                            BlocksAS.blockVolatileLight,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            false,
                            facing.ordinal(),
                            playerIn,
                            dustStack)) {
                        if (worldIn.setBlock(pos.getX(), pos.getY(), pos.getZ(), BlocksAS.blockVolatileLight, 0, 3)) {
                            Block placedBlock = worldIn.getBlock(pos.getX(), pos.getY(), pos.getZ());
                            // 1.7.10: Use stepSound field with getVolume(), getPitch(), getStepResourcePath()
                            float f = placedBlock.stepSound.getVolume();
                            float f1 = placedBlock.stepSound.getPitch();
                            worldIn.playSoundEffect(
                                (double) ((float) pos.getX() + 0.5F),
                                (double) ((float) pos.getY() + 0.5F),
                                (double) ((float) pos.getZ() + 0.5F),
                                placedBlock.stepSound.getStepResourcePath(),
                                (f + 1.0F) / 2.0F,
                                f1 * 0.8F);
                            if (!playerIn.capabilities.isCreativeMode) {
                                dustStack.stackSize--;
                                if (dustStack.stackSize <= 0) {
                                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                                }
                            }
                        }
                    }
                    break;
                case NOCTURNAL:
                    BlockPos newPos = new BlockPos(
                        pos.getX() + facing.getFrontOffsetX(),
                        pos.getY() + facing.getFrontOffsetY(),
                        pos.getZ() + facing.getFrontOffsetZ());
                    EntityNocturnalSpark noc = new EntityNocturnalSpark(worldIn, playerIn);
                    noc.setPosition(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                    noc.setSpawning();
                    worldIn.spawnEntityInWorld(noc);
                    if (!playerIn.capabilities.isCreativeMode) {
                        dustStack.stackSize--;
                        if (dustStack.stackSize <= 0) {
                            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        public ItemStack asStack() {
            return new ItemStack(ItemsAS.useableDust, 1, getMeta());
        }

        public String getUnlocalizedName() {
            return name().toLowerCase();
        }

        public int getMeta() {
            return ordinal();
        }

        public static DustType fromMeta(int meta) {
            int ord = WrapMathHelper.clamp(meta, 0, values().length - 1);
            return values()[ord];
        }

    }

}
