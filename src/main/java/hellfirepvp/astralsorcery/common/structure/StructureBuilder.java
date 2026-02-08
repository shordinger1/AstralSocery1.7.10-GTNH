/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Structure Builder - Build multiblock structures
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Structure Builder - Multiblock structure construction (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Build altar structures at different levels</li>
 * <li>Build machine structures (infuser, relay, gateway, etc.)</li>
 * <li>Build world generation structures (shrines, etc.)</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * StructureBuilder.buildAltar(world, x, y, z, AltarLevel.ATTUNEMENT);
 * </pre>
 * <p>
 * <b>Used by:</b>
 * <ul>
 * <li>/as build command</li>
 * <li>World generation</li>
 * <li>Debug/testing tools</li>
 * </ul>
 */
public class StructureBuilder {

    /**
     * Build altar structure by level
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param level Altar level (0=DISCOVERY, 1=ATTUNEMENT, 2=CONSTELLATION, 3=TRAIT, 4=BRILLIANCE)
     */
    public static void buildAltar(World world, int x, int y, int z, int level) {
        IStructureDefinition<?> def = MultiblockStructures.getAltarStructure(level);
        if (def == null) {
            LogHelper.debug("No structure to build for altar level " + level);
            return;
        }

        buildStructure(def, world, x, y, z, "Altar level " + level);
    }

    /**
     * Build starlight infuser structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildInfuser(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.STARLIGHT_INFUSER, world, x, y, z, "Starlight Infuser");
    }

    /**
     * Build collector relay structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildRelay(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.COLLECTOR_RELAY, world, x, y, z, "Collector Relay");
    }

    /**
     * Build celestial gateway structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildGateway(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.CELESTIAL_GATEWAY, world, x, y, z, "Celestial Gateway");
    }

    /**
     * Build ritual pedestal structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildPedestal(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.RITUAL_PEDESTAL, world, x, y, z, "Ritual Pedestal");
    }

    /**
     * Build ancient shrine structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildAncientShrine(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.ANCIENT_SHRINE, world, x, y, z, "Ancient Shrine");
    }

    /**
     * Build small shrine structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildSmallShrine(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.SMALL_SHRINE, world, x, y, z, "Small Shrine");
    }

    /**
     * Build treasure shrine structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildTreasureShrine(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.TREASURE_SHRINE, world, x, y, z, "Treasure Shrine");
    }

    /**
     * Build small ruin structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void buildSmallRuin(World world, int x, int y, int z) {
        buildStructure(MultiblockStructures.SMALL_RUIN, world, x, y, z, "Small Ruin");
    }

    /**
     * Generic structure build method
     *
     * @param definition    The structure definition
     * @param world         The world
     * @param x             X coordinate
     * @param y             Y coordinate
     * @param z             Z coordinate
     * @param structureName Name for logging
     */
    private static void buildStructure(IStructureDefinition<?> definition, World world, int x, int y, int z,
        String structureName) {
        try {
            @SuppressWarnings("unchecked")
            IStructureDefinition<Object> def = (IStructureDefinition<Object>) definition;

            // Correct parameter order: object, trigger, piece, world, facing, x, y, z, a, b, c
            def.build(
                null, // object (context - not used)
                null, // trigger (ItemStack - not needed)
                structureName, // piece (structure name)
                world,
                ExtendedFacing.SOUTH_NORMAL_NONE,
                x,
                y,
                z, // base position
                0,
                0,
                0 // A, B, C offsets
            );

            LogHelper.info("Built " + structureName + " at " + x + "," + y + "," + z);
        } catch (Exception e) {
            LogHelper
                .error("Failed to build " + structureName + " at " + x + "," + y + "," + z + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build structure by name (for command usage)
     *
     * @param world         The world
     * @param x             X coordinate
     * @param y             Y coordinate
     * @param z             Z coordinate
     * @param structureName Structure name (case-insensitive)
     * @return true if structure was built successfully
     */
    public static boolean buildByName(World world, int x, int y, int z, String structureName) {
        if (structureName == null || structureName.isEmpty()) {
            return false;
        }

        String name = structureName.toLowerCase()
            .replace(" ", "")
            .replace("_", "")
            .replace("-", "");

        // Match structure name to definition
        switch (name) {
            case "altarattunement":
            case "attunementaltar":
                buildAltar(world, x, y, z, 1);
                return true;

            case "altarconstellation":
            case "constellationaltar":
                buildAltar(world, x, y, z, 2);
                return true;

            case "altartrait":
            case "traitaltar":
                buildAltar(world, x, y, z, 3);
                return true;

            case "altarbrilliance":
            case "brilliancealtar":
                buildAltar(world, x, y, z, 4);
                return true;

            case "starlightinfuser":
            case "infuser":
                buildInfuser(world, x, y, z);
                return true;

            case "collectorrelay":
            case "relay":
                buildRelay(world, x, y, z);
                return true;

            case "celestialgateway":
            case "gateway":
                buildGateway(world, x, y, z);
                return true;

            case "ritualpedestal":
            case "pedestal":
                buildPedestal(world, x, y, z);
                return true;

            case "ancientshrine":
            case "shrine":
                buildAncientShrine(world, x, y, z);
                return true;

            case "smallshrine":
                buildSmallShrine(world, x, y, z);
                return true;

            case "treasureshrine":
                buildTreasureShrine(world, x, y, z);
                return true;

            case "smallruin":
            case "ruin":
                buildSmallRuin(world, x, y, z);
                return true;

            default:
                LogHelper.warn("Unknown structure name: " + structureName);
                return false;
        }
    }
}
