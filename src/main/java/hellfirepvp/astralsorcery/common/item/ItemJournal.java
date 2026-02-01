/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Journal Item - Stores constellation papers
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.gui.AltarRecipeViewer;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;

/**
 * Journal Item
 * <p>
 * Stores constellation papers and provides GUI to manage them.
 * <p>
 * Features:
 * - 27 slots for constellation papers
 * - Dual mode GUI (browse/manage)
 * - Sneak + right click to open
 * - NBT-based inventory
 * <p>
 * TODO:
 * - Implement GUI system
 * - Create GuiJournal class
 * - Create ContainerJournal class
 * - Implement inventory serialization
 * - Add sorting system
 * - Link with constellation system
 */
public class ItemJournal extends AstralBaseItem {

    private static final String TAG_INVENTORY = "inventory";
    private static final int INVENTORY_SIZE = 27;

    public ItemJournal() {
        super(1); // Max stack size 1
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isSneaking()) {
            return stack;
        }

        // Phase 4: Open recipe viewer on client side
        if (world.isRemote) {
            FMLCommonHandler.instance()
                .showGuiScreen(new AltarRecipeViewer());
        }

        return stack;
    }

    /**
     * Get stored inventory from NBT
     */
    public NBTTagList getInventory(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_INVENTORY)) {
            return nbt.getTagList(TAG_INVENTORY, 10); // 10 = NBTTagCompound
        }
        return new NBTTagList();
    }

    /**
     * Set stored inventory in NBT
     */
    public void setInventory(ItemStack stack, NBTTagList inventory) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setTag(TAG_INVENTORY, inventory);
    }

    /**
     * Add constellation paper to journal
     */
    public boolean addConstellationPaper(ItemStack journal, ItemStack paper) {
        // TODO: Implement adding paper to inventory
        return true;
    }

    /**
     * Get number of stored papers
     */
    public int getPaperCount(ItemStack journal) {
        NBTTagList inventory = getInventory(journal);
        return inventory.tagCount();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        int count = getPaperCount(stack);
        tooltip.add("§7Stored Papers: §e" + count + "§7/§e" + INVENTORY_SIZE);

        if (count > 0) {
            tooltip.add("§7Sneak + Right-click: §aOpen journal");
        } else {
            tooltip.add("§cEmpty journal");
            tooltip.add("§8Add constellation papers to fill");
        }

        // TODO: When GUI is implemented, show detailed contents
    }
}
