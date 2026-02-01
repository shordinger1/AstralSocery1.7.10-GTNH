/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * EntityNocturnalSpark - Nocturnal spark entity
 *
 * SKELETON VERSION - Complex spawn logic commented out with TODOs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.Color;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

// TODO: Import EffectHelper when available
// import hellfirepvp.astralsorcery.client.effect.EffectHelper;
// import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
// import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
// import hellfirepvp.astralsorcery.common.util.MiscUtils;
// import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * EntityNocturnalSpark - Nocturnal spark (1.7.10)
 * <p>
 * NOTE: This is a SKELETON implementation. The spawn cycle logic is
 * significantly simplified due to missing dependencies. Full implementation
 * requires: Vector3, MiscUtils, EffectHelper, and constellation system.
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Throwable projectile that spawns monsters at night</li>
 * <li>Client-side particle effects</li>
 * <li>Spawning cycle with anti-dupe mechanism</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>EntityDataManager → dataWatcher</li>
 * <li>RayTraceResult → MovingObjectPosition</li>
 * <li>Biome → BiomeGenBase</li>
 * <li>Biome.SpawnListEntry → BiomeGenBase.SpawnListEntry</li>
 * <li>WorldEntitySpawner.isValidEmptySpawnBlock() - May differ</li>
 * <li>EntityLiving.newInstance() - Use reflection or constructor</li>
 * <li>entity.onInitialSpawn() → Different spawn method</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>EffectHelper - Particle effect system</li>
 * <li>EntityFXFacingParticle - Facing particle entity</li>
 * <li>EntityComplexFX - Complex effect base</li>
 * <li>Vector3 - Vector utility class</li>
 * <li>MiscUtils - Utility functions</li>
 * <li>ConstellationSkyHandler - Night detection</li>
 * </ul>
 */
public class EntityNocturnalSpark extends EntityThrowable implements EntityTechnicalAmbient {

    // 1.7.10: Create bounding box differently
    private static final AxisAlignedBB NO_DUPE_BOX = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1)
        .expand(15, 15, 15);

    private static final int DATA_SPAWNING_IDX = 16; // DataWatcher index for spawning state

    private int ticksSpawning = 0;

    public EntityNocturnalSpark(World worldIn) {
        super(worldIn);
    }

    public EntityNocturnalSpark(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityNocturnalSpark(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        // TODO: shoot() method not available in base Entity in 1.7.10 - remove call
        // shoot(throwerIn, throwerIn.rotationPitch, throwerIn.rotationYaw, 0.0F, 0.7F, 0.9F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        // 1.7.10: Register spawning state in DataWatcher (using Byte for boolean)
        this.getDataWatcher()
            .addObject(DATA_SPAWNING_IDX, Byte.valueOf((byte) 0));
    }

    /**
     * Set spawning state
     * 1.7.10: Uses DataWatcher for synchronization
     */
    public void setSpawning() {
        this.getDataWatcher()
            .updateObject(DATA_SPAWNING_IDX, Byte.valueOf((byte) 1));
        this.ticksSpawning = 0;
    }

    /**
     * Check if spawning
     * 1.7.10: Uses DataWatcher for synchronization
     */
    public boolean isSpawning() {
        return this.getDataWatcher()
            .getWatchableObjectByte(DATA_SPAWNING_IDX) != 0;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (isDead) return;

        if (worldObj.isRemote) {
            playEffects();
        } else {
            if (isSpawning()) {
                ticksSpawning++;
                spawnCycle();
                if (ticksSpawning > 200) {
                    setDead();
                }
            }
        }
    }

    /**
     * Spawn cycle - spawns monsters periodically
     * TODO: Full implementation when all dependencies are available
     */
    @SuppressWarnings("unused")
    private void spawnCycle() {
        // 1.7.10: Use coordinates instead of BlockPos
        int posX = (int) Math.floor(this.posX);
        int posY = (int) Math.floor(this.posY);
        int posZ = (int) Math.floor(this.posZ);

        AxisAlignedBB searchBox = NO_DUPE_BOX.getOffsetBoundingBox(posX, posY, posZ);

        @SuppressWarnings("unchecked")
        List<EntityNocturnalSpark> sparks = worldObj.getEntitiesWithinAABB(EntityNocturnalSpark.class, searchBox);
        for (EntityNocturnalSpark spark : sparks) {
            if (this.equals(spark)) continue;
            if (spark.isDead || !spark.isSpawning()) continue;
            spark.setDead();
        }

        if (rand.nextInt(12) == 0) {
            try {
                // TODO: Implement spawn logic
                /*
                 * BlockPos pos = new BlockPos(posX, posY + 1, posZ);
                 * pos.add(rand.nextInt(2) - rand.nextInt(2),
                 * rand.nextInt(1) - rand.nextInt(1),
                 * rand.nextInt(2) - rand.nextInt(2));
                 * // 1.7.10: Different method for getting spawn list
                 * BiomeGenBase biome = worldObj.getBiomeGenForCoords(posX, posZ);
                 * List<BiomeGenBase.SpawnListEntry> list = biome.getSpawnableList(EnumCreatureType.MONSTER);
                 * if (list == null || list.isEmpty()) return;
                 * BiomeGenBase.SpawnListEntry entry = list.get(rand.nextInt(list.size()));
                 * // No creepers with mobGriefing
                 * if (worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") &&
                 * EntityCreeper.class.isAssignableFrom(entry.entityClass)) return;
                 * Block down = worldObj.getBlock(posX, posY, posZ);
                 * boolean canAtAll = down != Blocks.barrier && down != Blocks.bedrock;
                 * // TODO: Replace WorldEntitySpawner checks with 1.7.10 spawn validation
                 * // if (canAtAll && WorldEntitySpawner.func_151689_a(worldObj, posX, posY, posZ) &&
                 * // WorldEntitySpawner.func_151689_a(worldObj, posX, posY + 1, posZ)) {
                 * if (canAtAll) { // Simplified for compilation - TODO: implement proper spawn validation
                 * try {
                 * EntityLiving entity = entry.entityClass.getConstructor(World.class).newInstance(worldObj);
                 * entity.setLocationAndAngles(posX + 0.5, posY, posZ + 0.5, rand.nextFloat() * 360F, 0F);
                 * // 1.7.10: Different spawn initialization
                 * entity.onSpawnWithEgg(null);
                 * if (!worldObj.checkNoEntityCollision(entity.boundingBox) &&
                 * !worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty()) {
                 * worldObj.spawnEntityInWorld(entity);
                 * }
                 * } catch (Exception e) {
                 * // Silent exception handling for skeleton implementation
                 * }
                 * }
                 */
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    /**
     * Play client-side particle effects
     * EffectHelper is now implemented for 1.7.10
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    private void playEffects() {
        if (isSpawning()) {
            for (int i = 0; i < 15; i++) {
                Vector3 thisPos = Vector3.atEntityCorner(this)
                    .addY(1);
                MiscUtils.applyRandomOffset(thisPos, rand, 2 + rand.nextInt(4));
                EntityFXFacingParticle particle = EffectHelper
                    .genericFlareParticle(thisPos.getX(), thisPos.getY(), thisPos.getZ())
                    .scale(4F)
                    .setColor(Color.BLACK)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                    .gravity(0.004)
                    .setAlphaMultiplier(0.7F);
                if (rand.nextInt(5) == 0) {
                    randomizeColor(particle);
                }
                if (rand.nextInt(3) == 0) {
                    Vector3 target = Vector3.atEntityCorner(this);
                    MiscUtils.applyRandomOffset(target, rand, 4);
                    // AstralSorcery.proxy.fireLightning - not available yet
                }
            }
        } else {
            EntityFXFacingParticle particle;
            for (int i = 0; i < 6; i++) {
                particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
                particle
                    .motion(
                        0.04F - rand.nextFloat() * 0.08F,
                        0.04F - rand.nextFloat() * 0.08F,
                        0.04F - rand.nextFloat() * 0.08F)
                    .scale(0.25F);
                randomizeColor(particle);
            }
            particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.scale(0.6F);
            randomizeColor(particle);
            particle = EffectHelper.genericFlareParticle(posX + motionX / 2F, posY + motionY / 2F, posZ + motionZ / 2F);
            particle.scale(0.6F);
            randomizeColor(particle);
        }

    }

    /**
     * Randomize particle color
     * TODO: Implement when particle system is available
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    private void randomizeColor(Object particle) {
        // TODO: Uncomment when particle system is available
        /*
         * switch (rand.nextInt(3)) {
         * case 0:
         * particle.setColor(Color.BLACK);
         * break;
         * case 1:
         * particle.setColor(new Color(0x4E016D));
         * break;
         * case 2:
         * particle.setColor(new Color(0x0C1576));
         * break;
         * default:
         * break;
         * }
         */
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        // 1.7.10: MovingObjectPosition.MovingObjectType.ENTITY
        if (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return;
        }

        setSpawning();
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        // 1.7.10: result.hitVec is a Vec3
        if (result.hitVec != null) {
            this.posX = result.hitVec.xCoord;
            this.posY = result.hitVec.yCoord;
            this.posZ = result.hitVec.zCoord;
        }
    }

}
