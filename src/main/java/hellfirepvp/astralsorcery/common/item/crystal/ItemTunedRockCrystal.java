/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tuned Rock Crystal Item - Rock crystal attuned to constellations
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Tuned Rock Crystal Item
 * <p>
 * A rock crystal that has been attuned to a constellation.
 * <p>
 * Features:
 * - Rock crystal base (max size 400)
 * - Primary constellation attunement
 * - Trait constellation bonus
 * - Used in crafting and rituals
 * <p>
 * Created by:
 * - Attunement Altar ritual
 * - Crafting with constellation paper
 * <p>
 * Uses:
 * - Crafting ingredient for constellation-specific items
 * - Ritual components
 * - Tool enhancement
 * <p>
 * TODO:
 * - Implement attunement ritual
 * - Implement constellation-specific crafting recipes
 * - Implement constellation trait effects
 * - Implement particle effects for attuned crystals
 */
public class ItemTunedRockCrystal extends ItemTunedCrystalBase {

    @SideOnly(Side.CLIENT)
    private IIcon iconCrystal;

    public ItemTunedRockCrystal() {
        super();
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        // Use the same icon as rock crystal
        iconCrystal = IconHelper.registerIcon(register, "crystal_rock");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return iconCrystal;
    }

    public int getMaxSize() {
        return 400; // Rock crystal max size
    }

    public Class<? extends Item> getTunedItemVariant() {
        // This is already the tuned variant
        return null;
    }

    // ========== Display ==========

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // Call parent to show constellation info
        super.addInformation(stack, player, tooltip, advanced);

        // Add additional info
        IConstellation constellation = getConstellation(stack);
        if (constellation != null) {
            // TODO: Add constellation-specific description
            // TODO: Show trait effects
        }
    }

    // ========== Helper Methods ==========

    /**
     * Create a tuned rock crystal with the specified constellation
     *
     * @param constellation The constellation to attune
     * @param count         Stack size
     * @return Crystal item stack
     */
    public ItemStack createCrystal(IConstellation constellation, int count) {
        ItemStack stack = new ItemStack(this, count, 0);

        // Create base crystal properties
        CrystalProperties props = CrystalProperties.createMaxRock();
        setProperties(stack, props);

        // Apply constellation
        if (constellation != null) {
            setConstellation(stack, constellation);
        }

        return stack;
    }

    /**
     * Create a tuned rock crystal with constellation and trait
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
