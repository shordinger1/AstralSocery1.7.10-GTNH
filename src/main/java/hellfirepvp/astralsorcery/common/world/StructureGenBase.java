/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Structure Generation Base - Simplified structure world generator
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import hellfirepvp.astralsorcery.common.structure.StructureBuilder;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * StructureGenBase - Base class for structure world generation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Automatic structure generation using StructureLib</li>
 * <li>Biome-aware spawning</li>
 * <li>Height-based validation</li>
 * <li>Rarity control via chance parameter</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * public class StructureAncientShrine extends StructureGenBase {
 * 
 *     public StructureAncientShrine() {
 *         super("ancientShrine", 140, Type.MOUNTAIN);
 *     }
 *
 *     &#64;Override
 *     protected boolean isValidBiome(BiomeGenBase biome) {
 *         return biome.biomeName.equals("Extreme Hills") || biome.biomeName.equals("Ice Mountains");
 *     }
 * }
 * </pre>
 * <p>
 * <b>Structure Types:</b>
 * <ul>
 * <li>MOUNTAIN - Mountain peaks, high altitude</li>
 * <li>DESERT - Desert biomes</li>
 * <li>FOREST - Forest biomes</li>
 * <li>PLAINS - Plains and flat areas</li>
 * <li>ANY - Any biome</li>
 * </ul>
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public abstract class StructureGenBase extends AstralBaseWorldGenerator {

    /** Structure identifier */
    protected final String structureName;

    /** Generation chance (1 in X) */
    protected final int chance;

    /** Structure type for biome filtering */
    protected final Type type;

    /** Minimum Y level for spawning */
    protected int minY = 60;

    /** Maximum Y level for spawning */
    protected int maxY = 120;

    /** Height variance tolerance */
    protected int heightTolerance = 4;

    /**
     * Structure type enumeration
     */
    public enum Type {
        /** Mountain structures (high altitude, mountain biomes) */
        MOUNTAIN,
        /** Desert structures (desert biomes) */
        DESERT,
        /** Forest structures (forest biomes) */
        FOREST,
        /** Plains structures (plains and flat areas) */
        PLAINS,
        /** Any structure (any biome) */
        ANY
    }

    /**
     * Constructor with chance
     *
     * @param structureName Structure name (must match StructureBuilder.buildByName())
     * @param chance        Generation chance (1 in X chunks)
     * @param type          Structure type
     */
    public StructureGenBase(String structureName, int chance, Type type) {
        this.structureName = structureName;
        this.chance = chance;
        this.type = type;
    }

    /**
     * Constructor with default chance
     *
     * @param structureName Structure name
     * @param type          Structure type
     */
    public StructureGenBase(String structureName, Type type) {
        this(structureName, 140, type);
    }

    // ========== Generation Logic ==========

    @Override
    public void generateOverworld(Random random, int chunkX, int chunkZ, World world) {
        // Check generation chance
        if (random.nextInt(chance) != 0) {
            return;
        }

        // Get generation position
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = getSurfaceY(world, x, z);

        // Validate Y level
        if (y < minY || y > maxY) {
            return;
        }

        // Validate biome
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (!isValidBiome(biome)) {
            return;
        }

        // Validate spawn location
        if (!isValidSpawnLocation(world, x, y, z)) {
            return;
        }

        // Generate structure
        generateStructure(world, x, y, z);
    }

    /**
     * Get surface Y level at position
     *
     * @param world World instance
     * @param x     X coordinate
     * @param z     Z coordinate
     * @return Surface Y level
     */
    protected int getSurfaceY(World world, int x, int z) {
        // Find highest non-air block
        for (int y = world.getHeight() - 1; y >= 0; y--) {
            // 1.7.10: Use isOpaqueCube() instead of isOpaque(World, int, int, int)
            if (world.getBlock(x, y, z)
                .isOpaqueCube()) {
                return y;
            }
        }
        return 60; // Default to sea level
    }

    /**
     * Check if biome is valid for this structure
     * Override in subclass for custom biome filtering
     *
     * @param biome Biome at position
     * @return true if valid
     */
    protected boolean isValidBiome(BiomeGenBase biome) {
        if (type == Type.ANY) {
            return true;
        }

        String biomeName = biome.biomeName.toLowerCase();

        switch (type) {
            case MOUNTAIN:
                return biomeName.contains("mountain") || biomeName.contains("hill")
                    || biomeName.contains("extreme")
                    || biomeName.contains("snow");

            case DESERT:
                return biomeName.contains("desert") || biomeName.contains("savanna");

            case FOREST:
                return biomeName.contains("forest") || biomeName.contains("taiga") || biomeName.contains("jungle");

            case PLAINS:
                return biomeName.contains("plain") || biomeName.contains("field");

            default:
                return true;
        }
    }

    /**
     * Check if spawn location is valid
     * Override in subclass for custom validation
     *
     * @param world World instance
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if valid
     */
    protected boolean isValidSpawnLocation(World world, int x, int y, int z) {
        // Check if area is reasonably flat
        int y1 = getSurfaceY(world, x - 4, z - 4);
        int y2 = getSurfaceY(world, x + 4, z - 4);
        int y3 = getSurfaceY(world, x - 4, z + 4);
        int y4 = getSurfaceY(world, x + 4, z + 4);

        int minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
        int maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));

        return (maxY - minY) <= heightTolerance;
    }

    /**
     * Generate structure at position
     * Uses StructureBuilder.buildByName()
     *
     * @param world World instance
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    protected void generateStructure(World world, int x, int y, int z) {
        LogHelper.debug("Generating structure " + structureName + " at " + x + "," + y + "," + z);
        boolean success = StructureBuilder.buildByName(world, x, y, z, structureName);
        if (success) {
            LogHelper.info("Generated structure " + structureName + " at " + x + "," + y + "," + z);
        } else {
            LogHelper.warn("Failed to generate structure " + structureName + " at " + x + "," + y + "," + z);
        }
    }

    // ========== Configuration Getters ==========

    /**
     * Get structure name
     */
    public String getStructureName() {
        return structureName;
    }

    /**
     * Get generation chance
     */
    public int getChance() {
        return chance;
    }

    /**
     * Get structure type
     */
    public Type getType() {
        return type;
    }

    /**
     * Get minimum Y level
     */
    public int getMinY() {
        return minY;
    }

    /**
     * Set minimum Y level
     */
    public void setMinY(int minY) {
        this.minY = minY;
    }

    /**
     * Get maximum Y level
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * Set maximum Y level
     */
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    /**
     * Get height tolerance
     */
    public int getHeightTolerance() {
        return heightTolerance;
    }

    /**
     * Set height tolerance
     */
    public void setHeightTolerance(int heightTolerance) {
        this.heightTolerance = heightTolerance;
    }
}
