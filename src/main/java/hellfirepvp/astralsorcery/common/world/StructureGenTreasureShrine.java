/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Treasure Shrine Generator - Treasure-containing shrine
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import net.minecraft.world.biome.BiomeGenBase;

/**
 * Treasure Shrine Generator (1.7.10)
 * <p>
 * Generates treasure shrines in various biomes.
 * These contain loot chests with Astral Sorcery items.
 * <p>
 * <b>Biomes:</b> Forest, Plains, and similar biomes
 * <p>
 * <b>Structure:</b> Small shrine with chest
 * <p>
 * <b>Rarity:</b> 1 in 180 chunks (default)
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public class StructureGenTreasureShrine extends StructureGenBase {

    /**
     * Constructor with default settings
     */
    public StructureGenTreasureShrine() {
        super("treasureshrine", 180, Type.ANY);
        this.minY = 62;
        this.maxY = 90;
        this.heightTolerance = 3;
    }

    /**
     * Constructor with custom chance
     *
     * @param chance Generation chance (1 in X chunks)
     */
    public StructureGenTreasureShrine(int chance) {
        super("treasureshrine", chance, Type.ANY);
        this.minY = 62;
        this.maxY = 90;
        this.heightTolerance = 3;
    }

    @Override
    protected boolean isValidBiome(BiomeGenBase biome) {
        String biomeName = biome.biomeName.toLowerCase();

        // Exclude aquatic, nether, and mountain biomes
        if (biomeName.contains("ocean") || biomeName.contains("river")
            || biomeName.contains("beach")
            || biomeName.contains("swamp")
            || biomeName.contains("hell")
            || biomeName.contains("extreme")
            || biomeName.contains("mountain")) {
            return false;
        }

        return true;
    }
}
