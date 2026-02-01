/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Sextant - Navigation tool for finding structures
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Sextant
 * <p>
 * A navigation tool used to find specific structures.
 * <p>
 * Features:
 * - Can locate different structure types
 * - Advanced version has extended range
 * - GUI for selecting target (TODO)
 * - Shows direction to target (TODO)
 * <p>
 * Usage:
 * - Right-click to open GUI (TODO)
 * - Select target structure type
 * - Points toward nearest structure
 * <p>
 * TODO:
 * - Implement GUI for target selection
 * - Implement SextantFinder system
 * - Implement direction indicator
 * - Implement structure searching
 * - Implement particle trail effects
 */
public class ItemSextant extends AstralBaseItem {

    private static final String TAG_ADVANCED = "advanced";
    private static final String TAG_TARGET = "target_type";
    private static final String TAG_TARGET_X = "target_x";
    private static final String TAG_TARGET_Y = "target_y";
    private static final String TAG_TARGET_Z = "target_z";
    private static final String TAG_TARGET_DIM = "target_dim";

    public ItemSextant() {
        super();
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public String getUnlocalizedName(ItemStack stack) {
        if (isAdvanced(stack)) {
            return super.getUnlocalizedName() + ".advanced";
        }
        return super.getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (tab == this.getCreativeTab()) {
            // Normal version
            list.add(new ItemStack(item));

            // Advanced version
            ItemStack adv = new ItemStack(item);
            setAdvanced(adv);
            list.add(adv);
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // TODO: Open GUI for target selection
            // For now, just print current target info
            if (hasTarget(stack)) {
                int[] target = getTargetPosition(stack);
                player.addChatMessage(
                    new net.minecraft.util.ChatComponentText(
                        "§6[Sextant] §rTarget: §e" + target[0] + ", " + target[1] + ", " + target[2]));
            } else {
                player.addChatMessage(
                    new net.minecraft.util.ChatComponentText("§c[Sextant] No target set. Use GUI to select target."));
            }
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (isAdvanced(stack)) {
            tooltip.add("§7Version: §aAdvanced");
            tooltip.add("§7Extended Range: §e200 blocks");
        } else {
            tooltip.add("§7Version: §fNormal");
            tooltip.add("§7Range: §e100 blocks");
        }

        String target = getTargetType(stack);
        if (target != null && !target.isEmpty()) {
            tooltip.add("§7Target: §e" + target);
        } else {
            tooltip.add("§7Target: §cNone (Right-click to set)");
        }
    }

    /**
     * Check if sextant is advanced
     */
    public static boolean isAdvanced(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return false;
        }
        return stack.getTagCompound()
            .getBoolean(TAG_ADVANCED);
    }

    /**
     * Set sextant as advanced
     */
    public static void setAdvanced(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setBoolean(TAG_ADVANCED, true);
    }

    /**
     * Get target type
     */
    public static String getTargetType(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return null;
        }
        return stack.getTagCompound()
            .getString(TAG_TARGET);
    }

    /**
     * Set target type
     */
    public static void setTargetType(ItemStack stack, String targetType) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setString(TAG_TARGET, targetType);
    }

    /**
     * Get target position
     * Returns [x, y, z, dim] or null if not set
     */
    public static int[] getTargetPosition(ItemStack stack) {
        if (!hasTarget(stack)) {
            return null;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        return new int[] { nbt.getInteger(TAG_TARGET_X), nbt.getInteger(TAG_TARGET_Y), nbt.getInteger(TAG_TARGET_Z),
            nbt.getInteger(TAG_TARGET_DIM) };
    }

    /**
     * Set target position
     */
    public static void setTargetPosition(ItemStack stack, int x, int y, int z, int dimension) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();
        nbt.setInteger(TAG_TARGET_X, x);
        nbt.setInteger(TAG_TARGET_Y, y);
        nbt.setInteger(TAG_TARGET_Z, z);
        nbt.setInteger(TAG_TARGET_DIM, dimension);
    }

    /**
     * Check if has target
     */
    public static boolean hasTarget(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt.hasKey(TAG_TARGET_X) && nbt.hasKey(TAG_TARGET_Y)
            && nbt.hasKey(TAG_TARGET_Z)
            && nbt.hasKey(TAG_TARGET_DIM);
    }

    /**
     * Clear target
     */
    public static void clearTarget(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        nbt.removeTag(TAG_TARGET_X);
        nbt.removeTag(TAG_TARGET_Y);
        nbt.removeTag(TAG_TARGET_Z);
        nbt.removeTag(TAG_TARGET_DIM);
    }

    /**
     * NOTE: SextantFinder System
     * <p>
     * Original version:
     * - SextantFinder searches for structures
     * - Finds nearest structure of target type
     * - Provides distance and direction
     * - Supports multiple structure types
     * <p>
     * In 1.7.10:
     * - TODO: Implement SextantFinder class
     * - TODO: Implement structure registry
     * - TODO: Implement searching algorithm
     * - TODO: Implement GUI for selection
     */
}
