/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import java.awt.*;
import java.util.*;
import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderWorldBuffer;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileTranslucent;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRTranslucentBlock
 * Created by HellFirePvP
 * Date: 17.01.2017 / 03:57
 */
public class TESRTranslucentBlock extends TileEntitySpecialRenderer {

    private static final Map<Color, Collection<TranslucentBlockState>> blockEffects = new HashMap<>();
    private static final Map<Color, Integer> hashes = new HashMap<>();
    private static final Map<Color, Integer> batches = new HashMap<>();

    public static void renderTranslucentBlocks() {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(RenderingUtils.getCurrentRenderPartialTicks());
        GL11.glColor4f(1F, 1F, 1F, 1F);

        for (Map.Entry<Color, Collection<TranslucentBlockState>> setBlocks : blockEffects.entrySet()) {
            Color overlay = setBlocks.getKey();
            GL11.glColor4f(1F, 1F, 1F, overlay.getAlpha() / 255F);
            int batchList = batches.getOrDefault(setBlocks.getKey(), -1);
            Integer nullableHash = hashes.get(setBlocks.getKey());
            if (batchList == -1) {
                batches.put(overlay, batch(setBlocks.getValue(), overlay.getRGB()));
                hashes.put(overlay, hashBlocks(setBlocks.getValue()));
                setBlocks.getValue()
                    .clear();
            } else if (nullableHash == null) {
                batches.put(overlay, batch(setBlocks.getValue(), overlay.getRGB()));
                hashes.put(overlay, hashBlocks(setBlocks.getValue()));
                setBlocks.getValue()
                    .clear();
            } else {
                int newHash = hashBlocks(setBlocks.getValue());
                if (newHash != nullableHash) {
                    batches.put(overlay, batch(setBlocks.getValue(), overlay.getRGB()));
                    hashes.put(overlay, newHash);
                    setBlocks.getValue()
                        .clear();
                }
            }
        }

        GL11.glEnable(GL11.GL_BLEND);
        Blending.CONSTANT_ALPHA.applyStateManager();
        Blending.CONSTANT_ALPHA.apply();
        // Sync Statemanager
        GL11.glColor4f(1F, 1F, 1F, 1F);

        for (Color colorKey : blockEffects.keySet()) {
            Integer batch;
            if ((batch = batches.get(colorKey)) != null) {}
            blockEffects.getOrDefault(colorKey, new ArrayList<>(0))
                .clear();
        }

        Blending.DEFAULT.applyStateManager();
        // Drawing color-overlay'd blocks leaks color states into native GL context but doesn't apply them
        // to minecraft's GL wrapper. updating this manually.
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private static int batch(Collection<TranslucentBlockState> set, int color) {
        RenderWorldBuffer iba = new RenderWorldBuffer(
            BiomeGenBase.plains,
            new BlockArray());
        iba.appendAll(MiscUtils.splitMap(set, entry -> new Tuple<>(entry.pos, entry.state)));
        int batchDList = GLAllocation.generateDisplayLists(1);
        Blending.CONSTANT_ALPHA.applyStateManager();
        Tessellator tes = Tessellator.instance;
        tes.startDrawing(GL11.GL_QUADS);
        for (TranslucentBlockState tbs : set) {
            RenderingUtils.renderBlockSafelyWithOptionalColor(iba, tbs.pos, tbs.state, tes, color);
        }
        tes.draw();
        Blending.DEFAULT.applyStateManager();
        return batchDList;
    }

    private static int hashBlocks(Collection<TranslucentBlockState> set) {
        int hash = 80238287;
        for (TranslucentBlockState tbs : set) {
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getX() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getY() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.pos.getZ() * 5449 % 130651);
            hash = (hash << 4) ^ (hash >> 28) ^ (tbs.state.hashCode() * 5449 % 130651);
        }
        return hash % 75327403;
    }

    public static void cleanUp() {
        for (Integer batchList : batches.values()) {}
        batches.clear();
        hashes.clear();
        blockEffects.clear();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileTranslucent)) return;
        TileTranslucent tile = (TileTranslucent) te;
        renderTileTranslucentBlock(tile, x, y, z, partialTicks);
    }

    private void renderTileTranslucentBlock(TileTranslucent te, double x, double y, double z, float partialTicks) {
        if (te.getFakedState() == null) return;
        Block renderState = te.getFakedState();
        if (x * x + y * y + z * z >= 64 * 64) return;
        addForRender(null, renderState, new BlockPos(te.xCoord, te.yCoord, te.zCoord));
    }

    public static void addForRender(@Nullable Color overlay, Block state, BlockPos pos) {
        Color key = overlay == null ? Color.WHITE : overlay;
        if (!blockEffects.containsKey(key)) {
            blockEffects.put(key, new LinkedList<>());
        }
        blockEffects.get(key)
            .add(new TranslucentBlockState(state, pos));
    }

    public static class TranslucentBlockState {

        public final Block state;
        public final BlockPos pos;

        public TranslucentBlockState(Block block, BlockPos pos) {
            this.state = block;
            this.pos = pos;
        }

    }

}
