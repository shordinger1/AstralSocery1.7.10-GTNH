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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.ItemComparator;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightOreTransmutations
 * Created by HellFirePvP
 * Date: 30.01.2017 / 12:30
 */
public class LightOreTransmutations {

    public static Collection<Transmutation> mtTransmutations = new ArrayList<>(); // Minetweaker cache
    private static Collection<Transmutation> registeredTransmutations = new ArrayList<>();

    private static Collection<Transmutation> localFallback = new ArrayList<>();

    public static void init() {
        // 1.7.10: Use Block directly instead of getDefaultState()
        // 1.7.10: Use lowercase field names
        registerTransmutation(new Transmutation(Blocks.flowing_lava, Blocks.obsidian, 400.0D));
        registerTransmutation(new Transmutation(Blocks.sand, Blocks.clay, 400.0D));
        registerTransmutation(new Transmutation(Blocks.diamond_ore, Blocks.emerald_ore, 1000.0D));
        // NETHER_WART_BLOCK doesn't exist in 1.7.10, skip
        registerTransmutation(new Transmutation(Blocks.soul_sand, Blocks.lapis_block, 200.0D));
        registerTransmutation(new Transmutation(Blocks.sandstone, Blocks.end_stone, 200.0D));
        registerTransmutation(new Transmutation(Blocks.netherrack, Blocks.nether_brick, 200.0D));

        registerTransmutation(new Transmutation(Blocks.iron_ore, BlocksAS.customOre, 100));
        registerTransmutation(
            new Transmutation(
                Blocks.pumpkin,
                Blocks.cake,
                new ItemStack(Blocks.pumpkin),
                new ItemStack(Items.cake),
                600.0D));

        cacheLocalFallback();
    }

    private static void cacheLocalFallback() {
        // 1.7.10: Collection doesn't have stackSize, use isEmpty()
        if (localFallback.isEmpty()) {
            localFallback.addAll(registeredTransmutations);
        }
    }

    public static void loadFromFallback() {
        registeredTransmutations.clear();
        registeredTransmutations.addAll(localFallback);
    }

    public static Transmutation tryRemoveTransmutation(ItemStack outRemove, boolean matchMeta) {
        Block b = Block.getBlockFromItem(outRemove.getItem());
        // 1.7.10: Use air instead of AIR
        if (b != Blocks.air) {
            for (Transmutation tr : registeredTransmutations) {
                if (tr.output.equals(b)) {
                    if (!matchMeta || 0 == outRemove.getItemDamage()) { // 1.7.10: Use getItemDamage() instead of
                        // getMetadata()
                        registeredTransmutations.remove(tr);
                        return tr;
                    }
                }
            }
        }
        ItemStack outStack = outRemove;
        for (Transmutation tr : registeredTransmutations) {
            if (!(outStack == null || outStack.stackSize <= 0) && ItemComparator
                .compare(tr.outStack, outRemove, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                registeredTransmutations.remove(tr);
                return tr;
            }
        }
        return null;
    }

    // Will return itself if successful.
    @Nullable
    public static Transmutation registerTransmutation(Transmutation tr) {
        for (Transmutation t : registeredTransmutations) {
            if (t.matchesInput(tr)) {
                AstralSorcery.log
                    .warn("Tried to register Transmutation that has the same input as an already existing one.");
                return null;
            }
        }
        if (!tr.hasValidInput()) {
            AstralSorcery.log.warn("Tried to register Transmutation with null input - Skipping!");
            return null;
        }
        // if (tr.getInputAsBlock()
        // .equals(Blocks.workbench)) {
        // AstralSorcery.log.warn(
        // "Cannot register Transmutation of workbench -> something. By default occupied by general crafting which is
        // handled differently.");
        // return null;
        // }
        if (tr.output == null) {
            AstralSorcery.log.warn("Tried to register Transmutation with null output - Skipping!");
            return null;
        }
        registeredTransmutations.add(tr);
        return tr;
    }

    public static Collection<Transmutation> getRegisteredTransmutations() {
        return Collections.unmodifiableCollection(registeredTransmutations);
    }

    @Nullable
    public static Transmutation searchForTransmutation(Block tryStateIn) {
        for (Transmutation tr : registeredTransmutations) {
            if (tr.matchesInput(tryStateIn)) return tr;
        }
        for (Transmutation tr : mtTransmutations) {
            if (tr.matchesInput(tryStateIn)) return tr;
        }
        return null;
    }

    public static class Transmutation {

        private final MatchingType type;

        private final Block inBlock;

        private final Block input;
        private final Block output;
        private final double cost;

        @Nonnull
        private final ItemStack outStack;
        @Nonnull
        private final ItemStack inStack;

        private IWeakConstellation requiredType = null;

        public Transmutation(Block input, Block output, double cost) {
            this.type = MatchingType.BLOCK;
            this.input = null;
            this.inBlock = input;
            this.output = output;
            this.cost = cost;
            this.outStack = new ItemStack(input);
            this.inStack = new ItemStack(output);
        }

        public Transmutation(Block input, Block output, @Nonnull ItemStack inputDisplay,
            @Nonnull ItemStack outputDisplay, double cost) {
            this.type = MatchingType.BLOCK;
            this.input = null;
            this.inBlock = input;
            this.output = output;
            this.cost = cost;
            this.outStack = outputDisplay;
            this.inStack = inputDisplay;
        }

        public Transmutation setRequiredType(IWeakConstellation requiredType) {
            this.requiredType = requiredType;
            return this;
        }

        public IWeakConstellation getRequiredType() {
            return requiredType;
        }

        public Block getInputAsBlock() {
            switch (type) {
                case STATE:
                    return this.input;
                case BLOCK:
                    return this.inBlock;
                default:
                    break;
            }
            return Blocks.air;
        }

        public boolean hasValidInput() {
            switch (type) {
                case STATE:
                    return input != null && !input.equals(Blocks.air);
                case BLOCK:
                    return inBlock != null && !inBlock.equals(Blocks.air);
                default:
                    break;
            }
            return false;
        }

        public boolean matchesInput(Block state) {
            switch (type) {
                case STATE:
                    return input.equals(state);
                case BLOCK:
                    return inBlock.equals(state);
                default:
                    break;
            }
            return false;
        }

        public boolean matchesInput(Transmutation other) {
            switch (type) {
                case STATE:
                    switch (other.type) {
                        case STATE:
                            return input.equals(other.input);
                        case BLOCK:
                            return input.equals(other.inBlock);
                        default:
                            break;
                    }
                case BLOCK:
                    switch (other.type) {
                        case STATE:
                            return inBlock.equals(other.input);
                        case BLOCK:
                            return inBlock.equals(other.inBlock);
                        default:
                            break;
                    }
                default:
                    break;
            }
            return false;
        }

        public boolean matchesOutput(Block state) {
            return output.equals(state);
        }

        public Block getOutput() {
            return output;
        }

        public double getCost() {
            return cost;
        }

        @Nonnull
        public ItemStack getInputDisplayStack() {
            if (!(inStack == null || inStack.stackSize <= 0)) {
                return inStack.copy();
            }
            return ItemUtils.createBlockStack(input);
        }

        @Nonnull
        public ItemStack getOutputDisplayStack() {
            if (!(outStack == null || outStack.stackSize <= 0)) {
                return outStack.copy();
            }
            return ItemUtils.createBlockStack(output);
        }

    }

    private static enum MatchingType {

        STATE,
        BLOCK;

    }

}
