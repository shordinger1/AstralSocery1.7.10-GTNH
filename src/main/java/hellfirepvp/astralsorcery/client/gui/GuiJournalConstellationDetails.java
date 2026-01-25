/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.gui.journal.page.IJournalPage;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageTraitRecipe;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.MoonPhaseRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CapeAttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationPaperRecipe;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.RecipesAS;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellationDetails
 * Created by HellFirePvP
 * Date: 16.08.2016 / 19:09
 */
public class GuiJournalConstellationDetails extends GuiScreenJournal {

    private static final BindableResource texBlack = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MISC, "black");
    private static final BindableResource texBg = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guiresbgcst");
    private static final BindableResource texArrow = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");

    private IConstellation constellation;
    private GuiJournalConstellationCluster origin;
    private boolean detailed;

    private int doublePageID = 0;
    private int doublePages = 0;

    private IGuiRenderablePage lastFramePage = null;

    private Rectangle rectBack, rectNext, rectPrev;

    private List<MoonPhase> phases = new LinkedList<>();
    private List<MoonPhase> activePhases = new LinkedList<>();

    private List<String> locTextMain = new LinkedList<>();
    private List<String> locTextRitualEnch = new LinkedList<>();
    private List<String> locTextCapeEffect = new LinkedList<>();

    public GuiJournalConstellationDetails(GuiJournalConstellationCluster origin, IConstellation c) {
        super(-1);
        this.origin = origin;
        this.constellation = c;
        boolean has = false;
        for (String strConstellation : ResearchManager.clientProgress.getKnownConstellations()) {
            IConstellation ce = ConstellationRegistry.getConstellationByName(strConstellation);
            if (ce != null && ce.equals(c)) {
                has = true;
                break;
            }
        }
        this.detailed = has;
        ProgressionTier playerProgress = ResearchManager.clientProgress.getTierReached();
        if (has && (EnumGatedKnowledge.CONSTELLATION_RITUAL.canSee(playerProgress)
            || EnumGatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(playerProgress))) {
            this.doublePages++;

            if (EnumGatedKnowledge.CONSTELLATION_CAPE.canSee(playerProgress)
                && !(constellation instanceof IMinorConstellation)) {
                this.doublePages++;
            }
        }

        testPhases();
        testActivePhases();
        buildMainText();
        buildEnchText();
        buildRitualText();
        buildCapeText();
    }

    public IConstellation getConstellation() {
        return constellation;
    }

    private void buildCapeText() {
        if (EnumGatedKnowledge.CONSTELLATION_CAPE.canSee(ResearchManager.clientProgress.getTierReached())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".capeeffect";
            String textEnch = I18n.format(unlocEnch);
            if (!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.capeeffect");
                locTextCapeEffect.add(head);
                locTextCapeEffect.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(
                        RenderingUtils.listFormattedStringToWidth(
                            Minecraft.getMinecraft().fontRenderer,
                            segment,
                            IJournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextCapeEffect.addAll(lines);
                locTextCapeEffect.add("");
            }
        }
    }

    private void buildEnchText() {
        if (EnumGatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(ResearchManager.clientProgress.getTierReached())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".enchantments";
            String textEnch = I18n.format(unlocEnch);
            if (!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.enchantments");
                locTextRitualEnch.add(head);
                locTextRitualEnch.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(
                        RenderingUtils.listFormattedStringToWidth(
                            Minecraft.getMinecraft().fontRenderer,
                            segment,
                            IJournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextRitualEnch.addAll(lines);
                locTextRitualEnch.add("");
            }
        }
    }

    private void buildRitualText() {
        if (EnumGatedKnowledge.CONSTELLATION_RITUAL.canSee(ResearchManager.clientProgress.getTierReached())) {
            if (constellation instanceof IMinorConstellation) {
                String unlocRitual = constellation.getUnlocalizedName() + ".trait";
                String textRitual = I18n.format(unlocRitual);
                if (!unlocRitual.equals(textRitual)) {
                    String head = I18n.format("gui.journal.cst.ritual.trait");
                    locTextRitualEnch.add(head);
                    locTextRitualEnch.add("");

                    List<String> lines = new LinkedList<>();
                    for (String segment : textRitual.split("<NL>")) {
                        lines.addAll(
                            RenderingUtils.listFormattedStringToWidth(
                                Minecraft.getMinecraft().fontRenderer,
                                segment,
                                IJournalPage.DEFAULT_WIDTH));
                        lines.add("");
                    }
                    locTextRitualEnch.addAll(lines);
                }
            } else {
                String unlocRitual = constellation.getUnlocalizedName() + ".ritual";
                String textRitual = I18n.format(unlocRitual);
                if (!unlocRitual.equals(textRitual)) {
                    String head = I18n.format("gui.journal.cst.ritual");
                    locTextRitualEnch.add(head);
                    locTextRitualEnch.add("");

                    List<String> lines = new LinkedList<>();
                    for (String segment : textRitual.split("<NL>")) {
                        lines.addAll(
                            RenderingUtils.listFormattedStringToWidth(
                                Minecraft.getMinecraft().fontRenderer,
                                segment,
                                IJournalPage.DEFAULT_WIDTH));
                        lines.add("");
                    }
                    locTextRitualEnch.addAll(lines);
                }
            }
        }
    }

    private void buildMainText() {
        String unloc = constellation.getUnlocalizedName() + ".effect";
        String text = I18n.format(unloc);
        if (unloc.equals(text)) return;

        List<String> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            lines.addAll(
                RenderingUtils.listFormattedStringToWidth(
                    Minecraft.getMinecraft().fontRenderer,
                    segment,
                    IJournalPage.DEFAULT_WIDTH));
            lines.add("");
        }
        locTextMain.addAll(lines);
    }

    private void testPhases() {
        Collections.addAll(phases, MoonPhase.values());
    }

    private void testActivePhases() {
        if (Minecraft.getMinecraft().theWorld == null) return;
        WorldSkyHandler handler = ConstellationSkyHandler.getInstance()
            .getWorldHandler(Minecraft.getMinecraft().theWorld);
        if (handler == null) return;
        for (MoonPhase phase : this.phases) {
            List<IConstellation> active = handler.getConstellationsForMoonPhase(phase);
            if (active != null && !(active == null || active.size() <= 0)) {
                if (active.contains(constellation)) {
                    activePhases.add(phase);
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        lastFramePage = null;

        Point mouse = new Point(mouseX, mouseY);

        switch (doublePageID) {
            case 0:
                drawCstBackground();
                drawDefault(textureResShellCst, mouse);
                break;
            case 1:
                drawDefault(textureResBlank, mouse);
                break;
            case 2:
                drawDefault(textureResBlank, mouse);
                break;
            default:
                break;
        }
        TextureHelper.refreshTextureBindState();

        zLevel += 150;
        drawArrows(partialTicks, mouse);
        drawNavArrows(partialTicks, mouse);
        switch (doublePageID) {
            case 0:
                drawConstellation(partialTicks);
                drawPhaseInformation();
                drawExtendedInformation();
                break;
            case 1:
                drawERPInformationPages(partialTicks, mouseX, mouseY);
                break;
            case 2:
                drawCapeInformationPages(partialTicks, mouseX, mouseY);
                break;
            default:
                break;
        }
        zLevel -= 150;

    }

    private void drawCapeInformationPages(float partialTicks, int mouseX, int mouseY) {
        for (int i = 0; i < locTextCapeEffect.size(); i++) {
            String line = locTextCapeEffect.get(i);
            // 1.7.10: drawString doesn't have shadow parameter
            Minecraft.getMinecraft().fontRenderer.drawString(line, 0, (i * 10), 0x00DDDDDD);
        }

        CapeAttunementRecipe recipe = RecipesAS.capeCraftingRecipes.get(this.constellation);
        if (recipe != null) {
            lastFramePage = new JournalPageTraitRecipe(recipe).buildRenderPage();

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            lastFramePage.render(guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
            lastFramePage.postRender(guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
        }
    }

    private void drawERPInformationPages(float partialTicks, int mouseX, int mouseY) {
        ProgressionTier prog = ResearchManager.clientProgress.getTierReached();
        for (int i = 0; i < locTextRitualEnch.size(); i++) {
            String line = locTextRitualEnch.get(i);
            // 1.7.10: drawString doesn't have shadow parameter
            Minecraft.getMinecraft().fontRenderer.drawString(line, 0, (i * 10), 0x00DDDDDD);
        }
        if (EnumGatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(prog)) {
            ConstellationPaperRecipe recipe = RecipesAS.paperCraftingRecipes.get(this.constellation);
            if (recipe != null) {
                lastFramePage = new JournalPageTraitRecipe(recipe).buildRenderPage();

                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                lastFramePage.render(guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
                lastFramePage.postRender(guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
                GL11.glPopAttrib();
            }
        }
    }

    private void drawNavArrows(float partialTicks, Point mouse) {
        rectNext = null;
        rectPrev = null;
        if (doublePageID - 1 >= 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
            float uFrom = 0F, vFrom = 0.5F;
            if (rectPrev.contains(mouse)) {
                uFrom = 0.5F;
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            }
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        }
        if (doublePageID + 1 <= doublePages) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
            float uFrom = 0F, vFrom = 0F;
            if (rectNext.contains(mouse)) {
                uFrom = 0.5F;
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            }
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        }
    }

    private void drawCstBackground() {
        texBlack.bind();
        Tessellator tes = Tessellator.instance;
        tes.startDrawing(GL11.GL_QUADS);
        tes.addVertexWithUV(guiLeft + 15, guiTop + 240, zLevel, 0, 1);
        tes.addVertexWithUV(guiLeft + 200, guiTop + 240, zLevel, 1, 1);
        tes.addVertexWithUV(guiLeft + 200, guiTop + 10, zLevel, 1, 0);
        tes.addVertexWithUV(guiLeft + 15, guiTop + 10, zLevel, 0, 0);
        tes.draw();
        texBg.bind();
        tes.startDrawing(GL11.GL_QUADS);
        tes.addVertexWithUV(guiLeft + 35, guiTop + guiHeight - 10, zLevel, 0.3, 0.9);
        tes.addVertexWithUV(guiLeft + 35 + 170, guiTop + guiHeight - 10, zLevel, 0.7, 0.9);
        tes.addVertexWithUV(guiLeft + 35 + 170, guiTop + 10, zLevel, 0.7, 0.1);
        tes.addVertexWithUV(guiLeft + 35, guiTop + 10, zLevel, 0.3, 0.1);
        tes.draw();
    }

    private void drawExtendedInformation() {
        float br = 0.8666F;
        String info = I18n.format(constellation.getUnlocalizedInfo())
            .toUpperCase();
        info = detailed ? info : "???";
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        double width = fr.getStringWidth(info);
        double chX = 305 - (width / 2);
        int posX = (int) chX + guiLeft;
        int posY = guiTop + 36;
        fr.drawString(info, posX, posY, 0xCCDDDDDD);
        TextureHelper.refreshTextureBindState();

        if (detailed && !(locTextMain == null || locTextMain.size() <= 0)) {
            int offsetX = 220, offsetY = 77;
            for (String s : locTextMain) {
                fr.drawString(s, guiLeft + offsetX, guiTop + offsetY, 0xCCDDDDDD);
                TextureHelper.refreshTextureBindState();
                offsetY += 13;
            }
        }

        texArrow.bind();
        // 1.7.10: FontRenderer doesn't have font_size_multiplicator or advanced drawString
        // This would require significant rework for 1.7.10 compatibility
        // Skipping constraint rendering for now
        /*
         * fontRenderer.font_size_multiplicator = 0.06F;
         * String pref = I18n.translateToLocal("constraint.description");
         * fontRenderer.drawString(pref, guiLeft + 228, guiTop + 60, zLevel, null, 0.7F, 0);
         * texArrow.bind();
         * fontRenderer.font_size_multiplicator = 0.05F;
         * SizeConstraint sc = constellation.getSizeConstraint();
         * String trSize = I18n.translateToLocal(sc.getUnlocalizedName());
         * fontRenderer.drawString("- " + trSize, guiLeft + 228, guiTop + 85, zLevel, null, 0.7F, 0);
         * List<RitualConstraint> constrList = constellation.getConstraints();
         * for (int i = 0; i < constrList.size(); i++) {
         * RitualConstraint cstr = constrList.get(i);
         * String str = I18n.translateToLocal(cstr.getUnlocalizedName());
         * texArrow.bind();
         * fontRenderer.font_size_multiplicator = 0.05F;
         * fontRenderer.drawString("- " + str, guiLeft + 228, guiTop + 107 + (i * 22), zLevel, null, 0.7F, 0);
         * }
         */
    }

    private void drawPhaseInformation() {
        if (this.phases == null || phases.stackSize <= 0) {
            testPhases();
            testActivePhases();
            if (this.phases == null || phases.stackSize <= 0) {
                return;
            }
        }

        if (constellation instanceof IConstellationSpecialShowup) {
            double scale = 1.8;
            TextureHelper.refreshTextureBindState();
            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            double length = fr.getStringWidth("? ? ?") * scale;
            int posX = (int) (guiLeft + 296 - length / 2);
            int posY = guiTop + 199;
            fr.drawStringWithShadow("? ? ?", posX, posY, 0xCCDDDDDD);
            TextureHelper.refreshTextureBindState();
        } else if (ResearchManager.clientProgress.hasConstellationDiscovered(constellation.getUnlocalizedName())) {
            Blending.DEFAULT.applyStateManager();
            int size = 19;
            int offsetX = 95 + (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = 199 + guiTop;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                if (!this.activePhases.contains(ph)) {
                    MoonPhaseRenderHelper.getMoonPhaseTexture(ph)
                        .bind();
                    drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
                }
            }
            Blending.PREALPHA.applyStateManager();
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                if (this.activePhases.contains(ph)) {
                    MoonPhaseRenderHelper.getMoonPhaseTexture(ph)
                        .bind();
                    drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
                }
            }
            Blending.DEFAULT.applyStateManager();
            TextureHelper.refreshTextureBindState();
        } else {
            Blending.DEFAULT.applyStateManager();
            int size = 19;
            int offsetX = 95 + (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = 199 + guiTop;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                MoonPhaseRenderHelper.getMoonPhaseTexture(ph)
                    .bind();
                drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
            }
            TextureHelper.refreshTextureBindState();
        }
    }

    private void drawConstellation(float partial) {
        float br = 0.866F;
        String name = I18n.format(constellation.getUnlocalizedName())
            .toUpperCase();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double width = fr.getStringWidth(name);
        double offsetX = 305 - (width * 1.8 / 2);
        int posX = (int) offsetX + guiLeft;
        int posY = guiTop + 17;
        fr.drawString(name, posX, posY, 0xEEDDDDDD);
        TextureHelper.refreshTextureBindState();
        String dstInfo = "astralsorcery.journal.constellation.dst.";
        if (constellation instanceof IMajorConstellation) {
            dstInfo += "major";
        } else if (constellation instanceof IWeakConstellation) {
            dstInfo += "weak";
        } else {
            dstInfo += "minor";
        }
        if (!detailed) {
            dstInfo = "???";
        }
        dstInfo = I18n.format(dstInfo);
        width = fr.getStringWidth(dstInfo);
        offsetX = 305 - (width / 2);
        posX = (int) offsetX + guiLeft;
        posY = guiTop + 50;
        fr.drawString(dstInfo, posX, posY, 0x99DDDDDD);
        TextureHelper.refreshTextureBindState();

        Random rand = new Random(0x4196A15C91A5E199L);

        Blending.DEFAULT.apply();

        boolean known = ResearchManager.clientProgress.hasConstellationDiscovered(constellation.getUnlocalizedName());
        RenderConstellation.renderConstellationIntoGUI(
            known ? constellation.getConstellationColor() : constellation.getTierRenderColor(),
            constellation,
            guiLeft + 40,
            guiTop + 60,
            zLevel,
            150,
            150,
            2F,
            new RenderConstellation.BrightnessFunction() {

                @Override
                public float getBrightness() {
                    return 0.3F + 0.7F * RenderConstellation
                        .conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10));
                }
            },
            true,
            false);
    }

    private void drawArrows(float partialTicks, Point mouse) {
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
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        Minecraft.getMinecraft()
            .displayGuiScreen(origin);
        return true;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if (handleBookmarkClick(p)) {
            return;
        }

        if (rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft()
                .displayGuiScreen(origin);
            return;
        }
        if (rectPrev != null && rectPrev.contains(p)) {
            if (doublePageID >= 1) {
                this.doublePageID--;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if (rectNext != null && rectNext.contains(p)) {
            if (doublePageID <= doublePages - 1) {
                this.doublePageID++;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if (doublePageID != 0 && lastFramePage != null) {
            lastFramePage.propagateMouseClick(mouseX, mouseY);
        }
    }

}
