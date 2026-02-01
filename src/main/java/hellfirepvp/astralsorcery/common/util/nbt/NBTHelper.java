/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * NBTHelper - NBT serialization utility methods
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.nbt;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * NBTHelper - NBT Helper (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Persistent data storage for Entities and ItemStacks</li>
 * <li>NBT serialization for Blocks, ItemStacks, Vector3, AABB</li>
 * <li>Default value getters for NBT tags</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>IBlockState → Block + metadata</li>
 * <li>ItemStack.EMPTY → null</li>
 * <li>BlockPos → BlockPos from util.math</li>
 * <li>ForgeRegistries.BLOCKS → Block.blockRegistry</li>
 * <li>Optional<T> → null-checks</li>
 * </ul>
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
            compound = base.getCompoundTag(Constants.MODID);
        } else {
            compound = new NBTTagCompound();
            base.setTag(Constants.MODID, compound);
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
        return base.hasKey(Constants.MODID) && base.getTag(Constants.MODID) instanceof NBTTagCompound;
    }

    public static void removePersistentData(Entity entity) {
        removePersistentData(entity.getEntityData());
    }

    public static void removePersistentData(ItemStack item) {
        if (item.hasTagCompound()) removePersistentData(item.getTagCompound());
    }

    public static void removePersistentData(NBTTagCompound base) {
        base.removeTag(Constants.MODID);
    }

    public static NBTTagCompound getData(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }
        return compound;
    }

    /**
     * Set block and metadata to NBT
     * 1.7.10: Store block and metadata separately
     */
    public static void setBlockState(NBTTagCompound cmp, String key, Block block, int metadata) {
        if (block != null && block != Blocks.air) {
            NBTTagCompound serialized = getBlockStateNBTTag(block, metadata);
            if (serialized != null) {
                cmp.setTag(key, serialized);
            }
        }
    }

    /**
     * Get block from NBT
     * 1.7.10: Returns array of [Block, metadata], or null if not found
     */
    @Nullable
    public static Object[] getBlockState(NBTTagCompound cmp, String key) {
        return getBlockStateFromTag(cmp.getCompoundTag(key));
    }

    /**
     * Serialize block and metadata to NBT
     * 1.7.10: Simplified version without IBlockState properties
     */
    @Nonnull
    public static NBTTagCompound getBlockStateNBTTag(Block block, int metadata) {
        if (block == null) {
            block = Blocks.air;
        }
        NBTTagCompound tag = new NBTTagCompound();
        String registryName = Block.blockRegistry.getNameForObject(block);
        if (registryName != null) {
            tag.setString("registryName", registryName);
        }
        tag.setInteger("metadata", metadata);
        return tag;
    }

    /**
     * Deserialize block from NBT
     * 1.7.10: Returns array of [Block, metadata], or null if not found
     */
    @Nullable
    public static Object[] getBlockStateFromTag(NBTTagCompound cmp) {
        return getBlockStateFromTag(cmp, null);
    }

    /**
     * Deserialize block from NBT with default
     * 1.7.10: Returns array of [Block, metadata], or default if not found
     */
    @Nullable
    public static Object[] getBlockStateFromTag(NBTTagCompound cmp, Object[] _default) {
        if (!cmp.hasKey("registryName")) {
            return _default;
        }
        String registryName = cmp.getString("registryName");
        Block block = (Block) Block.blockRegistry.getObject(registryName);
        if (block == null || block == Blocks.air) {
            return _default;
        }
        int metadata = cmp.hasKey("metadata") ? cmp.getInteger("metadata") : 0;
        return new Object[] { block, metadata };
    }

    public static void setAsSubTag(NBTTagCompound compound, String tag, Consumer<NBTTagCompound> applyFct) {
        NBTTagCompound newTag = new NBTTagCompound();
        applyFct.accept(newTag);
        compound.setTag(tag, newTag);
    }

    public static void setStack(NBTTagCompound compound, String tag, ItemStack stack) {
        setAsSubTag(compound, tag, stack::writeToNBT);
    }

    public static void removeUUID(NBTTagCompound compound, String key) {
        compound.removeTag(key + "Most");
        compound.removeTag(key + "Least");
    }

    public static ItemStack getStack(NBTTagCompound compound, String tag) {
        return getStack(compound, tag, null);
    }

    /**
     * Get ItemStack from NBT
     * 1.7.10: null instead of ItemStack.EMPTY
     */
    public static ItemStack getStack(NBTTagCompound compound, String tag, ItemStack defaultValue) {
        if (compound.hasKey(tag)) {
            return ItemStack.loadItemStackFromNBT(compound.getCompoundTag(tag));
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

    /**
     * Write BlockPos to NBT
     * 1.7.10: Uses BlockPos from util.math
     */
    public static void writeBlockPosToNBT(BlockPos pos, NBTTagCompound compound) {
        compound.setInteger("bposX", pos.getX());
        compound.setInteger("bposY", pos.getY());
        compound.setInteger("bposZ", pos.getZ());
    }

    /**
     * Read BlockPos from NBT
     * 1.7.10: Uses BlockPos from util.math
     */
    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound) {
        int x = compound.getInteger("bposX");
        int y = compound.getInteger("bposY");
        int z = compound.getInteger("bposZ");
        return new BlockPos(x, y, z);
    }

    /**
     * Set BlockPos to NBT tag with specified key
     * Convenience wrapper for writeBlockPosToNBT
     */
    public static void setBlockPos(NBTTagCompound compound, String key, BlockPos pos) {
        NBTTagCompound posTag = new NBTTagCompound();
        writeBlockPosToNBT(pos, posTag);
        compound.setTag(key, posTag);
    }

    /**
     * Read BlockPos from NBT tag with specified key
     * Convenience wrapper for readBlockPosFromNBT
     */
    public static BlockPos readBlockPos(NBTTagCompound compound, String key) {
        if (compound.hasKey(key)) {
            return readBlockPosFromNBT(compound.getCompoundTag(key));
        }
        return null;
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
