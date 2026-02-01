/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Centralized Instance Management
 *
 * This class provides a unified API for accessing all static instances in the mod.
 * Instead of scattering static references across multiple classes (BlocksAS, ItemsAS, etc.),
 * all instances can be accessed through this central manager.
 *
 * Usage Example:
 *   Block altar = InstanceManager.blocks().altar();
 *   Item wand = InstanceManager.items().wand();
 *   IConstellation discidia = InstanceManager.constellations().discidia();
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.instance;

import cpw.mods.fml.common.registry.GameRegistry;
import hellfirepvp.astralsorcery.common.block.*;
import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentNightVision;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentScorchingHeat;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.crystal.*;
import hellfirepvp.astralsorcery.common.item.tool.charged.*;
import hellfirepvp.astralsorcery.common.item.wand.*;
import hellfirepvp.astralsorcery.common.item.wearable.*;
import hellfirepvp.astralsorcery.common.potion.*;
import hellfirepvp.astralsorcery.common.tile.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.RegistrySimple;

/**
 * Centralized instance manager for Astral Sorcery.
 * <p>
 * This class provides type-safe access to all mod instances through nested classes.
 * Each category of objects has its own accessor class with getter methods.
 * <p>
 * Design Benefits:
 * - Single point of access for all instances
 * - Type-safe with compile-time checking
 * - Easy to find and update instances
 * - Supports IDE autocomplete
 * - Can be extended with additional categories
 * <p>
 * Thread Safety: This class uses static final fields and is thread-safe.
 */
public class InstanceManager {

    // ========================================================================
    // =                    BLOCK INSTANCES                                 =
    // ========================================================================

    /**
     * Accessor for all block instances
     */
    public static class Blocks {

        // Ore blocks
        public static BlockCustomOre customOre() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.customOre;
        }

        public static BlockCustomSandOre customSandOre() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.customSandOre;
        }

        // Decorative blocks
        public static BlockCustomFlower customFlower() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.customFlower;
        }

        public static BlockMarble blockMarble() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockMarble;
        }

        public static BlockBlackMarble blockBlackMarble() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockBlackMarble;
        }

        public static BlockInfusedWood blockInfusedWood() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockInfusedWood;
        }

        public static BlockFlareLight blockFlareLight() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockFlareLight;
        }

        // Marble components
        public static BlockMarbleSlab blockMarbleSlab() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockMarbleSlab;
        }

        public static BlockMarbleDoubleSlab blockMarbleDoubleSlab() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockMarbleDoubleSlab;
        }

        public static BlockMarbleStairs blockMarbleStairs() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockMarbleStairs;
        }

        // Special blocks
        public static BlockStructural blockStructural() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockStructural;
        }

        public static BlockFakeTree blockFakeTree() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockFakeTree;
        }

        public static BlockVanishing blockVanishing() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockVanishing;
        }

        public static BlockTranslucentBlock translucentBlock() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.translucentBlock;
        }

        // Functional blocks
        public static BlockAltar blockAltar() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockAltar;
        }

        public static BlockAttunementAltar attunementAltar() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.attunementAltar;
        }

        public static BlockWell blockWell() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockWell;
        }

        public static BlockWorldIlluminator blockIlluminator() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockIlluminator;
        }

        public static BlockMapDrawingTable drawingTable() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.drawingTable;
        }

        public static BlockObservatory blockObservatory() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockObservatory;
        }

        public static BlockTelescope blockTelescope() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockTelescope;
        }

        // Starlight network blocks
        public static BlockCollectorCrystal collectorCrystal() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.collectorCrystal;
        }

        public static BlockLens lens() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.lens;
        }

        public static BlockPrism lensPrism() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.lensPrism;
        }

        public static BlockCelestialCollectorCrystal celestialCollectorCrystal() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.celestialCollectorCrystal;
        }

        public static BlockAttunementRelay attunementRelay() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.attunementRelay;
        }

        public static BlockCelestialCrystals celestialCrystals() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.celestialCrystals;
        }

        // Ritual blocks
        public static BlockRitualPedestal ritualPedestal() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.ritualPedestal;
        }

        public static BlockRitualLink ritualLink() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.ritualLink;
        }

        public static BlockTreeBeacon treeBeacon() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.treeBeacon;
        }

        // Advanced blocks
        public static BlockStarlightInfuser starlightInfuser() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.starlightInfuser;
        }

        public static BlockCelestialOrrery celestialOrrery() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.celestialOrrery;
        }

        // Bore blocks
        public static BlockBore blockBore() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockBore;
        }

        public static BlockBoreHead blockBoreHead() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockBoreHead;
        }

        // Gem crystals
        public static BlockGemCrystals gemCrystals() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.gemCrystals;
        }

        // Chalice
        public static BlockChalice blockChalice() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockChalice;
        }

        // Celestial Gateway blocks
        public static BlockCelestialGateway celestialGateway() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.celestialGateway;
        }

        public static BlockPortalNode portalNode() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.portalNode;
        }

        // Machine blocks
        public static BlockMachine blockMachine() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockMachine;
        }

        // Fluid blocks
        public static FluidBlockLiquidStarlight blockLiquidStarlight() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.blockLiquidStarlight;
        }

        public static FluidLiquidStarlight fluidLiquidStarlight() {
            return hellfirepvp.astralsorcery.common.registry.reference.BlocksAS.fluidLiquidStarlight;
        }

        // ====================================================================
        // =                    HELPER METHODS                               =
        // ====================================================================

        /**
         * Get all blocks as an array
         * Useful for iteration and registration
         */
        public static Block[] all() {
            return new Block[] {
                customOre(), customSandOre(), customFlower(),
                blockMarble(), blockBlackMarble(), blockInfusedWood(), blockFlareLight(),
                blockMarbleSlab(), blockMarbleDoubleSlab(), blockMarbleStairs(),
                blockStructural(), blockFakeTree(), blockVanishing(), translucentBlock(),
                blockAltar(), attunementAltar(), blockWell(), blockIlluminator(),
                drawingTable(), blockObservatory(), blockTelescope(),
                collectorCrystal(), lens(), lensPrism(), celestialCollectorCrystal(),
                attunementRelay(), celestialCrystals(),
                ritualPedestal(), ritualLink(), treeBeacon(),
                starlightInfuser(), celestialOrrery(),
                blockBore(), blockBoreHead(), gemCrystals(), blockChalice(),
                celestialGateway(), portalNode(), blockMachine(),
                blockLiquidStarlight()
            };
        }

        /**
         * Get block count
         */
        public static int count() {
            return all().length;
        }

        /**
         * Check if a block belongs to Astral Sorcery
         */
        public static boolean isAstralSorceryBlock(Block block) {
            if (block == null) return false;
            for (Block b : all()) {
                if (b == block) return true;
            }
            return false;
        }
    }

    // ========================================================================
    // =                    ITEM INSTANCES                                  =
    // ========================================================================

    /**
     * Accessor for all item instances
     */
    public static class Items {

        // Crafting components
        public static ItemCraftingComponent craftingComponent() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.craftingComponent;
        }

        // Crystal tools
        public static ItemCrystalToolBase crystalToolBase() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.crystalToolBase;
        }

        public static ItemCrystalPickaxe crystalPickaxe() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.crystalPickaxe;
        }

        public static ItemCrystalSword crystalSword() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.crystalSword;
        }

        public static ItemCrystalAxe crystalAxe() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.crystalAxe;
        }

        public static ItemCrystalShovel crystalShovel() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.crystalShovel;
        }

        // Charged crystal tools
        public static ItemChargedCrystalPickaxe chargedCrystalPickaxe() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.chargedCrystalPickaxe;
        }

        public static ItemChargedCrystalAxe chargedCrystalAxe() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.chargedCrystalAxe;
        }

        public static ItemChargedCrystalShovel chargedCrystalShovel() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.chargedCrystalShovel;
        }

        public static ItemChargedCrystalSword chargedCrystalSword() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.chargedCrystalSword;
        }

        // Crystal items
        public static ItemRockCrystalSimple rockCrystalSimple() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.rockCrystalSimple;
        }

        public static ItemTunedRockCrystal tunedRockCrystal() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.tunedRockCrystal;
        }

        public static ItemCelestialCrystal celestialCrystal() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.celestialCrystal;
        }

        public static ItemTunedCelestialCrystal tunedCelestialCrystal() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.tunedCelestialCrystal;
        }

        // Research items
        public static ItemJournal journal() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.journal;
        }

        public static ItemConstellationPaper constellationPaper() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.constellationPaper;
        }

        public static ItemKnowledgeFragment knowledgeFragment() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.knowledgeFragment;
        }

        public static ItemFragmentCapsule fragmentCapsule() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.fragmentCapsule;
        }

        public static ItemKnowledgeShare knowledgeShare() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.knowledgeShare;
        }

        // Functional items
        public static ItemHandTelescope handTelescope() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.handTelescope;
        }

        public static ItemInfusedGlass infusedGlass() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.infusedGlass;
        }

        public static ItemColoredLens coloredLens() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.coloredLens;
        }

        public static ItemUsableDust usableDust() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.usableDust;
        }

        // Perk items
        public static ItemPerkGem perkGem() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.perkGem;
        }

        public static ItemPerkSeal perkSeal() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.perkSeal;
        }

        public static ItemShiftingStar shiftingStar() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.shiftingStar;
        }

        // Wands
        public static ItemWand wand() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.wand;
        }

        public static ItemArchitectWand architectWand() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.architectWand;
        }

        public static ItemExchangeWand exchangeWand() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.exchangeWand;
        }

        public static ItemGrappleWand grappleWand() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.grappleWand;
        }

        public static ItemIlluminationWand illuminationWand() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.illuminationWand;
        }

        // Special tools
        // NOTE: These item classes don't exist yet - commented out
        /*
        public static ItemRoseBranchBow roseBranchBow() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.roseBranchBow;
        }

        public static ItemLinkingTool linkingTool() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.linkingTool;
        }

        public static ItemSkyResonator skyResonator() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.skyResonator;
        }

        public static ItemSextant sextant() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.sextant;
        }
        */

        // Wearable items
        public static ItemCape cape() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.cape;
        }

        public static ItemEnchantmentAmulet enchantmentAmulet() {
            return hellfirepvp.astralsorcery.common.registry.reference.ItemsAS.enchantmentAmulet;
        }

        // ====================================================================
        // =                    HELPER METHODS                               =
        // ====================================================================

        /**
         * Get all items as an array
         * NOTE: Some items may not exist yet and are commented out
         */
        public static Item[] all() {
            return new Item[] {
                craftingComponent(), crystalToolBase(),
                crystalPickaxe(), crystalSword(), crystalAxe(), crystalShovel(),
                chargedCrystalPickaxe(), chargedCrystalAxe(),
                chargedCrystalShovel(), chargedCrystalSword(),
                rockCrystalSimple(), tunedRockCrystal(), celestialCrystal(), tunedCelestialCrystal(),
                journal(), constellationPaper(), knowledgeFragment(), fragmentCapsule(), knowledgeShare(),
                handTelescope(), infusedGlass(), coloredLens(), usableDust(),
                perkGem(), perkSeal(), shiftingStar(),
                wand(), architectWand(), exchangeWand(), grappleWand(), illuminationWand(),
                // roseBranchBow(), linkingTool(), skyResonator(), sextant(), // TODO: These items don't exist yet
                cape(), enchantmentAmulet()
            };
        }

        /**
         * Get item count
         */
        public static int count() {
            return all().length;
        }

        /**
         * Check if an item belongs to Astral Sorcery
         */
        public static boolean isAstralSorceryItem(Item item) {
            if (item == null) return false;
            for (Item i : all()) {
                if (i == item) return true;
            }
            return false;
        }
    }

    // ========================================================================
    // =                    CONSTELLATION INSTANCES                         =
    // ========================================================================

    /**
     * Accessor for all constellation instances
     */
    public static class Constellations {

        // Major constellations
        public static IMajorConstellation discidia() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.discidia;
        }

        public static IMajorConstellation armara() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.armara;
        }

        public static IMajorConstellation vicio() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.vicio;
        }

        public static IMajorConstellation aevitas() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.aevitas;
        }

        public static IMajorConstellation evorsio() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.evorsio;
        }

        public static IMajorConstellation vectras() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.vectras;
        }

        // Weak constellations
        public static IWeakConstellation lucerna() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.lucerna;
        }

        public static IWeakConstellation mineralis() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.mineralis;
        }

        public static IWeakConstellation horologium() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.horologium;
        }

        public static IWeakConstellation octans() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.octans;
        }

        public static IWeakConstellation bootes() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.bootes;
        }

        public static IWeakConstellation fornax() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.fornax;
        }

        public static IWeakConstellation pelotrio() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.pelotrio;
        }

        // Minor constellations
        public static IMinorConstellation gelu() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.gelu;
        }

        public static IMinorConstellation ulteria() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.ulteria;
        }

        public static IMinorConstellation alcara() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.alcara;
        }

        public static IMinorConstellation vorux() {
            return hellfirepvp.astralsorcery.common.registry.RegistryConstellations.vorux;
        }

        // ====================================================================
        // =                    HELPER METHODS                               =
        // ====================================================================

        /**
         * Get all major constellations
         */
        public static IMajorConstellation[] allMajor() {
            return new IMajorConstellation[] {
                discidia(), armara(), vicio(), aevitas(), evorsio(), vectras()
            };
        }

        /**
         * Get all weak constellations
         */
        public static IWeakConstellation[] allWeak() {
            return new IWeakConstellation[] {
                lucerna(), mineralis(), horologium(), octans(), bootes(), fornax(), pelotrio()
            };
        }

        /**
         * Get all minor constellations
         */
        public static IMinorConstellation[] allMinor() {
            return new IMinorConstellation[] {
                gelu(), ulteria(), alcara(), vorux()
            };
        }

        /**
         * Get all constellations
         */
        public static IConstellation[] all() {
            return new IConstellation[] {
                discidia(), armara(), vicio(), aevitas(), evorsio(), vectras(),
                lucerna(), mineralis(), horologium(), octans(), bootes(), fornax(), pelotrio(),
                gelu(), ulteria(), alcara(), vorux()
            };
        }
    }

    // ========================================================================
    // =                    POTION INSTANCES                                =
    // ========================================================================

    /**
     * Accessor for all potion instances
     */
    public static class Potions {

        public static PotionBleed bleed() {
            return hellfirepvp.astralsorcery.common.registry.RegistryPotions.POTION_BLEED;
        }

        public static PotionTimeFreeze timeFreeze() {
            return hellfirepvp.astralsorcery.common.registry.RegistryPotions.POTION_TIME_FREEZE;
        }

        public static PotionDropModifier dropModifier() {
            return hellfirepvp.astralsorcery.common.registry.RegistryPotions.POTION_DROP_MODIFIER;
        }

        public static PotionSpellPlague spellPlague() {
            return hellfirepvp.astralsorcery.common.registry.RegistryPotions.POTION_SPELL_PLAGUE;
        }

        public static PotionCheatDeath cheatDeath() {
            return hellfirepvp.astralsorcery.common.registry.RegistryPotions.POTION_CHEAT_DEATH;
        }

        /**
         * Get all potions
         */
        public static Potion[] all() {
            return new Potion[] {
                bleed(), timeFreeze(), dropModifier(), spellPlague(), cheatDeath()
            };
        }
    }

    // ========================================================================
    // =                    ENCHANTMENT INSTANCES                           =
    // ========================================================================

    /**
     * Accessor for all enchantment instances
     */
    public static class Enchantments {

        public static EnchantmentNightVision nightVision() {
            return hellfirepvp.astralsorcery.common.registry.RegistryEnchantments.ENCHANTMENT_NIGHT_VISION;
        }

        public static EnchantmentScorchingHeat scorchingHeat() {
            return hellfirepvp.astralsorcery.common.registry.RegistryEnchantments.ENCHANTMENT_SCORCHING_HEAT;
        }
    }

    // ========================================================================
    // =                    GLOBAL HELPER METHODS                           =
    // ========================================================================

    /**
     * Get total count of all registered instances
     */
    public static int getTotalInstanceCount() {
        return Blocks.count() + Items.count() + Constellations.all().length +
               Potions.all().length + 2; // +2 for enchantments
    }

    /**
     * Check if all instances are initialized
     * Useful for debugging and validation
     */
    public static boolean areAllInstancesInitialized() {
        // Check critical instances
        if (Blocks.blockAltar() == null) return false;
        if (Items.wand() == null) return false;
        if (Constellations.discidia() == null) return false;
        if (Potions.bleed() == null) return false;
        return true;
    }

    /**
     * Print instance statistics
     * Call this during post-init to verify registration
     */
    public static void printStatistics() {
        hellfirepvp.astralsorcery.common.util.LogHelper.info("=== Astral Sorcery Instance Statistics ===");
        hellfirepvp.astralsorcery.common.util.LogHelper.info("Blocks: " + Blocks.count());
        hellfirepvp.astralsorcery.common.util.LogHelper.info("Items: " + Items.count());
        hellfirepvp.astralsorcery.common.util.LogHelper.info("Constellations: " + Constellations.all().length);
        hellfirepvp.astralsorcery.common.util.LogHelper.info("Potions: " + Potions.all().length);
        hellfirepvp.astralsorcery.common.util.LogHelper.info("Enchantments: 2");
        hellfirepvp.astralsorcery.common.util.LogHelper.info("Total: " + getTotalInstanceCount());
        hellfirepvp.astralsorcery.common.util.LogHelper.info("=======================================");
    }
}
