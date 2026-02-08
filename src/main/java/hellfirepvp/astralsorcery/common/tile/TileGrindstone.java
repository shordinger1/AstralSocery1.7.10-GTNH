/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Grindstone TileEntity - Grinds items into dust
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * TileGrindstone - Grindstone machine (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Stores item being ground</li>
 * <li>Plays wheel animation when grinding</li>
 * <li>Integrates with recipe system</li>
 * <li>Supports sword sharpening</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>updateEntity() instead of update()</li>
 * <li>ItemStack.EMPTY → null</li>
 * <li>stackSize instead of getCount()</li>
 * </ul>
 */
public class TileGrindstone extends TileEntitySynchronized {

    public static final int TICKS_WHEEL_ROTATION = 20;

    /** Item currently being ground */
    private ItemStack grindingItem = null;

    /** Animation ticks */
    public int tickWheelAnimation = 0, prevTickWheelAnimation = 0;

    /** Repeat flag for animation */
    private boolean repeat = false;

    @Override
    public void updateEntity() {
        // Client-side animation
        if (worldObj.isRemote) {
            if (tickWheelAnimation > 0) {
                prevTickWheelAnimation = tickWheelAnimation;
                tickWheelAnimation--;
                if (tickWheelAnimation <= 0 && repeat) {
                    tickWheelAnimation = TICKS_WHEEL_ROTATION;
                    prevTickWheelAnimation = TICKS_WHEEL_ROTATION + 1;
                    repeat = false;
                }
            } else {
                prevTickWheelAnimation = 0;
                tickWheelAnimation = 0;
            }
        }
    }

    /**
     * Play wheel effect (animation)
     * TODO: Implement network packet system for client sync
     */
    public void playWheelEffect() {
        // TODO: Re-enable after network packet system is migrated
        // For now, just set animation directly on server
        if (!worldObj.isRemote) {
            this.tickWheelAnimation = TICKS_WHEEL_ROTATION;
            this.prevTickWheelAnimation = TICKS_WHEEL_ROTATION + 1;
            markForUpdate();
        }
    }

    /**
     * Set the item being ground
     *
     * @param stack Item to grind (null to clear)
     */
    public void setGrindingItem(@Nullable ItemStack stack) {
        this.grindingItem = stack;
        markDirty();
        markForUpdate();
    }

    /**
     * Get the item being ground
     *
     * @return Item being ground (null if empty)
     */
    @Nullable
    public ItemStack getGrindingItem() {
        return grindingItem;
    }

    /**
     * Check if there's an item being ground
     *
     * @return true if has item
     */
    public boolean hasItem() {
        return grindingItem != null && grindingItem.stackSize > 0;
    }

    /**
     * Try to grind the current item
     *
     * @return Grinding result, or null if no item
     */
    @Nullable
    public GrindstoneRecipe.GrindResult tryGrind() {
        if (!hasItem()) {
            return null;
        }

        GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(grindingItem);
        if (recipe != null) {
            return recipe.grind(grindingItem);
        }

        // Check for sword sharpening
        if (SwordSharpenHelper.canBeSharpened(grindingItem)) {
            if (worldObj.rand.nextInt(40) == 0) {
                SwordSharpenHelper.setSwordSharpened(grindingItem);
                playWheelEffect();
            }
            return GrindstoneRecipe.GrindResult.success();
        }

        return null;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        NBTTagCompound itemTag = compound.getCompoundTag("item");
        if (itemTag == null || itemTag.hasNoTags()) {
            grindingItem = null;
        } else {
            grindingItem = ItemStack.loadItemStackFromNBT(itemTag);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (grindingItem != null && grindingItem.stackSize > 0) {
            NBTHelper.setAsSubTag(compound, "item", this.grindingItem::writeToNBT);
        } else {
            compound.setTag("item", new NBTTagCompound());
        }
    }

    /**
     * SwordSharpenHelper - Helper for sword sharpening on grindstone
     */
    public static class SwordSharpenHelper {

        /**
         * Check if an item can be sharpened
         *
         * @param stack Item to check
         * @return true if can be sharpened
         */
        public static boolean canBeSharpened(ItemStack stack) {
            if (stack == null) {
                return false;
            }

            // Check if it's a sword
            if (!(stack.getItem() instanceof net.minecraft.item.ItemSword)) {
                return false;
            }

            // Check if already sharpened
            if (isSwordSharpened(stack)) {
                return false;
            }

            return true;
        }

        /**
         * Check if a sword is already sharpened
         *
         * @param stack Sword to check
         * @return true if sharpened
         */
        public static boolean isSwordSharpened(ItemStack stack) {
            if (stack == null || !stack.hasTagCompound()) {
                return false;
            }

            NBTTagCompound compound = stack.getTagCompound();
            return compound.getBoolean("astralsorcery.sharpened");
        }

        /**
         * Mark a sword as sharpened
         *
         * @param stack Sword to sharpen
         */
        public static void setSwordSharpened(ItemStack stack) {
            if (stack == null) {
                return;
            }

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            NBTTagCompound compound = stack.getTagCompound();
            compound.setBoolean("astralsorcery.sharpened", true);
        }
    }

}
