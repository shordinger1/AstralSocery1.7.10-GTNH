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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hellfirepvp.astralsorcery.common.migration.Ingredient;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapelessRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public class ShapelessRecipe extends AbstractRecipeAccessor {

    private final ArrayList<ItemHandle> inputs;

    public ShapelessRecipe(@Nonnull ItemStack output, ArrayList<ItemHandle> inputs) {
        super(output);
        this.inputs = inputs;
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(int row, int column) {
        int index = row * 3 + column;
        return index >= inputs.size() ? null : inputs.get(index);
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(ShapedRecipeSlot slot) {
        int index = slot.rowMultipler * 3 + slot.columnMultiplier;
        return index >= inputs.size() ? null : inputs.get(index);
    }

    public static class Builder {

        private boolean registered = false;

        private final ResourceLocation entry;
        private final ItemStack output;
        private final ArrayList<ItemHandle> inputs = new ArrayList<>();

        private Builder(String name, ItemStack output) {
            this.entry = new ResourceLocation(AstralSorcery.MODID, "shapeless/" + name);
            this.output = ItemUtils.copyStackWithSize(output, output.stackSize);
        }

        public static Builder newShapelessRecipe(String name, Block output) {
            return newShapelessRecipe(name, new ItemStack(output));
        }

        public static Builder newShapelessRecipe(String name, Item output) {
            return newShapelessRecipe(name, new ItemStack(output));
        }

        public static Builder newShapelessRecipe(String name, ItemStack output) {
            return new Builder(name, output);
        }

        public Builder add(Block block) {
            return add(new ItemStack(block));
        }

        public Builder add(Item item) {
            return add(new ItemStack(item));
        }

        public Builder add(ItemStack stack) {
            if (inputs.size() >= 9) return this; // Add nothing then.
            this.inputs.add(new ItemHandle(stack));
            return this;
        }

        public Builder add(String oreDictName) {
            if (inputs.size() >= 9) return this; // Add nothing then.
            this.inputs.add(new ItemHandle(oreDictName));
            return this;
        }

        public Builder addPart(FluidStack fluidStack) {
            if (inputs.size() >= 9) return this; // Add nothing then.
            this.inputs.add(new ItemHandle(fluidStack));
            return this;
        }

        public Builder addPart(Fluid fluid, int mbAmount) {
            return addPart(new FluidStack(fluid, mbAmount));
        }

        public Builder addPart(Fluid fluid) {
            return addPart(fluid, 1000);
        }

        public Builder addPart(ItemHandle handle) {
            if (inputs.size() >= 9) return this; // Add nothing then.
            this.inputs.add(handle);
            return this;
        }

        public AccessibleRecipeAdapater buildAndRegisterShapelessRecipe() {
            if (registered) throw new IllegalArgumentException("Tried to register previously built recipe twice!");
            registered = true;
            BasePlainRecipe actual = RecipeHelper.getShapelessOreDictRecipe(entry, output, compileIngredients());
            // In 1.7.10, recipes don't need primer registration
            // CommonProxy.registryPrimer.register(actual);
            ShapelessRecipe access = new ShapelessRecipe(output, inputs);
            return new AccessibleRecipeAdapater(actual, access);
        }

        private ArrayList<Ingredient> compileIngredients() {
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            for (ItemHandle handle : inputs) {
                ingredients.add(handle.getRecipeIngredient());
            }
            return ingredients;
        }

    }

}
