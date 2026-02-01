/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Knowledge Share Item - Save and share player progress
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;

/**
 * Knowledge Share Item
 * <p>
 * Two-mode item for saving and loading player progress:
 * - Sneak + right click: Save current progress (RECORD mode)
 * - Right click: Learn saved progress (LEARN mode)
 * <p>
 * Features:
 * - Owner binding (UUID)
 * - Full progress serialization
 * - Progress merging
 * - Network synchronization
 * <p>
 * TODO:
 * - Implement PlayerProgress serialization
 * - Implement progress merging logic
 * - Add owner UUID system
 * - Implement network packets
 * - Add GUI or chat feedback
 */
public class ItemKnowledgeShare extends AstralBaseItem {

    private static final String TAG_OWNER = "owner_uuid";
    private static final String TAG_PROGRESS = "player_progress";
    private static final String TAG_MODE = "mode"; // 0 = RECORD, 1 = LEARN

    public ItemKnowledgeShare() {
        super(1); // Max stack size 1
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        boolean isSneaking = player.isSneaking();
        int mode = getMode(stack);

        if (isSneaking) {
            // Save progress (RECORD mode)
            // TODO: Serialize player progress to NBT
            setOwner(
                stack,
                player.getUniqueID()
                    .toString());
            // saveProgress(stack, player);
        } else {
            // Learn progress (LEARN mode)
            // TODO: Load and merge progress from NBT
            // String owner = getOwner(stack);
            // if (canLearn(player, owner)) {
            // loadProgress(stack, player);
            // }
        }

        return stack;
    }

    /**
     * Get owner UUID from NBT
     */
    public String getOwner(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_OWNER)) {
            return nbt.getString(TAG_OWNER);
        }
        return null;
    }

    /**
     * Set owner UUID in NBT
     */
    public void setOwner(ItemStack stack, String ownerUuid) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setString(TAG_OWNER, ownerUuid);
    }

    /**
     * Get current mode
     */
    public int getMode(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_MODE)) {
            return nbt.getInteger(TAG_MODE);
        }
        return 0; // Default to RECORD mode
    }

    /**
     * Set mode
     */
    public void setMode(ItemStack stack, int mode) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(TAG_MODE, mode);
    }

    /**
     * Get progress data from NBT
     */
    public NBTTagCompound getProgressData(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_PROGRESS)) {
            return nbt.getCompoundTag(TAG_PROGRESS);
        }
        return null;
    }

    /**
     * Set progress data in NBT
     */
    public void setProgressData(ItemStack stack, NBTTagCompound progress) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setTag(TAG_PROGRESS, progress);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        String owner = getOwner(stack);
        if (owner != null) {
            tooltip.add("§7Owner: §e" + owner);
        } else {
            tooltip.add("§7Owner: §cNone");
        }

        int mode = getMode(stack);
        String modeName = (mode == 0) ? "§aRECORD" : "§bLEARN";
        tooltip.add("§7Mode: " + modeName);
        tooltip.add("§7Sneak + Right-click: §eSave progress");
        tooltip.add("§7Right-click: §eLearn progress");
    }
}
