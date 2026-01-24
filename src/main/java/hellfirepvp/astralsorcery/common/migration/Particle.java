/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * Particle class for particles
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.particle.Particle
 * In 1.7.10: EntityFX is used instead
 */
public class Particle extends net.minecraft.client.particle.EntityFX {

    public TextureAtlasSprite particleIcon;

    public Particle(net.minecraft.world.World world, double x, double y, double z) {
        super(world, x, y, z);
    }
}
