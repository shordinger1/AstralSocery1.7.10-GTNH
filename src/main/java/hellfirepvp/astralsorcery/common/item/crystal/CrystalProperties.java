/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalProperties
 * Created by HellFirePvP
 * Date: 01.08.2016 / 22:21
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

    protected int size; // (theoretically) 0 to X
    protected int purity; // 0 to 100 where 100 being completely pure.
    protected int collectiveCapability; // 0 to 100 where 100 being best collection rate.
    protected int fractured = 0; // 0 to 100 where 100 means the crystal should shatter due to its integrity being too
                                 // damaged
    protected int sizeOverride = -1; // Set to -1 = no override

    public CrystalProperties(int size, int purity, int collectiveCapability, int fractured, int sizeOverride) {
        this.size = size;
        this.purity = purity;
        this.collectiveCapability = collectiveCapability;
        this.fractured = fractured;
        this.sizeOverride = sizeOverride;
    }

    public int getSize() {
        return size;
    }

    public int getPurity() {
        return purity;
    }

    public int getCollectiveCapability() {
        return collectiveCapability;
    }

    public int getFracturation() {
        return fractured;
    }

    public int getSizeOverride() {
        return sizeOverride;
    }

    public static CrystalProperties readFromNBT(NBTTagCompound compound) {
        CrystalProperties prop = new CrystalProperties(0, 0, 0, 0, -1);
        prop.size = compound.getInteger("size");
        prop.purity = compound.getInteger("purity");
        prop.collectiveCapability = compound.getInteger("collect");
        prop.fractured = NBTHelper.getInteger(compound, "fract", 0);
        prop.sizeOverride = NBTHelper.getInteger(compound, "sizeOverride", -1);
        return prop;
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("size", size);
        compound.setInteger("purity", purity);
        compound.setInteger("collect", collectiveCapability);
        compound.setInteger("fract", fractured);
        compound.setInteger("sizeOverride", sizeOverride);
    }

    public static CrystalProperties createStructural() {
        int size = Math.min(
            CrystalProperties.MAX_SIZE_ROCK,
            (CrystalProperties.MAX_SIZE_ROCK / 2) + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK / 2));
        int purity = 60 + rand.nextInt(41);
        int collect = 45 + rand.nextInt(56);
        return new CrystalProperties(size, purity, collect, 0, -1);
    }

    public static CrystalProperties createRandomRock() {
        int size = Math.max(
            1,
            (rand.nextInt(CrystalProperties.MAX_SIZE_ROCK) + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK)) / 2);
        int purity = (rand.nextInt(101) + rand.nextInt(101)) / 2;
        int collect = 5 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect, 0, -1);
    }

    public static CrystalProperties createRandomCelestial() {
        int size = Math.max(
            1,
            (rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL) + rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL))
                / 2);
        int purity = 40 + rand.nextInt(61);
        int collect = 50 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect, 0, -1);
    }

    public static CrystalProperties getMaxRockProperties() {
        return MAXED_ROCK_PROPERTIES;
    }

    public static CrystalProperties getMaxCelestialProperties() {
        return MAXED_CELESTIAL_PROPERTIES;
    }

    public static int getMaxSize(ItemStack stack) {
        CrystalProperties prop = getCrystalProperties(stack);
        if (prop != null && prop.sizeOverride != -1) {
            return prop.sizeOverride;
        }

        if ((stack == null || stack.stackSize <= 0)) {
            return MAX_SIZE_ROCK;
        }

        if (stack.getItem() instanceof CrystalPropertyItem) {
            return ((CrystalPropertyItem) stack.getItem()).getMaxSize(stack);
        }
        if (stack.getItem() instanceof ItemBlock) {
            Block b = ((ItemBlock) stack.getItem()).field_150939_a;
            if (b instanceof CrystalPropertyItem) {
                return ((CrystalPropertyItem) b).getMaxSize(stack);
            }
        }
        return MAX_SIZE_ROCK;
    }

    // 1.7.10 compatibility: Optional doesn't exist - use @Nullable Boolean instead
    @SideOnly(Side.CLIENT)
    @Nullable
    public static Boolean addPropertyTooltip(CrystalProperties prop, List<String> tooltip, int maxSize) {
        return addPropertyTooltip(prop, tooltip, GuiScreen.isShiftKeyDown(), maxSize);
    }

    // 1.7.10 compatibility: Optional doesn't exist - use @Nullable Boolean instead
    @SideOnly(Side.CLIENT)
    @Nullable
    public static Boolean addPropertyTooltip(CrystalProperties prop, List<String> tooltip, boolean extended,
        int maxSize) {
        return addPropertyTooltip(prop, tooltip, extended, ResearchManager.clientProgress.getTierReached(), maxSize);
    }

    /**
     * Adds the property tooltip to the given item, depending on the properties.
     *
     * @return Nullable boolean.
     *
     *         Null = no significant information was added
     *         False = The player misses some knowledge.
     *         True = Everything has been displayed.
     */
    // 1.7.10 compatibility: Optional doesn't exist - use @Nullable Boolean instead
    @SideOnly(Side.CLIENT)
    @Nullable
    public static Boolean addPropertyTooltip(CrystalProperties prop, List<String> tooltip, boolean extended,
        ProgressionTier tier, int maxSize) {
        if (prop != null) {
            if (extended) {
                boolean missing = false;
                if (EnumGatedKnowledge.CRYSTAL_SIZE.canSee(tier)) {
                    EnumChatFormatting color = (prop.getSize() > maxSize ? EnumChatFormatting.AQUA
                        : prop.getSize() == maxSize ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE);
                    tooltip.add(EnumChatFormatting.GRAY + I18n.format("crystal.size") + ": " + color + prop.getSize());
                } else {
                    missing = true;
                }
                if (EnumGatedKnowledge.CRYSTAL_PURITY.canSee(tier)) {
                    EnumChatFormatting color = (prop.getPurity() > 100 ? EnumChatFormatting.AQUA
                        : prop.getPurity() == 100 ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE);
                    tooltip.add(
                        EnumChatFormatting.GRAY + I18n
                            .format("crystal.purity") + ": " + color + prop.getPurity() + "%");
                } else {
                    missing = true;
                }
                if (EnumGatedKnowledge.CRYSTAL_COLLECT.canSee(tier)) {
                    EnumChatFormatting color = (prop.getCollectiveCapability() > 100 ? EnumChatFormatting.AQUA
                        : prop.getCollectiveCapability() == 100 ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE);
                    tooltip.add(
                        EnumChatFormatting.GRAY + I18n
                            .format("crystal.collectivity") + ": " + color + prop.getCollectiveCapability() + "%");
                } else {
                    missing = true;
                }
                if (EnumGatedKnowledge.CRYSTAL_FRACTURE.canSee(tier) && prop.getFracturation() > 0) {
                    tooltip.add(
                        EnumChatFormatting.GRAY + I18n
                            .format("crystal.fracture") + ": " + EnumChatFormatting.RED + prop.getFracturation() + "%");
                }
                if (missing) {
                    tooltip.add(EnumChatFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                }
                // 1.7.10 compatibility: Optional.of() replaced with direct return
                return missing;
            } else {
                tooltip.add(
                    EnumChatFormatting.DARK_GRAY + EnumChatFormatting.ITALIC.toString()
                        + I18n.format("misc.moreInformation"));
                // 1.7.10 compatibility: Optional.empty() replaced with null return
                return null;
            }
        }
        // 1.7.10 compatibility: Optional.empty() replaced with null return
        return null;
    }

    @Nullable
    public CrystalProperties grindCopy(Random rand) {
        CrystalProperties copy = new CrystalProperties(size, purity, collectiveCapability, fractured, sizeOverride);
        int grind = 7 + rand.nextInt(5);
        double purity = ((double) this.purity) / 100D;
        if (purity <= 0.4) purity = 0.4;
        for (int j = 0; j < 3; j++) {
            if (purity <= rand.nextFloat()) {
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

    public static void applyCrystalProperties(ItemStack stack, CrystalProperties properties) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        NBTTagCompound crystalProp = new NBTTagCompound();
        crystalProp.setInteger("size", properties.getSize());
        crystalProp.setInteger("purity", properties.getPurity());
        crystalProp.setInteger("collectiveCapability", properties.getCollectiveCapability());
        crystalProp.setInteger("fract", properties.getFracturation());
        crystalProp.setInteger("sizeOverride", properties.getSizeOverride());
        cmp.setTag("crystalProperties", crystalProp);
    }

    @Nullable
    public static CrystalProperties getCrystalProperties(ItemStack stack) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("crystalProperties")) return null;
        NBTTagCompound prop = cmp.getCompoundTag("crystalProperties");
        Integer size = prop.getInteger("size");
        Integer purity = prop.getInteger("purity");
        Integer colCap = prop.getInteger("collectiveCapability");
        Integer fract = prop.getInteger("fract");
        Integer sizeOvr = NBTHelper.getInteger(prop, "sizeOverride", -1);
        return new CrystalProperties(size, purity, colCap, fract, sizeOvr);
    }

    @Override
    public String toString() {
        return "CrystalProperties={Size=" + size
            + ", Purity="
            + purity
            + ",Cutting="
            + collectiveCapability
            + ",Fractured="
            + fractured
            + ",SizeOverride="
            + sizeOverride
            + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrystalProperties that = (CrystalProperties) o;
        return size == that.size && purity == that.purity
            && collectiveCapability == that.collectiveCapability
            && fractured == that.fractured;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, purity, collectiveCapability, fractured);
    }
}
