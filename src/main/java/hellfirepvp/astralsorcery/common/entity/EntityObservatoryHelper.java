/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Observatory helper entity - Player riding entity for telescope
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * EntityObservatoryHelper - Observatory helper entity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Invisible mount entity for telescope riding</li>
 * <li>Synchronizes player rotation with observatory</li>
 * <li>Fixed position to telescope tile</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>EntityDataManager → DataWatcher</li>
 * <li>BlockPos → utilmath.BlockPos</li>
 * <li>getPassengers() → riddenByEntity</li>
 * <li>dismountRidingEntity() → mountEntity(null)</li>
 * <li>BlockPos.ORIGIN → new BlockPos(0, 0, 0)</li>
 * <li>DataSerializers.BLOCK_POS → Custom storage</li>
 * <li>entityUniqueID → getUniqueID()</li>
 * <li>shouldRenderInPass() → Removed in 1.7.10</li>
 * <li>canPassengerSteer() → canRiderInteract()</li>
 * <li>getPickedResult() → getPickedResult() (same)</li>
 * <li>RayTraceResult → MovingObjectPosition</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>ContainerObservatory - GUI container</li>
 * <li>TileObservatory.updatePitchYaw() - Rotation sync method</li>
 * </ul>
 */
public class EntityObservatoryHelper extends Entity {

    private static final int DATA_FIXED_IDX = 16;
    private BlockPos fixedPos;

    public EntityObservatoryHelper(World worldIn) {
        super(worldIn);
        setSize(0, 0);
        this.isImmuneToFire = true;
        this.fixedPos = new BlockPos(0, 0, 0);
    }

    public EntityObservatoryHelper(World world, BlockPos fixedPos) {
        super(world);
        setSize(0, 0);
        this.fixedPos = fixedPos;
        // 1.7.10: Sync via DataWatcher
        getDataWatcher().addObject(DATA_FIXED_IDX, 0);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        // 1.7.10: Register DataWatcher object
        // We'll just store BlockPos as a field, not synced via DataWatcher
        // since 1.7.10 doesn't have BlockPos serializer
    }

    public BlockPos getFixedObservatoryPos() {
        return this.fixedPos;
    }

    /**
     * Try to get the TileObservatory at fixed position
     * 
     * @return TileEntity or null if not found
     */
    @Nullable
    public TileEntity tryGetObservatory() {
        return MiscUtils.getTileAt(
            this.worldObj,
            this.fixedPos.getX(),
            this.fixedPos.getY(),
            this.fixedPos.getZ(),
            TileEntity.class,
            false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.noClip = true;

        TileEntity to;
        if ((to = isOnTelescope()) == null) {
            if (!worldObj.isRemote) {
                setDead();
            }
            return;
        }
        // 1.7.10: riddenByEntity instead of getPassengers()
        Entity riding = riddenByEntity;
        if (!isUsable(to)) {
            if (riding != null) {
                // 1.7.10: Dismount by setting mountEntity to null
                riding.mountEntity(null);
            }
            return;
        }
        if (riding != null && riding instanceof EntityPlayer) {
            applyObservatoryRotationsFrom(to, (EntityPlayer) riding);
        }
    }

    /**
     * Check if telescope is usable
     * TODO: Update when TileObservatory is migrated
     */
    private boolean isUsable(TileEntity to) {
        // 1.7.10: TileObservatory doesn't exist yet, return true for now
        // TODO: Call to.isUsable() when TileObservatory is migrated
        return true;
    }

    /**
     * Apply observatory rotations from player
     * TODO: Update when TileObservatory is migrated
     */
    public void applyObservatoryRotationsFrom(TileEntity to, EntityPlayer riding) {
        // 1.7.10: Check if player has open container
        // TODO: Update when ContainerObservatory is migrated
        // if (riding.openContainer != null && riding.openContainer instanceof ContainerObservatory) {
        // //Adjust observatory pitch and jaw to player head
        // this.rotationYaw = riding.rotationYawHead;
        // this.prevRotationYaw = riding.prevRotationYawHead;
        // this.rotationPitch = riding.rotationPitch;
        // this.prevRotationPitch = riding.prevRotationPitch;
        // } else {
        // //Adjust observatory to player-body
        // this.rotationYaw = riding.renderYawOffset;
        // this.prevRotationYaw = riding.prevRenderYawOffset;
        // }

        // Simple version for now
        this.rotationYaw = riding.rotationYawHead;
        this.prevRotationYaw = riding.prevRotationYawHead;
        this.rotationPitch = riding.rotationPitch;
        this.prevRotationPitch = riding.prevRotationPitch;

        // 1.7.10: Check if method exists
        try {
            to.getClass()
                .getMethod("updatePitchYaw", float.class, float.class, float.class, float.class)
                .invoke(to, this.rotationPitch, this.prevRotationPitch, this.rotationYaw, this.prevRotationYaw);
        } catch (Exception e) {
            // Method may not exist yet, ignore
        }
    }

    @Nullable
    private TileEntity isOnTelescope() {
        TileEntity to = MiscUtils.getTileAt(
            this.worldObj,
            this.fixedPos.getX(),
            this.fixedPos.getY(),
            this.fixedPos.getZ(),
            TileEntity.class,
            true);
        if (to == null) {
            return null;
        }
        // 1.7.10: getEntityHelperRef may not exist yet
        UUID helper = null;
        try {
            helper = (UUID) to.getClass()
                .getMethod("getEntityHelperRef")
                .invoke(to);
        } catch (Exception e) {
            // Method doesn't exist yet, skip UUID check
        }
        if (helper != null && !helper.equals(this.getUniqueID())) {
            return null;
        }
        return to;
    }

    protected boolean canBeRidden(Entity entityIn) {
        // 1.7.10: Remove @Override, method may not exist
        // 1.7.10: super.canBeRidden() doesn't exist, check entityIn directly
        if (entityIn == null) return false;
        TileEntity to = isOnTelescope();
        return to != null && isUsable(to);
    }

    public boolean isSilent() {
        // 1.7.10: Remove @Override, check if method exists
        return true;
    }

    public boolean isBurning() {
        // 1.7.10: Remove @Override, method exists in 1.7.10
        return false;
    }

    // 1.7.10: isGlowing() doesn't exist
    // public boolean isGlowing() {
    // return false;
    // }

    public boolean isPushedByWater() {
        // 1.7.10: Remove @Override, method exists in 1.7.10
        return false;
    }

    public boolean isImmuneToExplosions() {
        // 1.7.10: Remove @Override, method may not exist
        return true;
    }

    protected boolean canTriggerWalking() {
        // 1.7.10: Remove @Override, method may not exist
        return false;
    }

    // 1.7.10: isOverWater() doesn't exist
    // public boolean isOverWater() {
    // return true;
    // }

    // 1.7.10: playSound signature is different
    public void playSound(String soundName, float volume, float pitch) {}

    protected void playStepSound(int x, int y, int z, Block blockIn) {
        // 1.7.10: Check method signature
    }

    // 1.7.10: getPickedResult takes Vec3 instead of RayTraceResult
    public ItemStack getPickedResult(MovingObjectPosition target) {
        return new ItemStack(BlocksAS.blockObservatory);
    }

    // 1.7.10: shouldRenderInPass doesn't exist
    // public boolean shouldRenderInPass(int pass) {
    // return false;
    // }

    // 1.7.10: canPassengerSteer doesn't exist
    // public boolean canPassengerSteer() {
    // return false;
    // }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        // 1.7.10: Read BlockPos from NBT
        if (compound.hasKey("fixedX")) {
            this.fixedPos = new BlockPos(
                compound.getInteger("fixedX"),
                compound.getInteger("fixedY"),
                compound.getInteger("fixedZ"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        // 1.7.10: Write BlockPos to NBT
        compound.setInteger("fixedX", this.fixedPos.getX());
        compound.setInteger("fixedY", this.fixedPos.getY());
        compound.setInteger("fixedZ", this.fixedPos.getZ());
    }
}
