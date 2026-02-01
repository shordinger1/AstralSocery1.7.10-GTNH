/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Item instances - All item references
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.reference;

import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.crystal.*;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.tool.charged.*;
import hellfirepvp.astralsorcery.common.item.wand.*;
import hellfirepvp.astralsorcery.common.item.wearable.*;

/**
 * Item instances for Astral Sorcery (1.7.10)
 * <p>
 * This class holds static references to all items in the mod.
 * Items are registered in RegistryItems.
 */
public class ItemsAS {

    // Crafting components
    public static ItemCraftingComponent craftingComponent;

    // Crystal tools
    public static ItemCrystalToolBase crystalToolBase;
    public static ItemCrystalPickaxe crystalPickaxe;
    public static ItemCrystalSword crystalSword;
    public static ItemCrystalAxe crystalAxe;
    public static ItemCrystalShovel crystalShovel;

    // Charged crystal tools
    public static ItemChargedCrystalPickaxe chargedCrystalPickaxe;
    public static ItemChargedCrystalAxe chargedCrystalAxe;
    public static ItemChargedCrystalShovel chargedCrystalShovel;
    public static ItemChargedCrystalSword chargedCrystalSword;

    // Simple items
    public static ItemRockCrystalSimple rockCrystalSimple;

    // Crystal items
    public static ItemTunedRockCrystal tunedRockCrystal;
    public static ItemCelestialCrystal celestialCrystal;
    public static ItemTunedCelestialCrystal tunedCelestialCrystal;

    // Research items
    public static ItemJournal journal;
    public static ItemConstellationPaper constellationPaper;
    public static ItemKnowledgeFragment knowledgeFragment;
    public static ItemFragmentCapsule fragmentCapsule;
    public static ItemKnowledgeShare knowledgeShare;

    // Functional items
    public static ItemHandTelescope handTelescope;
    public static ItemInfusedGlass infusedGlass;
    public static ItemColoredLens coloredLens;
    public static ItemUsableDust usableDust;

    // Perk items
    public static ItemPerkGem perkGem;
    public static ItemPerkSeal perkSeal;
    public static ItemShiftingStar shiftingStar;

    // Wands
    public static ItemWand wand;
    public static ItemArchitectWand architectWand;
    public static ItemExchangeWand exchangeWand;
    public static ItemGrappleWand grappleWand;
    public static ItemIlluminationWand illuminationWand;

    // Special tools (Phase 5)
    public static ItemRoseBranchBow roseBranchBow;
    public static ItemLinkingTool linkingTool;
    public static ItemSkyResonator skyResonator;
    public static ItemSextant sextant;

    // Wearable items (Phase 5)
    public static ItemCape cape;
    public static ItemEnchantmentAmulet enchantmentAmulet;
}
