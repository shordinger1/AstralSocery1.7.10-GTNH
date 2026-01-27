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

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.WRItemObject;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemConstellationPaper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:16
 */
public class ItemConstellationPaper extends Item implements ItemHighlighted, ItemDynamicColor {

    public ItemConstellationPaper() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorceryPapers);
    }

    // Removed @Override - 1.7.10 compatibility
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> items) {
        // 1.7.10 compatibility: Item.isInCreativeTab() doesn't exist, use tab == this.getCreativeTab() instead
        if (tab == this.getCreativeTab()) {
            items.add(new ItemStack(this, 1));

            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                ItemStack cPaper = new ItemStack(this, 1);
                setConstellation(cPaper, c);
                items.add(cPaper);
            }
        }
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFFFF;
        IConstellation c = getConstellation(stack);
        if (c != null) {
            if (ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                return 0xFF000000 | c.getConstellationColor()
                    .getRGB();
            }
        }
        return 0xFF333333;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        EntityItemHighlighted ei = new EntityItemHighlighted(world, entity.posX, entity.posY, entity.posZ, itemstack);
        // 1.7.10: setDefaultPickupDelay() doesn't exist
        ei.delayBeforeCanPickup = 10;
        ei.motionX = entity.motionX;
        ei.motionY = entity.motionY;
        ei.motionZ = entity.motionZ;
        // 1.7.10: EntityItem doesn't have getThrower() or getOwner()
        // These methods don't exist in this version
        if (entity instanceof EntityItem) {
            // 1.7.10: No thrower/owner tracking available
        }
        return ei;
    }

    // Removed @Override - different signature in 1.7.10
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip) {
        IConstellation c = getConstellation(stack);
        if (c != null && c.canDiscover(Minecraft.getMinecraft().thePlayer, ResearchManager.clientProgress)) {
            tooltip.add(EnumChatFormatting.BLUE + I18n.format(c.getUnlocalizedName()));
        } else {
            tooltip.add(EnumChatFormatting.GRAY + I18n.format("constellation.noInformation"));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (itemStackIn == null || itemStackIn.stackSize <= 0) return itemStackIn;
        if (worldIn.isRemote && getConstellation(itemStackIn) != null) {
            // 1.7.10: playSound expects String, not ResourceLocation
            playerIn.playSound(
                Sounds.bookFlip.getSoundName()
                    .toString(),
                1F,
                1F);
            AstralSorcery.proxy.openGui(
                CommonProxy.EnumGuiId.CONSTELLATION_PAPER,
                playerIn,
                worldIn,
                ConstellationRegistry.getConstellationId(getConstellation(itemStackIn)),
                0,
                0);
        }
        return itemStackIn;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || entityIn == null || !(entityIn instanceof EntityPlayer)) return;

        IConstellation cst = getConstellation(stack);

        if (cst == null) {
            PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn, Side.SERVER);

            List<IConstellation> constellations = new ArrayList<>();
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                if (c.canDiscover((EntityPlayer) entityIn, progress)) {
                    constellations.add(c);
                }
            }

            for (String strConstellation : progress.getKnownConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellationByName(strConstellation);
                if (c != null) {
                    constellations.remove(c);
                }
            }
            for (String strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellationByName(strConstellation);
                if (c != null) {
                    constellations.remove(c);
                }
            }

            if ( constellations.isEmpty()) {
                // 1.7.10: Just pick a random constellation from the list
                IConstellation result = constellations.get(worldIn.rand.nextInt(0));
                setConstellation(stack, result);
            }
        }

        cst = getConstellation(stack);
        if (cst != null) {
            PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn, Side.SERVER);

            boolean has = false;
            for (String strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellationByName(strConstellation);
                if (c != null && c.equals(cst)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                if (ResearchManager.memorizeConstellation(cst, (EntityPlayer) entityIn)) {
                    // 1.7.10: Need to cast to EntityPlayer, chat API is simpler
                    ((EntityPlayer) entityIn).addChatMessage(
                        new ChatComponentTranslation(
                            "progress.seen.constellation.chat",
                            new ChatComponentTranslation(cst.getUnlocalizedName())));
                    if (ResearchManager.clientProgress.getSeenConstellations()
                        .size() == 1) {
                        ((EntityPlayer) entityIn)
                            .addChatMessage(new ChatComponentTranslation("progress.seen.constellation.first.chat"));
                    }
                }
            }
        }
    }

    private List<WRItemObject<IConstellation>> buildWeightedRandomList(List<IConstellation> constellations) {
        List<WRItemObject<IConstellation>> wrc = new ArrayList<>();
        for (IConstellation c : constellations) {
            WRItemObject<IConstellation> i = new WRItemObject<>(1, c);// (int) (tier.getShowupChance() * 100), c);
            wrc.add(i);
        }
        return wrc;
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        IConstellation c = getConstellation(stack);
        if (c != null) {
            if (ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                return c.getConstellationColor();
            }
            return c.getTierRenderColor();
        }
        return Color.GRAY;
    }

    public static IConstellation getConstellation(ItemStack stack) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) return null;
        return IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public static void setConstellation(ItemStack stack, IConstellation constellation) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) return;
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        constellation.writeToNBT(tag);
    }

}
