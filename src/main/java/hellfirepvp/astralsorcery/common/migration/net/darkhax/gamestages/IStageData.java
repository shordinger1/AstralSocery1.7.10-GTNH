/*******************************************************************************
 * Migrated from GameStages mod API (1.12.2 version)
 * Adapted for Minecraft 1.7.10
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration.net.darkhax.gamestages;

import java.util.Set;

/**
 * 1.7.10 Migration of IStageData from GameStages mod
 * Original: https://github.com/Darkhax-Minecraft/GameStages
 *
 * Interface for accessing player stage data.
 */
public interface IStageData {

    /**
     * Gets all stages the player has unlocked.
     *
     * @return A set of all stage names the player has.
     */
    Set<String> getStages();
}
