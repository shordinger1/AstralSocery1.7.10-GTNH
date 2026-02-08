/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Common proxy for shared client and server logic
 ******************************************************************************/

package hellfirepvp.astralsorcery.proxy;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.event.EventHandler;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.network.NetworkWrapper;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import hellfirepvp.astralsorcery.common.registry.RegistryConstellations;
import hellfirepvp.astralsorcery.common.registry.RegistryEnchantments;
import hellfirepvp.astralsorcery.common.registry.RegistryEntities;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.registry.RegistryTileEntities;
import hellfirepvp.astralsorcery.common.registry.RegistryWorldGenerators;
import hellfirepvp.astralsorcery.common.util.LanguageManager;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Common proxy for shared client/server logic
 * <p>
 * This proxy contains all logic that runs on both sides.
 * Client-specific code goes in ClientProxy.
 */
public class CommonProxy {

    /**
     * Pre-initialization phase
     * <p>
     * Tasks:
     * - Load configuration
     * - Register items
     * - Register blocks
     * - Register TileEntities
     * - Register entities
     * - Register potions
     * - Register world generators
     * - Load localizations
     */
    public void preInit(FMLPreInitializationEvent event) {
        LogHelper.entry("CommonProxy.preInit");

        // Load configuration
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        LogHelper.info(Constants.MODNAME + " " + Constants.VERSION);

        // Set up integration flags
        setupIntegrations();

        // Initialize language manager
        LanguageManager.init();

        // Register items
        RegistryItems.preInit();

        // Register blocks
        RegistryBlocks.preInit();

        // Register TileEntities
        RegistryTileEntities.preInit();

        // Register entities
        RegistryEntities.preInit();

        // Register potions
        RegistryPotions.preInit();

        // Register enchantments
        RegistryEnchantments.preInit();

        // Register constellations
        RegistryConstellations.init();

        // Initialize multiblock structures
        hellfirepvp.astralsorcery.common.structure.MultiblockStructures.init();

        // Register world generators
        RegistryWorldGenerators.preInit();

        LogHelper.exit("CommonProxy.preInit");
    }

    /**
     * Initialization phase
     * <p>
     * Tasks:
     * - Register event handlers
     * - Register network packets
     * - Register GUI handlers
     */
    public void init(FMLInitializationEvent event) {
        LogHelper.entry("CommonProxy.init");

        // Register event handler
        EventHandler eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(eventHandler);

        // Register ConstellationSkyHandler as event listener for constellation updates
        MinecraftForge.EVENT_BUS.register(ConstellationSkyHandler.getInstance());
        LogHelper.info("Registered ConstellationSkyHandler event listener");

        // Register PerkEventHandler for constellation perk effects
        MinecraftForge.EVENT_BUS.register(new hellfirepvp.astralsorcery.common.event.PerkEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new hellfirepvp.astralsorcery.common.event.PerkEventHandler());
        LogHelper.info("Registered PerkEventHandler for constellation perk effects");

        // Register network packets
        NetworkWrapper.init();

        LogHelper.exit("CommonProxy.init");
    }

    /**
     * Post-initialization phase
     * <p>
     * Tasks:
     * - Register all game objects (blocks, items, entities)
     * - Register recipes
     * - Initialize NEI integration
     * - Finalize setup
     */
    public void postInit(FMLPostInitializationEvent event) {
        LogHelper.entry("CommonProxy.postInit");

        // Step 1: Register all game objects first (blocks, items, entities, etc.)
        LogHelper.info("Step 1: Registering game objects...");
        RegistryItems.init();
        RegistryBlocks.init();
        RegistryTileEntities.init();
        RegistryEntities.init();
        RegistryPotions.init();
        RegistryEnchantments.init();
        RegistryWorldGenerators.init();

        // Step 2: Register recipes (after all items/blocks are registered)
        LogHelper.info("Step 2: Registering recipes...");
        // Initialize recipe registry FIRST (creates empty recipe maps)
        hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry.init();
        // Then register all recipes (adds them to the initialized maps)
        hellfirepvp.astralsorcery.common.crafting.altar.ASAltarRecipes.registerRecipes();

        // Initialize WellLiquefaction system
        LogHelper.info("Initializing WellLiquefaction system...");
        hellfirepvp.astralsorcery.common.util.WellLiquefaction.init();

        // Step 3: Initialize NEI integration (after recipes are registered)
        LogHelper.info("Step 3: Initializing NEI integration...");
        try {
            hellfirepvp.astralsorcery.client.nei.NEIHandler.registerNEICatalysts(event);
            // Hide technical blocks after NEI has fully initialized
            // This avoids FastUtil hash collisions during GT's recipe cache building
            hellfirepvp.astralsorcery.client.nei.NEI_Config.hideTechnicalBlocksLate();
            LogHelper.info("NEI integration initialized successfully");
        } catch (Exception e) {
            LogHelper.info("NEI not installed or integration failed: " + e.getMessage());
        }
        LogHelper.info(
            "entity.EntityShootingStar.name   :   " + StatCollector.translateToLocal("entity.EntityShootingStar.name"));
        LogHelper.exit("CommonProxy.postInit");
    }

    /**
     * Server starting phase
     * <p>
     * Tasks:
     * - Register server commands
     */
    public void serverStarting(FMLServerStartingEvent event) {
        LogHelper.entry("CommonProxy.serverStarting");

        // Register Astral Sorcery commands
        event.registerServerCommand(new hellfirepvp.astralsorcery.common.cmd.CommandAstralSorcery());
        LogHelper.info("Registered /astralsorcery and /as commands");

        LogHelper.exit("CommonProxy.serverStarting");
    }

    /**
     * Server started phase
     * <p>
     * Tasks:
     * - Final server-side setup
     */
    public void serverStarted(FMLServerStartedEvent event) {
        LogHelper.entry("CommonProxy.serverStarted");

        // Any final server setup

        LogHelper.info(Constants.MODNAME + " loaded successfully!");
        LogHelper.exit("CommonProxy.serverStarted");
    }

    /**
     * Set up integration flags
     */
    private void setupIntegrations() {
        // This will be implemented when we add integrations
        // Example:
        // Constants.hasGregTech = Loader.isModLoaded("gregtech");
    }

    /**
     * Get a world object (for spawning particles, etc.)
     * Returns null on dedicated server
     */
    public Object getClientWorld() {
        return null;
    }

    /**
     * Get the player (for client-side operations)
     * Returns null on dedicated server
     */
    public Object getClientPlayer() {
        return null;
    }

    /**
     * Register a renderer (no-op on server)
     */
    public void registerRenderer(Object location, Object renderer) {
        // Client proxy will override this
    }

    /**
     * Register aTESR (no-op on server)
     */
    public void registerTESR(Class<?> tileClass, Object renderer) {
        // Client proxy will override this
    }

    /**
     * Register an item renderer (no-op on server)
     */
    public void registerItemRenderer(Item item, int meta, String location) {
        // Client proxy will override this
    }
}
