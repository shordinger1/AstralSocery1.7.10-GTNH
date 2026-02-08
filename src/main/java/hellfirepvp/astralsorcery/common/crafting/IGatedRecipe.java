/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * IGatedRecipe - Gated recipe interface
 *
 * Recipes implementing this interface require player progression
 * to be visible/craftable.
 *******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

/**
 * Interface for altar recipes that are gated behind player progression
 * <p>
 * Gated recipes require players to have discovered certain constellations
 * or reached certain research levels before they can craft the recipe.
 * <p>
 * <b>Implementation:</b>
 * <ul>
 * <li>Check server-side progression in {@link #hasProgressionServer()}</li>
 * <li>Check client-side progression in {@link #hasProgressionClient()}</li>
 * <li>Return false to hide/lock the recipe</li>
 * </ul>
 */
public interface IGatedRecipe {

    /**
     * Check if the player has the required progression on the server side
     * <p>
     * Called when:
     * <ul>
     * <li>Player tries to start crafting</li>
     * <li>Recipe matching is performed</li>
     * </ul>
     *
     * @param player The player to check
     * @return true if player can craft this recipe
     */
    boolean hasProgressionServer(net.minecraft.entity.player.EntityPlayer player);

    /**
     * Check if the player has the required progression on the client side
     * <p>
     * Called when:
     * <ul>
     * <li>Rendering recipe in GUI/NEI</li>
     * <li>Displaying recipe information</li>
     * </ul>
     * <p>
     * Return false to hide the recipe from the player's view.
     *
     * @return true if player can see this recipe
     */
    boolean hasProgressionClient();

    /**
     * Get the required constellation for this recipe
     *
     * @return The constellation name, or null if no constellation required
     */
    String getRequiredConstellation();

    /**
     * Get the required research level for this recipe
     *
     * @return The research level (0 = none), or -1 if no level required
     */
    int getRequiredResearchLevel();

    /**
     * Check if this recipe should be completely hidden from the player
     * <p>
     * If true, the recipe won't appear in NEI/JEI at all.
     * If false, the recipe appears but is shown as "locked" or "requires research".
     *
     * @return true to completely hide the recipe
     */
    boolean hideFromRecipeViewer();

    /**
     * Default implementation for recipes that don't require gating
     * <p>
     * Use this to make non-gated recipes compatible with the gated recipe system.
     */
    IGatedRecipe UNGATED = new IGatedRecipe() {

        @Override
        public boolean hasProgressionServer(net.minecraft.entity.player.EntityPlayer player) {
            return true; // Always accessible
        }

        @Override
        public boolean hasProgressionClient() {
            return true; // Always visible
        }

        @Override
        public String getRequiredConstellation() {
            return null; // No constellation required
        }

        @Override
        public int getRequiredResearchLevel() {
            return -1; // No level required
        }

        @Override
        public boolean hideFromRecipeViewer() {
            return false; // Always show
        }
    };
}
