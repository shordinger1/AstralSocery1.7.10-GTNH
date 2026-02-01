/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk Seal Item - Consumable item to unlock perk slots
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;

/**
 * Perk Seal Item
 * <p>
 * Consumable item that unlocks one perk slot when used.
 * This is a very simple item with minimal functionality.
 * <p>
 * TODO:
 * - Implement perk slot unlocking logic
 * - Link with perk system
 * - Add sound effects
 */
public class ItemPerkSeal extends AstralBaseItem {

    public ItemPerkSeal() {
        super(64); // Max stack size 64
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // Grant free allocation point for perk tree
            String token = "perk_seal_" + System.currentTimeMillis();
            if (hellfirepvp.astralsorcery.common.data.research.ResearchManager.grantFreePerkPoint(
                player,
                token)) {
                hellfirepvp.astralsorcery.common.util.LogHelper.info(
                    "Player " + player.getCommandSenderName() + " used Perk Seal - granted free perk point");
            }
            // Consume one item
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add("§7Right-click to unlock");
        tooltip.add("§ea perk slot");
        tooltip.add("§3Used in constellation progression");
    }
}
