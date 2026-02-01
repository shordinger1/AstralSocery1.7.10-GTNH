/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * AltarRecipeBuilder - Fluent API for building altar recipes
 *
 * Phase 4: Simplified recipe creation with builder pattern
 *******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Builder for creating altar recipes with a fluent API
 * <p>
 * Phase 4: Simplifies recipe creation and supports both shaped and shapeless recipes.
 * <p>
 * Usage examples:
 * 
 * <pre>
 * // Shapeless recipe
 * AltarRecipeBuilder.create(DISCOVERY)
 *     .input(new ItemStack(Items.diamond), new ItemStack(Items.gold_ingot))
 *     .output(new ItemStack(ItemsAS.celestialCrystal))
 *     .starlight(1000)
 *     .time(200)
 *     .build();
 *
 * // Shaped recipe with pattern
 * AltarRecipeBuilder.create(DISCOVERY)
 *     .shaped()
 *     .pattern(" P ", " C ", " M ")
 *     .input('P', new ItemStack(Items.paper))
 *     .input('C', new ItemStack(ItemsAS.rockCrystalSimple))
 *     .input('M', new ItemStack(BlocksAS.blockMarble))
 *     .output(new ItemStack(ItemsAS.constellationPaper))
 *     .starlight(700)
 *     .build();
 * </pre>
 */
public class AltarRecipeBuilder {

    private final TileAltar.AltarLevel altarLevel;
    private final List<ItemStack> inputs = new ArrayList<>();
    private final Map<Character, ItemStack> patternIngredients = new HashMap<>();

    private ItemStack output;
    private String constellation;
    private int starlightRequired = 0;
    private int craftingTime = 100;

    private boolean shaped = false;
    private int width = 3;
    private int height = 3;

    private AltarRecipeBuilder(TileAltar.AltarLevel altarLevel) {
        this.altarLevel = altarLevel;
    }

    /**
     * Create a new recipe builder for the specified altar level
     *
     * @param altarLevel The altar level required
     * @return A new recipe builder
     */
    public static AltarRecipeBuilder create(TileAltar.AltarLevel altarLevel) {
        return new AltarRecipeBuilder(altarLevel);
    }

    /**
     * Mark this recipe as shaped (ordered)
     *
     * @return this builder
     */
    public AltarRecipeBuilder shaped() {
        this.shaped = true;
        return this;
    }

    /**
     * Mark this recipe as shapeless (unordered)
     *
     * @return this builder
     */
    public AltarRecipeBuilder shapeless() {
        this.shaped = false;
        return this;
    }

    /**
     * Define a shaped recipe pattern
     * <p>
     * Each string represents a row, characters represent ingredient keys.
     * Space ' ' represents an empty slot.
     * <p>
     * Example:
     * 
     * <pre>
     * pattern("ABC", "DE ", " F ")
     * </pre>
     * 
     * Creates a 3x3 pattern where:
     * <ul>
     * <li>Row 1: A, B, C</li>
     * <li>Row 2: D, E, empty</li>
     * <li>Row 3: empty, empty, F</li>
     * </ul>
     *
     * @param pattern Recipe pattern strings
     * @return this builder
     */
    public AltarRecipeBuilder pattern(String... pattern) {
        this.shaped = true;
        this.height = pattern.length;

        // Calculate width (find longest row)
        this.width = 0;
        for (String row : pattern) {
            if (row.length() > width) {
                width = row.length();
            }
        }

        // Convert pattern to input array
        inputs.clear();
        for (String row : pattern) {
            for (int i = 0; i < width; i++) {
                if (i < row.length()) {
                    char key = row.charAt(i);
                    if (key == ' ') {
                        inputs.add(null); // Empty slot
                    } else {
                        inputs.add(
                            new ItemStack(
                                net.minecraft.init.Items.diamond, // Placeholder, will be replaced
                                0,
                                key // Store key in damage value temporarily
                            ));
                    }
                } else {
                    inputs.add(null); // Empty slot for padding
                }
            }
        }

        return this;
    }

    /**
     * Add input items (for shapeless recipes)
     *
     * @param stacks Input item stacks
     * @return this builder
     */
    public AltarRecipeBuilder input(ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            if (stack != null) {
                inputs.add(stack.copy());
            } else {
                inputs.add(null);
            }
        }
        return this;
    }

    /**
     * Add a pattern ingredient (for shaped recipes)
     * <p>
     * Associates a character key with an item stack.
     *
     * @param key   The character key from the pattern
     * @param stack The item stack
     * @return this builder
     */
    public AltarRecipeBuilder input(char key, ItemStack stack) {
        patternIngredients.put(key, stack.copy());
        return this;
    }

    /**
     * Set multiple pattern ingredients at once
     *
     * @param ingredients Map of character keys to item stacks
     * @return this builder
     */
    public AltarRecipeBuilder input(Map<Character, ItemStack> ingredients) {
        patternIngredients.putAll(ingredients);
        return this;
    }

    /**
     * Set the output item
     *
     * @param stack The output item stack
     * @return this builder
     */
    public AltarRecipeBuilder output(ItemStack stack) {
        this.output = stack.copy();
        return this;
    }

    /**
     * Set the constellation requirement
     *
     * @param constellation Constellation name (can be null)
     * @return this builder
     */
    public AltarRecipeBuilder constellation(String constellation) {
        this.constellation = constellation;
        return this;
    }

    /**
     * Set the starlight requirement
     *
     * @param amount Starlight required
     * @return this builder
     */
    public AltarRecipeBuilder starlight(int amount) {
        this.starlightRequired = amount;
        return this;
    }

    /**
     * Set the crafting time
     *
     * @param ticks Time in ticks
     * @return this builder
     */
    public AltarRecipeBuilder time(int ticks) {
        this.craftingTime = ticks;
        return this;
    }

    /**
     * Build the recipe
     *
     * @return A new ASAltarRecipe instance
     */
    public ASAltarRecipe build() {
        // Validate
        if (output == null) {
            throw new IllegalStateException("Recipe must have an output!");
        }

        if (inputs.isEmpty() && shaped) {
            throw new IllegalStateException("Shaped recipe must have a pattern or inputs!");
        }

        // Build final input array
        ItemStack[] finalInputs;

        if (shaped) {
            // Replace pattern placeholders with actual items
            finalInputs = new ItemStack[width * height];

            for (int i = 0; i < inputs.size(); i++) {
                ItemStack placeholder = inputs.get(i);

                if (placeholder != null && placeholder.getItemDamage() != 0) {
                    // This is a pattern key placeholder
                    char key = (char) placeholder.getItemDamage();
                    ItemStack actualItem = patternIngredients.get(key);

                    if (actualItem == null) {
                        LogHelper.warn("No item defined for pattern key: " + key);
                        finalInputs[i] = null;
                    } else {
                        finalInputs[i] = actualItem.copy();
                    }
                } else {
                    // Regular item or null
                    finalInputs[i] = placeholder == null ? null : placeholder.copy();
                }
            }
        } else {
            // Shapeless recipe
            finalInputs = inputs.toArray(new ItemStack[0]);
        }

        // Create recipe
        return new ASAltarRecipe(
            altarLevel,
            finalInputs,
            output,
            constellation,
            starlightRequired,
            craftingTime,
            shaped,
            width,
            height);
    }

    /**
     * Build and register the recipe
     *
     * @return The built and registered recipe
     */
    public ASAltarRecipe buildAndRegister() {
        ASAltarRecipe recipe = build();
        AltarRecipeRegistry.addRecipe(recipe);
        return recipe;
    }
}
