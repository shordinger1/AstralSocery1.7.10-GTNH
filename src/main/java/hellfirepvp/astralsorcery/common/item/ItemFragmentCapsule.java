/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Fragment Capsule Item - Single-use item that spawns knowledge fragments
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;

/**
 * Fragment Capsule Item
 * <p>
 * Single-use item that, when used, spawns a knowledge fragment.
 * Features:
 * - Max stack size: 1
 * - Has highlight effect (glowing)
 * - Rare rarity
 * - 15 second lifetime as dropped item
 * - Explosion resistant (custom entity)
 * <p>
 * TODO:
 * - Implement ItemHighlighted interface
 * - Implement EntityItemExplosionResistant
 * - Add knowledge fragment generation logic
 * - Add breaking sound effect
 */
public class ItemFragmentCapsule extends AstralBaseItem {

    public ItemFragmentCapsule() {
        super(1); // Max stack size 1
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // TODO: Spawn knowledge fragment at player position
            // For now, just consume the item
            stack.stackSize--;
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; // Glowing effect
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // TODO: Create rarityRelic in RegistryItems
        return EnumRarity.rare;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add("§7Right-click to open and receive");
        tooltip.add("§ea knowledge fragment");
        tooltip.add("§cSingle use");
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        // TODO: Return true when EntityItemExplosionResistant is implemented
        return false;
    }

    // TODO: Implement createEntity() for explosion-resistant entity
}
