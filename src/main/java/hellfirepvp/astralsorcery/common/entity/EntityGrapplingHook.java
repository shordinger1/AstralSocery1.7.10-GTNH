/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Grappling hook entity - Pulls player towards target
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import io.netty.buffer.ByteBuf;

/**
 * EntityGrapplingHook - Grappling hook entity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Throwable hook that pulls thrower towards target</li>
 * <li>Can attach to blocks or entities</li>
 * <li>Client-side rope rendering</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>EntityDataManager → DataWatcher</li>
 * <li>DataSerializers.VARINT/BOOLEAN → DataWatcher objects</li>
 * <li>RayTraceResult → MovingObjectPosition</li>
 * <li>getThrower() → getThrower()</li>
 * <li>BlockPos → int x, y, z</li>
 * <li>Entity.getUniqueID() → getUniqueID()</li>
 * <li>shouldRenderInPass() → Removed in 1.7.10</li>
 * <li>TileEntity.INFINITE_EXTENT_AABB → Infinite AABB</li>
 * <li>Vector3 class from util.data</li>
 * <li>RenderingUtils.interpolatePosition() - Not available</li>
 * <li>EntityComplexFX.AlphaFunction - Not available</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>RenderingUtils.interpolatePosition - Client interpolation</li>
 * <li>MiscUtils.applyRandomOffset - Random offset utility</li>
 * </ul>
 */
public class EntityGrapplingHook extends EntityThrowable implements EntityTechnicalAmbient {

    private static final int DATA_PULLING_ENTITY_IDX = 18;
    private static final int DATA_PULLING_IDX = 19;

    private boolean boosted = false;

    // Non-moving handling
    private int timeout = 0;
    private int previousDist = 0;

    public int despawning = -1;
    public float pullFactor = 0.0F;

    private EntityLivingBase throwingEntity;

    public EntityGrapplingHook(World worldIn) {
        super(worldIn);
        setSize(0.1F, 0.1F);
    }

    public EntityGrapplingHook(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        // 1.7.10: MathHelper is in util.math
        float f = -net.minecraft.util.MathHelper.sin(throwerIn.rotationYaw * 0.017453292F)
            * net.minecraft.util.MathHelper.cos(throwerIn.rotationPitch * 0.017453292F);
        float f1 = -net.minecraft.util.MathHelper.sin((throwerIn.rotationPitch) * 0.017453292F);
        float f2 = net.minecraft.util.MathHelper.cos(throwerIn.rotationYaw * 0.017453292F)
            * net.minecraft.util.MathHelper.cos(throwerIn.rotationPitch * 0.017453292F);
        // 1.7.10: shoot() method doesn't exist with this signature in EntityThrowable
        // Set motion directly instead
        this.motionX = f * 1.7;
        this.motionY = f1 * 1.7;
        this.motionZ = f2 * 1.7;
        this.throwingEntity = throwerIn;
        setSize(0.1F, 0.1F);
    }

    /**
     * 1.7.10: IEntityAdditionalSpawnData doesn't exist
     * Using NBT for additional data instead
     */
    public void readSpawnData(ByteBuf additionalData) {
        int id = additionalData.readInt();
        try {
            if (id > 0) {
                this.throwingEntity = (EntityLivingBase) worldObj.getEntityByID(id);
            }
        } catch (Exception exc) {}
    }

    public void writeSpawnData(ByteBuf buffer) {
        int id = -1;
        if (this.throwingEntity != null) {
            id = this.throwingEntity.getEntityId();
        }
        buffer.writeInt(id);
    }

    @Override
    protected float getGravityVelocity() {
        return isPulling() ? 0F : 0.03F;
    }

    public void setPulling(boolean pull, @Nullable EntityLivingBase hit) {
        this.getDataWatcher()
            .updateObject(DATA_PULLING_IDX, Boolean.valueOf(pull));
        this.getDataWatcher()
            .updateObject(DATA_PULLING_ENTITY_IDX, Integer.valueOf(hit == null ? -1 : hit.getEntityId()));
    }

    public boolean isPulling() {
        // 1.7.10: DataWatcher doesn't have getWatchableObjectBoolean
        // Use getWatchableObjectInt and check if != 0 for boolean
        return this.getDataWatcher()
            .getWatchableObjectInt(DATA_PULLING_IDX) != 0;
    }

    @Nullable
    public EntityLivingBase getPulling() {
        int idPull = this.getDataWatcher()
            .getWatchableObjectInt(DATA_PULLING_ENTITY_IDX);
        if (idPull > 0) {
            try {
                return (EntityLivingBase) this.worldObj.getEntityByID(idPull);
            } catch (Exception exc) {}
        }
        return null;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        // 1.7.10: Register DataWatcher objects
        this.getDataWatcher()
            .addObject(DATA_PULLING_IDX, Boolean.valueOf(false));
        this.getDataWatcher()
            .addObject(DATA_PULLING_ENTITY_IDX, Integer.valueOf(-1));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (getThrower() == null || getThrower().isDead) {
            setDespawning();
        }
        if (!isPulling() && ticksExisted > 20) {
            setDespawning();
        }

        if (!isDespawning()) {
            EntityLivingBase throwing = getThrower();
            double dist = Math.max(0.01, throwing.getDistanceToEntity(this));
            if (!isDead && isPulling()) {
                if (getPulling() != null) {
                    EntityLivingBase at = getPulling();
                    this.posX = at.posX;
                    this.posY = at.posY;
                    this.posZ = at.posZ;
                }

                if (((getPulling() != null && ticksExisted > 60 && dist < 2)
                    || (getPulling() == null && ticksExisted > 15 && dist < 2)) || timeout > 15) {
                    setDespawning();
                } else {
                    // 1.7.10: fallDistance is a field
                    getThrower().fallDistance = -5F;
                    double mx = this.posX - getThrower().posX;
                    double my = this.posY - getThrower().posY;
                    double mz = this.posZ - getThrower().posZ;
                    mx /= dist * 5.0D;
                    my /= dist * 5.0D;
                    mz /= dist * 5.0D;
                    Vec3 v2 = Vec3.createVectorHelper(mx, my, mz);
                    // 1.7.10: lengthVector() instead of length()
                    if (v2.lengthVector() > 0.25D) {
                        v2 = v2.normalize();
                        mx = v2.xCoord / 4.0D;
                        my = v2.yCoord / 4.0D;
                        mz = v2.zCoord / 4.0D;
                    }
                    getThrower().motionX += mx;
                    getThrower().motionY += my + 0.04D;
                    getThrower().motionZ += mz;

                    if (!boosted) {
                        getThrower().motionY += 0.4F;
                        boosted = true;
                    }

                    int roughDst = (int) (dist / 2.0D);
                    if (roughDst >= this.previousDist) {
                        this.timeout += 1;
                    } else {
                        this.timeout = 0;
                    }
                    this.previousDist = roughDst;
                }
            }
        } else {
            despawnTick();
        }
        if (worldObj.isRemote) {
            if (!isPulling()) {
                this.pullFactor += 0.02F;
            } else {
                this.pullFactor *= 0.66F;
            }
            spawnSparkles();
        }
    }

    // 1.7.10: shouldRenderInPass doesn't exist
    // public boolean shouldRenderInPass(int pass) {
    // return pass == 1;
    // }

    /**
     * Client-side sparkle effects
     * 1.7.10: Simplified version (RenderingUtils.interpolatePosition and EntityComplexFX.AlphaFunction not available)
     * EffectHelper is now implemented for 1.7.10
     */
    @SideOnly(Side.CLIENT)
    private void spawnSparkles() {
        if (despawning == 3 && !isPulling()) {
            // Simplified particle effect - just spawn sparkles around the entity
            for (int i = 0; i < 5; i++) {
                if (rand.nextInt(3) == 0) {
                    double offsetX = (rand.nextFloat() - 0.5) * 0.5;
                    double offsetY = (rand.nextFloat() - 0.5) * 0.5;
                    double offsetZ = (rand.nextFloat() - 0.5) * 0.5;

                    EntityFXFacingParticle p = EffectHelper
                        .genericFlareParticle(posX + offsetX, posY + offsetY, posZ + offsetZ);
                    p.scale(rand.nextFloat() * 0.2F + 0.2F);
                    p.setMaxAge(15 + rand.nextInt(10));

                    if (rand.nextBoolean()) {
                        p.setColor(Color.WHITE);
                    }

                    // Simple random motion
                    double mx = (rand.nextFloat() - 0.5) * 0.01;
                    double my = (rand.nextFloat() - 0.5) * 0.01;
                    double mz = (rand.nextFloat() - 0.5) * 0.01;
                    p.motion(mx, my, mz);
                }
            }
        }
    }

    /**
     * Calculate despawn percentage for rendering
     */
    public float despawnPercentage(float partial) {
        int tick = despawning;
        float p = tick - (1 - partial);
        p /= 10;
        return Math.min(1, Math.max(0, p));
    }

    public boolean isDespawning() {
        return despawning != -1;
    }

    private void setDespawning() {
        if (despawning == -1) {
            despawning = 0;
        }
    }

    private void despawnTick() {
        despawning++;
        if (despawning > 10) {
            setDead();
        }
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.boundingBox.getAverageEdgeLength() * 64D;
        if (Double.isNaN(d0)) {
            d0 = 64D;
        }
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    /**
     * Build rope points for rendering
     * TODO: Re-enable when RenderingUtils.interpolatePosition is available
     */
    public List<Vector3> buildPoints(float partial) {
        if (getThrower() == null) {
            return Collections.emptyList();
        }
        List<Vector3> list = new java.util.LinkedList<>();
        // TODO: Re-enable when RenderingUtils.interpolatePosition is migrated
        /*
         * Vector3 interpThrower = RenderingUtils.interpolatePosition(getThrower(), partial);
         * interpThrower.add(getThrower().width / 2, 0, getThrower().width / 2);
         * Vector3 interpHook = RenderingUtils.interpolatePosition(this, partial);
         * interpHook.add(width / 2, 0, width / 2);
         * Vector3 origin = new Vector3();
         * Vector3 to = interpThrower.clone().subtract(interpHook).addY(getThrower().height / 4);
         * float lineLength = (float) (to.length() * 5);
         * list.add(origin.clone());
         * int iter = (int) lineLength;
         * for (int xx = 1; xx < iter - 1; xx++) {
         * float dist = xx * (lineLength / iter);
         * double dx = (interpThrower.getX() - interpHook.getX()) / iter * xx +
         * MathHelper.sin(dist / 10.0F) * pullFactor;
         * double dy = (interpThrower.getY() - interpHook.getY() + getThrower().height / 2F) / iter * xx +
         * MathHelper.sin(dist / 7.0F) * pullFactor;
         * double dz = (interpThrower.getZ() - interpHook.getZ()) / iter * xx +
         * MathHelper.sin(dist / 2.0F) * pullFactor;
         * list.add(new Vector3(dx, dy, dz));
         * }
         * list.add(to.clone());
         */
        return list;
    }

    // 1.7.10: EntityThrowable.shoot() has different signature
    // Remove @Override and the shoot() call

    @Override
    public EntityLivingBase getThrower() {
        return this.throwingEntity != null ? this.throwingEntity : super.getThrower();
    }

    // 1.7.10: Remove @Override, getRenderBoundingBox may not be an override
    public AxisAlignedBB getRenderBoundingBox() {
        // 1.7.10: Infinite extent AABB for rendering the rope
        return AxisAlignedBB.getBoundingBox(
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY);
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        // 1.7.10: MovingObjectPosition instead of RayTraceResult
        Vec3 hit = result.hitVec;
        switch (result.typeOfHit) {
            case BLOCK:
                setPulling(true, null);
                break;
            case ENTITY:
                Entity e = result.entityHit;
                if (e == null || (getThrower() != null && e.equals(getThrower()))) {
                    return;
                }
                setPulling(
                    true,
                    (result.entityHit instanceof EntityLivingBase) ? (EntityLivingBase) result.entityHit : null);
                hit = Vec3.createVectorHelper(hit.xCoord, hit.yCoord + result.entityHit.height * 3 / 4, hit.zCoord);
                break;
            default:
                break;
        }
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.posX = hit.xCoord;
        this.posY = hit.yCoord;
        this.posZ = hit.zCoord;
    }

}
