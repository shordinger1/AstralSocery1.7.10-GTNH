/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Main mod class for Astral Sorcery
 * Based on the 1.12.2 version and adapted for 1.7.10 Forge
 *
 * Copyright (c) 2026 Astral Sorcery Port Team
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import hellfirepvp.astralsorcery.common.handler.GuiHandler;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.proxy.CommonProxy;

/**
 * Main mod class for Astral Sorcery
 *
 * This class handles the main FML lifecycle events and delegates to proxies.
 * Based on Twist Space Technology and BartWorks mod structures.
 */
@Mod(
    modid = Constants.MODID,
    name = Constants.MODNAME,
    version = Constants.VERSION,
    acceptedMinecraftVersions = Constants.ACCEPTED_VERSIONS,
    dependencies = Constants.DEPENDENCIES,
    guiFactory = Constants.CLIENT_PROXY + "$$GuiFactory" // Placeholder for GUI factory
)
public class AstralSorcery {

    /**
     * Mod instance - required by FML
     */
    @Mod.Instance(Constants.MODID)
    public static AstralSorcery instance;

    /**
     * Logger instance
     */
    public static final Logger LOG = LogManager.getLogger(Constants.MODNAME);

    /**
     * Proxies for client/server side separation
     */
    @SidedProxy(clientSide = Constants.CLIENT_PROXY, serverSide = Constants.SERVER_PROXY)
    public static CommonProxy proxy;

    /**
     * Constructor
     */
    public AstralSorcery() {
        LogHelper.info("Loading " + Constants.MODNAME + " " + Constants.VERSION);
    }

    /**
     * Pre-initialization phase
     *
     * In this phase:
     * - Load configuration
     * - Register items and blocks
     * - Register TileEntities and entities
     * - Set up creative tabs
     *
     * @param event The FML pre-initialization event
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LogHelper.info("Starting preInit phase...");

        long startTime = System.currentTimeMillis();

        // Delegate to proxy
        proxy.preInit(event);

        long duration = System.currentTimeMillis() - startTime;
        LogHelper.info("preInit completed in " + duration + "ms");
    }

    /**
     * Initialization phase
     *
     * In this phase:
     * - Register event handlers
     * - Register network packets
     * - Register GUI handlers
     * - Register recipes
     * - Register renderers (client side)
     *
     * @param event The FML initialization event
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LogHelper.info("Starting init phase...");

        long startTime = System.currentTimeMillis();

        // Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        // Delegate to proxy
        proxy.init(event);

        long duration = System.currentTimeMillis() - startTime;
        LogHelper.info("init completed in " + duration + "ms");
    }

    /**
     * Post-initialization phase
     *
     * In this phase:
     * - Complete recipe registration
     * - Initialize mod integrations
     * - Finalize compatibility settings
     * - Clean up and validate
     *
     * @param event The FML post-initialization event
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LogHelper.info("Starting postInit phase...");

        long startTime = System.currentTimeMillis();

        // Delegate to proxy
        proxy.postInit(event);

        long duration = System.currentTimeMillis() - startTime;
        LogHelper.info("postInit completed in " + duration + "ms");
    }

    /**
     * Server starting phase
     *
     * In this phase:
     * - Register server commands
     * - Set up server-specific handlers
     *
     * @param event The FML server starting event
     */
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        LogHelper.info("Server starting...");

        // Delegate to proxy
        proxy.serverStarting(event);
    }

    /**
     * Server started phase
     *
     * In this phase:
     * - Perform any final server-side setup
     * - Load server-specific data
     *
     * @param event The FML server started event
     */
    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        LogHelper.info("Server started successfully!");

        // Delegate to proxy
        proxy.serverStarted(event);
    }

    /**
     * Get the mod instance
     *
     * @return The mod instance
     */
    public static AstralSorcery getInstance() {
        return instance;
    }

    /**
     * Get the current mod ID
     *
     * @return The mod ID
     */
    public static String getModId() {
        return Constants.MODID;
    }
}
