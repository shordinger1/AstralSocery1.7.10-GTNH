/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * DirectionalLayerBlockDiscoverer - Discovers blocks in a spiral pattern
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.LinkedList;
import java.util.List;

import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * DirectionalLayerBlockDiscoverer - Spiral block discovery (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Discovers blocks in a spiral pattern from start position</li>
 * <li>Used to find blocks in a radius around a point</li>
 * <li>Follows a directional layer pattern</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Uses BlockPos from util.math instead of ChunkCoordinates</li>
 * <li>EnumFacing → Direction/ForgeDirection</li>
 * <li>BlockPos.offset() → Manual coordinate calculation</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * DirectionalLayerBlockDiscoverer discoverer = new DirectionalLayerBlockDiscoverer(new BlockPos(0, 64, 0), 10, 2);
 * LinkedList&lt;BlockPos&gt; found = discoverer.discoverApplicableBlocks();
 * </pre>
 */
public class DirectionalLayerBlockDiscoverer {

    private final BlockPos start;
    private final int radius;
    private final int stepWidth;

    public DirectionalLayerBlockDiscoverer(BlockPos start, int discoverRadius, int stepWidth) {
        this.start = start;
        this.radius = discoverRadius;
        this.stepWidth = stepWidth;
    }

    /**
     * Discover all applicable blocks in a spiral pattern
     *
     * @return List of discovered block positions
     */
    public LinkedList<BlockPos> discoverApplicableBlocks() {
        LinkedList<BlockPos> visited = new LinkedList<>();

        int xPos = start.getX();
        int yPos = start.getY();
        int zPos = start.getZ();
        BlockPos currentPos = start;
        tryAdd(start, visited);

        int dir = 2; // Direction.NORTH (0=down, 1=up, 2=north, 3=south, 4=west, 5=east)
        while (Math.abs(currentPos.getX() - xPos) <= radius && Math.abs(currentPos.getY() - yPos) <= radius
            && Math.abs(currentPos.getZ() - zPos) <= radius) {
            currentPos = offset(currentPos, dir, stepWidth);
            tryAdd(currentPos, visited);
            int tryDirNext = rotateY(dir);
            if (!visited.contains(offset(currentPos, tryDirNext, stepWidth))) {
                dir = tryDirNext;
            }
        }

        return visited;
    }

    /**
     * Offset position in direction
     * 1.7.10: Manual offset calculation
     */
    private BlockPos offset(BlockPos pos, int direction, int distance) {
        int dx = 0, dy = 0, dz = 0;
        switch (direction) {
            case 0: // DOWN
                dy = -distance;
                break;
            case 1: // UP
                dy = distance;
                break;
            case 2: // NORTH
                dz = -distance;
                break;
            case 3: // SOUTH
                dz = distance;
                break;
            case 4: // WEST
                dx = -distance;
                break;
            case 5: // EAST
                dx = distance;
                break;
        }
        return new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
    }

    /**
     * Rotate direction clockwise (Y-axis rotation)
     * North(2) → East(5) → South(3) → West(4) → North(2)
     */
    private int rotateY(int direction) {
        switch (direction) {
            case 2:
                return 5; // NORTH → EAST
            case 5:
                return 3; // EAST → SOUTH
            case 3:
                return 4; // SOUTH → WEST
            case 4:
                return 2; // WEST → NORTH
            default:
                return direction; // UP/DOWN unchanged
        }
    }

    /**
     * Try to add position to visited list
     */
    private void tryAdd(BlockPos at, List<BlockPos> visited) {
        if (!visited.contains(at)) {
            visited.add(at);
        }
    }

}
