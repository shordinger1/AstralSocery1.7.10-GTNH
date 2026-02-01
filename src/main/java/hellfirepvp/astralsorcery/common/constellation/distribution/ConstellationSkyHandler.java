/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationSkyHandler - Simplified 1.7.10 implementation
 *
 * This is a simplified version for 1.7.10 that:
 * - Manages WorldSkyHandler instances per dimension
 * - Uses fixed 8-day constellation cycles
 * - Does NOT implement complex eclipse systems
 * - Does NOT implement client-server seed synchronization
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.distribution;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * ConstellationSkyHandler (1.7.10 Simplified Version)
 * <p>
 * <b>Purpose</b>: Manages constellation visibility for each dimension
 * <p>
 * <b>Simplifications</b>:
 * <ul>
 * <li>Removed network seed synchronization (1.7.10 doesn't have ITickHandler)</li>
 * <li>Removed client-server split (single handler for both)</li>
 * <li>Simplified eclipse system (not implemented in Phase 2)</li>
 * <li>Fixed 8-day constellation cycles</li>
 * </ul>
 * <p>
 * <b>Usage</b>:
 * 
 * <pre>
 * 
 * WorldSkyHandler handler = ConstellationSkyHandler.getInstance()
 *     .getWorldHandler(world);
 * List&lt;IConstellation&gt; visible = handler.getActiveConstellations();
 * </pre>
 */
public class ConstellationSkyHandler {

    private static final ConstellationSkyHandler instance = new ConstellationSkyHandler();

    /**
     * Map of dimension ID to WorldSkyHandler
     * Key: dimension ID (0 = Overworld, -1 = Nether, 1 = End)
     * Value: WorldSkyHandler instance for that dimension
     */
    private final Map<Integer, WorldSkyHandler> worldHandlers = new HashMap<>();

    /**
     * Private constructor
     */
    private ConstellationSkyHandler() {
        LogHelper.info("ConstellationSkyHandler initialized (1.7.10 simplified version)");
    }

    /**
     * Get the singleton instance
     *
     * @return The ConstellationSkyHandler instance
     */
    public static ConstellationSkyHandler getInstance() {
        return instance;
    }

    /**
     * Get the WorldSkyHandler for a specific world
     * Creates a new handler if one doesn't exist
     *
     * @param world The world
     * @return The WorldSkyHandler for this world
     */
    public WorldSkyHandler getWorldHandler(net.minecraft.world.World world) {
        if (world == null) {
            return null;
        }

        int dimensionId = world.provider.dimensionId;

        WorldSkyHandler handler = worldHandlers.get(dimensionId);
        if (handler == null) {
            // Create new handler with world seed
            long seed = world.getSeed();
            handler = new WorldSkyHandler(seed);
            worldHandlers.put(dimensionId, handler);

            LogHelper.debug("Created WorldSkyHandler for dimension " + dimensionId + " with seed " + seed);
        }

        return handler;
    }

    /**
     * Get the WorldSkyHandler by dimension ID
     *
     * @param dimensionId The dimension ID
     * @return The WorldSkyHandler, or null if not found
     */
    public WorldSkyHandler getWorldHandler(int dimensionId) {
        return worldHandlers.get(dimensionId);
    }

    /**
     * Clear all handlers (for testing/debugging)
     */
    public void clearAllHandlers() {
        worldHandlers.clear();
        LogHelper.info("Cleared all WorldSkyHandlers");
    }

    /**
     * Get the current constellation distribution factor
     * This is used for starlight collection calculations
     * <p>
     * Returns a value between 0.0 and 1.0 based on:
     * - Time of day (night = higher)
     * - Smooth transitions at dusk and dawn
     *
     * @param world The world
     * @return Distribution factor (0.0 to 1.0)
     */
    public float getCurrentDaytimeDistribution(net.minecraft.world.World world) {
        if (world == null) {
            return 0.5F;
        }

        int dLength = 24000; // 1.7.10固定dayLength
        float dayPart = ((world.getWorldTime() % dLength) + dLength) % dLength;

        // 白天 = 0 (无星光)
        if (dayPart < (dLength / 2F)) return 0F;

        float part = dLength / 7F; // 过渡期长度 (~3429 ticks)

        // 黄昏过渡 (0 → 1)
        if (dayPart < ((dLength / 2F) + part)) {
            return ((dayPart - ((dLength / 2F) + part)) / part) + 1F;
        }

        // 黎明过渡 (1 → 0)
        if (dayPart > (dLength - part)) {
            return 1F - (dayPart - (dLength - part)) / part;
        }

        // 深夜 = 1.0 (满星光)
        return 1F;
    }

    /**
     * Check if it's currently night time
     *
     * @param world The world
     * @return true if distribution >= 0.6
     */
    public boolean isNight(net.minecraft.world.World world) {
        return getCurrentDaytimeDistribution(world) >= 0.6;
    }

    /**
     * Check if it's currently day time
     *
     * @param world The world
     * @return true if distribution <= 0.4
     */
    public boolean isDay(net.minecraft.world.World world) {
        return getCurrentDaytimeDistribution(world) <= 0.4;
    }

    /**
     * Get the solar eclipse half duration
     * <p>
     * Used for effect calculations to determine how long solar eclipses last
     *
     * @return Half duration in ticks (dayLength / 10)
     */
    public static int getSolarEclipseHalfDuration() {
        return 24000 / 10; // 1.7.10固定dayLength = 24000
    }

    /**
     * Get the lunar eclipse half duration
     * <p>
     * Used for effect calculations to determine how long lunar eclipses last
     *
     * @return Half duration in ticks (dayLength / 10)
     */
    public static int getLunarEclipseHalfDuration() {
        return 24000 / 10; // 1.7.10固定dayLength = 24000
    }

    /**
     * World tick event handler
     * Called each world tick on both client and server
     *
     * @param event The tick event
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            net.minecraft.world.World world = event.world;
            WorldSkyHandler handler = getWorldHandler(world);
            if (handler != null) {
                handler.tick(world);
            }
        }
    }

    /**
     * World unload event handler
     * Cleans up handlers when worlds are unloaded
     *
     * @param event The world event
     */
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world != null) {
            int dimensionId = event.world.provider.dimensionId;
            worldHandlers.remove(dimensionId);
            LogHelper.debug("Cleared WorldSkyHandler for dimension " + dimensionId);
        }
    }

    /**
     * Check if a constellation is currently visible
     *
     * @param world         The world
     * @param constellation The constellation to check
     * @return true if visible
     */
    public boolean isConstellationVisible(net.minecraft.world.World world,
        hellfirepvp.astralsorcery.common.constellation.IConstellation constellation) {
        WorldSkyHandler handler = getWorldHandler(world);
        if (handler == null) {
            return false;
        }

        return handler.getActiveConstellations()
            .contains(constellation);
    }

    /**
     * Get all currently visible constellations
     *
     * @param world The world
     * @return List of visible constellations
     */
    public java.util.List<hellfirepvp.astralsorcery.common.constellation.IConstellation> getVisibleConstellations(
        net.minecraft.world.World world) {
        WorldSkyHandler handler = getWorldHandler(world);
        if (handler == null) {
            return java.util.Collections.emptyList();
        }

        return new java.util.ArrayList<>(handler.getActiveConstellations());
    }

    /**
     * Reset the handler for a specific world
     * This will recalculate constellation visibility from scratch
     *
     * @param world The world to reset
     */
    public void resetWorldHandler(net.minecraft.world.World world) {
        if (world == null) {
            return;
        }

        int dimensionId = world.provider.dimensionId;
        worldHandlers.remove(dimensionId);

        LogHelper.info("Reset WorldSkyHandler for dimension " + dimensionId);
    }
}
