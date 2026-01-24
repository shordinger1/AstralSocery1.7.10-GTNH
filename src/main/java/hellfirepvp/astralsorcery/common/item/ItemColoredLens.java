/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLens
 * Created by HellFirePvP
 * Date: 29.11.2016 / 12:35
 */
public class ItemColoredLens extends Item implements ItemDynamicColor {

    public ItemColoredLens() {
        setMaxStackSize(16);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> subItems) {
        // 1.7.10 compatibility: Item.isInCreativeTab() doesn't exist, use tab == this.getCreativeTab() instead
        if (tab == this.getCreativeTab()) {
            for (ColorType ct : ColorType.values()) {
                subItems.add(new ItemStack(this, 1, ct.getMeta()));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip) {
        if (!(stack == null || stack.stackSize <= 0) && stack.getItem() instanceof ItemColoredLens) {
            int dmg = stack.getItemDamage();
            if (dmg >= 0 && dmg < ColorType.values().length) {
                tooltip.add(
                    I18n.format(
                        "item.itemcoloredlens.effect." + ColorType.values()[dmg].name()
                            .toLowerCase() + ".name"));
            }
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            BlockPos pos = new BlockPos(x, y, z);
            ItemStack inHand = playerIn.getHeldItem();
            ColorType type = null;
            if (!(inHand == null || inHand.stackSize <= 0) && inHand.getItem() instanceof ItemColoredLens) {
                int dmg = inHand.getItemDamage();
                if (dmg >= 0 && dmg < ColorType.values().length) {
                    type = ColorType.values()[dmg];
                }
            }
            if (type != null) {
                TileCrystalLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
                if (lens != null) {
                    ColorType oldType = lens.setLensColor(type);
                    if (!playerIn.capabilities.isCreativeMode) {
                        inHand.stackSize = inHand.stackSize - 1;
                        if (inHand.stackSize <= 0) {
                            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                        }
                    }
                    SoundHelper.playSoundAround(Sounds.clipSwitch, worldIn, pos, 0.8F, 1.5F);
                    if (oldType != null) {
                        playerIn.inventory.addItemStackToInventory(oldType.asStack());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        int dmg = stack.getItemDamage();
        if (dmg < 0 || dmg >= ColorType.values().length) return 0xFFFFFFFF;
        return ColorType.values()[dmg].colorRGB;
    }

    public static enum ColorType {

        FIRE(TargetType.ANY, 0xff7f00, 0.07F),
        BREAK(TargetType.BLOCK, 0xffdf00, 0.07F),
        GROW(TargetType.BLOCK, 0x00df00, 0.07F),
        DAMAGE(TargetType.ENTITY, 0xdf0000, 0.07F),
        REGEN(TargetType.ENTITY, 0xff7fbf, 0.07F),
        PUSH(TargetType.ENTITY, 0x00dfff, 0.07F),

        SPECTRAL(TargetType.NONE, 0x7f00bf, 0.25F);

        public final int colorRGB;
        public final Color wrappedColor;
        private final float flowReduction;
        private final TargetType type;

        private ColorType(TargetType type, int colorRGB, float flowReduction) {
            this.type = type;
            this.colorRGB = colorRGB;
            this.wrappedColor = new Color(colorRGB);
            this.flowReduction = flowReduction;
        }

        public TargetType getType() {
            return type;
        }

        public float getFlowReduction() {
            return flowReduction;
        }

        public String getUnlocalizedName() {
            return name().toLowerCase();
        }

        public ItemStack asStack() {
            return new ItemStack(ItemsAS.coloredLens, 1, getMeta());
        }

        public int getMeta() {
            return ordinal();
        }

        public void onEntityInBeam(Vector3 beamOrigin, Vector3 beamTarget, Entity entity, float percStrength) {
            switch (this) {
                case FIRE:
                    if (itemRand.nextFloat() > percStrength) return;
                    if (entity instanceof EntityItem) {
                        ItemStack current = ((EntityItem) entity).getEntityItem();
                        ItemStack result = FurnaceRecipes.smelting()
                            .getSmeltingResult(current);
                        if (!(result == null || result.stackSize <= 0)) {
                            Vector3 entityPos = Vector3.atEntityCenter(entity);
                            ItemUtils.dropItemNaturally(
                                entity.worldObj,
                                entityPos.getX(),
                                entityPos.getY(),
                                entityPos.getZ(),
                                ItemUtils.copyStackWithSize(result, result.stackSize));
                            if (current.stackSize > 1) {
                                current.stackSize -= 1;
                                ((EntityItem) entity).setEntityItemStack(current);
                            } else {
                                entity.setDead();
                            }
                        }
                    } else if (entity instanceof EntityLivingBase) {
                        entity.setFire(1);
                    }
                    break;
                case DAMAGE:
                    if (!(entity instanceof EntityLivingBase)) return;
                    if (itemRand.nextFloat() > percStrength) return;
                    // In 1.7.10, use MinecraftServer.getServer() instead of entity.getServer()
                    if (entity instanceof EntityPlayer && MinecraftServer.getServer() != null
                        && !MinecraftServer.getServer()
                            .isPVPEnabled())
                        return;
                    DamageUtil.attackEntityFrom(entity, CommonProxy.dmgSourceStellar, 6.5F);
                    break;
                case REGEN:
                    if (!(entity instanceof EntityLivingBase)) return;
                    if (itemRand.nextFloat() > percStrength) return;
                    ((EntityLivingBase) entity).heal(3.5F);
                    break;
                case PUSH:
                    if (entity instanceof EntityPlayer || itemRand.nextFloat() > percStrength) return;
                    Vector3 dir = beamTarget.clone()
                        .subtract(beamOrigin)
                        .normalize()
                        .multiply(0.5F);
                    entity.motionX = Math.min(1F, entity.motionZ + dir.getX());
                    entity.motionY = Math.min(1F, entity.motionY + dir.getY());
                    entity.motionZ = Math.min(1F, entity.motionZ + dir.getZ());
                    break;
                default:
                    break;
            }
        }

        public void onBlockOccupyingBeam(World world, BlockPos at, Block state, float percStrength) {
            switch (this) {
                case BREAK:
                    float hardness = state.getBlockHardness(world, at.getX(), at.getY(), at.getZ());
                    if (hardness < 0) return;
                    hardness *= 1.5F;
                    BlockBreakAssist.addProgress(world, at, hardness, percStrength * 4F);
                    PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.EffectType.BEAM_BREAK, at);
                    // In 1.7.10, Block.getStateId doesn't exist, use metadata instead
                    int meta = world.getBlockMetadata(at.getX(), at.getY(), at.getZ());
                    pkt.data = meta;
                    PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, at, 16.0));
                    break;
                case GROW:
                    if (world.rand.nextFloat() > percStrength) return;
                    CropHelper.GrowablePlant plant = CropHelper.wrapPlant(world, at);
                    if (plant != null) {
                        plant.tryGrow(world, world.rand);
                        PktParticleEvent packet = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                            at);
                        PacketChannel.CHANNEL.sendToAllAround(packet, PacketChannel.pointFromPos(world, at, 16.0));
                    }
                    break;
                case FIRE:
                    if (world.rand.nextFloat() > percStrength) return;

                    ItemStack blockStack = ItemUtils.createBlockStack(state);
                    if (blockStack == null || blockStack.stackSize <= 0) return;
                    ItemStack result = FurnaceRecipes.smelting()
                        .getSmeltingResult(blockStack);
                    if (result == null || result.stackSize <= 0) return;

                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_MELT_BLOCK, at);
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 16.0));
                    if (world.rand.nextInt(20) != 0) {
                        return;
                    }
                    Block resState = ItemUtils.createBlockState(result);
                    if (resState != null) {
                        world.setBlock(at.getX(), at.getY(), at.getZ(), resState, 0, 3);
                    }
                    break;
                /*
                 * case HARVEST:
                 * if(world.rand.nextFloat() > percStrength) return;
                 * CropHelper.HarvestablePlant harvest = CropHelper.wrapHarvestablePlant(world, at);
                 * if(harvest != null) {
                 * harvest.tryGrow(world, world.rand);
                 * if(harvest.canHarvest(world)) {
                 * List<ItemStack> drops = harvest.harvestDropsAndReplant(world, world.rand, 4);
                 * for (ItemStack st : drops) {
                 * ItemUtils.dropItemNaturally(world, at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, st);
                 * }
                 * }
                 * PktParticleEvent packet = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                 * at);
                 * PacketChannel.CHANNEL.sendToAllAround(packet, PacketChannel.pointFromPos(world, at, 16));
                 * }
                 * break;
                 */
                default:
                    break;
            }
        }

    }

    public static enum TargetType {

        ENTITY,
        BLOCK,
        NONE,
        ANY

    }

}
