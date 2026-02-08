/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Positioned loop sound for Astral Sorcery (1.7.10)
 * <p>
 * A sound that plays at a specific position and can loop until stopped.
 * Useful for continuous ambient sounds like machine hums or ritual effects.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * <ul>
 * <li>Extends PositionedSoundRecord (1.7.10 equivalent)</li>
 * <li>Does not implement ITickableSound (not available in 1.7.10)</li>
 * <li>Uses ActivityFunction to determine when to stop</li>
 * </ul>
 *
 * @author HellFirePvP
 * @date 06.12.2016 / 18:05
 */
@SideOnly(Side.CLIENT)
public class PositionedLoopSound extends PositionedSoundRecord {

    private ActivityFunction func = null;
    private boolean hasStoppedPlaying = false;

    /**
     * Create a positioned loop sound
     *
     * @param sound    The sound resource location
     * @param category The sound category
     * @param volume   The volume (0.0 to 1.0+)
     * @param pitch    The pitch (0.5 to 2.0)
     * @param pos      The position
     */
    public PositionedLoopSound(ResourceLocation sound, String category, float volume, float pitch, Vec3 pos) {
        super(sound, volume, pitch, (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
        // Note: 1.7.10 PositionedSoundRecord constructor is simpler
        // Parameters: sound, volume, pitch, repeat, delay, x, y, z
    }

    /**
     * Create a positioned loop sound with default settings
     *
     * @param soundName The sound name (e.g., "astralsorcery:attunement")
     * @param volume    The volume
     * @param pitch     The pitch
     * @param pos       The position
     */
    public PositionedLoopSound(String soundName, float volume, float pitch, Vec3 pos) {
        this(new ResourceLocation(soundName), "master", volume, pitch, pos);
    }

    /**
     * Set the refresh/stop function
     * <p>
     * This function is called to determine if the sound should stop playing.
     *
     * @param func The activity function
     */
    public void setRefreshFunction(ActivityFunction func) {
        this.func = func;
    }

    /**
     * Check if the sound has stopped playing
     * <p>
     * Returns true if no function is set or if the function indicates to stop.
     *
     * @return true if the sound should stop
     */
    public boolean hasStoppedPlaying() {
        if (func == null) {
            return true; // No function means play once then stop
        }
        hasStoppedPlaying = func.shouldStop();
        return hasStoppedPlaying;
    }

    /**
     * Activity function interface
     * <p>
     * Implement this to control when a loop sound should stop.
     */
    @SideOnly(Side.CLIENT)
    public static interface ActivityFunction {

        /**
         * Check if the sound should stop
         *
         * @return true to stop playing, false to continue
         */
        boolean shouldStop();

    }

}
