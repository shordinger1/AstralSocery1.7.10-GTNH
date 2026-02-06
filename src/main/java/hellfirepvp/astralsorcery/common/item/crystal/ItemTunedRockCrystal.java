/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tuned Rock Crystal Item - Rock crystal attuned to constellations
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
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
 * Migrated from 1.12.2 with full API compatibility
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

    @Override
    public Class<? extends Item> getTunedItemVariant() {
        // This IS the tuned variant
        // 1.7.10: Return the class instead of 'this' to match parent method signature
        return ItemTunedRockCrystal.class;
    }

    /**
     * Add all constellation variants to creative tab
     * Migrated from 1.12.2
     * 1.7.10: Removed isInCreativeTab() check - method doesn't exist in 1.7.10
     */
    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        // 1.7.10: Instead of isInCreativeTab(tab), check if tab matches
        if (tab == this.getCreativeTab() || tab == CreativeTabs.tabAllSearch) {
            ItemStack stack;
            for (IWeakConstellation c : ConstellationRegistry.getWeakConstellations()) {
                stack = new ItemStack(this);
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxRockProperties());
                applyMainConstellation(stack, c);
                list.add(stack);
            }
        }
    }

    // ========== Display ==========

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        // Call parent to show constellation info
        // 1.7.10: Changed signature from List<String> to List to match parent class
        super.addInformation(stack, player, tooltip, advanced);

        // Add additional info
        // 1.7.10: Use static method getMainConstellation() instead of instance getConstellation()
        IConstellation constellation = getMainConstellation(stack);
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
        // 1.7.10: Use static method applyMainConstellation() instead of instance setConstellation()
        if (constellation != null && constellation instanceof IWeakConstellation) {
            applyMainConstellation(stack, (IWeakConstellation) constellation);
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
    public ItemStack createCrystal(IConstellation constellation, IMinorConstellation trait) {
        ItemStack stack = createCrystal(constellation, 1);

        // 1.7.10: Use static method applyTrait() instead of instance setTrait()
        if (trait != null) {
            applyTrait(stack, trait);
        }

        return stack;
    }
}
