/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Enchantment Amulet - Amulet with random enchantments
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Enchantment Amulet
 * <p>
 * An amulet that provides random enchantment effects when worn.
 * <p>
 * Features:
 * - Random color assigned on first tick
 * - Random enchantments assigned (TODO)
 * - Worn in bauble slot (if Baubles mod is present)
 * - Can be worn as armor (TODO)
 * <p>
 * TODO:
 * - Implement AmuletEnchantment system
 * - Implement enchantment effects
 * - Implement Baubles API integration
 * - Implement armor slot compatibility
 * - Implement enchantment re-rolling
 */
public class ItemEnchantmentAmulet extends AstralBaseItem {

    private static final String TAG_COLOR = "amulet_color";
    private static final String TAG_ENCHANTMENTS = "amulet_enchantments";

    private static final Random RANDOM = new Random();

    public ItemEnchantmentAmulet() {
        super();
        setMaxStackSize(1);
        setMaxDamage(0);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
        if (!world.isRemote) {
            // Generate color on first update
            if (!hasColor(stack)) {
                generateColor(stack);
            }

            // Generate enchantments on first update
            if (!hasEnchantments(stack)) {
                generateEnchantments(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (hasColor(stack)) {
            int color = getColor(stack);
            Color c = new Color(color);
            tooltip.add(String.format("§7Color: §8R:%d G:%d B:%d", c.getRed(), c.getGreen(), c.getBlue()));
        }

        // TODO: Show enchantments when system is implemented
        // List<AmuletEnchantment> enchants = getEnchantments(stack);
        // for (AmuletEnchantment ench : enchants) {
        // tooltip.add("§7" + ench.getName());
        // }

        if (!hasEnchantments(stack)) {
            tooltip.add("§cNo enchantments yet");
        }
    }

    /**
     * Check if has color
     */
    public static boolean hasColor(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return false;
        }
        return stack.getTagCompound()
            .hasKey(TAG_COLOR);
    }

    /**
     * Get color
     */
    public static int getColor(ItemStack stack) {
        if (!hasColor(stack)) {
            return 0xFFFFFF;
        }
        return stack.getTagCompound()
            .getInteger(TAG_COLOR);
    }

    /**
     * Generate random color
     */
    private void generateColor(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        // Rare chance for white color
        if (RANDOM.nextInt(400) == 0) {
            stack.getTagCompound()
                .setInteger(TAG_COLOR, 0xFFFFFFFF);
        } else {
            // Generate random hue, fixed saturation and brightness
            float hue = RANDOM.nextFloat() * 360F;
            Color color = Color.getHSBColor(hue / 360F, 0.7F, 1.0F);
            stack.getTagCompound()
                .setInteger(TAG_COLOR, color.getRGB() | 0xFF000000);
        }
    }

    /**
     * Check if has enchantments
     */
    public static boolean hasEnchantments(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return false;
        }
        return stack.getTagCompound()
            .hasKey(TAG_ENCHANTMENTS);
    }

    /**
     * Get enchantments list
     */
    public static NBTTagList getEnchantments(ItemStack stack) {
        if (!hasEnchantments(stack)) {
            return new NBTTagList();
        }
        return stack.getTagCompound()
            .getTagList(TAG_ENCHANTMENTS, 10);
    }

    /**
     * Generate random enchantments
     */
    private void generateEnchantments(ItemStack stack) {
        // TODO: Implement AmuletEnchantment system
        // For now, just mark as having enchantments
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        // Placeholder: Create empty enchantment list
        NBTTagList list = new NBTTagList();
        stack.getTagCompound()
            .setTag(TAG_ENCHANTMENTS, list);
    }

    /**
     * Set enchantments
     */
    public static void setEnchantments(ItemStack stack, NBTTagList enchantments) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setTag(TAG_ENCHANTMENTS, enchantments);
    }

    /**
     * NOTE: AmuletEnchantment System
     * <p>
     * Original version:
     * - AmuletEnchantment provides various effects
     * - AmuletEnchantHelper rolls random enchantments
     * - Multiple enchantment types available
     * - Effects applied when worn
     * <p>
     * In 1.7.10:
     * - TODO: Implement AmuletEnchantment class
     * - TODO: Implement AmuletEnchantHelper
     * - TODO: Implement enchantment effects
     * - TODO: Implement Baubles integration
     * - TODO: Implement tick handler for effects
     */

    /**
     * NOTE: Baubles API Integration
     * <p>
     * Original version:
     * - Implements IBauble interface
     * - Worn in amulet slot
     * - Effects applied when equipped
     * <p>
     * In 1.7.10:
     * - TODO: Check if Baubles mod is present
     * - TODO: Implement IBauble if available
     * - TODO: Fallback to armor slot
     * - TODO: Implement onEquipped/onUnequipped
     */
}
