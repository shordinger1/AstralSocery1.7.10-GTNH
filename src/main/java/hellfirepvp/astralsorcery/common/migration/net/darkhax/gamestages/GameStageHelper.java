/*******************************************************************************
 * Migrated from GameStages mod API (1.12.2 version)
 * Adapted for Minecraft 1.7.10
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration.net.darkhax.gamestages;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 1.7.10 Migration of GameStageHelper from GameStages mod
 * Original: https://github.com/Darkhax-Minecraft/GameStages
 *
 * This is a stub implementation - the actual functionality requires the GameStages mod to be present.
 */
public final class GameStageHelper {

    /**
     * Gets the stage data for a player.
     *
     * @param player The player to get data for.
     * @return The player's stage data, or null if GameStages is not installed.
     */
    @Nullable
    public static IStageData getPlayerData(@Nullable EntityPlayer player) {
        // This is wrapped with @Optional.Method in actual usage
        // If the mod is not present, the method won't be called
        return null;
    }

    /**
     * Checks if the player has the specified stage.
     *
     * @param player The player to check.
     * @param stage  The stage to check for.
     * @return Whether the player has the stage. Returns true if GameStages is not installed.
     */
    public static boolean hasStage(@Nullable EntityPlayer player, String stage) {
        // This is wrapped with @Optional.Method in actual usage
        // If the mod is not present, the method won't be called
        return false;
    }

    /**
     * Checks if the client player has the specified stage.
     *
     * @param stage The stage to check for.
     * @return Whether the client player has the stage.
     */
    public static boolean clientHasStage(String stage) {
        // This is wrapped with @Optional.Method in actual usage
        // If the mod is not present, the method won't be called
        return false;
    }

    /**
     * Checks if the client player has the specified stage.
     *
     * @param player The player to check.
     * @param stage  The stage to check for.
     * @return Whether the player has the stage.
     */
    public static boolean clientHasStage(@Nullable EntityPlayer player, String stage) {
        // This is wrapped with @Optional.Method in actual usage
        // If the mod is not present, the method won't be called
        return false;
    }
}
