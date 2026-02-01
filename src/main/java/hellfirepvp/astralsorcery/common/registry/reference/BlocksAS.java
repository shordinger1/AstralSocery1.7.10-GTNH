/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Block instances - All block references
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.reference;

import net.minecraft.block.Block;

import hellfirepvp.astralsorcery.common.block.*;
import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.fluid.FluidLiquidStarlight;

/**
 * Block instances for Astral Sorcery (1.7.10)
 * <p>
 * This class holds static references to all blocks in the mod.
 * Blocks are registered in RegistryBlocks.
 */
public class BlocksAS {

    // Ore blocks
    public static BlockCustomOre customOre;
    public static BlockCustomSandOre customSandOre;

    // Decorative blocks
    public static BlockCustomFlower customFlower;
    public static BlockMarble blockMarble;
    public static BlockBlackMarble blockBlackMarble;
    public static BlockInfusedWood blockInfusedWood;
    public static BlockFlareLight blockFlareLight;

    // Marble components
    public static BlockMarbleSlab blockMarbleSlab;
    public static BlockMarbleDoubleSlab blockMarbleDoubleSlab;
    public static BlockMarbleStairs blockMarbleStairs;

    // Special blocks
    public static BlockStructural blockStructural;
    public static BlockFakeTree blockFakeTree;
    public static BlockVanishing blockVanishing;
    public static BlockTranslucentBlock translucentBlock;

    // Functional blocks
    public static BlockAltar blockAltar;
    public static BlockAttunementAltar attunementAltar;
    public static BlockWell blockWell;
    public static BlockWorldIlluminator blockIlluminator;
    public static BlockMapDrawingTable drawingTable;
    public static BlockObservatory blockObservatory;
    public static BlockTelescope blockTelescope;

    // Crafting altar tiers (TODO: implement these blocks)
    public static Block craftingAltarTier1;
    public static Block craftingAltarTier2;
    public static Block craftingAltarTier3;
    public static Block craftingAltarTier4;

    // Starlight network blocks
    public static BlockCollectorCrystal collectorCrystal;
    public static BlockLens lens;
    public static BlockPrism lensPrism;
    public static BlockCelestialCollectorCrystal celestialCollectorCrystal;
    public static BlockAttunementRelay attunementRelay;
    public static BlockCelestialCrystals celestialCrystals;

    // Ritual blocks
    public static BlockRitualPedestal ritualPedestal;
    public static BlockRitualLink ritualLink;
    public static BlockTreeBeacon treeBeacon;

    // Advanced blocks
    public static BlockStarlightInfuser starlightInfuser;
    public static BlockCelestialOrrery celestialOrrery;

    // Bore blocks
    public static BlockBore blockBore;
    public static BlockBoreHead blockBoreHead;

    // Gem crystals
    public static BlockGemCrystals gemCrystals;

    // Chalice
    public static BlockChalice blockChalice;

    // Celestial Gateway blocks
    public static BlockCelestialGateway celestialGateway;
    public static BlockPortalNode portalNode;

    // Machine blocks
    public static BlockMachine blockMachine;

    public static FluidBlockLiquidStarlight blockLiquidStarlight;
    public static FluidLiquidStarlight fluidLiquidStarlight;

    // NOTE: Additional blocks from original version (not yet implemented)
    // public static BlockCelestialCollectorCrystal celestialCollectorCrystal;
    // public static BlockAttunementRelay attunementRelay;
    // public static BlockCelestialCrystals celestialCrystals;
    // public static FluidBlockLiquidStarlight blockLiquidStarlight;
    // public static Fluid fluidLiquidStarlight;
}
