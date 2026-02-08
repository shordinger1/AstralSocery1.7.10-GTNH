/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tuned Celestial Crystal Item - Migrated from 1.12.2
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Tuned Celestial Crystal Item
 * <p>
 * A celestial crystal that has been attuned to a constellation.
 * <p>
 * Migrated from 1.12.2 with full API compatibility.
 * <p>
 * Features:
 * - Celestial crystal base (max size 900)
 * - Primary constellation attunement
 * - Trait constellation bonus
 * - Celestial blue highlight color
 * - Epic rarity
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

    @Override
    public int getMaxSize() {
        return CrystalProperties.MAX_SIZE_CELESTIAL;
    }

    @Override
    public Class<? extends Item> getTunedItemVariant() {
        // This IS the tuned variant
        // 1.7.10: Return the class instead of 'this' to match parent method signature
        return ItemTunedCelestialCrystal.class;
    }

    /**
     * Get highlight color for celestial crystals
     * Migrated from 1.12.2 - celestial blue color
     */
    @SideOnly(Side.CLIENT)
    public Color getHightlightColor(ItemStack stack) {
        // REMOVED: BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor
        // 1.7.10 doesn't have BlockCollectorCrystalBase with CollectorCrystalType enum
        // Using hardcoded celestial blue color instead
        return new Color(0x00, 0x88, 0xFF);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // REMOVED: RegistryItems.rarityCelestial
        // 1.7.10: Use epic rarity instead of custom rarityCelestial
        return EnumRarity.epic;
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
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
                applyMainConstellation(stack, c);
                list.add(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        // Show enchantment effect for tuned celestial crystals
        return true;
    }
}
