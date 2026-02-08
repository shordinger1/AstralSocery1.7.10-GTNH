/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Multiblock Structures - StructureLib-based structure definitions
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import hellfirepvp.astralsorcery.common.block.BlockAltar.AltarType;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Multiblock Structures - StructureLib-based structure definitions (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Defines all multiblock structures using StructureLib</li>
 * <li>Simplified altar structures for initial implementation</li>
 * <li>Machine structures: Infuser, Relay, Gateway, Fountain</li>
 * <li>World generation structures: Shrines, Ruins</li>
 * </ul>
 * <p>
 * <b>Migration from 1.12.2:</b>
 * <ul>
 * <li>Replaces BlockArray/PatternBlockArray with StructureLib</li>
 * <li>Uses string arrays instead of addBlock() calls</li>
 * <li>Defines elements with ofBlock() and ofBlockAnyMeta()</li>
 * </ul>
 * <p>
 * <b>Structure Definitions:</b>
 * 
 * <pre>
 * Character Mapping:
 * 'A' = Altar (center)
 * 'P' = Pedestal
 * 'M' = Marble (any metadata)
 * 'C' = Chiseled Marble (meta 4)
 * 'R' = Brick Marble (meta 1)
 * 'L' = Pillar Marble (meta 2)
 * 'I' = Infuser
 * 'S' = Storage
 * '~' = Main block (controller)
 * </pre>
 */
public class MultiblockStructures {

    // ==================== Altar Structures ====================

    /**
     * Discovery Altar - No structure required
     * Just the altar block itself
     */
    public static final IStructureDefinition<Object> ALTAR_DISCOVERY = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "~" } }))
        .addElement('~', ofBlock(BlocksAS.blockAltar, AltarType.DISCOVERY.getMetadata()))
        .build();

    /**
     * Attunement Altar - Simple structure for testing
     * <p>
     * Two layers:
     * - Y=0 (bottom): 3x3 marble base
     * - Y=1 (top): Altar in center
     */
    public static final IStructureDefinition<Object> ALTAR_ATTUNEMENT_SIMPLE = StructureDefinition.builder()
        .addShape(
            "main",
            transpose(
                new String[][] { { "MMM", "MMM", "MMM" }, // Y=0: 3x3 marble base (bottom)
                    { "   ", " ~ ", "   " } // Y=1: Altar in center (top)
                }))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .addElement('~', ofBlock(BlocksAS.blockAltar, AltarType.ATTUNEMENT.getMetadata()))
        .build();

    /**
     * Constellation Altar - Simple structure for testing
     * <p>
     * Two layers:
     * - Y=0 (bottom): 5x5 marble base
     * - Y=1 (top): Altar in center
     */
    public static final IStructureDefinition<Object> ALTAR_CONSTELLATION_SIMPLE = StructureDefinition.builder()
        .addShape(
            "main",
            transpose(
                new String[][] { { "MMMMM", "MMMMM", "MMMMM", "MMMMM", "MMMMM" }, // Y=0: 5x5 marble base (bottom)
                    { "     ", "     ", "  ~  ", "     ", "     " } // Y=1: Altar in center (top)
                }))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .addElement('~', ofBlock(BlocksAS.blockAltar, AltarType.CONSTELLATION_CRAFT.getMetadata()))
        .build();

    /**
     * Trait Altar - Simple structure for testing
     * <p>
     * Two layers:
     * - Y=0 (bottom): 7x7 marble base
     * - Y=1 (top): Altar in center
     */
    public static final IStructureDefinition<Object> ALTAR_TRAIT_SIMPLE = StructureDefinition.builder()
        .addShape(
            "main",
            transpose(
                new String[][] { { "MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM" }, // Y=0:
                                                                                                                  // 7x7
                                                                                                                  // marble
                                                                                                                  // base
                                                                                                                  // (bottom)
                    { "       ", "       ", "       ", "   ~   ", "       ", "       ", "       " } // Y=1: Altar in
                                                                                                    // center (top)
                }))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .addElement('~', ofBlock(BlocksAS.blockAltar, AltarType.TRAIT_CRAFT.getMetadata()))
        .build();

    /**
     * Brilliance Altar - No structure required (same as Discovery)
     */
    public static final IStructureDefinition<Object> ALTAR_BRILLIANCE_SIMPLE = ALTAR_DISCOVERY;

    // ==================== Machine Structures ====================

    /**
     * Starlight Infuser - Simplified
     * <p>
     * Shape:
     * 
     * <pre>
     * Layer 0 (top): I  C
     * Layer 1 (base): MMMM
     *                  MMMM
     * </pre>
     */
    public static final IStructureDefinition<Object> STARLIGHT_INFUSER = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "I  C", "    ", "MM  ", "MM  " } }))
        .addElement('I', ofBlock(BlocksAS.starlightInfuser, 0))
        .addElement('C', ofBlock(BlocksAS.collectorCrystal, 0))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .build();

    /**
     * Collector Relay - Simplified
     * <p>
     * Shape:
     * 
     * <pre>
     * Layer 0: R  C
     * Layer 1: MMMM
     * </pre>
     */
    public static final IStructureDefinition<Object> COLLECTOR_RELAY = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "R  C", "    ", "MM  ", "MM  " } }))
        .addElement('R', ofBlock(BlocksAS.attunementRelay, 0))
        .addElement('C', ofBlock(BlocksAS.collectorCrystal, 0))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .build();

    /**
     * Celestial Gateway - Placeholder
     * TODO: Implement actual structure
     */
    public static final IStructureDefinition<Object> CELESTIAL_GATEWAY = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "~" } }))
        .addElement('~', ofBlock(BlocksAS.celestialGateway, 0))
        .build();

    /**
     * Starlight Fountain - Placeholder
     * TODO: Implement actual structure
     * Note: This block doesn't exist yet, using marble as placeholder
     */
    public static final IStructureDefinition<Object> FOUNTAIN = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "~" } }))
        .addElement('~', ofBlockAnyMeta(BlocksAS.blockMarble))
        .build();

    // ==================== Ritual Structures ====================

    /**
     * Ritual Pedestal - Basic pedestal with lens
     * <p>
     * Shape:
     * 
     * <pre>
     * M
     * M
     * P
     * </pre>
     */
    public static final IStructureDefinition<Object> RITUAL_PEDESTAL = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "M", "M", "P" } }))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .addElement('P', ofBlock(BlocksAS.ritualPedestal, 0))
        .build();

    // ==================== World Gen Structures ====================

    /**
     * Ancient Shrine - Simplified mountain shrine
     * <p>
     * Shape:
     * 
     * <pre>
     *    C
     *   MMM
     *  MPMPM
     *   MMM
     * </pre>
     */
    public static final IStructureDefinition<Object> ANCIENT_SHRINE = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "   C   ", "  MMM  ", " MPMPM ", "  MMM  " } }))
        .addElement('C', ofBlock(BlocksAS.celestialCrystals, 0))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .addElement('P', ofBlock(BlocksAS.blockMarble, BlockMarble.MarbleType.PILLAR.ordinal()))
        .build();

    /**
     * Desert Shrine - Placeholder
     */
    public static final IStructureDefinition<Object> DESERT_SHRINE = ANCIENT_SHRINE;

    /**
     * Small Shrine - Minimal surface shrine
     */
    public static final IStructureDefinition<Object> SMALL_SHRINE = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { " M ", "MPM" } }))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .addElement('P', ofBlock(BlocksAS.blockMarble, BlockMarble.MarbleType.PILLAR.ordinal()))
        .build();

    /**
     * Treasure Shrine - Shrine with chest
     */
    public static final IStructureDefinition<Object> TREASURE_SHRINE = SMALL_SHRINE;

    /**
     * Small Ruin - Ruined structure
     */
    public static final IStructureDefinition<Object> SMALL_RUIN = StructureDefinition.builder()
        .addShape("main", transpose(new String[][] { { "M", "M" } }))
        .addElement('M', ofBlockAnyMeta(BlocksAS.blockMarble))
        .build();

    // ==================== Helper Methods ====================

    /**
     * Transpose a 2D string array (swap rows and columns)
     * <p>
     * StructureLib requires this transformation for proper orientation
     *
     * @param matrix The input matrix
     * @return The transposed matrix
     */
    private static String[][] transpose(String[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return new String[0][0];
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        String[][] result = new String[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }

        return result;
    }

    /**
     * Initialize all structures
     * <p>
     * Called during mod initialization to ensure all blocks are registered
     */
    public static void init() {
        LogHelper.info("MultiblockStructures initialized");
        LogHelper.info("  - Altar structures: 5");
        LogHelper.info("  - Machine structures: 4");
        LogHelper.info("  - Ritual structures: 1");
        LogHelper.info("  - World gen structures: 5");
        LogHelper.info("Total: 15 structure definitions");
    }

    /**
     * Get altar structure by level
     *
     * @param level The altar level (0=DISCOVERY, 1=ATTUNEMENT, 2=CONSTELLATION, 3=TRAIT, 4=BRILLIANCE)
     * @return The structure definition, or null if no structure required
     */
    public static IStructureDefinition<?> getAltarStructure(int level) {
        switch (level) {
            case 0: // DISCOVERY
                return ALTAR_DISCOVERY;
            case 1: // ATTUNEMENT
                return ALTAR_ATTUNEMENT_SIMPLE;
            case 2: // CONSTELLATION_CRAFT
                return ALTAR_CONSTELLATION_SIMPLE;
            case 3: // TRAIT_CRAFT
                return ALTAR_TRAIT_SIMPLE;
            case 4: // BRILLIANCE
                return ALTAR_BRILLIANCE_SIMPLE;
            default:
                return null;
        }
    }
}
