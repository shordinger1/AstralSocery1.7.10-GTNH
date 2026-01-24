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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionList;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectMineralis
 * Created by HellFirePvP
 * Date: 07.01.2017 / 19:12
 */
public class CEffectMineralis extends CEffectPositionList {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 8;
    public static int maxCount = 2;

    private static List<Block> replaceableStates = null;
    private static List<String> replaceableStatesSerialized = new ArrayList<>();

    public CEffectMineralis(@Nullable ILocatable origin) {
        super(origin, Constellations.mineralis, "mineralis", maxCount, new CEffectPositionListGen.Verifier() {

            @Override
            public boolean isValid(World world, BlockPos testPos) {
                if (replaceableStates == null) {
                    resolveReplaceableStates();
                }
                return MiscUtils
                    .getMatchingState(replaceableStates, world.getBlock(testPos.getX(), testPos.getY(), testPos.getZ()))
                    != null;
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
                .gravity(-0.01);
            p.scale(0.45F)
                .setColor(new Color(188, 188, 188))
                .setMaxAge(55);
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
        if (modified.isCorrupted()) {
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1)
                    .offset(pos.getX(), pos.getY(), pos.getZ())
                    .expand(modified.getSize(), modified.getSize(), modified.getSize()));
            for (EntityLivingBase entity : entities) {
                if (entity instanceof EntityFlying || entity.isDead) continue;
                BlockPos center = new BlockPos(entity).down();
                for (int xx = -1; xx <= 1; xx++) {
                    for (int zz = -1; zz <= 1; zz++) {
                        BlockPos at = center.add(xx, 0, zz);
                        Block state = world.getBlock(at.getX(), at.getY(), at.getZ());
                        if (world.isAirBlock(at.getX(), at.getY(), at.getZ())
                            || state.isReplaceable(world, at.getX(), at.getY(), at.getZ())) {
                            if (rand.nextInt(25) == 0) {
                                ItemStack blockStack = OreTypes.RITUAL_MINERALIS.getRandomOre(rand);
                                if (!(blockStack == null || blockStack.stackSize <= 0)) {
                                    state = ItemUtils.createBlockState(blockStack);
                                    if (state != null) {
                                        if (world.setBlock(at.getX(), at.getY(), at.getZ(), state, 0, 3)) {
                                            changed = true;
                                        }
                                    } else {
                                        if (world.setBlock(at.getX(), at.getY(), at.getZ(), Blocks.stone, 0, 3)) {
                                            changed = true;
                                        }
                                    }
                                } else {
                                    if (world.setBlock(at.getX(), at.getY(), at.getZ(), Blocks.stone, 0, 3)) {
                                        changed = true;
                                    }
                                }
                            } else {
                                if (world.setBlock(at.getX(), at.getY(), at.getZ(), Blocks.stone, 0, 3)) {
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        GenListEntries.SimpleBlockPosEntry entry = getRandomElementByChance(rand);
        if (entry != null) {
            BlockPos sel = entry.getPos();
            if (MiscUtils.isChunkLoaded(world, new ChunkPos(sel))) {
                if (verifier.isValid(world, sel)) {
                    ItemStack blockStack = OreTypes.RITUAL_MINERALIS.getRandomOre(rand);
                    if (rand.nextInt(2_000_000) == 0)
                        blockStack = new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal());
                    if (!(blockStack == null || blockStack.stackSize <= 0)) {
                        Block state = ItemUtils.createBlockState(blockStack);
                        if (state != null) {
                            world.setBlock(sel.getX(), sel.getY(), sel.getZ(), state, 0, 3);
                        }
                    }
                } else {
                    removeElement(entry);
                    changed = true;
                }
            }
        }

        if (findNewPosition(world, pos, modified)) changed = true;

        return changed;
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectMineralis.searchRange);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            searchRange,
            1,
            32,
            "Defines the radius (in blocks) in which the ritual will search for cleanStone to generate ores into.");
        maxCount = cfg.getInt(
            getKey() + "Count",
            getConfigurationSection(),
            maxCount,
            1,
            4000,
            "Defines the amount of block-positions the ritual can cache at max count");
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
        String[] applicableReplacements = cfg.getStringList(
            "ReplacementStates",
            getConfigurationSection(),
            new String[] { "minecraft:stone:0" },
            "Defines the blockstates that may be replaced by generated ore from the ritual. format: <modid>:<name>:<meta> - Use meta -1 for wildcard");
        replaceableStatesSerialized = Arrays.asList(applicableReplacements);
    }

    private static void resolveReplaceableStates() {
        replaceableStates = new LinkedList<>();
        for (String stateStr : replaceableStatesSerialized) {
            String[] spl = stateStr.split(":");
            if (spl.length != 3) {
                AstralSorcery.log.info("Skipping invalid replacement state: " + stateStr);
                continue;
            }
            String strMeta = spl[2];
            Integer meta;
            try {
                meta = Integer.parseInt(strMeta);
            } catch (NumberFormatException exc) {
                AstralSorcery.log
                    .error("Skipping invalid replacement state: " + stateStr + " - Its 'meta' is not a number!");
                continue;
            }
            Block b = GameRegistry.findBlock(spl[0], spl[1]);
            if (b == null) {
                AstralSorcery.log
                    .error("Skipping invalid replacement state: " + stateStr + " - The block does not exist!");
                continue;
            }
            // In 1.7.10, metadata is handled separately, just add the block
            replaceableStates.add(b);
        }
    }
}
