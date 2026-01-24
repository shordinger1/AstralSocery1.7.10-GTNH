/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import java.util.EnumSet;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentPlayerWornTick;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.registry.RegistryEnchantments;
import hellfirepvp.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAmuletHandler
 * Created by HellFirePvP
 * Date: 14.04.2018 / 17:49
 */
public class PlayerAmuletHandler implements ITickHandler {

    public static final PlayerAmuletHandler INSTANCE = new PlayerAmuletHandler();

    private PlayerAmuletHandler() {}

    @SubscribeEvent
    public void attachAmuletItemCapability(AttachCapabilitiesEvent<ItemStack> itemCapEvent) {
        if (!EnchantmentUpgradeHelper.isItemBlacklisted(itemCapEvent.getObject())) {
            itemCapEvent
                .addCapability(AmuletHolderCapability.CAP_AMULETHOLDER_NAME, new AmuletHolderCapability.Provider());
        }
    }

    @SubscribeEvent
    public void onAmuletEnchantApply(DynamicEnchantmentEvent.Add event) {
        if (EnchantmentUpgradeHelper.isItemBlacklisted(event.getEnchantedItemStack())) return;
        Tuple<ItemStack, EntityPlayer> linkedAmulet = EnchantmentUpgradeHelper
            .getWornAmulet(event.getEnchantedItemStack());
        if (linkedAmulet == null || linkedAmulet.key == null
            || linkedAmulet.key.stackSize <= 0
            || linkedAmulet.value == null) return;

        event.getEnchantmentsToApply()
            .addAll(ItemEnchantmentAmulet.getAmuletEnchantments(linkedAmulet.key));
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EntityPlayer player = (EntityPlayer) context[0];
        applyAmuletTags(player);
        clearAmuletTags(player);

        boolean client = player.worldObj.isRemote;
        for (EnchantmentPlayerWornTick e : RegistryEnchantments.wearableTickEnchantments) {
            int max = EnchantmentHelper.getMaxEnchantmentLevel(e, player);
            if (max > 0) {
                e.onWornTick(client, player, max);
            }
        }
    }

    private void applyAmuletTags(EntityPlayer player) {
        // 1.7.10: Check main hand and armor slots manually
        ItemStack mainHand = player.getCurrentEquippedItem();
        if (!(mainHand == null || mainHand.stackSize <= 0) && !EnchantmentUpgradeHelper.isItemBlacklisted(mainHand)) {
            EnchantmentUpgradeHelper.applyAmuletOwner(mainHand, player);
        }
        for (int i = 0; i < player.inventory.armorInventory.length; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (!(stack == null || stack.stackSize <= 0) && !EnchantmentUpgradeHelper.isItemBlacklisted(stack)) {
                EnchantmentUpgradeHelper.applyAmuletOwner(stack, player);
            }
        }
    }

    private void clearAmuletTags(EntityPlayer player) {
        EnchantmentUpgradeHelper.removeAmuletTagsAndCleanup(player, true);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "PlayerAmuletHandler";
    }

}
