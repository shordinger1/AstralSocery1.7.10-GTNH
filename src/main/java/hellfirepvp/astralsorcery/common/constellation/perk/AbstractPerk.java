/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Abstract perk - Base class for all constellation perks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import java.awt.Point;
import java.util.*;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Abstract perk - Base class for all constellation perks (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Position-based layout in perk tree</li>
 * <li>Category system (base, root, major, key, etc.)</li>
 * <li>Unlock requirements and progression</li>
 * <li>Attribute modifier application</li>
 * <li>Tooltip generation</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>ResourceLocation exists in 1.7.10 (same as 1.12.2)</li>
 * <li>Point exists in java.awt</li>
 * <li>No I18n format - use StatCollector.translateToLocal()</li>
 * <li>No TextFormatting - use EnumChatFormatting</li>
 * <li>No PlayerProgress - simplified to direct player data</li>
 * </ul>
 */
public abstract class AbstractPerk {

    protected static final Random rand = new Random();

    public static final PerkCategory CATEGORY_BASE = new PerkCategory("base", EnumChatFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_ROOT = new PerkCategory("root", EnumChatFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_MAJOR = new PerkCategory("major", EnumChatFormatting.WHITE.toString());
    public static final PerkCategory CATEGORY_KEY = new PerkCategory("key", EnumChatFormatting.GOLD.toString());
    public static final PerkCategory CATEGORY_EPIPHANY = new PerkCategory(
        "epiphany",
        EnumChatFormatting.GOLD.toString());
    public static final PerkCategory CATEGORY_FOCUS = new PerkCategory("focus", EnumChatFormatting.GOLD.toString());

    private final String registryName;
    protected final Point offset;
    private PerkCategory category = CATEGORY_BASE;
    private List<String> tooltipCache = null;
    private boolean cacheTooltip = true;
    protected String ovrUnlocalizedNamePrefix = null;
    private PerkTreePoint<? extends AbstractPerk> treePoint = null;

    /**
     * Create a new perk
     *
     * @param name The perk name (will be lowercased for registry name)
     * @param x    X position in perk tree
     * @param y    Y position in perk tree
     */
    public AbstractPerk(String name, int x, int y) {
        this.registryName = name.toLowerCase();
        this.offset = new Point(x, y);
    }

    /**
     * Initialize perk tree point
     * Override in subclasses to create custom point types
     */
    protected PerkTreePoint<? extends AbstractPerk> initPerkTreePoint() {
        return new PerkTreePoint<>(this, this.getOffset());
    }

    public String getRegistryName() {
        return registryName;
    }

    public Point getOffset() {
        return offset;
    }

    public final PerkTreePoint<? extends AbstractPerk> getPoint() {
        if (treePoint == null) {
            treePoint = initPerkTreePoint();
        }
        return treePoint;
    }

    /**
     * Set perk category
     */
    public <T> T setCategory(PerkCategory category) {
        this.category = category;
        return (T) this;
    }

    /**
     * Check if this perk's modifiers are disabled
     * Can be overridden in subclasses for custom logic
     */
    public boolean modifiersDisabled(EntityPlayer player, Side side) {
        return false; // Default: not disabled
    }

    /**
     * Apply perk to player
     */
    public final void applyPerk(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return;
        }

        this.applyPerkLogic(player, side);
        if (PerkAttributeHelper.getOrCreateMap(player, side)
            .markPerkApplied(this)) {
            LogHelper.debug("Perk " + getRegistryName() + " applied to " + player.getCommandSenderName());
        }
    }

    /**
     * Remove perk from player
     */
    public final void removePerk(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return;
        }

        this.removePerkLogic(player, side);
        if (PerkAttributeHelper.getOrCreateMap(player, side)
            .markPerkRemoved(this)) {
            LogHelper.debug("Perk " + getRegistryName() + " removed from " + player.getCommandSenderName());
        }
    }

    /**
     * Apply perk logic
     * Override in subclasses to apply actual effects
     */
    protected abstract void applyPerkLogic(EntityPlayer player, Side side);

    /**
     * Remove perk logic
     * Override in subclasses to remove actual effects
     */
    protected abstract void removePerkLogic(EntityPlayer player, Side side);

    /**
     * Get perk data for player
     */
    @Nullable
    public net.minecraft.nbt.NBTTagCompound getPerkData(EntityPlayer player, Side side) {
        hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress = hellfirepvp.astralsorcery.common.data.research.ResearchManager
            .getProgress(player, side);
        return progress.getPerkData(this);
    }

    /**
     * Called when the perk is modified
     * Called AFTER the perk has been re-applied with the new data.
     */
    public void modifyPerkServer(EntityPlayer player,
        hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress,
        net.minecraft.nbt.NBTTagCompound dataStorage) {}

    /**
     * Called ONCE when the perk is unlocked
     * You may use the NBTTagCompound to save data to remove it again later
     * The player might be null for root perks on occasion.
     */
    public void onUnlockPerkServer(@Nullable EntityPlayer player,
        hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress,
        net.minecraft.nbt.NBTTagCompound dataStorage) {}

    /**
     * Clean up and remove the perk from that single player.
     * Data in the dataStorage is filled with the data set in onUnlockPerkServer
     * Called after the perk is already removed from the player
     */
    public void onRemovePerkServer(EntityPlayer player,
        hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress,
        net.minecraft.nbt.NBTTagCompound dataStorage) {}

    /**
     * Set name override for this perk
     */
    public <T> T setNameOverride(AbstractPerk other) {
        return setNameOverride(other.getUnlocalizedName());
    }

    /**
     * Set name override for this perk
     */
    public <T> T setNameOverride(String namePrefix) {
        this.ovrUnlocalizedNamePrefix = namePrefix;
        return (T) this;
    }

    /**
     * Get perk category
     */
    public PerkCategory getCategory() {
        return category;
    }

    /**
     * Get perk status for a player
     */
    public PerkTreePoint.AllocationStatus getPerkStatus(EntityPlayer player, Side side) {
        if (player == null) {
            return PerkTreePoint.AllocationStatus.UNALLOCATED;
        }

        hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress = hellfirepvp.astralsorcery.common.data.research.ResearchManager
            .getProgress(player, side);

        if (progress.hasPerkUnlocked(this)) {
            return PerkTreePoint.AllocationStatus.ALLOCATED;
        }

        return mayUnlockPerk(player) ? PerkTreePoint.AllocationStatus.UNLOCKABLE
            : PerkTreePoint.AllocationStatus.UNALLOCATED;
    }

    /**
     * Check if player can unlock this perk
     */
    public boolean mayUnlockPerk(EntityPlayer player) {
        hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress = hellfirepvp.astralsorcery.common.data.research.ResearchManager
            .getProgress(player, Side.SERVER);

        if (!progress.hasFreeAllocationPoint(player)) return false;

        for (AbstractPerk otherPerks : PerkTree.PERK_TREE.getConnectedPerks(this)) {
            if (progress.hasPerkUnlocked(otherPerks)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get unlocalized name
     */
    public String getUnlocalizedName() {
        if (this.ovrUnlocalizedNamePrefix != null) {
            return this.ovrUnlocalizedNamePrefix;
        }
        return "perk." + getRegistryName();
    }

    /**
     * Disable tooltip caching
     */
    protected void disableTooltipCaching() {
        this.cacheTooltip = false;
        this.tooltipCache = null;
    }

    /**
     * Get localized tooltip
     */
    @SideOnly(Side.CLIENT)
    public final Collection<String> getLocalizedTooltip() {
        if (cacheTooltip && tooltipCache != null) {
            return tooltipCache;
        }

        tooltipCache = new ArrayList();
        String key = this.ovrUnlocalizedNamePrefix;
        if (modifiersDisabled(net.minecraft.client.Minecraft.getMinecraft().thePlayer, Side.CLIENT)) {
            tooltipCache.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("perk.info.disabled"));
        } else if (!(this instanceof ProgressGatedPerk) || ((ProgressGatedPerk) this).canSeeClient()) {
            tooltipCache.add(
                this.getCategory()
                    .getTextFormatting() + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name"));

            if (key == null) {
                key = "perk." + getRegistryName();
            }
            int prevLength = tooltipCache.size();
            boolean shouldAdd = addLocalizedTooltip(tooltipCache);
            if (shouldAdd && prevLength != tooltipCache.size()) {
                tooltipCache.add(""); // Add empty line
            }

            // Check for indexed description
            String descKey = key + ".desc.1";
            String desc = StatCollector.translateToLocal(descKey);
            if (!desc.equals(descKey)) { // Has translation
                int count = 1;
                while (true) {
                    String dKey = key + ".desc." + count;
                    String dText = StatCollector.translateToLocal(dKey);
                    if (dText.equals(dKey)) break; // No translation
                    tooltipCache.add(dText);
                    count++;
                }
                tooltipCache.add("");
            } else {
                // Check for single description
                descKey = key + ".desc";
                desc = StatCollector.translateToLocal(descKey);
                if (!desc.equals(descKey)) {
                    tooltipCache.add(desc);
                    tooltipCache.add("");
                }
            }
        } else {
            tooltipCache.add(EnumChatFormatting.RED + StatCollector.translateToLocal("perk.info.missing_progress"));
        }
        return tooltipCache;
    }

    /**
     * Add custom localized tooltip
     * Override in subclasses
     */
    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        return false;
    }

    /**
     * Clear caches
     */
    public void clearCaches(Side side) {}

    /**
     * Clear client caches
     */
    @SideOnly(Side.CLIENT)
    public void clearClientCaches() {
        this.tooltipCache = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof AbstractPerk)) return false;
        AbstractPerk that = (AbstractPerk) o;
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }

    /**
     * Perk category
     */
    public static class PerkCategory {

        private final String unlocName;
        private String textFormatting;

        public PerkCategory(String unlocName, String formattingPrefix) {
            this.unlocName = unlocName;
            this.textFormatting = formattingPrefix;
        }

        public String getUnlocalizedName() {
            return unlocName;
        }

        public String getTextFormatting() {
            return textFormatting;
        }

        @SideOnly(Side.CLIENT)
        public String getLocalizedName() {
            String str = "perk.category." + unlocName + ".name";
            String translated = StatCollector.translateToLocal(str);
            if (!translated.equals(str)) {
                return translated;
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PerkCategory that = (PerkCategory) o;
            return Objects.equals(unlocName, that.unlocName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(unlocName);
        }

    }
}
