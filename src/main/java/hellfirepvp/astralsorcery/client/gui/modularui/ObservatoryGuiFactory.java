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

import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Factory for creating Observatory GUI
 */
public class ObservatoryGuiFactory {

    /**
     * Create a ModularUI panel for the observatory
     *
     * @param tile The observatory tile entity
     * @param guiData GUI data from ModularUI
     * @param guiSyncManager Sync manager for data synchronization
     * @param settings UI settings
     * @return The constructed ModularPanel
     */
    public static ModularPanel createObservatoryUI(TileObservatory tile,
                                                   PosGuiData guiData,
                                                   PanelSyncManager guiSyncManager,
                                                   UISettings settings) {
        LogHelper.info("[ObservatoryGuiFactory] Creating Observatory GUI");
        return ObservatoryGui.buildUI(tile, guiData, guiSyncManager, settings);
    }
}
