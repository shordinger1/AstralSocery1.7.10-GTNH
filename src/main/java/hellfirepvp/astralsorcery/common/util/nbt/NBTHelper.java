/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.nbt;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTHelper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:15
 */
public class NBTHelper {

    @Nonnull
    public static NBTTagCompound getPersistentData(Entity entity) {
        return getPersistentData(entity.getEntityData());
    }

    @Nonnull
    public static NBTTagCompound getPersistentData(ItemStack item) {
        return getPersistentData(getData(item));
    }

    @Nonnull
    public static NBTTagCompound getPersistentData(NBTTagCompound base) {
        NBTTagCompound compound;
        if (hasPersistentData(base)) {
            compound = base.getCompoundTag(AstralSorcery.MODID);
        } else {
            compound = new NBTTagCompound();
            base.setTag(AstralSorcery.MODID, compound);
        }
        return compound;
    }

    public static boolean hasPersistentData(Entity entity) {
        return hasPersistentData(entity.getEntityData());
    }

    public static boolean hasPersistentData(ItemStack item) {
        return item.getTagCompound() != null && hasPersistentData(item.getTagCompound());
    }

    public static boolean hasPersistentData(NBTTagCompound base) {
        return base.hasKey(AstralSorcery.MODID) && base.getTag(AstralSorcery.MODID) instanceof NBTTagCompound;
    }

    public static void removePersistentData(Entity entity) {
        removePersistentData(entity.getEntityData());
    }

    public static void removePersistentData(ItemStack item) {
        if (item.hasTagCompound()) removePersistentData(item.getTagCompound());
    }

    public static void removePersistentData(NBTTagCompound base) {
        base.removeTag(AstralSorcery.MODID);
    }

    public static NBTTagCompound getData(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }
        return compound;
    }

    public static void setBlockState(NBTTagCompound cmp, String key, Block state) {
        NBTTagCompound serialized = getBlockStateNBTTag(state);
        if (serialized != null) {
            cmp.setTag(key, serialized);
        }
    }

    @Nullable
    public static Block getBlockState(NBTTagCompound cmp, String key) {
        return getBlockStateFromTag(cmp.getCompoundTag(key));
    }

    @Nonnull
    public static NBTTagCompound getBlockStateNBTTag(Block state) {
        if (state.getRegistryName() == null) {
            state = Blocks.air;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(
            "registryName",
            state.getRegistryName()
                .toString());
        NBTTagList properties = new NBTTagList();
        for (IProperty property : state.getPropertyKeys()) {
            NBTTagCompound propTag = new NBTTagCompound();
            try {
                propTag.setString("value", property.getName(state.getValue(property)));
            } catch (Exception exc) {
                continue;
            }
            propTag.setString("property", property.getName());
            properties.appendTag(propTag);
        }
        tag.setTag("properties", properties);
        return tag;
    }

    @Nullable
    public static Block getBlockStateFromTag(NBTTagCompound cmp) {
        return getBlockStateFromTag(cmp, null);
    }

    @Nullable
    public static <T extends Comparable<T>> Block getBlockStateFromTag(NBTTagCompound cmp, Block _default) {
        ResourceLocation key = new ResourceLocation(cmp.getString("registryName"));
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        if (block == null || block == Blocks.air) return _default;
        Block state = block;
        Collection<IProperty<?>> properties = state.getPropertyKeys();
        NBTTagList list = cmp.getTagList("properties", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound propertyTag = list.getCompoundTagAt(i);
            String valueStr = propertyTag.getString("value");
            String propertyStr = propertyTag.getString("property");
            IProperty<T> match = (IProperty<T>) MiscUtils.iterativeSearch(
                properties,
                prop -> prop.getName()
                    .equalsIgnoreCase(propertyStr));
            if (match != null) {
                try {
                    T val = match.parseValue(valueStr);
                    if (val != null) {
                        state = state.withProperty(match, val);
                    }
                } catch (Throwable tr) {} // Thanks Exu2
            }
        }
        return state;
    }

    public static void setAsSubTag(NBTTagCompound compound, String tag, Consumer<NBTTagCompound> applyFct) {
        NBTTagCompound newTag = new NBTTagCompound();
        applyFct.accept(newTag);
        compound.setTag(tag, newTag);
    }

    public static void setStack(NBTTagCompound compound, String tag, ItemStack stack) {
        setAsSubTag(compound, tag, (tag1, stack1) -> stack1.writeToNBT(tag1));
    }

    public static void removeUUID(NBTTagCompound compound, String key) {
        compound.removeTag(key + "Most");
        compound.removeTag(key + "Least");
    }

    public static UUID getUniqueId(NBTTagCompound compound, String key) {
        long most = compound.getLong(key + "Most");
        long least = compound.getLong(key + "Least");
        return new UUID(most, least);
    }

    public static void setUniqueId(NBTTagCompound compound, String key, UUID value) {
        compound.setLong(key + "Most", value.getMostSignificantBits());
        compound.setLong(key + "Least", value.getLeastSignificantBits());
    }

    public static boolean hasUniqueId(NBTTagCompound compound, String key) {
        return compound.hasKey(key + "Most") && compound.hasKey(key + "Least");
    }

    public static ItemStack getStack(NBTTagCompound compound, String tag) {
        return getStack(compound, tag, null);
    }

    // Get tags with default value
    public static ItemStack getStack(NBTTagCompound compound, String tag, ItemStack defaultValue) {
        if (compound.hasKey(tag)) {
            return new ItemStack(compound.getCompoundTag(tag));
        }
        return defaultValue;
    }

    public static boolean getBoolean(NBTTagCompound compound, String tag, boolean defaultValue) {
        return compound.hasKey(tag) ? compound.getBoolean(tag) : defaultValue;
    }

    public static String getString(NBTTagCompound compound, String tag, String defaultValue) {
        return compound.hasKey(tag) ? compound.getString(tag) : defaultValue;
    }

    public static int getInteger(NBTTagCompound compound, String tag, int defaultValue) {
        return compound.hasKey(tag) ? compound.getInteger(tag) : defaultValue;
    }

    public static double getDouble(NBTTagCompound compound, String tag, double defaultValue) {
        return compound.hasKey(tag) ? compound.getDouble(tag) : defaultValue;
    }

    public static float getFloat(NBTTagCompound compound, String tag, float defaultValue) {
        return compound.hasKey(tag) ? compound.getFloat(tag) : defaultValue;
    }

    public static byte getByte(NBTTagCompound compound, String tag, byte defaultValue) {
        return compound.hasKey(tag) ? compound.getByte(tag) : defaultValue;
    }

    public static short getShort(NBTTagCompound compound, String tag, short defaultValue) {
        return compound.hasKey(tag) ? compound.getShort(tag) : defaultValue;
    }

    public static long getLong(NBTTagCompound compound, String tag, long defaultValue) {
        return compound.hasKey(tag) ? compound.getLong(tag) : defaultValue;
    }

    public static void writeBlockPosToNBT(BlockPos pos, NBTTagCompound compound) {
        compound.setInteger("bposX", pos.getX());
        compound.setInteger("bposY", pos.getY());
        compound.setInteger("bposZ", pos.getZ());
    }

    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound) {
        int x = compound.getInteger("bposX");
        int y = compound.getInteger("bposY");
        int z = compound.getInteger("bposZ");
        return new BlockPos(x, y, z);
    }

    public static NBTTagCompound writeVector3(Vector3 v) {
        NBTTagCompound cmp = new NBTTagCompound();
        writeVector3(v, cmp);
        return cmp;
    }

    public static void writeVector3(Vector3 v, NBTTagCompound compound) {
        compound.setDouble("vecPosX", v.getX());
        compound.setDouble("vecPosY", v.getY());
        compound.setDouble("vecPosZ", v.getZ());
    }

    public static Vector3 readVector3(NBTTagCompound compound) {
        return new Vector3(compound.getDouble("vecPosX"), compound.getDouble("vecPosY"), compound.getDouble("vecPosZ"));
    }

    public static void writeBoundingBox(AxisAlignedBB box, NBTTagCompound tag) {
        tag.setDouble("boxMinX", box.minX);
        tag.setDouble("boxMinY", box.minY);
        tag.setDouble("boxMinZ", box.minZ);
        tag.setDouble("boxMaxX", box.maxX);
        tag.setDouble("boxMaxY", box.maxY);
        tag.setDouble("boxMaxZ", box.maxZ);
    }

    public static AxisAlignedBB readBoundingBox(NBTTagCompound tag) {
        return AxisAlignedBB.getBoundingBox(
            tag.getDouble("boxMinX"),
            tag.getDouble("boxMinY"),
            tag.getDouble("boxMinZ"),
            tag.getDouble("boxMaxX"),
            tag.getDouble("boxMaxY"),
            tag.getDouble("boxMaxZ"));
    }
}
