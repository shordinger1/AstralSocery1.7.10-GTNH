/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Sky Resonator - Tool for measuring and manipulating starlight
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Sky Resonator
 * <p>
 * A tool for measuring and manipulating starlight distribution.
 * <p>
 * Features:
 * - Measures starlight intensity at position
 * - Multiple upgrade modes
 * - Enhanced version with additional features
 * - Shift + right-click to cycle modes
 * <p>
 * Upgrades:
 * - STARLIGHT: Basic starlight measurement
 * - FLUID_FIELDS: Detect fluid deposits (TODO)
 * - AREA_SIZE: Display structure influence (TODO)
 * <p>
 * TODO:
 * - Implement starlight measurement system
 * - Implement ConstellationSkyHandler
 * - Implement upgrade system logic
 * - Implement fluid field detection
 * - Implement area size visualization
 * - Implement particle effects
 */
public class ItemSkyResonator extends AstralBaseItem {

    private static final String TAG_ENHANCED = "enhanced";
    private static final String TAG_UPGRADES = "upgrades";
    private static final String TAG_SELECTED = "selected_upgrade";

    public ItemSkyResonator() {
        super();
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public String getUnlocalizedName(ItemStack stack) {
        if (isEnhanced(stack)) {
            ResonatorUpgrade upgrade = getCurrentUpgrade(stack);
            return super.getUnlocalizedName() + "." + upgrade.getName();
        }
        return super.getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (tab == this.getCreativeTab()) {
            // Normal version
            list.add(new ItemStack(item));

            // Enhanced version with all upgrades
            ItemStack enhanced = new ItemStack(item);
            setEnhanced(enhanced);
            for (ResonatorUpgrade upgrade : ResonatorUpgrade.values()) {
                setUpgradeUnlocked(enhanced, upgrade);
            }
            list.add(enhanced);
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player.isSneaking()) {
            if (isEnhanced(stack)) {
                cycleUpgrade(stack);
                ResonatorUpgrade upgrade = getCurrentUpgrade(stack);
                player.addChatMessage(new ChatComponentText("§6[Sky Resonator] §rMode: §e" + upgrade.getDisplayName()));
                world.playSoundAtEntity(player, "random.click", 0.3F, 1.0F);
            }
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (!isEnhanced(stack)) {
            tooltip.add("§7Shift + Right-click: §cUpgrade required");
            return;
        }

        ResonatorUpgrade current = getCurrentUpgrade(stack);
        tooltip.add("§7Current Mode: §e" + current.getDisplayName());

        NBTTagList upgrades = getUpgrades(stack);
        if (upgrades.tagCount() > 0) {
            tooltip.add("§7Available Upgrades:");
            for (int i = 0; i < upgrades.tagCount(); i++) {
                int upgradeId = upgrades.getCompoundTagAt(i)
                    .getInteger("id");
                ResonatorUpgrade upgrade = ResonatorUpgrade.byId(upgradeId);
                if (upgrade != null) {
                    String prefix = (upgrade == current) ? "§a→ " : "§7  ";
                    tooltip.add(prefix + upgrade.getDisplayName());
                }
            }
        }
    }

    /**
     * Check if resonator is enhanced
     */
    public static boolean isEnhanced(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return false;
        }
        return stack.getTagCompound()
            .getBoolean(TAG_ENHANCED);
    }

    /**
     * Set resonator as enhanced
     */
    public static void setEnhanced(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setBoolean(TAG_ENHANCED, true);
    }

    /**
     * Get current upgrade
     */
    public static ResonatorUpgrade getCurrentUpgrade(ItemStack stack) {
        if (!isEnhanced(stack)) {
            return ResonatorUpgrade.STARLIGHT;
        }

        int selected = stack.getTagCompound()
            .getInteger(TAG_SELECTED);
        return ResonatorUpgrade.byId(selected);
    }

    /**
     * Set current upgrade
     */
    public static boolean setCurrentUpgrade(ItemStack stack, ResonatorUpgrade upgrade) {
        if (!isEnhanced(stack) || !hasUpgrade(stack, upgrade)) {
            return false;
        }

        stack.getTagCompound()
            .setInteger(TAG_SELECTED, upgrade.ordinal());
        return true;
    }

    /**
     * Cycle to next upgrade
     */
    private void cycleUpgrade(ItemStack stack) {
        NBTTagList upgrades = getUpgrades(stack);
        if (upgrades.tagCount() == 0) {
            return;
        }

        ResonatorUpgrade current = getCurrentUpgrade(stack);

        // Find next upgrade
        for (int i = 0; i < upgrades.tagCount(); i++) {
            int upgradeId = upgrades.getCompoundTagAt(i)
                .getInteger("id");
            ResonatorUpgrade upgrade = ResonatorUpgrade.byId(upgradeId);
            if (upgrade != null && upgrade.ordinal() > current.ordinal()) {
                setCurrentUpgrade(stack, upgrade);
                return;
            }
        }

        // Wrap to first upgrade
        int firstId = upgrades.getCompoundTagAt(0)
            .getInteger("id");
        setCurrentUpgrade(stack, ResonatorUpgrade.byId(firstId));
    }

    /**
     * Check if has upgrade
     */
    public static boolean hasUpgrade(ItemStack stack, ResonatorUpgrade upgrade) {
        if (!isEnhanced(stack)) {
            return false;
        }

        NBTTagList upgrades = getUpgrades(stack);
        for (int i = 0; i < upgrades.tagCount(); i++) {
            int upgradeId = upgrades.getCompoundTagAt(i)
                .getInteger("id");
            if (upgradeId == upgrade.ordinal()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get upgrades list
     */
    private static NBTTagList getUpgrades(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return new NBTTagList();
        }
        return stack.getTagCompound()
            .getTagList(TAG_UPGRADES, 10);
    }

    /**
     * Set upgrade as unlocked
     */
    public static void setUpgradeUnlocked(ItemStack stack, ResonatorUpgrade upgrade) {
        if (!isEnhanced(stack)) {
            return;
        }

        if (hasUpgrade(stack, upgrade)) {
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", upgrade.ordinal());

        NBTTagList upgrades = getUpgrades(stack);
        upgrades.appendTag(tag);

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setTag(TAG_UPGRADES, upgrades);
    }

    /**
     * Resonator upgrade enum
     */
    public enum ResonatorUpgrade {

        STARLIGHT(0, "starlight", "Starlight Measurement"),
        FLUID_FIELDS(1, "liquid", "Fluid Field Detection"),
        AREA_SIZE(2, "structure", "Area Size Display");

        private final int id;
        private final String name;
        private final String displayName;

        ResonatorUpgrade(int id, String name, String displayName) {
            this.id = id;
            this.name = name;
            this.displayName = displayName;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static ResonatorUpgrade byId(int id) {
            for (ResonatorUpgrade upgrade : values()) {
                if (upgrade.id == id) {
                    return upgrade;
                }
            }
            return STARLIGHT;
        }
    }

    /**
     * NOTE: Measurement Systems
     * <p>
     * Original version:
     * - ConstellationSkyHandler manages starlight distribution
     * - FluidRarityRegistry tracks fluid deposits
     * - IStructureAreaOfInfluence for structure ranges
     * <p>
     * In 1.7.10:
     * - TODO: Implement ConstellationSkyHandler
     * - TODO: Implement fluid detection system
     * - TODO: Implement structure area visualization
     * - TODO: Implement particle effects for feedback
     */
}
