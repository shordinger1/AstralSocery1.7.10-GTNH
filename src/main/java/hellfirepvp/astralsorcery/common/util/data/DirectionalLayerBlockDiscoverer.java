/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DirectionalLayerBlockDiscoverer
 * Created by HellFirePvP
 * Date: 01.11.2016 / 15:53
 */
public class DirectionalLayerBlockDiscoverer {

    private BlockPos start;
    private int rad, stepWidth;

    public DirectionalLayerBlockDiscoverer(BlockPos start, int discoverRadius, int stepWidth) {
        this.start = start;
        this.rad = discoverRadius;
        this.stepWidth = stepWidth;
    }

    public LinkedList<BlockPos> discoverApplicableBlocks() {
        LinkedList<BlockPos> visited = new LinkedList<>();

        int xPos = start.getX();
        int yPos = start.getY();
        int zPos = start.getZ();
        BlockPos currentPos = start;
        tryAdd(start, visited);

        EnumFacing dir = EnumFacing.NORTH;
        while (Math.abs(currentPos.getX() - xPos) <= rad && Math.abs(currentPos.getY() - yPos) <= rad
            && Math.abs(currentPos.getZ() - zPos) <= rad) {
            // 1.7.10: Convert EnumFacing to ForgeDirection for BlockPos.offset()
            ForgeDirection forgeDir = toForgeDirection(dir);
            currentPos = currentPos.offset(forgeDir, stepWidth);
            tryAdd(currentPos, visited);
            // 1.7.10: rotateY() doesn't exist, use manual rotation
            EnumFacing tryDirNext = rotateY(dir);
            ForgeDirection forgeTryDir = toForgeDirection(tryDirNext);
            if (!visited.contains(currentPos.offset(forgeTryDir, stepWidth))) {
                dir = tryDirNext;
            }
        }

        return visited;
    }

    // 1.7.10: Manual rotation since EnumFacing.rotateY() doesn't exist
    private EnumFacing rotateY(EnumFacing dir) {
        switch (dir) {
            case NORTH:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.SOUTH;
            case SOUTH:
                return EnumFacing.EAST;
            case EAST:
                return EnumFacing.NORTH;
            default:
                return dir;
        }
    }

    // 1.7.10: Convert EnumFacing to ForgeDirection
    private ForgeDirection toForgeDirection(EnumFacing dir) {
        return ForgeDirection.values()[dir.ordinal()];
    }

    private void tryAdd(BlockPos at, List<BlockPos> visited) {
        if (!visited.contains(at)) {
            visited.add(at);
        }
    }

}
