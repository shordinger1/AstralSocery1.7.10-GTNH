/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * WorldBlockPos - Block position with world reference
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;
import hellfirepvp.astralsorcery.common.util.math.Vec3d;

/**
 * WorldBlockPos - Block position with world reference (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Stores block position (x, y, z) with world reference</li>
 * <li>Convenience methods for block/tile access</li>
 * <li>Immutable position with offset operations</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Doesn't extend BlockPos (doesn't exist in 1.7.10)</li>
 * <li>Uses int x, y, z coordinates</li>
 * <li>IBlockState → Block + metadata</li>
 * <li>ChunkPos → ChunkCoordinates</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * WorldBlockPos pos = new WorldBlockPos(world, 10, 64, 10);
 * Block block = pos.getBlock();
 * int meta = pos.getMetadata();
 * Tile tile = pos.getTileAt(TileEntity.class, false);
 * </pre>
 */
public class WorldBlockPos {

    private final World world;
    private final int x;
    private final int y;
    private final int z;

    public WorldBlockPos(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WorldBlockPos(World world, BlockPos coords) {
        this(world, coords.getX(), coords.getY(), coords.getZ());
    }

    /**
     * Wrap TileEntity position
     */
    public static WorldBlockPos wrap(TileEntity te) {
        return new WorldBlockPos(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
    }

    /**
     * Wrap world and coordinates
     */
    public static WorldBlockPos wrap(World world, int x, int y, int z) {
        return new WorldBlockPos(world, x, y, z);
    }

    /**
     * Wrap BlockPos
     */
    public static WorldBlockPos wrap(World world, BlockPos coords) {
        return new WorldBlockPos(world, coords);
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * Get block at position
     */
    public Block getBlock() {
        return world.getBlock(x, y, z);
    }

    /**
     * Get block metadata at position
     */
    public int getMetadata() {
        return world.getBlockMetadata(x, y, z);
    }

    /**
     * Get TileEntity at position
     */
    public TileEntity getTile() {
        return world.getTileEntity(x, y, z);
    }

    /**
     * Get TileEntity at position if it matches class
     */
    public <T extends TileEntity> T getTileAt(Class<T> tileClass, boolean forceChunkLoad) {
        return MiscUtils.getTileAt(world, x, y, z, tileClass, forceChunkLoad);
    }

    /**
     * Check if chunk is loaded
     */
    public boolean isChunkLoaded() {
        return world.blockExists(x, y, z);
    }

    /**
     * Add offset to position
     */
    public WorldBlockPos add(int dx, int dy, int dz) {
        return new WorldBlockPos(world, x + dx, y + dy, z + dz);
    }

    /**
     * Subtract offset from position
     */
    public WorldBlockPos subtract(int dx, int dy, int dz) {
        return new WorldBlockPos(world, x - dx, y - dy, z - dz);
    }

    /**
     * Get distance to another position
     */
    public double distanceTo(WorldBlockPos other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Get distance squared (faster comparison)
     */
    public double distanceSqTo(WorldBlockPos other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldBlockPos that = (WorldBlockPos) o;
        return x == that.x && y == that.y
            && z == that.z
            && (world != null ? world.equals(that.world) : that.world == null);
    }

    @Override
    public int hashCode() {
        int result = world != null ? world.hashCode() : 0;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "WorldBlockPos{" + "world="
            + (world != null ? world.provider.dimensionId : "null")
            + ", x="
            + x
            + ", y="
            + y
            + ", z="
            + z
            + '}';
    }

    /**
     * Convert to BlockPos
     */
    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    /**
     * Convert to Vec3d
     */
    public Vec3d toVec3d() {
        return new Vec3d(x, y, z);
    }
}
