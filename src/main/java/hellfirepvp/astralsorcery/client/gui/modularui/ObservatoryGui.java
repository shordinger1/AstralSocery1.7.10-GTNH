/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Observatory GUI - Full-screen constellation viewing interface
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Observatory GUI - Full-screen constellation viewing interface
 * <p>
 * Features:
 * - Full-screen sky view
 * - Mouse-look camera control
 * - Constellation display
 * - Interactive constellation drawing
 * - Larger view area than telescope
 * <p>
 * NOTE: Custom rendering will be added when ModularUI custom widget support is available.
 */
public class ObservatoryGui {

    /**
     * Build the Observatory GUI panel
     * <p>
     * Note: This is a full-screen GUI, so the panel size should match the screen dimensions
     */
    public static ModularPanel buildUI(TileObservatory tile,
                                      PosGuiData guiData,
                                      PanelSyncManager guiSyncManager,
                                      UISettings settings) {
        LogHelper.info("[ObservatoryGui] Building Observatory GUI at " + tile.xCoord + "," + tile.yCoord + "," + tile.zCoord);

        // Sync viewing angles (use IntSyncValue to store float as int * 1000)
        IntSyncValue yawValue = new IntSyncValue(
            () -> (int) (tile.observatoryYaw * 1000),
            val -> tile.observatoryYaw = val / 1000F);
        guiSyncManager.syncValue("yaw", yawValue);

        IntSyncValue pitchValue = new IntSyncValue(
            () -> (int) (tile.observatoryPitch * 1000),
            val -> tile.observatoryPitch = val / 1000F);
        guiSyncManager.syncValue("pitch", pitchValue);

        // Create panel with reasonable size
        // Use larger size for better constellation viewing
        ModularPanel panel = new ModularPanel("observatory");
        panel.flex()
            .align(Alignment.Center)
            .size(256, 220); // Larger than standard GUI for constellation viewing

        // Add background texture
        panel.child(
            ObservatoryGuiTextures.BACKGROUND
                .asWidget()
                .pos(0, 0)
                .size(256, 220));

        // NOTE: Constellation rendering will be implemented separately
        // using event-based rendering or custom overlay system
        // For now, the GUI shows the background grid

        LogHelper.info("[ObservatoryGui] Observatory GUI built successfully");
        return panel;
    }

}
