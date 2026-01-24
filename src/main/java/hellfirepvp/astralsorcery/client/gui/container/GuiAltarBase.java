/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.container;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;

import hellfirepvp.astralsorcery.client.gui.base.GuiInventoryContainerBase;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.container.*;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiAltarBase
 * Created by HellFirePvP
 * Date: 16.10.2016 / 19:28
 */
public abstract class GuiAltarBase extends GuiInventoryContainerBase {

    public final ContainerAltarBase containerAltarBase;

    public GuiAltarBase(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(buildContainer(playerInv, tileAltar), tileAltar);
        this.containerAltarBase = (ContainerAltarBase) super.inventorySlots;
    }

    public AbstractAltarRecipe findCraftableRecipe() {
        return findCraftableRecipe(false);
    }

    public AbstractAltarRecipe findCraftableRecipe(boolean ignoreStarlightRequirement) {
        AbstractAltarRecipe rec = AltarRecipeRegistry
            .findMatchingRecipe(containerAltarBase.tileAltar, ignoreStarlightRequirement);
        if (rec != null) {
            if (rec instanceof IGatedRecipe) {
                if (((IGatedRecipe) rec).hasProgressionClient()) {
                    return rec;
                } else {
                    return null;
                }
            }
            return rec;
        }
        return null;
    }

    @Override
    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        renderGuiBackground(partialTicks, mouseX, mouseY);
        TextureHelper.refreshTextureBindState();
    }

    public abstract void renderGuiBackground(float partialTicks, int mouseX, int mouseY);

    private static ContainerAltarBase buildContainer(InventoryPlayer playerInv, TileAltar tileAltar) {
        switch (tileAltar.getAltarLevel()) {
            case DISCOVERY:
                return new ContainerAltarDiscovery(playerInv, tileAltar);
            case ATTUNEMENT:
                return new ContainerAltarAttunement(playerInv, tileAltar);
            case CONSTELLATION_CRAFT:
                return new ContainerAltarConstellation(playerInv, tileAltar);
            case TRAIT_CRAFT:
                return new ContainerAltarTrait(playerInv, tileAltar);
            case BRILLIANCE:
                break;
            default:
                break;
        }
        return new ContainerAltarDiscovery(playerInv, tileAltar);
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height, double u, double v, double uLength,
        double vLength) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, u, v + vLength);
        tes.addVertexWithUV(offsetX + width, offsetY + height, zLevel, u + uLength, v + vLength);
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, u + uLength, v);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, u, v);
        tes.draw();
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
