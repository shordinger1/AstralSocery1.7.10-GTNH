/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileObservatory - Observatory
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import hellfirepvp.astralsorcery.client.gui.modularui.ObservatoryGuiFactory;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * TileObservatory - Observatory TileEntity (1.7.10)
 * <p>
 * Stores player viewing angles and provides full-screen constellation viewing GUI
 */
public class TileObservatory extends TileEntityTick implements IGuiHolder<PosGuiData> {

    // Viewing angles for the observatory
    public float observatoryYaw = 0F;
    public float prevObservatoryYaw = 0F;
    public float observatoryPitch = -45F;
    public float prevObservatoryPitch = -45F;

    // TODO: Entity helper for camera positioning (from 1.12.2)
    // private UUID entityHelperRef;

    public TileObservatory() {
        super();
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        return ObservatoryGuiFactory.createObservatoryUI(this, guiData, guiSyncManager, settings);
    }

    /**
     * Update the viewing angles (called from GUI)
     */
    public void updatePitchYaw(float pitch, float prevPitch, float yaw, float prevYaw) {
        this.observatoryPitch = pitch;
        this.prevObservatoryPitch = prevPitch;
        this.observatoryYaw = yaw;
        this.prevObservatoryYaw = prevYaw;
        markForUpdate();
    }

    /**
     * Check if observatory can see the sky
     */
    public boolean isUsable() {
        // TODO: Implement sky visibility check
        // Check 3x3 area around observatory
        return true;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setFloat("oYaw", this.observatoryYaw);
        compound.setFloat("oPitch", this.observatoryPitch);
        compound.setFloat("oYawPrev", this.prevObservatoryYaw);
        compound.setFloat("oPitchPrev", this.prevObservatoryPitch);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.observatoryYaw = compound.getFloat("oYaw");
        this.observatoryPitch = compound.getFloat("oPitch");
        this.prevObservatoryYaw = compound.getFloat("oYawPrev");
        this.prevObservatoryPitch = compound.getFloat("oPitchPrev");
    }
}
