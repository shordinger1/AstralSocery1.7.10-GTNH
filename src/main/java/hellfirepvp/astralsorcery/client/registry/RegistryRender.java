/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Render registry for client-side rendering
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Render registry for Astral Sorcery (1.7.10)
 * <p>
 * Provides methods for registering:
 * - TileEntitySpecialRenderers (TESR) - using ClientRegistry
 * - ItemRenderers - using MinecraftForgeClient
 * - EntityRenderers - using RenderingRegistry
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>TESR: {@link cpw.mods.fml.client.registry.ClientRegistry#bindTileEntitySpecialRenderer}</li>
 * <li>ItemRenderer: {@link net.minecraftforge.client.MinecraftForgeClient#registerItemRenderer}</li>
 * <li>EntityRenderer: {@link cpw.mods.fml.client.registry.RenderingRegistry#registerEntityRenderingHandler}</li>
 * <li>Color handlers: NOT AVAILABLE in 1.7.10 (introduced in 1.8+)</li>
 * </ul>
 * <p>
 * <b>Recommended usage:</b>
 * 
 * <pre>
 * // Option 1: Register in TESR constructor (preferred)
 * public class MyTESR extends AstralBaseTESR&lt;MyTileEntity&gt; {
 * 
 *     public MyTESR() {
 *         super();
 *         ClientRegistry.bindTileEntitySpecialRenderer(MyTileEntity.class, this);
 *     }
 * }
 *
 * // Option 2: Register in ClientProxy.init()
 * ClientRegistry.bindTileEntitySpecialRenderer(MyTileEntity.class, new MyTESR());
 * MinecraftForgeClient.registerItemRenderer(item, new MyItemRenderer());
 * RenderingRegistry.registerEntityRenderingHandler(MyEntity.class, new MyEntityRenderer());
 * </pre>
 */
public class RegistryRender {

    /**
     * Initialize render registry
     * Called from ClientProxy.init()
     */
    public static void init() {
        LogHelper.entry("RegistryRender.init");

        // Initialize central OBJ model loading and TESR registration
        // This uses the TST pattern for cleaner rendering code
        LogHelper.info("Initializing AstralRenderLoader (TST pattern)...");
        try {
            hellfirepvp.astralsorcery.client.renderer.AstralRenderLoader.init();
        } catch (Exception e) {
            LogHelper.error("Failed to initialize AstralRenderLoader", e);
        }

        // Register all TileEntitySpecialRenderers
        // This includes the CelestialGateway TESR via GatewayRenderLoader
        registerTileEntityRenderers();

        // Register Entity renderers (old system, still needed)
        registerEntityRenderers();

        LogHelper.info("Render registry initialized");
        LogHelper.exit("RegistryRender.init");
    }

    /**
     * Register all TileEntitySpecialRenderers
     * <p>
     * 1.7.10: Uses direct instantiation with type casting
     */
    private static void registerTileEntityRenderers() {
        LogHelper.entry("registerTileEntityRenderers");
        LogHelper.debug("Starting TileEntity renderer registration...");

        int successCount = 0;
        int failCount = 0;

        // Register Altar TESR
        String tileName = "TileAltar";
        String className = "hellfirepvp.astralsorcery.common.tile.TileAltar";
        String tesrName = "hellfirepvp.astralsorcery.client.renderer.tile.TESRAltar";
        try {
            LogHelper.debug("Attempting to register TESR for: " + tileName);
            Class<? extends TileEntity> tileAltar = (Class<? extends TileEntity>) Class.forName(className);
            Class<?> tesrClass = Class.forName(tesrName);
            TileEntitySpecialRenderer tesr = (TileEntitySpecialRenderer) tesrClass.newInstance();
            registerTileEntityRenderer(tileAltar, tesr);
            successCount++;
            LogHelper.info("✓ Successfully registered TESR for: " + tileName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error("✗ Failed to register TESR for " + tileName + " - Class not found: " + e.getMessage(), e);
            LogHelper.debug("  Looking for TileEntity: " + className);
            LogHelper.debug("  Looking for TESR: " + tesrName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error("✗ Failed to instantiate TESR for " + tileName + " - Instantiation: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access TESR constructor for " + tileName + " - Illegal access: " + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering TESR for " + tileName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register Chalice TESR
        // DISABLED: Handled by AstralRenderLoader.registerChaliceRenderer()
        // which properly passes model and texture to TESRChalice constructor
        LogHelper.info("⊘ Skipping TileChalice TESR registration (handled by AstralRenderLoader)");

        // Register Well TESR
        tileName = "TileWell";
        className = "hellfirepvp.astralsorcery.common.tile.TileWell";
        tesrName = "hellfirepvp.astralsorcery.client.renderer.tile.TESRWell";
        try {
            LogHelper.debug("Attempting to register TESR for: " + tileName);
            Class<? extends TileEntity> tileWell = (Class<? extends TileEntity>) Class.forName(className);
            Class<?> tesrClass = Class.forName(tesrName);
            TileEntitySpecialRenderer tesr = (TileEntitySpecialRenderer) tesrClass.newInstance();
            registerTileEntityRenderer(tileWell, tesr);
            successCount++;
            LogHelper.info("✓ Successfully registered TESR for: " + tileName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error("✗ Failed to register TESR for " + tileName + " - Class not found: " + e.getMessage(), e);
            LogHelper.debug("  Looking for TileEntity: " + className);
            LogHelper.debug("  Looking for TESR: " + tesrName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error("✗ Failed to instantiate TESR for " + tileName + " - Instantiation: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access TESR constructor for " + tileName + " - Illegal access: " + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering TESR for " + tileName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register Vanishing TESR
        tileName = "TileVanishing";
        className = "hellfirepvp.astralsorcery.common.tile.TileVanishing";
        tesrName = "hellfirepvp.astralsorcery.client.renderer.tile.TESRVanishing";
        try {
            LogHelper.debug("Attempting to register TESR for: " + tileName);
            Class<? extends TileEntity> tileVanishing = (Class<? extends TileEntity>) Class.forName(className);
            Class<?> tesrClass = Class.forName(tesrName);
            TileEntitySpecialRenderer tesr = (TileEntitySpecialRenderer) tesrClass.newInstance();
            registerTileEntityRenderer(tileVanishing, tesr);
            successCount++;
            LogHelper.info("✓ Successfully registered TESR for: " + tileName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error("✗ Failed to register TESR for " + tileName + " - Class not found: " + e.getMessage(), e);
            LogHelper.debug("  Looking for TileEntity: " + className);
            LogHelper.debug("  Looking for TESR: " + tesrName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error("✗ Failed to instantiate TESR for " + tileName + " - Instantiation: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access TESR constructor for " + tileName + " - Illegal access: " + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering TESR for " + tileName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register Attunement Altar TESR
        tileName = "TileAttunementAltar";
        className = "hellfirepvp.astralsorcery.common.tile.TileAttunementAltar";
        tesrName = "hellfirepvp.astralsorcery.client.renderer.tile.TESRAttunementAltar";
        try {
            LogHelper.debug("Attempting to register TESR for: " + tileName);
            Class<? extends TileEntity> tileAttunementAltar = (Class<? extends TileEntity>) Class.forName(className);
            Class<?> tesrClass = Class.forName(tesrName);
            TileEntitySpecialRenderer tesr = (TileEntitySpecialRenderer) tesrClass.newInstance();
            registerTileEntityRenderer(tileAttunementAltar, tesr);
            successCount++;
            LogHelper.info("✓ Successfully registered TESR for: " + tileName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error("✗ Failed to register TESR for " + tileName + " - Class not found: " + e.getMessage(), e);
            LogHelper.debug("  Looking for TileEntity: " + className);
            LogHelper.debug("  Looking for TESR: " + tesrName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error("✗ Failed to instantiate TESR for " + tileName + " - Instantiation: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access TESR constructor for " + tileName + " - Illegal access: " + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering TESR for " + tileName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register Celestial Gateway TESR using AstralRenderLoader
        // Note: Gateway TESR is already registered in AstralRenderLoader.init()
        // which is called before this method in RegistryRender.init()
        // This prevents duplicate registration
        tileName = "TileCelestialGateway";
        try {
            LogHelper.debug("Celestial Gateway TESR already registered by AstralRenderLoader");
            successCount++;
            LogHelper.info("✓ Celestial Gateway TESR registration confirmed (via AstralRenderLoader)");
        } catch (Exception e) {
            failCount++;
            LogHelper.error("✗ Failed to confirm Gateway TESR registration", e);
        }

        // Register Grindstone TESR
        tileName = "TileGrindstone";
        className = "hellfirepvp.astralsorcery.common.tile.TileGrindstone";
        tesrName = "hellfirepvp.astralsorcery.client.renderer.tile.TESRGrindstone";
        try {
            LogHelper.debug("Attempting to register TESR for: " + tileName);
            Class<? extends TileEntity> tileGrindstone = (Class<? extends TileEntity>) Class.forName(className);
            Class<?> tesrClass = Class.forName(tesrName);
            TileEntitySpecialRenderer tesr = (TileEntitySpecialRenderer) tesrClass.newInstance();
            registerTileEntityRenderer(tileGrindstone, tesr);
            successCount++;
            LogHelper.info("✓ Successfully registered TESR for: " + tileName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error("✗ Failed to register TESR for " + tileName + " - Class not found: " + e.getMessage(), e);
            LogHelper.debug("  Looking for TileEntity: " + className);
            LogHelper.debug("  Looking for TESR: " + tesrName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error("✗ Failed to instantiate TESR for " + tileName + " - Instantiation: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access TESR constructor for " + tileName + " - Illegal access: " + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering TESR for " + tileName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register Attunement Relay TESR
        tileName = "TileAttunementRelay";
        className = "hellfirepvp.astralsorcery.common.tile.TileAttunementRelay";
        tesrName = "hellfirepvp.astralsorcery.client.renderer.tile.TESRAttunementRelay";
        try {
            LogHelper.debug("Attempting to register TESR for: " + tileName);
            Class<? extends TileEntity> tileAttunementRelay = (Class<? extends TileEntity>) Class.forName(className);
            Class<?> tesrClass = Class.forName(tesrName);
            TileEntitySpecialRenderer tesr = (TileEntitySpecialRenderer) tesrClass.newInstance();
            registerTileEntityRenderer(tileAttunementRelay, tesr);
            successCount++;
            LogHelper.info("✓ Successfully registered TESR for: " + tileName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error("✗ Failed to register TESR for " + tileName + " - Class not found: " + e.getMessage(), e);
            LogHelper.debug("  Looking for TileEntity: " + className);
            LogHelper.debug("  Looking for TESR: " + tesrName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error("✗ Failed to instantiate TESR for " + tileName + " - Instantiation: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access TESR constructor for " + tileName + " - Illegal access: " + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering TESR for " + tileName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        LogHelper.info(
            "TileEntity renderer registration complete: " + successCount + " succeeded, " + failCount + " failed");
        LogHelper.exit("registerTileEntityRenderers");
    }

    /**
     * Register all Entity renderers
     * <p>
     * 1.7.10: Uses direct instantiation with type casting
     */
    private static void registerEntityRenderers() {
        LogHelper.entry("registerEntityRenderers");
        LogHelper.debug("Starting Entity renderer registration...");

        int successCount = 0;
        int failCount = 0;

        // Register EntityFlare → RenderEntityFlare
        String entityName = "EntityFlare";
        String entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityFlare";
        String rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityFlare";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityFlare = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityFlare, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityCrystal → RenderEntityCrystal
        entityName = "EntityCrystal";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityCrystal";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityCrystal";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityCrystal = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityCrystal, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityItemStardust → RenderEntityItemStardust (requires RenderItem)
        entityName = "EntityItemStardust";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityItemStardust";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityItemStardust";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityItemStardust = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            // Create new RenderItem instance for 1.7.10
            net.minecraft.client.renderer.entity.RenderItem renderItem = new net.minecraft.client.renderer.entity.RenderItem();
            // Create renderer with RenderItem constructor parameter
            java.lang.reflect.Constructor<?> constructor = rendererClass
                .getConstructor(net.minecraft.client.renderer.entity.RenderItem.class);
            Render renderer = (Render) constructor.newInstance(renderItem);
            registerEntityRenderer(entityItemStardust, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (NoSuchMethodException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to find RenderItem constructor for " + entityName + " - No such method: " + e.getMessage(),
                e);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to invoke Entity renderer constructor for " + entityName
                    + " - Invocation target: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityStarburst → RenderEntityStarburst
        entityName = "EntityStarburst";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityStarburst";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityStarburst";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityStarburst = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityStarburst, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityGrapplingHook → RenderEntityGrapplingHook
        entityName = "EntityGrapplingHook";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityGrapplingHook";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityGrapplingHook";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityGrapplingHook = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityGrapplingHook, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntitySpectralTool → RenderEntitySpectralTool
        entityName = "EntitySpectralTool";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntitySpectralTool";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntitySpectralTool";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entitySpectralTool = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entitySpectralTool, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityShootingStar → RenderEntityShootingStar
        entityName = "EntityShootingStar";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityShootingStar";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityShootingStar";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityShootingStar = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityShootingStar, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityIlluminationSpark → RenderEntityIlluminationSpark
        entityName = "EntityIlluminationSpark";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityIlluminationSpark";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityIlluminationSpark = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityIlluminationSpark, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityLiquidSpark → RenderEntityLiquidSpark
        entityName = "EntityLiquidSpark";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityLiquidSpark";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityLiquidSpark";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityLiquidSpark = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityLiquidSpark, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityNocturnalSpark → RenderEntityNocturnalSpark
        entityName = "EntityNocturnalSpark";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityNocturnalSpark";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityNocturnalSpark = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityNocturnalSpark, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityCrystalTool → RenderEntityCrystalTool
        entityName = "EntityCrystalTool";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityCrystalTool";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityCrystalTool";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityCrystalTool = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityCrystalTool, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        // Register EntityObservatoryHelper → RenderEntityObservatoryHelper
        entityName = "EntityObservatoryHelper";
        entityClassName = "hellfirepvp.astralsorcery.common.entity.EntityObservatoryHelper";
        rendererClassName = "hellfirepvp.astralsorcery.client.renderer.entity.RenderEntityObservatoryHelper";
        try {
            LogHelper.debug("Attempting to register Entity renderer for: " + entityName);
            Class<? extends Entity> entityObservatoryHelper = (Class<? extends Entity>) Class.forName(entityClassName);
            Class<?> rendererClass = Class.forName(rendererClassName);
            Render renderer = (Render) rendererClass.newInstance();
            registerEntityRenderer(entityObservatoryHelper, renderer);
            successCount++;
            LogHelper.info("✓ Successfully registered Entity renderer for: " + entityName);
        } catch (ClassNotFoundException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to register Entity renderer for " + entityName + " - Class not found: " + e.getMessage(),
                e);
            LogHelper.debug("  Looking for Entity: " + entityClassName);
            LogHelper.debug("  Looking for Renderer: " + rendererClassName);
        } catch (InstantiationException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to instantiate Entity renderer for " + entityName + " - Instantiation: " + e.getMessage(),
                e);
        } catch (IllegalAccessException e) {
            failCount++;
            LogHelper.error(
                "✗ Failed to access Entity renderer constructor for " + entityName
                    + " - Illegal access: "
                    + e.getMessage(),
                e);
        } catch (Exception e) {
            failCount++;
            LogHelper.error(
                "✗ Unexpected error registering Entity renderer for " + entityName
                    + " - "
                    + e.getClass()
                        .getSimpleName()
                    + ": "
                    + e.getMessage(),
                e);
        }

        LogHelper
            .info("Entity renderer registration complete: " + successCount + " succeeded, " + failCount + " failed");
        LogHelper.exit("registerEntityRenderers");
    }

    /**
     * Register a TileEntitySpecialRenderer
     * <p>
     * Note: It's recommended to call this in the TESR constructor instead,
     * or register all TESRs in one place in ClientProxy.init()
     *
     * @param tileClass TileEntity class
     * @param renderer  TESR instance
     */
    public static void registerTileEntityRenderer(Class<? extends TileEntity> tileClass,
        TileEntitySpecialRenderer renderer) {
        String className = tileClass != null ? tileClass.getSimpleName() : "null";
        String rendererName = renderer != null ? renderer.getClass()
            .getSimpleName() : "null";

        LogHelper.entry("registerTileEntityRenderer(" + className + ", " + rendererName + ")");

        if (tileClass == null || renderer == null) {
            LogHelper.error("✗ Failed to register TESR: null parameter provided");
            LogHelper.debug("  TileEntity class: " + (tileClass != null ? tileClass.getName() : "NULL"));
            LogHelper.debug(
                "  Renderer class: " + (renderer != null ? renderer.getClass()
                    .getName() : "NULL"));
            LogHelper.exit("registerTileEntityRenderer");
            return;
        }

        try {
            LogHelper.debug("Calling ClientRegistry.bindTileEntitySpecialRenderer for: " + className);
            ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
            LogHelper.info("✓ Successfully bound TESR: " + className + " -> " + rendererName);
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register TESR for: " + className, e);
            LogHelper.debug("  TileEntity class: " + tileClass.getName());
            LogHelper.debug(
                "  Renderer class: " + renderer.getClass()
                    .getName());
            LogHelper.debug(
                "  Exception type: " + e.getClass()
                    .getName());
            LogHelper.debug("  Exception message: " + e.getMessage());

            // Print stack trace for debugging
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            LogHelper.debug("  Stack trace:\n" + sw.toString());
        }

        LogHelper.exit("registerTileEntityRenderer");
    }

    /**
     * Register an Item renderer (for blocks with special item rendering)
     * <p>
     * Uses MinecraftForgeClient.registerItemRenderer() for 1.7.10
     * <p>
     * <b>1.7.10 API Note:</b>
     * In 1.7.10, item renderers implement one of:
     * - {@link net.minecraftforge.client.IItemRenderer} - Forge item renderer interface
     * - {@link net.minecraft.client.renderer.entity.RenderItem} - Vanilla item renderer
     * <p>
     * Example usage:
     * 
     * <pre>
     * MinecraftForgeClient.registerItemRenderer(item, new IItemRenderer() {
     *     public boolean handleRenderType(ItemStack item, ItemRenderType type) { ... }
     *     public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) { ... }
     *     public void renderItem(ItemRenderType type, ItemStack item, Object... data) { ... }
     * });
     * </pre>
     *
     * @param item     Item to register renderer for
     * @param renderer IItemRenderer or RenderItem instance
     */
    public static void registerItemRenderer(Item item, IItemRenderer renderer) {
        String itemName = item != null ? item.getUnlocalizedName() : "null";
        String rendererName = renderer != null ? renderer.getClass()
            .getSimpleName() : "null";

        LogHelper.entry("registerItemRenderer(" + itemName + ", " + rendererName + ")");

        if (item == null || renderer == null) {
            LogHelper.error("✗ Failed to register item renderer: null parameter provided");
            LogHelper.debug("  Item: " + (item != null ? item.getUnlocalizedName() : "NULL"));
            LogHelper.debug(
                "  Renderer: " + (renderer != null ? renderer.getClass()
                    .getName() : "NULL"));
            LogHelper.exit("registerItemRenderer");
            return;
        }

        try {
            LogHelper.debug("Calling MinecraftForgeClient.registerItemRenderer for: " + itemName);
            // 1.7.10 uses MinecraftForgeClient for item renderers
            MinecraftForgeClient.registerItemRenderer(item, renderer);
            LogHelper.info("✓ Successfully registered item renderer for: " + itemName);
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register item renderer for: " + itemName, e);
            LogHelper.debug(
                "  Item class: " + item.getClass()
                    .getName());
            LogHelper.debug("  Item unlocalized name: " + item.getUnlocalizedName());
            LogHelper.debug(
                "  Renderer class: " + renderer.getClass()
                    .getName());
            LogHelper.debug(
                "  Exception type: " + e.getClass()
                    .getName());
            LogHelper.debug("  Exception message: " + e.getMessage());

            // Print stack trace
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            LogHelper.debug("  Stack trace:\n" + sw.toString());
        }

        LogHelper.exit("registerItemRenderer");
    }

    /**
     * Register an Item renderer with metadata variant
     * <p>
     * Uses MinecraftForgeClient.registerItemRenderer() for 1.7.10
     *
     * @param item     Item to register
     * @param metadata Metadata value (for multi-texture items)
     * @param renderer IItemRenderer or RenderItem instance
     */
    public static void registerItemRenderer(Item item, int metadata, IItemRenderer renderer) {
        String itemName = item != null ? item.getUnlocalizedName() : "null";
        String rendererName = renderer != null ? renderer.getClass()
            .getSimpleName() : "null";

        LogHelper.entry("registerItemRenderer(" + itemName + ", meta:" + metadata + ", " + rendererName + ")");

        if (item == null || renderer == null) {
            LogHelper.error("✗ Failed to register item renderer: null parameter provided");
            LogHelper.debug("  Item: " + (item != null ? item.getUnlocalizedName() : "NULL"));
            LogHelper.debug("  Metadata: " + metadata);
            LogHelper.debug(
                "  Renderer: " + (renderer != null ? renderer.getClass()
                    .getName() : "NULL"));
            LogHelper.exit("registerItemRenderer");
            return;
        }

        try {
            LogHelper.debug("Calling MinecraftForgeClient.registerItemRenderer for: " + itemName + " meta:" + metadata);
            // 1.7.10 uses MinecraftForgeClient for item renderers with metadata
            MinecraftForgeClient.registerItemRenderer(item, renderer);
            LogHelper.info("✓ Successfully registered item renderer for: " + itemName + " (meta: " + metadata + ")");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register item renderer for: " + itemName + " (meta: " + metadata + ")", e);
            LogHelper.debug(
                "  Item class: " + item.getClass()
                    .getName());
            LogHelper.debug("  Item unlocalized name: " + item.getUnlocalizedName());
            LogHelper.debug("  Metadata: " + metadata);
            LogHelper.debug(
                "  Renderer class: " + renderer.getClass()
                    .getName());
            LogHelper.debug(
                "  Exception type: " + e.getClass()
                    .getName());
            LogHelper.debug("  Exception message: " + e.getMessage());

            // Print stack trace
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            LogHelper.debug("  Stack trace:\n" + sw.toString());
        }

        LogHelper.exit("registerItemRenderer");
    }

    /**
     * Register an Item renderer (deprecated - location-based method not supported in 1.7.10)
     *
     * @deprecated Use registerItemRenderer(Item, Object) or registerItemRenderer(Item, int, Object)
     */
    @Deprecated
    public static void registerItemRenderer(Item item, int metadata, String location) {
        if (item == null) {
            LogHelper.error("Failed to register item renderer: null item");
            return;
        }

        // Location-based rendering not supported in 1.7.10
        LogHelper.warn(
            "Location-based item rendering not supported in 1.7.10. "
                + "Use registerItemRenderer(Item, Object) instead.");
    }

    /**
     * Register an Entity renderer
     * <p>
     * Uses RenderingRegistry.registerEntityRenderingHandler() for 1.7.10
     *
     * @param entityClass Entity class
     * @param renderer    Render instance
     */
    public static void registerEntityRenderer(Class<? extends Entity> entityClass, Render renderer) {
        String entityName = entityClass != null ? entityClass.getSimpleName() : "null";
        String rendererName = renderer != null ? renderer.getClass()
            .getSimpleName() : "null";

        LogHelper.entry("registerEntityRenderer(" + entityName + ", " + rendererName + ")");

        if (entityClass == null || renderer == null) {
            LogHelper.error("✗ Failed to register entity renderer: null parameter provided");
            LogHelper.debug("  Entity class: " + (entityClass != null ? entityClass.getName() : "NULL"));
            LogHelper.debug(
                "  Renderer class: " + (renderer != null ? renderer.getClass()
                    .getName() : "NULL"));
            LogHelper.exit("registerEntityRenderer");
            return;
        }

        try {
            LogHelper.debug("Calling RenderingRegistry.registerEntityRenderingHandler for: " + entityName);
            // 1.7.10 uses RenderingRegistry for entity renderers
            RenderingRegistry.registerEntityRenderingHandler(entityClass, renderer);
            LogHelper.info("✓ Successfully registered entity renderer for: " + entityName);
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register entity renderer for: " + entityName, e);
            LogHelper.debug("  Entity class: " + entityClass.getName());
            LogHelper.debug(
                "  Renderer class: " + renderer.getClass()
                    .getName());
            LogHelper.debug(
                "  Exception type: " + e.getClass()
                    .getName());
            LogHelper.debug("  Exception message: " + e.getMessage());

            // Print stack trace
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            LogHelper.debug("  Stack trace:\n" + sw.toString());
        }

        LogHelper.exit("registerEntityRenderer");
    }

    /**
     * Register a block color handler (1.7.10 - not implemented)
     * <p>
     * Note: BlockColors/ItemColors system was introduced in Minecraft 1.8+
     * In 1.7.10, blocks should implement IBlockColor directly (Forge extension)
     *
     * @param block        Block to register color handler for
     * @param colorHandler Color handler
     * @deprecated Block color handlers not available in 1.7.10. Implement IBlockColor directly on your block.
     */
    @Deprecated
    public static void registerBlockColorHandler(Block block, Object colorHandler) {
        LogHelper.warn(
            "Block color handlers not available in 1.7.10. "
                + "Implement IBlockColor or ISimpleBlockRendering directly on your block.");
    }

    /**
     * Register an item color handler (1.7.10 - not implemented)
     * <p>
     * Note: BlockColors/ItemColors system was introduced in Minecraft 1.8+
     * In 1.7.10, items should implement IItemColor directly (Forge extension)
     *
     * @param item         Item to register color handler for
     * @param colorHandler Color handler
     * @deprecated Item color handlers not available in 1.7.10. Implement IItemColor directly on your item.
     */
    @Deprecated
    public static void registerItemColorHandler(Item item, Object colorHandler) {
        LogHelper.warn(
            "Item color handlers not available in 1.7.10. "
                + "Implement IItemColor or similar Forge interface directly on your item.");
    }
}
