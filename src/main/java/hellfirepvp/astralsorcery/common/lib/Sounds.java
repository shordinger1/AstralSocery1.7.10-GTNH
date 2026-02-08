/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.util.SoundUtils;

/**
 * Sound event definitions for Astral Sorcery (1.7.10)
 * <p>
 * This class contains all sound event definitions used by the mod.
 * Sounds are defined in sounds.json and referenced here by name.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * <ul>
 * <li>Sounds are referenced by String names instead of SoundEvent objects</li>
 * <li>Sounds are defined in resources/assets/astralsorcery/sounds.json</li>
 * <li>Use SoundHelper to play these sounds</li>
 * </ul>
 *
 * @author HellFirePvP
 * @date 06.12.2016 / 12:54
 */
public class Sounds {

    /**
     * Clip switching sound
     * <p>
     * Played when switching between clips/tools
     * Category: BLOCKS
     */
    public static String clipSwitch;

    /**
     * Crafting completion sound
     * <p>
     * Played when altar crafting is completed
     * Category: MASTER
     */
    public static String craftFinish;

    /**
     * Attunement sound
     * <p>
     * Played during the attunement process
     * Category: MASTER
     */
    public static String attunement;

    /**
     * Book closing sound
     * <p>
     * Played when the journal/book is closed
     * Category: MASTER
     */
    public static String bookClose;

    /**
     * Book page flipping sound
     * <p>
     * Played when flipping pages in the journal
     * Category: MASTER
     */
    public static String bookFlip;

    /**
     * Categorized sound events for easy access
     * <p>
     * These combine the sound name with its category for playback
     */
    public static SoundUtils.CategorizedSoundEvent clipSwitchEvent;
    public static SoundUtils.CategorizedSoundEvent craftFinishEvent;
    public static SoundUtils.CategorizedSoundEvent attunementEvent;
    public static SoundUtils.CategorizedSoundEvent bookCloseEvent;
    public static SoundUtils.CategorizedSoundEvent bookFlipEvent;

    /**
     * Initialize all sound events
     * <p>
     * This method should be called during mod initialization to set up
     * all sound references and categorized events.
     */
    public static void init() {
        // Sound name definitions (matching sounds.json)
        clipSwitch = "astralsorcery:clipSwitch";
        craftFinish = "astralsorcery:craftFinish";
        attunement = "astralsorcery:attunement";
        bookClose = "astralsorcery:bookClose";
        bookFlip = "astralsorcery:bookFlip";

        // Create categorized events for easier playback
        clipSwitchEvent = new SoundUtils.CategorizedSoundEvent(clipSwitch, SoundUtils.SoundCategories.BLOCK);
        craftFinishEvent = new SoundUtils.CategorizedSoundEvent(craftFinish, SoundUtils.SoundCategories.MASTER);
        attunementEvent = new SoundUtils.CategorizedSoundEvent(attunement, SoundUtils.SoundCategories.MASTER);
        bookCloseEvent = new SoundUtils.CategorizedSoundEvent(bookClose, SoundUtils.SoundCategories.MASTER);
        bookFlipEvent = new SoundUtils.CategorizedSoundEvent(bookFlip, SoundUtils.SoundCategories.MASTER);
    }

    /**
     * Utility method to play a sound by name
     *
     * @param soundName The sound name to play
     * @param world     The world
     * @param x         X coordinate
     * @param y         Y coordinate
     * @param z         Z coordinate
     * @param volume    Volume (0.0 to 1.0+)
     * @param pitch     Pitch (0.5 to 2.0)
     */
    public static void play(String soundName, net.minecraft.world.World world, double x, double y, double z,
        float volume, float pitch) {
        world.playSoundEffect(x, y, z, soundName, volume, pitch);
    }

}
