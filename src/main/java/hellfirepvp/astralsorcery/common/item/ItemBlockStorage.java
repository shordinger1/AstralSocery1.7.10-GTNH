/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockStorage
 * Created by HellFirePvP
 * Date: 03.03.2017 / 17:08
 */
public abstract class ItemBlockStorage extends Item {

    public static void tryStoreBlock(ItemStack storeIn, World w, BlockPos pos) {
        if (w.getTileEntity(pos) != null) return;
        Block stateToStore = w.getBlock(pos);
        if (Item.getItemFromBlock(stateToStore) == Items.AIR) return; // Can't charge the player anyway.
        if (stateToStore.getBlockHardness(w, pos) == -1) return;
        NBTTagCompound stateTag = NBTHelper.getBlockStateNBTTag(stateToStore);

        NBTTagCompound cmp = NBTHelper.getPersistentData(storeIn);
        NBTTagList list = cmp.getTagList("storedStates", Constants.NBT.TAG_COMPOUND);
        list.appendTag(stateTag);
        cmp.setTag("storedStates", list);
    }

    @Nonnull
    public static Map<Block, ItemStack> getMappedStoredStates(ItemStack referenceContainer) {
        List<Block> blockStates = getStoredStates(referenceContainer);
        Map<Block, ItemStack> map = new LinkedHashMap<>();
        for (Block state : blockStates) {
            ItemStack stack = ItemUtils.createBlockStack(state);
            if (!(stack == null || stack.stackSize <= 0)) {
                map.put(state, stack);
            }
        }
        return map;
    }

    @Nonnull
    private static ArrayList<Block> getStoredStates(ItemStack referenceContainer) {
        ArrayList<Block> states = new ArrayList<>();
        if (!(referenceContainer == null || referenceContainer.stackSize <= 0)
            && referenceContainer.getItem() instanceof ItemBlockStorage) {
            NBTTagCompound persistent = NBTHelper.getPersistentData(referenceContainer);
            NBTTagList stored = persistent.getTagList("storedStates", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < stored.tagCount(); i++) {
                Block state = NBTHelper.getBlockStateFromTag(stored.getCompoundTagAt(i));
                if (state != null) {
                    states.add(state);
                }
            }
        }
        return states;
    }

    public static void tryClearContainerFor(EntityPlayer player) {
        ItemStack used = player.getHeldItem();
        if (!(used == null || used.stackSize <= 0) && used.getItem() instanceof ItemBlockStorage) {
            NBTHelper.getPersistentData(used)
                .removeTag("storedStates");
        }
    }

    protected static Random getPreviewRandomFromWorld(World world) {
        long tempSeed = 0x6834F10A91B03F15L;
        tempSeed *= (world.getWorldTime() / 40) << 8;
        return new Random(tempSeed);
    }
}
