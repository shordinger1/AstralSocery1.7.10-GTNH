/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Structure Checker - Check multiblock structures
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Structure Checker - Multiblock structure validation (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Check altar structures at different levels</li>
 * <li>Check machine structures (infuser, relay, gateway, etc.)</li>
 * <li>Check ritual structures (pedestal, etc.)</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * 
 * boolean formed = StructureChecker.checkAltar(world, x, y, z, AltarLevel.ATTUNEMENT);
 * </pre>
 */
public class StructureChecker {

    /**
     * Check altar structure by level
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param level Altar level (0=DISCOVERY, 1=ATTUNEMENT, 2=CONSTELLATION, 3=TRAIT, 4=BRILLIANCE)
     * @return true if structure is complete
     */
    public static boolean checkAltarStructure(World world, int x, int y, int z, int level) {
        IStructureDefinition<?> def = MultiblockStructures.getAltarStructure(level);
        if (def == null) {
            return true; // DISCOVERY level has no structure requirement
        }

        return checkStructure(def, world, x, y, z);
    }

    /**
     * Check starlight infuser structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if structure is complete
     */
    public static boolean checkInfuserStructure(World world, int x, int y, int z) {
        return checkStructure(MultiblockStructures.STARLIGHT_INFUSER, world, x, y, z);
    }

    /**
     * Check collector relay structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if structure is complete
     */
    public static boolean checkRelayStructure(World world, int x, int y, int z) {
        return checkStructure(MultiblockStructures.COLLECTOR_RELAY, world, x, y, z);
    }

    /**
     * Check celestial gateway structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if structure is complete
     */
    public static boolean checkGatewayStructure(World world, int x, int y, int z) {
        return checkStructure(MultiblockStructures.CELESTIAL_GATEWAY, world, x, y, z);
    }

    /**
     * Check ritual pedestal structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if structure is complete
     */
    public static boolean checkPedestalStructure(World world, int x, int y, int z) {
        return checkStructure(MultiblockStructures.RITUAL_PEDESTAL, world, x, y, z);
    }

    /**
     * Generic structure check method
     *
     * @param definition The structure definition
     * @param world      The world
     * @param x          X coordinate
     * @param y          Y coordinate
     * @param z          Z coordinate
     * @return true if structure is complete
     */
    private static boolean checkStructure(IStructureDefinition<?> definition, World world, int x, int y, int z) {
        try {
            @SuppressWarnings("unchecked")
            IStructureDefinition<Object> def = (IStructureDefinition<Object>) definition;
            boolean formed = def.check(
                null, // context (not used for Object-based structures)
                "main", // piece name
                world,
                ExtendedFacing.SOUTH_NORMAL_NONE,
                x,
                y,
                z,
                0,
                0,
                0, // offsets
                false // forceCheckAll
            );
            return formed;
        } catch (Exception e) {
            LogHelper.warn("Failed to check structure at " + x + "," + y + "," + z + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Check ancient shrine structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if structure is complete
     */
    public static boolean checkAncientShrine(World world, int x, int y, int z) {
        return checkStructure(MultiblockStructures.ANCIENT_SHRINE, world, x, y, z);
    }

    /**
     * Check small shrine structure
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if structure is complete
     */
    public static boolean checkSmallShrine(World world, int x, int y, int z) {
        return checkStructure(MultiblockStructures.SMALL_SHRINE, world, x, y, z);
    }
}
