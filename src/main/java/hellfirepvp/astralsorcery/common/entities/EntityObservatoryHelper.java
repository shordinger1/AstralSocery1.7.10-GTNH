/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityObservatoryHelper
 * Created by HellFirePvP
 * Date: 26.05.2018 / 14:37
 */
public class EntityObservatoryHelper extends Entity {

    // 1.7.10: DataWatcher doesn't support generic Objects, store coordinates separately
    private static final int DATAWATCHER_X = 20;
    private static final int DATAWATCHER_Y = 21;
    private static final int DATAWATCHER_Z = 22;

    // Fallback storage for when entity isn't initialized yet
    private BlockPos fixedPosStorage;

    public EntityObservatoryHelper(World worldIn) {
        super(worldIn);
        setSize(0, 0);
        this.isImmuneToFire = true;
    }

    public EntityObservatoryHelper(World world, BlockPos fixedPos) {
        super(world);
        setSize(0, 0);
        this.fixedPosStorage = fixedPos;
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(DATAWATCHER_X, Integer.valueOf(0));
        this.dataWatcher.addObject(DATAWATCHER_Y, Integer.valueOf(0));
        this.dataWatcher.addObject(DATAWATCHER_Z, Integer.valueOf(0));
    }

    public BlockPos getFixedObservatoryPos() {
        if (this.fixedPosStorage != null) {
            return this.fixedPosStorage;
        }
        // 1.7.10: Read coordinates separately
        int x = this.dataWatcher.getWatchableObjectInt(DATAWATCHER_X);
        int y = this.dataWatcher.getWatchableObjectInt(DATAWATCHER_Y);
        int z = this.dataWatcher.getWatchableObjectInt(DATAWATCHER_Z);
        return new BlockPos(x, y, z);
    }

    public void setFixedObservatoryPos(BlockPos pos) {
        this.fixedPosStorage = null;
        // 1.7.10: Update coordinates separately
        this.dataWatcher.updateObject(DATAWATCHER_X, Integer.valueOf(pos.getX()));
        this.dataWatcher.updateObject(DATAWATCHER_Y, Integer.valueOf(pos.getY()));
        this.dataWatcher.updateObject(DATAWATCHER_Z, Integer.valueOf(pos.getZ()));
    }

    @Nullable
    public TileObservatory tryGetObservatory() {
        return MiscUtils.getTileAt(this.worldObj, getFixedObservatoryPos(), TileObservatory.class, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.noClip = true;

        TileObservatory to;
        if ((to = isOnTelescope()) == null) {
            if (!worldObj.isRemote) {
                setDead();
            }
            return;
        }
        // 1.7.10: getPassengers() doesn't exist, use riddenByEntity field (single entity)
        Entity passenger = this.riddenByEntity;
        if (!to.isUsable()) {
            if (passenger != null) {
                passenger.mountEntity(null); // 1.7.10: mountEntity instead of dismountRidingEntity
            }
            return;
        }
        if (passenger instanceof EntityPlayer) {
            applyObservatoryRotationsFrom(to, (EntityPlayer) passenger);
        }
    }

    public void applyObservatoryRotationsFrom(TileObservatory to, EntityPlayer riding) {
        if (riding.openContainer != null && riding.openContainer instanceof ContainerObservatory) {
            // Adjust observatory pitch and jaw to player head
            this.rotationYaw = riding.rotationYawHead;
            this.prevRotationYaw = riding.prevRotationYawHead;
            this.rotationPitch = riding.rotationPitch;
            this.prevRotationPitch = riding.prevRotationPitch;
        } else {
            // Adjust observatory to player-body
            this.rotationYaw = riding.renderYawOffset;
            this.prevRotationYaw = riding.prevRenderYawOffset;
        }

        to.updatePitchYaw(this.rotationPitch, this.prevRotationPitch, this.rotationYaw, this.prevRotationYaw);
    }

    @Nullable
    private TileObservatory isOnTelescope() {
        BlockPos fixed = getFixedObservatoryPos();
        TileObservatory to = MiscUtils.getTileAt(this.worldObj, fixed, TileObservatory.class, true);
        if (to == null) {
            return null;
        }
        UUID helper = to.getEntityHelperRef();
        if (helper == null || !helper.equals(this.entityUniqueID)) {
            return null;
        }
        return to;
    }

    // 1.7.10: canBeRidden doesn't exist as an overridable method
    public boolean canBeRidden(Entity entityIn) {
        // In 1.7.10, this logic needs to be handled differently
        TileObservatory to = isOnTelescope();
        return to != null && to.isUsable();
    }

    // 1.7.10: isSilent() doesn't override anything in Entity
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    // 1.7.10: isGlowing() doesn't override anything in Entity
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    // 1.7.10: isImmuneToExplosions() doesn't override anything in Entity
    public boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    // 1.7.10: isOverWater() doesn't override anything in Entity
    public boolean isOverWater() {
        return true;
    }

    @Override
    public void playSound(String soundIn, float volume, float pitch) {}

    // 1.7.10: playStepSound has different signature, not overriding
    protected void playStepSound(int x, int y, int z, Block blockIn) {}

    // 1.7.10: RayTraceResult is MovingObjectPosition
    public ItemStack getPickedResult(MovingObjectPosition target) {
        return new ItemStack(BlocksAS.blockObservatory);
    }

    // 1.7.10: shouldRenderInPass() doesn't override anything in Entity
    public boolean shouldRenderInPass(int pass) {
        return false;
    }

    // 1.7.10: canPassengerSteer() doesn't override anything in Entity
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}
}
