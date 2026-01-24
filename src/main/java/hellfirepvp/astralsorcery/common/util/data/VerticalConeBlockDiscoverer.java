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

import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VerticalConeBlockDiscoverer
 * Created by HellFirePvP
 * Date: 03.11.2017 / 23:41
 */
public class VerticalConeBlockDiscoverer {

    private final BlockPos offset;

    public VerticalConeBlockDiscoverer(BlockPos offset) {
        this.offset = offset;
    }

    public List<BlockPos> tryDiscoverBlocksDown(float lengthDown, float flatRadius) {
        List<BlockPos> out = new LinkedList<>();

        int lX = WrapMathHelper.floor(offset.getX() - flatRadius);
        int hX = WrapMathHelper.ceil(offset.getX() + flatRadius);
        int lZ = WrapMathHelper.floor(offset.getZ() - flatRadius);
        int hZ = WrapMathHelper.ceil(offset.getZ() + flatRadius);

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
                        out.add(new BlockPos(at.toBlockPos()));
                    }
                }
            }
        }

        return out;
    }

}
