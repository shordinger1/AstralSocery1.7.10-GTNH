/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityGrapplingHook
 * Created by HellFirePvP
 * Date: 23.06.2017 / 13:12
 */
public class EntityGrapplingHook extends EntityThrowable implements IEntityAdditionalSpawnData, EntityTechnicalAmbient {

    private static final int PULLING_ENTITY_DATAWATCHER_ID = 20;
    private static final int PULLING_DATAWATCHER_ID = 21;

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
        float f = -WrapMathHelper.sin(throwerIn.rotationYaw * 0.017453292F)
            * WrapMathHelper.cos(throwerIn.rotationPitch * 0.017453292F);
        float f1 = -WrapMathHelper.sin((throwerIn.rotationPitch) * 0.017453292F);
        float f2 = WrapMathHelper.cos(throwerIn.rotationYaw * 0.017453292F)
            * WrapMathHelper.cos(throwerIn.rotationPitch * 0.017453292F);
        this.shoot((double) f, (double) f1, (double) f2, 1.7F, 0F);
        this.throwingEntity = throwerIn;
        setSize(0.1F, 0.1F);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        int id = additionalData.readInt();
        try {
            if (id > 0) {
                this.throwingEntity = (EntityLivingBase) worldObj.getEntityByID(id);
            }
        } catch (Exception exc) {}
    }

    @Override
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
        this.dataWatcher.updateObject(PULLING_DATAWATCHER_ID, Byte.valueOf((byte) (pull ? 1 : 0)));
        this.dataWatcher
            .updateObject(PULLING_ENTITY_DATAWATCHER_ID, Integer.valueOf(hit == null ? -1 : hit.getEntityId()));
    }

    public boolean isPulling() {
        return this.dataWatcher.getWatchableObjectByte(PULLING_DATAWATCHER_ID) == 1;
    }

    @Nullable
    public EntityLivingBase getPulling() {
        int idPull = this.dataWatcher.getWatchableObjectInt(PULLING_ENTITY_DATAWATCHER_ID);
        if (idPull > 0) {
            try {
                return (EntityLivingBase) worldObj.getEntityByID(idPull);
            } catch (Exception exc) {}
        }
        return null;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataWatcher.addObject(PULLING_DATAWATCHER_ID, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(PULLING_ENTITY_DATAWATCHER_ID, Integer.valueOf(-1));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // 1.7.10: getThrower() already returns throwingEntity, no need to call super
        EntityLivingBase throwing = getThrower();
        if (throwing == null || throwing.isDead) {
            setDespawning();
        }
        if (!isPulling() && ticksExisted > 20) {
            setDespawning();
        }

        if (!isDespawning()) {
            double dist = Math.max(0.01, throwing.getDistance(this.posX, this.posY, this.posZ));
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
                    getThrower().fallDistance = -5F;
                    double mx = this.posX - getThrower().posX;
                    double my = this.posY - getThrower().posY;
                    double mz = this.posZ - getThrower().posZ;
                    mx /= dist * 5.0D;
                    my /= dist * 5.0D;
                    mz /= dist * 5.0D;
                    // 1.7.10: Use Vector3 instead of Vec3 (protected constructor)
                    Vector3 v2 = new Vector3(mx, my, mz);
                    if (v2.length() > 0.25D) {
                        v2 = v2.normalize();
                        mx = v2.getX() / 4.0D;
                        my = v2.getY() / 4.0D;
                        mz = v2.getZ() / 4.0D;
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

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1; // After tr
    }

    @SideOnly(Side.CLIENT)
    private void spawnSparkles() {
        if (despawning == 3 && !isPulling()) {
            Vector3 ePos = RenderingUtils.interpolatePosition(this, 1F);
            List<Vector3> positions = buildPoints(1F);
            for (Vector3 v : positions) {
                if (rand.nextInt(3) == 0) {
                    v.add(ePos);
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                    p.scale(rand.nextFloat() * 0.2F + 0.2F)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    if (rand.nextBoolean()) {
                        p.setColor(Color.WHITE);
                    }
                    Vector3 m = new Vector3();
                    MiscUtils.applyRandomOffset(m, rand, 0.005F);
                    p.motion(m.getX(), m.getY(), m.getZ())
                        .setMaxAge(15 + rand.nextInt(10));
                }
            }
        }
    }

    // 0 = none, 1=basically gone
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
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.boundingBox.getAverageEdgeLength() * 64D;
        if (Double.isNaN(d0)) {
            d0 = 64D;
        }
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    public List<Vector3> buildPoints(float partial) {
        if (getThrower() == null) {
            return Collections.emptyList();
        }
        List<Vector3> list = Lists.newLinkedList();
        Vector3 interpThrower = RenderingUtils.interpolatePosition(getThrower(), partial);
        interpThrower.add(getThrower().width / 2, 0, getThrower().width / 2);
        Vector3 interpHook = RenderingUtils.interpolatePosition(this, partial);
        interpHook.add(width / 2, 0, width / 2);
        Vector3 origin = new Vector3();
        Vector3 to = interpThrower.clone()
            .subtract(interpHook)
            .addY(getThrower().height / 4);
        float lineLength = (float) (to.length() * 5);
        list.add(origin.clone());
        int iter = (int) lineLength;
        for (int xx = 1; xx < iter - 1; xx++) {
            float dist = xx * (lineLength / iter);
            double dx = (interpThrower.getX() - interpHook.getX()) / iter * xx
                + WrapMathHelper.sin(dist / 10.0F) * pullFactor;
            double dy = (interpThrower.getY() - interpHook.getY() + getThrower().height / 2F) / iter * xx
                + WrapMathHelper.sin(dist / 7.0F) * pullFactor;
            double dz = (interpThrower.getZ() - interpHook.getZ()) / iter * xx
                + WrapMathHelper.sin(dist / 2.0F) * pullFactor;
            list.add(new Vector3(dx, dy, dz));
        }
        list.add(to.clone());

        return list;
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        // 1.7.10: Call setThrowableHeading instead of shoot
        this.setThrowableHeading(x, y, z, velocity, 0.0F);
    }

    // 1.7.10: Return stored throwing entity instead of relying on superclass
    public EntityLivingBase getThrower() {
        return this.throwingEntity;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB; // Advantage: we can render the grapplinghook line in entity render
                                                // instead of particle hackery
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
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
                // 1.7.10: Use Vec3.createVectorHelper() instead of new Vec3()
                hit = Vec3.createVectorHelper(hit.xCoord, hit.yCoord + result.entityHit.height * 3 / 4, hit.zCoord);
                break;
            default:
                break;
        }
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        // 1.7.10: Vec3 uses xCoord, yCoord, zCoord instead of x, y, z
        this.posX = hit.xCoord;
        this.posY = hit.yCoord;
        this.posZ = hit.zCoord;
    }

}
