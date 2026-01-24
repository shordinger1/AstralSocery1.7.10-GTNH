/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import java.awt.*;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectCorruptedCelestialCrystal;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:42
 */
public class TESRCollectorCrystal extends TileEntitySpecialRenderer implements IItemRenderer {

    private static final BindableResource texWhite = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_white");
    private static final BindableResource texBlue = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_blue");
    private static final BindableResource texRed = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_red");

    private static int dlCrystal = -1;

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileCollectorCrystal)) return;
        TileCollectorCrystal tile = (TileCollectorCrystal) te;
        renderTileCollectorCrystal(tile, x, y, z, partialTicks);
    }

    private void renderTileCollectorCrystal(TileCollectorCrystal te, double x, double y, double z, float partialTicks) {
        UUID playerUUID = te.getPlayerReference();

        BlockCollectorCrystalBase.CollectorCrystalType type = te.getType();
        if (te.doesSeeSky()) {
            long sBase = 1553015L;
            sBase ^= (long) te.xCoord;
            sBase ^= (long) te.yCoord;
            sBase ^= (long) te.zCoord;
            Color c = type == null ? BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor
                : type.displayColor;
            if (te.getType() == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL && playerUUID != null
                && MiscUtils.contains(
                    PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerUUID),
                    pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                c = Color.RED;
            }
            if (te.isEnhanced()) {
                c = te.getConstellation() != null ? te.getConstellation()
                    .getConstellationColor() : c;
                RenderingUtils.renderLightRayEffects(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    c,
                    sBase,
                    ClientScheduler.getClientTick(),
                    20,
                    1.4F,
                    50,
                    25);
                RenderingUtils.renderLightRayEffects(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    Color.WHITE,
                    sBase,
                    ClientScheduler.getClientTick(),
                    40,
                    2,
                    15,
                    15);
            } else {
                RenderingUtils.renderLightRayEffects(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    c,
                    sBase,
                    ClientScheduler.getClientTick(),
                    20,
                    50,
                    25);
            }
        }

        renderCrystal(playerUUID, type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL, true);
    }

    public static void renderCrystal(@Nullable UUID playerUUID, boolean isCelestial, boolean bounce) {
        if (bounce) {
            int t = (int) (Minecraft.getMinecraft().theWorld.getWorldTime() & 255);
            float perc = (256 - t) / 256F;
            perc = WrapMathHelper.cos((float) (perc * 2 * Math.PI));
        }
        TextureHelper.refreshTextureBindState();
        RenderHelper.disableStandardItemLighting();
        if (isCelestial) {
            if (playerUUID != null && MiscUtils.contains(
                PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerUUID),
                pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                renderTile(texRed, playerUUID, isCelestial);
            } else {
                renderTile(texBlue, playerUUID, isCelestial);
            }
        } else {
            renderTile(texWhite, playerUUID, isCelestial);
        }
        RenderHelper.enableStandardItemLighting();
    }

    private static void renderTile(BindableResource tex, @Nullable UUID playerUUID, boolean isCelestial) {
        tex.bind();
        if (dlCrystal == -1) {
            dlCrystal = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(dlCrystal, GL11.GL_COMPILE);
            OBJModelLibrary.bigCrystal.renderAll(true);
            GL11.glEndList();
        }
        GL11.glCallList(dlCrystal);
    }

    @Override
    public void render(ItemStack stack) {
        RenderHelper.disableStandardItemLighting();
        BlockCollectorCrystalBase.CollectorCrystalType type = ItemCollectorCrystal.getType(stack);
        switch (type) {
            case ROCK_CRYSTAL:
                renderTile(texWhite, null, false);
                break;
            case CELESTIAL_CRYSTAL:
                renderTile(texBlue, null, true);
                break;
            default:
                break;
        }
        RenderHelper.enableStandardItemLighting();
    }

}
