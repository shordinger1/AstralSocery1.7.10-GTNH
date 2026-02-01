/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tuned Celestial Crystal Item - Celestial crystal attuned to constellations
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Tuned Celestial Crystal Item
 * <p>
 * A celestial crystal that has been attuned to a constellation.
 * <p>
 * Features:
 * - Celestial crystal base (max size 900)
 * - Primary constellation attunement
 * - Trait constellation bonus
 * - Glows with constellation color (TODO)
 * <p>
 * Combines properties of:
 * - ItemCelestialCrystal (larger size, pure)
 * - ItemTunedCrystalBase (constellation attunement)
 * <p>
 * Uses:
 * - Advanced constellation-specific crafting
 * - High-tier rituals
 * - Celestial machine components
 * <p>
 * Created by:
 * - Advanced attunement altar rituals
 * - Special celestial events
 * <p>
 * TODO:
 * - Implement constellation-colored glow
 * - Implement constellation-specific effects
 * - Implement advanced crafting recipes
 * - Implement particle effects based on constellation
 */
public class ItemTunedCelestialCrystal extends ItemTunedCrystalBase {

    @SideOnly(Side.CLIENT)
    private IIcon iconCrystal;

    public ItemTunedCelestialCrystal() {
        super();
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        // Use celestial crystal texture
        iconCrystal = IconHelper.registerIcon(register, "crystal_celestial");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return iconCrystal;
    }

    public int getMaxSize() {
        return 900; // Celestial crystal max size
    }

    // ========== Display ==========

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        // Add celestial-specific info
        IConstellation constellation = getConstellation(stack);
        if (constellation != null) {
            tooltip.add("Aligned with: " + constellation.getSimpleName());

            // TODO: Add constellation-specific description
            // TODO: Show trait effects
        }

        CrystalProperties props = getProperties(stack);
        if (props != null) {
            double efficiency = props.getEfficiencyMultiplier();
            tooltip.add(String.format("Efficiency: %.2fx", efficiency));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        // Show enchantment effect for tuned celestial crystals
        return true;
    }

    // ========== Helper Methods ==========

    /**
     * Create a tuned celestial crystal with the specified constellation
     *
     * @param constellation The constellation to attune
     * @param count         Stack size
     * @return Crystal item stack
     */
    public ItemStack createCrystal(IConstellation constellation, int count) {
        ItemStack stack = new ItemStack(this, count, 0);

        // Create max-tier celestial crystal properties
        CrystalProperties props = new CrystalProperties();
        props.setSize(900); // Max size
        props.setPurity(100); // Always pure
        props.setCollectiveCapability(100); // Max efficiency
        props.setFractured(0); // No damage

        setProperties(stack, props);

        // Apply constellation
        if (constellation != null) {
            setConstellation(stack, constellation);
        }

        return stack;
    }

    /**
     * Create a tuned celestial crystal with constellation and trait
     *
     * @param constellation Primary constellation
     * @param trait         Trait constellation
     * @return Crystal item stack
     */
    public ItemStack createCrystal(IConstellation constellation, IConstellation trait) {
        ItemStack stack = createCrystal(constellation, 1);

        if (trait != null) {
            setTrait(stack, trait);
        }

        return stack;
    }
}
