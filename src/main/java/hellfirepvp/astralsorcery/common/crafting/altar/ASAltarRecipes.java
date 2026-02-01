/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ASAltarRecipes - Altar recipe registration
 *
 * 1.7.10: GT-style recipe registration
 *******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.reference.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Altar recipe registration for Astral Sorcery
 * <p>
 * Registers all altar recipes for the GT-style recipe system.
 * <p>
 * Recipes are organized by altar level:
 * <ul>
 * <li>DISCOVERY - Basic constellation discovery recipes</li>
 * <li>ATTUNEMENT - Crystal attunement recipes</li>
 * <li>CONSTELLATION_CRAFT - Advanced constellation crafting</li>
 * <li>TRAIT_CRAFT - Trait application recipes</li>
 * <li>BRILLIANCE - End-game crafting recipes</li>
 * </ul>
 */
public class ASAltarRecipes {

    /**
     * Register all altar recipes
     * Called during postInit
     */
    public static void registerRecipes() {
        LogHelper.info("Registering altar recipes...");
        System.out.println("[ASAltarRecipes] Starting recipe registration...");

        int count = 0;

        // Register Discovery Altar recipes
        System.out.println("[ASAltarRecipes] Registering Discovery Altar recipes...");
        count += registerDiscoveryRecipes();

        // Register Attunement Altar recipes
        System.out.println("[ASAltarRecipes] Registering Attunement Altar recipes...");
        count += registerAttunementRecipes();

        // Register Constellation Altar recipes
        System.out.println("[ASAltarRecipes] Registering Constellation Altar recipes...");
        count += registerConstellationRecipes();

        // Register special item recipes (Attunement level for now)
        System.out.println("[ASAltarRecipes] Registering Special Item recipes...");
        count += registerSpecialItemRecipes();

        // Register Trait Altar recipes
        System.out.println("[ASAltarRecipes] Registering Trait Altar recipes...");
        count += registerTraitRecipes();

        // TODO: Register Brilliance Altar recipes
        // count += registerBrillianceRecipes();

        LogHelper.info("Registered " + count + " altar recipes");
        System.out.println("[ASAltarRecipes] Finished registering " + count + " altar recipes");
    }

    /**
     * Register Discovery Altar recipes
     * Basic recipes for constellation discovery
     */
    private static int registerDiscoveryRecipes() {
        int count = 0;

        // Recipe 1: Paper + Crystal + Infused Wood -> Constellation Paper
        // Input: Paper, Crystal, Infused Wood Plank
        // Output: Constellation Paper
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Items.paper), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockInfusedWood, 1, 2) // Plank variant
            },
            new ItemStack(ItemsAS.constellationPaper),
            700,
            100);
        count++;

        // Recipe 3: Book + Crystal + Marble -> Journal
        // Input: Book, Crystal, Marble
        // Output: Journal
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Items.book), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.journal),
            700,
            100);
        count++;

        // Recipe 4: Glass + Crystal + Starlight -> Infused Glass
        // Input: Glass, Crystal, Marble
        // Output: Infused Glass
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Blocks.glass), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.infusedGlass),
            700,
            100);
        count++;

        // Recipe 5: Glass + Crystal + Dye -> Colored Lens
        // Input: Glass, Crystal, Dye (any color)
        // Output: Colored Lens
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Blocks.glass), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(Items.dye, 1, 0) // Black dye as example
            },
            new ItemStack(ItemsAS.coloredLens),
            700,
            100);
        count++;

        // Recipe 6: Glass + Crystal + Gold Ingot -> Hand Telescope
        // Input: Glass, Crystal, Gold Ingot
        // Output: Hand Telescope
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Blocks.glass), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(Items.gold_ingot) },
            new ItemStack(ItemsAS.handTelescope),
            700,
            100);
        count++;

        // Recipe 7: Nether Star + Crystal + Marble -> Knowledge Fragment
        // Input: Nether Star, Crystal, Marble
        // Output: Knowledge Fragment
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Items.nether_star), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.knowledgeFragment),
            700,
            100);
        count++;

        // Recipe 8: Knowledge Fragment + Bottle -> Fragment Capsule
        // Input: Knowledge Fragment, Glass Bottle
        // Output: Fragment Capsule
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.knowledgeFragment), new ItemStack(Items.glass_bottle) },
            new ItemStack(ItemsAS.fragmentCapsule),
            700,
            100);
        count++;

        // Recipe 9: Knowledge Fragment x3 + Crystal -> Knowledge Share
        // Input: 3 Knowledge Fragments, Crystal
        // Output: Knowledge Share
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.knowledgeFragment, 3), new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.knowledgeShare),
            700,
            100);
        count++;

        // Recipe 10: Glowstone + Crystal + Marble -> Usable Dust
        // Input: Glowstone Dust, Crystal, Marble
        // Output: Usable Dust (Resonant)
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Items.glowstone_dust), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.usableDust),
            700,
            100);
        count++;

        // Recipe 11: Marble + Crystal + Glowstone -> Crafting Component
        // Input: Marble, Crystal, Glowstone
        // Output: Crafting Component
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockMarble), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(Items.glowstone_dust) },
            new ItemStack(ItemsAS.craftingComponent),
            700,
            100);
        count++;

        // Recipe 12: Nether Star + Crystal + Marble -> Celestial Crystal
        // Input: Nether Star, Crystal, Marble
        // Output: Celestial Crystal
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Items.nether_star), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.celestialCrystal),
            700,
            100);
        count++;

        // Recipe 13: Stick + Crystal + Marble -> Wand
        // Input: Stick, Crystal, Marble
        // Output: Wand
        // Starlight: 700
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(Items.stick), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.wand),
            700,
            100);
        count++;

        // Recipe 14: Sextant (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [Glass Lens] [ ]
        // [Gold] [Glass Lens] [Gold]
        // [Stick] [ Stick ] [Stick]
        // Output: Sextant
        // Starlight: 450
        // Time: 100 ticks
        addShapedDiscoveryRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(ItemsAS.coloredLens), // UPPER_CENTER (1) - Glass Lens
                null, // UPPER_RIGHT (2)
                new ItemStack(Items.gold_ingot), // LEFT (3)
                new ItemStack(ItemsAS.coloredLens), // CENTER (4) - Glass Lens
                new ItemStack(Items.gold_ingot), // RIGHT (5)
                new ItemStack(Items.stick), // LOWER_LEFT (6)
                new ItemStack(Items.stick), // LOWER_CENTER (7)
                new ItemStack(Items.stick) // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.sextant),
            450,
            100,
            3, // width
            3 // height
        );
        count++;

        // Recipe 15: Illumination Powder (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [Glowstone] [ ]
        // [Glowstone][Aquamarine][Glowstone]
        // [ ] [Glowstone] [ ]
        // Output: 16x Illumination Powder
        // Starlight: 200
        // Time: 100 ticks
        addShapedDiscoveryRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(Items.glowstone_dust), // UPPER_CENTER (1)
                null, // UPPER_RIGHT (2)
                new ItemStack(Items.glowstone_dust), // LEFT (3)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.AQUAMARINE.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Aquamarine
                new ItemStack(Items.glowstone_dust), // RIGHT (5)
                null, // LOWER_LEFT (6)
                new ItemStack(Items.glowstone_dust), // LOWER_CENTER (7)
                null // LOWER_RIGHT (8)
            },
            new ItemStack(
                ItemsAS.usableDust,
                16,
                hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.ILLUMINATION.ordinal()), // 16x
                                                                                                       // Illumination
                                                                                                       // Dust
            200,
            100,
            3, // width
            3 // height
        );
        count++;

        // Recipe 16: Nocturnal Powder (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [Black Dye] [ ]
        // [Coal][Illumination][Coal]
        // [ ] [ Blue Dye ] [ ]
        // Output: 4x Nocturnal Powder
        // Starlight: 300
        // Time: 100 ticks
        addShapedDiscoveryRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(Items.dye, 1, 0), // UPPER_CENTER (1) - Black Dye (Ink Sac)
                null, // UPPER_RIGHT (2)
                new ItemStack(Items.coal), // LEFT (3)
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.ILLUMINATION.ordinal()), // CENTER (4)
                                                                                                           // -
                                                                                                           // Illumination
                                                                                                           // Powder
                new ItemStack(Items.coal), // RIGHT (5)
                null, // LOWER_LEFT (6)
                new ItemStack(Items.dye, 1, 4), // LOWER_CENTER (7) - Blue Dye (Lapis Lazuli)
                null // LOWER_RIGHT (8)
            },
            new ItemStack(
                ItemsAS.usableDust,
                4,
                hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.NOCTURNAL.ordinal()), // 4x Nocturnal Dust
            300,
            100,
            3, // width
            3 // height
        );
        count++;

        // Recipe 17: Infused Wood Planks (4x)
        // Input: 4 Infused Wood Logs
        // Output: 4 Infused Wood Planks
        // Starlight: 20
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockInfusedWood, 1, 0), // Log
                new ItemStack(BlocksAS.blockInfusedWood, 1, 0), new ItemStack(BlocksAS.blockInfusedWood, 1, 0),
                new ItemStack(BlocksAS.blockInfusedWood, 1, 0) },
            new ItemStack(BlocksAS.blockInfusedWood, 4, 2), // Plank variant
            20,
            100);
        count++;

        // Recipe 18-29: Black Marble variants (all types)
        // Recipe: 8 Marble + 1 Coal -> 8 Black Marble Raw
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockMarble), new ItemStack(BlocksAS.blockMarble),
                new ItemStack(BlocksAS.blockMarble), new ItemStack(BlocksAS.blockMarble),
                new ItemStack(BlocksAS.blockMarble), new ItemStack(BlocksAS.blockMarble),
                new ItemStack(BlocksAS.blockMarble), new ItemStack(BlocksAS.blockMarble), new ItemStack(Items.coal) },
            new ItemStack(BlocksAS.blockBlackMarble, 8, 0), // Raw
            20,
            100);
        count++;

        // Black Marble Arch (3x)
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockBlackMarble, 1, 0),
                new ItemStack(BlocksAS.blockBlackMarble, 1, 0), new ItemStack(BlocksAS.blockBlackMarble, 1, 0) },
            new ItemStack(BlocksAS.blockBlackMarble, 3, 1), // Arch
            20,
            100);
        count++;

        // Black Marble Brick (4x)
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockBlackMarble, 1, 0),
                new ItemStack(BlocksAS.blockBlackMarble, 1, 0), new ItemStack(BlocksAS.blockBlackMarble, 1, 0),
                new ItemStack(BlocksAS.blockBlackMarble, 1, 0) },
            new ItemStack(BlocksAS.blockBlackMarble, 4, 2), // Brick
            20,
            100);
        count++;

        // Black Marble Chiseled (5x)
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockBlackMarble, 1, 0),
                new ItemStack(BlocksAS.blockBlackMarble, 1, 0), new ItemStack(BlocksAS.blockBlackMarble, 1, 0),
                new ItemStack(BlocksAS.blockBlackMarble, 1, 0), new ItemStack(BlocksAS.blockBlackMarble, 1, 0),
                new ItemStack(ItemsAS.craftingComponent, 1, 0) // Engraved
            },
            new ItemStack(BlocksAS.blockBlackMarble, 5, 3), // Chiseled
            20,
            100);
        count++;

        // Black Marble Pillar (2x)
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockBlackMarble, 1, 0),
                new ItemStack(BlocksAS.blockBlackMarble, 1, 0) },
            new ItemStack(BlocksAS.blockBlackMarble, 2, 4), // Pillar
            20,
            100);
        count++;

        // Recipe 30: Basic Wand (Tempering Wand)
        // Input: 1 Aquamarine + 1 Ender Pearl + 4 Marble
        // Output: Tempering Wand
        // Starlight: 200
        // Time: 100 ticks
        addDiscoveryRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.rockCrystalSimple), // Aquamarine placeholder
                new ItemStack(Items.ender_pearl), new ItemStack(BlocksAS.blockMarble),
                new ItemStack(BlocksAS.blockMarble), new ItemStack(BlocksAS.blockMarble),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.wand),
            200,
            100);
        count++;

        return count;
    }

    /**
     * Register Attunement Altar recipes
     * Crystal attunement and enhancement recipes
     */
    private static int registerAttunementRecipes() {
        int count = 0;

        // Recipe 1: Rock Crystal + Constellation Paper -> Tuned Rock Crystal
        // Input: Rock Crystal, Constellation Paper (with constellation)
        // Output: Tuned Rock Crystal
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.rockCrystalSimple), new ItemStack(ItemsAS.constellationPaper) },
            new ItemStack(ItemsAS.tunedRockCrystal),
            null, // No specific constellation required
            1000,
            200);
        count++;

        // Recipe 2: Celestial Crystal + Constellation Paper -> Tuned Celestial Crystal
        // Input: Celestial Crystal, Constellation Paper
        // Output: Tuned Celestial Crystal
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.celestialCrystal), new ItemStack(ItemsAS.constellationPaper) },
            new ItemStack(ItemsAS.tunedCelestialCrystal),
            null,
            1000,
            200);
        count++;

        // Recipe 3: Wand + Iron Ingot + Crystal -> Grapple Wand
        // Input: Wand, Iron Ingot, Crystal
        // Output: Grapple Wand
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.wand), new ItemStack(Items.iron_ingot),
                new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.grappleWand),
            null,
            1000,
            200);
        count++;

        // Recipe 4: Wand + Crystal + Marble -> Architect Wand
        // Input: Wand, Crystal, Marble
        // Output: Architect Wand
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.wand), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.architectWand),
            null,
            1000,
            200);
        count++;

        // Recipe 5: Wand + Crystal + Gold Ingot -> Exchange Wand
        // Input: Wand, Crystal, Gold Ingot
        // Output: Exchange Wand
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.wand), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(Items.gold_ingot) },
            new ItemStack(ItemsAS.exchangeWand),
            null,
            1000,
            200);
        count++;

        // Recipe 6: Wand + Torch + Crystal -> Illumination Wand
        // Input: Wand, Torch, Crystal
        // Output: Illumination Wand
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.wand), new ItemStack(Blocks.torch),
                new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.illuminationWand),
            null,
            1000,
            200);
        count++;

        // Recipe 7: Crystal Pickaxe + Crystal -> Charged Crystal Pickaxe
        // Input: Crystal Pickaxe, Rock Crystal
        // Output: Charged Crystal Pickaxe
        // Starlight: 1500
        // Time: 300 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.crystalPickaxe), new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.chargedCrystalPickaxe),
            null,
            1500,
            300);
        count++;

        // Recipe 8: Crystal Sword + Crystal -> Charged Crystal Sword
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.crystalSword), new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.chargedCrystalSword),
            null,
            1500,
            300);
        count++;

        // Recipe 9: Crystal Axe + Crystal -> Charged Crystal Axe
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.crystalAxe), new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.chargedCrystalAxe),
            null,
            1500,
            300);
        count++;

        // Recipe 10: Crystal Shovel + Crystal -> Charged Crystal Shovel
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.crystalShovel), new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.chargedCrystalShovel),
            null,
            1500,
            300);
        count++;

        // Recipe 11: Bow + Crystal + Marble -> Rose Branch Bow
        // Input: Bow, Crystal, Marble
        // Output: Rose Branch Bow
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(Items.bow), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.roseBranchBow),
            null,
            1000,
            200);
        count++;

        // Recipe 12: Wand + Linking Item + Crystal -> Linking Tool
        // Input: Wand, Iron Ingot, Crystal
        // Output: Linking Tool
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.wand), new ItemStack(Items.iron_ingot),
                new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.linkingTool),
            null,
            1000,
            200);
        count++;

        // Recipe 13: Telescope + Crystal + Marble -> Sky Resonator
        // Input: Hand Telescope, Crystal, Marble
        // Output: Sky Resonator
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.handTelescope), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.skyResonator),
            null,
            1000,
            200);
        count++;

        // Recipe 14: Compass + Crystal + Marble -> Sextant
        // Input: Compass, Crystal, Marble
        // Output: Sextant
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(Items.compass), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(BlocksAS.blockMarble) },
            new ItemStack(ItemsAS.sextant),
            null,
            1000,
            200);
        count++;

        // Recipe 15: Knowledge Share (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [ Feather ] [ ]
        // [StarDust][Parchment][StarDust]
        // [ ] [Black Dye ] [ ]
        // Output: Knowledge Share
        // Starlight: 1000
        // Time: 200 ticks
        addShapedAttunementRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(Items.feather), // UPPER_CENTER (1)
                null, // UPPER_RIGHT (2)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()), // LEFT
                                                                                                              // (3) -
                                                                                                              // Starmetal
                                                                                                              // Dust
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.PARCHMENT.ordinal()), // CENTER
                                                                                                               // (4) -
                                                                                                               // Parchment
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()), // RIGHT
                                                                                                              // (5) -
                                                                                                              // Starmetal
                                                                                                              // Dust
                null, // LOWER_LEFT (6)
                new ItemStack(Items.dye, 1, 0), // LOWER_CENTER (7) - Black Dye (Ink Sac)
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.knowledgeShare),
            null, // No specific constellation required
            1000,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 16: Shifting Star (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [Runed ][StarDust ][Runed ]
        // [Illum.][Starlite][Illum.]
        // [Runed ][StarDust ][Runed ]
        // Output: Shifting Star
        // Starlight: 1200
        // Time: 200 ticks
        addShapedAttunementRecipe(
            new ItemStack[] { new ItemStack(BlocksAS.blockMarble, 1, 6), // UPPER_LEFT (0) - Runed Marble
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()), // UPPER_CENTER
                                                                                                              // (1) -
                                                                                                              // Starmetal
                                                                                                              // Dust
                new ItemStack(BlocksAS.blockMarble, 1, 6), // UPPER_RIGHT (2) - Runed Marble
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.ILLUMINATION.ordinal()), // LEFT (3) -
                                                                                                           // Illumination
                                                                                                           // Powder
                new ItemStack(Blocks.stone), // CENTER (4) - Placeholder for Liquid Starlight
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.ILLUMINATION.ordinal()), // RIGHT (5)
                                                                                                           // -
                                                                                                           // Illumination
                                                                                                           // Powder
                new ItemStack(BlocksAS.blockMarble, 1, 6), // LOWER_LEFT (6) - Runed Marble
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()), // LOWER_CENTER
                                                                                                              // (7) -
                                                                                                              // Starmetal
                                                                                                              // Dust
                new ItemStack(BlocksAS.blockMarble, 1, 6) // LOWER_RIGHT (8) - Runed Marble
            },
            new ItemStack(ItemsAS.shiftingStar),
            null, // No specific constellation required
            1200,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 17: Perk Seal (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [Nocturnal] [ ]
        // [Nocturnal][Glass Lens][Nocturnal]
        // [ ] [StarDust ] [ ]
        // Output: Perk Seal
        // Starlight: 1000
        // Time: 200 ticks
        addShapedAttunementRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.NOCTURNAL.ordinal()), // UPPER_CENTER
                                                                                                        // (1) -
                                                                                                        // Nocturnal
                                                                                                        // Powder
                null, // UPPER_RIGHT (2)
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.NOCTURNAL.ordinal()), // LEFT (3) -
                                                                                                        // Nocturnal
                                                                                                        // Powder
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.NOCTURNAL.ordinal()), // RIGHT (5) -
                                                                                                        // Nocturnal
                                                                                                        // Powder
                null, // LOWER_LEFT (6)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()), // LOWER_CENTER
                                                                                                              // (7) -
                                                                                                              // Starmetal
                                                                                                              // Dust
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.perkSeal),
            null, // No specific constellation required
            1000,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 18: Transmission Wand (传送魔杖)
        // Input: 1 Purple Dye + 1 Ender Pearl + 4 Starstone + 1 Rune Marble
        // Output: Transmission Wand
        // Starlight: 1600
        // Time: 100 ticks
        addShapedAttunementRecipe(
            new ItemStack[] { null, // UPPER_LEFT
                new ItemStack(Items.dye, 1, 5), // Purple Dye - UPPER_CENTER
                null, // UPPER_RIGHT
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - LEFT
                new ItemStack(Items.ender_pearl), // CENTER
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - RIGHT
                null, // LOWER_LEFT
                new ItemStack(ItemsAS.craftingComponent, 1, 0), // Rune - LOWER_CENTER
                null // LOWER_RIGHT
            },
            new ItemStack(ItemsAS.wand),
            "aevitas", // Any constellation
            1600,
            100,
            3, // width
            3 // height
        );
        count++;

        // Recipe 19: Exchange Wand (交换魔杖)
        // Input: 1 Rune Marble + 4 Starstone + 1 Diamond + 1 Rune Marble
        // Output: Exchange Wand
        // Starlight: 1600
        // Time: 100 ticks
        addShapedAttunementRecipe(
            new ItemStack[] { null, // UPPER_LEFT
                new ItemStack(ItemsAS.craftingComponent, 1, 0), // Rune - UPPER_CENTER
                null, // UPPER_RIGHT
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - LEFT
                new ItemStack(Items.diamond), // CENTER
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - RIGHT
                null, // LOWER_LEFT
                new ItemStack(ItemsAS.craftingComponent, 1, 0), // Rune - LOWER_CENTER
                null // LOWER_RIGHT
            },
            new ItemStack(ItemsAS.wand),
            "evorsio", // Any constellation
            1600,
            100,
            3, // width
            3 // height
        );
        count++;

        // Recipe 20: Building Wand (建筑魔杖)
        // Input: 1 Purple Dye + 4 Starstone + 1 Rune Marble
        // Output: Building Wand
        // Starlight: 1600
        // Time: 100 ticks
        addShapedAttunementRecipe(
            new ItemStack[] { null, // UPPER_LEFT
                new ItemStack(Items.dye, 1, 5), // Purple Dye - UPPER_CENTER
                null, // UPPER_RIGHT
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - LEFT
                new ItemStack(ItemsAS.rockCrystalSimple), // CENTER
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - RIGHT
                null, // LOWER_LEFT
                new ItemStack(ItemsAS.craftingComponent, 1, 0), // Rune - LOWER_CENTER
                null // LOWER_RIGHT
            },
            new ItemStack(ItemsAS.wand),
            "discidia", // Any constellation
            1600,
            100,
            3, // width
            3 // height
        );
        count++;

        // Recipe 21: Grapple Wand (抓钩魔杖)
        // Input: 1 Ender Pearl + 1 Purple Dye + 4 Starstone + 1 Rune Marble
        // Output: Grapple Wand
        // Starlight: 1600
        // Time: 100 ticks
        addShapedAttunementRecipe(
            new ItemStack[] { null, // UPPER_LEFT
                new ItemStack(Items.dye, 1, 5), // Purple Dye - UPPER_CENTER
                null, // UPPER_RIGHT
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - LEFT
                new ItemStack(Items.ender_pearl), // CENTER
                new ItemStack(ItemsAS.rockCrystalSimple), // Starstone - RIGHT
                null, // LOWER_LEFT
                new ItemStack(ItemsAS.craftingComponent, 1, 0), // Rune - LOWER_CENTER
                null // LOWER_RIGHT
            },
            new ItemStack(ItemsAS.wand),
            "vicio", // Any constellation
            1600,
            100,
            3, // width
            3 // height
        );
        count++;

        // Recipe 22: Knowledge Share
        // Input: 1 Paper + 1 Feather + 1 Black Dye + 4 Starstone
        // Output: Knowledge Share
        // Starlight: 800
        // Time: 100 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(Items.paper), new ItemStack(Items.feather), new ItemStack(Items.dye, 1, 0), // Black
                                                                                                                        // Dye
                new ItemStack(ItemsAS.rockCrystalSimple), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(ItemsAS.rockCrystalSimple), new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.knowledgeShare),
            null, // No constellation
            800,
            100);
        count++;

        return count;
    }

    /**
     * Register Constellation Altar recipes
     * Advanced constellation crafting recipes
     */
    private static int registerConstellationRecipes() {
        int count = 0;

        // Recipe 1: Infused Glass (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [ColoredLens] [ ]
        // [Glass][ ResoGem ][Glass]
        // [ ] [ Stone ] [ ]
        // Output: Infused Glass
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(ItemsAS.coloredLens), // UPPER_CENTER (1) - Any colored lens
                null, // UPPER_RIGHT (2)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // LEFT
                                                                                                                // (3) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.RESO_GEM.ordinal()), // CENTER
                                                                                                              // (4) -
                                                                                                              // Reso
                                                                                                              // Gem
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // RIGHT
                                                                                                                // (5) -
                                                                                                                // Glass
                                                                                                                // Lens
                null, // LOWER_LEFT (6)
                new ItemStack(Blocks.stone), // LOWER_CENTER (7) - Placeholder for constellation slots
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.infusedGlass),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 2: Ritual Link (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [GoldNugget][GoldIngot][GoldNugget]
        // [GlassLens][ResoGem ][GlassLens]
        // [GlassPane][StarDust][GlassPane]
        // Output: 2x Ritual Link
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { new ItemStack(Items.gold_nugget), // UPPER_LEFT (0)
                new ItemStack(Items.gold_ingot), // UPPER_CENTER (1)
                new ItemStack(Items.gold_nugget), // UPPER_RIGHT (2)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // LEFT
                                                                                                                // (3) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.RESO_GEM.ordinal()), // CENTER
                                                                                                              // (4) -
                                                                                                              // Reso
                                                                                                              // Gem
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // RIGHT
                                                                                                                // (5) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(Blocks.glass_pane), // LOWER_LEFT (6)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()), // LOWER_CENTER
                                                                                                              // (7) -
                                                                                                              // Starmetal
                                                                                                              // Dust
                new ItemStack(Blocks.glass_pane) // LOWER_RIGHT (8)
            },
            new ItemStack(BlocksAS.ritualLink, 2),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 3: Glass Lens - Fire (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [Aquamarine] [ ]
        // [ ] [GlassLens] [ ]
        // [ ] [Aquamarine] [ ]
        // Output: Fire Colored Lens
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.AQUAMARINE.ordinal()), // UPPER_CENTER
                                                                                                                // (1)
                null, // UPPER_RIGHT (2)
                null, // LEFT (3)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                null, // RIGHT (5)
                null, // LOWER_LEFT (6)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.AQUAMARINE.ordinal()), // LOWER_CENTER
                                                                                                                // (7)
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.coloredLens),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 4: Glass Lens - Break (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [ Diamond ] [ ]
        // [ ] [GlassLens] [ ]
        // [ ] [IronPick ] [ ]
        // Output: Break Colored Lens
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(Items.diamond), // UPPER_CENTER (1)
                null, // UPPER_RIGHT (2)
                null, // LEFT (3)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                null, // RIGHT (5)
                null, // LOWER_LEFT (6)
                new ItemStack(Items.iron_pickaxe), // LOWER_CENTER (7)
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.coloredLens),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 5: Glass Lens - Growth (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [Aquamarine] [ ]
        // [Carrot][GlassLens][Carrot]
        // [ ] [ Stone ] [ ]
        // Output: Growth Colored Lens
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.AQUAMARINE.ordinal()), // UPPER_CENTER
                                                                                                                // (1)
                null, // UPPER_RIGHT (2)
                new ItemStack(Items.carrot), // LEFT (3)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(Items.carrot), // RIGHT (5)
                null, // LOWER_LEFT (6)
                new ItemStack(Blocks.stone), // LOWER_CENTER (7) - Placeholder for constellation slots
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.coloredLens),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 6: Glass Lens - Damage (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [ Diamond ] [ ]
        // [Iron ][GlassLens][Iron ]
        // [Flint][ Stone ][Flint]
        // Output: Damage Colored Lens
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(Items.diamond), // UPPER_CENTER (1)
                null, // UPPER_RIGHT (2)
                new ItemStack(Items.iron_ingot), // LEFT (3)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(Items.iron_ingot), // RIGHT (5)
                new ItemStack(Items.flint), // LOWER_LEFT (6)
                new ItemStack(Blocks.stone), // LOWER_CENTER (7) - Placeholder for constellation slots
                new ItemStack(Items.flint) // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.coloredLens),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 7: Glass Lens - Regeneration (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [GhastTear] [ ]
        // [ ] [GlassLens] [ ]
        // [ ] [ Diamond ] [ ]
        // Output: Regeneration Colored Lens
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(Items.ghast_tear), // UPPER_CENTER (1)
                null, // UPPER_RIGHT (2)
                null, // LEFT (3)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                null, // RIGHT (5)
                null, // LOWER_LEFT (6)
                new ItemStack(Items.diamond), // LOWER_CENTER (7)
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.coloredLens),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 8: Glass Lens - Push (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [Piston][ ][Piston]
        // [ ] [GlassLens] [ ]
        // [ ] [ Stone ] [ ]
        // Output: Push Colored Lens
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { new ItemStack(Blocks.piston), // UPPER_LEFT (0)
                null, // UPPER_CENTER (1)
                new ItemStack(Blocks.piston), // UPPER_RIGHT (2)
                null, // LEFT (3)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                null, // RIGHT (5)
                null, // LOWER_LEFT (6)
                new ItemStack(Blocks.stone), // LOWER_CENTER (7) - Placeholder for constellation slots
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.coloredLens),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 9: Glass Lens - Spectral (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [ ] [ ResoGem ] [ ]
        // [Illum.][GlassLens][Illum.]
        // [ ] [ ResoGem ] [ ]
        // Output: Spectral Colored Lens
        // Starlight: 1500
        // Time: 200 ticks
        addShapedConstellationRecipe(
            new ItemStack[] { null, // UPPER_LEFT (0)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.RESO_GEM.ordinal()), // UPPER_CENTER
                                                                                                              // (1)
                null, // UPPER_RIGHT (2)
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.ILLUMINATION.ordinal()), // LEFT (3) -
                                                                                                           // Illumination
                                                                                                           // Powder
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(
                    ItemsAS.usableDust,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemUsableDust.DustType.ILLUMINATION.ordinal()), // RIGHT (5)
                                                                                                           // -
                                                                                                           // Illumination
                                                                                                           // Powder
                null, // LOWER_LEFT (6)
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.RESO_GEM.ordinal()), // LOWER_CENTER
                                                                                                              // (7)
                null // LOWER_RIGHT (8)
            },
            new ItemStack(ItemsAS.coloredLens),
            null, // No specific constellation required
            1500,
            200,
            3, // width
            3 // height
        );
        count++;

        // Recipe 10: Constellation Paper (星座纸) - for all constellations
        // Base recipe for constellation paper
        // Input: Paper + Feather + Black Dye + 4 Starmetal Dust + Constellation signature
        // Output: Constellation Paper
        // Starlight: 800
        // Time: 150 ticks

        // Recipe 11: Aevitas Constellation Paper
        addConstellationRecipe(
            new ItemStack[] { new ItemStack(Items.paper), new ItemStack(Items.feather), new ItemStack(Items.dye, 1, 0), // Black
                                                                                                                        // dye
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()),
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()),
                new ItemStack(Items.diamond), // Aevitas signature
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()) },
            new ItemStack(ItemsAS.constellationPaper),
            "aevitas",
            800,
            150);
        count++;

        // Recipe 12: Armara Constellation Paper
        addConstellationRecipe(
            new ItemStack[] { new ItemStack(Items.paper), new ItemStack(Items.feather), new ItemStack(Items.dye, 1, 0),
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()),
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()),
                new ItemStack(Items.diamond_sword), // Armara signature
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.STARDUST.ordinal()) },
            new ItemStack(ItemsAS.constellationPaper),
            "armara",
            800,
            150);
        count++;

        // Recipe 13: Celestial Crystal (Charged version - placeholder)
        // Note: This is a simplified recipe for celestial crystal enhancement
        addConstellationRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.celestialCrystal), new ItemStack(ItemsAS.celestialCrystal),
                new ItemStack(ItemsAS.celestialCrystal), new ItemStack(ItemsAS.celestialCrystal),
                new ItemStack(ItemsAS.rockCrystalSimple), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.celestialCrystal, 2), // 2x celestial crystals
            null,
            1000,
            200);
        count++;

        return count;
    }

    /**
     * Register Trait Altar recipes
     * Trait application and advanced crafting recipes
     */
    private static int registerTraitRecipes() {
        int count = 0;

        // Recipe 1: Observatory (有序配方 - 精确槽位)
        // Input layout (3x3):
        // [GoldNugget][RunedMarble][InfusedGlass]
        // [RunedMarble][ GlassLens ][RunedMarble]
        // [ Stone ][RunedMarble][ GoldNugget ]
        // Output: Observatory
        // Starlight: 2000
        // Time: 300 ticks
        addShapedTraitRecipe(
            new ItemStack[] { new ItemStack(Items.gold_nugget), // UPPER_LEFT (0)
                new ItemStack(BlocksAS.blockMarble, 1, 6), // UPPER_CENTER (1) - Runed Marble
                new ItemStack(ItemsAS.infusedGlass), // UPPER_RIGHT (2) - Infused Glass
                new ItemStack(BlocksAS.blockMarble, 1, 6), // LEFT (3) - Runed Marble
                new ItemStack(
                    ItemsAS.craftingComponent,
                    1,
                    hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal()), // CENTER
                                                                                                                // (4) -
                                                                                                                // Glass
                                                                                                                // Lens
                new ItemStack(BlocksAS.blockMarble, 1, 6), // RIGHT (5) - Runed Marble
                new ItemStack(Blocks.stone), // LOWER_LEFT (6) - Placeholder for attunement slot
                new ItemStack(BlocksAS.blockMarble, 1, 6), // LOWER_CENTER (7) - Runed Marble
                new ItemStack(Items.gold_nugget) // LOWER_RIGHT (8)
            },
            new ItemStack(BlocksAS.blockObservatory),
            null, // No specific constellation required
            2000,
            300,
            3, // width
            3 // height
        );
        count++;

        // Recipe 2: Celestial Crystal (Enhanced version)
        // Input: 4 Celestial Crystal + 3 Rock Crystal
        // Output: 2x Celestial Crystal
        // Starlight: 1000
        // Time: 150 ticks
        addTraitRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.celestialCrystal), new ItemStack(ItemsAS.celestialCrystal),
                new ItemStack(ItemsAS.celestialCrystal), new ItemStack(ItemsAS.celestialCrystal),
                new ItemStack(ItemsAS.rockCrystalSimple), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.celestialCrystal, 2),
            null,
            1000,
            150);
        count++;

        return count;
    }

    /**
     * Register special item recipes
     * Perks, wearable items, etc.
     */
    private static int registerSpecialItemRecipes() {
        int count = 0;

        // Recipe 1: Knowledge Fragment + Crystal + Clay -> Perk Gem
        // Input: Knowledge Fragment, Crystal, Clay
        // Output: Perk Gem
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.knowledgeFragment), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(Blocks.clay) },
            new ItemStack(ItemsAS.perkGem),
            null,
            1000,
            200);
        count++;

        // Recipe 2: Knowledge Fragment + Crystal + Brick -> Perk Seal
        // Input: Knowledge Fragment, Crystal, Brick
        // Output: Perk Seal
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(ItemsAS.knowledgeFragment), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(Items.brick) },
            new ItemStack(ItemsAS.perkSeal),
            null,
            1000,
            200);
        count++;

        // Recipe 3: Nether Star + Knowledge Fragment + Crystal -> Shifting Star
        // Input: Nether Star, Knowledge Fragment, Crystal
        // Output: Shifting Star
        // Starlight: 1500
        // Time: 300 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(Items.nether_star), new ItemStack(ItemsAS.knowledgeFragment),
                new ItemStack(ItemsAS.rockCrystalSimple) },
            new ItemStack(ItemsAS.shiftingStar),
            null,
            1500,
            300);
        count++;

        // Recipe 4: Cloth + Crystal + Knowledge Fragment -> Cape
        // Input: Cloth (Wool), Crystal, Knowledge Fragment
        // Output: Cape
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(Blocks.wool), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(ItemsAS.knowledgeFragment) },
            new ItemStack(ItemsAS.cape),
            null,
            1000,
            200);
        count++;

        // Recipe 5: Diamond + Crystal + Knowledge Fragment -> Enchantment Amulet
        // Input: Diamond, Crystal, Knowledge Fragment
        // Output: Enchantment Amulet
        // Starlight: 1000
        // Time: 200 ticks
        addAttunementRecipe(
            new ItemStack[] { new ItemStack(Items.diamond), new ItemStack(ItemsAS.rockCrystalSimple),
                new ItemStack(ItemsAS.knowledgeFragment) },
            new ItemStack(ItemsAS.enchantmentAmulet),
            null,
            1000,
            200);
        count++;

        return count;
    }

    /**
     * Helper method to add a Discovery Altar recipe
     */
    private static void addDiscoveryRecipe(ItemStack[] inputs, ItemStack output, int starlight, int craftingTime) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.DISCOVERY,
            inputs,
            output,
            null, // No constellation required for discovery
            starlight,
            craftingTime);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add a shaped Discovery Altar recipe (有序配方)
     * Phase 4: New method for shaped recipes with precise slot positions
     */
    private static void addShapedDiscoveryRecipe(ItemStack[] inputs, ItemStack output, int starlight, int craftingTime,
        int width, int height) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.DISCOVERY,
            inputs,
            output,
            null, // No constellation required for discovery
            starlight,
            craftingTime,
            true, // shaped
            width,
            height);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add an Attunement Altar recipe
     */
    private static void addAttunementRecipe(ItemStack[] inputs, ItemStack output, String constellation, int starlight,
        int craftingTime) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.ATTUNEMENT,
            inputs,
            output,
            constellation,
            starlight,
            craftingTime);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add a shaped Attunement Altar recipe (有序配方)
     * Phase 4: New method for shaped recipes with precise slot positions
     */
    private static void addShapedAttunementRecipe(ItemStack[] inputs, ItemStack output, String constellation,
        int starlight, int craftingTime, int width, int height) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.ATTUNEMENT,
            inputs,
            output,
            constellation,
            starlight,
            craftingTime,
            true, // shaped
            width,
            height);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add a Constellation Altar recipe
     */
    private static void addConstellationRecipe(ItemStack[] inputs, ItemStack output, String constellation,
        int starlight, int craftingTime) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.CONSTELLATION_CRAFT,
            inputs,
            output,
            constellation,
            starlight,
            craftingTime);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add a shaped Constellation Altar recipe (有序配方)
     * Phase 4: New method for shaped recipes with precise slot positions
     */
    private static void addShapedConstellationRecipe(ItemStack[] inputs, ItemStack output, String constellation,
        int starlight, int craftingTime, int width, int height) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.CONSTELLATION_CRAFT,
            inputs,
            output,
            constellation,
            starlight,
            craftingTime,
            true, // shaped
            width,
            height);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add a Trait Altar recipe
     */
    private static void addTraitRecipe(ItemStack[] inputs, ItemStack output, String constellation, int starlight,
        int craftingTime) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.TRAIT_CRAFT,
            inputs,
            output,
            constellation,
            starlight,
            craftingTime);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add a shaped Trait Altar recipe (有序配方)
     * Phase 4: New method for shaped recipes with precise slot positions
     */
    private static void addShapedTraitRecipe(ItemStack[] inputs, ItemStack output, String constellation, int starlight,
        int craftingTime, int width, int height) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.TRAIT_CRAFT,
            inputs,
            output,
            constellation,
            starlight,
            craftingTime,
            true, // shaped
            width,
            height);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    /**
     * Helper method to add a Brilliance Altar recipe
     */
    private static void addBrillianceRecipe(ItemStack[] inputs, ItemStack output, String constellation, int starlight,
        int craftingTime) {
        ASAltarRecipe recipe = new ASAltarRecipe(
            TileAltar.AltarLevel.BRILLIANCE,
            inputs,
            output,
            constellation,
            starlight,
            craftingTime);
        AltarRecipeRegistry.addRecipe(recipe);
    }

    private ASAltarRecipes() {
        // Private constructor
    }
}
