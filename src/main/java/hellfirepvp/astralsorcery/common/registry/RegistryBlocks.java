/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Block registration handler - All blocks registered here
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.block.*;
import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.itemblock.*;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.util.ResourceChecker;

/**
 * Block registry for Astral Sorcery (1.7.10)
 * <p>
 * Handles registration of all blocks in the mod.
 */
public class RegistryBlocks {

    private static final List<Block> BLOCKS_TO_REGISTER = Lists.newArrayList();

    /**
     * Register fluids
     * IMPORTANT: This must be called BEFORE registering fluid blocks
     */
    private static void registerFluids() {
        LogHelper.info("=== Registering Astral Sorcery Fluids ===");

        // Create and register liquid starlight fluid
        BlocksAS.fluidLiquidStarlight = new FluidLiquidStarlight();
        boolean registered = FluidRegistry.registerFluid(BlocksAS.fluidLiquidStarlight);

        if (registered) {
            LogHelper.info("Registered fluid: " + BlocksAS.fluidLiquidStarlight.getName());
        } else {
            LogHelper.warn(
                "Fluid already registered: " + BlocksAS.fluidLiquidStarlight.getName()
                    + " (may be registered by another mod)");
        }

        LogHelper.info("=== Fluid Registration Complete ===");
    }

    /**
     * Pre-initialization: register all blocks
     */
    public static void preInit() {

        // === Ore blocks ===
        BlocksAS.customOre = (BlockCustomOre) registerBlock(
            new BlockCustomOre(),
            ItemBlockCustomOre.class,
            "blockcustomore");
        BlocksAS.customSandOre = (BlockCustomSandOre) registerBlock(
            new BlockCustomSandOre(),
            ItemBlockCustomSandOre.class,
            "blockcustomsandore");

        // === Decorative blocks ===
        BlocksAS.customFlower = (BlockCustomFlower) registerBlock(
            new BlockCustomFlower(),
            ItemBlockCustomFlower.class,
            "blockcustomflower");
        BlocksAS.blockMarble = (BlockMarble) registerBlock(new BlockMarble(), ItemBlockMarble.class, "blockmarble");
        BlocksAS.blockBlackMarble = (BlockBlackMarble) registerBlock(
            new BlockBlackMarble(),
            ItemBlockBlackMarble.class,
            "blockblackmarble");
        BlocksAS.blockInfusedWood = (BlockInfusedWood) registerBlock(
            new BlockInfusedWood(),
            ItemBlockInfusedWood.class,
            "blockinfusedwood");
        BlocksAS.blockFlareLight = (BlockFlareLight) registerBlock(
            new BlockFlareLight(),
            ItemBlockFlareLight.class,
            "blockflarelight");

        // === Marble components ===
        BlocksAS.blockMarbleSlab = (BlockMarbleSlab) registerBlock(
            new BlockMarbleSlab(),
            ItemBlockMarbleSlab.class,
            "blockmarbleslab");
        BlocksAS.blockMarbleDoubleSlab = (BlockMarbleDoubleSlab) registerBlock(
            new BlockMarbleDoubleSlab(),
            ItemBlockMarbleDoubleSlab.class,
            "blockmarbledoubleslab");
        BlocksAS.blockMarbleStairs = (BlockMarbleStairs) registerBlock(
            new BlockMarbleStairs(),
            ItemBlockMarbleStairs.class,
            "blockmarblestairs");

        // === Special blocks ===
        BlocksAS.blockStructural = (BlockStructural) registerBlockWithoutTab(
            new BlockStructural(),
            ItemBlockStructural.class,
            "blockstructural");
        BlocksAS.blockFakeTree = (BlockFakeTree) registerBlockWithoutTab(
            new BlockFakeTree(),
            ItemBlockFakeTree.class,
            "blockfaketree");
        BlocksAS.blockVanishing = (BlockVanishing) registerBlockWithoutTab(
            new BlockVanishing(),
            ItemBlockVanishing.class,
            "blockvanishing");
        BlocksAS.translucentBlock = (BlockTranslucentBlock) registerBlockWithoutTab(
            new BlockTranslucentBlock(),
            ItemBlockTranslucentBlock.class,
            "translucentblock");

        // === Functional blocks ===
        BlocksAS.blockAltar = (BlockAltar) registerBlock(new BlockAltar(), ItemBlockAltar.class, "blockaltar");
        BlocksAS.attunementAltar = (BlockAttunementAltar) registerBlock(
            new BlockAttunementAltar(),
            ItemBlockAttunementAltar.class,
            "blockattunementaltar");
        BlocksAS.blockWell = (BlockWell) registerBlock(new BlockWell(), ItemBlockWell.class, "blockwell");
        BlocksAS.blockIlluminator = (BlockWorldIlluminator) registerBlock(
            new BlockWorldIlluminator(),
            ItemBlockIlluminator.class,
            "blockworldilluminator");
        BlocksAS.drawingTable = (BlockMapDrawingTable) registerBlock(
            new BlockMapDrawingTable(),
            ItemBlockMapDrawingTable.class,
            "blockmapdrawingtable");
        BlocksAS.blockObservatory = (BlockObservatory) registerBlock(
            new BlockObservatory(),
            ItemBlockObservatory.class,
            "blockobservatory");
        BlocksAS.blockTelescope = (BlockTelescope) registerBlock(
            new BlockTelescope(),
            ItemBlock.class,
            "blocktelescope");

        // === Fluid blocks ===
        // IMPORTANT: Register fluid BEFORE creating the fluid block
        registerFluids();
        BlocksAS.blockLiquidStarlight = (FluidBlockLiquidStarlight) registerBlock(
            new FluidBlockLiquidStarlight(BlocksAS.fluidLiquidStarlight),
            ItemBlock.class,
            "blockliquidstarlight");

        // === Starlight network blocks ===
        BlocksAS.collectorCrystal = (BlockCollectorCrystal) registerBlock(
            new BlockCollectorCrystal(),
            ItemBlockCollectorCrystal.class,
            "blockcollectorcrystal");
        BlocksAS.lens = (BlockLens) registerBlock(new BlockLens(), ItemBlockLens.class, "blocklens");
        BlocksAS.lensPrism = (BlockPrism) registerBlock(new BlockPrism(), ItemBlockPrism.class, "blockprism");
        BlocksAS.celestialCollectorCrystal = (BlockCelestialCollectorCrystal) registerBlock(
            new BlockCelestialCollectorCrystal(),
            ItemBlockCelestialCollectorCrystal.class,
            "blockcelestialcollectorcrystal");
        BlocksAS.attunementRelay = (BlockAttunementRelay) registerBlock(
            new BlockAttunementRelay(),
            ItemBlockAttunementRelay.class,
            "blockattunementrelay");
        BlocksAS.celestialCrystals = (BlockCelestialCrystals) registerBlock(
            new BlockCelestialCrystals(),
            ItemBlockCelestialCrystals.class,
            "blockcelestialcrystals");

        // === Ritual blocks ===
        BlocksAS.ritualPedestal = (BlockRitualPedestal) registerBlock(
            new BlockRitualPedestal(),
            ItemBlockRitualPedestal.class,
            "blockritualpedestal");
        BlocksAS.ritualLink = (BlockRitualLink) registerBlockWithoutTab(
            new BlockRitualLink(),
            ItemBlockRitualLink.class,
            "blockrituallink");
        BlocksAS.treeBeacon = (BlockTreeBeacon) registerBlockWithoutTab(
            new BlockTreeBeacon(),
            ItemBlockTreeBeacon.class,
            "blocktreebeacon");

        // === Advanced blocks ===
        BlocksAS.starlightInfuser = (BlockStarlightInfuser) registerBlock(
            new BlockStarlightInfuser(),
            ItemBlockStarlightInfuser.class,
            "blockstarlightinfuser");
        BlocksAS.celestialOrrery = (BlockCelestialOrrery) registerBlock(
            new BlockCelestialOrrery(),
            ItemBlockCelestialOrrery.class,
            "blockcelestialorrery");

        // === Bore blocks ===
        BlocksAS.blockBore = (BlockBore) registerBlock(new BlockBore(), ItemBlockBore.class, "blockbore");
        BlocksAS.blockBoreHead = (BlockBoreHead) registerBlock(
            new BlockBoreHead(),
            ItemBlockBoreHead.class,
            "blockborehead");

        // === Gem crystals ===
        BlocksAS.gemCrystals = (BlockGemCrystals) registerBlock(
            new BlockGemCrystals(),
            ItemBlockGemCrystals.class,
            "blockgemcrystals");

        // === Chalice ===
        BlocksAS.blockChalice = (BlockChalice) registerBlock(
            new BlockChalice(),
            ItemBlockChalice.class,
            "blockchalice");

        // === Celestial Gateway Blocks ===
        BlocksAS.celestialGateway = (BlockCelestialGateway) registerBlock(
            new BlockCelestialGateway(),
            ItemBlockCelestialGateway.class,
            "blockcelestialgateway");
        BlocksAS.portalNode = (BlockPortalNode) registerBlockWithoutTab(
            new BlockPortalNode(),
            ItemBlockPortalNode.class,
            "blockportalnode");

        // === Machine Blocks ===
        BlocksAS.blockMachine = (BlockMachine) registerBlock(
            new BlockMachine(),
            ItemBlockMachine.class,
            "blockmachine");

        // Log registered blocks
        LogHelper.info("Registered " + BLOCKS_TO_REGISTER.size() + " blocks");
    }

    /**
     * Register a block with specific ItemBlock class
     * Uses correct 1.7.10 GameRegistry API
     */
    private static Block registerBlock(Block block, Class<? extends ItemBlock> itemClass, String name) {
        if (block == null) {
            throw new IllegalArgumentException("Attempted to register null block!");
        }

        // Register block with custom ItemBlock using GameRegistry (1.7.10)
        // Single call handles both Block and ItemBlock registration
        GameRegistry.registerBlock(block, itemClass, name);

        // IMPORTANT: Set blockName AFTER registration to override any auto-added prefix
        // In 1.7.10, GameRegistry.registerBlock may modify the unlocalizedName
        // Minecraft will automatically add "tile." prefix, so we don't include it here
        // unlocalizedName: "blockcustomore" -> lang key: "tile.blockcustomore.name"
        block.setBlockName(name);

        // Track for later
        BLOCKS_TO_REGISTER.add(block);

        return block;
    }

    /**
     * Register a block without creative tab
     */
    private static Block registerBlockWithoutTab(Block block, Class<? extends ItemBlock> itemClass, String name) {
        return registerBlock(block, itemClass, name);
    }

    /**
     * Get all registered blocks
     */
    public static List<Block> getRegisteredBlocks() {
        return Lists.newArrayList(BLOCKS_TO_REGISTER);
    }

    /**
     * Initialize blocks after registration
     */
    public static void init() {
        // Check block icons (client side only)
        checkBlockIcons();
    }

    /**
     * Check all registered blocks for icons and localization
     * This is a debug utility to verify texture registration and translations
     */
    @SideOnly(Side.CLIENT)
    private static void checkBlockIcons() {
        LogHelper.info("=== Checking Block Resources ===");
        LogHelper.info("Total blocks registered: " + BLOCKS_TO_REGISTER.size());

        int issuesCount = 0;
        int okCount = 0;

        for (Block block : BLOCKS_TO_REGISTER) {
            String blockName = block.getUnlocalizedName();
            ResourceChecker.CheckResult result = ResourceChecker.checkBlock(block, blockName);

            if (result.hasIssues()) {
                LogHelper.warn("[BLOCK ISSUE] " + result.format());
                issuesCount++;
            } else {
                LogHelper.info("[BLOCK OK] " + result.format());
                okCount++;
            }
        }

        LogHelper.info("=== Block Resource Check Complete ===");
        LogHelper.info("OK: " + okCount);
        LogHelper.info("Issues: " + issuesCount);

        if (issuesCount > 0) {
            LogHelper.warn("WARNING: " + issuesCount + " blocks have resource issues!");
        } else {
            LogHelper.info("All blocks are OK!");
        }
    }
}
