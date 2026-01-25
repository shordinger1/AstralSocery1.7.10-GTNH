/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.gui.base.GuiTileBase;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.starmap.ActiveStarMap;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.ItemInfusedGlass;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktBurnParchment;
import hellfirepvp.astralsorcery.common.network.packet.client.PktEngraveGlass;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiMapDrawing
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:29
 */
public class GuiMapDrawing extends GuiTileBase<TileMapDrawingTable> {

    public static final BindableResource texMapDrawing = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing");
    public static final BindableResource texMapDrawingEmpty = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing_empty");

    private static final Rectangle rctDrawingGrid = new Rectangle(
        68 + DrawnConstellation.CONSTELLATION_DRAW_SIZE,
        45 + DrawnConstellation.CONSTELLATION_DRAW_SIZE,
        120 - (DrawnConstellation.CONSTELLATION_DRAW_SIZE * 2),
        120 - (DrawnConstellation.CONSTELLATION_DRAW_SIZE * 2));
    private Map<Rectangle, IConstellation> mapRenderedConstellations = new HashMap<>();

    private List<DrawnConstellation> drawnConstellations = new LinkedList<>();

    private IConstellation dragging = null;
    private int dragRequested = 0;

    public GuiMapDrawing(TileMapDrawingTable table) {
        super(table, 188, 256);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        TileMapDrawingTable tile = getOwningTileEntity();
        if (!tile.hasParchment()) {
            drawWHRect(texMapDrawingEmpty);
        } else {
            drawWHRect(texMapDrawing);
        }

        mapRenderedConstellations.clear();

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();

        List<String> tooltip = null;
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        boolean hasLens = false;
        if (itemRender != null) {
            float prev = zLevel;
            float itemPrev = itemRender.zLevel;
            zLevel += 100;
            itemRender.zLevel += 100;

            ItemStack in = tile.getSlotIn();
            if (!(in == null || in.stackSize <= 0)) {
                Rectangle rc = new Rectangle(guiLeft + 111, guiTop + 8, 16, 16);
                // 1.7.10: renderItemAndEffectIntoGUI needs FontRenderer and renderEngine parameters
                itemRender.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, mc.renderEngine, in, 0, 0);
                // 1.7.10: renderItemOverlayIntoGUI needs 5 parameters
                itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, mc.renderEngine, in, 0, 0);
                if (rc.contains(mouseX, mouseY)) {
                    FontRenderer custom = in.getItem()
                        .getFontRenderer(in);
                    if (custom != null) {
                        fr = custom;
                    }
                    tooltip = in.getTooltip(
                        Minecraft.getMinecraft().thePlayer,
                        Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
                }
            }
            in = tile.getSlotGlassLens();
            if (!(in == null || in.stackSize <= 0)) {
                if (in.getItem() instanceof ItemInfusedGlass) {
                    hasLens = true;
                }

                Rectangle rc = new Rectangle(guiLeft + 129, guiTop + 8, 16, 16);
                // 1.7.10: renderItemAndEffectIntoGUI needs FontRenderer and renderEngine parameters
                itemRender.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, mc.renderEngine, in, 0, 0);
                // 1.7.10: renderItemOverlayIntoGUI needs 5 parameters
                itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, mc.renderEngine, in, 0, 0);
                if (rc.contains(mouseX, mouseY)) {
                    FontRenderer custom = in.getItem()
                        .getFontRenderer(in);
                    if (custom != null) {
                        fr = custom;
                    }
                    tooltip = in.getTooltip(
                        Minecraft.getMinecraft().thePlayer,
                        Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
                }
            }

            zLevel = prev;
            itemRender.zLevel = itemPrev;
        }

        RenderConstellation.BrightnessFunction f = new RenderConstellation.BrightnessFunction() {

            @Override
            public float getBrightness() {
                return ConstellationSkyHandler.getInstance()
                    .getCurrentDaytimeDistribution(Minecraft.getMinecraft().theWorld);
            }
        };
        if (hasLens) {
            WorldSkyHandler wsh = ConstellationSkyHandler.getInstance()
                .getWorldHandler(tile.getWorldObj());
            if (wsh != null && tile.doesSeeSky()) {

                if (f.getBrightness() > 1E-4) {
                    DataActiveCelestials dac = SyncDataHolder.getDataClient(SyncDataHolder.DATA_CONSTELLATIONS);
                    Collection<IConstellation> cst = dac
                        .getActiveConstellations(Minecraft.getMinecraft().theWorld.provider.dimensionId);

                    if (cst != null) {
                        // 1.7.10: Replace stream operations with traditional loop
                        List<IConstellation> filtered = Lists.newArrayList();
                        for (IConstellation c : cst) {
                            if (ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                                filtered.add(c);
                            }
                        }

                        for (int i = 0; i < Math.min(filtered.size(), 12); i++) {
                            IConstellation c = filtered.get(i);
                            int offsetX = i % 2 == 0 ? 8 : 232;
                            int offsetY = 40 + (i / 2) * 23;

                            mapRenderedConstellations.put(new Rectangle(offsetX, offsetY, 16, 16), c);

                            drawConstellation(c, new Point(offsetX, offsetY), f);
                        }
                    }
                }
            }

            ActiveStarMap map = ItemInfusedGlass.getMapEngravingInformations(tile.getSlotGlassLens());
            if (map != null && tile.doesSeeSky()) {
                RenderConstellation.BrightnessFunction dim = new RenderConstellation.BrightnessFunction() {

                    @Override
                    public float getBrightness() {
                        return f.getBrightness() * 0.8F;
                    }
                };
                Map<IConstellation, List<Point>> constellationMap = map.getMapOffsets();
                for (IConstellation c : constellationMap.keySet()) {
                    for (Point p : constellationMap.get(c)) {
                        int whDrawn = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
                        Point offset = new Point(p.x, p.y);
                        offset.translate(guiLeft, guiTop);
                        offset.translate(rctDrawingGrid.x, rctDrawingGrid.y);
                        offset.translate(-whDrawn, -whDrawn);

                        RenderConstellation.renderConstellationIntoGUI(
                            c,
                            offset.x,
                            offset.y,
                            zLevel,
                            whDrawn * 2,
                            whDrawn * 2,
                            1.6F,
                            dim,
                            true,
                            false);
                    }
                }
            }
        }

        if (tile.getPercRunning() > 1E-4) {
            SpriteSheetResource halo = SpriteLibrary.spriteHalo2;
            halo.getResource()
                .bindTexture();
            Tuple<Double, Double> uvFrame = halo.getUVOffset(ClientScheduler.getClientTick());

            float rot = ((float) (ClientScheduler.getClientTick() % 2000) / 2000F * 360F);

            float scale = 160F;

            Blending.DEFAULT.applyStateManager();

            drawTexturedRectAtCurrentPos(
                scale,
                scale,
                (float) (double) uvFrame.key,
                (float) (double) uvFrame.value, // Jeeez. Double -> float is not a thing.
                (float) halo.getULength(),
                (float) halo.getVLength());

            TextureHelper.refreshTextureBindState();
        }

        // 1.7.10: ItemStack == null || ItemStack.stackSize <= 0 doesn't exist, check stackSize
        ItemStack slotIn = tile.getSlotIn();
        if (slotIn != null && slotIn.stackSize > 0 && !tile.hasParchment() && itemRender != null) {
            TextureHelper.refreshTextureBindState();
            TextureHelper.setActiveTextureToAtlasSprite();
            float prev = zLevel;
            float itemPrev = itemRender.zLevel;
            zLevel += 100;
            itemRender.zLevel += 100;

            ItemStack in = tile.getSlotIn();

            // 1.7.10: renderItemAndEffectIntoGUI needs FontRenderer and renderEngine parameters
            itemRender.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, mc.renderEngine, in, 0, 0);
            // 1.7.10: renderItemOverlayIntoGUI needs 5 parameters
            itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, mc.renderEngine, in, 0, 0);

            zLevel = prev;
            itemRender.zLevel = itemPrev;
        }

        if (f.getBrightness() <= 1E-4 || !tile.hasParchment()) {
            drawnConstellations.clear();
            dragging = null;
            dragRequested = 0;
        }

        for (DrawnConstellation cst : drawnConstellations) {
            int whDrawn = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
            Point offset = new Point(cst.point.x, cst.point.y);
            offset.translate(guiLeft, guiTop);
            offset.translate(-whDrawn, -whDrawn);

            RenderConstellation.renderConstellationIntoGUI(
                cst.constellation,
                offset.x,
                offset.y,
                zLevel,
                whDrawn * 2,
                whDrawn * 2,
                1.6F,
                f,
                true,
                false);
        }

        if (dragging != null) {
            int whDragging = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
            Point offset = new Point(mouseX, mouseY);
            offset.translate(-whDragging, -whDragging);

            RenderConstellation.renderConstellationIntoGUI(
                dragging,
                offset.x,
                offset.y,
                zLevel,
                whDragging * 2,
                whDragging * 2,
                1.6F,
                f,
                true,
                false);

            if (ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(Minecraft.getMinecraft().theWorld) <= 1E-4) {
                dragging = null;
            }
        }

        for (Rectangle r : mapRenderedConstellations.keySet()) {
            if (r.contains(mouseX - guiLeft, mouseY - guiTop)) {
                tooltip = Lists.newArrayList(
                    I18n.format(
                        mapRenderedConstellations.get(r)
                            .getUnlocalizedName()));
            }
        }

        if (tooltip != null) {
            RenderingUtils.renderBlueTooltip(mouseX, mouseY, tooltip, fr);
        }
        TextureHelper.refreshTextureBindState();
    }

    private void drawConstellation(IConstellation c, Point p, RenderConstellation.BrightnessFunction fct) {
        RenderConstellation.renderConstellationIntoGUI(
            Color.WHITE,
            c,
            guiLeft + p.x,
            guiTop + p.y,
            zLevel,
            16,
            16,
            0.5,
            fct,
            true,
            false);
    }

    @Nullable
    private Point translatePointToGrid(Point mouse) {
        mouse = new Point(mouse.x, mouse.y);
        mouse.translate(-guiLeft, -guiTop);
        if (rctDrawingGrid.contains(mouse)) {
            return mouse;
        }
        return null;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (dragRequested > 0) {
            if (!Mouse.isButtonDown(0)) {
                dragRequested--;
                if (dragRequested <= 0) {
                    dragging = null;
                    dragRequested = 0;
                }
            }
        }

        if (drawnConstellations.size() >= 3) {
            LinkedList<DrawnConstellation> filtered = new LinkedList<>();
            for (int i = 0; i < 3; i++) {
                DrawnConstellation c = drawnConstellations.get(i);
                Point at = new Point(c.point.x, c.point.y);
                at.translate(-rctDrawingGrid.x, -rctDrawingGrid.y);
                filtered.add(new DrawnConstellation(at, c.constellation));
            }
            // 1.7.10: Use BlockPos from migration package
            PktEngraveGlass pkt = new PktEngraveGlass(
                getOwningTileEntity().getWorldObj().provider.dimensionId,
                new BlockPos(getOwningTileEntity().xCoord, getOwningTileEntity().yCoord, getOwningTileEntity().zCoord),
                filtered);
            PacketChannel.CHANNEL.sendToServer(pkt);
            drawnConstellations.clear();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && getOwningTileEntity().hasParchment()
            && drawnConstellations.size() < 3
            && getOwningTileEntity().hasUnengravedGlass()) {
            tryPickUp(new Point(mouseX, mouseY));
        }

    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (clickedMouseButton == 0 && dragging != null
            && getOwningTileEntity().hasParchment()
            && drawnConstellations.size() < 3
            && getOwningTileEntity().hasUnengravedGlass()) {
            dragRequested = 10;
        }
    }

    // 1.7.10: mouseReleased doesn't have @Override in GuiScreen
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        // 1.7.10: No super.mouseReleased in GuiScreen

        if (getOwningTileEntity().hasParchment() && drawnConstellations.size() < 3
            && getOwningTileEntity().hasUnengravedGlass()) {
            tryDrop(new Point(mouseX, mouseY));
        }
    }

    private void tryDrop(Point mouse) {
        if (dragging != null && dragRequested > 0) {

            Point gridPoint = translatePointToGrid(mouse);
            if (gridPoint != null) {
                if (!tryBurnParchment()) {
                    drawnConstellations.add(new DrawnConstellation(gridPoint, dragging));
                }
            }

            dragging = null;
            dragRequested = 0;
        }
    }

    private boolean tryBurnParchment() {
        for (int i = 0; i < drawnConstellations.size() + 1; i++) {
            if (EffectHandler.STATIC_EFFECT_RAND.nextInt(
                Math.max(
                    1,
                    WrapMathHelper.ceil(
                        7 * ConstellationSkyHandler.getInstance()
                            .getCurrentDaytimeDistribution(Minecraft.getMinecraft().theWorld))))
                == 0) {
                // 1.7.10: Use BlockPos from migration package
                PktBurnParchment pkt = new PktBurnParchment(
                    Minecraft.getMinecraft().theWorld.provider.dimensionId,
                    new BlockPos(
                        getOwningTileEntity().xCoord,
                        getOwningTileEntity().yCoord,
                        getOwningTileEntity().zCoord));
                PacketChannel.CHANNEL.sendToServer(pkt);
                return true;
            }
        }
        return false;
    }

    private void tryPickUp(Point mouse) {
        mouse.translate(-guiLeft, -guiTop);
        for (Rectangle r : mapRenderedConstellations.keySet()) {
            if (r.contains(mouse)) {
                dragging = mapRenderedConstellations.get(r);
                dragRequested = 10;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
