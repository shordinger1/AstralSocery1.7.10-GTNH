/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;
// TODO: Forge fluid system - manual review needed

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.Ingredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidIngredient
 * Created by HellFirePvP
 * Date: 28.12.2018 / 15:07
 */
public class FluidIngredient extends Ingredient {

    private final List<FluidStack> fluidsIn;
    private IntList itemIds = null;
    private ItemStack[] itemArray = null;
    private int cacheItemStacks = -1, cacheItemIds = -1;

    // In 1.7.10, RecipeItemHelper doesn't exist, so we create a simple pack method
    private static int pack(ItemStack stack) {
        if (stack == null) return 0;
        int itemId = Item.getIdFromItem(stack.getItem());
        int meta = stack.getItemDamage();
        return (itemId << 16) | (meta & 0xFFFF);
    }

    public FluidIngredient(FluidStack... fluids) {
        super(0);
        this.fluidsIn = Arrays.asList(fluids);
    }

    public FluidIngredient(Fluid... fluids) {
        super(0);
        this.fluidsIn = new ArrayList<>(fluids.length);
        for (Fluid f : fluids) {
            fluidsIn.add(new FluidStack(f, Fluid.BUCKET_VOLUME));
        }
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        if (itemArray == null || this.cacheItemStacks != this.fluidsIn.size()) {
            ArrayList<ItemStack> lst = new ArrayList<>();

            for (FluidStack fluid : this.fluidsIn) {
                // In 1.7.10, use FluidContainerRegistry to get filled container
                ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluid);
                if (filledContainer != null) {
                    lst.add(filledContainer);
                }
            }

            this.itemArray = lst.toArray(new ItemStack[lst.size()]);
            this.cacheItemStacks = this.fluidsIn.size();
        }
        return this.itemArray;
    }

    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        if (this.itemIds == null || this.cacheItemIds != fluidsIn.size()) {
            this.itemIds = new IntArrayList(this.fluidsIn.size());

            for (FluidStack fluid : this.fluidsIn) {
                // In 1.7.10, use FluidContainerRegistry to get filled container
                ItemStack bucketFluid = FluidContainerRegistry.fillFluidContainer(fluid);
                if (bucketFluid != null) {
                    this.itemIds.add(pack(bucketFluid));
                }
            }

            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
            this.cacheItemIds = this.fluidsIn.size();
        }

        return this.itemIds;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        }

        // In 1.7.10, use FluidContainerRegistry
        FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(input);
        if (contained == null || contained.amount <= 0) {
            return false;
        }

        for (FluidStack target : this.fluidsIn) {
            if (contained.fluidID == target.fluidID && contained.amount >= target.amount) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void invalidate() {
        this.itemIds = null;
        this.itemArray = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
