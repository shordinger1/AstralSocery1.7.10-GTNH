/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.awt.*;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityNocturnalSpark
 * Created by HellFirePvP
 * Date: 03.07.2017 / 13:32
 */
public class EntityNocturnalSpark extends EntityThrowable implements EntityTechnicalAmbient {

    // 1.7.10: grow() doesn't exist, use expand() instead
    private static final AxisAlignedBB NO_DUPE_BOX = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1)
        .expand(15, 15, 15);

    private static final int SPAWNING_DATAWATCHER_ID = 20;
    private int ticksSpawning = 0;

    public EntityNocturnalSpark(World worldIn) {
        super(worldIn);
    }

    public EntityNocturnalSpark(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityNocturnalSpark(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        // 1.7.10: shoot() doesn't exist - use setThrowableHeading() instead
        Vec3 look = throwerIn.getLookVec();
        this.setThrowableHeading(look.xCoord, look.yCoord, look.zCoord, 0.7F, 0.9F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataWatcher.addObject(SPAWNING_DATAWATCHER_ID, Byte.valueOf((byte) 0));
    }

    public void setSpawning() {
        this.dataWatcher.updateObject(SPAWNING_DATAWATCHER_ID, Byte.valueOf((byte) 1));
    }

    public boolean isSpawning() {
        return this.dataWatcher.getWatchableObjectByte(SPAWNING_DATAWATCHER_ID) == 1;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (isDead) return; // Uhh.......... mojang pls

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

    private void spawnCycle() {
        // 1.7.10: offset() needs separate x, y, z coordinates
        BlockPos bp = new BlockPos(this);
        AxisAlignedBB box = NO_DUPE_BOX.getOffsetBoundingBox(bp.getX(), bp.getY(), bp.getZ());
        List<EntityNocturnalSpark> sparks = worldObj.getEntitiesWithinAABB(EntityNocturnalSpark.class, box);
        for (EntityNocturnalSpark spark : sparks) {
            if (this.equals(spark)) continue;
            if (spark.isDead || !spark.isSpawning()) continue;
            spark.setDead();
        }
        if (rand.nextInt(12) == 0 && worldObj instanceof WorldServer) {
            try {
                BlockPos pos = new BlockPos(this).add(0, 1, 0);
                pos.add(
                    rand.nextInt(2) - rand.nextInt(2),
                    0, // rand.nextInt(1) - rand.nextInt(1) is always 0
                    rand.nextInt(2) - rand.nextInt(2));
                // 1.7.10: getPossibleCreatures takes world coordinates (x, y, z), not chunk coords
                List<BiomeGenBase.SpawnListEntry> list = ((WorldServer) worldObj).getChunkProvider()
                    .getPossibleCreatures(EnumCreatureType.monster, pos.getX(), pos.getY(), pos.getZ());
                list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(
                    (WorldServer) worldObj,
                    EnumCreatureType.monster,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    list);
                if (list == null  || list.isEmpty()) return;
                BiomeGenBase.SpawnListEntry entry = list.get(rand.nextInt(list.size())); // Intentionally non-weighted.
                // 1.7.10: entityClass is a public field, method is getGameRuleBooleanValue
                if (worldObj.getGameRules()
                    .getGameRuleBooleanValue("mobGriefing") && EntityCreeper.class.isAssignableFrom(entry.entityClass))
                    return; // No.

                // 1.7.10: BARRIER doesn't exist, check directly
                Block down = worldObj.getBlock(bp.getX(), bp.getY(), bp.getZ());
                boolean canAtAll = down != Blocks.bedrock;
                if (canAtAll && worldObj.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
                    // 1.7.10: Create entity using reflection or direct constructor
                    EntityLiving entity = (EntityLiving) entry.entityClass.getConstructor(World.class)
                        .newInstance(worldObj);
                    entity.setPositionAndRotation(
                        pos.getX() + 0.5,
                        pos.getY(),
                        pos.getZ() + 0.5,
                        rand.nextFloat() * 360F,
                        0F);
                    // 1.7.10: onInitialSpawn doesn't exist, entity is already prepared
                    if (!net.minecraftforge.event.ForgeEventFactory
                        .doSpecialSpawn(entity, worldObj, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F)) {
                        // No special spawn handling needed in 1.7.10
                    }
                    // 1.7.10: isNotColliding doesn't exist, check manually
                    if (!worldObj.checkNoEntityCollision(entity.boundingBox)) {
                        worldObj.spawnEntityInWorld(entity);
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    @SideOnly(Side.CLIENT)
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
                    AstralSorcery.proxy.fireLightning(worldObj, Vector3.atEntityCorner(this), target, Color.BLACK);
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

    @SideOnly(Side.CLIENT)
    private void randomizeColor(EntityFXFacingParticle particle) {
        switch (rand.nextInt(3)) {
            case 0:
                particle.setColor(Color.BLACK);
                break;
            case 1:
                particle.setColor(new Color(0x4E016D));
                break;
            case 2:
                particle.setColor(new Color(0x0C1576));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (MovingObjectPosition.MovingObjectType.ENTITY.equals(result.typeOfHit)) {
            return;
        }
        Vec3 hit = result.hitVec;
        setSpawning();
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.posX = hit.xCoord;
        this.posY = hit.yCoord;
        this.posZ = hit.zCoord;
    }

}
