/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base.render;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import hellfirepvp.astralsorcery.common.migration.RayTraceResult;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * Helper class for ItemHandRender utility methods.
 * 1.7.10: Moved from interface default method (requires Java 8) to static utility method (Java 6/7 compatible)
 */
public class ItemHandRenderHelper {

    /**
     * Gets the block the entity is looking at
     *
     * @param e                             The entity
     * @param stopTraceOnLiquids            Whether to stop on liquids
     * @param ignoreBlockWithoutBoundingBox Whether to ignore blocks without bounding boxes
     * @param range                         The maximum range
     * @return The ray trace result, or null if no block is hit
     */
    @Nullable
    public static RayTraceResult getLookBlock(Entity e, boolean stopTraceOnLiquids,
        boolean ignoreBlockWithoutBoundingBox, double range) {
        float pitch = e.rotationPitch;
        float yaw = e.rotationYaw;
        // In 1.7.10, Vec3 constructor is protected, use createVectorHelper
        Vec3 entityVec = Vec3.createVectorHelper(e.posX, e.posY + e.getEyeHeight(), e.posZ);
        float f2 = WrapMathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = WrapMathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = -WrapMathHelper.cos(-pitch * 0.017453292F);
        float f5 = WrapMathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec3d1 = entityVec.addVector((double) f6 * range, (double) f5 * range, (double) f7 * range);
        // In 1.7.10, rayTraceBlocks has signature (Vec3, Vec3, boolean)
        // ignoreBlockWithoutBoundingBox parameter doesn't exist
        MovingObjectPosition mop = e.worldObj
            .rayTraceBlocks(entityVec, vec3d1, stopTraceOnLiquids);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return null;
        }
        return new RayTraceResult(mop);
    }

}
