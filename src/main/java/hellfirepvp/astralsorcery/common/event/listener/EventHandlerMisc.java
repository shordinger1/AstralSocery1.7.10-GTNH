/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.event.ItemEnchantmentTooltipEvent;
// import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet; // TODO: Implement

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerMisc
 * Created by HellFirePvP
 * Date: 04.11.2016 / 23:42
 */
public class EventHandlerMisc {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onToolTip(ItemTooltipEvent event) {
        List toolTip = event.toolTip;
        ItemStack stack = event.itemStack;

        /*
         * TODO: Implement ItemEnchantmentAmulet
         * if (stack.getItem() instanceof ItemEnchantmentAmulet && ItemEnchantmentAmulet.getAmuletColor(stack)
         * .orElse(0) == 0xFFFFFFFF) {
         * List<String> newTooltip = new LinkedList<>();
         * if (toolTip.size() > 1) {
         * newTooltip.addAll(toolTip);
         * newTooltip.add(
         * 1,
         * EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC.toString()
         * + StatCollector.translateToLocal("item.itemenchantmentamulet.pure"));
         * } else {
         * newTooltip.add(
         * EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC.toString()
         * + StatCollector.translateToLocal("item.itemenchantmentamulet.pure"));
         * newTooltip.addAll(toolTip);
         * }
         * toolTip.clear();
         * toolTip.addAll(newTooltip);
         * }
         */

        /*
         * TODO: Implement SwordSharpenHelper
         * if (SwordSharpenHelper.isSwordSharpened(stack)) {
         * List<String> newTooltip = new LinkedList<>();
         * if (toolTip.size() > 1) {
         * newTooltip.addAll(toolTip);
         * newTooltip.add(
         * 1,
         * StatCollector.translateToLocalFormatted(
         * "misc.sword.sharpened",
         * String.valueOf(Math.round(Config.swordSharpMultiplier * 100)) + "%"));
         * } else {
         * newTooltip.add(
         * StatCollector.translateToLocalFormatted(
         * "misc.sword.sharpened",
         * String.valueOf(Math.round(Config.swordSharpMultiplier * 100)) + "%"));
         * newTooltip.addAll(toolTip);
         * }
         * toolTip.clear();
         * toolTip.addAll(newTooltip);
         * }
         */
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEnchTooltip(ItemEnchantmentTooltipEvent event) {
        List toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();

        @SuppressWarnings("unchecked")
        Map enchantments = EnchantmentHelper.getEnchantments(stack);
        if (stack.stackTagCompound == null && !enchantments.isEmpty()) {
            for (Object e : enchantments.keySet()) {
                if (e instanceof Enchantment) {
                    toolTip.add(((Enchantment) e).getTranslatedName((Integer) enchantments.get(e)));
                }
            }
        }
    }

    // Player CAP stuffs.

    /*
     * @SubscribeEvent
     * public void onAttach(AttachCapabilitiesEvent<Entity> event) {
     * if(event.getObject() instanceof EntityPlayer) {
     * event.addCapability(new ResourceLocation(AstralSorcery.MODID, "constellationperks"), new
     * IPlayerCapabilityPerks.Provider());
     * }
     * }
     * @SubscribeEvent
     * public void onClone(PlayerEvent.Clone event) {
     * IPlayerCapabilityPerks current = PlayerPerkHelper.getPerks(event.getEntityPlayer());
     * IPlayerCapabilityPerks cloned = PlayerPerkHelper.getPerks(event.getEntityPlayer());
     * if(cloned != null && current != null) {
     * cloned.updatePerks(current.getAttunedConstellation(), current.getCurrentPlayerPerks());
     * }
     * }
     */

}
