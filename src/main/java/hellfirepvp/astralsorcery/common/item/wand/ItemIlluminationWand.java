/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.block.BlockTranslucentBlock;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.tile.TileTranslucent;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationWand
 * Created by HellFirePvP
 * Date: 17.01.2017 / 15:09
 */
public class ItemIlluminationWand extends Item implements ItemAlignmentChargeConsumer, ItemDynamicColor {

    public ItemIlluminationWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        EnumDyeColor color = getConfiguredColor(stack);
        if (color != null) {
            tooltip.add(
                MiscUtils.textFormattingForDye(color)
                    + MiscUtils.capitalizeFirst(I18n.format(color.getUnlocalizedName())));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return ct == ChargeType.TEMP;
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFF;
        EnumDyeColor color = getConfiguredColor(stack);
        if (color == null) color = EnumDyeColor.YELLOW;
        Color c = MiscUtils.flareColorFromDye(color);
        return 0xFF000000 | c.getRGB();
    }

    public static void setConfiguredColor(ItemStack stack, EnumDyeColor color) {
        NBTHelper.getPersistentData(stack)
            .setInteger("color", color.getDyeDamage());
    }

    @Nullable
    public static EnumDyeColor getConfiguredColor(ItemStack stack) {
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if (tag != null && tag.hasKey("color")) {
            return EnumDyeColor.byDyeDamage(
                NBTHelper.getPersistentData(stack)
                    .getInteger("color"));
        }
        return null;
    }

    public static Block getPlacingState(ItemStack wand) {
        // 1.7.10: Block state properties not available, return base block
        return BlocksAS.blockVolatileLight;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemIlluminationWand)) {
            return true;
        }
        if (!worldIn.isRemote) {
            BlockPos pos = new BlockPos(x, y, z);
            Block at = worldIn.getBlock(x, y, z);
            if (!playerIn.isSneaking()) {
                TileIlluminator illum = MiscUtils.getTileAt(worldIn, pos, TileIlluminator.class, false);
                if (illum != null) {
                    EnumDyeColor thisColor = getConfiguredColor(stack);
                    if (thisColor == null) {
                        thisColor = EnumDyeColor.YELLOW;
                    }
                    illum.onWandUsed(thisColor);
                    drainTempCharge(playerIn, PlayerChargeHandler.INSTANCE.getCharge(playerIn), false);
                } else {
                    Block block = worldIn.getBlock(x, y, z);
                    int placeX = x;
                    int placeY = y;
                    int placeZ = z;
                    if (!block.isReplaceable(worldIn, x, y, z)) {
                        net.minecraftforge.common.util.ForgeDirection dir = net.minecraftforge.common.util.ForgeDirection
                            .getOrientation(side);
                        placeX = x + dir.offsetX;
                        placeY = y + dir.offsetY;
                        placeZ = z + dir.offsetZ;
                    }
                    BlockPos placePos = new BlockPos(placeX, placeY, placeZ);
                    if (playerIn.canPlayerEdit(placeX, placeY, placeZ, side, stack)) {
                        Block atPlace = worldIn.getBlock(placeX, placeY, placeZ);
                        Block placingState = getPlacingState(stack);
                        if (atPlace == placingState) {
                            // 1.7.10: Use stepSound field with getStepResourcePath(), getVolume(), getPitch()
                            if (worldIn.setBlockToAir(placeX, placeY, placeZ)) {
                                worldIn.playSoundEffect(
                                    x + 0.5,
                                    y + 0.5,
                                    z + 0.5,
                                    atPlace.stepSound.getStepResourcePath(),
                                    (atPlace.stepSound.getVolume() + 1.0F) / 2.0F,
                                    atPlace.stepSound.getPitch() * 0.8F);
                            }
                        } else if (drainTempCharge(playerIn, Config.illuminationWandUseCost, true)) {
                            if (worldIn.setBlock(placeX.posX, placeX.posY, placeX.posZ, placeY, placeZ, BlocksAS.blockVolatileLight, 0, 3)) {
                                // 1.7.10: Use stepSound field with getStepResourcePath(), getVolume(), getPitch()
                                Block placedBlock = worldIn.getBlock(placeX, placeY, placeZ);
                                worldIn.playSoundEffect(
                                    x + 0.5,
                                    y + 0.5,
                                    z + 0.5,
                                    placedBlock.stepSound.getStepResourcePath(),
                                    (placedBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                                    placedBlock.stepSound.getPitch() * 0.8F);
                                drainTempCharge(playerIn, Config.illuminationWandUseCost, false);
                            }
                        }
                    }
                }
            } else {
                // 1.7.10: Use isOpaqueCube() instead of isNormalCube()
                if (at.isOpaqueCube()) {
                    TileEntity te = worldIn.getTileEntity(x, y, z);
                    if (te == null && drainTempCharge(playerIn, Config.illuminationWandUseCost, true)) {
                        if (worldIn.setBlock(x.posX, x.posY, x.posZ, y, z, BlocksAS.translucentBlock, 0, 3)) {
                            TileTranslucent tt = MiscUtils.getTileAt(worldIn, pos, TileTranslucent.class, true);
                            if (tt == null) {
                                worldIn.setBlockToAir(x, y, z);
                            } else {
                                tt.setFakedState(at);
                                drainTempCharge(playerIn, Config.illuminationWandUseCost, false);
                            }
                        }
                    }
                } else if (at instanceof BlockTranslucentBlock) {
                    TileTranslucent tt = MiscUtils.getTileAt(worldIn, pos, TileTranslucent.class, true);
                    if (tt != null && tt.getFakedState() != null) {
                        worldIn.setBlockToAir(x, y, z);
                    }
                }
            }
        }
        return true;
    }

}
