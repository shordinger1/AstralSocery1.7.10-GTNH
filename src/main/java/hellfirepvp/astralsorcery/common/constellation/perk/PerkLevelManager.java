/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk level manager - Manages perk levels and XP requirements
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Perk level manager - Manages perk levels and XP requirements (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Level calculation from total XP</li>
 * <li>XP requirement lookup</li>
 * <li>Next level percentage calculation</li>
 * <li>Level cap configuration</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No GameStages integration</li>
 * <li>No CraftTweaker integration</li>
 * <li>Fixed level cap (configurable)</li>
 * </ul>
 */
public class PerkLevelManager {

    private static int LEVEL_CAP = 30;
    public static final PerkLevelManager INSTANCE = new PerkLevelManager();

    private Map<Integer, Long> totalExpLevelRequired = new HashMap<>();

    private PerkLevelManager() {
        ensureLevels();
    }

    /**
     * Ensure level requirements are calculated
     */
    private void ensureLevels() {
        if (totalExpLevelRequired.isEmpty()) {
            for (int i = 1; i <= LEVEL_CAP; i++) {
                long prev = totalExpLevelRequired.getOrDefault(i - 1, 0L);
                totalExpLevelRequired.put(i, prev + 150L + (long) Math.pow(2, (i / 2) + 3));
            }
            LogHelper.info("Initialized " + LEVEL_CAP + " perk levels");
        }
    }

    /**
     * Get level from total XP
     *
     * @param totalExp Total experience
     * @param player   The player
     * @return The calculated level
     */
    public int getLevel(double totalExp, EntityPlayer player) {
        return getLevel((long) totalExp, player);
    }

    /**
     * Get level from total XP
     *
     * @param totalExp Total experience
     * @param player   The player
     * @return The calculated level
     */
    private int getLevel(long totalExp, EntityPlayer player) {
        ensureLevels();

        if (totalExp <= 0) {
            return 1;
        }

        int levelCap = getLevelCapFor(player);

        for (int i = 1; i <= levelCap; i++) {
            if (totalExp < totalExpLevelRequired.getOrDefault(i, Long.MAX_VALUE)) {
                return i;
            }
        }
        return levelCap;
    }

    /**
     * Get XP required for level
     *
     * @param level  The level
     * @param player The player
     * @return Total XP required
     */
    public long getExpForLevel(int level, EntityPlayer player) {
        ensureLevels();

        if (level <= 1) {
            return 0;
        }
        int levelCap = getLevelCapFor(player);

        if (level > levelCap) {
            level = levelCap;
        }
        return totalExpLevelRequired.get(level);
    }

    /**
     * Get next level percentage
     *
     * @param totalExp Total current XP
     * @param player   The player
     * @return Percentage to next level (0-1)
     */
    public float getNextLevelPercent(double totalExp, EntityPlayer player) {
        ensureLevels();

        int level = getLevel(totalExp, player);
        if (level >= LEVEL_CAP) {
            return 1F; // Done
        }
        long nextLevel = this.totalExpLevelRequired.getOrDefault(level, 0L);
        long prevLevel = this.totalExpLevelRequired.getOrDefault(level - 1, 0L);
        return ((float) (totalExp - prevLevel)) / ((float) (nextLevel - prevLevel));
    }

    /**
     * Get level cap for player
     *
     * @param player The player
     * @return The level cap
     */
    public static int getLevelCapFor(EntityPlayer player) {
        // TODO: Add permission/gamestage checks when implemented
        return LEVEL_CAP;
    }

    /**
     * Set level cap
     *
     * @param cap The new level cap
     */
    public static void setLevelCap(int cap) {
        LEVEL_CAP = cap;
        LogHelper.info("Perk level cap set to " + cap);
    }

    /**
     * Get current level cap
     *
     * @return The level cap
     */
    public static int getLevelCap() {
        return LEVEL_CAP;
    }

}
