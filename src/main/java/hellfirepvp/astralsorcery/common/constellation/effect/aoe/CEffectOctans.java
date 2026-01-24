/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;
// TODO: Forge fluid system - manual review needed

import java.awt.*;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.BlockFluidBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectOctans
 * Created by HellFirePvP
 * Date: 10.01.2017 / 18:34
 */
public class CEffectOctans extends CEffectPositionListGen<GenListEntries.CounterMaxListEntry> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 12;
    public static int maxFishingGrounds = 20;

    public static int minFishTickTime = 1000;
    public static int maxFishTickTime = 5000;

    public CEffectOctans(@Nullable ILocatable origin) {
        super(origin, Constellations.octans, "octans", maxFishingGrounds, (world, pos) -> {
            Block at = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            BlockPos up = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            // 1.7.10: Check water level using metadata instead of BlockState properties
            int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            return at instanceof BlockLiquid && at.getMaterial()
                .equals(Material.water) && meta == 0 && world.isAirBlock(up.getX(), up.getY(), up.getZ());
        },
            (pos) -> new GenListEntries.CounterMaxListEntry(
                pos,
                minFishTickTime + rand.nextInt(maxFishTickTime - minFishTickTime + 1)));
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified,
        @Nullable IMinorConstellation possibleTraitEffect) {
        if (!enabled) return false;
        percStrength *= potencyMultiplier;
        if (percStrength < 1) {
            if (world.rand.nextFloat() > percStrength) return false;
        }

        if (modified.isCorrupted()) {
            boolean did = false;
            double searchRange = modified.getSize();
            double offX = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offY = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offZ = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            // 1.7.10: BlockPos.add doesn't support doubles, convert to ints
            BlockPos at = new BlockPos(
                pos.getX() + (int) Math.round(offX),
                pos.getY() + (int) Math.round(offY),
                pos.getZ() + (int) Math.round(offZ));
            Block state = world.getBlock(at.getX(), at.getY(), at.getZ());
            // 1.7.10: isReplaceable needs individual coordinates
            if ((world.isAirBlock(at.getX(), at.getY(), at.getZ())
                || state.isReplaceable(world, at.getX(), at.getY(), at.getZ()))
                && ((Math.abs(offX) > 5 || Math.abs(offZ) > 5) || offY < 0)) {
                // 1.7.10: Blocks fields are lowercase
                if (world.setBlock(at.getX(), at.getY(), at.getZ(), Blocks.water, 0, 3)) {
                    for (int i = 0; i < 3; i++) {
                        spawnFishDropsAt(at, world);
                    }
                    // 1.7.10: notifyBlocksOfNeighborChange instead of neighborChanged
                    world.notifyBlocksOfNeighborChange(at.getX(), at.getY(), at.getZ(), Blocks.water);
                    did = true;
                }
            } else if ((state instanceof BlockLiquid || state instanceof BlockFluidBase) && !state.equals(Blocks.water)
                && !state.equals(Blocks.flowing_water)) {
                    if (rand.nextBoolean()) {
                        if (world.setBlock(at.getX(), at.getY(), at.getZ(), Blocks.sand, 0, 3)) {
                            // 1.7.10: notifyBlocksOfNeighborChange instead of neighborChanged
                            world.notifyBlocksOfNeighborChange(at.getX(), at.getY(), at.getZ(), Blocks.sand);
                            did = true;
                        }
                    } else {
                        // 1.7.10: setBlockToAir needs individual coordinates
                        if (world.setBlockToAir(at.getX(), at.getY(), at.getZ())) {
                            did = true;
                        }
                    }
                }
            return did;
        }

        boolean changed = false;
        GenListEntries.CounterMaxListEntry entry = getRandomElementByChance(rand);
        if (entry != null) {
            if (MiscUtils.canEntityTickAt(world, entry.getPos())) {
                if (!verifier.isValid(world, entry.getPos())) {
                    removeElement(entry);
                    changed = true;
                } else {
                    do {
                        entry.counter++;
                        percStrength -= 0.1;
                    } while (rand.nextFloat() < percStrength);
                    changed = true;
                    if (entry.counter >= entry.maxCount) {
                        entry.maxCount = minFishTickTime + rand.nextInt(maxFishTickTime - minFishTickTime + 1);
                        entry.counter = 0;

                        spawnFishDropsAt(entry.getPos(), world);
                    }
                    PktParticleEvent ev = new PktParticleEvent(
                        PktParticleEvent.ParticleEventType.CE_WATER_FISH,
                        entry.getPos());
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, entry.getPos(), 8));
                }
            }
        }

        if (findNewPosition(world, pos, modified)) changed = true;

        return changed;
    }

    private void spawnFishDropsAt(BlockPos pos, World world) {
        Vector3 dropLoc = new Vector3(pos).add(0.5, 0.85, 0.5);
        // 1.7.10: No loot table system, drop random fishing items manually
        ItemStack fish = getRandomFishItem();
        if (fish != null) {
            EntityItem ei = ItemUtils.dropItemNaturally(world, dropLoc.getX(), dropLoc.getY(), dropLoc.getZ(), fish);
            if (ei != null) {
                ei.motionY = Math.abs(ei.motionY);
            }
        }
    }

    private ItemStack getRandomFishItem() {
        // Simple fishing loot table for 1.7.10
        float randFloat = rand.nextFloat();
        if (randFloat < 0.4) {
            // 40% chance for fish
            int fishType = rand.nextInt(3); // 0=raw fish, 1=salmon, 2=clownfish
            switch (fishType) {
                case 0:
                    return new ItemStack(Items.fish, 1, 0);
                case 1:
                    return new ItemStack(Items.fish, 1, 1);
                case 2:
                    return new ItemStack(Items.fish, 1, 2);
                default:
                    return new ItemStack(Items.fish, 1, 0);
            }
        } else if (randFloat < 0.45) {
            // 5% chance for treasure
            int treasureType = rand.nextInt(4);
            switch (treasureType) {
                case 0:
                    return new ItemStack(Items.bow);
                case 1:
                    return new ItemStack(Items.fishing_rod);
                case 2:
                    return new ItemStack(Items.book);
                case 3:
                    return new ItemStack(Items.name_tag);
                default:
                    return new ItemStack(Items.fish, 1, 0);
            }
        } else if (randFloat < 0.5) {
            // 5% chance for junk
            int junkType = rand.nextInt(3);
            switch (junkType) {
                case 0:
                    return new ItemStack(Items.bowl);
                case 1:
                    return new ItemStack(Items.stick);
                case 2:
                    return new ItemStack(Items.string);
                default:
                    return new ItemStack(Items.leather);
            }
        } else {
            // 50% chance for nothing
            return null;
        }
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectOctans.searchRange);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            12,
            1,
            32,
            "Defines the radius (in blocks) in which the ritual will search for water ");
        maxFishingGrounds = cfg.getInt(
            getKey() + "Count",
            getConfigurationSection(),
            20,
            1,
            4000,
            "Defines the amount of crops the ritual can cache at max. count");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            true,
            "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(
            getKey() + "PotencyMultiplier",
            getConfigurationSection(),
            1.0F,
            0.01F,
            100F,
            "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
        minFishTickTime = cfg.getInt(
            getKey() + "MinFishTickTime",
            getConfigurationSection(),
            100,
            20,
            Integer.MAX_VALUE,
            "Defines the minimum default tick-time until a fish may be fished by the ritual. gets reduced internally the more starlight was provided at the ritual.");
        maxFishTickTime = cfg.getInt(
            getKey() + "MaxFishTickTime",
            getConfigurationSection(),
            500,
            20,
            Integer.MAX_VALUE,
            "Defines the maximum default tick-time until a fish may be fished by the ritual. gets reduced internally the more starlight was provided at the ritual. Has to be bigger as the minimum time; if it isn't it'll be set to the minimum.");

        if (maxFishTickTime < minFishTickTime) {
            maxFishTickTime = minFishTickTime;
        }

    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            at.getX() + rand.nextFloat(),
            at.getY() + rand.nextFloat(),
            at.getZ() + rand.nextFloat());
        p.motion(0, 0.03 + rand.nextFloat() * 0.01, 0)
            .setMaxAge(5 + rand.nextInt(5));
        p.scale(0.2F)
            .setColor(Color.CYAN)
            .gravity(-0.03);
    }

}
