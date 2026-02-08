/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Factory for creating ModularUI altar GUIs.
 * Routes to the appropriate altar GUI builder based on altar level.
 */
public class AltarGuiFactory {

    /**
     * Create a ModularUI panel for the given altar tile entity.
     *
     * @param tile           The altar tile entity
     * @param guiData        GUI data from ModularUI
     * @param guiSyncManager Sync manager for data synchronization
     * @param settings       UI settings
     * @return The constructed ModularPanel
     */
    public static ModularPanel createAltarUI(TileAltar tile, PosGuiData guiData, PanelSyncManager guiSyncManager,
        UISettings settings) {
        TileAltar.AltarLevel level = tile.getAltarLevel();
        LogHelper.info(
            "[AltarGuiFactory] Creating GUI for altar level: " + level
                + " at "
                + tile.xCoord
                + ","
                + tile.yCoord
                + ","
                + tile.zCoord);

        switch (level) {
            case DISCOVERY:
                LogHelper.info("[AltarGuiFactory] Routing to DiscoveryAltarGui");
                return DiscoveryAltarGui.buildUI(tile, guiData, guiSyncManager, settings);
            case ATTUNEMENT:
                LogHelper.info("[AltarGuiFactory] Routing to AttunementAltarGui");
                return AttunementAltarGui.buildUI(tile, guiData, guiSyncManager, settings);
            case CONSTELLATION_CRAFT:
                LogHelper.info("[AltarGuiFactory] Routing to ConstellationAltarGui");
                return ConstellationAltarGui.buildUI(tile, guiData, guiSyncManager, settings);
            case TRAIT_CRAFT:
                LogHelper.info("[AltarGuiFactory] Routing to TraitAltarGui");
                return TraitAltarGui.buildUI(tile, guiData, guiSyncManager, settings);
            case BRILLIANCE:
                LogHelper.info("[AltarGuiFactory] Routing to TraitAltarGui (Brilliance placeholder)");
                // Brilliance altar not yet implemented - use Trait as placeholder
                return TraitAltarGui.buildUI(tile, guiData, guiSyncManager, settings);
            default:
                LogHelper.warn("[AltarGuiFactory] Unknown altar level: " + level + ", routing to DiscoveryAltarGui");
                return DiscoveryAltarGui.buildUI(tile, guiData, guiSyncManager, settings);
        }
    }
}
