/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.knowledge;

import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.cleanroommc.modularui.utils.item.InvWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.data.KnowledgeFragmentData;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRemoveKnowledgeFragment;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemKnowledgeFragment
 * Created by HellFirePvP
 * Date: 21.10.2018 / 14:35
 */
public class ItemKnowledgeFragment extends Item implements ItemHighlighted {

    public ItemKnowledgeFragment() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    // 1.7.10: getSubItems signature without @Override
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> items) {}

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    // 1.7.10: getRarity signature without @Override (doesn't exist in Item base class)
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityRelic;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean showAdvanced) {
        // 1.7.10: Optional doesn't exist, check if seed != 0
        if (getSeed(stack) != 0) {
            if (hasConstellationInformation(stack)) {
                tooltip.add(EnumChatFormatting.GRAY + I18n.format("misc.fragment.constellation.desc.1"));
                tooltip.add(EnumChatFormatting.GRAY + I18n.format("misc.fragment.constellation.desc.2"));
            } else if (resolveFragment(stack) != null) {
                tooltip.add(EnumChatFormatting.GRAY + I18n.format("misc.fragment.content.desc.1"));
                tooltip.add(EnumChatFormatting.GRAY + I18n.format("misc.fragment.content.desc.2"));
            } else {
                tooltip.add(EnumChatFormatting.GRAY + I18n.format("misc.fragment.content.empty"));
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // 1.7.10: Optional doesn't exist, check if seed == 0
        if (!worldIn.isRemote && !(stack == null || stack.stackSize <= 0)
            && stack.getItem() instanceof ItemKnowledgeFragment
            && getSeed(stack) == 0) {
            stack.stackSize = 0;
        }
    }

    @Override
    // 1.7.10: onItemRightClick returns ItemStack directly
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            if (hasConstellationInformation(stack)) {
                player.openGui(
                    AstralSorcery.instance,
                    CommonProxy.EnumGuiId.KNOWLEDGE_CONSTELLATION.ordinal(),
                    world,
                    0,
                    0,
                    0);
                return stack;
            } else {
                KnowledgeFragment frag = resolveFragment(stack);
                if (frag != null) {
                    ItemKnowledgeFragment.clearFragment(player, frag);
                    KnowledgeFragmentData dat = PersistentDataManager.INSTANCE
                        .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
                    if (dat.addFragment(frag)) {
                        player.addChatMessage(
                            new ChatComponentText(
                                EnumChatFormatting.GREEN
                                    + I18n.format("misc.fragment.added", frag.getLocalizedIndexName())));
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (!entityItem.worldObj.isRemote) {
            // 1.7.10: EntityItem.getItem() → getEntityItem()
            ItemStack stack = entityItem.getEntityItem();
            // 1.7.10: Optional doesn't exist, check if seed == 0
            if (!(stack == null || stack.stackSize <= 0) && stack.getItem() instanceof ItemKnowledgeFragment
                && getSeed(stack) == 0) {
                entityItem.setDead();
                stack.stackSize = 0;
                entityItem.setEntityItemStack(stack);
            }
        }
        return false;
    }

    static void generateSeed(EntityPlayer player, ItemStack stack) {
        if (!(stack == null || stack.stackSize <= 0) && stack.getItem() instanceof ItemKnowledgeFragment) {
            long baseRand = (((player.getEntityId() << 6) | (System.currentTimeMillis() & 223)) << 16)
                | player.worldObj.getWorldTime();
            Random r = new Random(baseRand);
            r.nextLong();
            setSeed(stack, r.nextLong());
        }
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return new Color(0xCEEAFF);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        EntityItemHighlighted ei = new EntityItemHighlighted(world, entity.posX, entity.posY, entity.posZ, itemstack);
        // 1.7.10: Use delayBeforeCanPickup field instead of setDefaultPickupDelay()
        ei.delayBeforeCanPickup = 10;
        ei.motionX = entity.motionX;
        ei.motionY = entity.motionY;
        ei.motionZ = entity.motionZ;
        // 1.7.10: EntityItem doesn't have getThrower/setThrower or getOwner/setOwner methods
        // These methods don't exist in 1.7.10, skip setting thrower/owner
        return ei;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static Tuple<IConstellation, List<MoonPhase>> getConstellationInformation(ItemStack stack) {
        KnowledgeFragment frag = resolveFragment(stack);
        if (frag != null) {
            // 1.7.10: Optional doesn't exist, use long directly
            long seed = getSeed(stack);
            if (seed != 0) {
                IConstellation cst = frag.getDiscoverConstellation(seed);
                List<MoonPhase> phases = frag.getShowupPhases(seed);
                if (cst != null && !phases.isEmpty()) {
                    return new Tuple<>(cst, phases);
                }
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static boolean hasConstellationInformation(ItemStack stack) {
        return getConstellationInformation(stack) != null;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static KnowledgeFragment resolveFragment(ItemStack stack) {
        // 1.7.10: Optional doesn't exist, use long directly (0 = not present)
        long seed = getSeed(stack);
        if (seed == 0) return null;
        Random sRand = new Random(seed);
        KnowledgeFragmentData dat = PersistentDataManager.INSTANCE
            .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
        List<KnowledgeFragment> all = dat.getDiscoverableFragments();
        Iterator<KnowledgeFragment> it = all.iterator();
        while (it.hasNext()) {
            if (!it.next()
                .isFullyPresent()) {
                it.remove();
            }
        }
        // 1.7.10: Check List.isEmpty() instead of stackSize
        if ((all == null || all.isEmpty())) return null;
        int index = sRand.nextInt(all.size());
        return all.get(index);
    }

    @SideOnly(Side.CLIENT)
    public static List<ItemStack> gatherFragments(EntityPlayer player) {
        Collection<ItemStack> fragItems = ItemUtils
            .findItemsInInventory(new InvWrapper(player.inventory), new ItemStack(ItemsAS.knowledgeFragment), false);
        List<ItemStack> frags = new LinkedList<>();
        for (ItemStack stack : fragItems) {
            if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemKnowledgeFragment))
                continue;
            KnowledgeFragment fr = resolveFragment(stack);
            if (fr != null) {
                frags.add(stack);
            }
        }
        return frags;
    }

    @SideOnly(Side.CLIENT)
    public static void clearFragment(EntityPlayer player, KnowledgeFragment frag) {
        Map<Integer, ItemStack> fragItems = ItemUtils.findItemsIndexedInInventory(
            new InvWrapper(player.inventory),
            new ItemStack(ItemsAS.knowledgeFragment),
            false);
        for (Map.Entry<Integer, ItemStack> entry : fragItems.entrySet()) {
            ItemStack stack = entry.getValue();
            if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemKnowledgeFragment))
                continue;
            KnowledgeFragment fr = resolveFragment(stack);
            if (fr != null && fr.equals(frag)) {
                PacketChannel.CHANNEL.sendToServer(new PktRemoveKnowledgeFragment(entry.getKey()));
                player.inventory.setInventorySlotContents(entry.getKey(), null);
                break;
            }
        }
    }

    public static void setSeed(ItemStack stack, long seed) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemKnowledgeFragment)) return;
        NBTHelper.getPersistentData(stack)
            .setLong("seed", seed);
    }

    // 1.7.10: Optional doesn't exist, return long directly (0 = not present)
    public static long getSeed(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemKnowledgeFragment)) {
            return 0;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("seed")) {
            return 0;
        }
        return cmp.getLong("seed");
    }

}
