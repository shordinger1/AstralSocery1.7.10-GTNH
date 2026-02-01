/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Constellation Altar GUI (Tier 3)
 * Uses guialtar3.png as background
 *
 * Slot Layout: 21 slots total
 * GUI Size: 255x202
 */
public class ConstellationAltarGui {

    public static ModularPanel buildUI(TileAltar tile,
                                      PosGuiData guiData,
                                      PanelSyncManager guiSyncManager,
                                      UISettings settings) {
        LogHelper.info("[ConstellationAltarGui] Building Constellation Altar GUI at " + tile.xCoord + "," + tile.yCoord + "," + tile.zCoord + ", level: " + tile.getAltarLevel());
        IItemHandlerModifiable inventory = tile.getInventory();

        // Register slot groups for altar inventory
        guiSyncManager.registerSlotGroup("altar_inv", tile.getInventorySize());
        guiSyncManager.bindPlayerInventory(guiData.getPlayer());

        // Sync starlight stored value
        IntSyncValue starlightValue = new IntSyncValue(
            tile::getStarlightStored,
            val -> tile.setStarlightStored(val));
        guiSyncManager.syncValue("starlight", starlightValue);

        // Sync multiblock state
        IntSyncValue multiblockValue = new IntSyncValue(
            () -> tile.getMultiblockState() ? 1 : 0,
            val -> tile.setMultiblockState(val == 1));
        guiSyncManager.syncValue("multiblock", multiblockValue);

        // Create main panel - 255x202 size
        ModularPanel panel = new ModularPanel("constellation_altar");
        panel.flex()
            .size(255, 202)
            .align(Alignment.Center);

        // Add background texture
        panel.child(
            AltarGuiTextures.CONSTELLATION_BACKGROUND
                .asWidget()
                .pos(0, 0)
                .size(255, 202));

        // Add starlight bar rendering (over background) - centered
        panel.child(StarlightBarWidget.create(tile, 45, 105, 165, 10));

        // Add altar slots with starlight rendering
        // Central 3x3 grid
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 0).slotGroup("altar_inv")).pos(102, 29));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 1).slotGroup("altar_inv")).pos(120, 29));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 2).slotGroup("altar_inv")).pos(138, 29));

        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 3).slotGroup("altar_inv")).pos(102, 47));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 4).slotGroup("altar_inv")).pos(120, 47));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 5).slotGroup("altar_inv")).pos(138, 47));

        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 6).slotGroup("altar_inv")).pos(102, 65));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 7).slotGroup("altar_inv")).pos(120, 65));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 8).slotGroup("altar_inv")).pos(138, 65));

        // Corner slots
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 9).slotGroup("altar_inv")).pos(84, 11));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 10).slotGroup("altar_inv")).pos(156, 11));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 11).slotGroup("altar_inv")).pos(84, 83));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 12).slotGroup("altar_inv")).pos(156, 83));

        // Outer ring slots (Constellation-specific)
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 13).slotGroup("altar_inv")).pos(102, 11));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 14).slotGroup("altar_inv")).pos(138, 11));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 15).slotGroup("altar_inv")).pos(84, 29));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 16).slotGroup("altar_inv")).pos(156, 29));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 17).slotGroup("altar_inv")).pos(84, 65));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 18).slotGroup("altar_inv")).pos(156, 65));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 19).slotGroup("altar_inv")).pos(102, 83));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 20).slotGroup("altar_inv")).pos(138, 83));

        // Add player inventory
        panel.child(SlotGroupWidget.playerInventory(7, true));

        return panel;
    }
}
