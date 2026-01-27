/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.registry;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;

/**
 * Astral Sorcery Recipe - GTNH style recipe representation
 * Replaces the CraftTweaker SerializeableRecipe system
 */
public class ASRecipe {

    /**
     * Recipe types corresponding to different Astral Sorcery crafting mechanics
     */
    public enum Type {
        INFUSION,
        GRINDSTONE,
        LIGHT_TRANSMUTATION,
        ALTAR_DISCOVERY,
        ALTAR_ATTUNEMENT,
        ALTAR_CONSTELLATION,
        ALTAR_TRAIT,
        WELL,
        LIQUID_INTERACTION
    }

    // Common properties
    private final Type type;
    private final ItemHandle[] inputs;
    private final ItemStack output;
    private final FluidStack fluidOutput;
    private final int duration;

    // Infusion-specific
    private final boolean consumeMultiple;
    private final float consumptionChance;

    // Grindstone-specific
    private final float doubleChance;

    // Light Transmutation-specific
    private final double transmutationCost;
    private final IWeakConstellation constellation;

    // Altar-specific
    private final int starlightRequired;
    private final IConstellation requiredConstellationFocus;
    private final String recipeRegistryName;

    // Well-specific
    private final float productionMultiplier;
    private final float shatterMultiplier;
    private final int colorHex;

    // Liquid Interaction-specific
    private final FluidStack fluidInput2;
    private final float chanceConsumption1;
    private final float chanceConsumption2;
    private final int weight;

    private ASRecipe(Builder builder) {
        this.type = builder.type;
        this.inputs = builder.inputs;
        this.output = builder.output;
        this.fluidOutput = builder.fluidOutput;
        this.duration = builder.duration;
        this.consumeMultiple = builder.consumeMultiple;
        this.consumptionChance = builder.consumptionChance;
        this.doubleChance = builder.doubleChance;
        this.transmutationCost = builder.transmutationCost;
        this.constellation = builder.constellation;
        this.starlightRequired = builder.starlightRequired;
        this.requiredConstellationFocus = builder.requiredConstellationFocus;
        this.recipeRegistryName = builder.recipeRegistryName;
        this.productionMultiplier = builder.productionMultiplier;
        this.shatterMultiplier = builder.shatterMultiplier;
        this.colorHex = builder.colorHex;
        this.fluidInput2 = builder.fluidInput2;
        this.chanceConsumption1 = builder.chanceConsumption1;
        this.chanceConsumption2 = builder.chanceConsumption2;
        this.weight = builder.weight;
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    @Nonnull
    public ItemHandle[] getInputs() {
        return inputs;
    }

    @Nullable
    public ItemStack getOutput() {
        return output;
    }

    @Nullable
    public FluidStack getFluidOutput() {
        return fluidOutput;
    }

    public int getDuration() {
        return duration;
    }

    public boolean doesConsumeMultiple() {
        return consumeMultiple;
    }

    public float getConsumptionChance() {
        return consumptionChance;
    }

    public float getDoubleChance() {
        return doubleChance;
    }

    public double getTransmutationCost() {
        return transmutationCost;
    }

    @Nullable
    public IWeakConstellation getConstellation() {
        return constellation;
    }

    public int getStarlightRequired() {
        return starlightRequired;
    }

    @Nullable
    public IConstellation getRequiredConstellationFocus() {
        return requiredConstellationFocus;
    }

    @Nullable
    public String getRecipeRegistryName() {
        return recipeRegistryName;
    }

    public float getProductionMultiplier() {
        return productionMultiplier;
    }

    public float getShatterMultiplier() {
        return shatterMultiplier;
    }

    public int getColorHex() {
        return colorHex;
    }

    @Nullable
    public FluidStack getFluidInput2() {
        return fluidInput2;
    }

    public float getChanceConsumption1() {
        return chanceConsumption1;
    }

    public float getChanceConsumption2() {
        return chanceConsumption2;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Creates a new builder for this recipe type
     */
    public static Builder builder(Type type) {
        return new Builder(type);
    }

    /**
     * Builder class for ASRecipe using fluent API
     */
    public static class Builder {

        private final Type type;

        // Common
        private ItemHandle[] inputs = new ItemHandle[0];
        private ItemStack output = null;
        private FluidStack fluidOutput = null;
        private int duration = 100;

        // Infusion
        private boolean consumeMultiple = false;
        private float consumptionChance = 1.0F;

        // Grindstone
        private float doubleChance = 0.0F;

        // Transmutation
        private double transmutationCost = 0;
        private IWeakConstellation constellation = null;

        // Altar
        private int starlightRequired = 0;
        private IConstellation requiredConstellationFocus = null;
        private String recipeRegistryName = null;

        // Well
        private float productionMultiplier = 1.0F;
        private float shatterMultiplier = 1.0F;
        private int colorHex = 0xFFFFFF;

        // Liquid Interaction
        private FluidStack fluidInput2 = null;
        private float chanceConsumption1 = 1.0F;
        private float chanceConsumption2 = 1.0F;
        private int weight = 1;

        private Builder(Type type) {
            this.type = Objects.requireNonNull(type, "Recipe type cannot be null");
        }

        public Builder inputs(ItemHandle... inputs) {
            this.inputs = inputs != null ? inputs : new ItemHandle[0];
            return this;
        }

        public Builder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public Builder fluidOutput(FluidStack fluidOutput) {
            this.fluidOutput = fluidOutput;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = Math.max(0, duration);
            return this;
        }

        public Builder consumeMultiple(boolean consume) {
            this.consumeMultiple = consume;
            return this;
        }

        public Builder consumptionChance(float chance) {
            this.consumptionChance = Math.max(0, Math.min(1, chance));
            return this;
        }

        public Builder doubleChance(float chance) {
            this.doubleChance = chance;
            return this;
        }

        public Builder transmutationCost(double cost) {
            this.transmutationCost = Math.max(0, cost);
            return this;
        }

        public Builder constellation(IWeakConstellation constellation) {
            this.constellation = constellation;
            return this;
        }

        public Builder starlightRequired(int amount) {
            this.starlightRequired = Math.max(0, amount);
            return this;
        }

        public Builder requiredConstellationFocus(IConstellation constellation) {
            this.requiredConstellationFocus = constellation;
            return this;
        }

        public Builder recipeRegistryName(String name) {
            this.recipeRegistryName = name;
            return this;
        }

        public Builder productionMultiplier(float multiplier) {
            this.productionMultiplier = multiplier;
            return this;
        }

        public Builder shatterMultiplier(float multiplier) {
            this.shatterMultiplier = multiplier;
            return this;
        }

        public Builder colorHex(int color) {
            this.colorHex = color;
            return this;
        }

        public Builder fluidInput2(FluidStack fluid) {
            this.fluidInput2 = fluid;
            return this;
        }

        public Builder chanceConsumption1(float chance) {
            this.chanceConsumption1 = Math.max(0, chance);
            return this;
        }

        public Builder chanceConsumption2(float chance) {
            this.chanceConsumption2 = Math.max(0, chance);
            return this;
        }

        public Builder weight(int weight) {
            this.weight = Math.max(0, weight);
            return this;
        }

        /**
         * Builds the recipe
         */
        public ASRecipe build() {
            return new ASRecipe(this);
        }

        /**
         * Builds and adds the recipe to the specified recipe map
         */
        public ASRecipe addTo(ASRecipeMap map) {
            ASRecipe recipe = build();
            map.addRecipe(recipe);
            return recipe;
        }
    }
}
