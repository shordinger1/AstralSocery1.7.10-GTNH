/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * VerticalConeBlockDiscoverer - Discovers blocks in a vertical cone shape
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.MathHelper;

import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * VerticalConeBlockDiscoverer - Vertical cone block discovery (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Discovers blocks in a vertical cone shape from offset point</li>
 * <li>Cone radius decreases as you go down</li>
 * <li>Used to find blocks in a cone-shaped area</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>ChunkCoordinates → BlockPos from util.math</li>
 * <li>Vector3.toBlockPos() for conversion</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * VerticalConeBlockDiscoverer discoverer = new VerticalConeBlockDiscoverer(new BlockPos(0, 64, 0));
 * List&lt;BlockPos&gt; blocks = discoverer.tryDiscoverBlocksDown(10, 5);
 * </pre>
 */
public class VerticalConeBlockDiscoverer {

    private final BlockPos offset;

    public VerticalConeBlockDiscoverer(BlockPos offset) {
        this.offset = offset;
    }

    /**
     * Discover blocks in a vertical cone downwards
     *
     * @param lengthDown Length of the cone
     * @param flatRadius Maximum radius at the top
     * @return List of discovered block positions
     */
    public List<BlockPos> tryDiscoverBlocksDown(float lengthDown, float flatRadius) {
        List<BlockPos> out = new LinkedList<>();

        int lX = MathHelper.floor_float(offset.getX() - flatRadius);
        int hX = MathHelper.ceiling_float_int(offset.getX() + flatRadius);
        int lZ = MathHelper.floor_float(offset.getZ() - flatRadius);
        int hZ = MathHelper.ceiling_float_int(offset.getZ() + flatRadius);

        Vector3 center = new Vector3(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5);
        for (int yy = offset.getY(); yy >= Math.max(0, offset.getY() - lengthDown); yy--) {
            for (int xx = lX; xx <= hX; xx++) {
                for (int zz = lZ; zz <= hZ; zz++) {
                    Vector3 at = new Vector3(xx + 0.5, yy + 0.5, zz + 0.5);
                    float perc = 1F - (float) ((offset.getY() - at.getY()) / lengthDown);
                    float dstAllowed = flatRadius * perc;
                    double dX = center.getX() - at.getX();
                    double dZ = center.getZ() - at.getZ();
                    double dstCur = Math.sqrt(dX * dX + dZ * dZ);
                    if (dstCur <= dstAllowed) {
                        out.add(at.toBlockPos());
                    }
                }
            }
        }

        return out;
    }

}
