/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageKey
 * Created by HellFirePvP
 * Date: 01.12.2018 / 10:58
 */
public class StorageKey {

    @Nonnull
    private final Item item;
    private final int metadata;

    public StorageKey(@Nonnull ItemStack stack) {
        this.item = stack.getItem();
        if (this.item.isDamageable()) {
            this.metadata = 0;
        } else {
            this.metadata = stack.getItemDamage();
        }
    }

    private StorageKey(@Nonnull Item item, int metadata) {
        this.item = item;
        this.metadata = metadata;
    }

    public static StorageKey from(ItemStack stack) {
        return new StorageKey(stack);
    }

    public static StorageKey from(@Nonnull Item item, int meta) {
        return new StorageKey(item, meta);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageKey that = (StorageKey) o;

        // 1.7.10: Use itemRegistry.getNameForObject() instead of getRegistryName()
        String thisName = Item.itemRegistry.getNameForObject(this.item);
        String thatName = Item.itemRegistry.getNameForObject(that.item);
        return (item.getHasSubtypes() || metadata == that.metadata) && thisName.equals(thatName);
    }

    @Override
    public int hashCode() {
        // 1.7.10: Use itemRegistry.getNameForObject() instead of getRegistryName()
        String itemName = Item.itemRegistry.getNameForObject(this.item);
        int result = itemName.hashCode();
        if (item.getHasSubtypes()) {
            result = 31 * result + metadata;
        }
        return result;
    }

    @Nonnull
    public NBTTagCompound serialize() {
        NBTTagCompound keyTag = new NBTTagCompound();
        // 1.7.10: Use itemRegistry.getNameForObject() instead of getRegistryName()
        String itemName = Item.itemRegistry.getNameForObject(this.item);
        keyTag.setString("name", itemName);
        keyTag.setInteger("meta", metadata);
        return keyTag;
    }

    // If the item in question does no longer exist in the registry, return null.
    @Nullable
    public static StorageKey deserialize(NBTTagCompound nbt) {
        // 1.7.10: Use Item.itemRegistry.getObject() instead of ForgeRegistries.ITEMS.getValue()
        String name = nbt.getString("name");
        Item i = (Item) Item.itemRegistry.getObject(name);
        if (i == null) {
            return null;
        }
        int meta = nbt.getInteger("meta");
        return new StorageKey(i, meta);
    }
}
