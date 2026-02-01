/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Telescope GUI - 280x280 constellation viewing interface
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Telescope GUI - 280x280 constellation viewing interface
 * <p>
 * Features:
 * - Sky background with random stars
 * - Constellation display
 * - Rotation arrows (CW/CCW)
 * - Interactive constellation drawing
 * <p>
 * NOTE: Rotation buttons will be added when we determine the correct ModularUI button API.
 * For now, the GUI displays the background and syncs rotation state.
 */
public class TelescopeGui {

    /**
     * Build the Telescope GUI panel
     */
    public static ModularPanel buildUI(TileTelescope tile,
                                      PosGuiData guiData,
                                      PanelSyncManager guiSyncManager,
                                      UISettings settings) {
        LogHelper.info("[TelescopeGui] Building Telescope GUI at " + tile.xCoord + "," + tile.yCoord + "," + tile.zCoord);

        // Sync rotation value
        IntSyncValue rotationValue = new IntSyncValue(
            () -> tile.getRotation().ordinal(),
            val -> tile.setRotation(TileTelescope.TelescopeRotation.values()[val]));
        guiSyncManager.syncValue("rotation", rotationValue);

        // Create main panel - 280x280 size
        ModularPanel panel = new ModularPanel("telescope");
        panel.flex()
            .size(280, 280)
            .align(Alignment.Center);

        // Add background texture
        panel.child(
            TelescopeGuiTextures.BACKGROUND
                .asWidget()
                .pos(0, 0)
                .size(280, 280));

        // TODO: Add rotation buttons (CW/CCW arrows)
        // Need to determine correct ModularUI button API for 1.7.10

        // TODO: Add custom sky rendering widget when ModularUI supports it

        return panel;
    }

}
