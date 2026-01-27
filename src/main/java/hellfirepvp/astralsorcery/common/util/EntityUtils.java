/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.Collection;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import com.google.common.base.Predicate;

import cpw.mods.fml.common.eventhandler.Event;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityUtils
 * Created by HellFirePvP
 * Date: 14.09.2016 / 20:10
 */
public class EntityUtils {

    // 1.7.10: Removed getLootTableMethod - not applicable

    public static boolean canEntitySpawnHere(World world, BlockPos at, ResourceLocation entityKey,
        boolean respectConditions, @Nullable Function<Entity, Void> preCheckEntity) {
        Entity entity = EntityList.createEntityByName(entityKey.toString(), world);
        if (entity == null) {
            return false;
        }
        entity.setLocationAndAngles(
            at.getX() + 0.5,
            at.getY() + 0.5,
            at.getZ() + 0.5,
            world.rand.nextFloat() * 360.0F,
            0.0F);
        if (preCheckEntity != null) {
            preCheckEntity.apply(entity);
        }
        if (respectConditions) {
            if (entity instanceof EntityLiving) {
                Event.Result canSpawn = ForgeEventFactory
                    .canEntitySpawn((EntityLiving) entity, world, at.getX() + 0.5F, at.getY() + 0.5F, at.getZ() + 0.5F);
                if (canSpawn != Event.Result.ALLOW
                    && (canSpawn != Event.Result.DEFAULT || !((EntityLiving) entity).getCanSpawnHere())) {
                    return false;
                }
            }
        }
        return doesEntityHaveSpace(world, entity);
    }

    public static boolean doesEntityHaveSpace(World world, Entity entity) {
        AxisAlignedBB box = entity.getBoundingBox();
        return !world.isAABBInMaterial(box, Material.water) && !world.isAABBInMaterial(box, Material.lava)
            && world.getCollidingBoundingBoxes(entity, box)
                .isEmpty()
            && world.checkNoEntityCollision(box, entity);
    }

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

    public static Predicate<? super Entity> selectEntities(Class<? extends Entity>... entities) {
        return new Predicate<Entity>() {

            @Override
            public boolean apply(Entity entity) {
                if (entity == null || entity.isDead) return false;
                Class<? extends Entity> clazz = entity.getClass();
                for (Class<? extends Entity> test : entities) {
                    if (test.isAssignableFrom(clazz)) return true;
                }
                return false;
            }
        };
    }

    public static Predicate<? super Entity> selectItemClassInstaceof(Class<?> itemClass) {
        return new Predicate<Entity>() {

            @Override
            public boolean apply(Entity entity) {
                if (entity == null || entity.isDead) return false;
                if (!(entity instanceof EntityItem)) return false;
                ItemStack i = ((EntityItem) entity).getEntityItem();
                if ((i == null || i.stackSize <= 0)) return false;
                return itemClass.isAssignableFrom(
                    i.getItem()
                        .getClass());
            }
        };
    }

    public static Predicate<? super Entity> selectItem(Item item) {
        return new Predicate<Entity>() {

            @Override
            public boolean apply(Entity entity) {
                if (entity == null || entity.isDead) return false;
                if (!(entity instanceof EntityItem)) return false;
                ItemStack i = ((EntityItem) entity).getEntityItem();
                if ((i == null || i.stackSize <= 0)) return false;
                return i.getItem()
                    .equals(item);
            }
        };
    }

    public static Predicate<? super Entity> selectItemStack(final Function<ItemStack, Boolean> acceptor) {
        return new Predicate<Entity>() {

            @Override
            public boolean apply(Entity entity) {
                if (entity == null || entity.isDead) return false;
                if (!(entity instanceof EntityItem)) return false;
                ItemStack i = ((EntityItem) entity).getEntityItem();
                if ((i == null || i.stackSize <= 0)) return false;
                return acceptor.apply(i);
            }
        };
    }

    @Nullable
    public static <T> T selectClosest(Collection<T> elements, Function<T, Double> dstFunc) {
        if (elements == null || elements.isEmpty()) return null;

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

    // 1.7.10: LootTable API doesn't exist - returns Object to avoid compilation errors
    @Nullable
    public static Object getLootTable(EntityLiving entity) {
        // 1.7.10: Loot table system is very different, not accessible via this API
        return null;
    }

    // 1.7.10: Removed getLootTableMethod initialization - not applicable

}
