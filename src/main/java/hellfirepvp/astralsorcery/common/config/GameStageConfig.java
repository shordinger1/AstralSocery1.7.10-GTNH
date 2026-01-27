/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * Configuration for Game Stage modifications
 * Replaces the CraftTweaker GameStageTweaks class
 * This is a code-based configuration system instead of script-based
 */
public class GameStageConfig {

    private static Map<String, Integer> stageLevelCap = new HashMap<>();
    private static Map<String, Collection<String>> constellationStages = new HashMap<>();

    private GameStageConfig() {}

    /**
     * Adds a level cap for a game stage
     *
     * @param stageName The game stage name
     * @param levelCap  The level cap to apply
     */
    public static void addLevelCap(String stageName, int levelCap) {
        stageLevelCap.put(stageName, levelCap);
    }

    /**
     * Adds a constellation discovery stage requirement
     *
     * @param stageName                    The game stage name
     * @param unlocalizedConstellationName The constellation name
     */
    public static void addConstellationDiscoveryStage(String stageName, String unlocalizedConstellationName) {
        if (!constellationStages.containsKey(unlocalizedConstellationName)) {
            constellationStages.put(unlocalizedConstellationName, Lists.newArrayList());
        }
        constellationStages.get(unlocalizedConstellationName)
            .add(stageName);
    }

    /**
     * Gets the maximum level cap for a game stage
     *
     * @param gameStageName The game stage name
     * @return The level cap, or -1 if no cap is set
     */
    public static int getMaxCap(String gameStageName) {
        return stageLevelCap.getOrDefault(gameStageName, -1);
    }

    /**
     * Checks if a constellation can be discovered with the given game stages
     *
     * @param gameStages        The player's game stages
     * @param constellationName The constellation name
     * @return true if the constellation can be discovered
     */
    public static boolean canDiscover(Collection<String> gameStages, String constellationName) {
        Collection<String> stages = constellationStages.getOrDefault(constellationName, Lists.newArrayList());
        if (gameStages == null || stages == null || stages.isEmpty()) {
            return true;
        }
        for (String gameStage : gameStages) {
            if (stages.contains(gameStage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all configuration
     */
    public static void clear() {
        stageLevelCap.clear();
        constellationStages.clear();
    }
}
