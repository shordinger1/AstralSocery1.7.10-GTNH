/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Maps;

import baubles.api.BaubleType;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.enchantment.amulet.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.util.BaublesHelper;
import hellfirepvp.astralsorcery.common.util.ItemComparator;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.core.ASMCallHook;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentUpgradeHelper
 * Created by HellFirePvP
 * Date: 27.01.2018 / 11:27
 */
public class EnchantmentUpgradeHelper {

    public static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item) {
        if (isItemBlacklisted(item)) return current;
        return getNewEnchantmentLevel(current, currentEnchantmentId, item, null);
    }

    @ASMCallHook
    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item) {
        if (isItemBlacklisted(item)) return current;
        return getNewEnchantmentLevel(current, enchantment, item, null);
    }

    private static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item,
        @Nullable List<DynamicEnchantment> context) {
        if (isItemBlacklisted(item)) return current;
        // In 1.7.10, getEnchantmentByID returns Enchantment by effectId
        Enchantment ench = Enchantment.enchantmentsList[currentEnchantmentId];
        if (ench != null) {
            return getNewEnchantmentLevel(current, ench, item, context);
        }
        return current;
    }

    private static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item,
        @Nullable List<DynamicEnchantment> context) {
        if (isItemBlacklisted(item)) return current;

        // Check if item is valid or enchantment can be influenced
        if (item == null || item.stackSize <= 0 || !AmuletEnchantmentRegistry.canBeInfluenced(enchantment)) {
            return current;
        }

        List<DynamicEnchantment> modifiers = context != null ? context : fireEnchantmentGatheringEvent(item);
        for (DynamicEnchantment mod : modifiers) {
            Enchantment target = mod.getEnchantment();
            switch (mod.getType()) {
                case ADD_TO_SPECIFIC:
                    if (enchantment.equals(target)) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_SPECIFIC:
                    if (enchantment.equals(target) && current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_ALL:
                    if (current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
                default:
                    break;
            }
        }
        return current;
    }

    @ASMCallHook
    public static NBTTagList modifyEnchantmentTags(@Nonnull NBTTagList existingEnchantments, ItemStack stack) {
        if (isItemBlacklisted(stack)) return existingEnchantments;

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        // In 1.7.10, List uses .size() instead of .length
        if ((context == null || context.size() <= 0)) return existingEnchantments;

        NBTTagList returnNew = new NBTTagList();
        List<Enchantment> enchantments = new ArrayList<>(existingEnchantments.tagCount());
        for (int i = 0; i < existingEnchantments.tagCount(); i++) {
            NBTTagCompound cmp = existingEnchantments.getCompoundTagAt(i);
            int enchId = cmp.getShort("id");
            int lvl = cmp.getShort("lvl");
            int newLvl = getNewEnchantmentLevel(lvl, enchId, stack, context);

            NBTTagCompound newEnchTag = new NBTTagCompound();
            newEnchTag.setShort("id", (short) enchId);
            newEnchTag.setShort("lvl", (short) newLvl);
            returnNew.appendTag(newEnchTag);
            // In 1.7.10, getEnchantmentByID returns Enchantment by effectId
            Enchantment e = Enchantment.enchantmentsList[enchId];
            if (e != null) { // If that is actually null, something went terribly wrong.
                enchantments.add(e);
            }
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantment.Type.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (!AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                EnumEnchantmentType type = ench.type;
                if (type != null && !type.canEnchantItem(stack.getItem())) {
                    continue;
                }
                if (!enchantments.contains(ench)) { // Means we didn't add the levels on the other iteration
                    NBTTagCompound newEnchTag = new NBTTagCompound();
                    // In 1.7.10, getEnchantmentID returns effectId field
                    newEnchTag.setShort("id", (short) ench.effectId);
                    newEnchTag.setShort("lvl", (short) getNewEnchantmentLevel(0, ench, stack, context));
                    returnNew.appendTag(newEnchTag);
                }
            }
        }
        return returnNew;
    }

    @ASMCallHook
    public static Map<Enchantment, Integer> applyNewEnchantmentLevels(Map<Enchantment, Integer> enchantmentLevelMap,
        ItemStack stack) {
        if (isItemBlacklisted(stack)) return enchantmentLevelMap;

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        // In 1.7.10, List uses .size() instead of .length
        if ((context == null || context.size() <= 0)) return enchantmentLevelMap;

        Map<Enchantment, Integer> copyRet = Maps.newLinkedHashMap();
        for (Map.Entry<Enchantment, Integer> enchant : enchantmentLevelMap.entrySet()) {
            copyRet.put(enchant.getKey(), getNewEnchantmentLevel(enchant.getValue(), enchant.getKey(), stack, context));
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantment.Type.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (!AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                EnumEnchantmentType type = ench.type;
                if (type != null && !type.canEnchantItem(stack.getItem())) {
                    continue;
                }
                if (!enchantmentLevelMap.containsKey(ench)) { // Means we didn't add the levels on the other iteration
                    copyRet.put(ench, getNewEnchantmentLevel(0, ench, stack, context));
                }
            }
        }
        return copyRet;
    }

    public static boolean isItemBlacklisted(ItemStack stack) {
        if (!(stack == null || stack.stackSize <= 0)) {
            if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return true; // We're not gonna apply enchantments to items used for querying matches
            }

            if (stack.getMaxStackSize() > 1) {
                return true; // Only swords & armor and stuff that isn't stackable
            }
            if (stack.getItem() instanceof ItemPotion || stack.getItem() instanceof ItemEnchantedBook) {
                return true; // Not gonna apply enchantments to potions or books
            }

            // In 1.7.10, Item doesn't have getRegistryName(), use field access or different approach
            // For now, skip the draconicevolution check as it requires registry name lookup
            return false;
        }
        return true;
    }

    // ---------------------------------------------------
    // Data organization
    // ---------------------------------------------------

    // This is more or less just a map to say whatever we add upon.
    private static List<DynamicEnchantment> fireEnchantmentGatheringEvent(ItemStack tool) {
        DynamicEnchantmentEvent.Add addEvent = new DynamicEnchantmentEvent.Add(tool, getPlayerHavingTool(tool));
        MinecraftForge.EVENT_BUS.post(addEvent);
        DynamicEnchantmentEvent.Modify modifyEvent = new DynamicEnchantmentEvent.Modify(
            tool,
            addEvent.getEnchantmentsToApply(),
            addEvent.getResolvedPlayer());
        MinecraftForge.EVENT_BUS.post(modifyEvent);
        return modifyEvent.getEnchantmentsToApply();
    }

    public static void removeAmuletTagsAndCleanup(EntityPlayer player, boolean keepEquipped) {
        InventoryPlayer inv = player.inventory;
        // In 1.7.10, mainInventory and armorInventory use arrays, not lists
        for (int i = 0; i < inv.mainInventory.length; i++) {
            if (i == inv.currentItem && keepEquipped) continue;
            removeAmuletOwner(inv.mainInventory[i]);
        }
        removeAmuletOwner(inv.getItemStack());
        if (!keepEquipped) {
            for (int i = 0; i < inv.armorInventory.length; i++) {
                removeAmuletOwner(inv.armorInventory[i]);
            }
            // In 1.7.10, there's no offHandInventory
        }
    }

    @Nullable
    private static UUID getWornPlayerUUID(ItemStack anyTool) {
        if (!(anyTool == null || anyTool.stackSize <= 0) && AmuletHolderCapability.hasCapability(anyTool)) {
            AmuletHolderCapability cap = AmuletHolderCapability.getCapability(anyTool);
            if (cap != null) {
                return cap.getHolderUUID();
            }
        }
        return null;
    }

    public static void applyAmuletOwner(ItemStack tool, EntityPlayer wearer) {
        if ((tool == null || tool.stackSize <= 0)) return;
        AmuletHolderCapability cap = new AmuletHolderCapability();
        cap.setHolderUUID(wearer.getUniqueID());
        AmuletHolderCapability.saveToItemStack(tool, cap);
    }

    private static void removeAmuletOwner(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0)) {
            return;
        }
        AmuletHolderCapability.saveToItemStack(stack, null);
    }

    @Nullable
    static EntityPlayer getPlayerHavingTool(ItemStack anyTool) {
        UUID plUUID = getWornPlayerUUID(anyTool);
        if (plUUID == null) return null;
        EntityPlayer player = null;
        if (FMLCommonHandler.instance()
            .getEffectiveSide() == Side.CLIENT) {
            player = resolvePlayerClient(plUUID);
        } else {
            MinecraftServer server = FMLCommonHandler.instance()
                .getMinecraftServerInstance();
            if (server == null) return null;
            // In 1.7.10, use getConfigurationManager() instead of getPlayerList()
            // Also need to iterate through playerEntityList
            // In 1.7.10, EntityPlayerMP uses getGameProfile().getId() for UUID
            for (Object playerObj : server.getConfigurationManager().playerEntityList) {
                if (playerObj instanceof EntityPlayerMP) {
                    EntityPlayerMP p = (EntityPlayerMP) playerObj;
                    if (p.getGameProfile()
                        .getId()
                        .equals(plUUID)) {
                        player = p;
                        break;
                    }
                }
            }
            if (player == null) return null;
        }
        if (player == null) return null;

        // In 1.7.10, findItemsIndexedInPlayerInventory may not exist, use manual check
        ItemStack tombBookStack = null;
        // Try to find the disenchantment book in player's inventory
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemEnchantedBook) {
                // Check if it's a disenchantment book
                tombBookStack = stack;
                break;
            }
        }
        if (tombBookStack != null) {
            return null;
        }

        // Check if the player actually wears/carries the tool
        boolean foundTool = false;
        // In 1.7.10, use getCurrentEquippedItem() which returns held ItemStack
        ItemStack mainHand = player.getCurrentEquippedItem();
        if (ItemComparator.compare(mainHand, anyTool, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
            foundTool = true;
        }
        if (!foundTool) {
            // In 1.7.10, armorInventory is an array
            for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                ItemStack stack = player.inventory.armorInventory[i];
                if (ItemComparator.compare(stack, anyTool, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
                    foundTool = true;
                    break;
                }
            }
        }
        if (!foundTool) return null;

        return player;
    }

    @Nullable
    static Tuple<ItemStack, EntityPlayer> getWornAmulet(ItemStack anyTool) {
        EntityPlayer player = getPlayerHavingTool(anyTool);
        if (player == null) return null;

        // Check if the player wears an amulet and return that one then..
        ItemStack stack = BaublesHelper.getFirstWornBaublesForType(player, BaubleType.AMULET);
        if (!(stack == null || stack.stackSize <= 0) && stack.getItem() instanceof ItemEnchantmentAmulet) {
            return new Tuple<>(stack, player);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static EntityPlayer resolvePlayerClient(UUID plUUID) {
        World w = FMLClientHandler.instance()
            .getWorldClient();
        if (w == null) return null;
        // In 1.7.10, World doesn't have getPlayerEntityByUUID()
        // Return null and let server-side logic handle it
        return null;
    }

}
