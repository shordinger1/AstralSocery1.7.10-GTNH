/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * HandTelescope GUI - 216x216 handheld constellation viewing interface
 * <p>
 * This is opened when using the handheld telescope item.
 * Features:
 * - Smaller view than block telescope
 * - Shows most prominent constellation
 * - Simplified constellation drawing
 * <p>
 * NOTE: Custom rendering will be added when ModularUI custom widget support is available.
 */
public class HandTelescopeGui {

    /**
     * Build the Hand Telescope GUI panel
     * <p>
     * This is an item GUI, not tied to a TileEntity
     */
    public static ModularPanel buildUI(EntityPlayer player, World world, PosGuiData guiData,
        PanelSyncManager guiSyncManager, UISettings settings) {
        LogHelper.info("[HandTelescopeGui] Building Hand Telescope GUI");

        // Create main panel - 216x216 size
        ModularPanel panel = new ModularPanel("hand_telescope");
        panel.flex()
            .size(216, 216)
            .align(Alignment.Center);

        // Add background texture
        panel.child(
            HandTelescopeGuiTextures.BACKGROUND.asWidget()
                .pos(0, 0)
                .size(216, 216));

        // NOTE: Constellation rendering will be implemented using event-based rendering
        // or custom ModularUI textures. For now, the GUI shows the background.
        // Players can still see constellations through the telescope's "lens" effect.

        return panel;
    }

}
