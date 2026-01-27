/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.util.BitSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.migration.BakedQuad;
import hellfirepvp.astralsorcery.common.migration.BlockColors;
import hellfirepvp.astralsorcery.common.migration.BlockFluidRenderer;
import hellfirepvp.astralsorcery.common.migration.IBakedModel;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockModelRenderHelper
 * Created by HellFirePvP
 * Date: 21.06.2018 / 19:52
 */
public class BlockModelRenderHelper {

    private static BlockFluidRenderer bfr;

    private static BlockFluidRenderer getFluidRenderer() {
        if (bfr == null) {
            // 1.7.10: BlockFluidRenderer constructor doesn't take parameters
            bfr = new BlockFluidRenderer();
        }
        return bfr;
    }

    public static void renderBlockModelWithColor(IBlockAccess world, BlockPos pos, Block state, Object vb, int color) {
        // Not implemented for 1.7.10 - requires WorldRenderer which doesn't exist
        // This method is 1.12.2 specific
    }

    private static void renderModelFlat(IBlockAccess worldIn, IBakedModel modelIn, Block stateIn, BlockPos posIn,
        boolean checkSides, long rand, int color) {
        // Not implemented for 1.7.10 - requires WorldRenderer which doesn't exist
    }

    private static void renderQuadsFlat(IBlockAccess blockAccessIn, Block stateIn, BlockPos posIn, int brightnessIn,
        boolean ownBrightness, List<BakedQuad> list, BitSet bitSet, int color) {
        // Not implemented for 1.7.10 - requires WorldRenderer which doesn't exist
    }

    private static void fillQuadBounds(Block stateIn, int[] vertexData, EnumFacing face, @Nullable float[] quadBounds,
        BitSet boundsFlags) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        for (int i = 0; i < 4; ++i) {
            float f6 = Float.intBitsToFloat(vertexData[i * 7]);
            float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
            float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null) {
            // 1.7.10: use ordinal() instead of getIndex()
            quadBounds[EnumFacing.WEST.ordinal()] = f;
            quadBounds[EnumFacing.EAST.ordinal()] = f3;
            quadBounds[EnumFacing.DOWN.ordinal()] = f1;
            quadBounds[EnumFacing.UP.ordinal()] = f4;
            quadBounds[EnumFacing.NORTH.ordinal()] = f2;
            quadBounds[EnumFacing.SOUTH.ordinal()] = f5;
            int j = EnumFacing.values().length;
            quadBounds[EnumFacing.WEST.ordinal() + j] = 1.0F - f;
            quadBounds[EnumFacing.EAST.ordinal() + j] = 1.0F - f3;
            quadBounds[EnumFacing.DOWN.ordinal() + j] = 1.0F - f1;
            quadBounds[EnumFacing.UP.ordinal() + j] = 1.0F - f4;
            quadBounds[EnumFacing.NORTH.ordinal() + j] = 1.0F - f2;
            quadBounds[EnumFacing.SOUTH.ordinal() + j] = 1.0F - f5;
        }

        switch (face) {
            case DOWN:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f1 < 1.0E-4F || isFullCube(stateIn)) && f1 == f4);
                break;
            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f4 > 0.9999F || isFullCube(stateIn)) && f1 == f4);
                break;
            case NORTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f2 < 1.0E-4F || isFullCube(stateIn)) && f2 == f5);
                break;
            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f5 > 0.9999F || isFullCube(stateIn)) && f2 == f5);
                break;
            case WEST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f < 1.0E-4F || isFullCube(stateIn)) && f == f3);
                break;
            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f3 > 0.9999F || isFullCube(stateIn)) && f == f3);
            default:
                break;
        }
    }

    // 1.7.10: Helper method since Block.isFullCube() doesn't exist
    private static boolean isFullCube(Block block) {
        return block.isOpaqueCube();
    }

    private static class BlockColorsOverride extends BlockColors {

        private static int override = -1;
        private final BlockColors prev;

        public BlockColorsOverride(BlockColors prev) {
            this.prev = prev;
        }

        @Override
        public int colorMultiplier(Block block, int metadata, @Nullable IBlockAccess blockAccess, @Nullable BlockPos pos,
            int tintIndex) {
            if (override != -1) {
                return override;
            }
            return prev.colorMultiplier(block, metadata, blockAccess, pos, tintIndex);
        }
    }

}
