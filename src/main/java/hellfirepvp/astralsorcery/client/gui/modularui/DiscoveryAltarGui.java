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
 * Discovery Altar GUI (Tier 1)
 * Uses guialtar1.png as background
 *
 * Slot Layout: 9 slots in 3x3 grid
 * GUI Size: 176x166
 */
public class DiscoveryAltarGui {

    public static ModularPanel buildUI(TileAltar tile,
                                      PosGuiData guiData,
                                      PanelSyncManager guiSyncManager,
                                      UISettings settings) {
        LogHelper.info("[DiscoveryAltarGui] Building Discovery Altar GUI at " + tile.xCoord + "," + tile.yCoord + "," + tile.zCoord + ", level: " + tile.getAltarLevel());
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

        // Create main panel - 176x166 size
        ModularPanel panel = new ModularPanel("discovery_altar");
        panel.flex()
            .size(176, 166)
            .align(Alignment.Center);

        // Add background texture (already has slots drawn on it)
        panel.child(
            AltarGuiTextures.DISCOVERY_BACKGROUND
                .asWidget()
                .pos(0, 0)
                .size(176, 166));

        // Add starlight bar rendering (over background)
        panel.child(StarlightBarWidget.create(tile, 6, 69, 165, 10));

        // Add altar slots (3x3 grid layout)
        // Row 1: slots 0, 1, 2 at y=11
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 0).slotGroup("altar_inv")).pos(62, 11));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 1).slotGroup("altar_inv")).pos(80, 11));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 2).slotGroup("altar_inv")).pos(98, 11));

        // Row 2: slots 3, 4, 5 at y=29
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 3).slotGroup("altar_inv")).pos(62, 29));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 4).slotGroup("altar_inv")).pos(80, 29));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 5).slotGroup("altar_inv")).pos(98, 29));

        // Row 3: slots 6, 7, 8 at y=47
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 6).slotGroup("altar_inv")).pos(62, 47));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 7).slotGroup("altar_inv")).pos(80, 47));
        panel.child(new ItemSlot().slot(new ModularSlot(inventory, 8).slotGroup("altar_inv")).pos(98, 47));

        // Add player inventory using SlotGroupWidget
        panel.child(SlotGroupWidget.playerInventory(7, true));

        return panel;
    }
}
