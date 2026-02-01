/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tuned Crystal Base Item - Base class for constellation-tuned crystals
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;

/**
 * Tuned Crystal Base Item
 * <p>
 * Base class for crystals that can be attuned to constellations.
 * <p>
 * Extends ItemRockCrystalBase with constellation functionality:
 * - Primary constellation (main constellation attunement)
 * - Trait constellation (secondary constellation bonus)
 * - Constellation-specific effects
 * <p>
 * Subclasses:
 * - ItemTunedRockCrystal - Tuned rock crystal
 * - ItemTunedCelestialCrystal - Tuned celestial crystal
 * <p>
 * TODO:
 * - Implement constellation ritual attunement
 * - Implement constellation trait effects
 * - Implement attunement GUI
 * - Implement constellation discovery requirements
 */
public class ItemTunedCrystalBase extends ItemRockCrystalBase {

    private static final String TAG_CONSTELLATION = "constellation";
    private static final String TAG_TRAIT = "trait";

    public ItemTunedCrystalBase() {
        super();
    }

    // ========== Constellation System ==========

    /**
     * Get the primary constellation
     *
     * @param stack The tuned crystal stack
     * @return The constellation, or null if not attuned
     */
    public IConstellation getConstellation(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return null;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey(TAG_CONSTELLATION)) {
            return null;
        }

        String constellationName = nbt.getString(TAG_CONSTELLATION);
        return ConstellationRegistry.getConstellationByName(constellationName);
    }

    /**
     * Set the primary constellation
     *
     * @param stack         The tuned crystal stack
     * @param constellation The constellation to attune
     */
    public void setConstellation(ItemStack stack, IConstellation constellation) {
        if (stack == null || constellation == null) {
            return;
        }

        NBTTagCompound nbt = getOrCreateNBT(stack);
        nbt.setString(TAG_CONSTELLATION, constellation.getUnlocalizedName());
    }

    /**
     * Get the trait (secondary) constellation
     *
     * @param stack The tuned crystal stack
     * @return The trait constellation, or null if not set
     */
    public IConstellation getTrait(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return null;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey(TAG_TRAIT)) {
            return null;
        }

        String traitName = nbt.getString(TAG_TRAIT);
        return ConstellationRegistry.getConstellationByName(traitName);
    }

    /**
     * Set the trait (secondary) constellation
     *
     * @param stack The tuned crystal stack
     * @param trait The trait constellation to apply
     */
    public void setTrait(ItemStack stack, IConstellation trait) {
        if (stack == null || trait == null) {
            return;
        }

        NBTTagCompound nbt = getOrCreateNBT(stack);
        nbt.setString(TAG_TRAIT, trait.getUnlocalizedName());
    }

    /**
     * Apply trait to the crystal
     * <p>
     * Alias for setTrait
     *
     * @param stack The tuned crystal stack
     * @param trait The trait constellation to apply
     */
    public void applyTrait(ItemStack stack, IConstellation trait) {
        setTrait(stack, trait);
    }

    /**
     * Check if crystal has a constellation
     *
     * @param stack The tuned crystal stack
     * @return true if attuned to a constellation
     */
    public boolean hasConstellation(ItemStack stack) {
        return getConstellation(stack) != null;
    }

    /**
     * Check if crystal has a trait
     *
     * @param stack The tuned crystal stack
     * @return true if has a trait constellation
     */
    public boolean hasTrait(ItemStack stack) {
        return getTrait(stack) != null;
    }

    // ========== Display ==========

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // Call parent to show crystal stats
        super.addInformation(stack, player, tooltip, advanced);

        // Show constellation information
        IConstellation mainConstellation = getConstellation(stack);
        if (mainConstellation != null) {
            tooltip.add("Constellation: " + mainConstellation.getSimpleName());
        }

        IConstellation trait = getTrait(stack);
        if (trait != null) {
            tooltip.add("Trait: " + trait.getSimpleName());
        }

        // TODO: Show attunement progress
        // TODO: Show constellation effects
    }

    // ========== Helper Methods ==========

    /**
     * Check if player has discovered the required constellation
     *
     * @param player        The player to check
     * @param constellation The constellation to check
     * @return true if discovered
     */
    protected boolean isConstellationDiscovered(EntityPlayer player, IConstellation constellation) {
        // TODO: Implement discovery tracking
        // For now, always return true
        return true;
    }

    /**
     * Apply main constellation (alias)
     *
     * @param stack         The tuned crystal stack
     * @param constellation The constellation to apply
     */
    public void applyMainConstellation(ItemStack stack, IConstellation constellation) {
        setConstellation(stack, constellation);
    }

    /**
     * Get main constellation (alias)
     *
     * @param stack The tuned crystal stack
     * @return The main constellation
     */
    public IConstellation getMainConstellation(ItemStack stack) {
        return getConstellation(stack);
    }

    /**
     * Check if world is server
     */
    @Override
    protected boolean isServer(World world) {
        return !world.isRemote;
    }
}
