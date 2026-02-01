/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Ancient Shrine Generator - Mountain shrine structure
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Ancient Shrine Generator (1.7.10)
 * <p>
 * Generates ancient shrines in mountain biomes.
 * These are large structures with celestial crystals.
 * <p>
 * <b>Biomes:</b> Extreme Hills, Ice Mountains, etc.
 * <p>
 * <b>Structure:</b> Multi-tier marble shrine with crystal on top
 * <p>
 * <b>Rarity:</b> 1 in 200 chunks (default)
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public class StructureGenAncientShrine extends StructureGenBase {

    /**
     * Constructor with default settings
     */
    public StructureGenAncientShrine() {
        super("ancientshrine", 200, Type.MOUNTAIN);
        this.minY = 70;
        this.maxY = 130;
        this.heightTolerance = 6;
    }

    /**
     * Constructor with custom chance
     *
     * @param chance Generation chance (1 in X chunks)
     */
    public StructureGenAncientShrine(int chance) {
        super("ancientshrine", chance, Type.MOUNTAIN);
        this.minY = 70;
        this.maxY = 130;
        this.heightTolerance = 6;
    }

    @Override
    protected boolean isValidBiome(BiomeGenBase biome) {
        String biomeName = biome.biomeName.toLowerCase();

        // Mountain biomes
        return biomeName.contains("extreme") ||
               biomeName.contains("mountain") ||
               biomeName.contains("hill") ||
               biomeName.contains("snow") ||
               biomeName.equals("taiga") ||
               biomeName.equals("taigahills");
    }

    @Override
    protected boolean isValidSpawnLocation(World world, int x, int y, int z) {
        // Check if area is reasonably flat (larger radius for big structure)
        int radius = 7;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int dx = -radius; dx <= radius; dx += 4) {
            for (int dz = -radius; dz <= radius; dz += 4) {
                int surfaceY = getSurfaceY(world, x + dx, z + dz);
                minY = Math.min(minY, surfaceY);
                maxY = Math.max(maxY, surfaceY);
            }
        }

        return (maxY - minY) <= heightTolerance;
    }
}
