/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.*;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.google.common.collect.Lists;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Plants
 * Created by HellFirePvP
 * Date: 10.10.2017 / 22:03
 */
public enum Plants {

    // 1.7.10: Use lowercase field names
    FLOWER_RED(Blocks.red_flower, true),
    FLOWER_YELLOW(Blocks.yellow_flower, true),
    GRASS(Blocks.tallgrass, true),
    MELON(Blocks.melon_block),
    PUMPKIN(Blocks.pumpkin, true),
    SAPLING(Blocks.sapling, true);

    private static final Random rand = new Random();
    private List<Block> potentialBlockStates = new ArrayList<>();

    private Plants(List<Block> states) {
        this.potentialBlockStates = Lists.newArrayList(states);
    }

    private Plants(Block... states) {
        this.potentialBlockStates = Arrays.asList(states);
    }

    private Plants(Block block, boolean computeAll) {
        this(buildStates(block));
    }

    // 1.7.10: Remove ForgeRegistries constructor and BlockVariants (doesn't exist)
    private static List<Block> buildStates(Block block) {
        List<Block> available = new LinkedList<>();
        // 1.7.10: BlockVariants doesn't exist, just add the block
        available.add(block);
        if (available == null || available.stackSize <= 0) {
            available.add(block);
        }
        return available;
    }

    public Block getRandomState() {
        return potentialBlockStates.get(rand.nextInt(potentialBlockStates.size()));
    }

    private Block getRandomState_Rec() {
        if (potentialBlockStates == null || potentialBlockStates.stackSize <= 0) {
            return getAnyRandomState(); // Unloaded mod. rec call.
        }
        return potentialBlockStates.get(rand.nextInt(potentialBlockStates.size()));
    }

    public static Block getAnyRandomState() {
        return values()[rand.nextInt(values().length)].getRandomState_Rec();
    }

    public static boolean matchesAny(Block test) {
        for (Plants plant : values()) {
            if (plant.potentialBlockStates == null || potentialBlockStates.stackSize <= 0) continue;

            for (Block state : plant.potentialBlockStates) {
                if (state.equals(test)) {
                    return true;
                }
            }
        }
        return false;
    }

}
