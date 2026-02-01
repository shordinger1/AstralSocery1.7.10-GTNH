/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Small Ruin Generator - Ruined marble structure
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Small Ruin Generator (1.7.10)
 * <p>
 * Generates small ruined marble structures.
 * These are remnants of ancient Astral Sorcery structures.
 * <p>
 * <b>Biomes:</b> Any non-aquatic biome
 * <p>
 * <b>Structure:</b> Partial pillar or wall remnant
 * <p>
 * <b>Rarity:</b> 1 in 80 chunks (default) - more common than intact structures
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public class StructureGenSmallRuin extends StructureGenBase {

    /**
     * Constructor with default settings
     */
    public StructureGenSmallRuin() {
        super("smallruin", 80, Type.ANY);
        this.minY = 62;
        this.maxY = 100;
        this.heightTolerance = 4;
    }

    /**
     * Constructor with custom chance
     *
     * @param chance Generation chance (1 in X chunks)
     */
    public StructureGenSmallRuin(int chance) {
        super("smallruin", chance, Type.ANY);
        this.minY = 62;
        this.maxY = 100;
        this.heightTolerance = 4;
    }

    @Override
    protected boolean isValidBiome(BiomeGenBase biome) {
        String biomeName = biome.biomeName.toLowerCase();

        // Only exclude aquatic biomes
        if (biomeName.contains("ocean") ||
            biomeName.contains("river") ||
            biomeName.contains("beach")) {
            return false;
        }

        return true;
    }

    @Override
    protected boolean isValidSpawnLocation(World world, int x, int y, int z) {
        // Ruins can tolerate more uneven terrain
        this.heightTolerance = 6;
        return super.isValidSpawnLocation(world, x, y, z);
    }
}
