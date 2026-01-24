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
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.LootTableUtil;
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

    private static final int SHOOT_CONSTANT_DATAWATCHER_ID = 20;
    private static final int EFFECT_SEED_DATAWATCHER_ID = 21;
    private static final int LAST_UPDATE_DATAWATCHER_ID = 22;

    // Not saved or synced value to deny 'capturing' one.
    private boolean removalPending = true;

    public EntityShootingStar(World worldIn) {
        super(worldIn);
    }

    public EntityShootingStar(World worldIn, double x, double y, double z, Vector3 shot) {
        super(worldIn, x, y, z);
        this.setSize(0.1F, 0.1F);
        this.removalPending = false;
        this.dataWatcher.updateObject(SHOOT_CONSTANT_DATAWATCHER_ID, shot);
        this.dataWatcher.updateObject(EFFECT_SEED_DATAWATCHER_ID, rand.nextLong());
        this.dataWatcher.updateObject(LAST_UPDATE_DATAWATCHER_ID, worldIn.getWorldTime());
        correctMovement();
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataWatcher.addObject(SHOOT_CONSTANT_DATAWATCHER_ID, new Vector3());
        this.dataWatcher.addObject(EFFECT_SEED_DATAWATCHER_ID, 0L);
        this.dataWatcher.addObject(LAST_UPDATE_DATAWATCHER_ID, 0L);
    }

    private void correctMovement() {
        Vector3 shot = (Vector3) this.dataWatcher.getWatchableObjectObject(SHOOT_CONSTANT_DATAWATCHER_ID);
        this.motionX = shot.getX();
        this.motionZ = shot.getZ();
        if (this.posY >= 500) {
            this.motionY = -0.09;
        } else {
            this.motionY = -0.7F * (1F - (((float) this.posY) / 1000F));
        }
    }

    public long getEffectSeed() {
        return this.dataWatcher.getWatchableObjectLong(EFFECT_SEED_DATAWATCHER_ID);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        long lastTrackedTick = this.dataWatcher.getWatchableObjectLong(LAST_UPDATE_DATAWATCHER_ID);

        if (!worldObj.isRemote) {
            if (removalPending || !ConstellationSkyHandler.getInstance()
                .isNight(worldObj) || worldObj.getWorldTime() - lastTrackedTick >= 20) {
                setDead();
                return;
            }

            this.dataWatcher.updateObject(LAST_UPDATE_DATAWATCHER_ID, world.getWorldTime());

            if (isInWater() || isInLava()) {
                MovingObjectPosition rtr = new MovingObjectPosition(
                    new Vec3(0, 0, 0),
                    EnumFacing.UP,
                    new BlockPos(this));
                if (!ForgeEventFactory.onProjectileImpact(this, rtr)) {
                    this.onImpact(rtr);
                }
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
        Vector3 shot = (Vector3) this.dataWatcher.getWatchableObjectObject(SHOOT_CONSTANT_DATAWATCHER_ID);
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

            Block state = world.getBlock(hit);
            boolean eligableForExplosion = true;
            if (MiscUtils.isFluidBlock(state)) {
                Fluid f = MiscUtils.tryGetFuild(state);
                if (f != null) {
                    if (f.getTemperature(world, hit) <= 300) { // About room temp; incl. water
                        eligableForExplosion = false;
                    }
                }
            }

            Vector3 v = Vector3.atEntityCenter(this);
            ShootingStarExplosion.play(world, v, !eligableForExplosion, getEffectSeed());

            EntityItem generated = new EntityItem(
                world,
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
            world.spawnEntityInWorld(generated);

            LootTable table = world.getLootTableManager()
                .getLootTableFromLocation(LootTableUtil.LOOT_TABLE_SHOOTING_STAR);
            if (table != null && world instanceof WorldServer) {
                LootContext context = new LootContext.Builder((WorldServer) world).build();
                List<ItemStack> stacks = table.generateLootForPools(rand, context);
                for (ItemStack stack : stacks) {
                    ItemUtils.dropItemNaturally(world, v.getX(), v.getY(), v.getZ(), stack);
                }
            }
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }
}
