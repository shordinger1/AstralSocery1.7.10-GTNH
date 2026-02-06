/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Telescope GUI - 280x280 constellation viewing interface
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import java.util.UUID;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;

import hellfirepvp.astralsorcery.client.gui.modularui.widget.SkyRenderWidget;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Telescope GUI - 280x280 constellation viewing interface
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Sky background with random stars</li>
 * <li>Constellation display with discovery state</li>
 * <li>Rotation buttons (CW/CCW)</li>
 * <li>Interactive constellation discovery (click to discover)</li>
 * </ul>
 * <p>
 * <b>Discovery Mechanics:</b>
 * - Click on an undiscovered constellation to discover it
 * - Constellation must be currently visible in the sky
 * - Adds constellation to player's known constellations
 * <p>
 * <b>Rotation:</b>
 * - Use arrow buttons to rotate the sky view
 * - Rotation cycles through 8 directions (N, NE, E, SE, S, SW, W, NW)
 * - Affects which part of sky is visible
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

        // Get player and world
        EntityPlayer player = getPlayer(guiData);
        World world = tile.getWorldObj();
        if (player == null || world == null) {
            LogHelper.warn("[TelescopeGui] Cannot get player or world, skipping GUI build");
            return createErrorPanel("Player or world not available");
        }

        // Get player progress
        PlayerProgress progress = ResearchManager.getProgress(player);
        if (progress == null) {
            LogHelper.warn("[TelescopeGui] Player progress is null, creating new one");
            progress = new PlayerProgress();
        }

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

        // Add sky render widget (main viewing area)
        SkyRenderWidget skyWidget = new SkyRenderWidget()
            .pos(20, 20)
            .size(240, 240)
            .setTelescope(tile)
            .setPlayerProgress(progress)
            .setClickListener(constellation -> onConstellationClicked(player, world, constellation));

        panel.child(skyWidget);

        // Add rotation buttons
        panel.child(
            new ButtonWidget<>()
                .pos(10, 230)
                .size(40, 20)
                .overlay(TelescopeGuiTextures.ARROW_CCW)
                .onMousePressed(mouseButton -> {
                    // Rotate counter-clockwise
                    if (mouseButton == 0) {
                        TileTelescope.TelescopeRotation current = tile.getRotation();
                        TileTelescope.TelescopeRotation next = current.nextCounterClockWise();
                        tile.setRotation(next);
                        LogHelper.debug("[TelescopeGui] Rotated CCW to " + next);
                        return true;
                    }
                    return false;
                }));

        panel.child(
            new ButtonWidget<>()
                .pos(230, 230)
                .size(40, 20)
                .overlay(TelescopeGuiTextures.ARROW_CW)
                .onMousePressed(mouseButton -> {
                    // Rotate clockwise
                    if (mouseButton == 0) {
                        TileTelescope.TelescopeRotation current = tile.getRotation();
                        TileTelescope.TelescopeRotation next = current.nextClockWise();
                        tile.setRotation(next);
                        LogHelper.debug("[TelescopeGui] Rotated CW to " + next);
                        return true;
                    }
                    return false;
                }));

        return panel;
    }

    /**
     * Handle constellation click - attempt discovery
     *
     * @param player       The player
     * @param world        The world
     * @param constellation The constellation that was clicked
     */
    private static void onConstellationClicked(EntityPlayer player, World world,
                                                IConstellation constellation) {
        LogHelper.info("[TelescopeGui] Constellation clicked: " + constellation.getUnlocalizedName());

        // Check if constellation is currently visible
        ConstellationSkyHandler skyHandler = ConstellationSkyHandler.getInstance();
        if (!skyHandler.isConstellationVisible(world, constellation)) {
            // Constellation not visible
            LogHelper.debug("[TelescopeGui] Constellation not visible, cannot discover");
            return;
        }

        // Check if constellation can be discovered by this player
        PlayerProgress progress = ResearchManager.getProgress(player);
        if (!constellation.canDiscover(player, progress)) {
            // Player doesn't meet requirements
            LogHelper.debug("[TelescopeGui] Player cannot discover this constellation yet");
            return;
        }

        // Discover constellation
        boolean success = ResearchManager.discoverConstellation(constellation, player);
        if (success) {
            LogHelper.info("[TelescopeGui] Constellation discovered: " + constellation.getUnlocalizedName());

            // TODO: Play discovery sound effect
            // Play sound effect here when sound system is implemented
            // world.playSoundEffect(x, y, z, "astralsorcery:constellation.discover", 1.0F, 1.0F);
        } else {
            LogHelper.warn("[TelescopeGui] Failed to discover constellation: " + constellation.getUnlocalizedName());
        }
    }

    /**
     * Get player from PosGuiData
     */
    private static EntityPlayer getPlayer(PosGuiData guiData) {
        // Try to get player from Minecraft instance
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
            return mc.thePlayer;
        } catch (Exception e) {
            LogHelper.error("[TelescopeGui] Failed to get player", e);
            return null;
        }
    }

    /**
     * Create an error panel when GUI cannot be built
     */
    private static ModularPanel createErrorPanel(String errorMessage) {
        ModularPanel panel = new ModularPanel("telescope_error");
        panel.flex()
            .size(280, 280)
            .align(Alignment.Center);

        panel.child(
            new com.cleanroommc.modularui.widgets.TextWidget(errorMessage)
                .pos(10, 10));

        return panel;
    }
}
