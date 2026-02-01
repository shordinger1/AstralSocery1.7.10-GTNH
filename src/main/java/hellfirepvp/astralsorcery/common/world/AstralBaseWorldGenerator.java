/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base WorldGenerator class for all AstralSorcery world generation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.IWorldGenerator;

/**
 * AstralBaseWorldGenerator - Base class for all AstralSorcery world generators
 * <p>
 * Provides common ore generation methods and world generation utilities.
 * Implements IWorldGenerator for FML integration.
 * <p>
 * All AstralSorcery world generators should extend this class.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public abstract class AstralBaseWorldGenerator implements IWorldGenerator {

    /** Overworld dimension ID */
    public static final int DIMENSION_OVERWORLD = 0;

    /** Nether dimension ID */
    public static final int DIMENSION_NETHER = -1;

    /** End dimension ID */
    public static final int DIMENSION_END = 1;

    // ========== IWorldGenerator Implementation ==========

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {

        switch (world.provider.dimensionId) {
            case DIMENSION_OVERWORLD:
                generateOverworld(random, chunkX, chunkZ, world);
                break;

            case DIMENSION_NETHER:
                generateNether(random, chunkX, chunkZ, world);
                break;

            case DIMENSION_END:
                generateEnd(random, chunkX, chunkZ, world);
                break;

            default:
                generateCustomDimension(random, chunkX, chunkZ, world, world.provider.dimensionId);
                break;
        }
    }

    // ========== Dimension Generation Methods ==========

    /**
     * Generate overworld content
     * Subclasses override to implement custom generation
     */
    public void generateOverworld(Random random, int chunkX, int chunkZ, World world) {
        // Default: no generation
    }

    /**
     * Generate nether content
     * Subclasses override to implement custom generation
     */
    public void generateNether(Random random, int chunkX, int chunkZ, World world) {
        // Default: no generation
    }

    /**
     * Generate end content
     * Subclasses override to implement custom generation
     */
    public void generateEnd(Random random, int chunkX, int chunkZ, World world) {
        // Default: no generation
    }

    /**
     * Generate custom dimension content
     * Subclasses override to implement custom generation
     * 
     * @param dimensionId Dimension ID
     */
    public void generateCustomDimension(Random random, int chunkX, int chunkZ, World world, int dimensionId) {
        // Default: no generation
    }

    // ========== Helper Methods - Ore Generation ==========

    /**
     * Generate ore in stone
     * Most commonly used ore generation method
     *
     * @param ore      Ore block
     * @param world    World instance
     * @param random   Random
     * @param chunkX   Chunk X
     * @param chunkZ   Chunk Z
     * @param veinSize Vein size
     * @param minY     Min Y
     * @param maxY     Max Y
     * @param attempts Attempts per chunk
     */
    protected void generateOre(Block ore, World world, Random random, int chunkX, int chunkZ, int veinSize, int minY,
        int maxY, int attempts) {
        generateOreInBlock(ore, Blocks.stone, world, random, chunkX, chunkZ, veinSize, minY, maxY, attempts);
    }

    /**
     * Generate ore in specific block
     * For generating in non-stone blocks (netherrack, end stone, etc.)
     *
     * @param ore      Ore block
     * @param target   Target block to replace
     * @param world    World instance
     * @param random   Random
     * @param chunkX   Chunk X
     * @param chunkZ   Chunk Z
     * @param veinSize Vein size
     * @param minY     Min Y
     * @param maxY     Max Y
     * @param attempts Attempts per chunk
     */
    protected void generateOreInBlock(Block ore, Block target, World world, Random random, int chunkX, int chunkZ,
        int veinSize, int minY, int maxY, int attempts) {
        if (minY < 0) minY = 0;
        if (maxY > 255) maxY = 255;
        if (minY > maxY) {
            int temp = minY;
            minY = maxY;
            maxY = temp;
        }

        for (int i = 0; i < attempts; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = minY + random.nextInt(maxY - minY + 1);

            new WorldGenMinable(ore, veinSize, target).generate(world, random, x, y, z);
        }
    }

    /**
     * Generate ore with metadata
     * For ores requiring specific metadata (different colored ores)
     */
    protected void generateOreWithMeta(Block ore, int metadata, Block target, World world, Random random, int chunkX,
        int chunkZ, int veinSize, int minY, int maxY, int attempts) {
        if (minY < 0) minY = 0;
        if (maxY > 255) maxY = 255;
        if (minY > maxY) {
            int temp = minY;
            minY = maxY;
            maxY = temp;
        }

        for (int i = 0; i < attempts; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = minY + random.nextInt(maxY - minY + 1);

            new WorldGenMinable(ore, metadata, veinSize, target).generate(world, random, x, y, z);
        }
    }

    // ========== Helper Methods - Feature Generation ==========

    /**
     * Generate custom feature (trees, flowers, structures, etc.)
     *
     * @param generator Feature generator
     * @param world     World instance
     * @param random    Random
     * @param chunkX    Chunk X
     * @param chunkZ    Chunk Z
     * @param minY      Min Y
     * @param maxY      Max Y
     * @param attempts  Attempts
     */
    protected void generateFeature(WorldGenerator generator, World world, Random random, int chunkX, int chunkZ,
        int minY, int maxY, int attempts) {
        if (minY < 0) minY = 0;
        if (maxY > 255) maxY = 255;
        if (minY > maxY) {
            int temp = minY;
            minY = maxY;
            maxY = temp;
        }

        for (int i = 0; i < attempts; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = minY + random.nextInt(maxY - minY + 1);

            generator.generate(world, random, x, y, z);
        }
    }

    /**
     * Generate feature on surface
     * Automatically finds surface height (highest non-air block)
     *
     * @param generator Feature generator
     * @param world     World instance
     * @param random    Random
     * @param chunkX    Chunk X
     * @param chunkZ    Chunk Z
     * @param attempts  Attempts
     */
    protected void generateFeatureOnSurface(WorldGenerator generator, World world, Random random, int chunkX,
        int chunkZ, int attempts) {
        for (int i = 0; i < attempts; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = world.getHeightValue(x, z);

            generator.generate(world, random, x, y, z);
        }
    }

    // ========== Helper Methods - Validation ==========

    /**
     * Check if spawn location is valid
     * Can override in subclass for custom validation
     *
     * @param world World instance
     * @param x,    y, z Position
     * @return true if valid
     */
    protected boolean isValidSpawnLocation(World world, int x, int y, int z) {
        return world.blockExists(x, y, z);
    }

    // ========== Helper Methods - Coordinate Calculation ==========

    /**
     * Get random X in chunk
     * 
     * @param random Random
     * @param chunkX Chunk X
     * @return World X
     */
    protected int getRandomX(Random random, int chunkX) {
        return chunkX * 16 + random.nextInt(16);
    }

    /**
     * Get random Z in chunk
     * 
     * @param random Random
     * @param chunkZ Chunk Z
     * @return World Z
     */
    protected int getRandomZ(Random random, int chunkZ) {
        return chunkZ * 16 + random.nextInt(16);
    }

    /**
     * Get random Y in range
     * 
     * @param random Random
     * @param minY   Min Y
     * @param maxY   Max Y
     * @return Random Y
     */
    protected int getRandomY(Random random, int minY, int maxY) {
        if (minY < 0) minY = 0;
        if (maxY > 255) maxY = 255;
        if (minY > maxY) {
            int temp = minY;
            minY = maxY;
            maxY = temp;
        }
        return minY + random.nextInt(maxY - minY + 1);
    }

    // ========== Ore Configuration Constants ==========

    /** Ultra rare ore config (like diamond) */
    public static class OreConfigUltraRare {

        public static final int VEIN_SIZE = 7;
        public static final int MIN_Y = 5;
        public static final int MAX_Y = 16;
        public static final int ATTEMPTS = 2;
    }

    /** Rare ore config (like gold, redstone) */
    public static class OreConfigRare {

        public static final int VEIN_SIZE = 8;
        public static final int MIN_Y = 5;
        public static final int MAX_Y = 32;
        public static final int ATTEMPTS = 8;
    }

    /** Common ore config (like iron) */
    public static class OreConfigCommon {

        public static final int VEIN_SIZE = 8;
        public static final int MIN_Y = 5;
        public static final int MAX_Y = 64;
        public static final int ATTEMPTS = 20;
    }

    /** Abundant ore config (like coal) */
    public static class OreConfigAbundant {

        public static final int VEIN_SIZE = 16;
        public static final int MIN_Y = 5;
        public static final int MAX_Y = 128;
        public static final int ATTEMPTS = 25;
    }

    /** Nether ore config */
    public static class OreConfigNether {

        public static final int VEIN_SIZE = 8;
        public static final int MIN_Y = 10;
        public static final int MAX_Y = 118;
        public static final int ATTEMPTS = 12;
    }
}
