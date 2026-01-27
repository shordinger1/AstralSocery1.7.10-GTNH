/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import java.util.Collection;

import hellfirepvp.astralsorcery.common.config.GameStageConfig;

/**
 * Game Stage configuration helpers
 * Replaces CraftTweaker-based GameStageTweaks class
 *
 * This class now delegates to GameStageConfig
 *
 * Usage:
 * GameStageTweaks.addLevelCap("stage_name", 10);
 * GameStageTweaks.addConstellationDiscoveryStage("stage_name", "constellation_name");
 */
public final class GameStageTweaks {

    private GameStageTweaks() {}

    /**
     * Adds a level cap for a game stage
     *
     * @param stageName The game stage name
     * @param levelCap  The level cap to apply
     */
    public static void addLevelCap(String stageName, int levelCap) {
        GameStageConfig.addLevelCap(stageName, levelCap);
    }

    /**
     * Adds a constellation discovery stage requirement
     *
     * @param stageName                    The game stage name
     * @param unlocalizedConstellationName The constellation name
     */
    public static void addConstellationDiscoveryStage(String stageName, String unlocalizedConstellationName) {
        GameStageConfig.addConstellationDiscoveryStage(stageName, unlocalizedConstellationName);
    }

    /**
     * Gets the maximum level cap for a game stage
     *
     * @param gameStageName The game stage name
     * @return The level cap, or -1 if no cap is set
     */
    public static int getMaxCap(String gameStageName) {
        return GameStageConfig.getMaxCap(gameStageName);
    }

    /**
     * Checks if a constellation can be discovered with the given game stages
     *
     * @param gameStages        The player's game stages
     * @param constellationName The constellation name
     * @return true if the constellation can be discovered
     */
    public static boolean canDiscover(Collection<String> gameStages, String constellationName) {
        return GameStageConfig.canDiscover(gameStages, constellationName);
    }
}
