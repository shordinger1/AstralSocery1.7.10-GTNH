/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.util.math.Vec3d;

/**
 * Sound helper for Astral Sorcery (1.7.10)
 * <p>
 * Provides utility methods for playing sounds in different contexts.
 * In 1.7.10, sounds are played using string names rather than SoundEvent objects.
 *
 * @author HellFirePvP
 * @date 06.12.2016 / 12:45
 *
 *       1.7.10 Migration:
 *       - Replaced SoundEvent with String
 *       - Replaced SoundCategory with String
 *       - Uses world.playSoundAtEntity() and world.playSoundEffect()
 *       - Client-side uses Minecraft.getMinecraft().sndManager
 */
public class SoundHelper {

    /**
     * Play a sound around a position in the world
     *
     * @param sound  The sound name (e.g., "astralsorcery:attunement")
     * @param world  The world to play in
     * @param pos    The position
     * @param volume The volume (0.0 to 1.0+)
     * @param pitch  The pitch (0.5 to 2.0)
     */
    public static void playSoundAround(String sound, World world, Vec3d pos, float volume, float pitch) {
        playSoundAround(sound, SoundUtils.SoundCategories.MASTER, world, pos, volume, pitch);
    }

    /**
     * Play a categorized sound around a position in the world
     *
     * @param sound    The sound name
     * @param category The sound category
     * @param world    The world to play in
     * @param pos      The position
     * @param volume   The volume
     * @param pitch    The pitch
     */
    public static void playSoundAround(String sound, String category, World world, Vec3d pos, float volume,
        float pitch) {
        playSoundAround(sound, category, world, pos.x, pos.y, pos.z, volume, pitch);
    }

    /**
     * Play a sound at precise coordinates
     *
     * @param sound    The sound name
     * @param category The sound category
     * @param world    The world to play in
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param volume   The volume
     * @param pitch    The pitch
     */
    public static void playSoundAround(String sound, String category, World world, double x, double y, double z,
        float volume, float pitch) {
        // In 1.7.10, playSoundEffect takes (x, y, z, soundName, volume, pitch)
        world.playSoundEffect(x, y, z, sound, volume, pitch);
    }

    /**
     * Play a categorized sound event
     *
     * @param categorizedSound The categorized sound event
     * @param world            The world to play in
     * @param pos              The position
     * @param volume           The volume
     * @param pitch            The pitch
     */
    public static void playSoundAround(SoundUtils.CategorizedSoundEvent categorizedSound, World world, Vec3d pos,
        float volume, float pitch) {
        playSoundAround(categorizedSound.getSoundName(), categorizedSound.getCategory(), world, pos, volume, pitch);
    }

    /**
     * Play sound at a position (for blocks/tiles)
     *
     * @param sound  The sound name
     * @param world  The world
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param z      Z coordinate
     * @param volume The volume
     * @param pitch  The pitch
     */
    public static void playSoundAtBlock(String sound, World world, double x, double y, double z, float volume,
        float pitch) {
        world.playSoundEffect(x, y, z, sound, volume, pitch);
    }

    /**
     * Play sound at an entity (usually player)
     *
     * @param sound  The sound name
     * @param entity The entity to play at
     * @param volume The volume
     * @param pitch  The pitch
     */
    public static void playSoundAtEntity(String sound, Entity entity, float volume, float pitch) {
        if (entity instanceof EntityPlayer) {
            playSoundAtPlayer(sound, (EntityPlayer) entity, volume, pitch);
        } else {
            entity.worldObj.playSoundAtEntity(entity, sound, volume, pitch);
        }
    }

    /**
     * Play sound at a player
     *
     * @param sound  The sound name
     * @param player The player
     * @param volume The volume
     * @param pitch  The pitch
     */
    public static void playSoundAtPlayer(String sound, EntityPlayer player, float volume, float pitch) {
        player.worldObj.playSoundAtEntity(player, sound, volume, pitch);
    }

    /**
     * Play sound for all nearby players
     *
     * @param sound  The sound name
     * @param world  The world
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param z      Z coordinate
     * @param range  The range (in blocks)
     * @param volume The volume
     * @param pitch  The pitch
     */
    public static void playSoundToNearby(String sound, World world, double x, double y, double z, double range,
        float volume, float pitch) {
        world.playSoundEffect(x, y, z, sound, volume, pitch);
        // Note: 1.7.10 doesn't have a direct "play to nearby" method
        // playSoundEffect already handles this correctly
    }

    // ========== Client-side methods ==========

    /**
     * Play a sound on the client side
     *
     * @param sound  The sound name
     * @param volume The volume
     * @param pitch  The pitch
     */
    @SideOnly(Side.CLIENT)
    public static void playSoundClient(String sound, float volume, float pitch) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player != null) {
            playSoundAtPlayer(sound, player, volume, pitch);
        }
    }

    /**
     * Play a categorized sound event on the client
     *
     * @param categorizedSound The categorized sound event
     * @param volume           The volume
     * @param pitch            The pitch
     */
    @SideOnly(Side.CLIENT)
    public static void playSoundClient(SoundUtils.CategorizedSoundEvent categorizedSound, float volume, float pitch) {
        playSoundClient(categorizedSound.getSoundName(), volume, pitch);
    }

    /**
     * Play a sound on the client at a specific position
     *
     * @param sound    The sound name
     * @param category The category (may be ignored in 1.7.10)
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param volume   The volume
     * @param pitch    The pitch
     */
    @SideOnly(Side.CLIENT)
    public static void playSoundClientWorld(String sound, String category, double x, double y, double z, float volume,
        float pitch) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc.theWorld != null) {
            // 1.7.10: playSound signature is (x, y, z, sound, volume, pitch, distanceDelay)
            mc.theWorld.playSound(x, y, z, sound, volume, pitch, false);
        }
    }

    /**
     * Play a categorized sound on the client at a specific position
     *
     * @param categorizedSound The categorized sound event
     * @param pos              The position
     * @param volume           The volume
     * @param pitch            The pitch
     */
    @SideOnly(Side.CLIENT)
    public static void playSoundClientWorld(SoundUtils.CategorizedSoundEvent categorizedSound, Vec3d pos, float volume,
        float pitch) {
        playSoundClientWorld(
            categorizedSound.getSoundName(),
            categorizedSound.getCategory(),
            pos.x,
            pos.y,
            pos.z,
            volume,
            pitch);
    }

    // ========== Utility methods ==========

    /**
     * Get the current sound volume for a category (client-side only)
     *
     * @param category The category
     * @return The volume (0.0 to 1.0)
     */
    @SideOnly(Side.CLIENT)
    public static float getSoundVolume(String category) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        // 1.7.10: No sndManager field, and no musicVolume field in gameSettings
        // Return 1.0 as default volume
        if (mc.gameSettings == null) {
            return 1.0F;
        }

        // 1.7.10 gameSettings has: soundLevels (Map<String, Float>), but accessing it directly
        // is complex. For now, return default volume.
        return 1.0F;
    }

}
