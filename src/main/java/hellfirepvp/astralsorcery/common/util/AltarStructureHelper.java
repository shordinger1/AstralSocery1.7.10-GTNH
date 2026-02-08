package hellfirepvp.astralsorcery.common.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import hellfirepvp.astralsorcery.common.structure.MultiblockStructures;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * Helper class for validating altar multiblock structures (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Validate multiblock structures around altars</li>
 * <li>Uses StructureLib for pattern matching</li>
 * <li>Supports all altar levels and their structures</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 *
 * <pre>
 * 
 * // Check if altar has complete structure
 * boolean complete = AltarStructureHelper.isStructureComplete(world, x, y, z, altarLevel);
 *
 * // Get structure for altar level
 * IStructureDefinition<?> structure = AltarStructureHelper.getStructure(altarLevel);
 * </pre>
 */
public class AltarStructureHelper {

    /**
     * Check if the altar at the given position has a complete multiblock structure
     *
     * @param world The world
     * @param x     X coordinate of altar
     * @param y     Y coordinate of altar
     * @param z     Z coordinate of altar
     * @param level The altar level
     * @return true if the structure is complete or no structure is required
     */
    public static boolean isStructureComplete(World world, int x, int y, int z, TileAltar.AltarLevel level) {
        if (world == null || level == null) {
            return false;
        }

        // Discovery and Brilliance don't require structures
        if (level == TileAltar.AltarLevel.DISCOVERY || level == TileAltar.AltarLevel.BRILLIANCE) {
            return true;
        }

        IStructureDefinition<?> structure = getStructure(level);
        if (structure == null) {
            // No structure defined for this level
            return true;
        }

        return validateStructure(world, x, y, z, structure);
    }

    /**
     * Validate the structure around an altar
     *
     * @param world     The world
     * @param x         X coordinate of altar
     * @param y         Y coordinate of altar
     * @param z         Z coordinate of altar
     * @param structure The structure definition to validate
     * @return true if the structure is complete
     */
    private static boolean validateStructure(World world, int x, int y, int z, IStructureDefinition<?> structure) {
        if (structure == null) {
            LogHelper.info("[AltarStructure] Structure is null, returning true (no structure required)");
            return true;
        }

        LogHelper.info("[AltarStructure] Starting structure validation at [%d, %d, %d]", x, y, z);

        // Try all 4 rotations using ExtendedFacing
        ExtendedFacing[] facings = ExtendedFacing.values();
        int facingIndex = 0;
        for (ExtendedFacing facing : facings) {
            try {
                LogHelper.debug("[AltarStructure] Trying facing %d/%d: %s", facingIndex + 1, facings.length, facing);

                @SuppressWarnings("unchecked")
                IStructureDefinition<Object> def = (IStructureDefinition<Object>) structure;
                if (def.check(
                    null, // context
                    "main", // piece name
                    world, // world
                    facing, // facing
                    x,
                    y,
                    z, // position
                    0,
                    0,
                    0, // offsets
                    false // forceCheckAll
                )) {
                    // Structure validated successfully!
                    LogHelper.info(
                        "[AltarStructure] ✓ Structure validated at [%d, %d, %d] with facing: %s",
                        x,
                        y,
                        z,
                        facing);
                    return true;
                }
            } catch (Exception e) {
                LogHelper
                    .warn("[AltarStructure] Exception during validation with facing %s: %s", facing, e.getMessage());
                LogHelper.warn("[AltarStructure] Exception stack trace:", e);
                // Continue to next facing
            }
            facingIndex++;
        }

        // Structure validation failed - log as info since this is important
        LogHelper.info("[AltarStructure] ✗ Structure validation failed at [%d, %d, %d]", x, y, z);
        return false;
    }

    /**
     * Get the structure definition for an altar level
     *
     * @param level The altar level
     * @return The structure definition, or null if no structure is required
     */
    public static IStructureDefinition<?> getStructure(TileAltar.AltarLevel level) {
        if (level == null) {
            return null;
        }

        return MultiblockStructures.getAltarStructure(level.ordinal());
    }

    /**
     * Check if structure is complete from TileEntity
     *
     * @param te    The altar TileEntity
     * @param level The altar level
     * @return true if structure is complete or not required
     */
    public static boolean isStructureComplete(TileEntity te, TileAltar.AltarLevel level) {
        if (te == null || te.getWorldObj() == null) {
            return false;
        }

        return isStructureComplete(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, level);
    }

    /**
     * Check if the altar can craft based on structure completion
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param level The altar level
     * @return true if the altar can craft (structure is complete)
     */
    public static boolean canCraft(World world, int x, int y, int z, TileAltar.AltarLevel level) {
        boolean structureComplete = isStructureComplete(world, x, y, z, level);

        if (!structureComplete) {
            LogHelper.debug(
                "[AltarStructure] Cannot craft at %d, %d, %d: structure incomplete for level %s",
                x,
                y,
                z,
                level);
        }

        return structureComplete;
    }

    /**
     * Check if the altar can craft based on structure completion (TileEntity version)
     *
     * @param te    The altar TileEntity
     * @param level The altar level
     * @return true if the altar can craft
     */
    public static boolean canCraft(TileEntity te, TileAltar.AltarLevel level) {
        if (te == null || te.getWorldObj() == null) {
            return false;
        }

        return canCraft(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, level);
    }
}
