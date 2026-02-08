/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Small Shrine Generator - Small surface shrine
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import net.minecraft.world.biome.BiomeGenBase;

/**
 * Small Shrine Generator (1.7.10)
 * <p>
 * Generates small shrines in most biomes.
 * These are minimal marble structures.
 * <p>
 * <b>Biomes:</b> Most non-aquatic biomes
 * <p>
 * <b>Structure:</b> Simple 2-block tall shrine
 * <p>
 * <b>Rarity:</b> 1 in 120 chunks (default)
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public class StructureGenSmallShrine extends StructureGenBase {

    /**
     * Constructor with default settings
     */
    public StructureGenSmallShrine() {
        super("smallshrine", 120, Type.ANY);
        this.minY = 62;
        this.maxY = 100;
        this.heightTolerance = 3;
    }

    /**
     * Constructor with custom chance
     *
     * @param chance Generation chance (1 in X chunks)
     */
    public StructureGenSmallShrine(int chance) {
        super("smallshrine", chance, Type.ANY);
        this.minY = 62;
        this.maxY = 100;
        this.heightTolerance = 3;
    }

    @Override
    protected boolean isValidBiome(BiomeGenBase biome) {
        String biomeName = biome.biomeName.toLowerCase();

        // Exclude aquatic and nether biomes
        if (biomeName.contains("ocean") || biomeName.contains("river")
            || biomeName.contains("beach")
            || biomeName.contains("swamp")
            || biomeName.contains("hell")) {
            return false;
        }

        return true;
    }
}
