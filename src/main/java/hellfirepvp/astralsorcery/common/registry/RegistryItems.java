/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Item registration handler
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.ItemTextureMap;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.tool.charged.ItemChargedCrystalAxe;
import hellfirepvp.astralsorcery.common.item.tool.charged.ItemChargedCrystalPickaxe;
import hellfirepvp.astralsorcery.common.item.tool.charged.ItemChargedCrystalShovel;
import hellfirepvp.astralsorcery.common.item.tool.charged.ItemChargedCrystalSword;
import hellfirepvp.astralsorcery.common.item.wand.ItemArchitectWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemExchangeWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemGrappleWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.wearable.*;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.registry.reference.ItemsAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.util.ResourceChecker;

/**
 * Item registry for Astral Sorcery
 *
 * Handles registration of all items in the mod.
 * Based on TST ItemRegister and BartWorks ItemRegistry
 *
 * IMPORTANT: All new items should extend {@link hellfirepvp.astralsorcery.common.base.AstralBaseItem}
 * rather than extending {@link net.minecraft.item.Item} directly.
 */
public class RegistryItems {

    // Item instances will be declared here
    // public static Item itemExample;

    private static final List<Item> ITEMS_TO_REGISTER = Lists.newArrayList();

    /**
     * Pre-initialization: register all items
     */
    public static void preInit() {
        LogHelper.entry("RegistryItems.preInit");

        // === Crafting Components ===
        ItemsAS.craftingComponent = (ItemCraftingComponent) registerItemWithTab(
            new ItemCraftingComponent(),
            "itemcraftingcomponent");

        // === Crystal Tools ===
        ItemsAS.crystalPickaxe = (ItemCrystalPickaxe) registerItemWithTab(
            new ItemCrystalPickaxe(),
            "itemcrystalpickaxe");
        ItemsAS.crystalSword = (ItemCrystalSword) registerItemWithTab(new ItemCrystalSword(), "itemcrystalsword");
        ItemsAS.crystalAxe = (ItemCrystalAxe) registerItemWithTab(new ItemCrystalAxe(), "itemcrystalaxe");
        ItemsAS.crystalShovel = (ItemCrystalShovel) registerItemWithTab(new ItemCrystalShovel(), "itemcrystalshovel");

        // === Charged Crystal Tools ===
        ItemsAS.chargedCrystalPickaxe = (ItemChargedCrystalPickaxe) registerItemWithTab(
            new ItemChargedCrystalPickaxe(),
            "itemchargedcrystalpickaxe");
        ItemsAS.chargedCrystalAxe = (ItemChargedCrystalAxe) registerItemWithTab(
            new ItemChargedCrystalAxe(),
            "itemchargedcrystalaxe");
        ItemsAS.chargedCrystalShovel = (ItemChargedCrystalShovel) registerItemWithTab(
            new ItemChargedCrystalShovel(),
            "itemchargedcrystalshovel");
        ItemsAS.chargedCrystalSword = (ItemChargedCrystalSword) registerItemWithTab(
            new ItemChargedCrystalSword(),
            "itemchargedcrystalsword");

        // === Simple Items ===
        ItemsAS.rockCrystalSimple = (ItemRockCrystalSimple) registerItemWithTab(
            new ItemRockCrystalSimple(),
            "itemrockcrystalsimple");

        // === Crystal Items ===
        ItemsAS.tunedRockCrystal = (ItemTunedRockCrystal) registerItemWithTab(
            new ItemTunedRockCrystal(),
            "itemtunedrockcrystal");
        ItemsAS.celestialCrystal = (ItemCelestialCrystal) registerItemWithTab(
            new ItemCelestialCrystal(),
            "itemcelestialcrystal");
        ItemsAS.tunedCelestialCrystal = (ItemTunedCelestialCrystal) registerItemWithTab(
            new ItemTunedCelestialCrystal(),
            "itemtunedcelestialcrystal");

        // === Research Items ===
        ItemsAS.journal = (ItemJournal) registerItemWithTab(new ItemJournal(), "itemjournal");
        ItemsAS.constellationPaper = (ItemConstellationPaper) registerItemWithTab(
            new ItemConstellationPaper(),
            "itemconstellationpaper");
        ItemsAS.knowledgeFragment = (ItemKnowledgeFragment) registerItemWithTab(
            new ItemKnowledgeFragment(),
            "itemknowledgefragment");
        ItemsAS.fragmentCapsule = (ItemFragmentCapsule) registerItemWithTab(
            new ItemFragmentCapsule(),
            "itemfragmentcapsule");
        ItemsAS.knowledgeShare = (ItemKnowledgeShare) registerItemWithTab(
            new ItemKnowledgeShare(),
            "itemknowledgeshare");

        // === Functional Items ===
        ItemsAS.handTelescope = (ItemHandTelescope) registerItemWithTab(new ItemHandTelescope(), "itemhandtelescope");
        ItemsAS.infusedGlass = (ItemInfusedGlass) registerItemWithTab(new ItemInfusedGlass(), "iteminfusedglass");
        ItemsAS.coloredLens = (ItemColoredLens) registerItemWithTab(new ItemColoredLens(), "itemcoloredlens");
        ItemsAS.usableDust = (ItemUsableDust) registerItemWithTab(new ItemUsableDust(), "itemusabledust");

        // === Perk Items ===
        ItemsAS.perkGem = (ItemPerkGem) registerItemWithTab(new ItemPerkGem(), "itemperkgem");
        ItemsAS.perkSeal = (ItemPerkSeal) registerItemWithTab(new ItemPerkSeal(), "itemperkseal");
        ItemsAS.shiftingStar = (ItemShiftingStar) registerItemWithTab(new ItemShiftingStar(), "itemshiftingstar");

        // === Wands ===
        ItemsAS.wand = (ItemWand) registerItemWithTab(new ItemWand(), "itemwand");
        ItemsAS.architectWand = (ItemArchitectWand) registerItemWithTab(new ItemArchitectWand(), "itemarchitectwand");
        ItemsAS.exchangeWand = (ItemExchangeWand) registerItemWithTab(new ItemExchangeWand(), "itemexchangewand");
        ItemsAS.grappleWand = (ItemGrappleWand) registerItemWithTab(new ItemGrappleWand(), "itemgrapplewand");
        ItemsAS.illuminationWand = (ItemIlluminationWand) registerItemWithTab(
            new ItemIlluminationWand(),
            "itemilluminationwand");

        // === Special Tools (Phase 5) ===
        ItemsAS.roseBranchBow = (ItemRoseBranchBow) registerItemWithTab(new ItemRoseBranchBow(), "itemrosebranchbow");
        ItemsAS.linkingTool = (ItemLinkingTool) registerItemWithTab(new ItemLinkingTool(), "itemlinkingtool");
        ItemsAS.skyResonator = (ItemSkyResonator) registerItemWithTab(new ItemSkyResonator(), "itemskyresonator");
        ItemsAS.sextant = (ItemSextant) registerItemWithTab(new ItemSextant(), "itemsextant");

        // === Wearable Items (Phase 5) ===
        ItemsAS.cape = (ItemCape) registerItemWithTab(new ItemCape(), "itemcape");
        ItemsAS.enchantmentAmulet = (ItemEnchantmentAmulet) registerItemWithTab(
            new ItemEnchantmentAmulet(),
            "itemenchantmentamulet");

        // Log registered items
        LogHelper.info("Registered " + ITEMS_TO_REGISTER.size() + " items");

        LogHelper.exit("RegistryItems.preInit");
    }

    /**
     * Register an item with the specified name
     *
     * @param item The item to register
     * @param name The registry name (without mod ID prefix)
     * @return The registered item
     */
    public static Item registerItem(Item item, String name) {
        return registerItem(item, name, null);
    }

    /**
     * Register an item with the specified name and creative tab
     *
     * @param item        The item to register
     * @param name        The registry name (without mod ID prefix)
     * @param creativeTab The creative tab, or null for none
     * @return The registered item
     */
    public static Item registerItem(Item item, String name, CreativeTabs creativeTab) {
        if (item == null) {
            throw new IllegalArgumentException("Attempted to register null item!");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty!");
        }

        // Set unlocalized name - match original 1.12.2 format
        // Use simple name without prefixes - Minecraft adds "item." automatically
        // unlocalizedName: "itemcraftingcomponent" -> lang key: "item.itemcraftingcomponent.name"
        item.setUnlocalizedName(name);

        // Set texture name - use ItemTextureMap to get actual texture file name
        String textureName = ItemTextureMap.getTextureName(name);
        item.setTextureName(Constants.RESOURCE_ROOT + textureName);

        // Set creative tab if specified
        if (creativeTab != null) {
            item.setCreativeTab(creativeTab);
        }

        // Register the item - use simple name without prefix
        GameRegistry.registerItem(item, name);

        // Track for later
        ITEMS_TO_REGISTER.add(item);

        LogHelper.debug("Registered item: " + name + " (unlocalizedName: " + name + ")");

        return item;
    }

    /**
     * Register an item with the default creative tab
     *
     * @param item The item to register
     * @param name The registry name (without mod ID prefix)
     * @return The registered item
     */
    public static Item registerItemWithTab(Item item, String name) {
        return registerItem(item, name, CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    /**
     * Get all registered items
     *
     * @return List of all registered items
     */
    public static List<Item> getRegisteredItems() {
        return Lists.newArrayList(ITEMS_TO_REGISTER);
    }

    /**
     * Register item subtypes (for items with metadata)
     *
     * @param item        The item with subtypes
     * @param subItems    The list to populate
     * @param tab         The creative tab
     * @param maxMetadata Maximum metadata value
     */
    public static void registerItemSubTypes(Item item, List<ItemStack> subItems, CreativeTabs tab, int maxMetadata) {
        for (int meta = 0; meta <= maxMetadata; meta++) {
            ItemStack stack = new ItemStack(item, 1, meta);
            if (stack.getDisplayName() != null) {
                subItems.add(stack);
            }
        }
    }

    /**
     * Initialize items after registration
     * Called during postInit
     */
    public static void init() {
        LogHelper.entry("RegistryItems.init");

        // Check item resources (client side only)
        checkItemResources();

        LogHelper.exit("RegistryItems.init");
    }

    /**
     * Check all registered items for icons and localization
     * This is a debug utility to verify texture registration and translations
     */
    @SideOnly(Side.CLIENT)
    private static void checkItemResources() {
        LogHelper.info("=== Checking Item Resources ===");
        LogHelper.info("Total items registered: " + ITEMS_TO_REGISTER.size());

        int issuesCount = 0;
        int okCount = 0;

        for (Item item : ITEMS_TO_REGISTER) {
            String itemName = item.getUnlocalizedName();
            ResourceChecker.CheckResult result = ResourceChecker.checkItem(item, itemName);

            if (result.hasIssues()) {
                LogHelper.warn("[ITEM ISSUE] " + result.format());
                issuesCount++;
            } else {
                LogHelper.info("[ITEM OK] " + result.format());
                okCount++;
            }
        }

        LogHelper.info("=== Item Resource Check Complete ===");
        LogHelper.info("OK: " + okCount);
        LogHelper.info("Issues: " + issuesCount);

        if (issuesCount > 0) {
            LogHelper.warn("WARNING: " + issuesCount + " items have resource issues!");
        } else {
            LogHelper.info("All items are OK!");
        }
    }
}
