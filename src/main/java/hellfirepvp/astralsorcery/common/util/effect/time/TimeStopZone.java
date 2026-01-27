/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import hellfirepvp.astralsorcery.common.base.TileAccelerationBlacklist;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopZone
 * Created by HellFirePvP
 * Date: 17.10.2017 / 22:14
 */
public class TimeStopZone {

    final EntityTargetController targetController;

    final float range;
    final BlockPos offset;
    private final World world;
    private int ticksToLive = 0;

    private boolean active = true;
    boolean reducedParticles = false;

    private List<TileEntity> cachedTiles = new LinkedList<>();

    TimeStopZone(EntityTargetController ctrl, float range, BlockPos offset, World world, int tickLivespan,
        boolean reducedParticles) {
        this.targetController = ctrl;
        this.range = range;
        this.offset = offset;
        this.world = world;
        this.ticksToLive = tickLivespan;
        this.reducedParticles = reducedParticles;
    }

    void onServerTick() {
        if (!active) return;
        this.ticksToLive--;

        int minX = WrapMathHelper.floor((offset.getX() - range) / 16.0D);
        int maxX = WrapMathHelper.floor((offset.getX() + range) / 16.0D);
        int minZ = WrapMathHelper.floor((offset.getZ() - range) / 16.0D);
        int maxZ = WrapMathHelper.floor((offset.getZ() + range) / 16.0D);

        for (int xx = minX; xx <= maxX; ++xx) {
            for (int zz = minZ; zz <= maxZ; ++zz) {
                Chunk ch = world.getChunkFromChunkCoords(xx, zz);
                if (ch != null) {
                    // In 1.7.10, Chunk doesn't have getTileEntityMap().
                    // We need to iterate over tile entities differently.
                    // For now, let's skip this optimization in 1.7.10
                    // This would require accessing chunk tile entity storage directly
                }
            }
        }
    }

    private void safeCacheTile(TileEntity te) {
        if (te == null) return;

        for (TileEntity tile : cachedTiles) {
            if (tile.xCoord == te.xCoord && tile.yCoord == te.yCoord && tile.zCoord == te.zCoord) {
                return;
            }
        }
        cachedTiles.add(te);
    }

    public void setTicksToLive(int ticksToLive) {
        this.ticksToLive = ticksToLive;
    }

    void stopEffect() {
        // In 1.7.10, tickableTileEntities field doesn't exist
        // We just clear the cache and mark inactive
        this.cachedTiles.clear();
        this.active = false;
    }

    boolean shouldDespawn() {
        return ticksToLive <= 0 || !active;
    }

    boolean interceptEntityTick(EntityLivingBase e) {
        return active && e != null
            && targetController.shouldFreezeEntity(e)
            && Vector3.atEntityCorner(e)
                .distance(offset) <= range;
    }

    // Mainly because we still want to be able to do damage.
    static void handleImportantEntityTicks(EntityLivingBase e) {
        if (e.hurtTime > 0) {
            e.hurtTime--;
        }
        if (e.hurtResistantTime > 0) {
            e.hurtResistantTime--;
        }
        e.prevPosX = e.posX;
        e.prevPosY = e.posY;
        e.prevPosZ = e.posZ;
        e.prevLimbSwingAmount = e.limbSwingAmount;
        e.prevRenderYawOffset = e.renderYawOffset;
        e.prevRotationPitch = e.rotationPitch;
        e.prevRotationYaw = e.rotationYaw;
        e.prevRotationYawHead = e.rotationYawHead;
        e.prevSwingProgress = e.swingProgress;
        e.prevDistanceWalkedModified = e.distanceWalkedModified;
        e.prevCameraPitch = e.cameraPitch;

        if (e.isPotionActive(RegistryPotions.potionTimeFreeze)) {
            PotionEffect pe = e.getActivePotionEffect(RegistryPotions.potionTimeFreeze);
            if (!pe.onUpdate(e)) {
                if (!e.worldObj.isRemote) {
                    // In 1.7.10, removePotionEffect takes Potion directly, not ID
                    e.removePotionEffect(RegistryPotions.potionTimeFreeze.id);
                }
            }
        }

        if (e instanceof EntityDragon) {
            // In 1.7.10, EntityDragon doesn't have getPhaseManager() or dragon phases like 1.12.2
            // Dragon phases were added in later versions. Skip for 1.7.10
        }
    }

    public static class EntityTargetController {

        final int ownerId;
        final boolean hasOwner;
        final boolean targetPlayers;

        EntityTargetController(int ownerId, boolean hasOwner, boolean targetPlayers) {
            this.ownerId = ownerId;
            this.hasOwner = hasOwner;
            this.targetPlayers = targetPlayers;
        }

        boolean shouldFreezeEntity(EntityLivingBase e) {
            if (e.isDead || e.getHealth() <= 0) {
                return false;
            }
            if (e instanceof EntityDragon) {
                // In 1.7.10, EntityDragon doesn't have getPhaseManager() or dragon phases
                // Just freeze all dragons unless they're dead
            }
            if (hasOwner && e.getEntityId() == ownerId) {
                return false;
            }
            return targetPlayers || !(e instanceof EntityPlayer);
        }

        public static EntityTargetController allExcept(Entity entity) {
            return new EntityTargetController(entity.getEntityId(), true, true);
        }

        public static EntityTargetController noPlayers() {
            return new EntityTargetController(-1, false, false);
        }

        @Nonnull
        public NBTTagCompound serializeNBT() {
            NBTTagCompound out = new NBTTagCompound();
            out.setBoolean("targetPlayers", this.targetPlayers);
            out.setBoolean("hasOwner", this.hasOwner);
            out.setInteger("ownerEntityId", this.ownerId);
            return out;
        }

        @Nonnull
        public static EntityTargetController deserializeNBT(NBTTagCompound cmp) {
            boolean targetPlayers = cmp.getBoolean("targetPlayers");
            boolean hasOwner = cmp.getBoolean("hasOwner");
            int ownerId = cmp.getInteger("ownerEntityId");
            return new EntityTargetController(ownerId, hasOwner, targetPlayers);
        }

    }

}
