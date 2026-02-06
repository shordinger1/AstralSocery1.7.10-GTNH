/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tuned Crystal Base Item - Base class for constellation-tuned crystals
 * Migrated from 1.12.2 with full API compatibility
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * Tuned Crystal Base Item (1.7.10 port from 1.12.2)
 * <p>
 * Base class for crystals attuned to constellations.
 * Implements ItemConstellationFocus for constellation-linked functionality.
 * <p>
 * API compatible with 1.12.2 version.
 * <p>
 * Static methods for constellation manipulation (matching 1.12.2 API):
 * - {@link #applyMainConstellation(ItemStack, IWeakConstellation)}
 * - {@link #getMainConstellation(ItemStack)}
 * - {@link #applyTrait(ItemStack, IMinorConstellation)}
 * - {@link #getTrait(ItemStack)}
 */
public abstract class ItemTunedCrystalBase extends ItemRockCrystalBase implements ItemConstellationFocus {

    public ItemTunedCrystalBase() {
        super();
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    // ========== 1.12.2 API Compatibility: Static Methods ==========

    /**
     * Apply trait constellation to crystal (1.12.2 API)
     */
    public static void applyTrait(ItemStack stack, IMinorConstellation trait) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;
        if (trait == null) return;

        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cmp.setString("trait", trait.getUnlocalizedName());
    }

    /**
     * Get trait constellation from crystal (1.12.2 API)
     */
    public static IMinorConstellation getTrait(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        String strCName = cmp.getString("trait");
        return (IMinorConstellation) ConstellationRegistry.getConstellationByName(strCName);
    }

    /**
     * Apply main constellation to crystal (1.12.2 API)
     */
    public static void applyMainConstellation(ItemStack stack, IWeakConstellation constellation) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;
        if (constellation == null) return;

        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    /**
     * Get main constellation from crystal (1.12.2 API)
     */
    public static IWeakConstellation getMainConstellation(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    // ========== ItemConstellationFocus Implementation ==========

    @Override
    public IConstellation getFocusConstellation(ItemStack stack) {
        return getMainConstellation(stack);
    }

    // ========== Instance Methods (Convenience wrappers) ==========

    /**
     * Check if crystal has a constellation
     */
    public boolean hasConstellation(ItemStack stack) {
        return getMainConstellation(stack) != null;
    }

    /**
     * Check if crystal has a trait
     */
    public boolean hasTrait(ItemStack stack) {
        return getTrait(stack) != null;
    }

    // ========== Abstract Methods ==========

    /**
     * Get the tuned item variant for this crystal type
     * Subclasses should return themselves if they are the tuned variant
     * 1.7.10: Changed return type to Class to match parent class ItemRockCrystalBase
     */
    @Override
    public abstract Class<? extends Item> getTunedItemVariant();

    /**
     * Get maximum crystal size for this type
     */
    public abstract int getMaxSize();

    // ========== Display ==========

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean adv) {
        // Call parent to show crystal stats
        super.addInformation(stack, player, tooltip, adv);

        // Show constellation information
        IWeakConstellation mainConstellation = getMainConstellation(stack);
        if (mainConstellation != null) {
            tooltip.add("\u00A78Attuned: \u00A7b" + mainConstellation.getSimpleName());
        }

        IMinorConstellation trait = getTrait(stack);
        if (trait != null) {
            tooltip.add("\u00A78Trait: \u00A7b" + trait.getSimpleName());
        }

        // TODO: Show attunement progress
        // TODO: Show constellation effects
    }

    /**
     * Get sub-items for creative tab
     * Subclasses should override this to add constellation variants
     */
    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        // Base implementation - subclasses should override to add variants
        super.getSubItems(item, tab, list);
    }
}
