/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.awt.*;
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystalBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockCelestialCrystals;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialCrystals
 * Created by HellFirePvP
 * Date: 15.09.2016 / 00:13
 */
public class TileCelestialCrystals extends TileSkybound {
    // Just in case you wonder. i do have a reason to control growth in the TileEntity other than just in the block
    // itself.

    private static final Random rand = new Random();

    // shouldRefresh not available in 1.7.10 - removed

    public int getGrowth() {
        // 1.7.10: use getWorld() and metadata instead of getValue()
        Block block = getWorld().getBlock(getPos().getX(), getPos().getY(), getPos().getZ());
        if (!(block instanceof BlockCelestialCrystals)) return 0;
        // In 1.7.10, stage is stored as metadata
        return getWorld().getBlockMetadata(getPos().getX(), getPos().getY(), getPos().getZ());
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!getWorld().isRemote) {
            double mul = 1;
            BlockPos downPos = getPos().down();
            Block downBlock = getWorld().getBlock(downPos.getX(), downPos.getY(), downPos.getZ());
            int downMeta = getWorld().getBlockMetadata(downPos.getX(), downPos.getY(), downPos.getZ());
            // 1.7.10: Check metadata instead of property
            if (downBlock == BlocksAS.customOre && downMeta == BlockCustomOre.OreType.STARMETAL.ordinal()) {
                mul *= 0.3;

                if (rand.nextInt(300) == 0) {
                    // 1.7.10: Use Blocks.iron_ore instead of IRON_ORE
                    getWorld().setBlock(downPos.getX(), downPos.getY(), downPos.getZ(), Blocks.iron_ore, 0, 3);
                }
            }
            tryGrowth(mul);
        } else {
            BlockPos downPos = getPos().down();
            Block downBlock = getWorld().getBlock(downPos.getX(), downPos.getY(), downPos.getZ());
            int downMeta = getWorld().getBlockMetadata(downPos.getX(), downPos.getY(), downPos.getZ());
            // 1.7.10: Check metadata instead of property
            if (downBlock == BlocksAS.customOre && downMeta == BlockCustomOre.OreType.STARMETAL.ordinal()) {
                playStarmetalOreParticles();
            }
            int stage = getGrowth();
            if (stage == 4) {
                playHarvestEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playHarvestEffects() {
        if (rand.nextInt(15) == 0) {
            // 1.7.10: use getPos() instead of pos
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                getPos().getX() + 0.3 + rand.nextFloat() * 0.4,
                getPos().getY() + rand.nextFloat() * 0.1,
                getPos().getZ() + 0.3 + rand.nextFloat() * 0.4);
            p.motion(0, rand.nextFloat() * 0.05, 0);
            p.setColor(Color.WHITE);
            p.scale(0.2F);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playStarmetalOreParticles() {
        if (rand.nextInt(5) == 0) {
            // 1.7.10: use getPos() instead of pos
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                getPos().getX() + rand.nextFloat(),
                getPos().down()
                    .getY() + rand.nextFloat(),
                getPos().getZ() + rand.nextFloat());
            p.motion(0, rand.nextFloat() * 0.05, 0);
            p.scale(0.2F);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {
        BlockPos at = event.getVec()
            .toBlockPos();
        int id = 19;
        id ^= at.getX();
        id ^= at.getY();
        id ^= at.getZ();
        EffectHandler.getInstance()
            .registerFX(new EntityFXCrystalBurst(id, at.getX() + 0.5, at.getY() + 0.2, at.getZ() + 0.5, 1.5F));
    }

    @Override
    protected void onFirstTick() {}

    public void grow() {
        // 1.7.10: use getWorld() and metadata instead of getValue()
        int metadata = getWorld().getBlockMetadata(getPos().getX(), getPos().getY(), getPos().getZ());
        if (metadata < 4) {
            // Increment stage by setting new metadata
            getWorld().setBlockMetadataWithNotify(getPos().getX(), getPos().getY(), getPos().getZ(), metadata + 1, 3);
        }
    }

    public void tryGrowth(double mul) {
        int r = 24000;
        // 1.7.10: use getWorld() instead of world
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(getWorld());
        if (doesSeeSky() && handle != null) {
            double dstr = ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(getWorld());
            if (dstr > 0) {
                // 1.7.10: Use getWorld().provider.dimensionId instead of world.provider.dimensionId
                Collection<IConstellation> activeConstellations = ((DataActiveCelestials) SyncDataHolder
                    .getDataClient(SyncDataHolder.DATA_CONSTELLATIONS))
                        .getActiveConstellations(getWorld().provider.dimensionId);
                if (activeConstellations != null) {
                    r = 9500; // If this dim has sky handling active.
                }
                r *= (0.5 + ((1 - dstr) * 0.5));
            }
        }
        r *= Math.abs(mul);

        if (getWorld().rand.nextInt(Math.max(r, 6000)) == 0) {
            grow();
        }
    }
}
