/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import java.util.Random;

import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileNetwork
 * Created by HellFirePvP
 * Date: 03.08.2016 / 18:12
 */
public abstract class TileNetwork extends TileEntityTick {

    protected static final Random rand = new Random();
    private boolean isNetworkInformed = false;

    public void updateEntity() {
        super.updateEntity();

        if (this.worldObj.isRemote) {
            return;
        }

        if (!isNetworkInformed && !TransmissionNetworkHelper.isTileInNetwork(this)) {
            TransmissionNetworkHelper.informNetworkTilePlacement(this);
            isNetworkInformed = true;
        }
    }

    @Override
    protected void onFirstTick() {

    }

    public void onBreak() {
        if (this.worldObj.isRemote) return;
        TransmissionNetworkHelper.informNetworkTileRemoval(this);
        isNetworkInformed = false;
    }

}
