/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.base.ASstarmapper;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRMapDrawingTable
 * Created by HellFirePvP
 * Date: 23.04.2017 / 22:39
 */
public class TESRMapDrawingTable extends TileEntitySpecialRenderer {

    private static final ASstarmapper modelDrawingTable = new ASstarmapper();
    private static final BindableResource texDrawingTable = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "starmapper/astralsorcery_starmapper");
    // 1.7.10: Create RenderItem instance directly
    private final RenderItem renderItem = new RenderItem();

    private static List<BlockPos> requiredGlasses = new LinkedList<>();

    public static void renderRemainingGlasses(float pTicks) {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();

        // RenderHelper.disableStandardItemLighting();
        // GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        Blending.DEFAULT.applyStateManager();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);

        texDrawingTable.bind();
        for (BlockPos p : requiredGlasses) {

            modelDrawingTable.treated_glass.render(1F);

        }

        requiredGlasses.clear();
        RenderHelper.disableStandardItemLighting();

        TextureHelper.refreshTextureBindState();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileMapDrawingTable)) return;
        TileMapDrawingTable tile = (TileMapDrawingTable) te;
        renderTileMapDrawingTable(tile, x, y, z, partialTicks);
    }

    private void renderTileMapDrawingTable(TileMapDrawingTable te, double x, double y, double z, float partialTicks) {
        Blending.DEFAULT.applyStateManager();

        if (!te.hasParchment() && !(te.getSlotIn() == null || te.getSlotIn().stackSize <= 0)) {
            ItemStack in = te.getSlotIn();
            RenderHelper.enableStandardItemLighting();

            // 1.7.10: Create EntityItem and render with RenderItem.doRender
            EntityItem entityItem = new EntityItem(te.getWorldObj(), 0, 0, 0, in);
            entityItem.hoverStart = 0;
            renderItem.doRender(entityItem, 0, 0, 0, 0, partialTicks);
            RenderHelper.disableStandardItemLighting();
        }

        RenderHelper.disableStandardItemLighting();

        RenderHelper.enableStandardItemLighting();

        texDrawingTable.bind();
        modelDrawingTable.render(null, te.hasParchment() ? 1 : 0, 0, 0, 0, 0, 1F);
        RenderHelper.disableStandardItemLighting();
        TextureHelper.refreshTextureBindState();

        if (te.getPercRunning() > 1E-4) {

            SpriteSheetResource halo = SpriteLibrary.spriteHalo2;
            halo.getResource()
                .bindTexture();
            Tuple<Double, Double> uvFrame = halo.getUVOffset(ClientScheduler.getClientTick());
            float rot = 360F - ((float) (ClientScheduler.getClientTick() % 2000) / 2000F * 360F);

            RenderingUtils.renderAngleRotatedTexturedRect(
                new Vector3(0.5, 1.01, 0.5).add(new Vector3(te.xCoord, te.yCoord, te.zCoord)),
                Vector3.RotAxis.Y_AXIS,
                Math.toRadians(rot),
                1F,
                uvFrame.key,
                uvFrame.value,
                halo.getULength(),
                halo.getVLength(),
                partialTicks);

        }

        if (!(te.getSlotGlassLens() == null || te.getSlotGlassLens().stackSize <= 0)) {
            requiredGlasses.add(new BlockPos(te.xCoord, te.yCoord, te.zCoord));
        }

        TextureHelper.refreshTextureBindState();
    }

}
