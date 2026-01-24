/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import java.awt.*;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.client.models.base.ASlens;
import hellfirepvp.astralsorcery.client.models.base.ASlens_color;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRLens
 * Created by HellFirePvP
 * Date: 20.09.2016 / 13:07
 */
public class TESRLens extends TileEntitySpecialRenderer {

    private static final ASlens modelLensPart = new ASlens();
    private static final BindableResource texLensPart = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "base/lens");

    private static final ASlens_color modelLensColoredFrame = new ASlens_color();
    private static final BindableResource texLensColorFrame = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "lens/lens_color");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileCrystalLens)) return;
        TileCrystalLens tile = (TileCrystalLens) te;
        renderTileLens(tile, x, y, z, partialTicks);
    }

    private void renderTileLens(TileCrystalLens te, double x, double y, double z, float partialTicks) {
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();

        // Let's just... not look at that stuff down there again and be happy it works alright? Thanks.
        List<BlockPos> linked = te.getLinkedPositions();
        float yaw = 0; // Degree
        float pitch = 0; // Degree
        switch (te.getPlacedAgainst()) {
            case DOWN:
                if (!(linked == null || linked.size() <= 0) && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = te.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    pitch = (float) Math
                        .atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

                    yaw = (float) Math.atan2(dir.getX(), dir.getZ());

                    yaw = 180F + (float) Math.toDegrees(-yaw);
                    pitch = (float) Math.toDegrees(pitch);
                }

                renderHandle(pitch);
                if (te.getLensColor() != null) {
                    Color c = te.getLensColor().wrappedColor;
                    renderColoredLens(-pitch);
                }
                break;
            case UP:
                if (!(linked == null || linked.size() <= 0) && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = te.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    pitch = (float) Math
                        .atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

                    yaw = (float) Math.atan2(dir.getX(), dir.getZ());

                    yaw = 180F + (float) Math.toDegrees(-yaw);
                    pitch = (float) Math.toDegrees(pitch);
                }

                renderHandle(-pitch);
                if (te.getLensColor() != null) {
                    Color c = te.getLensColor().wrappedColor;
                    renderColoredLens(pitch);
                }
                break;
            case NORTH:
                if (!(linked == null || linked.size() <= 0) && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = te.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    pitch = (float) Math
                        .atan2(dir.getZ(), Math.sqrt(dir.getX() * dir.getX() + dir.getY() * dir.getY()));

                    yaw = (float) Math.atan2(dir.getX(), dir.getY());

                    yaw = 180F + (float) Math.toDegrees(-yaw);
                    pitch = (float) Math.toDegrees(pitch);
                }

                renderHandle(pitch);
                if (te.getLensColor() != null) {
                    Color c = te.getLensColor().wrappedColor;
                    renderColoredLens(-pitch);
                }
                break;
            case SOUTH:
                if (!(linked == null || linked.size() <= 0) && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = te.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    pitch = (float) Math
                        .atan2(dir.getZ(), Math.sqrt(dir.getX() * dir.getX() + dir.getY() * dir.getY()));

                    yaw = (float) Math.atan2(dir.getX(), dir.getY());

                    yaw = 180F + (float) Math.toDegrees(-yaw);
                    pitch = (float) Math.toDegrees(pitch);
                }

                renderHandle(-pitch);
                if (te.getLensColor() != null) {
                    Color c = te.getLensColor().wrappedColor;
                    renderColoredLens(pitch);
                }
                break;
            case WEST:
                if (!(linked == null || linked.size() <= 0) && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = te.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    pitch = (float) Math
                        .atan2(dir.getX(), Math.sqrt(dir.getZ() * dir.getZ() + dir.getY() * dir.getY()));

                    yaw = (float) Math.atan2(dir.getZ(), dir.getY());

                    yaw = 180F + (float) Math.toDegrees(-yaw);
                    pitch = (float) Math.toDegrees(pitch);
                }

                renderHandle(pitch);
                if (te.getLensColor() != null) {
                    Color c = te.getLensColor().wrappedColor;
                    renderColoredLens(-pitch);
                }
                break;
            case EAST:
                if (!(linked == null || linked.size() <= 0) && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = te.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    pitch = (float) Math
                        .atan2(dir.getX(), Math.sqrt(dir.getZ() * dir.getZ() + dir.getY() * dir.getY()));

                    yaw = (float) Math.atan2(dir.getZ(), dir.getY());

                    yaw = 180F + (float) Math.toDegrees(-yaw);
                    pitch = (float) Math.toDegrees(pitch);
                }

                renderHandle(-pitch);
                if (te.getLensColor() != null) {
                    Color c = te.getLensColor().wrappedColor;
                    renderColoredLens(pitch);
                }
                break;
            default:
                break;
        }

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
    }

    private void renderColoredLens(float pitch) {
        modelLensColoredFrame.glass.rotateAngleX = pitch * 0.017453292F;
        modelLensColoredFrame.fitting1.rotateAngleX = pitch * 0.017453292F;
        modelLensColoredFrame.fitting2.rotateAngleX = pitch * 0.017453292F;
        modelLensColoredFrame.detail1_1.rotateAngleX = pitch * 0.017453292F;
        modelLensColoredFrame.detail1.rotateAngleX = pitch * 0.017453292F;

        texLensColorFrame.bind();
        modelLensColoredFrame.render(null, 0, 0, 0, 0, 0, 1);
    }

    private void renderHandle(float pitch) {
        RenderHelper.enableStandardItemLighting();

        texLensPart.bind();
        modelLensPart.lens.rotateAngleX = pitch * 0.017453292F;
        modelLensPart.render(null, 0, 0, 0, 0, 0, 1);
        RenderHelper.disableStandardItemLighting();
    }

}
