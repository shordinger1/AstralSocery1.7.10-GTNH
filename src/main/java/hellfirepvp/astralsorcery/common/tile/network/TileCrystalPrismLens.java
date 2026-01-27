/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.block.network.BlockPrism;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCrystalPrismLens
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:15
 */
public class TileCrystalPrismLens extends TileCrystalLens {

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (getWorld().isRemote && getLinkedPositions().size() > 0) {
            playPrismEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playPrismEffects() {
        Entity rView = Minecraft.getMinecraft().renderViewEntity;
        if (rView == null) rView = Minecraft.getMinecraft().thePlayer;
        // 1.7.10: getDistanceSq takes individual x, y, z coordinates
        if (rView.getDistanceSq(getPos().getX(), getPos().getY(), getPos().getZ()) > Config.maxEffectRenderDistanceSq)
            return;
        Vector3 pos = new Vector3(this).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        particle.motion(
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1));
        particle.scale(0.2F);
    }

    @Override
    public EnumFacing getPlacedAgainst() {
        // 1.7.10: Use metadata instead of blockstate properties
        Block block = getWorld().getBlock(getPos().getX(), getPos().getY(), getPos().getZ());
        if (!(block instanceof BlockPrism)) {
            return EnumFacing.DOWN;
        }
        // Metadata corresponds to: 0=DOWN, 1=UP, 2=NORTH, 3=SOUTH, 4=WEST, 5=EAST
        int meta = getWorld().getBlockMetadata(getPos().getX(), getPos().getY(), getPos().getZ());
        switch (meta) {
            case 0:
                return EnumFacing.DOWN;
            case 1:
                return EnumFacing.UP;
            case 2:
                return EnumFacing.NORTH;
            case 3:
                return EnumFacing.SOUTH;
            case 4:
                return EnumFacing.WEST;
            case 5:
                return EnumFacing.EAST;
            default:
                return EnumFacing.DOWN;
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockprism.name";
    }

    @Override
    @Nonnull
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new CrystalPrismTransmissionNode(at, getCrystalProperties());
    }
}
