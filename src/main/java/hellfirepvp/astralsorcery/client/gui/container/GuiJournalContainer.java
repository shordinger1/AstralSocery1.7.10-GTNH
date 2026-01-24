/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.container;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.container.ContainerJournal;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalContainer
 * Created by HellFirePvP
 * Date: 22.11.2016 / 14:38
 */
public class GuiJournalContainer extends GuiContainer {

    private static BindableResource texJournalContainer = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijstoragebook");

    public GuiJournalContainer(InventoryPlayer playerInv, ItemStack journal, int journalIndex) {
        super(new ContainerJournal(playerInv, journal, journalIndex));
    }

    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 166;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        // 1.7.10: Tooltips are rendered automatically by super.drawScreen()
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        texJournalContainer.bind();
        drawRect(guiLeft, guiTop, xSize, ySize);
        TextureHelper.refreshTextureBindState();
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, 0, 1);
        tes.addVertexWithUV(offsetX + width, offsetY + height, zLevel, 1, 1);
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, 1, 0);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, 0, 0);
        tes.draw();
    }

}
