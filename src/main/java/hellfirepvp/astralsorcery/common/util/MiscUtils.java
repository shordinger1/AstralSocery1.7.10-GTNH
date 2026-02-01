/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * MiscUtils - General utility methods
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * MiscUtils - General utilities (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>TileEntity retrieval utilities</li>
 * <li>Random selection from collections</li>
 * <li>Collection utilities</li>
 * <li>Color conversions</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>BlockPos → ChunkCoordinates or int x, y, z</li>
 * <li>IBlockState → Block + metadata</li>
 * <li>Vec3d → Vec3</li>
 * <li>RayTraceResult → MovingObjectPosition</li>
 * <li>EnumHand → Removed (1.7.10 doesn't have offhand)</li>
 * </ul>
 */
public class MiscUtils {

    /**
     * Get TileEntity at position if it matches the specified class
     * 1.7.10: Use x, y, z coordinates
     */
    @Nullable
    public static <T> T getTileAt(IBlockAccess world, int x, int y, int z, Class<T> tileClass, boolean forceChunkLoad) {
        if (world == null) return null;
        if (world instanceof World) {
            if (!((World) world).blockExists(x, y, z) && !forceChunkLoad) return null;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null) return null;
        if (tileClass.isInstance(te)) return (T) te;
        return null;
    }

    /**
     * Get random entry from list
     */
    @Nullable
    public static <T> T getRandomEntry(List<T> list, Random rand) {
        if (list == null || list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
    }

    /**
     * Get weighted random entry from collection
     */
    @Nullable
    public static <T> T getWeightedRandomEntry(Collection<T> list, Random rand,
        java.util.function.Function<T, Integer> getWeightFunction) {
        List<WRItemObject<T>> weightedItems = new ArrayList<>(list.size());
        for (T e : list) {
            weightedItems.add(new WRItemObject<>(getWeightFunction.apply(e), e));
        }
        WRItemObject<T> item = (WRItemObject<T>) net.minecraft.util.WeightedRandom.getRandomItem(rand, weightedItems);
        return item != null ? item.getValue() : null;
    }

    /**
     * Get max value from collection
     */
    public static <T, V extends Comparable<V>> V getMaxEntry(Collection<T> elements,
        java.util.function.Function<T, V> valueFunction) {
        V max = null;
        for (T element : elements) {
            V val = valueFunction.apply(element);
            if (max == null || max.compareTo(val) < 0) {
                max = val;
            }
        }
        return max;
    }

    /**
     * Merge lists without duplicates
     */
    public static <T> void mergeList(Collection<T> src, List<T> dst) {
        for (T element : src) {
            if (!dst.contains(element)) {
                dst.add(element);
            }
        }
    }

    /**
     * Remove elements from list
     */
    public static <T> void cutList(List<T> toRemove, List<T> from) {
        for (T element : toRemove) {
            if (from.contains(element)) {
                from.remove(element);
            }
        }
    }

    /**
     * Iterative search in collection
     */
    @Nullable
    public static <T> T iterativeSearch(Collection<T> collection, Predicate<T> matchingFct) {
        for (T element : collection) {
            if (matchingFct.test(element)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Check if collection contains element matching predicate
     */
    public static <T> boolean contains(Collection<T> collection, Predicate<T> matchingFct) {
        return iterativeSearch(collection, matchingFct) != null;
    }

    /**
     * Get color from dye color
     * 1.7.10: EnumDyeColor doesn't exist, use ItemDye dye color methods instead
     * This method is commented out as it requires a different implementation for 1.7.10
     */
    /*
     * @Nonnull
     * public static Color flareColorFromDye(EnumDyeColor color) {
     * // Simplified version - just return the dye's item color
     * float[] rgb = color.getColorComponentValues();
     * return new Color(rgb[0], rgb[1], rgb[2]);
     * }
     */

    /**
     * Capitalize first letter of string
     */
    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return String.valueOf(Character.toTitleCase(str.charAt(0))) + str.substring(1);
    }

    /**
     * Apply random offset to vector
     */
    public static void applyRandomOffset(Vector3 target, Random rand) {
        applyRandomOffset(target, rand, 1F);
    }

    /**
     * Apply random offset to vector with multiplier
     */
    public static void applyRandomOffset(Vector3 target, Random rand, float multiplier) {
        target.addX(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addY(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addZ(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
    }

    /**
     * Get circle positions
     */
    public static List<Vector3> getCirclePositions(Vector3 centerOffset, Vector3 axis, double radius,
        int amountOfPointsOnCircle) {
        List<Vector3> out = new LinkedList<>();
        Vector3 circleVec = axis.clone()
            .perpendicular()
            .normalize()
            .multiply(radius);
        double degPerPoint = 360D / ((double) amountOfPointsOnCircle);
        for (int i = 0; i < amountOfPointsOnCircle; i++) {
            double deg = i * degPerPoint;
            out.add(
                circleVec.clone()
                    .rotate(Math.toRadians(deg), axis.clone())
                    .add(centerOffset));
        }
        return out;
    }

    /**
     * Raytrace look direction
     * 1.7.10: Returns MovingObjectPosition instead of RayTraceResult
     */
    @Nullable
    public static MovingObjectPosition rayTraceLook(EntityLivingBase entity, double reachDst) {
        Vec3 pos = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3 lookVec = entity.getLookVec();
        Vec3 end = pos.addVector(lookVec.xCoord * reachDst, lookVec.yCoord * reachDst, lookVec.zCoord * reachDst);
        return entity.worldObj.rayTraceBlocks(pos, end);
    }

    /**
     * Calculate random constellation color
     */
    public static Color calcRandomConstellationColor(float perc) {
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, 0.8F, 0.8F - (0.3F * perc)));
    }

    /**
     * Check if block is fluid
     * 1.7.10: Check block and material directly
     */
    public static boolean isFluidBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Material mat = block.getMaterial();
        return mat == Material.water || mat == Material.lava || mat.isLiquid();
    }

    /**
     * Check if chunk is loaded
     * 1.7.10: Use blockExists() or chunk coordinate checks
     */
    public static boolean isChunkLoaded(World world, int x, int z) {
        return world.blockExists(x, 0, z);
    }

    /**
     * Check if position is loaded
     */
    public static boolean isChunkLoaded(World world, BlockPos pos) {
        return world.blockExists(pos.getX(), 0, pos.getZ());
    }

    /**
     * Get main hand item
     * 1.7.10: No offhand, just returns current equipped item
     */
    @Nullable
    public static ItemStack getMainHandItem(EntityLivingBase entity, Item search) {
        // 1.7.10: EntityLivingBase doesn't have getCurrentEquippedItem()
        // Use inventory-based approach for EntityPlayer only
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack held = player.getCurrentEquippedItem();
            if (held != null && held.getItem() == search) {
                return held;
            }
        }
        return null;
    }

    /**
     * Check if player can attack entity
     * 1.7.10: No spectator mode
     */
    public static boolean canPlayerAttackServer(@Nullable EntityLivingBase source, @Nonnull EntityLivingBase target) {
        if (target.isDead) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer plTarget = (EntityPlayer) target;
            if (plTarget.capabilities.isCreativeMode) {
                return false;
            }
            if (source != null && source instanceof EntityPlayer
                && !((EntityPlayer) source).canAttackPlayer(plTarget)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Copy ItemStack with specific size
     * 1.7.10: Use standard ItemStack copy mechanism
     */
    @Nullable
    public static ItemStack copyStackWithSize(@Nullable ItemStack stack, int size) {
        if (stack == null) {
            return null;
        }
        ItemStack copy = stack.copy();
        copy.stackSize = size;
        return copy;
    }
}
