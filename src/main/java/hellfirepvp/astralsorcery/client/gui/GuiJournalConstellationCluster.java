/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellationCluster
 * Created by HellFirePvP
 * Date: 15.08.2016 / 12:53
 */
public class GuiJournalConstellationCluster extends GuiScreenJournal {

    // private static OverlayText.OverlayFontRenderer fRend = new OverlayText.OverlayFontRenderer();

    // private static final BindableResource texArrowLeft = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC,
    // "arrow_left");
    // private static final BindableResource texArrowRight = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC,
    // "arrow_right");

    private static final int CONSTELLATIONS_PER_PAGE = 4;
    private static final BindableResource texBlack = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MISC, "black");
    private static final BindableResource texArrow = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");
    private static final BindableResource texBg = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guiresbgcst");

    private static final int width = 80, height = 110;
    private static final Map<Integer, Point> offsetMap = new HashMap<>(); // we put 6 on "1" page/screen

    private List<IConstellation> constellations;
    private String unlocTitle;
    private int pageId = 0;

    private Map<Rectangle, IConstellation> rectCRenderMap = new HashMap<>();

    private Rectangle rectBack, rectPrev, rectNext;

    public GuiJournalConstellationCluster(int bookmark, int pageId, String unlocTitle,
        List<IConstellation> constellations) {
        this(bookmark, unlocTitle, constellations);
        this.pageId = pageId;
    }

    public GuiJournalConstellationCluster(int bookmark, String unlocTitle, List<IConstellation> constellations) {
        super(bookmark);
        this.unlocTitle = unlocTitle;
        this.constellations = constellations;
    }

    public static GuiScreenJournal getConstellationScreen() {
        PlayerProgress client = ResearchManager.clientProgress;
        List<IConstellation> constellations = ConstellationRegistry.resolve(client.getSeenConstellations());
        return new GuiJournalConstellationCluster(20, "no.title", constellations);

        /*
         * if(tiersFound.isEmpty()) {
         * return new GuiJournalConstellationCluster(1, false, "gui.journal.c.unmapped", unmapped);
         * } else if(tiersFound.size() == 1) {
         * return new GuiJournalConstellationCluster(1, true, tiersFound.get(0).getUnlocalizedName(),
         * tierMapped.get(tiersFound.get(0)));
         * } else {
         * return new GuiJournalConstellations(unmapped, tiersFound);
         * }
         */
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Point mouse = new Point(mouseX, mouseY);

        drawCstBackground();
        drawDefault(textureResShell, mouse);

        zLevel += 250;
        drawNavArrows(partialTicks, mouse);
        rectCRenderMap.clear();
        drawConstellations(partialTicks, mouse);
        zLevel -= 250;

    }

    private void drawCstBackground() {
        texBlack.bind();
        Tessellator tes = Tessellator.instance;
        tes.startDrawing(GL11.GL_QUADS);
        tes.addVertexWithUV(guiLeft + 15, guiTop + guiHeight - 10, zLevel, 0, 1);
        tes.addVertexWithUV(guiLeft + guiWidth - 15, guiTop + guiHeight - 10, zLevel, 1, 1);
        tes.addVertexWithUV(guiLeft + guiWidth - 15, guiTop + 10, zLevel, 1, 0);
        tes.addVertexWithUV(guiLeft + 15, guiTop + 10, zLevel, 0, 0);
        tes.draw();
        texBg.bind();
        tes.startDrawing(GL11.GL_QUADS);
        tes.addVertexWithUV(guiLeft + 15, guiTop + guiHeight - 10, zLevel, 0.1, 0.9);
        tes.addVertexWithUV(guiLeft + guiWidth - 15, guiTop + guiHeight - 10, zLevel, 0.9, 0.9);
        tes.addVertexWithUV(guiLeft + guiWidth - 15, guiTop + 10, zLevel, 0.9, 0.1);
        tes.addVertexWithUV(guiLeft + 15, guiTop + 10, zLevel, 0.1, 0.1);
        tes.draw();
    }

    private void drawConstellations(float partial, Point mouse) {
        List<IConstellation> cs = constellations.subList(
            pageId * CONSTELLATIONS_PER_PAGE,
            Math.min((pageId + 1) * CONSTELLATIONS_PER_PAGE, constellations.size()));
        for (int i = 0; i < cs.size(); i++) {
            IConstellation c = cs.get(i);
            Point p = offsetMap.get(i);
            rectCRenderMap.put(drawConstellationRect(c, guiLeft + p.x, guiTop + p.y, zLevel, partial, mouse, null), c);
        }
        TextureHelper.refreshTextureBindState();
    }

    private void drawNavArrows(float partialTicks, Point mouse) {
        int cIndex = pageId * CONSTELLATIONS_PER_PAGE;
        rectBack = null;
        rectNext = null;
        rectPrev = null;
        if (cIndex > 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 15, guiTop + 127, width, height);
            float uFrom = 0F, vFrom = 0.5F;
            if (rectPrev.contains(mouse)) {
                uFrom = 0.5F;
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            }
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            TextureHelper.refreshTextureBindState();
        }
        int nextIndex = cIndex + CONSTELLATIONS_PER_PAGE;
        if (constellations.size() >= (nextIndex + 1)) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 125, width, height);
            float uFrom = 0F, vFrom = 0F;
            if (rectNext.contains(mouse)) {
                uFrom = 0.5F;
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            }
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            TextureHelper.refreshTextureBindState();
        }

        if (bookmarkIndex != 1) {
            int width = 30;
            int height = 15;
            rectBack = new Rectangle(guiLeft + 197, guiTop + 230, width, height);
            float uFrom = 0F, vFrom = 0.5F;
            if (rectBack.contains(mouse)) {
                uFrom = 0.5F;
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            }
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            TextureHelper.refreshTextureBindState();
        }
    }

    protected static Rectangle drawConstellationRect(IConstellation display, double offsetX, double offsetY,
        float zLevel, float partial, Point mouse, @Nullable String specTitle) {
        Rectangle rect = new Rectangle(WrapMathHelper.floor(offsetX), WrapMathHelper.floor(offsetY), width, height);

        if (rect.contains(mouse)) {}

        Random rand = new Random(0x4196A15C91A5E199L);

        float r = 0xDD / 255F;
        float g = 0xDD / 255F;
        float b = 0xDD / 255F;

        RenderConstellation
            .renderConstellationIntoGUI(display, 0, 0, 0, 95, 95, 2F, new RenderConstellation.BrightnessFunction() {

                @Override
                public float getBrightness() {
                    return 0.3F + 0.7F * RenderConstellation
                        .conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10));
                }
            }, true, false);

        TextureHelper.refreshTextureBindState();
        String trName = specTitle == null ? I18n.format(display.getUnlocalizedName())
            .toUpperCase()
            : I18n.format(specTitle)
                .toUpperCase();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        float fullLength = (width / 2) - (((float) fr.getStringWidth(trName)) / 2F);
        fr.drawString(trName, (int) fullLength, 90, 0xBBDDDDDD, true);

        return rect;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if (handleBookmarkClick(p)) {
            return;
        }
        for (Rectangle r : rectCRenderMap.keySet()) {
            if (r.contains(p)) {
                IConstellation c = rectCRenderMap.get(r);
                Minecraft.getMinecraft()
                    .displayGuiScreen(new GuiJournalConstellationDetails(this, c));
            }
        }
        if (rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft()
                .displayGuiScreen(getConstellationScreen());
            return;
        }
        if (rectPrev != null && rectPrev.contains(p)) {
            Minecraft.getMinecraft()
                .displayGuiScreen(
                    new GuiJournalConstellationCluster(bookmarkIndex, pageId - 1, unlocTitle, constellations));
            return;
        }
        if (rectNext != null && rectNext.contains(p)) {
            Minecraft.getMinecraft()
                .displayGuiScreen(
                    new GuiJournalConstellationCluster(bookmarkIndex, pageId + 1, unlocTitle, constellations));
        }
    }

    static {
        offsetMap.put(0, new Point(45, 55));
        offsetMap.put(1, new Point(125, 105));
        offsetMap.put(2, new Point(200, 45));
        offsetMap.put(3, new Point(280, 110));
    }

}
