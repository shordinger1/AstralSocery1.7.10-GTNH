/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import hellfirepvp.astralsorcery.client.gui.modularui.TelescopeGuiFactory;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileTelescope - Telescope TileEntity (1.7.10)
 * <p>
 * Stores telescope rotation state and provides GUI for constellation viewing.
 */
public class TileTelescope extends TileEntitySynchronized implements IGuiHolder<PosGuiData> {

    private TelescopeRotation rotation = TelescopeRotation.N;

    public TileTelescope() {
        super();
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        LogHelper.info("[TileTelescope] Building Telescope GUI at " + xCoord + "," + yCoord + "," + zCoord);
        return TelescopeGuiFactory.createTelescopeUI(this, guiData, guiSyncManager, settings);
    }

    public TelescopeRotation getRotation() {
        return rotation;
    }

    public void setRotation(TelescopeRotation rotation) {
        this.rotation = rotation;
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.rotation = TelescopeRotation.values()[compound.getInteger("rotation")];
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setInteger("rotation", rotation.ordinal());
    }

    /**
     * Telescope rotation enum
     * Represents 8 cardinal/intercardinal directions
     */
    public static enum TelescopeRotation {

        N,
        N_E,
        E,
        S_E,
        S,
        S_W,
        W,
        N_W;

        public TelescopeRotation nextClockWise() {
            return values()[(ordinal() + 1) % values().length];
        }

        public TelescopeRotation nextCounterClockWise() {
            return values()[(ordinal() + 7) % values().length];
        }

    }

}
