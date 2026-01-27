/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * RayTraceResult alias for MovingObjectPosition
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.util.Vec3;
import net.minecraft.entity.Entity;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.util.math.RayTraceResult
 * In 1.7.10: net.minecraft.util.MovingObjectPosition
 */
public class RayTraceResult extends net.minecraft.util.MovingObjectPosition {

    // Compatibility enum for Type
    public static class Type {

        public static final MovingObjectType BLOCK = MovingObjectType.BLOCK;
        public static final MovingObjectType ENTITY = MovingObjectType.ENTITY;
        public static final MovingObjectType MISS = MovingObjectType.MISS;
    }

    // Additional field for BlockPos compatibility
    public BlockPos blockPos;

    public RayTraceResult(net.minecraft.util.MovingObjectPosition mop) {
        // In 1.7.10, constructor signature is (int, int, int, int, Vec3)
        super(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, mop.hitVec);
        this.blockPos = new BlockPos(mop.blockX, mop.blockY, mop.blockZ);
    }

    public RayTraceResult(MovingObjectType type, Vec3 vec, BlockPos pos) {
        this(type, vec, pos.getX(), pos.getY(), pos.getZ(), 0);
    }

    public RayTraceResult(MovingObjectType type, Vec3 vec, BlockPos pos, Entity entity) {
        // For entity hits, call the entity constructor
        super(entity);
        this.blockPos = pos;
    }

    public RayTraceResult(MovingObjectType type, Vec3 vec, int x, int y, int z, int side) {
        // In 1.7.10, constructor signature is (int, int, int, int, Vec3)
        super(x, y, z, side, vec);
        this.blockPos = new BlockPos(x, y, z);
    }

    // Return EnumFacing instead of int for sideHit
    public net.minecraft.util.EnumFacing getSideHit() {
        return net.minecraft.util.EnumFacing.getFront(this.sideHit);
    }

    // For compatibility with code that accesses sideHit as EnumFacing
    public net.minecraft.util.EnumFacing sideHitEnum;
}
