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
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.migration.RayTraceResult;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandRender
 * Created by HellFirePvP
 * Date: 06.02.2017 / 23:21
 */
public interface ItemHandRender {

    @SideOnly(Side.CLIENT)
    public void onRenderWhileInHand(ItemStack stack, float pTicks);

    @Nullable
    default public RayTraceResult getLookBlock(Entity e, boolean stopTraceOnLiquids,
        boolean ignoreBlockWithoutBoundingBox, double range) {
        float pitch = e.rotationPitch;
        float yaw = e.rotationYaw;
        Vec3 entityVec = new Vec3(e.posX, e.posY + e.getEyeHeight(), e.posZ);
        float f2 = WrapMathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = WrapMathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = -WrapMathHelper.cos(-pitch * 0.017453292F);
        float f5 = WrapMathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec3d1 = entityVec.addVector((double) f6 * range, (double) f5 * range, (double) f7 * range);
        MovingObjectPosition mop = e.worldObj
            .rayTraceBlocks(entityVec, vec3d1, stopTraceOnLiquids, ignoreBlockWithoutBoundingBox, false);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return null;
        }
        return new RayTraceResult(mop);
    }

}
