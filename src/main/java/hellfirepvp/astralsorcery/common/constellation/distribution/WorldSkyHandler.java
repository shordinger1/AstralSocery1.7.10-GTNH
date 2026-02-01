/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * WorldSkyHandler - Simplified 1.7.10 implementation
 *
 * This is a simplified version for 1.7.10 that:
 * - Uses fixed 8-day constellation cycles
 * - Does NOT implement complex eclipse systems
 * - Does NOT implement client-side constellation position mapping
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.distribution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * WorldSkyHandler (1.7.10 Simplified Version)
 * <p>
 * <b>Purpose</b>: Manages constellation visibility for a specific world
 * <p>
 * <b>Simplifications</b>:
 * <ul>
 * <li>Fixed 8-day constellation cycles (instead of complex distribution)</li>
 * <li>No solar/lunar eclipse system (Phase 3 feature)</li>
 * <li>No client-side position mapping (Phase 3 feature)</li>
 * <li>Simple random distribution based on world seed</li>
 * </ul>
 * <p>
 * <b>Constellation Cycles</b>:
 * <ul>
 * <li>Day 0-7: 8 consecutive days</li>
 * <li>Each day: 3 Major/Weak constellations visible</li>
 * <li>Minor constellations: tied to moon phase</li>
 * </ul>
 */
public class WorldSkyHandler {

    /**
     * Last recorded day (for detecting day changes)
     */
    public int lastRecordedDay = -1;

    /**
     * Currently visible constellations
     */
    private LinkedList<IConstellation> activeConstellations = new LinkedList<>();

    /**
     * Pre-calculated constellation mappings for 8-day cycle
     * Key: Day in cycle (0-7)
     * Value: List of constellations visible on that day
     */
    private java.util.Map<Integer, java.util.LinkedList<IConstellation>> initialValueMappings = new java.util.HashMap<>();

    /**
     * Pre-calculated distribution factors for each constellation
     * Key: Day in cycle (0-7)
     * Value: Map of constellation → distribution factor
     */
    private java.util.Map<Integer, java.util.Map<IConstellation, Float>> dayDistributionMap = new java.util.HashMap<>();

    /**
     * Active distribution factors for current day
     */
    private java.util.Map<IConstellation, Float> activeDistributions = new java.util.HashMap<>();

    /**
     * Random number generator seeded with world seed
     */
    private Random seededRand;

    /**
     * World seed (saved for reference)
     */
    private final long savedSeed;

    /**
     * Eclipse flags (Phase 3: not yet implemented)
     */
    public boolean solarEclipse = false;
    public boolean lunarEclipse = false;

    /**
     * Create a new WorldSkyHandler
     *
     * @param seed The world seed
     */
    public WorldSkyHandler(long seed) {
        this.savedSeed = seed;
        this.seededRand = new Random(seed);
        LogHelper.debug("WorldSkyHandler created with seed: " + seed);

        // Initialize: pre-calculate constellation mappings for all 8 days
        setupInitialConstellations();
    }

    /**
     * Main tick method - call this each world tick
     * Updates constellation visibility based on day progression
     *
     * @param world The world
     */
    public void tick(net.minecraft.world.World world) {
        if (world == null) {
            return;
        }

        // Check if world has a sky (not Nether/End)
        if (world.provider.hasNoSky) {
            return;
        }

        // Check if daylight cycle game rule is enabled
        if (!world.getGameRules()
            .getGameRuleBooleanValue("doDaylightCycle")) {
            return;
        }

        // Calculate current day
        int currentDay = (int) (world.getWorldTime() / 24000L);

        // Check if day has changed
        if (currentDay != lastRecordedDay) {
            int dayDifference = currentDay - lastRecordedDay;

            if (dayDifference > 0) {
                // Forward progression
                updateDayProgression(world, currentDay, dayDifference);
            } else if (dayDifference < 0) {
                // Time went backwards (command changed time?)
                // Reset and recalculate
                setupInitialConstellations();
                updateDayProgression(world, currentDay, currentDay + 1);
            }

            lastRecordedDay = currentDay;
        }
    }

    /**
     * Update constellation progression for multiple days
     *
     * @param world     The world
     * @param targetDay The target day
     * @param days      Number of days to progress
     */
    private void updateDayProgression(net.minecraft.world.World world, int targetDay, int days) {
        if (days <= 0) {
            return;
        }

        // Simplified: just recalculate for the current day
        recalculateActiveConstellations(world, targetDay);
    }

    /**
     * Recalculate active constellations for a specific day
     * Now uses pre-calculated mappings instead of random selection
     *
     * @param world The world
     * @param day   The day number
     */
    private void recalculateActiveConstellations(net.minecraft.world.World world, int day) {
        activeConstellations.clear();
        activeDistributions.clear();

        // 获取8天周期中的天数
        int activeDay = ((day % 8) + 8) % 8;

        // 从预计算的映射中获取星座列表
        java.util.LinkedList<IConstellation> linkedConstellations = initialValueMappings
            .computeIfAbsent(activeDay, k -> new java.util.LinkedList<IConstellation>());

        // 最多10个星座
        for (int i = 0; i < Math.min(10, linkedConstellations.size()); i++) {
            activeConstellations.addLast(linkedConstellations.get(i));
        }

        // 复制分布因子
        java.util.Map<IConstellation, Float> iteration = dayDistributionMap.get(activeDay);
        if (iteration != null) {
            activeDistributions.putAll(iteration);
        }

        LogHelper.debug("Day " + day + ": " + activeConstellations.size() + " constellations visible");
    }

    /**
     * Setup initial constellation mappings
     * Pre-calculates constellation visibility for all 8 days
     */
    private void setupInitialConstellations() {
        LogHelper.debug("Setting up initial constellation mappings...");

        // 初始化8天的映射
        initialValueMappings.clear();
        dayDistributionMap.clear();
        for (int i = 0; i < 8; i++) {
            initialValueMappings.put(i, new java.util.LinkedList<IConstellation>());
            dayDistributionMap.put(i, new java.util.HashMap<IConstellation, Float>());
        }

        refreshRandom();

        // 槽位占用算法
        boolean[] occupied = new boolean[8];
        java.util.Arrays.fill(occupied, false);

        // 1. 处理Minor星座（基于月相）
        java.util.LinkedList<IConstellation> constellations = new java.util.LinkedList<>(
            ConstellationRegistry.getMinorConstellations());
        java.util.Collections.shuffle(constellations, seededRand);

        // 2. 处理Weak/Major星座（槽位占用）
        java.util.LinkedList<IWeakConstellation> weakAndMajor = new java.util.LinkedList<>(
            ConstellationRegistry.getWeakConstellations());
        java.util.Collections.shuffle(weakAndMajor, seededRand);
        weakAndMajor.forEach(constellations::addFirst);

        for (IConstellation c : constellations) {
            // 跳过特殊显示的星座（如果有这个接口）
            try {
                Class<?> specialClass = Class
                    .forName("hellfirepvp.astralsorcery.common.constellation.IConstellationSpecialShowup");
                if (specialClass.isInstance(c)) {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                // 接口不存在，继续
            }

            if (c instanceof IMinorConstellation) {
                // Minor星座：基于月相分配
                for (MoonPhase ph : ((IMinorConstellation) c).getShowupMoonPhases(savedSeed)) {
                    initialValueMappings.get(ph.ordinal())
                        .add(c);
                }
                // Minor星座分布因子=0
                for (int i = 0; i < 8; i++) {
                    dayDistributionMap.get(i)
                        .put(c, 0F);
                }
            } else {
                // Weak/Major星座：槽位占用算法
                int start;
                boolean foundFree = false;
                int tries = 5;
                do {
                    tries--;
                    start = seededRand.nextInt(8);

                    int needed = Math.min(3, getFreeSlots(occupied));
                    int count = collect(start, occupied);
                    if (count >= needed) {
                        foundFree = true;
                    }
                } while (!foundFree && tries > 0);

                occupySlots(start, occupied);
                if (getFreeSlots(occupied) <= 0) {
                    java.util.Arrays.fill(occupied, false);
                }

                // 每个星座连续5天
                for (int i = 0; i < 5; i++) {
                    int index = (start + i) % 8;
                    initialValueMappings.get(index)
                        .addLast(c);
                }

                if (c instanceof IWeakConstellation) {
                    // Weak星座：使用正弦函数计算分布（0.75-1.0）
                    for (int i = 0; i < 8; i++) {
                        int index = (start + i) % 8;
                        float distr = spSine(start, index);
                        dayDistributionMap.get(index)
                            .put(c, distr);
                    }
                } else {
                    // Major星座：分布因子=0
                    for (int i = 0; i < 8; i++) {
                        dayDistributionMap.get(i)
                            .put(c, 0F);
                    }
                }
            }
        }

        LogHelper.info("Pre-calculated constellation mappings for 8-day cycle");
    }

    /**
     * Refresh random number generator with saved seed
     */
    private void refreshRandom() {
        this.seededRand = new java.util.Random(savedSeed);
    }

    /**
     * Calculate sine-based distribution factor
     * Returns value between 0.75 and 1.0
     *
     * @param dayStart The start day
     * @param dayIn    The current day
     * @return Distribution factor
     */
    private float spSine(int dayStart, int dayIn) {
        int v = dayStart > dayIn ? (dayIn + 8) - dayStart : dayIn;
        float part = ((float) v) / 4F;
        return (float) (Math.sin((part * Math.PI)) * 0.25F + 0.75F);
    }

    /**
     * Occupy slots in the occupied array
     *
     * @param start    The start day
     * @param occupied The occupied array
     */
    private void occupySlots(int start, boolean[] occupied) {
        for (int i = 0; i < 5; i++) {
            int index = (start + i) % 8;
            occupied[index] = true;
        }
    }

    /**
     * Count free slots starting from start day
     *
     * @param start    The start day
     * @param occupied The occupied array
     * @return Number of free slots
     */
    private int collect(int start, boolean[] occupied) {
        int found = 0;
        for (int i = 0; i < 5; i++) {
            int index = (start + i) % 8;
            if (!occupied[index]) found++;
        }
        return found;
    }

    /**
     * Get number of free slots in occupied array
     *
     * @param array The occupied array
     * @return Number of free slots
     */
    private int getFreeSlots(boolean[] array) {
        int it = 0;
        for (boolean b : array) {
            if (!b) it++;
        }
        return it;
    }

    /**
     * Get currently active constellations
     *
     * @return List of active constellations
     */
    public LinkedList<IConstellation> getActiveConstellations() {
        return activeConstellations;
    }

    /**
     * Get current moon phase
     *
     * @param world The world
     * @return Current moon phase
     */
    public MoonPhase getCurrentMoonPhase(net.minecraft.world.World world) {
        if (world == null) {
            return MoonPhase.FULL;
        }

        int phase = (int) (world.getWorldTime() / 24000L % 8L);
        return MoonPhase.values()[phase];
    }

    /**
     * Check if a specific constellation is currently visible
     *
     * @param constellation The constellation to check
     * @return true if visible
     */
    public boolean isConstellationVisible(IConstellation constellation) {
        return activeConstellations.contains(constellation);
    }

    /**
     * Get the distribution factor for a constellation
     * Now uses pre-calculated distribution factors
     *
     * @param constellation The constellation
     * @return Distribution factor (0.0 to 1.0)
     */
    public float getConstellationDistribution(IConstellation constellation) {
        if (!activeDistributions.containsKey(constellation)) {
            return 0.0F;
        }
        return activeDistributions.get(constellation);
    }

    /**
     * Get all constellation distributions
     * <p>
     * Returns a map of constellation → distribution factor
     *
     * @return Map of distributions
     */
    public Map<IConstellation, Float> getAllDistributions() {
        Map<IConstellation, Float> distributions = new HashMap<>();

        for (IConstellation constellation : activeConstellations) {
            distributions.put(constellation, getConstellationDistribution(constellation));
        }

        return distributions;
    }

    /**
     * Force recalculation of constellations
     * Useful for testing/debugging
     *
     * @param world The world
     */
    public void forceRecalculation(net.minecraft.world.World world) {
        if (world != null) {
            int currentDay = (int) (world.getWorldTime() / 24000L);

            // 重新初始化
            setupInitialConstellations();

            // 重新计算当前天
            recalculateActiveConstellations(world, currentDay);

            LogHelper.info("Forced constellation recalculation for day " + currentDay);
        }
    }
}
