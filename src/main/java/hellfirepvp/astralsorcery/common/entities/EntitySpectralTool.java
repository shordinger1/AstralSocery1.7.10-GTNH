/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.awt.*;

import javax.annotation.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntitySpectralTool
 * Created by HellFirePvP
 * Date: 11.10.2017 / 20:56
 */
public class EntitySpectralTool extends EntityFlying implements EntityTechnicalAmbient {

    private static final int ITEM_DATAWATCHER_ID = 20;

    private AIToolTask aiTask;
    private BlockPos originalStartPosition;
    private int ticksUntilDeath = 0;

    public EntitySpectralTool(World worldIn) {
        super(worldIn);
        setSize(0.6F, 0.8F);
        // 1.7.10: moveHelper is private, EntityFlyHelper doesn't exist
        // Just use default EntityFlying behavior
    }

    public EntitySpectralTool(World world, BlockPos spawnPos, ItemStack tool, ToolTask task) {
        super(world);
        setSize(0.6F, 0.8F);
        setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        setItem(tool);
        this.originalStartPosition = spawnPos;
        this.ticksUntilDeath = 100 + rand.nextInt(40);
        // 1.7.10: moveHelper is private, EntityFlyHelper doesn't exist
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        if (entityIn != null && !(entityIn instanceof EntityPlayer || entityIn instanceof EntitySpectralTool)) {
            super.applyEntityCollision(entityIn);
        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        if (entityIn != null && !(entityIn instanceof EntityPlayer || entityIn instanceof EntitySpectralTool)) {
            super.collideWithEntity(entityIn);
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        // 1.7.10: DataWatcher.addObject with ItemStack
        this.dataWatcher.addObject(ITEM_DATAWATCHER_ID, new ItemStack(Items.diamond_pickaxe));
        // Initialize AI task
        aiTask = new AIToolTask(this);
        this.tasks.addTask(1, aiTask);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        // 1.7.10: Use maxHealth (lowercase) and movementSpeed
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(0.85);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            spawnAmbientEffects();
        } else {
            this.ticksUntilDeath--;
            if (this.ticksUntilDeath <= 0) {
                DamageUtil.attackEntityFrom(this, CommonProxy.dmgSourceStellar, 5000.0F);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnAmbientEffects() {
        if (rand.nextFloat() < 0.2F) {
            Color c = IConstellation.weak;
            double x = posX + rand.nextFloat() * width - (width / 2);
            double y = posY + rand.nextFloat() * (height / 2) + 0.2;
            double z = posZ + rand.nextFloat() * width - (width / 2);

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.5F + 0.3F);
            p.setMaxAge(30 + rand.nextInt(20));

            if (rand.nextFloat() < 0.8F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    private void setItem(ItemStack tool) {
        this.dataWatcher.updateObject(ITEM_DATAWATCHER_ID, tool);
    }

    public ItemStack getItem() {
        return this.dataWatcher.getWatchableObjectItemStack(ITEM_DATAWATCHER_ID);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        setItem(NBTHelper.getStack(compound, "AS_SpectralItem", null));
        int task = compound.getInteger("AS_ToolTask");
        if (this.aiTask != null) {
            this.aiTask.taskTarget = new ToolTask(
                ToolTask.Type.values()[WrapMathHelper.clamp(task, 0, ToolTask.Type.values().length - 1)]);
        } else {
            // Fcking thanks TOP
            this.aiTask = new AIToolTask(this);
            this.aiTask.taskTarget = new ToolTask(ToolTask.Type.BREAK_BLOCK);
        }
        this.ticksUntilDeath = compound.getInteger("AS_ToolDeathTicks");
        this.originalStartPosition = compound.hasKey("AS_StartPosition")
            ? NBTHelper.readBlockPosFromNBT(compound.getCompoundTag("AS_StartPosition"))
            : new BlockPos(this);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        NBTHelper.setStack(compound, "AS_SpectralItem", getItem());
        int task = 0;
        if (this.aiTask != null) {
            task = this.aiTask.taskTarget.type.ordinal();
        }
        compound.setInteger("AS_ToolTask", task);
        compound.setInteger("AS_ToolDeathTicks", this.ticksUntilDeath);
        NBTHelper.setAsSubTag(
            compound,
            "AS_StartPosition",
            subTag -> NBTHelper.writeBlockPosToNBT(this.originalStartPosition, subTag));
    }

    public static class ToolTask {

        private final Type type;

        private ToolTask(Type type) {
            this.type = type;
        }

        // 1.7.10: Add getter for type field to allow external access
        public Type getType() {
            return type;
        }

        public static ToolTask createPickaxeTask() {
            return new ToolTask(Type.BREAK_BLOCK);
        }

        public static ToolTask createLogTask() {
            return new ToolTask(Type.BREAK_LOG);
        }

        public static ToolTask createAttackTask() {
            return new ToolTask(Type.ATTACK_MONSTER);
        }

        // 1.7.10: Make enum public for external access
        public static enum Type {

            BREAK_BLOCK,
            BREAK_LOG,
            ATTACK_MONSTER

        }

    }

    private static class AIToolTask extends EntityAIBase {

        private final EntitySpectralTool parentEntity;
        private ToolTask taskTarget = null;

        private BlockPos designatedBreakTarget = null;
        private EntityLivingBase designatedAttackTarget = null;

        private int actionTicks = 0;

        public AIToolTask(EntitySpectralTool entity) {
            this.parentEntity = entity;
            this.setMutexBits(7);
        }

        @Override
        public boolean shouldExecute() {
            if (this.taskTarget == null) {
                return false;
            }
            return findTarget() != null;
        }

        private Object findTarget() {
            switch (this.taskTarget.type) {
                case BREAK_BLOCK:
                    return MiscUtils.searchAreaForFirst(
                        parentEntity.worldObj,
                        new BlockPos(parentEntity),
                        8,
                        null,
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
            BlockPos center = new BlockPos(parentEntity);
            AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(
                center.getX() - 8,
                center.getY() - 8,
                center.getZ() - 8,
                center.getX() + 8,
                center.getY() + 8,
                center.getZ() + 8);

            java.util.List<EntityLivingBase> allEntities = parentEntity.worldObj
                .getEntitiesWithinAABB(EntityLivingBase.class, searchBox);

            java.util.List<EntityLivingBase> hostile = new java.util.ArrayList<>();
            for (EntityLivingBase e : allEntities) {
                if (e instanceof EntityPlayer || e instanceof EntitySpectralTool) continue;
                if (e.isDead) continue;
                hostile.add(e);
            }

//            if (hostile.stackSize <= 0) return null;
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
            switch (this.taskTarget.type) {
                case BREAK_BLOCK:
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
                            if (this.actionTicks > CapeEffectPelotrio.getTicksBreakBlockPick()
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

    }

}
