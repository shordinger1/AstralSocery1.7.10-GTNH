/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Illumination Wand - Place colored light blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.block.BlockFlareLight;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Illumination Wand
 * <p>
 * A wand that places colored light blocks.
 * <p>
 * Features:
 * - Place FlareLight blocks in 16 colors
 * - Sneak + right-click to cycle colors
 * - Right-click to place/remove light blocks
 * - Costs temporary charge (alignment)
 * <p>
 * Usage:
 * - Not sneaking: Place/remove light block
 * - Sneaking: Cycle through colors
 * <p>
 * TODO:
 * - Implement alignment charge system
 * - Implement TileIlluminator interaction
 * - Implement particle effects
 * - Implement sound effects
 */
public class ItemIlluminationWand extends AstralBaseItem {

    private static final String TAG_COLOR = "color";

    public ItemIlluminationWand() {
        super();
        setMaxStackSize(1); // Only one wand per stack
        setMaxDamage(0); // No durability - infinite use
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    // ========== Color Management ==========

    /**
     * Set the configured color for this wand
     *
     * @param stack   The wand item stack
     * @param colorId The color ID (0-15)
     */
    public static void setConfiguredColor(ItemStack stack, int colorId) {
        if (colorId < 0 || colorId > 15) {
            colorId = 0; // Default to black
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        nbt.setInteger(TAG_COLOR, colorId);
    }

    /**
     * Get the configured color for this wand
     *
     * @param stack The wand item stack
     * @return The color ID (0-15), or 11 (yellow) if not set
     */
    public static int getConfiguredColor(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_COLOR)) {
            int color = nbt.getInteger(TAG_COLOR);
            if (color >= 0 && color <= 15) {
                return color;
            }
        }
        return 11; // Default to yellow
    }

    /**
     * Cycle to the next color
     *
     * @param stack The wand item stack
     */
    public static void cycleColor(ItemStack stack) {
        int currentColor = getConfiguredColor(stack);
        int nextColor = (currentColor + 1) % 16;
        setConfiguredColor(stack, nextColor);
    }

    /**
     * Get the color name for display
     *
     * @param colorId The color ID (0-15)
     * @return The formatted color name
     */
    public static String getColorName(int colorId) {
        if (colorId < 0 || colorId >= BlockFlareLight.DYE_COLORS.length) {
            colorId = 11; // Yellow
        }
        String colorName = BlockFlareLight.DYE_COLORS[colorId];
        // Capitalize first letter
        return colorName.substring(0, 1)
            .toUpperCase() + colorName.substring(1);
    }

    /**
     * Get the chat formatting for a color
     *
     * @param colorId The color ID (0-15)
     * @return The chat formatting
     */
    public static EnumChatFormatting getColorFormatting(int colorId) {
        switch (colorId) {
            case 0:
                return EnumChatFormatting.BLACK;
            case 1:
                return EnumChatFormatting.DARK_RED;
            case 2:
                return EnumChatFormatting.DARK_GREEN;
            case 3:
                return EnumChatFormatting.GOLD;
            case 4:
                return EnumChatFormatting.DARK_BLUE;
            case 5:
                return EnumChatFormatting.DARK_PURPLE;
            case 6:
                return EnumChatFormatting.DARK_AQUA;
            case 7:
                return EnumChatFormatting.DARK_GRAY;
            case 8:
                return EnumChatFormatting.GRAY;
            case 9:
                return EnumChatFormatting.RED;
            case 10:
                return EnumChatFormatting.GREEN;
            case 11:
                return EnumChatFormatting.YELLOW;
            case 12:
                return EnumChatFormatting.BLUE;
            case 13:
                return EnumChatFormatting.LIGHT_PURPLE;
            case 14:
                return EnumChatFormatting.DARK_GREEN; // Orange
            case 15:
                return EnumChatFormatting.WHITE;
            default:
                return EnumChatFormatting.YELLOW;
        }
    }

    // ========== Item Behavior ==========

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Check if client side
        if (world.isRemote) {
            return true;
        }

        // Sneaking: cycle color
        if (player.isSneaking()) {
            cycleColor(stack);
            int newColor = getConfiguredColor(stack);
            String colorName = getColorName(newColor);
            EnumChatFormatting formatting = getColorFormatting(newColor);
            player.addChatMessage(new net.minecraft.util.ChatComponentText(formatting + "Color: " + colorName));
            world.playSoundAtEntity(player, "random.click", 0.3F, 1.0F);
            return true;
        }

        // Not sneaking: place or remove light block
        Block blockAt = world.getBlock(x, y, z);

        // If it's a FlareLight, remove it
        if (blockAt == BlocksAS.blockFlareLight) {
            world.setBlockToAir(x, y, z);
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "step.stone", 0.3F, 0.8F);
            return true;
        }

        // Otherwise, place new light block
        // Adjust position based on side
        int newX = x;
        int newY = y;
        int newZ = z;
        switch (side) {
            case 0:
                newY--;
                break; // Bottom
            case 1:
                newY++;
                break; // Top
            case 2:
                newZ--;
                break; // North
            case 3:
                newZ++;
                break; // South
            case 4:
                newX--;
                break; // West
            case 5:
                newX++;
                break; // East
        }

        // Check if we can place here
        // In 1.7.10: canPlaceEntityOnSide needs Block, x, y, z, flag, side, entity, itemstack
        if (!world.canPlaceEntityOnSide(BlocksAS.blockFlareLight, newX, newY, newZ, false, side, player, stack)) {
            return false;
        }

        // Place the light block with configured color
        int color = getConfiguredColor(stack);
        world.setBlock(newX, newY, newZ, BlocksAS.blockFlareLight, color, 3);
        world.playSoundEffect(newX + 0.5, newY + 0.5, newZ + 0.5, "step.glass", 0.3F, 1.0F);

        // TODO: Drain alignment charge
        // TODO: Check if player has enough charge

        return true;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Cycle color when right-clicking in air while sneaking
        if (world.isRemote) {
            return stack;
        }

        if (player.isSneaking()) {
            cycleColor(stack);
            int newColor = getConfiguredColor(stack);
            String colorName = getColorName(newColor);
            EnumChatFormatting formatting = getColorFormatting(newColor);
            player.addChatMessage(new net.minecraft.util.ChatComponentText(formatting + "Color: " + colorName));
            world.playSoundAtEntity(player, "random.click", 0.3F, 1.0F);
        }

        return stack;
    }

    // ========== Display ==========

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        int color = getConfiguredColor(stack);
        String colorName = getColorName(color);
        EnumChatFormatting formatting = getColorFormatting(color);

        tooltip.add(formatting + "Current Color: " + colorName);
        tooltip.add(EnumChatFormatting.GRAY + "Sneak + Click to change color");
        tooltip.add(EnumChatFormatting.GRAY + "Click to place/remove light");
    }

    /**
     * NOTE: Alignment Charge System
     * <p>
     * Original version:
     * - Uses Config.illuminationWandUseCost charge
     * - Requires PlayerChargeHandler
     * - Checks charge before placing
     * <p>
     * In 1.7.10:
     * - TODO: Implement alignment charge system
     * - TODO: Implement PlayerChargeHandler
     * - TODO: Check charge before placement
     * - TODO: Drain charge on use
     * <p>
     * NOTE: TileIlluminator Interaction
     * <p>
     * Original version:
     * - Can interact with TileIlluminator
     * - Wand applies color to illuminator
     * - Illuminator provides light in that color
     * <p>
     * In 1.7.10:
     * - TODO: Implement TileIlluminator
     * - TODO: Implement wand-illuminator interaction
     * - TODO: Apply color to illuminator
     */
}
