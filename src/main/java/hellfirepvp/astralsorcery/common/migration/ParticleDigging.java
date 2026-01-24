/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ParticleDigging class for block break particles
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.particle.ParticleDigging
 * In 1.7.10: EntityFX is used instead
 */
public class ParticleDigging extends Particle {

    private Block block;

    public ParticleDigging(World world, double x, double y, double z, Block block) {
        super(world, x, y, z);
        this.block = block;
        this.particleIcon = getIconForBlock(block);
    }

    private TextureAtlasSprite getIconForBlock(Block block) {
        return net.minecraft.client.Minecraft.getMinecraft()
            .getRenderGlobal()
            .getBlockModelShapes()
            .getTexture(block);
    }

    public static class Factory {

        public Factory() {}

        public ParticleDigging createParticle(int particleId, World world, double x, double y, double z, double xSpeed,
            double ySpeed, double zSpeed, int... blockId) {
            Block block = Block.getBlockById(blockId.length > 0 ? blockId[0] : 0);
            return new ParticleDigging(world, x, y, z, block);
        }
    }
}
