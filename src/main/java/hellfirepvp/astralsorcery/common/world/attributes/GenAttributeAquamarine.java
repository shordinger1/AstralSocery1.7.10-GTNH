/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.attributes;

import java.util.*;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.world.WorldGenAttribute;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeAquamarine
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:59
 */
public class GenAttributeAquamarine extends WorldGenAttribute {

    private List<Block> replaceableStates = null;
    private List<String> replaceableStatesSerialized = new ArrayList<>(); // Delay resolving states to a later state...

    public GenAttributeAquamarine() {
        super(0);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.WORLDGEN, "aquamarine") {

            @Override
            public void loadFromConfig(Configuration cfg) {
                String[] applicableReplacements = cfg.getStringList(
                    "ReplacementStates",
                    getConfigurationSection(),
                    new String[] { "minecraft:sand:0" },
                    "Defines the blockstates that may be replaced by aquamarine shale when trying to generate aquamarine shale. format: <modid>:<name>:<meta> - Use meta -1 for wildcard");
                replaceableStatesSerialized = Arrays.asList(applicableReplacements);
            }
        });
    }

    private void resolveReplaceableStates() {
        replaceableStates = new LinkedList<>();
        for (String stateStr : replaceableStatesSerialized) {
            String[] spl = stateStr.split(":");
            if (spl.length != 3) {
                AstralSorcery.log.info("Skipping invalid replacement state: " + stateStr);
                continue;
            }
            String strMeta = spl[2];
            int meta;
            try {
                meta = Integer.parseInt(strMeta);
            } catch (NumberFormatException exc) {
                AstralSorcery.log
                    .error("Skipping invalid replacement state: " + stateStr + " - Its 'meta' is not a number!");
                continue;
            }
            Block b = GameRegistry.findBlock(spl[0], spl[1]);
            if (b == null || b == Blocks.air) {
                AstralSorcery.log
                    .error("Skipping invalid replacement state: " + stateStr + " - The block does not exist!");
                continue;
            }
            // In 1.7.10, blocks don't have multiple variants in the same way
            // Just add the block regardless of meta
            replaceableStates.add(b);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if (replaceableStates == null) {
            resolveReplaceableStates();
        }

        for (int i = 0; i < Config.aquamarineAmount; i++) {
            int rX = (chunkX * 16) + random.nextInt(16) + 8;
            int rY = 48 + random.nextInt(19);
            int rZ = (chunkZ * 16) + random.nextInt(16) + 8;
            BlockPos pos = new BlockPos(rX, rY, rZ);
            Block stateAt = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (MiscUtils.getMatchingState(this.replaceableStates, stateAt) == null) {
                continue;
            }

            boolean foundWater = false;
            for (int yy = 0; yy < 2; yy++) {
                BlockPos check = pos.offset(ForgeDirection.UP, yy);
                Block bs = world.getBlock(check.getX(), check.getY(), check.getZ());
                if ((bs instanceof BlockLiquid && bs.getMaterial() == Material.water) || bs.equals(Blocks.ice)
                    || bs.equals(Blocks.packed_ice)) {
                    // Note: frosted_ice doesn't exist in 1.7.10 (added in 1.9)
                    foundWater = true;
                    break;
                }
            }
            if (!foundWater) continue;

            world.setBlock(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                BlocksAS.customSandOre,
                BlockCustomSandOre.OreType.AQUAMARINE.ordinal(),
                3);
        }
    }
}
