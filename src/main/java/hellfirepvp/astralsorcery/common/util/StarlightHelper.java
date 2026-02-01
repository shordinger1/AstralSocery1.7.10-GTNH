/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * StarlightHelper - Utility for starlight collection and calculation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.Random;

import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;

/**
 * StarlightHelper - Utility class for starlight calculations
 * <p>
 * This class provides methods to:
 * - Calculate starlight collection based on world conditions
 * - Check constellation visibility
 * - Calculate starlight transmission efficiency
 * - Determine optimal collection times
 * <p>
 * Phase 2.3: Core starlight collection logic
 */
public class StarlightHelper {

    private static final Random rand = new Random();

    /**
     * Calculate starlight collection for a position
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return Starlight amount collected (0.0 to 1.0)
     */
    public static float calculateStarlightCollection(World world, int x, int y, int z) {
        // Check if can see sky
        if (!world.canBlockSeeTheSky(x, y, z)) {
            return 0.0F;
        }

        // Base collection amount
        float collection = 0.0F;

        // Time-based multiplier
        float timeMultiplier = calculateTimeMultiplier(world);
        if (timeMultiplier <= 0.0F) {
            return 0.0F;
        }

        // Height-based multiplier
        float heightMultiplier = calculateHeightMultiplier(y);

        // Weather penalty
        float weatherMultiplier = calculateWeatherMultiplier(world);

        // Combine multipliers
        collection = 100.0F; // Base amount
        collection *= heightMultiplier;
        collection *= timeMultiplier;
        collection *= weatherMultiplier;

        // Noise-based variation (using position as seed)
        float noise = calculateSkyNoise(world, x, y, z);
        collection *= (0.6F + (0.4F * noise));

        return Math.max(0.0F, Math.min(1.0F, collection / 1000.0F));
    }

    /**
     * Calculate time-based starlight multiplier
     * <p>
     * Starlight is only available at night
     *
     * @param world The world
     * @return Multiplier (0.0 to 1.0)
     */
    public static float calculateTimeMultiplier(World world) {
        long time = world.getWorldTime() % 24000L;

        // Day: 0 - 12000
        // Sunset: 12000 - 13000
        // Night: 13000 - 23000
        // Sunrise: 23000 - 24000

        if (time >= 13000L && time <= 23000L) {
            // Full night - full starlight
            return 1.0F;
        } else if ((time >= 12000L && time < 13000L) || (time > 23000L && time <= 24000L)) {
            // Transition periods - reduced starlight
            return 0.3F;
        } else {
            // Day - no starlight
            return 0.0F;
        }
    }

    /**
     * Calculate height-based starlight multiplier
     * <p>
     * Higher altitudes collect more starlight
     *
     * @param y Y coordinate
     * @return Multiplier (0.0 to 2.0+)
     */
    public static float calculateHeightMultiplier(int y) {
        if (y <= 20) {
            return 0.0F; // Too low
        } else if (y <= 120) {
            // Linear from 20 to 120
            return (y - 20) / 100.0F;
        } else {
            // Above 120, bonus but with diminishing returns
            float bonus = (y - 120) / 272.0F;
            return 1.0F + Math.min(bonus, 1.0F);
        }
    }

    /**
     * Calculate weather-based starlight multiplier
     * <p>
     * Rain and storms reduce starlight collection
     *
     * @param world The world
     * @return Multiplier (0.0 to 1.0)
     */
    public static float calculateWeatherMultiplier(World world) {
        if (world.isRaining()) {
            if (world.isThundering()) {
                // Storm - severe reduction
                return 0.1F;
            } else {
                // Rain - moderate reduction
                return 0.5F;
            }
        }
        return 1.0F; // Clear weather
    }

    /**
     * Calculate sky noise for variation
     * <p>
     * This adds natural variation to starlight collection
     * based on position and world seed
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return Noise value (0.0 to 1.0)
     */
    public static float calculateSkyNoise(World world, int x, int y, int z) {
        // Simple noise based on position and world seed
        long seed = world.getSeed() + (long) x * 3129871L + (long) z * 116129781L + (long) y * 73423L;
        seed = seed * seed * 4234887351L + 12345;
        int noise = (int) (seed >> 16);
        return (Math.abs(noise % 1000) / 1000.0F);
    }

    /**
     * Check if a constellation is currently visible
     *
     * @param world         The world
     * @param constellation The constellation to check
     * @return true if visible
     */
    public static boolean isConstellationVisible(World world, IConstellation constellation) {
        // Check if night
        if (calculateTimeMultiplier(world) <= 0.0F) {
            return false;
        }

        // Check weather
        if (calculateWeatherMultiplier(world) < 0.5F) {
            return false; // Too cloudy
        }

        // Check moon phase for minor constellations
        if (constellation instanceof hellfirepvp.astralsorcery.common.constellation.IMinorConstellation) {
            hellfirepvp.astralsorcery.common.constellation.IMinorConstellation minor = (hellfirepvp.astralsorcery.common.constellation.IMinorConstellation) constellation;

            MoonPhase currentPhase = getCurrentMoonPhase(world);
            java.util.List<MoonPhase> visiblePhases = minor.getShowupMoonPhases(world.getSeed());

            if (!visiblePhases.contains(currentPhase)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get current moon phase
     *
     * @param world The world
     * @return Current moon phase
     */
    public static MoonPhase getCurrentMoonPhase(World world) {
        int phase = (int) (world.getWorldTime() / 24000L % 8L);
        return MoonPhase.values()[phase];
    }

    /**
     * Calculate starlight transmission between two points
     * <p>
     * This checks if starlight can travel from point A to point B
     * without being blocked
     *
     * @param world The world
     * @param x1,   y1, z1 Start position
     * @param x2,   y2, z2 End position
     * @return Transmission efficiency (0.0 to 1.0)
     */
    public static float calculateTransmission(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        // Simple line-of-sight check
        // TODO: Implement raytracing for accurate transmission

        double dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));

        // Distance-based attenuation
        float attenuation = (float) (1.0F / (1.0F + (dist / 64.0F)));

        // Check if both endpoints can see sky
        boolean seesSky1 = world.canBlockSeeTheSky(x1, y1, z1);
        boolean seesSky2 = world.canBlockSeeTheSky(x2, y2, z2);

        if (!seesSky1 || !seesSky2) {
            attenuation *= 0.5F; // Penalty for not seeing sky
        }

        return attenuation;
    }

    /**
     * Get optimal collection time
     *
     * @param world The world
     * @return true if currently optimal collection time
     */
    public static boolean isOptimalCollectionTime(World world) {
        // Optimal at midnight during clear weather
        long time = world.getWorldTime() % 24000L;

        // Midnight is at 18000
        boolean isMidnight = time >= 17000L && time <= 19000L;
        boolean isClear = !world.isRaining();

        return isMidnight && isClear;
    }

    /**
     * Calculate constellation discovery chance
     *
     * @param world The world
     * @param x,    y, z Position
     * @return Discovery chance (0.0 to 1.0)
     */
    public static float calculateDiscoveryChance(World world, int x, int y, int z) {
        float baseChance = 0.1F; // 10% base chance

        // Time bonus
        float timeBonus = calculateTimeMultiplier(world);
        if (timeBonus > 0.8F) {
            baseChance += 0.2F; // Bonus for full night
        }

        // Height bonus
        float heightBonus = calculateHeightMultiplier(y);
        baseChance += heightBonus * 0.1F;

        // Weather penalty
        float weatherPenalty = calculateWeatherMultiplier(world);
        baseChance *= weatherPenalty;

        return Math.min(1.0F, Math.max(0.0F, baseChance));
    }

    /**
     * Check if starlight collection is possible at position
     *
     * @param world The world
     * @param x,    y, z Position
     * @return true if collection is possible
     */
    public static boolean canCollectStarlight(World world, int x, int y, int z) {
        // Must see sky
        if (!world.canBlockSeeTheSky(x, y, z)) {
            return false;
        }

        // Must be night
        if (calculateTimeMultiplier(world) <= 0.0F) {
            return false;
        }

        // Height must be sufficient
        if (y <= 20) {
            return false;
        }

        return true;
    }
}
