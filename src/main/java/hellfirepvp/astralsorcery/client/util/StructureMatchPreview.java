/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.awt.*;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.tile.IMultiblockDependantTile;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatchPreview
 * Created by HellFirePvP
 * Date: 03.11.2017 / 09:42
 */
public class StructureMatchPreview {

    private final IMultiblockDependantTile tile;
    private int timeout;

    public StructureMatchPreview(IMultiblockDependantTile tile) {
        this.tile = tile;
        this.timeout = 100;
    }

    public void tick() {
        PatternBlockArray pattern = tile.getRequiredStructure();
        if (pattern != null && Minecraft.getMinecraft().thePlayer != null) {
            BlockPos at = tile.getLocationPos();
            BlockPos v = pattern.getSize();
            int maxDim = Math.max(Math.max(v.getX(), v.getY()), v.getZ());
            maxDim = Math.max(9, maxDim);
            if (Minecraft.getMinecraft().thePlayer.getDistance(at.getX(), at.getY(), at.getZ()) <= maxDim) {
                resetTimeout();
                return;
            }
        }
        timeout--;
    }

    public void resetTimeout() {
        this.timeout = 300;
    }

    @Nullable
    public Integer getPreviewSlice() {
        PatternBlockArray pattern = tile.getRequiredStructure();
        World world = Minecraft.getMinecraft().theWorld;
        if (pattern == null || world == null) {
            return null;
        }
        int minY = pattern.getMin()
            .getY();
        for (int y = minY; y <= pattern.getMax()
            .getY(); y++) {
            if (!pattern.matchesSlice(world, tile.getLocationPos(), y)) {
                return y;
            }
        }
        return null;
    }

    public boolean shouldBeRemoved() {
        return timeout <= 0 || tile.getRequiredStructure() == null
            || Minecraft.getMinecraft().theWorld == null
            || Minecraft.getMinecraft().theWorld.provider.dimensionId
                != ((TileEntity) tile).getWorldObj().provider.dimensionId
            || tile.getRequiredStructure()
                .matches(
                    Minecraft.getMinecraft().theWorld,
                    new BlockPos(((TileEntity) tile).xCoord, ((TileEntity) tile).yCoord, ((TileEntity) tile).zCoord))
            || ((TileEntity) tile).isInvalid();
    }

    public boolean isOriginatingFrom(IMultiblockDependantTile tile) {
        if (!(tile instanceof TileEntity)) return false;
        if (shouldBeRemoved()) return false;
        TileEntity thisTe = (TileEntity) this.tile;
        TileEntity otherTe = (TileEntity) tile;
        return new BlockPos(thisTe.xCoord, thisTe.yCoord, thisTe.zCoord)
            .equals(new BlockPos(otherTe.xCoord, otherTe.yCoord, otherTe.zCoord));
    }

    public void renderPreview(float partialTicks) {
        PatternBlockArray pba = tile.getRequiredStructure();
        World world = Minecraft.getMinecraft().theWorld;
        Integer slice = getPreviewSlice();
        if (shouldBeRemoved() || pba == null || slice == null || world == null) {
            return;
        }

        BlockPos center = tile.getLocationPos();

        IBlockAccess airWorld = new AirBlockRenderWorld(BiomeGenBase.plains);
        Tessellator tes = Tessellator.instance;

        TextureHelper.setActiveTextureToAtlasSprite();
        Blending.CONSTANT_ALPHA.applyStateManager();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);

        for (Map.Entry<BlockPos, BlockArray.BlockInformation> patternEntry : pba.getPatternSlice(slice)
            .entrySet()) {
            BlockPos offset = patternEntry.getKey();
            BlockArray.BlockInformation info = patternEntry.getValue();

            if (offset.equals(BlockPos.ORIGIN) || pba.matchSingleBlock(world, center, offset)) {
                continue;
            }

            // 1.7.10: BlockPos doesn't have add() method, add coordinates manually
            BlockPos targetPos = new BlockPos(
                center.getX() + offset.getX(),
                center.getY() + offset.getY(),
                center.getZ() + offset.getZ());
            Block state = world.getBlock(targetPos.getX(), targetPos.getY(), targetPos.getZ());

            tes.startDrawing(GL11.GL_QUADS);

            // 1.7.10: Block.isAir() signature is different - use Block.isAir(IBlockAccess, int, int, int)
            if (state.isAir(world, targetPos.getX(), targetPos.getY(), targetPos.getZ())) {
                RenderingUtils.renderBlockSafely(airWorld, BlockPos.ORIGIN, info.state, tes);
            } else {
                RenderingUtils.renderBlockSafelyWithOptionalColor(airWorld, BlockPos.ORIGIN, info.state, tes, 16711680);
            }

            tes.draw();
        }

        Blending.DEFAULT.applyStateManager();
        TextureHelper.refreshTextureBindState();
    }

}
