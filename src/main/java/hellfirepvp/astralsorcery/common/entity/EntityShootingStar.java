/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * EntityShootingStar - Shooting star entity
 *
 * SKELETON VERSION - Loot table logic commented out with TODOs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.Color;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * EntityShootingStar - Shooting star (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Throwable projectile that falls from the sky</li>
 * <li>Spawns loot on impact</li>
 * <li>Client-side particle effects</li>
 * <li>Only active at night</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>EntityDataManager → dataWatcher</li>
 * <li>RayTraceResult → MovingObjectPosition</li>
 * <li>World.getTotalWorldTime() → worldObj.getTotalWorldTime() (same)</li>
 * <li>LootTable system - May differ significantly</li>
 * <li>LootContext.Builder - Different constructor</li>
 * <li>world.getLootTableManager() - Different access method</li>
 * <li>IBlockState → Block + metadata</li>
 * <li>FluidStack → Different fluid system</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>ConstellationSkyHandler - Night detection</li>
 * <li>ItemsAS.fragmentCapsule - Fragment capsule item</li>
 * <li>ShootingStarExplosion - Explosion effect</li>
 * <li>LootTableUtil - Loot table utility</li>
 * <li>LootTable/LootContext - Loot system</li>
 * <li>MiscUtils - Utility functions</li>
 * </ul>
 */
public class EntityShootingStar extends EntityThrowable implements EntityTechnicalAmbient {

    // Not saved or synced value to deny 'capturing' one
    private boolean removalPending = true;
    private Vector3 shotDirection;

    public EntityShootingStar(World worldIn) {
        super(worldIn);
    }

    public EntityShootingStar(World worldIn, double x, double y, double z, Vector3 shot) {
        super(worldIn, x, y, z);
        this.setSize(0.1F, 0.1F);
        this.removalPending = false;
        this.shotDirection = shot;
        correctMovement();
    }

    /**
     * Correct movement vector based on position
     * 1.7.10: Simplified version without dataManager
     */
    private void correctMovement() {
        if (shotDirection != null) {
            this.motionX = shotDirection.getX();
            this.motionZ = shotDirection.getZ();
            if (this.posY >= 500) {
                this.motionY = -0.09;
            } else {
                this.motionY = -0.7F * (1F - (((float) this.posY) / 1000F));
            }
        }
    }

    /**
     * Get effect seed for particle effects
     * 1.7.10: Return random seed
     */
    public long getEffectSeed() {
        return rand.nextLong();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!worldObj.isRemote) {
            if (removalPending) {
                setDead();
                return;
            }

            // TODO: Check if night when ConstellationSkyHandler is available
            // if (!ConstellationSkyHandler.getInstance().isNight(worldObj)) {
            // setDead();
            // return;
            // }

            // 1.7.10: isInLava() not available - only check water
            if (isInWater()) {
                MovingObjectPosition rtr = new MovingObjectPosition((int) posX, (int) posY, (int) posZ, 1, null); // side
                                                                                                                  // 1 =
                                                                                                                  // UP
                this.onImpact(rtr);
            }
        }

        correctMovement();

        if (worldObj.isRemote) {
            // TODO: Check night condition
            // if (!ConstellationSkyHandler.getInstance().isNight(worldObj)) {
            // setDead();
            // return;
            // }

            spawnEffects();
        }
    }

    /**
     * Spawn particle effects
     * 1.7.10: Simplified version (RenderOffsetController and ScaleFunction not available)
     * EffectHelper is now implemented for 1.7.10
     */
    @SideOnly(Side.CLIENT)
    private void spawnEffects() {
        if (shotDirection == null) return;

        // Simplified particle effect - basic flare particles
        for (int i = 0; i < 4; i++) {
            if (rand.nextFloat() > 0.75F) continue;

            Vector3 dir = shotDirection.clone()
                .multiply(rand.nextFloat() * -0.6F);
            dir.setX(dir.getX() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));
            dir.setZ(dir.getZ() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
            p.setColor(Color.WHITE)
                .scale(1.2F + rand.nextFloat() * 0.5F)
                .motion(dir.getX(), dir.getY(), dir.getZ())
                .setMaxAge(90 + rand.nextInt(40));
        }

        // Star particle with rainbow color
        float scale = 4F + rand.nextFloat() * 3F;
        int age = 5 + rand.nextInt(2);

        Color starColor = Color.getHSBColor(rand.nextFloat(), 1F, 1F);
        EntityFXFacingParticle star = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        star.setColor(starColor)
            .scale(scale)
            .setMaxAge(age);

        EntityFXFacingParticle st2 = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        st2.setColor(Color.WHITE)
            .scale(scale * 0.6F)
            .setMaxAge(Math.round(age * 1.5F));
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (removalPending || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
            return;
        }

        setDead();

        // 1.7.10: Get coordinates from result
        int hitX = result.blockX;
        int hitY = result.blockY;
        int hitZ = result.blockZ;

        if (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && result.entityHit != null) {
            hitX = (int) result.entityHit.posX;
            hitY = (int) result.entityHit.posY;
            hitZ = (int) result.entityHit.posZ;
        }

        if (!worldObj.isRemote) {
            // TODO: Check if chunk is loaded
            // if (!MiscUtils.isChunkLoaded(worldObj, hit)) return;

            // TODO: Implement explosion effect
            /*
             * IBlockState state = worldObj.getBlockState(hit);
             * boolean eligibleForExplosion = true;
             * if (MiscUtils.isFluidBlock(state)) {
             * Fluid f = MiscUtils.tryGetFluid(state);
             * if (f != null) {
             * if (f.getTemperature(worldObj, hit) <= 300) {
             * eligibleForExplosion = false;
             * }
             * }
             * }
             * Vector3 v = Vector3.atEntityCenter(this);
             * ShootingStarExplosion.play(worldObj, v, !eligibleForExplosion, getEffectSeed());
             * // Spawn fragment capsule
             * EntityItem generated = new EntityItem(worldObj, v.getX(), v.getY(), v.getZ(),
             * new ItemStack(ItemsAS.fragmentCapsule));
             * Vector3 m = new Vector3();
             * MiscUtils.applyRandomOffset(m, rand, 0.25F);
             * generated.motionX = m.getX();
             * generated.motionY = Math.abs(m.getY());
             * generated.motionZ = m.getZ();
             * generated.delayBeforeCanPickup = 20;
             * worldObj.spawnEntityInWorld(generated);
             * // Loot table generation
             * LootTable table = worldObj.getLootTableManager().getLootTableFromLocation(
             * LootTableUtil.LOOT_TABLE_SHOOTING_STAR);
             * if (table != null && worldObj instanceof WorldServer) {
             * LootContext context = new LootContext.Builder((WorldServer) worldObj).build();
             * List<ItemStack> stacks = table.generateLootForPools(rand, context);
             * for (ItemStack stack : stacks) {
             * ItemUtils.dropItemNaturally(worldObj, v.getX(), v.getY(), v.getZ(), stack);
             * }
             * }
             */
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

}
