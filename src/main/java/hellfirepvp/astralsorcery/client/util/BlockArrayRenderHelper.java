/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.util.*;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.migration.VertexFormat;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArrayRender
 * Created by HellFirePvP
 * Date: 30.09.2016 / 11:37
 */
public class BlockArrayRenderHelper {

    private BlockArray blocks;
    private WorldBlockArrayRenderAccess renderAccess;
    private double rotX, rotY, rotZ;

    public BlockArrayRenderHelper(BlockArray blocks) {
        this.blocks = blocks;
        this.renderAccess = new WorldBlockArrayRenderAccess(blocks);
        resetRotation();
    }

    private void resetRotation() {
        this.rotX = -30;
        this.rotY = 45;
        this.rotZ = 0;
    }

    public void rotate(double x, double y, double z) {
        this.rotX += x;
        this.rotY += y;
        this.rotZ += z;
    }

    public int getDefaultSlice() {
        return Collections.min(renderAccess.blockRenderData.keySet(), Comparator.comparing((BlockPos p) -> p.getY()))
            .getY();
    }

    public boolean hasSlice(int y) {
        return MiscUtils.contains(renderAccess.blockRenderData.keySet(), pos -> pos.getY() == y);
    }

    public void render3DGUI(double x, double y, float pTicks) {
        render3DSliceGUI(x, y, pTicks, null);
    }

    public void render3DSliceGUI(double x, double y, float pTicks, Integer slice) {
        GuiScreen scr = Minecraft.getMinecraft().currentScreen;
        if (scr == null) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        Minecraft mc = Minecraft.getMinecraft();
        double sc = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();

        double mul = 10.5;

        double size = 2;
        double minSize = 0.5;

        BlockPos max = blocks.getMax();
        BlockPos min = blocks.getMin();

        double maxLength = 0;
        double pointDst = max.getX() - min.getX();
        if (pointDst > maxLength) maxLength = pointDst;
        pointDst = max.getY() - min.getY();
        if (pointDst > maxLength) maxLength = pointDst;
        pointDst = max.getZ() - min.getZ();
        if (pointDst > maxLength) maxLength = pointDst;
        maxLength -= 5;

        if (maxLength > 0) {
            size = (size - minSize) * (1D - (maxLength / 20D));
        }

        double dr = -5.75 * size;

        VertexFormat blockFormat = VertexFormat.BLOCK;

        TextureHelper.setActiveTextureToAtlasSprite();
        Tessellator tes = Tessellator.instance;
        tes.startDrawing(GL11.GL_QUADS);

        Set<Map.Entry<BlockPos, BakedBlockData>> renderArray = renderAccess.blockRenderData.entrySet();
        renderAccess.slice = slice;
        for (Map.Entry<BlockPos, BakedBlockData> data : renderArray) {
            BlockPos offset = data.getKey();
            if (slice != null) {
                if (offset.getY() != slice) {
                    continue;
                }
            }
            BakedBlockData renderData = data.getValue();
            if (renderData.tileEntity != null) {
                // 1.7.10: TileEntity uses xCoord, yCoord, zCoord fields
                renderData.tileEntity.xCoord = offset.getX();
                renderData.tileEntity.yCoord = offset.getY();
                renderData.tileEntity.zCoord = offset.getZ();
            }
            if (renderData.type != Blocks.air) {
                RenderingUtils.renderBlockSafely(renderAccess, offset, renderData.state, tes);
            }
        }
        tes.draw();

        for (Map.Entry<BlockPos, BakedBlockData> data : renderArray) {
            BlockPos offset = data.getKey();
            if (slice != null) {
                if (offset.getY() != slice) {
                    continue;
                }
            }
            BakedBlockData renderData = data.getValue();
            if (renderData.tileEntity != null && renderData.tesr != null) {
                // 1.7.10: TileEntity uses xCoord, yCoord, zCoord fields
                renderData.tileEntity.xCoord = offset.getX();
                renderData.tileEntity.yCoord = offset.getY();
                renderData.tileEntity.zCoord = offset.getZ();
                // 1.7.10: Use renderTileEntityAt() instead of render()
                renderData.tesr
                    .renderTileEntityAt(renderData.tileEntity, offset.getX(), offset.getY(), offset.getZ(), pTicks);
            }
        }
        renderAccess.slice = null;

        GL11.glPopAttrib();
    }

    public static class BakedBlockData extends BlockArray.BlockInformation {

        private TileEntity tileEntity;
        private TileEntitySpecialRenderer tesr; // Raw type for 1.7.10 compatibility

        protected BakedBlockData(Block type, Block state, TileEntity te) {
            super(type, state);
            this.tileEntity = te;
            if (te != null) {
                tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(te);
            }
        }

    }

    public static class WorldBlockArrayRenderAccess implements IBlockAccess {

        private Map<BlockPos, BakedBlockData> blockRenderData = new HashMap<>();
        private Integer slice = null;

        public WorldBlockArrayRenderAccess(BlockArray array) {
            for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : array.getPattern()
                .entrySet()) {
                BlockPos offset = entry.getKey();
                BlockArray.BlockInformation info = entry.getValue();
                // 1.7.10: hasTileEntity takes int metadata, not Block state
                if (info.type.hasTileEntity(0)) {
                    // 1.7.10: createTileEntity takes (World, int metadata)
                    TileEntity te = info.type.createTileEntity(Minecraft.getMinecraft().theWorld, 0);
                    BlockArray.TileEntityCallback callback = array.getTileCallbacks()
                        .get(offset);
                    if (te != null && callback != null) {
                        if (callback.isApplicable(te)) {
                            callback.onPlace(this, offset, te);
                        }
                    }
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.state, te));
                } else {
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.state, null));
                }
            }
        }

        // 1.7.10 IBlockAccess methods (with x, y, z parameters)
        @Nullable
        public TileEntity getTileEntity(int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return isInBounds(pos) ? blockRenderData.get(pos).tileEntity : null;
        }

        @Override
        public int getBlockMetadata(int x, int y, int z) {
            return 0;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue) {
            return 0;
        }

        @SideOnly(Side.CLIENT)
        public int getCombinedLight(int x, int y, int z, int lightValue) {
            return 0;
        }

        @Override
        public Block getBlock(int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return isInBounds(pos) ? blockRenderData.get(pos).state : Blocks.air;
        }

        @Override
        public boolean isAirBlock(int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return !isInBounds(pos) || blockRenderData.get(pos).type == Blocks.air;
        }

        @Override
        public int isBlockProvidingPowerTo(int x, int y, int z, int direction) {
            return 0;
        }

        // Remove getStrongPower - not part of IBlockAccess interface

        @Override
        @SideOnly(Side.CLIENT)
        public BiomeGenBase getBiomeGenForCoords(int x, int z) {
            return BiomeGenBase.plains;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getHeight() {
            return 256; // Default world height in 1.7.10
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean extendedLevelsInChunkCache() {
            return false;
        }

        @Override
        public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
            BlockPos pos = new BlockPos(x, y, z);
            return isInBounds(pos) ? getBlock(x, y, z) != Blocks.air : _default;
        }

        // Helper methods for BlockPos access (compatibility with 1.12.2 code style)
        @Nullable
        public TileEntity getTileEntity(BlockPos pos) {
            return getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        }

        @SideOnly(Side.CLIENT)
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return getCombinedLight(pos.getX(), pos.getY(), pos.getZ(), lightValue);
        }

        public Block getBlockState(BlockPos pos) {
            return getBlock(pos.getX(), pos.getY(), pos.getZ());
        }

        public boolean isAirBlock(BlockPos pos) {
            return isAirBlock(pos.getX(), pos.getY(), pos.getZ());
        }

        @SideOnly(Side.CLIENT)
        public BiomeGenBase getBiome(BlockPos pos) {
            return BiomeGenBase.plains;
        }

        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
            // 1.7.10: Convert EnumFacing to ForgeDirection
            ForgeDirection fd = ForgeDirection.values()[side.ordinal()];
            return isSideSolid(pos.getX(), pos.getY(), pos.getZ(), fd, _default);
        }

        private boolean isInBounds(BlockPos pos) {
            return blockRenderData.containsKey(pos) && (slice == null || pos.getY() == slice);
        }

    }

}
