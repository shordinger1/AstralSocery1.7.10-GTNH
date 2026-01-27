/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import java.util.Collection;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObservableAreaBoundingBox
 * Created by HellFirePvP
 * Date: 02.12.2018 / 13:20
 */
public class ObservableAreaBoundingBox implements ObservableArea {

    private final AxisAlignedBB boundingBox;

    public ObservableAreaBoundingBox(Vec3i min, Vec3i max) {
        this(AxisAlignedBB.getBoundingBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
    }

    public ObservableAreaBoundingBox(BlockPos min, BlockPos max) {
        this(AxisAlignedBB.getBoundingBox(min.posX, min.posY, min.posZ, max.posX, max.posY, max.posZ));
    }

    public ObservableAreaBoundingBox(AxisAlignedBB boundingBox) {
        // 1.7.10: AxisAlignedBB doesn't have grow(), use constructor to create expanded box
        this.boundingBox = boundingBox;
    }

    @Override
    public Collection<ChunkPos> getAffectedChunks(BlockPos offset) {
        return calculateAffectedChunks(this.boundingBox, offset);
    }

    @Override
    public boolean observes(BlockPos pos) {
        // 1.7.10: Use Vec3.createVectorHelper() and isVecInside()
        return this.boundingBox.isVecInside(Vec3.createVectorHelper(pos.posX, pos.posY, pos.posZ));
    }

}
