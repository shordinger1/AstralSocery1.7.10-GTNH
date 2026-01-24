/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.overlay;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalOverlayKnowledge
 * Created by HellFirePvP
 * Date: 26.09.2018 / 12:51
 */
public class GuiJournalOverlayKnowledge extends GuiScreenJournalOverlay {

    public static final BindableResource textureKnowledgeOverlay = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper_blank");
    private static final int HEADER_WIDTH = 190;
    private static final int DEFAULT_WIDTH = 175;

    private final KnowledgeFragment knowledgeFragment;
    private final List<String> lines = new LinkedList<>();

    public GuiJournalOverlayKnowledge(GuiScreenJournal origin, KnowledgeFragment display) {
        super(origin);
        this.knowledgeFragment = display;
    }

    @Override
    public void initGui() {
        super.initGui();

        String text = this.knowledgeFragment.getLocalizedPage();
        for (String segment : text.split("<NL>")) {
            lines.addAll(
                RenderingUtils
                    .listFormattedStringToWidth(Minecraft.getMinecraft().fontRenderer, segment, DEFAULT_WIDTH));
            lines.add("");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        Blending.DEFAULT.applyStateManager();

        int width = 275;
        int height = 344;

        textureKnowledgeOverlay.bindTexture();
        drawTexturedRect(
            guiLeft + guiWidth / 2 - width / 2,
            guiTop + guiHeight / 2 - height / 2,
            width,
            height,
            textureKnowledgeOverlay);

        drawHeader();
        drawPageText();

        TextureHelper.refreshTextureBindState();
    }

    private void drawPageText() {
        int offsetY = guiTop + 40;
        int offsetX = guiLeft + guiWidth / 2 - DEFAULT_WIDTH / 2;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // 1.7.10: drawString doesn't have shadow parameter
            Minecraft.getMinecraft().fontRenderer.drawString(line, offsetX, offsetY + (i * 10), 0xEE333333);
        }
    }

    private void drawHeader() {
        String locTitle = this.knowledgeFragment.getLocalizedIndexName();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        // 1.7.10: Use helper method for listFormattedStringToWidth
        List<String> split = RenderingUtils
            .listFormattedStringToWidth(fr, locTitle, WrapMathHelper.floor(HEADER_WIDTH / 1.4));
        int step = 14;

        int offsetTop = guiTop + 15 - (split.size() * step) / 2;

        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);

            double offsetLeft = width / 2 - (fr.getStringWidth(s) * 1.4) / 2;
            // 1.7.10: drawString doesn't have shadow parameter
            fr.drawString(s, 0, 0, 0xEE333333);
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    public KnowledgeFragment getKnowledgeFragment() {
        return knowledgeFragment;
    }

}
