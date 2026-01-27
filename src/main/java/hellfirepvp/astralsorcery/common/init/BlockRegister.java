/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.init;

import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.GameRegistry;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.block.*;
import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.network.*;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationGeolosys;
import hellfirepvp.astralsorcery.common.migration.MappingMigrationHandler;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;

/**
 * Simplified block registration following TSS-style pattern.
 * Direct GameRegistry calls instead of Primer system.
 */
public class BlockRegister {

    public static List<Block> defaultItemBlocksToRegister = new LinkedList<>();
    public static List<Block> customNameItemBlocksToRegister = new LinkedList<>();
    public static List<BlockDynamicColor> pendingIBlockColorBlocks = new LinkedList<>();

    /**
     * Main registry method - call from CommonProxy.preInit()
     */
    public static void registry() {
        registerFluids();
        registerBlocks();
        registerTileEntities();

        if (Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleBlock();
        }
    }

    private static void registerFluids() {
        FluidLiquidStarlight f = new FluidLiquidStarlight();
        FluidRegistry.registerFluid(f);
        fluidLiquidStarlight = FluidRegistry.getFluid(f.getName());
        blockLiquidStarlight = new FluidBlockLiquidStarlight();
        String fluidName = blockLiquidStarlight.getClass()
            .getSimpleName()
            .toLowerCase();
        // 1.7.10: setUnlocalizedName via registerBlock, fluid blocks don't have setUnlocalizedName
        GameRegistry.registerBlock(blockLiquidStarlight, fluidName);
        fluidLiquidStarlight.setBlock(blockLiquidStarlight);
        // 1.7.10: addBucketForFluid doesn't exist, buckets handled separately if needed
    }

    private static void registerBlocks() {
        // WorldGen & Related
        customOre = registerBlock(new BlockCustomOre());
        queueCustomNameItemBlock(customOre);
        customSandOre = registerBlock(new BlockCustomSandOre());
        queueCustomNameItemBlock(customSandOre);
        customFlower = registerBlock(new BlockCustomFlower());
        queueCustomNameItemBlock(customFlower);
        blockMarble = registerBlock(new BlockMarble());
        queueCustomNameItemBlock(blockMarble);
        blockMarbleStairs = registerBlock(new BlockMarbleStairs());
        queueDefaultItemBlock(blockMarbleStairs);
        blockMarbleSlab = registerBlock(new BlockMarbleSlab());
        blockMarbleDoubleSlab = registerBlock(new BlockMarbleDoubleSlab());
        blockBlackMarble = registerBlock(new BlockBlackMarble());
        queueCustomNameItemBlock(blockBlackMarble);
        blockInfusedWood = registerBlock(new BlockInfusedWood());
        queueCustomNameItemBlock(blockInfusedWood);
        blockVolatileLight = registerBlock(new BlockFlareLight());
        queueDefaultItemBlock(blockVolatileLight);
        blockVanishing = registerBlock(new BlockVanishing());
        queueDefaultItemBlock(blockVanishing);
        blockChalice = registerBlock(new BlockChalice());
        queueDefaultItemBlock(blockChalice);
        blockBore = registerBlock(new BlockBore());
        queueDefaultItemBlock(blockBore);
        blockBoreHead = registerBlock(new BlockBoreHead());
        queueCustomNameItemBlock(blockBoreHead);

        // Mechanics
        blockAltar = registerBlock(new BlockAltar());
        attunementAltar = registerBlock(new BlockAttunementAltar());
        queueDefaultItemBlock(attunementAltar);
        attunementRelay = registerBlock(new BlockAttunementRelay());
        queueDefaultItemBlock(attunementRelay);
        ritualPedestal = registerBlock(new BlockRitualPedestal());
        blockWell = registerBlock(new BlockWell());
        queueDefaultItemBlock(blockWell);
        blockIlluminator = registerBlock(new BlockWorldIlluminator());
        queueDefaultItemBlock(blockIlluminator);
        blockMachine = registerBlock(new BlockMachine());
        queueCustomNameItemBlock(blockMachine);
        blockFakeTree = registerBlock(new BlockFakeTree());
        queueDefaultItemBlock(blockFakeTree);
        starlightInfuser = registerBlock(new BlockStarlightInfuser());
        queueDefaultItemBlock(starlightInfuser);
        ritualLink = registerBlock(new BlockRitualLink());
        queueDefaultItemBlock(ritualLink);
        blockPortalNode = registerBlock(new BlockPortalNode());
        queueDefaultItemBlock(blockPortalNode);

        treeBeacon = registerBlock(new BlockTreeBeacon());
        queueDefaultItemBlock(treeBeacon);
        translucentBlock = registerBlock(new BlockTranslucentBlock());
        queueDefaultItemBlock(translucentBlock);
        drawingTable = registerBlock(new BlockMapDrawingTable());
        queueDefaultItemBlock(drawingTable);
        celestialGateway = registerBlock(new BlockCelestialGateway());
        queueDefaultItemBlock(celestialGateway);
        blockObservatory = registerBlock(new BlockObservatory());
        queueDefaultItemBlock(blockObservatory);

        lens = registerBlock(new BlockLens());
        lensPrism = registerBlock(new BlockPrism());
        queueDefaultItemBlock(lens);
        queueDefaultItemBlock(lensPrism);

        celestialCrystals = registerBlock(new BlockCelestialCrystals());
        queueCustomNameItemBlock(celestialCrystals);
        gemCrystals = registerBlock(new BlockGemCrystals());
        queueCustomNameItemBlock(gemCrystals);

        // Machines & Related
        collectorCrystal = registerBlock(new BlockCollectorCrystal());
        celestialCollectorCrystal = registerBlock(new BlockCelestialCollectorCrystal());

        blockStructural = registerBlock(new BlockStructural());
        queueCustomNameItemBlock(blockStructural);
    }

    private static void registerTileEntities() {
        registerTile(TileAltar.class);
        registerTile(TileRitualPedestal.class);
        registerTile(TileCollectorCrystal.class);
        registerTile(TileCelestialCrystals.class);
        registerTile(TileGemCrystals.class);
        registerTile(TileWell.class);
        registerTile(TileIlluminator.class);
        registerTile(TileTelescope.class);
        registerTile(TileGrindstone.class);
        registerTile(TileStructuralConnector.class);
        registerTile(TileFakeTree.class);
        registerTile(TileAttunementAltar.class);
        registerTile(TileStarlightInfuser.class);
        registerTile(TileTreeBeacon.class);
        registerTile(TileRitualLink.class);
        registerTile(TileTranslucent.class);
        registerTile(TileAttunementRelay.class);
        registerTile(TileMapDrawingTable.class);
        registerTile(TileCelestialGateway.class);
        registerTile(TileOreGenerator.class);
        registerTile(TileVanishing.class);
        registerTile(TileChalice.class);
        registerTile(TileBore.class);
        registerTile(TileStructController.class);
        registerTile(TileObservatory.class);

        registerTile(TileCrystalLens.class);
        registerTile(TileCrystalPrismLens.class);
    }

    private static <T extends Block> T registerBlock(T block, String name) {
        // 1.7.10: setUnlocalizedName via registerBlock, not on block directly
        GameRegistry.registerBlock(block, name);
        if (block instanceof BlockDynamicColor) {
            pendingIBlockColorBlocks.add((BlockDynamicColor) block);
        }
        return block;
    }

    private static <T extends Block> T registerBlock(T block) {
        return registerBlock(
            block,
            block.getClass()
                .getSimpleName()
                .toLowerCase());
    }

    private static void registerTile(Class<? extends net.minecraft.tileentity.TileEntity> tile, String name) {
        GameRegistry.registerTileEntity(tile, AstralSorcery.MODID + ":" + name);
        MappingMigrationHandler.listenTileMigration(name);
    }

    private static void registerTile(Class<? extends net.minecraft.tileentity.TileEntity> tile) {
        registerTile(
            tile,
            tile.getSimpleName()
                .toLowerCase());
    }

    public static void queueCustomNameItemBlock(Block block) {
        customNameItemBlocksToRegister.add(block);
    }

    public static void queueDefaultItemBlock(Block block) {
        defaultItemBlocksToRegister.add(block);
    }

    /**
     * Called after items are registered.
     * Necessary for blocks that require different models/renders for different metadata values
     */
    public static void initRenderRegistry() {
        registerBlockRender(blockMarble);
        registerBlockRender(blockBlackMarble);
        registerBlockRender(blockInfusedWood);
        registerBlockRender(blockAltar);
        registerBlockRender(blockBoreHead);
        registerBlockRender(customOre);
        registerBlockRender(customSandOre);
        registerBlockRender(customFlower);
        registerBlockRender(blockStructural);
        registerBlockRender(blockMachine);
        registerBlockRender(treeBeacon);

        registerBlockRender(celestialCrystals);
        registerBlockRender(gemCrystals);
    }

    private static void registerBlockRender(Block block) {
        if (block instanceof BlockVariants) {
            BlockVariants variants = (BlockVariants) block;
            String blockName = variants.getBlockName();
            List<Block> validStates = variants.getValidStates();
            for (int meta = 0; meta < validStates.size(); meta++) {
                String stateName = variants.getStateName(meta);
                String name = blockName + "_" + stateName;
                AstralSorcery.proxy.registerVariantName(net.minecraft.item.Item.getItemFromBlock(block), name);
                AstralSorcery.proxy.registerBlockRender(block, meta, name);
            }
        } else {
            AstralSorcery.proxy
                .registerVariantName(net.minecraft.item.Item.getItemFromBlock(block), block.getUnlocalizedName());
            AstralSorcery.proxy.registerBlockRender(block, 0, block.getUnlocalizedName());
        }
    }

}
