/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.common.collect.Iterables;

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

    private static final int FIXED_DATAWATCHER_ID = 20;

    public EntityObservatoryHelper(World worldIn) {
        super(worldIn);
        setSize(0, 0);
        this.isImmuneToFire = true;
    }

    public EntityObservatoryHelper(World world, BlockPos fixedPos) {
        super(world);
        setSize(0, 0);
        this.dataWatcher.updateObject(FIXED_DATAWATCHER_ID, fixedPos);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(FIXED_DATAWATCHER_ID, BlockPos.ORIGIN);
    }

    public BlockPos getFixedObservatoryPos() {
        return (BlockPos) this.dataWatcher.getWatchableObjectObject(FIXED_DATAWATCHER_ID);
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
        List<Entity> passengers = getPassengers();
        if (!to.isUsable()) {
            // 1.7.10: forEach doesn't exist, use for loop
            for (Entity passenger : passengers) {
                passenger.mountEntity(null); // 1.7.10: mountEntity instead of dismountRidingEntity
            }
            return;
        }
        Entity riding = Iterables.getFirst(passengers, null);
        if (riding != null && riding instanceof EntityPlayer) {
            applyObservatoryRotationsFrom(to, (EntityPlayer) riding);
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

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        if (!super.canBeRidden(entityIn)) return false;
        TileObservatory to = isOnTelescope();
        return to != null && to.isUsable();
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean isOverWater() {
        return true;
    }

    @Override
    public void playSound(String soundIn, float volume, float pitch) {}

    @Override
    // 1.7.10: Use BlockPos instead of BlockPos parameter
    protected void playStepSound(int x, int y, int z, Block blockIn) {}

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(BlocksAS.blockObservatory);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}
}
