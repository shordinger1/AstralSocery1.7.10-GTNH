/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.api.drawable.IKey;

/**
 * Factory for creating traditional bar-shaped progress widgets for starlight display.
 * Creates a horizontal progress bar without arrow, left-to-right fill.
 */
public class StarlightBarWidget {

    /**
     * Create a traditional horizontal progress bar widget for starlight display
     * @param tile The altar tile entity
     * @param x X position
     * @param y Y position
     * @param width Bar width
     * @param height Bar height
     */
    public static ProgressWidget create(TileAltar tile, int x, int y, int width, int height) {
        return new ProgressWidget()
            .progress(() -> (double) tile.getStarlightStored() / (double) tile.getMaxStarlightStorage())
            .pos(x, y)
            .size(width, height)
            .tooltip(tooltip -> {
                tooltip.addLine(IKey.str("Starlight: " + tile.getStarlightStored() + " / " + tile.getMaxStarlightStorage()));
                if (!tile.getMultiblockState() && tile.getAltarLevel() != TileAltar.AltarLevel.DISCOVERY) {
                    tooltip.addLine(IKey.str("§cMultiblock incomplete!"));
                }
            });
    }
}
