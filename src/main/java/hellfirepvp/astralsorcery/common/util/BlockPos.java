package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Compatibility class for BlockPos (introduced in Minecraft 1.8+).
 * Provides a mutable/immutable position holder for x, y, z coordinates.
 *
 * In 1.7.10, positions are typically handled as ChunkCoordinates or raw x,y,z.
 * This class bridges the API difference.
 */
public class BlockPos extends ChunkCoordinates {

    /**
     * Create a new BlockPos.
     */
    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * Create a BlockPos from ChunkCoordinates.
     */
    public BlockPos(ChunkCoordinates coords) {
        this(coords.posX, coords.posY, coords.posZ);
    }

    /**
     * Create a BlockPos from Entity position.
     */
    public BlockPos(Entity entity) {
        this((int) entity.posX, (int) entity.posY, (int) entity.posZ);
    }

    /**
     * Get X coordinate.
     */
    public int getX() {
        return this.posX;
    }

    /**
     * Get Y coordinate.
     */
    public int getY() {
        return this.posY;
    }

    /**
     * Get Z coordinate.
     */
    public int getZ() {
        return this.posZ;
    }

    /**
     * Add offset to this position and return a new BlockPos.
     */
    public BlockPos add(int x, int y, int z) {
        return new BlockPos(this.posX + x, this.posY + y, this.posZ + z);
    }

    /**
     * Add another BlockPos to this position and return a new BlockPos.
     */
    public BlockPos add(BlockPos other) {
        return new BlockPos(this.posX + other.posX, this.posY + other.posY, this.posZ + other.posZ);
    }

    /**
     * Subtract offset from this position and return a new BlockPos.
     */
    public BlockPos subtract(int x, int y, int z) {
        return new BlockPos(this.posX - x, this.posY - y, this.posZ - z);
    }

    /**
     * Subtract another BlockPos from this position and return a new BlockPos.
     */
    public BlockPos subtract(BlockPos other) {
        return new BlockPos(this.posX - other.posX, this.posY - other.posY, this.posZ - other.posZ);
    }

    /**
     * Add offset to this position and return a new BlockPos.
     */
    public BlockPos add(double x, double y, double z) {
        return new BlockPos(
            this.posX + (int) Math.floor(x),
            this.posY + (int) Math.floor(y),
            this.posZ + (int) Math.floor(z));
    }

    /**
     * Offset (add) by 1 in the Y direction.
     */
    public BlockPos up() {
        return up(1);
    }

    /**
     * Offset (add) in the Y direction.
     */
    public BlockPos up(int n) {
        return new BlockPos(this.posX, this.posY + n, this.posZ);
    }

    /**
     * Offset (add) by -1 in the Y direction.
     */
    public BlockPos down() {
        return down(1);
    }

    /**
     * Offset (add) in the negative Y direction.
     */
    public BlockPos down(int n) {
        return new BlockPos(this.posX, this.posY - n, this.posZ);
    }

    /**
     * Offset (add) in the X direction.
     */
    public BlockPos north() {
        return north(1);
    }

    /**
     * Offset (add) in the negative Z direction.
     */
    public BlockPos north(int n) {
        return new BlockPos(this.posX, this.posY, this.posZ - n);
    }

    /**
     * Offset (add) in the X direction.
     */
    public BlockPos south() {
        return south(1);
    }

    /**
     * Offset (add) in the Z direction.
     */
    public BlockPos south(int n) {
        return new BlockPos(this.posX, this.posY, this.posZ + n);
    }

    /**
     * Offset (add) in the negative X direction.
     */
    public BlockPos west() {
        return west(1);
    }

    /**
     * Offset (add) in the negative X direction.
     */
    public BlockPos west(int n) {
        return new BlockPos(this.posX - n, this.posY, this.posZ);
    }

    /**
     * Offset (add) in the X direction.
     */
    public BlockPos east() {
        return east(1);
    }

    /**
     * Offset (add) in the X direction.
     */
    public BlockPos east(int n) {
        return new BlockPos(this.posX + n, this.posY, this.posZ);
    }

    /**
     * Offset this position in the given direction by 1 block.
     */
    public BlockPos offset(ForgeDirection facing) {
        return offset(facing, 1);
    }

    /**
     * Offset this position in the given direction by n blocks.
     */
    public BlockPos offset(ForgeDirection facing, int n) {
        return new BlockPos(
            this.posX + facing.offsetX * n,
            this.posY + facing.offsetY * n,
            this.posZ + facing.offsetZ * n);
    }

    /**
     * Get the block at this position.
     */
    public net.minecraft.block.Block getBlock(World world) {
        return world.getBlock(this.posX, this.posY, this.posZ);
    }

    /**
     * Get the block metadata at this position.
     */
    public int getBlockMetadata(World world) {
        return world.getBlockMetadata(this.posX, this.posY, this.posZ);
    }

    /**
     * Set the block at this position.
     */
    public boolean setBlock(World world, net.minecraft.block.Block block) {
        return world.setBlock(this.posX, this.posY, this.posZ, block);
    }

    /**
     * Set the block with metadata at this position.
     */
    public boolean setBlock(World world, net.minecraft.block.Block block, int metadata) {
        return world.setBlock(this.posX, this.posY, this.posZ, block, metadata, 3);
    }

    /**
     * Convert to ChunkCoordinates.
     */
    public ChunkCoordinates toChunkCoordinates() {
        return new ChunkCoordinates(this.posX, this.posY, this.posZ);
    }

    /**
     * Get the TileEntity at this position.
     */
    public net.minecraft.tileentity.TileEntity getTileEntity(World world) {
        return world.getTileEntity(this.posX, this.posY, this.posZ);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockPos)) return false;
        BlockPos other = (BlockPos) obj;
        return this.posX == other.posX && this.posY == other.posY && this.posZ == other.posZ;
    }

    @Override
    public int hashCode() {
        return (this.posX * 31 + this.posZ) * 31 + this.posY;
    }

    @Override
    public String toString() {
        return "BlockPos{x=" + this.posX + ", y=" + this.posY + ", z=" + this.posZ + "}";
    }

    /**
     * ORIGIN constant for BlockPos(0, 0, 0).
     */
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    /**
     * Serialize this position to a long.
     * Format: ((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF)
     */
    public long toLong() {
        return ((long) this.posX & 0x3FFFFFF) << 38 | ((long) this.posY & 0xFFF) << 26 | (this.posZ & 0x3FFFFFF);
    }

    /**
     * Deserialize a BlockPos from a long.
     */
    public static BlockPos fromLong(long serialized) {
        int x = (int) (serialized >> 38);
        int y = (int) ((serialized << 26) >> 52);
        int z = (int) ((serialized << 38) >> 38);
        return new BlockPos(x, y, z);
    }
}
