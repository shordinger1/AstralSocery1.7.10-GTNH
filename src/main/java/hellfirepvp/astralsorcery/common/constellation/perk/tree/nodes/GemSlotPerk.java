/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Gem slot perk - Interface for perks with gem slots
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;

/**
 * Gem slot perk - Interface for perks with gem slots (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Item containment system</li>
 * <li>NBT-based data storage</li>
 * <li>Tooltip integration</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Uses NBTHelper for NBT operations</li>
 * <li>No PlayerProgress - uses direct checks</li>
 * <li>TODO: Integrate with ItemPerkGem when implemented</li>
 * </ul>
 */
public interface GemSlotPerk {

    public static final String SOCKET_DATA_KEY = "socketedItem";

    /**
     * Check if has item
     */
    default public boolean hasItem(EntityPlayer player, Side side) {
        return hasItem(player, side, null);
    }

    /**
     * Check if has item with data override
     */
    default public boolean hasItem(EntityPlayer player, Side side, @Nullable NBTTagCompound data) {
        return getContainedItem(player, side, data) != null;
    }

    /**
     * Get contained item
     */
    default public ItemStack getContainedItem(EntityPlayer player, Side side) {
        return getContainedItem(player, side, null);
    }

    /**
     * Get contained item with data override
     */
    default public ItemStack getContainedItem(EntityPlayer player, Side side, @Nullable NBTTagCompound dataOvr) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException(
                "Cannot do perk-specific socketing logic on something that's not a perk!");
        }
        NBTTagCompound data = dataOvr != null ? dataOvr : ((AbstractPerk) this).getPerkData(player, side);
        if (data == null) {
            return null;
        }

        // TODO: Use NBTHelper.readItemStack() when implemented
        // return NBTHelper.readItemStack(data, SOCKET_DATA_KEY);
        if (data.hasKey(SOCKET_DATA_KEY)) {
            NBTTagCompound itemTag = data.getCompoundTag(SOCKET_DATA_KEY);
            return ItemStack.loadItemStackFromNBT(itemTag);
        }
        return null;
    }

    /**
     * Set contained item
     */
    default public boolean setContainedItem(EntityPlayer player, Side side, ItemStack stack) {
        return setContainedItem(player, side, null, stack);
    }

    /**
     * Set contained item with data override
     */
    default public boolean setContainedItem(EntityPlayer player, Side side, @Nullable NBTTagCompound dataOvr,
        ItemStack stack) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException(
                "Cannot do perk-specific socketing logic on something that's not a perk!");
        }

        // TODO: Check PlayerProgress when implemented
        // PlayerProgress prog = ResearchManager.getProgress(player, side);
        // if (!prog.hasPerkUnlocked((AbstractPerk) this)) {
        // return false;
        // }

        boolean updateData = dataOvr == null;
        NBTTagCompound data = dataOvr != null ? dataOvr : ((AbstractPerk) this).getPerkData(player, side);
        if (data == null) {
            return false;
        }
        NBTTagCompound prev = (NBTTagCompound) data.copy();

        if (stack == null) {
            data.removeTag(SOCKET_DATA_KEY);
        } else {
            // TODO: Use NBTHelper.writeItemStack() when implemented
            NBTTagCompound itemTag = new NBTTagCompound();
            stack.writeToNBT(itemTag);
            data.setTag(SOCKET_DATA_KEY, itemTag);
        }

        // TODO: Update ResearchManager when implemented
        // if (updateData) {
        // ResearchManager.setPerkData(player, (AbstractPerk) this, prev, data);
        // }
        return true;
    }

    /**
     * Drop item to player
     */
    default public void dropItemToPlayer(EntityPlayer player) {
        dropItemToPlayer(player, null);
    }

    /**
     * Drop item to player with data override
     */
    default public void dropItemToPlayer(EntityPlayer player, @Nullable NBTTagCompound data) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException(
                "Cannot do perk-specific socketing logic on something that's not a perk!");
        }

        if (player.worldObj.isRemote) {
            return;
        }

        boolean updateData = data == null;
        if (updateData) {
            data = ((AbstractPerk) this).getPerkData(player, Side.SERVER);
        }
        if (data == null) {
            return;
        }
        NBTTagCompound prev = (NBTTagCompound) data.copy();

        ItemStack contained = getContainedItem(player, Side.SERVER, data);
        if (contained != null) {
            if (!player.inventory.addItemStackToInventory(contained)) {
                player.dropPlayerItemWithRandomChoice(contained, false);
            }
        }
        setContainedItem(player, Side.SERVER, data, null);

        // TODO: Update ResearchManager when implemented
        // if (updateData) {
        // ResearchManager.setPerkData(player, (AbstractPerk) this, prev, data);
        // }
    }

    /**
     * Add tooltip info (client-side)
     */
    @SideOnly(Side.CLIENT)
    default public void addTooltipInfo(Collection<String> tooltip) {
        if (!(this instanceof AbstractPerk)) {
            return;
        }

        // Check PlayerProgress for perk effects
        hellfirepvp.astralsorcery.common.data.research.PlayerProgress prog = hellfirepvp.astralsorcery.common.data.research.ResearchManager
            .getProgress(net.minecraft.client.Minecraft.getMinecraft().thePlayer);
        // Note: GemSlotPerk is an interface, check if this is unlocked in player progress
        // The actual perk unlocking is handled by the AbstractPerk implementation

        ItemStack contained = getContainedItem(net.minecraft.client.Minecraft.getMinecraft().thePlayer, Side.CLIENT);
        if (contained == null) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("perk.info.gem.empty"));
        } else {
            tooltip.add(
                EnumChatFormatting.GRAY + StatCollector.translateToLocal("perk.info.gem.content.item")
                    + ": "
                    + contained.getRarity().rarityColor
                    + contained.getDisplayName());
            tooltip.add(EnumChatFormatting.BLUE + "Right-click to remove gem");
        }
    }

}
