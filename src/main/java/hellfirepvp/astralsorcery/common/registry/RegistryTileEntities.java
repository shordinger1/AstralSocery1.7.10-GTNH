/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileEntity registration handler
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.TileBore;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.tile.TileCelestialOrrery;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.TileCrystalLens;
import hellfirepvp.astralsorcery.common.tile.TileCrystalPrismLens;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.tile.TileGemCrystals;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.tile.TileStructuralConnector;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.tile.TileStructController;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeacon;
import hellfirepvp.astralsorcery.common.tile.TileTranslucent;
import hellfirepvp.astralsorcery.common.tile.TileVanishing;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.tile.TileWorldIlluminator;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileEntity registry for Astral Sorcery
 *
 * Handles registration of all TileEntities in the mod.
 *
 * IMPORTANT: All new TileEntities should extend {@link hellfirepvp.astralsorcery.common.base.AstralBaseTileEntity}
 * rather than extending {@link net.minecraft.tileentity.TileEntity} directly.
 */
public class RegistryTileEntities {

    private static final List<Class<? extends TileEntity>> TILES_TO_REGISTER = Lists.newArrayList();

    /**
     * Pre-initialization: register all TileEntities
     */
    public static void preInit() {
        LogHelper.entry("RegistryTileEntities.preInit");

        // Register TileEntities here
        // registerTileEntity(TileOwned.class, "TileOwned");
        registerTileEntity(TileTranslucent.class, "TileTranslucent");

        // Register migrated TileEntities - Batch 1 (Simple TileEntities)
        registerTileEntity(TileVanishing.class, "TileVanishing");
        registerTileEntity(TileGrindstone.class, "TileGrindstone");
        registerTileEntity(TileChalice.class, "TileChalice");
        registerTileEntity(TileAltar.class, "TileAltar");
        registerTileEntity(TileAttunementAltar.class, "TileAttunementAltar");
        registerTileEntity(TileWell.class, "TileWell");

        // Register migrated TileEntities - Batch 2 (Advanced TileEntities)
        registerTileEntity(TileAttunementRelay.class, "TileAttunementRelay");
        registerTileEntity(TileCelestialGateway.class, "TileCelestialGateway");

        // Register migrated TileEntities - Batch 3 (Ritual & Machine blocks)
        registerTileEntity(TileRitualPedestal.class, "TileRitualPedestal");
        registerTileEntity(TileObservatory.class, "TileObservatory");
        registerTileEntity(TileTelescope.class, "TileTelescope");
        registerTileEntity(TileBore.class, "TileBore");

        // Register migrated TileEntities - Batch 4 (Starlight network blocks)
        registerTileEntity(TileCollectorCrystal.class, "TileCollectorCrystal");
        registerTileEntity(TileCrystalLens.class, "TileCrystalLens");
        registerTileEntity(TileCrystalPrismLens.class, "TileCrystalPrismLens");

        // Register migrated TileEntities - Batch 5 (Structure & Special blocks)
        registerTileEntity(TileStructController.class, "TileStructController");
        registerTileEntity(TileStructuralConnector.class, "TileStructuralConnector");
        registerTileEntity(TileRitualLink.class, "TileRitualLink");
        registerTileEntity(TileTreeBeacon.class, "TileTreeBeacon");

        // Register migrated TileEntities - Batch 6 (Celestial & Decorative blocks)
        registerTileEntity(TileCelestialOrrery.class, "TileCelestialOrrery");
        registerTileEntity(TileMapDrawingTable.class, "TileMapDrawingTable");
        registerTileEntity(TileStarlightInfuser.class, "TileStarlightInfuser");
        registerTileEntity(TileWorldIlluminator.class, "TileWorldIlluminator");

        // Register migrated TileEntities - Batch 7 (Gem & Crystal blocks)
        registerTileEntity(TileGemCrystals.class, "TileGemCrystals");
        registerTileEntity(TileFakeTree.class, "TileFakeTree");

        // Log registered TileEntities
        LogHelper.info("Registered " + TILES_TO_REGISTER.size() + " TileEntities");

        LogHelper.exit("RegistryTileEntities.preInit");
    }

    /**
     * Register a TileEntity with the specified name
     *
     * @param tileClass The TileEntity class to register
     * @param name      The registry name (without mod ID prefix)
     */
    public static void registerTileEntity(Class<? extends TileEntity> tileClass, String name) {
        if (tileClass == null) {
            throw new IllegalArgumentException("Attempted to register null TileEntity class!");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("TileEntity name cannot be null or empty!");
        }

        // Register the TileEntity
        GameRegistry.registerTileEntity(tileClass, name);

        // Track for later
        TILES_TO_REGISTER.add(tileClass);

        LogHelper.debug("Registered TileEntity: " + name);
    }

    /**
     * Register a TileEntity with a helper (common name pattern)
     *
     * @param tileClass The TileEntity class to register
     * @param blockName The associated block name
     */
    public static void registerTileEntityWithBlock(Class<? extends TileEntity> tileClass, String blockName) {
        registerTileEntity(tileClass, "Tile" + blockName);
    }

    /**
     * Get all registered TileEntity classes
     *
     * @return List of all registered TileEntity classes
     */
    public static List<Class<? extends TileEntity>> getRegisteredTileEntities() {
        return Lists.newArrayList(TILES_TO_REGISTER);
    }

    /**
     * Initialize TileEntities after registration
     * Called during postInit
     */
    public static void init() {
        LogHelper.entry("RegistryTileEntities.init");

        // Initialize TileEntities here (if needed)

        LogHelper.exit("RegistryTileEntities.init");
    }
}
