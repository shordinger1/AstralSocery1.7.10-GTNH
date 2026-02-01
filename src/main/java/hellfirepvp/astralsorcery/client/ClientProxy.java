/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Client-side proxy for client-specific logic
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.proxy.CommonProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.event.ClientEventHandler;
import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Client-side proxy for client-specific logic
 *
 * This proxy handles all client-side only code such as:
 * - Rendering
 * - Models
 * - Textures
 * - Particles
 * - GUIs
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LogHelper.entry("ClientProxy.preInit");

        // Call super
        super.preInit(event);

        // Register client event handler
        LogHelper.debug("Attempting to register ClientEventHandler...");
        try {
            MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
            LogHelper.info("✓ ClientEventHandler registered successfully");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register ClientEventHandler", e);
        }

        // Register all ResourceLocation objects for entities and TESRs
        LogHelper.debug("Attempting to register ResourceLocation objects...");
        try {
            ResourceLocationRegister.registerAll();
            LogHelper.info("✓ ResourceLocation registration complete");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register ResourceLocation objects", e);
        }

        // Register effect handler
        LogHelper.debug("Attempting to register EffectHandler...");
        try {
            FMLCommonHandler.instance()
                .bus()
                .register(EffectHandler.getInstance());
            LogHelper.info("✓ EffectHandler registered successfully");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register EffectHandler", e);
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

        LogHelper.exit("ClientProxy.preInit");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        LogHelper.entry("ClientProxy.init");

        // Call super
        super.init(event);

        // Register NEI altar recipe handlers
        LogHelper.debug("Attempting to register NEI altar recipe handlers...");
        try {
            hellfirepvp.astralsorcery.client.nei.ASNEIRegistry.registerNEI();
            LogHelper.info("✓ Successfully registered NEI altar recipe handlers");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register NEI altar handlers", e);
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

        // Initialize render registry - THIS IS CRITICAL!
        // This registers all TESRs, entity renderers, and item renderers
        LogHelper.debug("Initializing render registry...");
        try {
            hellfirepvp.astralsorcery.client.registry.RegistryRender.init();
            LogHelper.info("✓ Render registry initialization complete");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to initialize render registry", e);
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

        // Register OBJ models
        LogHelper.debug("Registering OBJ models...");
        try {
            hellfirepvp.astralsorcery.client.renderer.ModelRegistry.registerModels();
            LogHelper.info("✓ OBJ model registration complete");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register OBJ models", e);
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

        // Client-specific initialization here
        // - Additional renderers
        // - Models
        // - Textures

        // Register fluid icons
        LogHelper.debug("Registering fluid icons...");
        try {
            registerFluidIcons();
            LogHelper.info("✓ Fluid icon registration complete");
        } catch (Exception e) {
            LogHelper.error("✗ Failed to register fluid icons", e);
        }

        LogHelper.exit("ClientProxy.init");
    }

    /**
     * Register fluid icons for rendering
     */
    @SideOnly(Side.CLIENT)
    private void registerFluidIcons() {
        // In 1.7.10, fluid icons are registered automatically when the Fluid is registered
        // But we can also set them explicitly if needed

        // Note: In 1.7.10, fluids use IIconRegister through their own registerIcons() method
        // The setIcons() call will be handled by the Fluid class itself
        // We just need to make sure the Fluid is properly registered

        if (BlocksAS.fluidLiquidStarlight != null) {
            LogHelper.debug("Liquid starlight fluid registered - icons will be set by Fluid class");
            // Icons are automatically registered through Fluid.registerFluid()
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        LogHelper.entry("ClientProxy.postInit");

        // Call super
        super.postInit(event);

        LogHelper.exit("ClientProxy.postInit");
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        LogHelper.entry("ClientProxy.serverStarting");

        // Call super
        super.serverStarting(event);

        LogHelper.exit("ClientProxy.serverStarting");
    }

    @Override
    public void serverStarted(FMLServerStartedEvent event) {
        LogHelper.entry("ClientProxy.serverStarted");

        // Call super
        super.serverStarted(event);

        LogHelper.exit("ClientProxy.serverStarted");
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public void registerRenderer(Object block, Object renderer) {
        // Will be implemented in RegistryRender
    }

    @Override
    public void registerTESR(Class<?> tileClass, Object renderer) {
        // Will be implemented in RegistryRender
        // ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
    }

    @Override
    public void registerItemRenderer(net.minecraft.item.Item item, int meta, String location) {
        // Will be implemented in RegistryRender
    }
}
