/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tool Crystal Properties - Stores crystal tool attributes
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * ToolCrystalProperties - Crystal tool properties (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Extends CrystalProperties with tool-specific attributes</li>
 * <li>Can merge multiple crystal properties into tool properties</li>
 * <li>Handles tool damage and efficiency calculations</li>
 * <li>Serializes/deserializes from NBT</li>
 * <li>Calculates mining speed and damage multipliers</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>NBTHelper.getInteger() → compound.hasKey() + compound.getInteger()</li>
 * <li>NBTHelper.getPersistentData() → stack.getTagCompound()</li>
 * <li>Added static helper methods for ItemStack integration</li>
 * </ul>
 * <p>
 * <b>Key Attributes (inherited):</b>
 * <ul>
 * <li>size (0-400 for rock, 0-900 for celestial)</li>
 * <li>purity (0-100) - Affects damage chance</li>
 * <li>collectiveCapability (0-100) - Affects efficiency and damage resistance</li>
 * <li>fractured (0-100) - Damage level, tool breaks at 100</li>
 * <li>sizeOverride (-1 for none)</li>
 * </ul>
 * <p>
 * <b>Damage Mechanics:</b>
 * <ul>
 * <li>Higher collectiveCapability = Less damage chance</li>
 * <li>Higher purity = Less damage chance</li>
 * <li>Damage can reduce collectiveCapability (copyDamagedCutting)</li>
 * li>When size reaches 0, tool breaks</li>
 * </ul>
 */
public class ToolCrystalProperties extends CrystalProperties {

    /**
     * Constructor
     *
     * @param size                 Crystal size
     * @param purity               Crystal purity (0-100)
     * @param collectiveCapability Efficiency (0-100)
     * @param fracturation         Damage level (0-100)
     * @param sizeOverride         Custom max size (-1 for none)
     */
    public ToolCrystalProperties(int size, int purity, int collectiveCapability, int fracturation, int sizeOverride) {
        super(size, purity, collectiveCapability, fracturation, sizeOverride);
    }

    /**
     * Merge multiple crystal properties into tool properties
     * Used when crafting tools from multiple crystals
     *
     * @param properties Properties to merge
     * @return Merged tool properties
     */
    public static ToolCrystalProperties merge(CrystalProperties... properties) {
        return merge(Arrays.asList(properties));
    }

    /**
     * Merge a list of crystal properties into tool properties
     *
     * @param properties List of properties to merge
     * @return Merged tool properties
     */
    public static ToolCrystalProperties merge(List<CrystalProperties> properties) {
        int totalSize = 0;
        int totalPurity = 0;
        int totalCollectivity = 0;
        int frac = 0;
        int ovr = 0;

        for (CrystalProperties c : properties) {
            totalSize += c.getSize();
            totalPurity += c.getPurity();
            totalCollectivity += c.getCollectiveCapability();
            frac += c.getFracturation();
            if (c.getSizeOverride() >= 0) {
                ovr += (c.getSizeOverride() - MAX_SIZE_CELESTIAL);
            }
        }

        if (ovr != 0) {
            ovr /= properties.size();
            ovr += MAX_SIZE_CELESTIAL * properties.size();
        } else {
            ovr = -1;
        }

        return new ToolCrystalProperties(
            totalSize,
            totalPurity / properties.size(),
            totalCollectivity / properties.size(),
            frac / properties.size(),
            ovr);
    }

    /**
     * Read tool crystal properties from NBT
     * 1.7.10: Direct NBT access instead of NBTHelper
     *
     * @param compound NBT compound to read from
     * @return ToolCrystalProperties instance
     */
    public static ToolCrystalProperties readFromNBT(NBTTagCompound compound) {
        if (compound == null || !compound.hasKey("crystalProperties")) {
            return null;
        }

        NBTTagCompound prop = compound.getCompoundTag("crystalProperties");
        ToolCrystalProperties tcp = new ToolCrystalProperties(0, 0, 0, 0, -1);
        tcp.setSize(prop.getInteger("size"));
        tcp.setPurity(prop.getInteger("purity"));
        tcp.setCollectiveCapability(prop.getInteger("collectiveCapability"));
        tcp.setFractured(prop.getInteger("fract"));

        // 1.7.10: Check if key exists before reading
        if (prop.hasKey("sizeOverride")) {
            tcp.setSizeOverride(prop.getInteger("sizeOverride"));
        }

        return tcp;
    }

    /**
     * Read tool crystal properties from ItemStack
     * Convenience method for ItemStack integration
     *
     * @param stack ItemStack to read from
     * @return ToolCrystalProperties, or null if not found
     */
    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) {
            return null;
        }
        return readFromNBT(stack.getTagCompound());
    }

    /**
     * Write tool crystal properties to ItemStack
     * Convenience method for ItemStack integration
     *
     * @param stack      ItemStack to write to
     * @param properties Properties to write
     */
    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        if (stack == null || properties == null) {
            return;
        }

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
        if (properties.getSizeOverride() >= 0) {
            crystalProp.setInteger("sizeOverride", properties.getSizeOverride());
        }

        cmp.setTag("crystalProperties", crystalProp);
    }

    /**
     * Create a damaged copy for cutting tools
     * Reduces collective capability by 1
     *
     * @return New ToolCrystalProperties with reduced capability
     */
    public ToolCrystalProperties copyDamagedCutting() {
        return new ToolCrystalProperties(
            this.getSize(),
            this.getPurity(),
            Math.max(0, this.getCollectiveCapability() - 1),
            this.getFractured(),
            this.getSizeOverride());
    }

    /**
     * Create a copy after grinding
     * Calls parent grindCopy() and wraps result
     *
     * @param rand Random instance
     * @return Ground tool properties, or null if tool should break
     */
    @Nullable
    public ToolCrystalProperties grindCopy(Random rand) {
        // Call parent class grindCopy() method
        CrystalProperties out = super.grindCopy(rand);
        if (out == null) return null;
        return new ToolCrystalProperties(
            out.getSize(),
            out.getPurity(),
            out.getCollectiveCapability(),
            out.getFractured(),
            out.getSizeOverride());
    }

    /**
     * Get efficiency multiplier for tool
     * Based on collective capability
     * Formula: sqrt(collectiveCapability / 100)
     * Minimum: 0.05F, Maximum: 1.0F
     *
     * @return Efficiency multiplier (0.05 - 1.0)
     */
    public float getEfficiencyMultiplier() {
        float mult = ((float) getCollectiveCapability()) / 100F;
        return Math.max(0.05F, (float) Math.sqrt(mult));
    }

    /**
     * Calculate damage chance multiplier
     * Based on collective capability
     * Lower value = Lower damage chance
     * Formula: pow(collectiveCapability / 100, 2)
     *
     * @return Damage chance (0.0 - 1.0)
     */
    public double getDamageChance() {
        return Math.pow(((double) getCollectiveCapability()) / 100D, 2);
    }

    /**
     * Calculate purity chance
     * Based on purity attribute
     * Lower value = Lower chance
     *
     * @return Purity chance (0.0 - 1.0)
     */
    public double getPurityChance() {
        return ((double) getPurity()) / 100D;
    }

    /**
     * Check if tool should be damaged
     * Based on collective capability and random chance
     *
     * @param rand Random instance
     * @return true if tool should be damaged
     */
    public boolean shouldDamage(Random rand) {
        return getDamageChance() >= rand.nextFloat();
    }

    /**
     * Check if tool should be damaged based on purity
     *
     * @param rand Random instance
     * @return true if tool should be damaged
     */
    public boolean shouldDamagePurity(Random rand) {
        return getPurityChance() <= rand.nextFloat();
    }

}
