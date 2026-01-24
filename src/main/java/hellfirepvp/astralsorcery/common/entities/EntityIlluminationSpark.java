/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.awt.*;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityIlluminationSpark
 * Created by HellFirePvP
 * Date: 08.04.2017 / 00:24
 */
public class EntityIlluminationSpark extends EntityThrowable implements EntityTechnicalAmbient {

    public EntityIlluminationSpark(World worldIn) {
        super(worldIn);
    }

    public EntityIlluminationSpark(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityIlluminationSpark(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        shoot(throwerIn, throwerIn.rotationPitch, throwerIn.rotationYaw, 0.0F, 0.7F, 0.9F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (getWorld().isRemote) {
            playEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
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

    @SideOnly(Side.CLIENT)
    private void randomizeColor(EntityFXFacingParticle particle) {
        switch (rand.nextInt(3)) {
            case 0:
                particle.setColor(Color.WHITE);
                break;
            case 1:
                particle.setColor(new Color(0xFEFF9E));
                break;
            case 2:
                particle.setColor(new Color(0xFFE539));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (!getWorld().isRemote) {
            if (result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (worldObj.canPlaceEntityOnSide(
                    BlocksAS.blockVolatileLight,
                    new BlockPos(result.blockX, result.blockY, result.blockZ).offset(result.sideHit),
                    false,
                    result.sideHit,
                    null,
                    null)) {
                    BlockPos placePos = new BlockPos(result.blockX, result.blockY, result.blockZ)
                        .offset(result.sideHit);
                    worldObj
                        .setBlock(placePos.getX(), placePos.getY(), placePos.getZ(), BlocksAS.blockVolatileLight, 0, 3);
                }
            } else if (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                if (result.entityHit.equals(getThrower())) {
                    return;
                }
            }
            setDead();
        }
    }

}
