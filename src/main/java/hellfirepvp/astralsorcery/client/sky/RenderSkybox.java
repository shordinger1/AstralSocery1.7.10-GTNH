/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.client.IRenderHandler;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.common.data.config.Config;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderSkybox
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:44
 */
public class RenderSkybox extends IRenderHandler {

    private static boolean inRender = false;

    private static final RenderAstralSkybox astralSky = new RenderAstralSkybox();

    private final IRenderHandler otherSkyRenderer;

    public RenderSkybox(IRenderHandler skyRenderer) {
        this.otherSkyRenderer = skyRenderer;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if (!astralSky.isInitialized() && !AssetLibrary.reloading) {
            astralSky.setInitialized(
                world.getWorldInfo()
                    .getSeed());
        }

        if (inRender) return;

        inRender = true;

        if (Config.weakSkyRendersWhitelist.contains(world.provider.dimensionId)) {
            if (otherSkyRenderer != null) {
                otherSkyRenderer.render(partialTicks, world, mc);
            } else {
                RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
                // Make vanilla guess
                if (world.provider.dimensionId == 1) {
                    // 1.7.10: renderSkyEnd() doesn't exist, skip rendering
                } else if (world.provider.dimensionId == 0) { // 1.7.10: isSurfaceWorld() doesn't exist, check
                                                              // dimensionId == 0
                    IRenderHandler render = world.provider.getSkyRenderer();
                    world.provider.setSkyRenderer(null);

                    // 1.7.10: renderSky() takes only 1 parameter (partialTicks)
                    rg.renderSky(partialTicks);

                    world.provider.setSkyRenderer(render);
                }
            }
            RenderAstralSkybox.renderConstellationsWrapped(world, partialTicks);
        } else {
            astralSky.render(partialTicks, world, mc);
        }

        inRender = false;
    }

    public static void resetAstralSkybox() {
        astralSky.refreshRender();
    }

    static {
        RenderDefaultSkybox.setupDefaultSkybox();
    }

}
