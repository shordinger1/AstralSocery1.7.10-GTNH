/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * EntityIlluminationSpark - Illumination spark entity
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.Color;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;

/**
 * EntityIlluminationSpark - Illumination spark (1.7.10)
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
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            playEffects();
        }
    }

    /**
     * Play client-side particle effects
     * EffectHelper is now implemented for 1.7.10
     */
    @SuppressWarnings("unused")
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

    /**
     * Randomize particle color
     */
    @SuppressWarnings("unused")
    private void randomizeColor(EntityFXFacingParticle particle) {
        switch (rand.nextInt(3)) {
            case 0:
                particle.setColor(Color.WHITE);
                break;
            case 1:
                particle.setColor(new Color(0xFEFF9E));
                break;
            case 2:
                particle.setColor(new Color(0xFFAA00));
                break;
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        // 1.7.10: Impact handling
        // TODO: Place volatile light block when BlocksAS is implemented
        this.setDead();
    }
}
