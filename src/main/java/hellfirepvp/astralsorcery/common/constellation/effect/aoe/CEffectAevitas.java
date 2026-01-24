/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectAevitas
 * Created by HellFirePvP
 * Date: 29.11.2016 / 01:38
 */
public class CEffectAevitas extends CEffectPositionListGen<CropHelper.GrowablePlant> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 16;
    public static int maxCropCount = 200;
    public static int potionAmplifier = 1;

    public CEffectAevitas(@Nullable ILocatable origin) {
        super(origin, Constellations.aevitas, "aevitas", maxCropCount, new CEffectPositionListGen.Verifier() {

            @Override
            public boolean isValid(World world, BlockPos testPos) {
                return CropHelper.wrapPlant(world, testPos) != null;
            }
        }, new com.google.common.base.Function<BlockPos, CropHelper.GrowablePlant>() {

            @Override
            public CropHelper.GrowablePlant apply(BlockPos input) {
                return CropHelper.wrapPlant(null, input);
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
        boolean extendedEffects) {
        if (rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                pos.getY() + rand.nextFloat() * 2 + 0.5,
                pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion(0, 0, 0)
                .gravity(0.05);
            p.scale(0.45F)
                .setColor(new Color(63, 255, 63))
                .setMaxAge(35);
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified,
        @Nullable IMinorConstellation possibleTraitEffect) {
        if (!enabled) return false;
        percStrength *= potencyMultiplier;
        if (percStrength < 1) {
            if (world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;
        CropHelper.GrowablePlant plant = getRandomElementByChance(rand);
        if (plant != null) {
            if (MiscUtils.isChunkLoaded(world, new ChunkPos(plant.getPos()))) {
                if (modified.isCorrupted()) {
                    if (world instanceof WorldServer) {
                        BlockPos p = plant.getPos();
                        if (MiscUtils.breakBlockWithoutPlayer(((WorldServer) world), p)) {
                            changed = true;
                        }
                    } else {
                        BlockPos p = plant.getPos();
                        if (world.setBlockToAir(p.getX(), p.getY(), p.getZ())) {
                            changed = true;
                        }
                    }
                } else {
                    if (!plant.isValid(world, true)) {
                        removeElement(plant);
                        changed = true;
                    } else {
                        if (plant.tryGrow(world, rand)) {
                            PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                                plant.getPos());
                            PacketChannel.CHANNEL
                                .sendToAllAround(ev, PacketChannel.pointFromPos(world, plant.getPos(), 8));
                            changed = true;
                        }
                    }
                }
            }
        }

        if (findNewPosition(world, pos, modified)) changed = true;
        if (findNewPosition(world, pos, modified)) changed = true;

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1)
                .offset(pos.getX(), pos.getY(), pos.getZ())
                .expand(modified.getSize(), modified.getSize(), modified.getSize()));
        for (EntityLivingBase entity : entities) {
            if (!entity.isDead) {
                if (modified.isCorrupted()) {
                    entity.addPotionEffect(new PotionEffect(RegistryPotions.potionBleed.id, 200, potionAmplifier * 2));
                    entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, potionAmplifier * 3));
                    entity.addPotionEffect(new PotionEffect(Potion.hunger.id, 200, potionAmplifier * 4));
                    entity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, potionAmplifier * 2));
                } else {
                    entity.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, potionAmplifier));
                }
            }
        }

        return changed;
    }

    @Override
    public CropHelper.GrowablePlant newElement(World world, BlockPos at) {
        return CropHelper.wrapPlant(world, at);
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectAevitas.searchRange);
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        for (int i = 0; i < 8; i++) {
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(at.getX() + rand.nextFloat(), at.getY() + 0.2, at.getZ() + rand.nextFloat());
            p.motion(0, 0.005 + rand.nextFloat() * 0.01, 0);
            p.scale(0.2F)
                .setColor(Color.GREEN);
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            16,
            1,
            32,
            "Defines the radius (in blocks) in which the ritual will search for valid crops.");
        maxCropCount = cfg.getInt(
            getKey() + "Count",
            getConfigurationSection(),
            200,
            1,
            4000,
            "Defines the amount of crops the ritual can cache at max. count");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            true,
            "Set to false to disable this ConstellationEffect.");
        potionAmplifier = cfg.getInt(
            getKey() + "RegenerationAmplifier",
            getConfigurationSection(),
            1,
            0,
            Short.MAX_VALUE,
            "Set the amplifier for the regeneration potion effect.");
        potencyMultiplier = cfg.getFloat(
            getKey() + "PotencyMultiplier",
            getConfigurationSection(),
            1.0F,
            0.01F,
            100F,
            "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
