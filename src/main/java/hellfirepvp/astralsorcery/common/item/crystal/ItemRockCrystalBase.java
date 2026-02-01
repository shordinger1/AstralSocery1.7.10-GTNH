/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Rock Crystal Base Item - Base class for all rock crystals
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.registry.reference.ItemsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Rock Crystal Base Item
 * <p>
 * Base class for all rock crystal items.
 * <p>
 * Features:
 * - Single item, not stackable
 * - Stores crystal properties in NBT
 * - Can fracture over time
 * - Has custom entity rendering (TODO)
 * <p>
 * Subclasses:
 * - ItemRockCrystalSimple - Basic crystal
 * - ItemTunedCrystalBase - Tuned crystals (inherits from this)
 * - ItemCelestialCrystal - Celestial crystal
 * <p>
 * TODO:
 * - Implement crystal entity rendering
 * - Implement crystal degradation logic
 * - Implement crystal size visualization
 * - Implement tooltip with crystal stats
 */
public class ItemRockCrystalBase extends AstralBaseItem {

    private static final String TAG_CRYSTAL_PROPERTIES = "crystalProperties";

    @SideOnly(Side.CLIENT)
    private IIcon iconCrystal;

    public ItemRockCrystalBase() {
        super();
        setMaxStackSize(1); // Crystals are not stackable
        setHasSubtypes(true); // Support for different crystal types
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        // Subclasses should override this
        iconCrystal = IconHelper.registerIcon(register, "crystal_rock");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return iconCrystal;
    }

    // ========== Crystal Properties ==========

    /**
     * Get crystal properties from item stack
     *
     * @param stack The crystal item stack
     * @return Crystal properties, or null if not present
     */
    public CrystalProperties getProperties(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return null;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey(TAG_CRYSTAL_PROPERTIES)) {
            NBTTagCompound propsNbt = nbt.getCompoundTag(TAG_CRYSTAL_PROPERTIES);
            return CrystalProperties.readFromNBT(propsNbt);
        }

        return null;
    }

    /**
     * Set crystal properties on item stack
     *
     * @param stack      The crystal item stack
     * @param properties The properties to set
     */
    public void setProperties(ItemStack stack, CrystalProperties properties) {
        if (stack == null || properties == null) {
            return;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        NBTTagCompound propsNbt = properties.writeToNBT(new NBTTagCompound());
        nbt.setTag(TAG_CRYSTAL_PROPERTIES, propsNbt);
    }

    /**
     * Get or create crystal properties
     * <p>
     * If properties don't exist, creates random ones
     *
     * @param stack The crystal item stack
     * @return Crystal properties
     */
    public CrystalProperties getOrCreateProperties(ItemStack stack) {
        CrystalProperties props = getProperties(stack);
        if (props == null) {
            props = CrystalProperties.createRandomRock();
            setProperties(stack, props);
        }
        return props;
    }

    /**
     * Get maximum crystal size
     * <p>
     * Subclasses should override this for different max sizes
     *
     * @return Maximum size
     */
    public int getMaxSize() {
        return 400; // Default max size for rock crystals
    }

    /**
     * Get tuned item variant
     * <p>
     * Abstract method - subclasses should return the tuned version
     *
     * @return Tuned item class, or null if not applicable
     */
    public Class<? extends Item> getTunedItemVariant() {
        // Subclasses override this
        return null;
    }

    // ========== Update Logic ==========

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        super.onUpdate(stack, world, entity, slot, isSelected);

        // Only update on server side
        if (world.isRemote) {
            return;
        }

        // Crystal degradation logic
        // TODO: Implement gradual fracturing over time
        // For now, crystals don't degrade automatically

        // ========== Entity ==========
    }

    public boolean hasCustomEntity(ItemStack stack) {
        // TODO: Return true for custom crystal entity rendering
        // For now, return false to use default entity
        return false;
    }

    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        // TODO: Create EntityCrystal for special rendering
        // For now, return null to use default entity
        return null;
    }

    // ========== Display ==========

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        CrystalProperties props = getProperties(stack);
        if (props == null) {
            tooltip.add("No crystal data");
            return;
        }

        // Display crystal statistics
        tooltip.add("Size: " + props.getSize() + " / " + props.getMaxSize());
        tooltip.add("Purity: " + props.getPurity() + "%");
        tooltip.add("Efficiency: " + props.getCollectiveCapability() + "%");

        if (props.getFractured() > 0) {
            tooltip.add("Fractured: " + props.getFractured() + "%");
        }

        // TODO: Add efficiency calculation
        // TODO: Add constellation attunement info (for tuned crystals)
    }

    // ========== Helper Methods ==========

    /**
     * Check if crystal is broken
     *
     * @param stack The crystal item stack
     * @return true if fractured >= 100
     */
    public boolean isBroken(ItemStack stack) {
        CrystalProperties props = getProperties(stack);
        return props != null && props.isBroken();
    }

    /**
     * Check if world is client
     */
    protected boolean isClient(World world) {
        return world.isRemote;
    }

    /**
     * Check if world is server
     */
    protected boolean isServer(World world) {
        return !world.isRemote;
    }

    /**
     * Get or create NBT tag compound
     */
    protected NBTTagCompound getOrCreateNBT(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }

    /**
     * Create random base crystal item
     * 1.7.10: Uses rockCrystalSimple instead of rockCrystal
     *
     * @return ItemStack with random crystal properties
     */
    public static ItemStack createRandomBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystalSimple);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomRock());
        return crystal;
    }

    /**
     * Create random celestial crystal item
     *
     * @return ItemStack with random celestial crystal properties
     */
    public static ItemStack createRandomCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomCelestial());
        return crystal;
    }
}
