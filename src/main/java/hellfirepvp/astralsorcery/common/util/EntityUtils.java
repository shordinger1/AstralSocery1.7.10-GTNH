/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * EntityUtils - Entity utility methods
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.Collection;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * EntityUtils - Entity utilities (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Entity spawning utilities</li>
 * <li>Entity collision and space checks</li>
 * <li>Vortex motion calculations</li>
 * <li>Entity selection predicates</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>BlockPos → int x, y, z</li>
 * <li>EntityList.createEntityByIDFromName() → EntityList.createEntityByName()</li>
 * <li>ItemStack.isEmpty() → stack == null || stack.stackSize <= 0</li>
 * <li>Entity.getEntityBoundingBox() → entity.boundingBox (field access)</li>
 * <li>LootTable system → Removed (not available in 1.7.10)</li>
 * </ul>
 */
public class EntityUtils {

    /**
     * Check if entity can spawn at location
     * 1.7.10: Use int coordinates instead of BlockPos
     */
    public static boolean canEntitySpawnHere(World world, int x, int y, int z, String entityKey,
        boolean respectConditions, @Nullable Function<Entity, Void> preCheckEntity) {
        Entity entity = EntityList.createEntityByName(entityKey, world);
        if (entity == null) {
            return false;
        }
        entity.setLocationAndAngles(x + 0.5, y + 0.5, z + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
        if (preCheckEntity != null) {
            preCheckEntity.apply(entity);
        }
        if (respectConditions) {
            if (entity instanceof EntityLiving) {
                EntityLiving living = (EntityLiving) entity;
                if (!living.getCanSpawnHere()) {
                    return false;
                }
                // 1.7.10: isNotColliding() doesn't exist, check collision manually
            }
        }
        return doesEntityHaveSpace(world, entity);
    }

    /**
     * Check if entity has space to exist
     * 1.7.10: Different API for collision checks
     */
    public static boolean doesEntityHaveSpace(World world, Entity entity) {
        // 1.7.10: Use field access for bounding box
        AxisAlignedBB box = entity.boundingBox;
        return !world.isAnyLiquid(box) && !world.checkBlockCollision(box)
            && world.getEntitiesWithinAABBExcludingEntity(entity, box)
                .isEmpty();
    }

    /**
     * Apply vortex motion towards target
     */
    public static void applyVortexMotion(Function<Void, Vector3> getPositionFunction,
        Function<Vector3, Object> addMotionFunction, Vector3 to, double vortexRange, double multiplier) {
        Vector3 pos = getPositionFunction.apply(null);
        double diffX = (to.getX() - pos.getX()) / vortexRange;
        double diffY = (to.getY() - pos.getY()) / vortexRange;
        double diffZ = (to.getZ() - pos.getZ()) / vortexRange;
        double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (1.0D - dist > 0.0D) {
            double dstFactorSq = (1.0D - dist) * (1.0D - dist);
            Vector3 toAdd = new Vector3();
            toAdd.setX(diffX / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setY(diffY / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setZ(diffZ / dist * dstFactorSq * 0.15D * multiplier);
            addMotionFunction.apply(toAdd);
        }
    }

    /**
     * Select entities by class
     */
    public static java.util.function.Predicate<? super Entity> selectEntities(Class<? extends Entity>... entities) {
        return (java.util.function.Predicate<Entity>) entity -> {
            if (entity == null || entity.isDead) return false;
            Class<? extends Entity> clazz = entity.getClass();
            for (Class<? extends Entity> test : entities) {
                if (test.isAssignableFrom(clazz)) return true;
            }
            return false;
        };
    }

    /**
     * Select entities by item class
     * 1.7.10: Check stackSize for isEmpty
     */
    public static java.util.function.Predicate<? super Entity> selectItemClassInstaceof(Class<?> itemClass) {
        return (java.util.function.Predicate<Entity>) entity -> {
            if (entity == null || entity.isDead) return false;
            if (!(entity instanceof EntityItem)) return false;
            ItemStack i = ((EntityItem) entity).getEntityItem();
            if (i == null || i.stackSize <= 0) return false;
            return itemClass.isAssignableFrom(
                i.getItem()
                    .getClass());
        };
    }

    /**
     * Select entities by item
     * 1.7.10: Check stackSize for isEmpty
     */
    public static java.util.function.Predicate<? super Entity> selectItem(Item item) {
        return (java.util.function.Predicate<Entity>) entity -> {
            if (entity == null || entity.isDead) return false;
            if (!(entity instanceof EntityItem)) return false;
            ItemStack i = ((EntityItem) entity).getEntityItem();
            if (i == null || i.stackSize <= 0) return false;
            return i.getItem()
                .equals(item);
        };
    }

    /**
     * Select entities by item stack acceptor
     * 1.7.10: Check stackSize for isEmpty
     */
    public static java.util.function.Predicate<? super Entity> selectItemStack(Function<ItemStack, Boolean> acceptor) {
        return entity -> {
            if (entity == null || entity.isDead) return false;
            if (!(entity instanceof EntityItem)) return false;
            ItemStack i = ((EntityItem) entity).getEntityItem();
            if (i == null || i.stackSize <= 0) return false;
            return acceptor.apply(i);
        };
    }

    /**
     * Select closest element from collection
     */
    @Nullable
    public static <T> T selectClosest(Collection<T> elements, Function<T, Double> dstFunc) {
        if (elements.isEmpty()) return null;

        double dstClosest = Double.MAX_VALUE;
        T closestElement = null;
        for (T element : elements) {
            double dst = dstFunc.apply(element);
            if (dst < dstClosest) {
                closestElement = element;
                dstClosest = dst;
            }
        }
        return closestElement;
    }

}
