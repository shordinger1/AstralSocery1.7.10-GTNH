/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Flare entity - Floating light source entity
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * EntityFlare - Flare entity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Flying entity that floats around</li>
 * <li>Can follow players</li>
 * <li>Can kill bats (if configured)</li>
 * <li>Ambient spawning at night</li>
 * <li>Client-side sprite rendering (TODO)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Entity.getEntityWorld() → Entity.worldObj</li>
 * <li>world.spawnEntityInWorld() → same method name</li>
 * <li>BlockPos → (x, y, z) coordinates</li>
 * <li>getPosition() → posX, posY, posZ</li>
 * <li>getPosition().add() → Manual coordinate addition</li>
 * <li>getEntityBoundingBox().grow() → Manual bounds calculation</li>
 * <li>world.findNearestEntityWithinAABB() → world.findNearestEntityWithinAABB() (same)</li>
 * <li>PacketChannel.CHANNEL.sendToAllAround() - Network packet system (TODO)</li>
 * <li>SoundEvent → String sound names</li>
 * <li>Vector3 class - May need to be created or use alternative</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>EffectHelper/EntityFXFacingParticle/EntityFXFacingSprite - Client particles</li>
 * <li>SpriteLibrary - Client sprite management</li>
 * <li>PacketChannel/PktParticleEvent - Network packet system</li>
 * <li>DamageUtil - Damage utility functions</li>
 * <li>CommonProxy.dmgSourceStellar - Custom damage source</li>
 * <li>ConstellationSkyHandler - Sky constellation system</li>
 * <li>Vector3 - Vector utility class</li>
 * </ul>
 */
public class EntityFlare extends EntityFlying {

    private static final int strollRange = 31;

    public Object texSprite = null;
    // 1.7.10: Use coordinates instead of BlockPos
    private double[] moveTarget = null;
    private boolean isAmbient = false;
    private int entityAge = 0;

    private int followingEntityId = -1;

    public EntityFlare(World worldIn) {
        super(worldIn);
        setSize(0.7F, 0.7F);
    }

    public EntityFlare(World worldIn, double x, double y, double z) {
        super(worldIn);
        setSize(0.7F, 0.7F);
        // 1.7.10: setPositionAndRotation() takes individual coordinates
        this.setPosition(x, y, z);
        this.rotationYaw = 0;
        this.rotationPitch = 0;
    }

    public EntityFlare setAmbient(boolean ambient) {
        this.isAmbient = ambient;
        return this;
    }

    public EntityFlare setFollowingTarget(EntityPlayer player) {
        this.followingEntityId = player.getEntityId();
        return this;
    }

    /**
     * Spawn ambient flare at location
     * 1.7.10: Simplified version without ConstellationSkyHandler
     * Uses world time to check for night time instead
     */
    public static void spawnAmbient(World world, Vector3 at) {
        if (world.isRemote) return;
        if (!MiscUtils.isChunkLoaded(world, (int) at.getX() >> 4, (int) at.getZ() >> 4)) return;
        if (Config.ambientFlareChance <= 0) return;

        // 1.7.10: Use world time to check for night (simplified from ConstellationSkyHandler)
        long time = world.getWorldTime();
        boolean isNight = (time % 24000L) > 13000L && (time % 24000L) < 23000L;

        if (world.rand.nextInt(Config.ambientFlareChance) == 0
            && world.isAirBlock((int) at.getX(), (int) at.getY(), (int) at.getZ())
            && isNight) {
            world.spawnEntityInWorld(new EntityFlare(world, at.getX(), at.getY(), at.getZ()).setAmbient(true));
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // 1.7.10: Same method signature
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(1D);
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        if (entityIn != null && !(entityIn instanceof EntityPlayer)) {
            super.applyEntityCollision(entityIn);
        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        if (entityIn != null && !(entityIn instanceof EntityPlayer)) {
            super.collideWithEntity(entityIn);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        entityAge++;

        // 1.7.10: worldObj.isRemote instead of world.isRemote
        if (worldObj.isRemote) {
            if (texSprite == null) {
                setupSprite();
            }
            clientUpdate();
        } else {
            if (followingEntityId != -1) {
                if (getFollowingEntity() == null) {
                    damageEntity(DamageSource.magic, 20F);
                }
            } else if (entityAge > 300 && rand.nextInt(700) == 0) {
                damageEntity(DamageSource.magic, 20F);
            }

            if (!isDead) {

                if (Config.flareKillsBats && entityAge % 70 == 0 && rand.nextBoolean()) {
                    // 1.7.10: Calculate bounds manually instead of grow()
                    AxisAlignedBB searchBox = this.boundingBox.expand(10, 10, 10);
                    Entity closest = worldObj.findNearestEntityWithinAABB(EntityBat.class, searchBox, this);
                    if (closest != null && closest instanceof EntityBat
                        && ((EntityBat) closest).getHealth() > 0
                        && !closest.isDead) {
                        // TODO: Re-enable when DamageUtil and network packet system are migrated
                        // DamageUtil.attackEntityFrom(closest, CommonProxy.dmgSourceStellar, 40F);

                        closest.attackEntityFrom(DamageSource.magic, 4F);

                        // TODO: Re-enable when PacketChannel, PktParticleEvent, and Vector3 are migrated
                        /*
                         * PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.FLARE_PROC,
                         * new Vector3(posX, posY + this.height / 2, posZ));
                         * PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(worldObj, posX, posY,
                         * posZ, 16));
                         * AstralSorcery.proxy.fireLightning(worldObj,
                         * Vector3.atEntityCenter(this),
                         * Vector3.atEntityCenter(closest),
                         * new Color(0, 0, 216));
                         */
                    }
                }

                if (isAmbient) {
                    // 1.7.10: Manual coordinate math instead of BlockPos methods
                    if ((moveTarget == null || getDistanceSq(moveTarget[0], moveTarget[1], moveTarget[2]) < 5D)
                        && rand.nextInt(260) == 0) {
                        moveTarget = new double[] { posX + (rand.nextInt(strollRange) - strollRange / 2),
                            posY + (rand.nextInt(strollRange) - strollRange / 2),
                            posZ + (rand.nextInt(strollRange) - strollRange / 2) };
                    }
                } else if (followingEntityId != -1) {
                    Entity following = getFollowingEntity();
                    if (following != null) {
                        // 1.7.10: Use coordinates directly
                        moveTarget = new double[] { following.posX, following.posY + following.height, following.posZ };
                    }
                }

                if (moveTarget != null) {
                    // 1.7.10: Calculate distance manually
                    double dist = getDistanceSq(moveTarget[0], moveTarget[1], moveTarget[2]);
                    if (dist > 4D) {
                        // 1.7.10: Move towards target
                        double dx = moveTarget[0] - posX;
                        double dy = moveTarget[1] - posY;
                        double dz = moveTarget[2] - posZ;
                        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);

                        if (d > 0) {
                            motionX += (dx / d) * 0.05;
                            motionY += (dy / d) * 0.05;
                            motionZ += (dz / d) * 0.05;
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the following entity
     * 1.7.10: worldObj.getEntityByID() instead of world.getEntityByID()
     */
    public Entity getFollowingEntity() {
        if (followingEntityId == -1) return null;
        // 1.7.10: Use worldObj.getEntityByID()
        try {
            return worldObj.getEntityByID(followingEntityId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Setup client sprite
     * TODO: Implement when SpriteLibrary is migrated
     */
    private void setupSprite() {
        // TODO: Re-enable when SpriteLibrary is migrated
        /*
         * texSprite = SpriteLibrary spriteLibrary = SpriteLibrary.getInstance();
         * texSprite = spriteLibrary.getSprite("astralsorcery:sprites/entity/flare.png");
         */
    }

    /**
     * Client-side update
     * EffectHelper is now implemented for 1.7.10
     */
    private void clientUpdate() {
        if (worldObj.isRemote) {
            // EffectHandler update not available
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("isAmbient", isAmbient);
        compound.setInteger("entityAge", entityAge);
        compound.setInteger("followingEntityId", followingEntityId);
        // 1.7.10: Save moveTarget as array
        if (moveTarget != null) {
            compound.setDouble("moveTargetX", moveTarget[0]);
            compound.setDouble("moveTargetY", moveTarget[1]);
            compound.setDouble("moveTargetZ", moveTarget[2]);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        isAmbient = compound.getBoolean("isAmbient");
        entityAge = compound.getInteger("entityAge");
        followingEntityId = compound.getInteger("followingEntityId");
        // 1.7.10: Load moveTarget from coordinates
        if (compound.hasKey("moveTargetX")) {
            moveTarget = new double[] { compound.getDouble("moveTargetX"), compound.getDouble("moveTargetY"),
                compound.getDouble("moveTargetZ") };
        }
    }

    @Override
    protected boolean canDespawn() {
        return !isAmbient;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return false;
    }

    /**
     * Get distance squared to target coordinates
     * 1.7.10: Helper method for distance calculation
     */
    public double getDistanceSq(double x, double y, double z) {
        double dx = posX - x;
        double dy = posY - y;
        double dz = posZ - z;
        return dx * dx + dy * dy + dz * dz;
    }

}
