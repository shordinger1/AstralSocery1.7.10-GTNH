/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * RayTraceResult alias for MovingObjectPosition
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.util.Vec3;

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
        this(mop.typeOfHit, mop.hitVec, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);
    }

    public RayTraceResult(MovingObjectType type, Vec3 vec, BlockPos pos) {
        this(type, vec, pos.getX(), pos.getY(), pos.getZ(), 0);
    }

    public RayTraceResult(MovingObjectType type, Vec3 vec, BlockPos pos, net.minecraft.entity.Entity entity) {
        this(type, vec, pos.getX(), pos.getY(), pos.getZ(), 0);
    }

    public RayTraceResult(MovingObjectType type, Vec3 vec, int x, int y, int z, int side) {
        super(type, vec);
        this.blockPos = new BlockPos(x, y, z);
        // 1.7.10 stores these in parent class fields
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.sideHit = side;
    }

    // Return EnumFacing instead of int for sideHit
    public net.minecraft.util.EnumFacing getSideHit() {
        return net.minecraft.util.EnumFacing.getFront(this.sideHit);
    }

    // For compatibility with code that accesses sideHit as EnumFacing
    public net.minecraft.util.EnumFacing sideHitEnum;
}
