/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ParticleManager alias for EffectRenderer
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility alias for 1.7.10
 * In 1.12.2: net.minecraft.client.particle.ParticleManager
 * In 1.7.10: net.minecraft.client.particle.EffectRenderer
 */
public class ParticleManager extends net.minecraft.client.particle.EffectRenderer {

    public ParticleManager(net.minecraft.world.World worldIn) {
        super(worldIn);
    }
}
