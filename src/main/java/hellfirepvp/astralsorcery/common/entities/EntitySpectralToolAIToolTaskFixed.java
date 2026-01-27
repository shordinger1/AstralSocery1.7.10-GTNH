/*******************************************************************************
 * Fixed AIToolTask class for EntitySpectralTool (1.7.10 compatibility)
 * This replaces the inner class in EntitySpectralTool.java
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

// 1.7.10: Class name matches filename with underscore prefix
public class EntitySpectralToolAIToolTaskFixed extends EntityAIBase {

    private final EntitySpectralTool parentEntity;
    private EntitySpectralTool.ToolTask taskTarget = null;

    private BlockPos designatedBreakTarget = null;
    private EntityLivingBase designatedAttackTarget = null;

    private int actionTicks = 0;

    public EntitySpectralToolAIToolTaskFixed(EntitySpectralTool entity) {
        this.parentEntity = entity;
        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        if (this.taskTarget == null) {
            return false;
        }

        // 1.7.10: Simplified check - just find a target
        return findTarget() != null;
    }

    private Object findTarget() {
        switch (this.taskTarget.getType()) {
            case BREAK_BLOCK:
                return MiscUtils.searchAreaForFirst(
                    parentEntity.worldObj,
                    new BlockPos(parentEntity),
                    8,
                    null, // offsetFrom not critical
                    EntitySpectralToolAIHelpers.PICKAXE_CHECK);
            case BREAK_LOG:
                return MiscUtils.searchAreaForFirst(
                    parentEntity.worldObj,
                    new BlockPos(parentEntity),
                    10,
                    null,
                    EntitySpectralToolAIHelpers.AXE_CHECK);
            case ATTACK_MONSTER:
                return findHostileEntity();
            default:
                return null;
        }
    }

    private EntityLivingBase findHostileEntity() {
        // 1.7.10: getEntitiesWithinAABB without predicate - filter manually
        BlockPos center = new BlockPos(parentEntity);
        AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(
            center.getX() - 8,
            center.getY() - 8,
            center.getZ() - 8,
            center.getX() + 8,
            center.getY() + 8,
            center.getZ() + 8);

        List<EntityLivingBase> allEntities = parentEntity.worldObj
            .getEntitiesWithinAABB(EntityLivingBase.class, searchBox);

        // Filter for hostile entities (not players, not spectral tools, not dead)
        List<EntityLivingBase> hostile = new ArrayList<>();
        for (EntityLivingBase e : allEntities) {
            if (e instanceof EntityPlayer || e instanceof EntitySpectralTool) continue;
            // 1.7.10: isDead is a field
            if (e.isDead) continue;
            // In 1.7.10 we can't easily check creature type, just accept all non-player entities
            hostile.add(e);
        }

//        if (hostile == null || hostile.stackSize <= 0) return null;

        // Find closest
        return EntityUtils.selectClosest(hostile, e -> e.getDistanceSqToEntity(parentEntity));
    }

    // 1.7.10: shouldContinueExecuting doesn't override in EntityAIBase
    public boolean shouldContinueExecuting() {
        return taskTarget != null && (designatedAttackTarget != null || designatedBreakTarget != null);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.designatedBreakTarget = null;
        this.designatedAttackTarget = null;
        this.actionTicks = 0;
    }

    @Override
    public void updateTask() {
        super.updateTask();

        if (!shouldContinueExecuting() || this.taskTarget == null) {
            return;
        }

        if (actionTicks < 0) {
            actionTicks = 0;
        }

        boolean resetTimer = false;
        switch (this.taskTarget.getType()) {
            case BREAK_BLOCK:
                if (this.parentEntity.worldObj.isAirBlock(
                    this.designatedBreakTarget.getX(),
                    this.designatedBreakTarget.getY(),
                    this.designatedBreakTarget.getZ())) {
                    this.designatedBreakTarget = null;
                    resetTimer = true;
                } else {
                    // 1.7.10: Calculate distance to target manually
                    double dx = this.designatedBreakTarget.getX() - this.parentEntity.posX;
                    double dy = this.designatedBreakTarget.getY() - this.parentEntity.posY;
                    double dz = this.designatedBreakTarget.getZ() - this.parentEntity.posZ;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    this.parentEntity.getMoveHelper()
                        .setMoveTo(
                            this.designatedBreakTarget.getX(),
                            this.designatedBreakTarget.getY(),
                            this.designatedBreakTarget.getZ(),
                            1.5);

                    if (distSq < 9D) { // 3 blocks squared
                        this.actionTicks++;
                        if (this.actionTicks > CapeEffectPelotrio.getTicksBreakBlockPick()
                            && this.parentEntity.worldObj instanceof WorldServer) {
                            // 1.7.10: getBlock takes x, y, z
                            if (MiscUtils.breakBlockWithoutPlayer(
                                (WorldServer) this.parentEntity.worldObj,
                                this.designatedBreakTarget,
                                this.parentEntity.worldObj.getBlock(
                                    this.designatedBreakTarget.getX(),
                                    this.designatedBreakTarget.getY(),
                                    this.designatedBreakTarget.getZ()),
                                true,
                                true,
                                true)) {
                                resetTimer = true;
                            }
                        }
                    }
                }
                break;
            case BREAK_LOG:
                if (this.parentEntity.worldObj.isAirBlock(
                    this.designatedBreakTarget.getX(),
                    this.designatedBreakTarget.getY(),
                    this.designatedBreakTarget.getZ())) {
                    this.designatedBreakTarget = null;
                    resetTimer = true;
                } else {
                    double dx = this.designatedBreakTarget.getX() - this.parentEntity.posX;
                    double dy = this.designatedBreakTarget.getY() - this.parentEntity.posY;
                    double dz = this.designatedBreakTarget.getZ() - this.parentEntity.posZ;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    this.parentEntity.getMoveHelper()
                        .setMoveTo(
                            this.designatedBreakTarget.getX(),
                            this.designatedBreakTarget.getY(),
                            this.designatedBreakTarget.getZ(),
                            1.5);

                    if (distSq < 9D) {
                        this.actionTicks++;
                        if (this.actionTicks > CapeEffectPelotrio.getTicksBreakBlockAxe()
                            && this.parentEntity.worldObj instanceof WorldServer) {
                            if (MiscUtils.breakBlockWithoutPlayer(
                                (WorldServer) this.parentEntity.worldObj,
                                this.designatedBreakTarget,
                                this.parentEntity.worldObj.getBlock(
                                    this.designatedBreakTarget.getX(),
                                    this.designatedBreakTarget.getY(),
                                    this.designatedBreakTarget.getZ()),
                                true,
                                true,
                                true)) {
                                resetTimer = true;
                            }
                        }
                    }
                }
                break;
            case ATTACK_MONSTER:
                if (this.designatedAttackTarget.isDead) {
                    this.designatedAttackTarget = null;
                    resetTimer = true;
                } else {
                    // Recheck for closer target
                    EntityLivingBase closer = findHostileEntity();
                    if (closer != null) {
                        this.parentEntity.getMoveHelper()
                            .setMoveTo(closer.posX, closer.posY, closer.posZ, 1.6D);
                    }

                    double dx = this.designatedAttackTarget.posX - this.parentEntity.posX;
                    double dy = this.designatedAttackTarget.posY - this.parentEntity.posY;
                    double dz = this.designatedAttackTarget.posZ - this.parentEntity.posZ;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    this.parentEntity.getMoveHelper()
                        .setMoveTo(
                            this.designatedAttackTarget.posX,
                            this.designatedAttackTarget.posY,
                            this.designatedAttackTarget.posZ,
                            1.7);

                    if (distSq < 9D) {
                        this.actionTicks++;
                        if (this.actionTicks > CapeEffectPelotrio.getTicksSwordAttacks()) {
                            DamageUtil.attackEntityFrom(
                                this.designatedAttackTarget,
                                CommonProxy.dmgSourceStellar,
                                CapeEffectPelotrio.getSwordAttackDamage());
                            resetTimer = true;
                        }
                    }
                }
                break;
        }

        if (resetTimer) {
            this.actionTicks = 0;
        }
    }

    @Override
    public void startExecuting() {
        if (this.taskTarget == null) {
            return;
        }

        Object target = findTarget();
        if (target instanceof BlockPos) {
            this.designatedBreakTarget = (BlockPos) target;
            this.parentEntity.getMoveHelper()
                .setMoveTo(
                    this.designatedBreakTarget.getX(),
                    this.designatedBreakTarget.getY(),
                    this.designatedBreakTarget.getZ(),
                    1.5);
        } else if (target instanceof EntityLivingBase) {
            this.designatedAttackTarget = (EntityLivingBase) target;
            this.parentEntity.getMoveHelper()
                .setMoveTo(
                    this.designatedAttackTarget.posX,
                    this.designatedAttackTarget.posY,
                    this.designatedAttackTarget.posZ,
                    1.7);
        }
    }

    public void setTaskTarget(EntitySpectralTool.ToolTask taskTarget) {
        this.taskTarget = taskTarget;
    }
}
