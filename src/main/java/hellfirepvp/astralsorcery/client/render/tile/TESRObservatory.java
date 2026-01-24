/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.client.models.base.ASobservatory;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.entities.EntityObservatoryHelper;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRObservatory
 * Created by HellFirePvP
 * Date: 26.05.2018 / 16:04
 */
public class TESRObservatory extends TileEntitySpecialRenderer {

    private static final ASobservatory modelTelescope = new ASobservatory();
    private static final BindableResource texTelescope = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "base/observatory");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileObservatory)) return;
        TileObservatory tile = (TileObservatory) te;
        renderTileObservatory(tile, x, y, z, partialTicks);
    }

    private void renderTileObservatory(TileObservatory te, double x, double y, double z, float partialTicks) {
        if (new Vector3(x, y, z).length() >= 64) {
            return;
        }

        Entity ridden;
        EntityPlayer player;
        if ((player = Minecraft.getMinecraft().thePlayer) != null
            && (ridden = Minecraft.getMinecraft().thePlayer.ridingEntity) != null
            && ridden instanceof EntityObservatoryHelper
            && ((EntityObservatoryHelper) ridden).tryGetObservatory() != null) {
            ((EntityObservatoryHelper) ridden).applyObservatoryRotationsFrom(te, player);
        }

        float prevYaw = te.prevObservatoryYaw;
        float yaw = te.observatoryYaw;
        float prevPitch = te.prevObservatoryPitch;
        float pitch = te.observatoryPitch;

        float iYaw = RenderingUtils.interpolateRotation(prevYaw + 180, yaw + 180, partialTicks);
        float iPitch = RenderingUtils.interpolateRotation(prevPitch, pitch, partialTicks);

        RenderHelper.disableStandardItemLighting();

        renderModel(iYaw, iPitch);

        TextureHelper.refreshTextureBindState();
    }

    private void renderModel(float iYaw, float iPitch) {
        texTelescope.bind();
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        modelTelescope.render(null, iYaw, iPitch, 0, 0, 0, 1);
    }
}
