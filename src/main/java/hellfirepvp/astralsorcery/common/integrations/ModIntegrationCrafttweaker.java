/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.config.PerkTreeConfig;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipe;
import hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMap;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.BaseAltarRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.SerializeableRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * Integration for recipe system - GTNH style
 * Replaces the CraftTweaker-based system with code-based recipe registration
 *
 * This class handles:
 * - Recipe map initialization
 * - Recipe loading and compilation
 * - Configuration management for perks and game stages
 */
public class ModIntegrationCrafttweaker {

    public static ModIntegrationCrafttweaker instance = new ModIntegrationCrafttweaker();

    // Recipe queues for dynamic recipe additions
    public static List<ASRecipe> recipeQueue = new LinkedList<>();

    private ModIntegrationCrafttweaker() {}

    /**
     * Loads the recipe system
     * Called during mod initialization
     */
    public void load() {
        AstralSorcery.log.info("Initializing Astral Sorcery Recipe System...");

        // Initialize recipe maps
        hellfirepvp.astralsorcery.common.crafting.registry.ASRecipeMaps.init();

        // Register event handlers for perk configuration
        MinecraftForge.EVENT_BUS.register(new PerkTreeConfig.EventHandler());

        // Load built-in recipes
        loadBuiltInRecipes();

        AstralSorcery.log
            .info("Recipe System initialized with " + ASRecipeMap.ALL_RECIPE_MAPS.size() + " recipe maps.");
    }

    /**
     * Loads built-in mod recipes
     * This is where you would call your recipe loading classes
     */
    private void loadBuiltInRecipes() {
        // Recipe classes will be added here
        // For example:
        // hellfirepvp.astralsorcery.common.crafting.recipe.InfusionRecipes.load();
        // hellfirepvp.astralscery.common.crafting.recipe.GrindstoneRecipes.load();
        // etc.

        AstralSorcery.log.info("Built-in recipes loaded.");
    }

    /**
     * Applies all queued recipes to the crafting system
     * Called after all recipe modifications have been queued
     */
    public void applyRecipes() {
        if (recipeQueue == null || recipeQueue.isEmpty()) {
            AstralSorcery.log.info("No queued recipes to apply.");
            return;
        }

        AstralSorcery.log.info("Applying " + recipeQueue.size() + " queued recipes...");

        for (ASRecipe recipe : recipeQueue) {
            try {
                applyRecipe(recipe);
            } catch (Exception exc) {
                AstralSorcery.log.error(
                    "Failed to apply recipe for type " + recipe.getType()
                        .name()
                        .toLowerCase());
                exc.printStackTrace();
            }
        }

        AstralSorcery.log.info("Successfully applied " + recipeQueue.size() + " recipes.");
        recipeQueue.clear();
    }

    /**
     * Applies a single recipe to the appropriate registry
     *
     * @param recipe The recipe to apply
     */
    private void applyRecipe(ASRecipe recipe) {
        switch (recipe.getType()) {
            case INFUSION:
                applyInfusionRecipe(recipe);
                break;
            case GRINDSTONE:
                applyGrindstoneRecipe(recipe);
                break;
            case LIGHT_TRANSMUTATION:
                applyTransmutationRecipe(recipe);
                break;
            case ALTAR_DISCOVERY:
            case ALTAR_ATTUNEMENT:
            case ALTAR_CONSTELLATION:
            case ALTAR_TRAIT:
                applyAltarRecipe(recipe);
                break;
            case WELL:
                applyWellRecipe(recipe);
                break;
            case LIQUID_INTERACTION:
                applyLiquidInteractionRecipe(recipe);
                break;
            default:
                AstralSorcery.log.warn("Unknown recipe type: " + recipe.getType());
                break;
        }
    }

    private void applyInfusionRecipe(ASRecipe recipe) {
        ItemHandle input = recipe.getInputs()[0];
        ItemStack output = recipe.getOutput();

        // Create native InfusionRecipe
        AbstractInfusionRecipe nativeRecipe = new BasicInfusionRecipe(output, input) {

            @Override
            public int craftingTickTime() {
                return recipe.getDuration();
            }

            @Override
            public boolean doesConsumeMultiple() {
                return recipe.doesConsumeMultiple();
            }
        }.setLiquidStarlightConsumptionChance(recipe.getConsumptionChance());

        CraftingAccessManager.registerMTInfusion(nativeRecipe);
    }

    private void applyGrindstoneRecipe(ASRecipe recipe) {
        ItemHandle input = recipe.getInputs()[0];
        ItemStack output = recipe.getOutput();
        int duration = recipe.getDuration();
        float doubleChance = recipe.getDoubleChance();

        CraftingAccessManager.addGrindstoneRecipe(input, output, duration, doubleChance);
    }

    private void applyTransmutationRecipe(ASRecipe recipe) {
        ItemStack inputStack = recipe.getInputs()[0].getApplicableItems()
            .get(0);
        ItemStack outputStack = recipe.getOutput();
        double cost = recipe.getTransmutationCost();
        IWeakConstellation constellation = recipe.getConstellation();

        CraftingAccessManager.addMTTransmutation(inputStack, outputStack, cost, constellation);
    }

    private void applyAltarRecipe(ASRecipe recipe) {
        // Map ASRecipe.Type to TileAltar.AltarLevel and CraftingType
        TileAltar.AltarLevel altarLevel;
        SerializeableRecipe.CraftingType craftingType;

        switch (recipe.getType()) {
            case ALTAR_DISCOVERY:
                altarLevel = TileAltar.AltarLevel.DISCOVERY;
                craftingType = SerializeableRecipe.CraftingType.ALTAR_T1_ADD;
                break;
            case ALTAR_ATTUNEMENT:
                altarLevel = TileAltar.AltarLevel.ATTUNEMENT;
                craftingType = SerializeableRecipe.CraftingType.ALTAR_T2_ADD;
                break;
            case ALTAR_CONSTELLATION:
                altarLevel = TileAltar.AltarLevel.CONSTELLATION_CRAFT;
                craftingType = SerializeableRecipe.CraftingType.ALTAR_T3_ADD;
                break;
            case ALTAR_TRAIT:
                altarLevel = TileAltar.AltarLevel.TRAIT_CRAFT;
                craftingType = SerializeableRecipe.CraftingType.ALTAR_T4_ADD;
                break;
            default:
                AstralSorcery.log.error("Unknown altar recipe type: " + recipe.getType());
                return;
        }

        // Create a BaseAltarRecipe subclass that can access buildRecipeUnsafe
        AltarRecipeBuilder builder = new AltarRecipeBuilder(
            recipe.getRecipeRegistryName() != null ? recipe.getRecipeRegistryName() : "as_recipe",
            recipe.getInputs(),
            recipe.getOutput(),
            recipe.getStarlightRequired(),
            recipe.getDuration(),
            craftingType);

        AbstractAltarRecipe nativeRecipe = builder.buildRecipe(altarLevel);

        if (nativeRecipe == null) {
            AstralSorcery.log.error("Failed to create altar recipe for: " + recipe.getRecipeRegistryName());
            return;
        }

        CraftingAccessManager.registerMTAltarRecipe(nativeRecipe);
    }

    /**
     * Helper class to extend BaseAltarRecipe and access its protected buildRecipeUnsafe method
     * Must be in the same package as BaseAltarRecipe to access protected members
     */
    private static class AltarRecipeBuilder extends BaseAltarRecipe {

        private final SerializeableRecipe.CraftingType craftingType;
        private final int storedStarlightRequired;
        private final int storedCraftingTickTime;
        private final ItemStack storedOutput;
        private final ItemHandle[] storedInputs;

        public AltarRecipeBuilder(String name, ItemHandle[] inputs, ItemStack output, int starlightRequired,
            int craftingTickTime, SerializeableRecipe.CraftingType craftingType) {
            super(name, inputs, output, starlightRequired, craftingTickTime);
            this.craftingType = craftingType;
            // Store values locally to avoid accessing protected fields from parent
            this.storedStarlightRequired = starlightRequired;
            this.storedCraftingTickTime = craftingTickTime;
            this.storedOutput = output;
            this.storedInputs = inputs;
        }

        public AbstractAltarRecipe buildRecipe(TileAltar.AltarLevel altarLevel) {
            // Call the protected method from BaseAltarRecipe using stored values
            AbstractAltarRecipe result = buildRecipeUnsafe(
                altarLevel,
                this.storedStarlightRequired,
                this.storedCraftingTickTime,
                this.storedOutput,
                this.storedInputs);
            if (result == null) {
                AstralSorcery.log.error("buildRecipeUnsafe returned null for altar level: " + altarLevel);
            }
            return result;
        }

        @Override
        public SerializeableRecipe.CraftingType getType() {
            return craftingType;
        }

        @Override
        public void applyRecipe() {
            // Not used - we call buildRecipe() directly
        }
    }

    private void applyWellRecipe(ASRecipe recipe) {
        ItemStack inputStack = recipe.getInputs()[0].getApplicableItems()
            .get(0);
        if (inputStack == null) {
            AstralSorcery.log.error("Well recipe input is null");
            return;
        }

        net.minecraftforge.fluids.FluidStack fluidStack = recipe.getFluidOutput();
        if (fluidStack == null || fluidStack.getFluid() == null) {
            AstralSorcery.log.error("Well recipe fluid output is null");
            return;
        }

        net.minecraftforge.fluids.Fluid fluid = fluidStack.getFluid();
        float productionMultiplier = recipe.getProductionMultiplier();
        float shatterMultiplier = recipe.getShatterMultiplier();
        int colorHex = recipe.getColorHex();

        CraftingAccessManager.addMTLiquefaction(inputStack, fluid, productionMultiplier, shatterMultiplier, colorHex);
    }

    private void applyLiquidInteractionRecipe(ASRecipe recipe) {
        ItemStack output = recipe.getOutput();
        // We need to get the fluid inputs from the recipe
        // For now, this implementation depends on how ASRecipe stores fluid inputs
        AstralSorcery.log.info("Liquid interaction recipes should be added directly to the registry");
    }

    /**
     * Compiles all recipes
     * Called after all recipes have been applied
     */
    public void compile() {
        CraftingAccessManager.compile();
    }

    /**
     * Clears all recipe modifications and reloads default recipes
     */
    public void reload() {
        CraftingAccessManager.clearModifications();
        loadBuiltInRecipes();
        applyRecipes();
        compile();
    }
}
