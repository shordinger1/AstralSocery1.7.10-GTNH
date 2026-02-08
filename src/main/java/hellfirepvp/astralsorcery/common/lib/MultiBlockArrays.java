/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import hellfirepvp.astralsorcery.common.structure.MultiblockStructures;

/**
 * Multiblock structure definitions for Astral Sorcery
 * <p>
 * This class provides centralized access to all multiblock structure definitions.
 * In the 1.7.10 version, structures are defined using StructureLib instead of
 * the custom BlockArray system used in 1.12.2.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * This class acts as a compatibility layer, delegating to {@link MultiblockStructures}
 * which uses the StructureLib system commonly found in GTNH modpacks.
 * <p>
 * <b>Migration from 1.12.2:</b>
 * <ul>
 * <li>StructureBlockArray/PatternBlockArray replaced with IStructureDefinition</li>
 * <li>Uses StructureLib for structure validation and placement</li>
 * <li>@PasteBlacklist annotation not applicable in 1.7.10</li>
 * </ul>
 *
 * @author HellFirePvP
 * @date 16.05.2016 / 15:45
 */
public class MultiBlockArrays {

    // ==================== World Generation Structures ====================

    /**
     * Ancient Shrine structure
     * <p>
     * A large shrine structure generated in the world.
     * Contains treasure and celestial crystals.
     */
    public static IStructureDefinition<Object> ancientShrine;

    /**
     * Desert Shrine structure
     * <p>
     * A shrine variant found in desert biomes.
     */
    public static IStructureDefinition<Object> desertShrine;

    /**
     * Small Shrine structure
     * <p>
     * A smaller shrine structure for tighter spaces.
     */
    public static IStructureDefinition<Object> smallShrine;

    /**
     * Treasure Shrine structure
     * <p>
     * A shrine containing valuable loot.
     */
    public static IStructureDefinition<Object> treasureShrine;

    /**
     * Small Ruin structure
     * <p>
     * A small ruined structure found during world generation.
     */
    public static IStructureDefinition<Object> smallRuin;

    // ==================== Crafting Structures ====================

    /**
     * Ritual Pedestal structure
     * <p>
     * Single pedestal - no additional structure required
     */
    public static IStructureDefinition<Object> patternRitualPedestal;

    /**
     * Ritual Pedestal with Link structure
     * <p>
     * Ritual pedestal connected to a ritual link
     */
    public static IStructureDefinition<Object> patternRitualPedestalWithLink;

    /**
     * Attunement Altar structure
     * <p>
     * The marble constellation required for the attunement altar.
     */
    public static IStructureDefinition<Object> patternAltarAttunement;

    /**
     * Constellation Altar structure
     * <p>
     * The marble constellation required for constellation crafting.
     */
    public static IStructureDefinition<Object> patternAltarConstellation;

    /**
     * Trait Altar structure
     * <p>
     * The marble constellation required for trait crafting.
     */
    public static IStructureDefinition<Object> patternAltarTrait;

    /**
     * Attunement Frame structure
     * <p>
     * The frame structure for starlight attunement.
     */
    public static IStructureDefinition<Object> patternAttunementFrame;

    /**
     * Starlight Infuser structure
     * <p>
     * The complete starlight infuser multiblock.
     */
    public static IStructureDefinition<Object> patternStarlightInfuser;

    /**
     * Collector Relay structure
     * <p>
     * The starlight collector crystal with relay.
     */
    public static IStructureDefinition<Object> patternCollectorRelay;

    /**
     * Celestial Gateway structure
     * <p>
     * The complete celestial gateway multiblock.
     */
    public static IStructureDefinition<Object> patternCelestialGateway;

    /**
     * Fountain structure
     * <p>
     * The liquid starlight fountain multiblock.
     */
    public static IStructureDefinition<Object> patternFountain;

    /**
     * Collector Enhancement structure
     * <p>
     * The enhancement structure for collector crystals.
     */
    public static IStructureDefinition<Object> patternCollectorEnhancement;

    /**
     * Initialize all multiblock structure references
     * <p>
     * This method should be called during mod initialization to populate
     * the static fields with structure definitions from MultiblockStructures.
     */
    public static void init() {
        // World generation structures
        ancientShrine = MultiblockStructures.ANCIENT_SHRINE;
        desertShrine = MultiblockStructures.DESERT_SHRINE;
        smallShrine = MultiblockStructures.SMALL_SHRINE;
        treasureShrine = MultiblockStructures.TREASURE_SHRINE;
        smallRuin = MultiblockStructures.SMALL_RUIN;

        // Crafting structures
        patternRitualPedestal = MultiblockStructures.RITUAL_PEDESTAL;
        // patternRitualPedestalWithLink = MultiblockStructures.RITUAL_PEDESTAL_WITH_LINK; // TODO: Implement
        patternAltarAttunement = MultiblockStructures.ALTAR_ATTUNEMENT_SIMPLE;
        patternAltarConstellation = MultiblockStructures.ALTAR_CONSTELLATION_SIMPLE;
        patternAltarTrait = MultiblockStructures.ALTAR_TRAIT_SIMPLE;
        // patternAttunementFrame = MultiblockStructures.ATTUNEMENT_FRAME; // TODO: Implement
        patternStarlightInfuser = MultiblockStructures.STARLIGHT_INFUSER;
        patternCollectorRelay = MultiblockStructures.COLLECTOR_RELAY;
        patternCelestialGateway = MultiblockStructures.CELESTIAL_GATEWAY;
        patternFountain = MultiblockStructures.FOUNTAIN;
        // patternCollectorEnhancement = MultiblockStructures.COLLECTOR_ENHANCEMENT; // TODO: Implement
    }

    /**
     * Annotation to blacklist structures from paste operations
     * <p>
     * <b>1.7.10 Note:</b>
     * This annotation is not used in the 1.7.10 version as StructureLib
     * handles structure paste operations differently.
     * Kept for API compatibility only.
     */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
    public static @interface PasteBlacklist {}

}
