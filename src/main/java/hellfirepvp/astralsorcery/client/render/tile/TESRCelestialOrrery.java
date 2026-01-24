/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileCelestialOrrery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRCelestialOrrery
 * Created by HellFirePvP
 * Date: 15.02.2017 / 22:49
 */
public class TESRCelestialOrrery extends TileEntitySpecialRenderer {

    public static final BindableResource texSmoke = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "smoke");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileCelestialOrrery)) return;
        TileCelestialOrrery tile = (TileCelestialOrrery) te;
        renderTileCelestialOrrery(tile, x, y, z, partialTicks);
    }

    private void renderTileCelestialOrrery(TileCelestialOrrery te, double x, double y, double z, float partialTicks) {

    }

}
