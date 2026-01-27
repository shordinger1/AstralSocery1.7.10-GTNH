/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;
// TODO: Forge fluid system - manual review needed

import java.awt.*;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.effect.ShootingStarExplosion;

/**
 * This class is part of the BeeBetterAtBees Mod
 * Class: EntityShootingStar
 * Created by HellFirePvP
 * Date: 13.10.2018 / 12:54
 */
public class EntityShootingStar extends EntityThrowable implements EntityTechnicalAmbient {

    // 1.7.10: Store Vector3 as separate components
    private static final int SHOOT_X_DATAWATCHER_ID = 20;
    private static final int SHOOT_Y_DATAWATCHER_ID = 21;
    private static final int SHOOT_Z_DATAWATCHER_ID = 22;
    private static final int EFFECT_SEED_LOW_DATAWATCHER_ID = 23; // Store long as two ints
    private static final int EFFECT_SEED_HIGH_DATAWATCHER_ID = 24;
    private static final int LAST_UPDATE_DATAWATCHER_ID = 25;

    // Not saved or synced value to deny 'capturing' one.
    private boolean removalPending = true;

    public EntityShootingStar(World worldIn) {
        super(worldIn);
    }

    public EntityShootingStar(World worldIn, double x, double y, double z, Vector3 shot) {
        super(worldIn, x, y, z);
        this.setSize(0.1F, 0.1F);
        this.removalPending = false;
        // 1.7.10: Store Vector3 components separately
        this.dataWatcher.updateObject(SHOOT_X_DATAWATCHER_ID, Float.valueOf((float) shot.getX()));
        this.dataWatcher.updateObject(SHOOT_Y_DATAWATCHER_ID, Float.valueOf((float) shot.getY()));
        this.dataWatcher.updateObject(SHOOT_Z_DATAWATCHER_ID, Float.valueOf((float) shot.getZ()));
        // 1.7.10: Store long as two ints
        long seed = rand.nextLong();
        this.dataWatcher.updateObject(EFFECT_SEED_LOW_DATAWATCHER_ID, Integer.valueOf((int) (seed & 0xFFFFFFFFL)));
        this.dataWatcher.updateObject(EFFECT_SEED_HIGH_DATAWATCHER_ID, Integer.valueOf((int) (seed >> 32)));
        this.dataWatcher.updateObject(LAST_UPDATE_DATAWATCHER_ID, Integer.valueOf((int) worldIn.getWorldTime()));
        correctMovement();
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        // 1.7.10: Store Vector3 components as Float
        this.dataWatcher.addObject(SHOOT_X_DATAWATCHER_ID, Float.valueOf(0F));
        this.dataWatcher.addObject(SHOOT_Y_DATAWATCHER_ID, Float.valueOf(0F));
        this.dataWatcher.addObject(SHOOT_Z_DATAWATCHER_ID, Float.valueOf(0F));
        // 1.7.10: Store long as two ints
        this.dataWatcher.addObject(EFFECT_SEED_LOW_DATAWATCHER_ID, Integer.valueOf(0));
        this.dataWatcher.addObject(EFFECT_SEED_HIGH_DATAWATCHER_ID, Integer.valueOf(0));
        this.dataWatcher.addObject(LAST_UPDATE_DATAWATCHER_ID, Integer.valueOf(0));
    }

    private void correctMovement() {
        // 1.7.10: Read Vector3 components separately
        float shotX = this.dataWatcher.getWatchableObjectFloat(SHOOT_X_DATAWATCHER_ID);
        float shotY = this.dataWatcher.getWatchableObjectFloat(SHOOT_Y_DATAWATCHER_ID);
        float shotZ = this.dataWatcher.getWatchableObjectFloat(SHOOT_Z_DATAWATCHER_ID);
        this.motionX = shotX;
        this.motionZ = shotZ;
        if (this.posY >= 500) {
            this.motionY = -0.09;
        } else {
            this.motionY = -0.7F * (1F - (((float) this.posY) / 1000F));
        }
    }

    public long getEffectSeed() {
        // 1.7.10: Reconstruct long from two integers
        int low = this.dataWatcher.getWatchableObjectInt(EFFECT_SEED_LOW_DATAWATCHER_ID);
        int high = this.dataWatcher.getWatchableObjectInt(EFFECT_SEED_HIGH_DATAWATCHER_ID);
        return ((long) high << 32) | (low & 0xFFFFFFFFL);
    }

    // 1.7.10: Helper to get the shot Vector3
    private Vector3 getShotVector() {
        return new Vector3(
            this.dataWatcher.getWatchableObjectFloat(SHOOT_X_DATAWATCHER_ID),
            this.dataWatcher.getWatchableObjectFloat(SHOOT_Y_DATAWATCHER_ID),
            this.dataWatcher.getWatchableObjectFloat(SHOOT_Z_DATAWATCHER_ID));
    }

    // 1.7.10: isInLava doesn't exist, implement manually
    private boolean handleLavaCollision() {
        BlockPos pos = new BlockPos(this);
        return worldObj.getBlock(pos.getX(), pos.getY(), pos.getZ())
            .getMaterial()
            .isLiquid();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // 1.7.10: LAST_UPDATE also needs to be stored as int (world time fits in int)
        int lastTrackedTick = this.dataWatcher.getWatchableObjectInt(LAST_UPDATE_DATAWATCHER_ID);

        if (!worldObj.isRemote) {
            if (removalPending || !ConstellationSkyHandler.getInstance()
                .isNight(worldObj) || worldObj.getWorldTime() - lastTrackedTick >= 20) {
                setDead();
                return;
            }

            this.dataWatcher.updateObject(LAST_UPDATE_DATAWATCHER_ID, worldObj.getWorldTime());

            // 1.7.10: isInLava doesn't exist, check manually
            if (isInWater() || handleLavaCollision()) {
                // 1.7.10: MovingObjectPosition constructor takes (int, int, int, int, Vec3)
                // The int after coordinates is the side hit (0=down, 1=up, 2=north, 3=south, 4=west, 5=east)
                MovingObjectPosition rtr = new MovingObjectPosition(
                    (int) posX,
                    (int) posY,
                    (int) posZ,
                    1, // side hit: 1 = UP
                    Vec3.createVectorHelper(posX, posY, posZ));
                this.onImpact(rtr);
            }
        }

        correctMovement();

        if (worldObj.isRemote) {
            if (!ConstellationSkyHandler.getInstance()
                .isNight(worldObj) || worldObj.getWorldTime() - lastTrackedTick >= 20) {
                setDead();
                return;
            }

            spawnEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnEffects() {
        Vector3 shot = getShotVector(); // 1.7.10: Use helper method
        float positionDist = 96F;

        EntityComplexFX.RenderOffsetController renderCtrl = (fx, currentRenderPos, currentMotion, pTicks) -> {
            EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
            if (pl == null) {
                return currentRenderPos;
            }
            EntityFXFacingParticle pt = (EntityFXFacingParticle) fx;
            Vector3 v = pt.getPosition()
                .clone()
                .subtract(new Vector3(new BlockPos(pl)));
            if (v.length() <= positionDist) {
                return currentRenderPos;
            }
            return Vector3.atEntityCenter(pl)
                .add(
                    v.normalize()
                        .multiply(positionDist));
        };
        EntityComplexFX.ScaleFunction scaleFct = (fx, pos, pTicks, scaleIn) -> {
            EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
            if (pl == null) {
                return scaleIn;
            }
            scaleIn = new EntityComplexFX.ScaleFunction.Shrink<>().getScale((EntityComplexFX) fx, pos, pTicks, scaleIn);
            EntityFXFacingParticle pt = (EntityFXFacingParticle) fx;
            Vector3 v = pt.getPosition()
                .clone()
                .subtract(new Vector3(new BlockPos(pl)));
            float mul = v.length() <= positionDist ? 1 : (float) (positionDist / (v.length()));
            return (scaleIn * 0.25F) + ((mul * scaleIn) - (scaleIn * 0.25F));
        };

        for (int i = 0; i < 4; i++) {
            if (rand.nextFloat() > 0.75F) continue;
            Vector3 dir = shot.clone()
                .multiply(rand.nextFloat() * -0.6F);
            dir.setX(dir.getX() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));
            dir.setZ(dir.getZ() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));
            // dir.rotate(Math.toRadians((30 + rand.nextInt(15)) * (rand.nextBoolean() ? 1 : -1)), dir.perpendicular());
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
            p.setColor(Color.WHITE)
                .setDistanceRemovable(false)
                .scale(1.2F + rand.nextFloat() * 0.5F)
                .motion(dir.getX(), dir.getY(), dir.getZ())
                .setAlphaMultiplier(0.85F)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .setMaxAge(90 + rand.nextInt(40));
            // Position within view distance
            p.setRenderOffsetController(renderCtrl);
            // Make smaller if further away, not too linearly though.
            p.setScaleFunction(scaleFct);
        }

        float scale = 4F + rand.nextFloat() * 3F;
        int age = 5 + rand.nextInt(2);

        Random seeded = new Random(getEffectSeed());
        EntityFXFacingParticle star = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        star.setColor(Color.getHSBColor(seeded.nextFloat() * 360F, 1F, 1F))
            .setDistanceRemovable(false)
            .scale(scale)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
            .setMaxAge(age);
        star.setRenderOffsetController(renderCtrl);
        star.setScaleFunction(scaleFct);
        EntityFXFacingParticle st2 = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        st2.setColor(Color.WHITE)
            .setDistanceRemovable(false)
            .scale(scale * 0.6F)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
            .setMaxAge(Math.round(age * 1.5F));
        st2.setRenderOffsetController(renderCtrl);
        st2.setScaleFunction(scaleFct);
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (removalPending || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
            return;
        }

        setDead();

        BlockPos hit = new BlockPos(result.blockX, result.blockY, result.blockZ);
        if (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            hit = new BlockPos(result.entityHit);
        }
        if (!worldObj.isRemote && MiscUtils.isChunkLoaded(worldObj, hit)) {

            Block state = worldObj.getBlock(hit.getX(), hit.getY(), hit.getZ());
            boolean eligableForExplosion = true;
            if (MiscUtils.isFluidBlock(state)) {
                Fluid f = MiscUtils.tryGetFuild(state);
                if (f != null) {
                    if (f.getTemperature(worldObj, hit.getX(), hit.getY(), hit.getZ()) <= 300) { // About room temp;
                                                                                                 // incl. water
                        eligableForExplosion = false;
                    }
                }
            }

            Vector3 v = Vector3.atEntityCenter(this);
            ShootingStarExplosion.play(worldObj, v, !eligableForExplosion, getEffectSeed());

            EntityItem generated = new EntityItem(
                worldObj,
                v.getX(),
                v.getY(),
                v.getZ(),
                new ItemStack(ItemsAS.fragmentCapsule));
            Vector3 m = new Vector3();
            MiscUtils.applyRandomOffset(m, rand, 0.25F);
            generated.motionX = m.getX();
            generated.motionY = Math.abs(m.getY());
            generated.motionZ = m.getZ();
            // 1.7.10: Use delayBeforeCanPickup field instead of setPickupDelay()
            generated.delayBeforeCanPickup = 20;
            worldObj.spawnEntityInWorld(generated);

            // 1.7.10: Loot tables don't exist, drop additional items manually
            // For now, just drop the capsule (above)
            // TODO: Implement custom loot system for 1.7.10 if needed
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }
}
