/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;

import hellfirepvp.astralsorcery.common.util.ItemUtils;
import vazkii.botania.api.item.IBlockProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationBotania
 * Created by HellFirePvP
 * Date: 21.04.2017 / 00:38
 */
public class ModIntegrationBotania {

    private ModIntegrationBotania() {}

    // Empty if no provider can provide that, an item (size 1) if it could be requested successfully.
    @Nonnull
    public static ItemStack requestFromInventory(EntityPlayer requestingPlayer, ItemStack requestingStack, Block block,
        int meta, boolean doit) {
        // In 1.7.10, use InvWrapper instead of capability system
        IItemHandler inv = new InvWrapper(requestingPlayer.inventory);
        if (inv == null) {
            return null;
        }
        List<ItemStack> providers = new LinkedList<>();
        for (int i = inv.getSlots() - 1; i >= 0; i--) {
            ItemStack invStack = inv.getStackInSlot(i);
            if (!(invStack == null || invStack.stackSize <= 0)) {
                Item item = invStack.getItem();
                if ((item instanceof IBlockProvider)) {
                    providers.add(invStack);
                }
            }
        }
        for (ItemStack provStack : providers) {
            IBlockProvider prov = (IBlockProvider) provStack.getItem();
            if (prov.provideBlock(requestingPlayer, requestingStack, provStack, block, meta, doit)) {
                return new ItemStack(block, 1, meta);
            }
        }
        return null;
    }

    // -1 = infinite
    public static int getItemCount(EntityPlayer requestingPlayer, ItemStack requestingStack,
        @Nullable Block stateSearch) {
        if (stateSearch == null) return 0;
        Block block = stateSearch;
        int meta;
        try {
            meta = block.damageDropped(stateSearch);
        } catch (Exception e) {
            meta = 0;
        }
        ItemStack blockStackStored = ItemUtils.createBlockStack(stateSearch);
        // In 1.7.10, use InvWrapper instead of capability system
        IItemHandler inv = new InvWrapper(requestingPlayer.inventory);
        if (inv == null) {
            return 0;
        }
        int amtFound = 0;
        for (int i = inv.getSlots() - 1; i >= 0; i--) {
            ItemStack invStack = inv.getStackInSlot(i);
            if (!(invStack == null || invStack.stackSize <= 0)) {
                Item item = invStack.getItem();
                if ((item instanceof IBlockProvider)) {
                    int res = ((IBlockProvider) item)
                        .getBlockCount(requestingPlayer, requestingStack, invStack, block, meta);
                    if (res == -1) {
                        return -1;
                    } else {
                        amtFound += res;
                    }
                }
            }
        }

        Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(inv, blockStackStored, false);
        for (ItemStack stack : stacks) {
            amtFound += stack.stackSize;
        }
        return amtFound;
    }

}
