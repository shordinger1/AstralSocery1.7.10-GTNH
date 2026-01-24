/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import java.awt.*;
import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.block.BlockRitualLink;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectEvorsio
 * Created by HellFirePvP
 * Date: 26.07.2017 / 16:15
 */
public class CEffectEvorsio extends CEffectPositionListGen<BlockBreakAssist.BreakEntry> {

    private static BlockArray copyResizedPedestal = null;

    public static boolean enabled = true;
    public static float potencyMultiplier = 1F;
    public static int searchRange = 13;

    public CEffectEvorsio(@Nullable ILocatable origin) {
        super(
            origin,
            Constellations.evorsio,
            "evorsio",
            2,
            (w, pos) -> isAllowedToBreak(origin, w, pos),
            (pos) -> null);
    }

    private static boolean isAllowedToBreak(@Nullable ILocatable origin, World world, BlockPos pos) {
        if (!MiscUtils.isChunkLoaded(world, pos)) return false;
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        float hardness = block.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
        if (world.isAirBlock(pos.getX(), pos.getY(), pos.getZ()) || block instanceof BlockRitualLink
            || hardness < 0
            || hardness > 75) {
            return false;
        }
        if (origin != null && MiscUtils.isChunkLoaded(world, origin.getLocationPos())) {
            BlockPos originPedestal = origin.getLocationPos();
            TileRitualLink link = MiscUtils.getTileAt(
                world,
                originPedestal.getX(),
                originPedestal.getY(),
                originPedestal.getZ(),
                TileRitualLink.class);
            if (link != null && link.getLinkedTo() != null && MiscUtils.isChunkLoaded(world, link.getLinkedTo())) {
                originPedestal = link.getLinkedTo();
            }
            TileRitualPedestal pedestal = MiscUtils.getTileAt(
                world,
                originPedestal.getX(),
                originPedestal.getY(),
                originPedestal.getZ(),
                TileRitualPedestal.class);
            if (pedestal != null) {
                if (copyResizedPedestal == null) {
                    if (MultiBlockArrays.patternRitualPedestalWithLink != null) {
                        copyResizedPedestal = new BlockArray();
                        // 1.7.10: Java 7 doesn't support lambdas, iterate manually
                        for (int i = 0; i < 5; i++) {
                            for (BlockPos p : MultiBlockArrays.patternRitualPedestalWithLink.getPattern()
                                .keySet()) {
                                BlockPos offset = new BlockPos(p.getX(), p.getY() + i, p.getZ());
                                BlockArray.BlockInformation info = MultiBlockArrays.patternRitualPedestalWithLink
                                    .getPattern()
                                    .get(p);
                                copyResizedPedestal.addBlock(offset, info.state);
                            }
                        }
                    }
                }
                if (copyResizedPedestal != null) {
                    // 1.7.10: BlockPos doesn't have subtract, calculate manually
                    BlockPos diff = new BlockPos(
                        pos.getX() - originPedestal.getX(),
                        pos.getY() - originPedestal.getY(),
                        pos.getZ() - originPedestal.getZ());
                    if (copyResizedPedestal.hasBlockAt(diff)) {
                        return false;
                    }
                }
                return true;
            }
            // Critical state: Has a link leading into a nonexistent pedestal OR
            // is an unlinked anchor casting a ritual...
            return true;
        }
        return true;
    }

    @Override
    public BlockBreakAssist.BreakEntry newElement(World world, BlockPos at) {
        return new BlockBreakAssist.BreakEntry(0F, world, at, world.getBlock(at.getX(), at.getY(), at.getZ()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
        boolean extendedEffects) {
        if (rand.nextInt(4) == 0) {
            Vector3 at = new Vector3(
                pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                pos.getY() + rand.nextFloat() * 2 + 0.5,
                pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            for (int i = 0; i < 15; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                p.gravity(0.004);
                p.scale(0.25F)
                    .setMaxAge(35 + rand.nextInt(10));
                Vector3 mot = new Vector3();
                MiscUtils.applyRandomOffset(mot, rand, 0.01F * rand.nextFloat() + 0.01F);
                p.motion(mot.getX(), mot.getY(), mot.getZ());
                switch (rand.nextInt(3)) {
                    case 0:
                    case 1:
                        p.setColor(Constellations.evorsio.getConstellationColor());
                        break;
                    case 2:
                        p.setColor(Constellations.armara.getConstellationColor());
                        break;
                    default:
                        break;
                }
            }
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

        if (modified.isCorrupted()) {
            double searchRange = modified.getSize();
            double offX = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offY = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offZ = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            // 1.7.10: BlockPos.add only takes ints, convert doubles to ints
            BlockPos at = new BlockPos(
                pos.getX() + (int) Math.round(offX),
                pos.getY() + (int) Math.round(offY),
                pos.getZ() + (int) Math.round(offZ));
            if (!world.isAirBlock(at.getX(), at.getY(), at.getZ()) && !world.getBlock(at.getX(), at.getY(), at.getZ())
                .isReplaceable(world, at.getX(), at.getY(), at.getZ())) {
                return false;
            }
            // 1.7.10: Blocks fields are lowercase
            Block toSet = rand.nextBoolean() ? Blocks.dirt : Blocks.stone;
            if (rand.nextInt(20) == 0) {
                ItemStack randOre = OreTypes.RITUAL_MINERALIS.getNonWeightedOre(rand);
                if (!(randOre == null || randOre.stackSize <= 0)) {
                    // 1.7.10: Create block from ItemStack instead of createBlockState
                    Block oreBlock = Block.getBlockFromItem(randOre.getItem());
                    if (oreBlock != null) {
                        toSet = oreBlock;
                    }
                }
            }
            TileRitualLink link = MiscUtils.getTileAt(world, pos.getX(), pos.getY(), pos.getZ(), TileRitualLink.class);
            if (link != null) {
                if (!at.equals(pos)) {
                    // 1.7.10: world.setBlock signature is different
                    int meta = world.getBlockMetadata(at.getX(), at.getY(), at.getZ());
                    int oldMeta = meta != -1 ? meta : 0;
                    // 1.7.10: No playEvent method in this form, just set the block
                    world.setBlock(at.getX(), at.getY(), at.getZ(), toSet, meta, 2);
                    return true;
                }
            } else {
                TileRitualPedestal ped = MiscUtils
                    .getTileAt(world, pos.getX(), pos.getY(), pos.getZ(), TileRitualPedestal.class);
                if (ped != null) {
                    if (at.getZ() == pos.getZ() && at.getX() == pos.getX()) {
                        return false;
                    }
                    BlockArray ba = new BlockArray();
                    if (MultiBlockArrays.patternRitualPedestalWithLink != null) {
                        // 1.7.10: Java 7 doesn't support lambdas, iterate manually
                        for (int i = 0; i < 5; i++) {
                            for (BlockPos p : MultiBlockArrays.patternRitualPedestalWithLink.getPattern()
                                .keySet()) {
                                BlockPos offset = new BlockPos(
                                    pos.getX() + p.getX(),
                                    pos.getY() + p.getY() + i,
                                    pos.getZ() + p.getZ());
                                BlockArray.BlockInformation info = MultiBlockArrays.patternRitualPedestalWithLink
                                    .getPattern()
                                    .get(p);
                                ba.addBlock(offset, info.state);
                            }
                        }
                        if (!ba.hasBlockAt(at)) {
                            // 1.7.10: world.setBlock signature is different
                            int meta = world.getBlockMetadata(at.getX(), at.getY(), at.getZ());
                            world.setBlock(at.getX(), at.getY(), at.getZ(), toSet, meta, 2);
                            return true;
                        }
                    }
                }
            }

        } else {
            if (world instanceof WorldServer && findNewPosition(world, pos, modified)) {
                BlockBreakAssist.BreakEntry be = getRandomElement(world.rand);
                if (be != null) {
                    removeElement(be);

                    boolean broken = false;
                    BlockDropCaptureAssist.startCapturing();
                    try {
                        BlockPos bePos = be.getPos();
                        broken = MiscUtils.breakBlockWithoutPlayer(
                            (WorldServer) world,
                            bePos,
                            world.getBlock(bePos.getX(), bePos.getY(), bePos.getZ()),
                            true,
                            true,
                            true);
                    } finally {
                        ArrayList<ItemStack> captured = BlockDropCaptureAssist.getCapturedStacksAndStop();
                        for (ItemStack stack : captured) {
                            ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, stack);
                        }
                    }
                    if (broken) {
                        PktParticleEvent ev = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CE_BREAK_BLOCK,
                            be.getPos());
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, be.getPos(), 16));
                    }
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playBreakEffects(PktParticleEvent pktParticleEvent) {
        Vector3 at = pktParticleEvent.getVec()
            .add(0.5, 0.5, 0.5);
        for (int i = 0; i < 15; i++) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            p.gravity(0.004);
            p.scale(0.25F)
                .setMaxAge(35 + rand.nextInt(10));
            Vector3 mot = new Vector3();
            MiscUtils.applyRandomOffset(mot, rand, 0.01F * rand.nextFloat() + 0.01F);
            p.motion(mot.getX(), mot.getY(), mot.getZ());
            switch (rand.nextInt(3)) {
                case 0:
                    p.setColor(Color.WHITE);
                    break;
                case 1:
                    p.setColor(Constellations.evorsio.getConstellationColor());
                    break;
                case 2:
                    p.setColor(Constellations.armara.getConstellationColor());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectEvorsio.searchRange);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            searchRange,
            1,
            32,
            "Defines the radius (in blocks) in which the ritual will search for blocks to break.");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            enabled,
            "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(
            getKey() + "PotencyMultiplier",
            getConfigurationSection(),
            potencyMultiplier,
            0.01F,
            100F,
            "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
