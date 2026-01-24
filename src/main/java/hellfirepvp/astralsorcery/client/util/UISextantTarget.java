/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: UISextantTarget
 * Created by HellFirePvP
 * Date: 23.04.2018 / 17:40
 */
public class UISextantTarget {

    public static void renderTargets(float pTicks) {
        EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
        World w = Minecraft.getMinecraft().theWorld;
        if (pl == null || w == null) {
            return;
        }
        ItemStack held = pl.getHeldItem();
        Tuple<BlockPos, Integer> info;
        SextantFinder.TargetObject target;

        if (!(held == null || held.stackSize <= 0) && held.getItem() instanceof ItemSextant
            && (info = ItemSextant.getCurrentTargetInformation(held)) != null
            && (target = ItemSextant.getTarget(held)) != null
            && target.isSelectable(held, ResearchManager.clientProgress)
            && info.value == w.provider.dimensionId) {
            renderStar(info.key, target, pTicks);
        }

        held = pl.getHeldItem();
        if (!(held == null || held.stackSize <= 0) && held.getItem() instanceof ItemSextant
            && (info = ItemSextant.getCurrentTargetInformation(held)) != null
            && (target = ItemSextant.getTarget(held)) != null
            && target.isSelectable(held, ResearchManager.clientProgress)
            && info.value == w.provider.dimensionId) {
            renderStar(info.key, target, pTicks);
        }
    }

    private static void renderStar(BlockPos actPos, SextantFinder.TargetObject target, float pTicks) {
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }
        Entity e = Minecraft.getMinecraft().renderViewEntity;
        if (e == null) {
            e = Minecraft.getMinecraft().thePlayer;
        }
        if (e == null) {
            return;
        }
        float dayMultiplier = ConstellationSkyHandler.getInstance()
            .getCurrentDaytimeDistribution(Minecraft.getMinecraft().theWorld);
        if (dayMultiplier <= 0.1F) {
            return;
        }
        // Flattened distance
        Vector3 dir = new Vector3(actPos).setY(0)
            .subtract(
                Vector3.atEntityCenter(e)
                    .setY(0));
        // length, yaw, pitch
        Vector3 polar = dir.clone()
            .copyToPolar();
        if (polar.getX() <= 20D) {
            return;
        }
        float proximity = polar.getX() >= 350D ? 1F : WrapMathHelper.sqrt(((float) polar.getX()) / 350F);

        double yaw = 180D - polar.getZ();
        double pitch = polar.getX() >= 350D ? -20D : Math.min(-20D, -20D - (70D - (70D * (polar.getX() / 350D))));
        // 1.7.10: Vec3.fromPitchYaw() doesn't exist, calculate direction vector manually
        float pitchRad = (float) Math.toRadians(pitch);
        float yawRad = (float) Math.toRadians(yaw);
        double dx = -Math.cos(pitchRad) * Math.sin(yawRad);
        double dy = -Math.sin(pitchRad);
        double dz = Math.cos(pitchRad) * Math.cos(yawRad);
        Vector3 act = new Vector3(dx, dy, dz).normalize()
            .multiply(200);
        act.add(Vector3.atEntityCenter(e));

        Blending.DEFAULT.applyStateManager();
        float alpha = RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pTicks, 16);
        alpha = (0.4F + 0.6F * alpha) * dayMultiplier * proximity;
        Color c = new Color(target.getColorTheme(), false);
        RenderAstralSkybox.TEX_STAR_1.bind();
        RenderingUtils.renderFacingFullQuad(act.getX(), act.getY(), act.getZ(), pTicks, 7F, 0);
        TextureHelper.refreshTextureBindState();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

}
