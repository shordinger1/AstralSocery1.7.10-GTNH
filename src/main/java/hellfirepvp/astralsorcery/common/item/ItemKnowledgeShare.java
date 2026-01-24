/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.render.INBTModel;
import hellfirepvp.astralsorcery.common.migration.ModelResourceLocation;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemKnowledgeShare
 * Created by HellFirePvP
 * Date: 05.07.2017 / 11:39
 */
public class ItemKnowledgeShare extends Item implements INBTModel {

    public ItemKnowledgeShare() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> items) {
        // 1.7.10: Use tab == this.getCreativeTab() instead of isInCreativeTab()
        if (tab == this.getCreativeTab()) {
            items.add(new ItemStack(this));

            ItemStack creative = new ItemStack(this);
            setCreative(creative);
            items.add(creative);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip) {
        if (isCreative(stack)) {
            tooltip.add(EnumChatFormatting.LIGHT_PURPLE + I18n.format("misc.knowledge.inscribed.creative"));
            return;
        }
        if (getKnowledge(stack) == null) {
            tooltip.add(I18n.format("misc.knowledge.missing"));
        } else {
            String name = getKnowledgeOwnerName(stack);
            if (name != null) {
                tooltip.add(I18n.format("misc.knowledge.inscribed", (EnumChatFormatting.BLUE + name)));
            }
        }
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation suggestedDefaultLocation) {
        if (isCreative(stack) || getKnowledgeOwnerName(stack) != null) {
            return new ModelResourceLocation(
                new ResourceLocation(
                    suggestedDefaultLocation.getResourceDomain(),
                    suggestedDefaultLocation.getResourcePath() + "_written"),
                suggestedDefaultLocation.getVariant());
        }
        return suggestedDefaultLocation;
    }

    @Override
    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation) {
        List<ResourceLocation> out = new LinkedList<>();
        out.add(defaultLocation);
        out.add(
            new ResourceLocation(defaultLocation.getResourceDomain(), defaultLocation.getResourcePath() + "_written"));
        return out;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        ItemStack stack = itemStackIn;
        if ((stack == null || stack.stackSize <= 0) || worldIn.isRemote) {
            return stack;
        }
        if (!isCreative(stack) && (playerIn.isSneaking() || getKnowledge(stack) == null)) {
            tryInscribeKnowledge(stack, playerIn);
        } else {
            tryGiveKnowledge(stack, playerIn);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World worldIn, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        if ((stack == null || stack.stackSize <= 0) || worldIn.isRemote) {
            return true;
        }
        if (!isCreative(stack) && (player.isSneaking() || getKnowledge(stack) == null)) {
            tryInscribeKnowledge(stack, player);
        } else {
            tryGiveKnowledge(stack, player);
        }
        return true;
    }

    private void tryGiveKnowledge(ItemStack stack, EntityPlayer player) {
        if (player instanceof EntityPlayerMP && MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            return;
        }

        if (isCreative(stack)) {
            ResearchManager.forceMaximizeAll(player);
            return;
        }
        if (canInscribeKnowledge(stack, player)) return; // Means it's either empty or the player that has incsribed the
                                                         // knowledge is trying to use it.
        PlayerProgress progress = getKnowledge(stack);
        if (progress == null) return;
        ProgressionTier prev = progress.getTierReached();
        if (ResearchManager.mergeApplyPlayerprogress(progress, player) && progress.getTierReached()
            .isThisLater(prev)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(progress.getTierReached());
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }
    }

    private void tryInscribeKnowledge(ItemStack stack, EntityPlayer player) {
        if (canInscribeKnowledge(stack, player)) {
            setKnowledge(stack, player, ResearchManager.getProgress(player, Side.SERVER));
        }
    }

    @Nullable
    public EntityPlayer getKnowledgeOwner(ItemStack stack, MinecraftServer server) {
        if (isCreative(stack)) return null;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!NBTHelper.hasUniqueId(compound, "knowledgeOwnerUUID")) {
            return null;
        }
        UUID owner = NBTHelper.getUniqueId(compound, "knowledgeOwnerUUID");
        return server.getPlayerList()
            .getPlayerByUUID(owner);
    }

    @Nullable
    public String getKnowledgeOwnerName(ItemStack stack) {
        if (isCreative(stack)) return null;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasKey("knowledgeOwnerName")) {
            return null;
        }
        return compound.getString("knowledgeOwnerName");
    }

    @Nullable
    public PlayerProgress getKnowledge(ItemStack stack) {
        if (isCreative(stack)) return null;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasKey("knowledgeTag")) {
            return null;
        }
        NBTTagCompound tag = compound.getCompoundTag("knowledgeTag");
        try {
            PlayerProgress progress = new PlayerProgress();
            progress.loadKnowledge(tag);
            return progress;
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean canInscribeKnowledge(ItemStack stack, EntityPlayer player) {
        if (isCreative(stack)) return false;
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        if (!NBTHelper.hasUniqueId(compound, "knowledgeOwnerUUID")) {
            return true;
        }
        UUID owner = NBTHelper.getUniqueId(compound, "knowledgeOwnerUUID");
        return player.getUniqueID()
            .equals(owner);
    }

    public void setKnowledge(ItemStack stack, EntityPlayer player, PlayerProgress progress) {
        if (isCreative(stack) || !progress.isValid()) return;

        NBTTagCompound knowledge = new NBTTagCompound();
        progress.storeKnowledge(knowledge);
        NBTTagCompound compound = NBTHelper.getPersistentData(stack);
        compound.setString("knowledgeOwnerName", player.getName());
        NBTHelper.setUniqueId(compound, "knowledgeOwnerUUID", player.getUniqueID());
        compound.setTag("knowledgeTag", knowledge);
    }

    public boolean isCreative(ItemStack stack) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("creativeKnowledge")) {
            return false;
        }
        return cmp.getBoolean("creativeKnowledge");
    }

    private void setCreative(ItemStack stack) {
        NBTHelper.getPersistentData(stack)
            .setBoolean("creativeKnowledge", true);
    }

}
