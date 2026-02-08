/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

/**
 * Sound utilities for Astral Sorcery (1.7.10)
 * <p>
 * Provides helper classes for categorized sound events.
 * In 1.7.10, sounds are referenced by String names instead of SoundEvent objects.
 *
 * @author HellFirePvP
 * @date 06.12.2016 / 17:02
 *
 *       1.7.10 Migration:
 *       - Replaced SoundEvent with String sound names
 *       - Removed LoopableSoundEvent (not needed in 1.7.10)
 *       - Simplified CategorizedSoundEvent to store name and category
 */
public class SoundUtils {

    /**
     * Categorized sound event for 1.7.10
     * <p>
     * Combines a sound name with its category for easier playback.
     * In 1.7.10, sounds are played using string names rather than SoundEvent objects.
     */
    public static class CategorizedSoundEvent {

        private final String soundName;
        private final String category;

        /**
         * Create a categorized sound event
         *
         * @param soundName The sound name (e.g., "astralsorcery:attunement")
         * @param category  The sound category (e.g., "master", "blocks", "ambient")
         */
        public CategorizedSoundEvent(String soundName, String category) {
            this.soundName = soundName;
            this.category = category;
        }

        /**
         * Get the sound name
         *
         * @return The sound name string
         */
        public String getSoundName() {
            return soundName;
        }

        /**
         * Get the sound category
         *
         * @return The category string
         */
        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return "CategorizedSoundEvent{name='" + soundName + "', category='" + category + "'}";
        }
    }

    /**
     * Standard sound categories for Minecraft 1.7.10
     * <p>
     * These match the valid categories for sounds.json
     */
    public static class SoundCategories {

        public static final String MASTER = "master";
        public static final String MUSIC = "music";
        public static final String RECORD = "record";
        public static final String WEATHER = "weather";
        public static final String BLOCK = "block";
        public static final String HOSTILE = "hostile";
        public static final String NEUTRAL = "neutral";
        public static final String PLAYER = "player";
        public static final String AMBIENT = "ambient";
    }

}
