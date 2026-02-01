/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal Properties - Stores crystal attributes
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Crystal Properties
 * <p>
 * Stores the attributes of a crystal that affect its behavior and effectiveness.
 * <p>
 * Properties:
 * - size (0-400 for rock, 0-900 for celestial): The size/capacity of the crystal
 * - purity (0-100): The purity/quality of the crystal
 * - collectiveCapability (0-100): Efficiency multiplier
 * - fractured (0-100): Damage accumulated (breaks at 100%)
 * - sizeOverride: Custom max size (-1 for none)
 * <p>
 * TODO:
 * - Implement attribute degradation over time
 * - Implement attribute randomization
 * - Implement attribute merging for tools
 */
public class CrystalProperties {

    private static final Random rand = new Random();

    public static final int MAX_SIZE_ROCK = 400;
    public static final int MAX_SIZE_CELESTIAL = 900;

    private static final CrystalProperties MAXED_ROCK_PROPERTIES = new CrystalProperties(
        MAX_SIZE_ROCK,
        100,
        100,
        0,
        -1);
    private static final CrystalProperties MAXED_CELESTIAL_PROPERTIES = new CrystalProperties(
        MAX_SIZE_CELESTIAL,
        100,
        100,
        0,
        -1);

    private int size;
    private int purity;
    private int collectiveCapability;
    private int fractured;
    private int sizeOverride;

    /**
     * Create default crystal properties
     */
    public CrystalProperties() {
        this(0, 0, 0, 0, -1);
    }

    /**
     * Create crystal properties with specified values
     *
     * @param size                 Crystal size
     * @param purity               Crystal purity (0-100)
     * @param collectiveCapability Efficiency (0-100)
     * @param fractured            Damage level (0-100)
     */
    public CrystalProperties(int size, int purity, int collectiveCapability, int fractured) {
        this(size, purity, collectiveCapability, fractured, -1);
    }

    /**
     * Create crystal properties with all values
     *
     * @param size                 Crystal size
     * @param purity               Crystal purity (0-100)
     * @param collectiveCapability Efficiency (0-100)
     * @param fractured            Damage level (0-100)
     * @param sizeOverride         Custom max size (-1 for none)
     */
    public CrystalProperties(int size, int purity, int collectiveCapability, int fractured, int sizeOverride) {
        this.size = size;
        this.purity = Math.max(0, Math.min(100, purity));
        this.collectiveCapability = Math.max(0, Math.min(100, collectiveCapability));
        this.fractured = Math.max(0, Math.min(100, fractured));
        this.sizeOverride = sizeOverride;
    }

    /**
     * Get crystal size
     *
     * @return Crystal size
     */
    public int getSize() {
        return size;
    }

    /**
     * Set crystal size
     *
     * @param size New size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Get crystal purity
     *
     * @return Purity (0-100)
     */
    public int getPurity() {
        return purity;
    }

    /**
     * Set crystal purity
     *
     * @param purity New purity (0-100)
     */
    public void setPurity(int purity) {
        this.purity = Math.max(0, Math.min(100, purity));
    }

    /**
     * Get collective capability (efficiency)
     *
     * @return Capability (0-100)
     */
    public int getCollectiveCapability() {
        return collectiveCapability;
    }

    /**
     * Set collective capability
     *
     * @param capability New capability (0-100)
     */
    public void setCollectiveCapability(int capability) {
        this.collectiveCapability = Math.max(0, Math.min(100, capability));
    }

    /**
     * Get fractured damage
     *
     * @return Fractured level (0-100)
     */
    public int getFractured() {
        return fractured;
    }

    /**
     * Get fractured damage (alias for 1.12.2 compatibility)
     *
     * @return Fractured level (0-100)
     */
    public int getFracturation() {
        return fractured;
    }

    /**
     * Set fractured damage
     *
     * @param fractured New fractured level (0-100)
     */
    public void setFractured(int fractured) {
        this.fractured = Math.max(0, Math.min(100, fractured));
    }

    /**
     * Increase fractured damage
     *
     * @param amount Amount to increase
     */
    public void increaseFractured(int amount) {
        this.fractured = Math.min(100, this.fractured + amount);
    }

    /**
     * Check if crystal is broken
     *
     * @return true if fractured >= 100
     */
    public boolean isBroken() {
        return fractured >= 100;
    }

    /**
     * Get size override
     *
     * @return Custom max size, or -1 if none
     */
    public int getSizeOverride() {
        return sizeOverride;
    }

    /**
     * Set size override
     *
     * @param sizeOverride Custom max size, or -1 for none
     */
    public void setSizeOverride(int sizeOverride) {
        this.sizeOverride = sizeOverride;
    }

    /**
     * Get actual max size
     *
     * @return Max size (considering override)
     */
    public int getMaxSize() {
        if (sizeOverride > 0) {
            return sizeOverride;
        }
        return 400; // Default max size for rock crystals
    }

    /**
     * Calculate efficiency multiplier
     * <p>
     * Formula: sqrt(collectiveCapability / 100)
     *
     * @return Efficiency multiplier (0.0 - 1.0)
     */
    public float getEfficiencyMultiplier() {
        return (float) Math.sqrt(collectiveCapability / 100.0);
    }

    /**
     * Serialize to NBT
     *
     * @param NBT compound to write to
     * @return The NBT compound
     */
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (nbt == null) {
            nbt = new NBTTagCompound();
        }

        nbt.setInteger("size", size);
        nbt.setInteger("purity", purity);
        nbt.setInteger("collectiveCapability", collectiveCapability);
        nbt.setInteger("fract", fractured);
        nbt.setInteger("sizeOverride", sizeOverride);

        return nbt;
    }

    /**
     * Deserialize from NBT
     *
     * @param nbt NBT compound to read from
     * @return CrystalProperties instance
     */
    public static CrystalProperties readFromNBT(NBTTagCompound nbt) {
        if (nbt == null) {
            return new CrystalProperties();
        }

        CrystalProperties props = new CrystalProperties();
        props.size = nbt.getInteger("size");
        props.purity = nbt.getInteger("purity");
        props.collectiveCapability = nbt.getInteger("collectiveCapability");
        props.fractured = nbt.getInteger("fract");
        props.sizeOverride = nbt.getInteger("sizeOverride");

        return props;
    }

    /**
     * Create random rock crystal properties
     *
     * @return Random properties
     */
    public static CrystalProperties createRandomRock() {
        // TODO: Implement proper random generation
        // For now, use mid-range values
        CrystalProperties props = new CrystalProperties();
        props.size = 200 + (int) (Math.random() * 200); // 200-400
        props.purity = 50 + (int) (Math.random() * 50); // 50-100
        props.collectiveCapability = 50 + (int) (Math.random() * 50); // 50-100
        props.fractured = 0;
        return props;
    }

    /**
     * Create random celestial crystal properties
     *
     * @return Random celestial properties
     */
    public static CrystalProperties createRandomCelestial() {
        // TODO: Implement proper random generation
        // For now, use mid-range values
        CrystalProperties props = new CrystalProperties();
        props.size = 450 + (int) (Math.random() * 450); // 450-900
        props.purity = 40 + (int) (Math.random() * 60); // 40-100
        props.collectiveCapability = 50 + (int) (Math.random() * 50); // 50-100
        props.fractured = 0;
        return props;
    }

    /**
     * Create max-tier rock crystal properties
     *
     * @return Maximum properties
     */
    public static CrystalProperties createMaxRock() {
        return new CrystalProperties(400, 100, 100, 0);
    }

    /**
     * Create structural crystal properties (for blocks)
     *
     * @return Structural properties
     */
    public static CrystalProperties createStructural() {
        return new CrystalProperties(400, 100, 100, 0);
    }

    /**
     * Apply crystal properties to ItemStack
     * 1.7.10: Uses stack.getTagCompound() instead of NBTHelper
     *
     * @param stack      The ItemStack to apply properties to
     * @param properties The properties to apply
     */
    public static void applyCrystalProperties(ItemStack stack, CrystalProperties properties) {
        // 1.7.10: Get or create tag compound
        NBTTagCompound cmp = stack.getTagCompound();
        if (cmp == null) {
            cmp = new NBTTagCompound();
            stack.setTagCompound(cmp);
        }

        NBTTagCompound crystalProp = new NBTTagCompound();
        crystalProp.setInteger("size", properties.getSize());
        crystalProp.setInteger("purity", properties.getPurity());
        crystalProp.setInteger("collectiveCapability", properties.getCollectiveCapability());
        crystalProp.setInteger("fract", properties.getFracturation());
        crystalProp.setInteger("sizeOverride", properties.getSizeOverride());
        cmp.setTag("crystalProperties", crystalProp);
    }

    /**
     * Get crystal properties from ItemStack
     * 1.7.10: Uses stack.getTagCompound() instead of NBTHelper
     *
     * @param stack The ItemStack to read properties from
     * @return CrystalProperties, or null if not found
     */
    public static CrystalProperties getCrystalProperties(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) {
            return null;
        }

        NBTTagCompound cmp = stack.getTagCompound();
        if (!cmp.hasKey("crystalProperties")) {
            return null;
        }

        NBTTagCompound prop = cmp.getCompoundTag("crystalProperties");
        Integer size = prop.getInteger("size");
        Integer purity = prop.getInteger("purity");
        Integer colCap = prop.getInteger("collectiveCapability");
        Integer fract = prop.getInteger("fract");
        Integer sizeOvr = prop.hasKey("sizeOverride") ? prop.getInteger("sizeOverride") : -1;
        return new CrystalProperties(size, purity, colCap, fract, sizeOvr);
    }

    /**
     * Get max size for an ItemStack
     * 1.7.10: Simplified version that doesn't check CrystalPropertyItem interface
     *
     * @param stack The ItemStack to check
     * @return Max size (400 for rock, 900 for celestial, or custom override)
     */
    public static int getMaxSize(ItemStack stack) {
        CrystalProperties prop = getCrystalProperties(stack);
        if (prop != null && prop.sizeOverride != -1) {
            return prop.sizeOverride;
        }

        // 1.7.10: Check stack size instead of isEmpty()
        if (stack == null || stack.stackSize <= 0) {
            return MAX_SIZE_ROCK;
        }

        // TODO: Check for CrystalPropertyItem interface when it's migrated
        // For now, default to rock crystal max size
        return MAX_SIZE_ROCK;
    }

    /**
     * Add crystal property tooltip to list
     * 1.7.10: Simplified version without progression tier checks
     *
     * @param prop    Crystal properties
     * @param tooltip Tooltip list to add to
     * @param maxSize Max size for color coding
     */
    public static void addPropertyTooltip(CrystalProperties prop, java.util.List<String> tooltip, int maxSize) {
        if (prop == null) {
            return;
        }

        // 1.7.10: Simple tooltip without progression tier checks
        // 1.12.2 has colored tooltips based on progression tier
        // 1.7.10 version uses basic color codes
        int size = prop.getSize();
        String sizeColor = size > maxSize ? "\u00a7b" : (size == maxSize ? "\u00a7e" : "\u00a77");
        tooltip.add("  Size: " + sizeColor + size);

        int purity = prop.getPurity();
        String purityColor = purity > 100 ? "\u00a7b" : (purity == 100 ? "\u00a76" : "\u00a77");
        tooltip.add("  Purity: " + purityColor + purity + "%");

        int collect = prop.getCollectiveCapability();
        String collectColor = collect > 100 ? "\u00a7b" : (collect == 100 ? "\u00a76" : "\u00a77");
        tooltip.add("  Collective: " + collectColor + collect + "%");

        int fract = prop.getFractured();
        if (fract > 0) {
            tooltip.add("  \u00a74Fractured: " + fract + "%");
        }
    }

    /**
     * Create maxed rock crystal properties
     *
     * @return Max rock crystal properties
     */
    public static CrystalProperties getMaxRockProperties() {
        return MAXED_ROCK_PROPERTIES;
    }

    /**
     * Create maxed celestial crystal properties
     *
     * @return Max celestial crystal properties
     */
    public static CrystalProperties getMaxCelestialProperties() {
        return MAXED_CELESTIAL_PROPERTIES;
    }

    /**
     * Create random rock crystal properties
     *
     * @return Random rock crystal properties
     */
    // public static CrystalProperties createRandomRock() {
    // int size = Math.max(1, (rand.nextInt(MAX_SIZE_ROCK) + rand.nextInt(MAX_SIZE_ROCK)) / 2);
    // int purity = (rand.nextInt(101) + rand.nextInt(101)) / 2;
    // int collect = 5 + rand.nextInt(26);
    // return new CrystalProperties(size, purity, collect, 0, -1);
    // }

    /**
     * Create random celestial crystal properties
     *
     * @return Random celestial crystal properties
     */
    // public static CrystalProperties createRandomCelestial() {
    // int size = Math.max(1, (rand.nextInt(MAX_SIZE_CELESTIAL) + rand.nextInt(MAX_SIZE_CELESTIAL)) / 2);
    // int purity = 40 + rand.nextInt(61);
    // int collect = 50 + rand.nextInt(26);
    // return new CrystalProperties(size, purity, collect, 0, -1);
    // }

    /**
     * Create a copy after grinding
     * Used in grinding mechanics to reduce size and increase collective capability
     *
     * @param rand Random instance
     * @return Ground crystal properties, or null if crystal should break
     */
    @Nullable
    public CrystalProperties grindCopy(Random rand) {
        CrystalProperties copy = new CrystalProperties(size, purity, collectiveCapability, fractured, sizeOverride);
        int grind = 7 + rand.nextInt(5);
        double purityVal = ((double) this.purity) / 100D;
        if (purityVal <= 0.4) purityVal = 0.4;
        for (int j = 0; j < 3; j++) {
            if (purityVal <= rand.nextFloat()) {
                grind += grind;
            }
        }
        int collectToAdd = 3 + rand.nextInt(4);
        copy.size = size - grind;
        copy.collectiveCapability = Math
            .min((collectiveCapability > 100 ? collectiveCapability : 100), collectiveCapability + collectToAdd);
        if (copy.size <= 0) return null;
        return copy;
    }
}
