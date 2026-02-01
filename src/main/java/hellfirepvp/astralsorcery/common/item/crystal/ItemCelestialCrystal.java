/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Celestial Crystal Item - Crystal from constellations
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
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Celestial Crystal Item
 * <p>
 * A crystal formed from constellation light.
 * <p>
 * Features:
 * - Larger size capacity (max 900 vs rock crystal's 400)
 * - Used in advanced crafting
 * - Can be attuned to constellations
 * - Glows with light (TODO: 1.7.10 light rendering)
 * <p>
 * Sources:
 * - Celestial Altar rituals
 * - Rare drops from special mobs
 * - Found in special structures
 * <p>
 * Uses:
 * - Advanced crafting recipes
 * - High-tier tool enhancement
 * - Celestial Collector Crystal creation
 * <p>
 * TODO:
 * - Implement dynamic light emission
 * - Implement crystal growth logic
 * - Implement constellation attunement
 * - Implement particle glow effects
 * - Implement special rendering
 */
public class ItemCelestialCrystal extends ItemRockCrystalBase {

    @SideOnly(Side.CLIENT)
    private IIcon iconCrystal;

    public ItemCelestialCrystal() {
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
        return 900; // Celestial crystals can be larger
    }

    public Class<? extends Item> getTunedItemVariant() {
        // Tuned variant is ItemTunedCelestialCrystal
        return ItemTunedCelestialCrystal.class;
    }

    // ========== Display ==========

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        // Add celestial-specific info
        CrystalProperties props = getProperties(stack);
        if (props != null) {
            double efficiency = props.getEfficiencyMultiplier();
            tooltip.add(String.format("§7Efficiency: §e%.2fx", efficiency));
        }

        // Celestial crystals emit light
        tooltip.add("§7Light Level: §b15 (Max)");
        tooltip.add("§7Type: §6Celestial Crystal");

        // TODO: Show constellation attunement status when attunement system is implemented
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        // Show enchantment effect for celestial crystals
        return true;
    }

    // ========== Helper Methods ==========

    /**
     * Create a celestial crystal with specified size
     *
     * @param size  Crystal size (0-900)
     * @param count Stack size
     * @return Crystal item stack
     */
    public ItemStack createCrystal(int size, int count) {
        ItemStack stack = new ItemStack(this, count, 0);

        CrystalProperties props = new CrystalProperties();
        props.setSize(size);
        props.setPurity(100); // Celestial crystals are always pure
        props.setCollectiveCapability(100);
        props.setFractured(0);

        setProperties(stack, props);
        return stack;
    }

    /**
     * Create a max-sized celestial crystal
     *
     * @param count Stack size
     * @return Crystal item stack
     */
    public ItemStack createMaxCrystal(int count) {
        return createCrystal(900, count);
    }
}
