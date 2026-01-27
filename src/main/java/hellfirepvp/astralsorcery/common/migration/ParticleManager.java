/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ParticleManager alias for EffectRenderer
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureManager;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * Compatibility alias for 1.7.10
 * In 1.12.2: net.minecraft.client.particle.ParticleManager
 * In 1.7.10: net.minecraft.client.particle.EffectRenderer
 */
public class ParticleManager extends net.minecraft.client.particle.EffectRenderer {

    public ParticleManager(net.minecraft.world.World worldIn, TextureManager textureManager) {
        // 1.7.10: EffectRenderer constructor takes World and TextureManager
        super(worldIn, textureManager);
    }

    // Convenience constructor that gets TextureManager from Minecraft instance
    public ParticleManager(net.minecraft.world.World worldIn) {
        // 1.7.10: Get TextureManager from Minecraft instance
        this(worldIn, net.minecraft.client.Minecraft.getMinecraft().renderEngine);
    }

    /**
     * 1.12.2 compatibility: addBlockDestroyEffects(BlockPos, Block)
     * In 1.7.10, this takes (int x, int y, int z, Block block, int metadata)
     */
    public void addBlockDestroyEffects(BlockPos pos, Block block) {
        int meta = 0; // Default metadata
        addBlockDestroyEffects(pos.getX(), pos.getY(), pos.getZ(), block, meta);
    }
}
