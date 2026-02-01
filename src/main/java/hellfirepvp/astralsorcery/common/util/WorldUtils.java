/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * World utility class for API compatibility
 *
 * This class provides compatibility methods between 1.12.2 and 1.7.10 World/TileEntity APIs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * WorldUtils - Utility class for World and TileEntity operations
 * <p>
 * <b>Purpose</b>: Provides 1.12.2-like API methods for 1.7.10 World/TileEntity handling
 * <p>
 * <b>Key Differences</b>:
 * <ul>
 * <li>1.12.2 uses world.isRemote for client check</li>
 * <li>1.7.10 uses worldObj.isRemote (same method, different field name)</li>
 * <li>1.12.2 uses getPos() for position</li>
 * <li>1.7.10 uses xCoord, yCoord, zCoord fields</li>
 * <li>1.12.2 uses markForUpdate() method</li>
 * <li>1.7.10 uses worldObj.markBlockForUpdate(x, y, z)</li>
 * </ul>
 * <p>
 * <b>Usage</b>:
 * 
 * <pre>
 * if (WorldUtils.isServer(world)) {
 *     // Server-side logic
 * }
 * BlockPos pos = WorldUtils.getPos(tileEntity);
 * WorldUtils.markForUpdate(tileEntity);
 * </pre>
 */
public class WorldUtils {

    /**
     * Check if a world is on the client side
     * <p>
     * 1.12.2 equivalent: world.isRemote
     *
     * @param world The world to check
     * @return true if on client, false if on server or world is null
     */
    public static boolean isClient(World world) {
        return world != null && world.isRemote;
    }

    /**
     * Check if a world is on the server side
     *
     * @param world The world to check
     * @return true if on server, false if on client or world is null
     */
    public static boolean isServer(World world) {
        return world != null && !world.isRemote;
    }

    /**
     * Check if a TileEntity is on the client side
     *
     * @param te The TileEntity to check
     * @return true if on client
     */
    public static boolean isClient(TileEntity te) {
        return te != null && te.getWorldObj() != null && te.getWorldObj().isRemote;
    }

    /**
     * Check if a TileEntity is on the server side
     *
     * @param te The TileEntity to check
     * @return true if on server
     */
    public static boolean isServer(TileEntity te) {
        return te != null && te.getWorldObj() != null && !te.getWorldObj().isRemote;
    }

    /**
     * Get a BlockPos from a TileEntity
     * <p>
     * 1.12.2 equivalent: tileEntity.getPos()
     *
     * @param te The TileEntity
     * @return A BlockPos representing the TileEntity's position
     */
    public static BlockPos getPos(TileEntity te) {
        if (te == null) {
            return null;
        }
        return new BlockPos(te.xCoord, te.yCoord, te.zCoord);
    }

    /**
     * Get a BlockPos from coordinates
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return A new BlockPos
     */
    public static BlockPos getPos(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    /**
     * Mark a TileEntity for update (sync to client)
     * <p>
     * 1.12.2 equivalent: tileEntity.markForUpdate()
     * <p>
     * This notifies the game that the TileEntity data has changed
     * and needs to be synchronized to the client
     *
     * @param te The TileEntity to mark for update
     */
    public static void markForUpdate(TileEntity te) {
        if (te != null && te.getWorldObj() != null) {
            te.getWorldObj()
                .markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
        }
    }

    /**
     * Mark a block position for update
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void markForUpdate(World world, int x, int y, int z) {
        if (world != null) {
            world.markBlockForUpdate(x, y, z);
        }
    }

    /**
     * Mark a BlockPos for update
     *
     * @param world The world
     * @param pos   The BlockPos
     */
    public static void markForUpdate(World world, BlockPos pos) {
        if (world != null && pos != null) {
            world.markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    /**
     * Mark a TileEntity as dirty (data changed)
     * <p>
     * 1.12.2 equivalent: tileEntity.markDirty()
     * <p>
     * This notifies the game that the TileEntity needs to be saved to disk
     *
     * @param te The TileEntity to mark dirty
     */
    public static void markDirty(TileEntity te) {
        if (te != null) {
            te.markDirty();
        }
    }

    /**
     * Check if a block position can see the sky
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if the position can see the sky
     */
    public static boolean canSeeSky(World world, int x, int y, int z) {
        if (world == null) {
            return false;
        }
        return world.canBlockSeeTheSky(x, y, z);
    }

    /**
     * Check if a BlockPos can see the sky
     *
     * @param world The world
     * @param pos   The BlockPos
     * @return true if the position can see the sky
     */
    public static boolean canSeeSky(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        return world.canBlockSeeTheSky(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Check if a TileEntity position can see the sky
     *
     * @param te The TileEntity
     * @return true if the position can see the sky
     */
    public static boolean canSeeSky(TileEntity te) {
        if (te == null || te.getWorldObj() == null) {
            return false;
        }
        return te.getWorldObj()
            .canBlockSeeTheSky(te.xCoord, te.yCoord, te.zCoord);
    }

    /**
     * Get the world time
     *
     * @param world The world
     * @return The world time in ticks
     */
    public static long getWorldTime(World world) {
        if (world == null) {
            return 0;
        }
        return world.getWorldTime();
    }

    /**
     * Get the world time from a TileEntity
     *
     * @param te The TileEntity
     * @return The world time in ticks
     */
    public static long getWorldTime(TileEntity te) {
        if (te == null || te.getWorldObj() == null) {
            return 0;
        }
        return te.getWorldObj()
            .getWorldTime();
    }

    /**
     * Check if it's currently night time
     *
     * @param world The world
     * @return true if it's night (time between 13000 and 23000)
     */
    public static boolean isNight(World world) {
        long time = getWorldTime(world);
        return time >= 13000L && time <= 23000L;
    }

    /**
     * Check if it's currently day time
     *
     * @param world The world
     * @return true if it's day (time between 0 and 13000 or 23000 and 24000)
     */
    public static boolean isDay(World world) {
        return !isNight(world);
    }

    /**
     * Get the current day/night cycle as a value between 0 and 1
     * <p>
     * 0 = midnight start (13000)
     * 0.5 = noon (6000)
     * 1 = midnight end (23000)
     *
     * @param world The world
     * @return A value between 0 and 1 representing the day cycle
     */
    public static float getDaytimeFactor(World world) {
        if (world == null) {
            return 0.5F;
        }
        long time = getWorldTime(world) % 24000L;

        // Night time (13000-23000) = 1.0 (full starlight collection)
        if (time >= 13000L && time <= 23000L) {
            return 1.0F;
        }

        // Day time = 0.2 (minimal starlight collection)
        return 0.2F;
    }

    /**
     * Check if a TileEntity is invalid (world is null or block is wrong)
     *
     * @param te The TileEntity to check
     * @return true if the TileEntity is invalid
     */
    public static boolean isInvalid(TileEntity te) {
        if (te == null || te.getWorldObj() == null) {
            return true;
        }
        return te.isInvalid();
    }

    /**
     * Safely get a TileEntity from the world
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return The TileEntity, or null if not found
     */
    public static TileEntity getTileEntity(World world, int x, int y, int z) {
        if (world == null) {
            return null;
        }
        return world.getTileEntity(x, y, z);
    }

    /**
     * Safely get a TileEntity from the world using BlockPos
     *
     * @param world The world
     * @param pos   The BlockPos
     * @return The TileEntity, or null if not found
     */
    public static TileEntity getTileEntity(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return null;
        }
        return world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Get the seed of a world
     *
     * @param world The world
     * @return The world seed, or 0 if world is null
     */
    public static long getSeed(World world) {
        if (world == null) {
            return 0L;
        }
        return world.getSeed();
    }

    /**
     * Get the dimension ID of a world
     *
     * @param world The world
     * @return The dimension ID, or 0 if world is null
     */
    public static int getDimension(World world) {
        if (world == null) {
            return 0;
        }
        return world.provider.dimensionId;
    }

    /**
     * Check if two positions are in the same dimension
     *
     * @param world1 First world
     * @param world2 Second world
     * @return true if both worlds are in the same dimension
     */
    public static boolean sameDimension(World world1, World world2) {
        if (world1 == null || world2 == null) {
            return false;
        }
        return getDimension(world1) == getDimension(world2);
    }

    /**
     * Play a sound at a position
     * <p>
     * Wrapper for world.playSoundEffect() with null checks
     *
     * @param world     The world
     * @param x         X coordinate
     * @param y         Y coordinate
     * @param z         Z coordinate
     * @param soundName The sound name (e.g., "ambient.weather.thunder")
     * @param volume    Volume (0.0 - 1.0)
     * @param pitch     Pitch (0.5 - 2.0)
     */
    public static void playSound(World world, double x, double y, double z, String soundName, float volume,
        float pitch) {
        if (world != null && !world.isRemote) {
            world.playSoundEffect(x, y, z, soundName, volume, pitch);
        }
    }

    /**
     * Play a sound at a TileEntity position
     *
     * @param te        The TileEntity
     * @param soundName The sound name
     * @param volume    Volume
     * @param pitch     Pitch
     */
    public static void playSound(TileEntity te, String soundName, float volume, float pitch) {
        if (te != null) {
            playSound(te.getWorldObj(), te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5, soundName, volume, pitch);
        }
    }
}
