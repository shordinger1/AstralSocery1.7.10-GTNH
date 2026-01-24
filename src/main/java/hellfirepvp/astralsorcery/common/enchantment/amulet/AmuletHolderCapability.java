/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletHolderCapability
 * Created by HellFirePvP
 * Date: 19.05.2018 / 22:13
 *
 * 1.7.10 Compatibility: Uses NBT data on ItemStack instead of capabilities.
 */
public class AmuletHolderCapability {

    public static final ResourceLocation CAP_AMULETHOLDER_NAME = new ResourceLocation(
        AstralSorcery.MODID,
        "cap_item_amulet_holder");

    public static final String NBT_KEY = "AS_Amulet_Holder";

    private UUID holderUUID = null;

    public UUID getHolderUUID() {
        return holderUUID;
    }

    public void setHolderUUID(UUID holderUUID) {
        this.holderUUID = holderUUID;
    }

    /**
     * Serialize this capability to NBT.
     */
    public NBTTagCompound serializeNBT() {
        NBTTagCompound cmp = new NBTTagCompound();
        if (holderUUID != null) {
            NBTHelper.setUniqueId(cmp, NBT_KEY, holderUUID);
        }
        return cmp;
    }

    /**
     * Deserialize from NBT.
     */
    public void deserializeNBT(NBTTagCompound nbt) {
        if (NBTHelper.hasUniqueId(nbt, NBT_KEY)) {
            this.holderUUID = NBTHelper.getUniqueId(nbt, NBT_KEY);
        } else {
            this.holderUUID = null;
        }
    }

    /**
     * 1.7.10: Save capability data to ItemStack NBT.
     */
    public static void saveToItemStack(ItemStack stack, AmuletHolderCapability cap) {
        if (stack.stackSize > 0) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            if (cap != null && cap.holderUUID != null) {
                // Write UUID as two longs (1.7.10 compatible)
                nbt.setLong(NBT_KEY + "_Most", cap.holderUUID.getMostSignificantBits());
                nbt.setLong(NBT_KEY + "_Least", cap.holderUUID.getLeastSignificantBits());
            } else {
                // Remove the capability data if cap is null
                nbt.removeTag(NBT_KEY + "_Most");
                nbt.removeTag(NBT_KEY + "_Least");
            }
        }
    }

    /**
     * 1.7.10: Load capability data from ItemStack NBT.
     */
    @Nullable
    public static AmuletHolderCapability loadFromItemStack(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return null;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(NBT_KEY + "_Most")) {
            AmuletHolderCapability cap = new AmuletHolderCapability();
            cap.holderUUID = new UUID(nbt.getLong(NBT_KEY + "_Most"), nbt.getLong(NBT_KEY + "_Least"));
            return cap;
        }
        return null;
    }

    /**
     * 1.7.10: Check if ItemStack has this capability (has NBT data).
     */
    public static boolean hasCapability(ItemStack stack) {
        return loadFromItemStack(stack) != null;
    }

    /**
     * 1.7.10: Get capability from ItemStack.
     */
    @Nullable
    public static AmuletHolderCapability getCapability(ItemStack stack) {
        return loadFromItemStack(stack);
    }

}
