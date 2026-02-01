/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * EntityStarburst - Starburst entity
 *
 * SKELETON VERSION - EffectHelper logic commented out with TODOs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.Color;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * EntityStarburst - Starburst (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Throwable projectile that targets entities</li>
 * <li>Homing behavior toward nearby living entities</li>
 * <li>Client-side particle effects</li>
 * <li>Triggers celestial strike on impact</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>RayTraceResult → MovingObjectPosition</li>
 * <li>result.typeOfHit → result.typeOfHit</li>
 * <li>result.entityHit → result.entityHit (same)</li>
 * <li>world.getEntitiesWithinAABB() → world.getEntitiesWithinAABB() (same)</li>
 * <li>AxisAlignedBB.offset() → getOffsetBoundingBox()</li>
 * <li>EntitySelectors.IS_ALIVE → Custom predicate</li>
 * <li>Entity.getEntityId() → getEntityId() (same)</li>
 * <li>world.getEntityByID() → worldObj.getEntityByID() (same)</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>CelestialStrike - Celestial strike effect</li>
 * <li>MiscUtils.getCirclePositions() - Circle position helper</li>
 * <li>MiscUtils.canPlayerAttackServer() - Attack check utility</li>
 * </ul>
 */
public class EntityStarburst extends EntityThrowable {

    // 1.7.10: Create bounding box differently
    private static final AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(-1, -1, -1, 1, 1, 1)
        .expand(17, 17, 17);
    private static final double descendingDst = 17.0D;

    private int targetId = -1;

    public EntityStarburst(World worldIn) {
        super(worldIn);
    }

    public EntityStarburst(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityStarburst(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        // 1.7.10: shoot() method not available in base Entity - removed call
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            playEffects();
        } else {
            if (targetId == -1) {
                // 1.7.10: Use getOffsetBoundingBox
                AxisAlignedBB box = searchBox.getOffsetBoundingBox(posX, posY, posZ);

                // 1.7.10: Use different method for getting entities
                List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
                // Filter alive entities
                entities.removeIf(e -> e.isDead);

                if (getThrower() != null) {
                    entities.remove(getThrower());
                }

                // TODO: Apply attack check when MiscUtils is available
                // entities.removeIf(e -> !MiscUtils.canPlayerAttackServer(getThrower(), e));

                // Find closest entity manually
                EntityLivingBase closest = null;
                double minDist = Double.MAX_VALUE;
                for (EntityLivingBase e : entities) {
                    double dist = e.getDistanceSqToEntity(this);
                    if (dist < minDist) {
                        minDist = dist;
                        closest = e;
                    }
                }

                if (closest != null) {
                    targetId = closest.getEntityId();
                }
            }

            if (targetId != -1) {
                Entity e = worldObj.getEntityByID(targetId);
                if (e == null || e.isDead || !(e instanceof EntityLivingBase)) {
                    targetId = -1;
                } else {
                    EntityLivingBase entity = (EntityLivingBase) e;

                    // Vector3 homing logic - enabled for 1.7.10
                    Vector3 thisPos = Vector3.atEntityCorner(this);
                    Vector3 targetEntity = Vector3.atEntityCorner(entity);
                    Vector3 dirMotion = targetEntity.clone()
                        .subtract(thisPos);
                    Vector3 currentMotion = new Vector3(this.motionX, this.motionY, this.motionZ);
                    double dst = thisPos.distance(targetEntity);
                    if (dst < descendingDst) {
                        double originalPart = dst / descendingDst;
                        double length = currentMotion.length();
                        currentMotion = dirMotion.multiply(1 - originalPart)
                            .add(
                                currentMotion.clone()
                                    .multiply(originalPart));
                        currentMotion.normalize()
                            .multiply(length);
                    }

                    this.motionX = currentMotion.getX();
                    this.motionY = currentMotion.getY();
                    this.motionZ = currentMotion.getZ();
                }
            }
        }
    }

    /**
     * Play client-side particle effects
     * 1.7.10: Simplified version (MiscUtils.getCirclePositions() not available)
     * EffectHelper is now implemented for 1.7.10
     */
    @SideOnly(Side.CLIENT)
    private void playEffects() {
        EntityFXFacingParticle particle;

        // Random flare particles
        for (int i = 0; i < 2; i++) {
            particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle
                .motion(
                    rand.nextFloat() * 0.03F - rand.nextFloat() * 0.06F,
                    rand.nextFloat() * 0.03F - rand.nextFloat() * 0.06F,
                    rand.nextFloat() * 0.03F - rand.nextFloat() * 0.06F)
                .scale(0.3F);
            switch (rand.nextInt(4)) {
                case 0:
                    particle.setColor(Color.WHITE);
                    break;
                case 1:
                    particle.setColor(new Color(0x69B5FF));
                    break;
                case 2:
                    particle.setColor(new Color(0x0078FF));
                    break;
                default:
                    break;
            }
        }

        // TODO: Re-enable circle effect when MiscUtils.getCirclePositions() is available
        // Simplified: only spawn particles periodically
        if (ticksExisted % 12 == 0) {
            // Manual circle approximation without MiscUtils.getCirclePositions()
            for (int i = 0; i < 8; i++) {
                float angle = (float) (i * Math.PI * 2 / 8);
                float radius = 1F;
                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;

                particle = EffectHelper.genericFlareParticle(posX + offsetX, posY, posZ + offsetZ);
                particle.scale(0.4F)
                    .setMaxAge(20 + rand.nextInt(10));
                particle.motion(
                    rand.nextFloat() * 0.02F - rand.nextFloat() * 0.04F,
                    rand.nextFloat() * 0.02F - rand.nextFloat() * 0.04F,
                    rand.nextFloat() * 0.02F - rand.nextFloat() * 0.04F);
                switch (rand.nextInt(3)) {
                    case 0:
                        particle.setColor(Color.WHITE);
                        break;
                    case 1:
                        particle.setColor(new Color(0x61A2FF));
                        break;
                    case 2:
                        particle.setColor(new Color(0x3A4ABD));
                        break;
                    default:
                        break;
                }
            }
        }

        // Main particle
        particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
        particle.scale(0.6F);
        switch (rand.nextInt(4)) {
            case 0:
                particle.setColor(Color.WHITE);
                break;
            case 1:
                particle.setColor(new Color(0x69B5FF));
                break;
            case 2:
                particle.setColor(new Color(0x0078FF));
                break;
            default:
                break;
        }

        // Trail particle
        particle = EffectHelper.genericFlareParticle(posX + motionX / 2F, posY + motionY / 2F, posZ + motionZ / 2F);
        particle.scale(0.6F);
        switch (rand.nextInt(4)) {
            case 0:
                particle.setColor(Color.WHITE);
                break;
            case 1:
                particle.setColor(new Color(0x69B5FF));
                break;
            case 2:
                particle.setColor(new Color(0x0078FF));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (!worldObj.isRemote) {
            // 1.7.10: Check typeOfHit
            if (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                if (result.entityHit != null && result.entityHit.equals(getThrower())) {
                    return;
                }

                // TODO: Implement celestial strike when available
                /*
                 * CelestialStrike.play(getThrower(), worldObj,
                 * Vector3.atEntityCenter(result.entityHit),
                 * Vector3.atEntityCenter(result.entityHit));
                 */
            }
            setDead();
        }
    }

}
