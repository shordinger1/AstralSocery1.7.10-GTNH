/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Configuration class
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * Config - Configuration class (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Loads and manages all mod configuration options</li>
 * <li>Uses Forge Configuration system</li>
 * <li>Simplified from 1.12.2 version (no event-based reloading)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Removed ConfigChangedEvent.OnConfigChangedEvent event handler</li>
 * <li>Removed ConfigDataAdapter and ConfigEntry system</li>
 * <li>Simple one-time loading in synchronizeConfiguration()</li>
 * <li>Configuration class API is the same (getBoolean, getInt, getFloat, etc.)</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // In your main mod class or proxy:
 * File configFile = new File(event.getModConfigurationDirectory(), "AstralSorcery.cfg");
 * Config.synchronizeConfiguration(configFile);
 * </pre>
 */
public class Config {

    // General settings
    public static boolean enablePatreonEffects = true;
    public static boolean respectIdealDistances = true;
    public static int aquamarineAmount = 64;
    public static int marbleAmount = 4;
    public static int marbleVeinSize = 20;
    public static int constellationPaperRarity = 10;
    public static int constellationPaperQuality = 2;

    public static boolean lightProximityAltarRecipe = true;
    public static boolean lightProximityResonatingWandRecipe = true;

    public static boolean clientPreloadTextures = true;
    public static boolean giveJournalFirst = true;
    public static boolean doesMobSpawnDenyDenyEverything = false;
    public static boolean rockCrystalOreSilkTouchHarvestable = false;

    public static boolean disableFestiveMapper = false;

    public static float capeChaosResistance = 0.8F;

    // Attuned wands configs
    public static float evorsioEffectChance = 0.8F;
    public static int discidiaStackCap = 10;
    public static float discidiaStackMultiplier = 1F;

    public static boolean grindstoneAddDustRecipes = true;

    // Liquid starlight crafting - IMPORTANT: Used by Entity classes
    public static boolean craftingLiqCrystalGrowth = true;
    public static boolean craftingLiqCrystalToolGrowth = true;
    public static boolean craftingLiqCelestialCrystalForm = true;
    public static boolean canCrystalGrowthYieldDuplicates = true;

    // Liquid starlight interactions
    public static boolean liquidStarlightAquamarine = true;
    public static boolean liquidStarlightSand = true;
    public static boolean liquidStarlightIce = true;
    public static boolean liquidStarlightInfusedWood = true;

    public static boolean enableFlatGen = false;
    public static boolean enableRetroGen = false;

    // Rendering
    public static int maxEffectRenderDistance = 64;
    public static int maxEffectRenderDistanceSq;
    public static int particleAmount = 2;

    // Feature Toggles (from ConfigurationHandler)
    public static boolean enableAlchemy = true;
    public static boolean enableConstellations = true;
    public static boolean enableRituals = true;
    public static int starlightTransmissionRange = 256;

    // Client Graphics (from ConfigurationHandler)
    public static boolean fancyGraphics = true;
    public static boolean enableParticles = true;
    public static boolean enableLightBeams = true;
    public static float beamOpacity = 0.6F;

    // Server Settings (from ConfigurationHandler)
    public static boolean allowRitualsInSpawn = true;
    public static int maxRitualLinkDistance = 64;
    public static int celestialCrystalGrowthMultiplier = 100;

    // Debug Settings (from ConfigurationHandler)
    public static boolean debugMode = false;
    public static boolean verboseLogging = false;
    public static boolean enableDevTools = false;

    // Entity settings
    public static int ambientFlareChance = 9;
    public static boolean flareKillsBats = true;

    // Tool settings
    public static boolean shouldChargedToolsRevert = true;
    public static int revertStart = 40;
    public static int revertChance = 80;

    public static double swordSharpMultiplier = 0.1;

    public static float illuminationWandUseCost = 0.5F;
    public static float grappleWandUseCost = 0.7F;
    public static float architectWandUseCost = 0.07F;
    public static float exchangeWandUseCost = 0.08F;

    public static float exchangeWandMaxHardness = -1;

    public static int dayLength = 24000;

    public static List<Integer> constellationSkyDimWhitelist = new ArrayList<Integer>();
    public static List<Integer> weakSkyRendersWhitelist = new ArrayList<Integer>();
    public static List<String> modidOreGenBlacklist = new ArrayList<String>();
    public static List<Integer> worldGenDimWhitelist = new ArrayList<Integer>();
    public static boolean performNetworkIntegrityCheck = false;

    private Config() {}

    /**
     * Synchronize configuration from file
     * 1.7.10: Simple static loading method
     *
     * @param configFile The configuration file
     */
    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        try {
            configuration.load();

            // General settings
            giveJournalFirst = configuration.getBoolean(
                "giveJournalAtFirstJoin",
                "general",
                giveJournalFirst,
                "If set to 'true', the player will receive an AstralSorcery Journal when they join the server for the first time.");

            doesMobSpawnDenyDenyEverything = configuration.getBoolean(
                "doesMobSpawnDenyAllTypes",
                "general",
                doesMobSpawnDenyDenyEverything,
                "If set to 'true' anything that prevents mobspawning by this mod, will also prevent EVERY natural mobspawning of any mobtype. When set to 'false' it'll only stop monsters from spawning.");

            swordSharpMultiplier = configuration.getFloat(
                "swordSharpenedMultiplier",
                "general",
                (float) swordSharpMultiplier,
                0.0F,
                10000.0F,
                "Defines how much the 'sharpened' modifier increases the damage of the sword if applied. Config value is in percent.");

            rockCrystalOreSilkTouchHarvestable = configuration.getBoolean(
                "isRockCrystalOreSilkHarvestable",
                "general",
                rockCrystalOreSilkTouchHarvestable,
                "If this is set to true, Rock-Crystal-Ore may be silk-touch harvested by a player.");

            String[] dimWhitelist = configuration.getStringList(
                "skySupportedDimensions",
                "general",
                new String[] { "0" },
                "Whitelist of dimension ID's that will have special sky rendering");

            String[] weakSkyRenders = configuration.getStringList(
                "weakSkyRenders",
                "general",
                new String[] {},
                "IF a dimensionId is listed in 'skySupportedDimensions' you can add it here to keep its sky render, but AS will try to render only constellations on top of its existing sky render.");

            String[] oreModidBlacklist = configuration.getStringList(
                "oreGenBlacklist",
                "general",
                new String[] { "techreborn" },
                "List any number of modid's here and the aevitas perk & mineralis ritual will not spawn ores that originate from any of the mods listed here.");

            modidOreGenBlacklist.clear();
            for (String s : oreModidBlacklist) {
                modidOreGenBlacklist.add(s);
            }

            dayLength = configuration.getInt(
                "dayLength",
                "general",
                dayLength,
                100,
                Integer.MAX_VALUE,
                "Defines the length of a day (both daytime & nighttime obviously) for the mod's internal logic. NOTE: This does NOT CHANGE HOW LONG A DAY IN MC IS! It is only to provide potential compatibility for mods that do provide such functionality.");

            // Entity settings
            ambientFlareChance = configuration.getInt(
                "EntityFlare.ambientspawn",
                "entities",
                ambientFlareChance,
                0,
                200_000,
                "Defines how common ***ambient*** flares are. the lower the more common. 0 = ambient ones don't appear/disabled.");

            flareKillsBats = configuration.getBoolean(
                "EntityFlare.killbats",
                "entities",
                flareKillsBats,
                "If this is set to true, occasionally, a spawned flare will (attempt to) kill bats close to it.");

            // Recipe settings
            lightProximityAltarRecipe = configuration.getBoolean(
                "LightProximity-Altar",
                "recipes",
                lightProximityAltarRecipe,
                "If this is set to false, the luminous crafting table recipe that'd require 'light shining at a crafting table' is disabled.");

            lightProximityResonatingWandRecipe = configuration.getBoolean(
                "LightProximity-ResonatingWand",
                "recipes",
                lightProximityResonatingWandRecipe,
                "If this is set to false, the resonating wand recipe that'd require 'light shining at a crafting table' is disabled.");

            // Tool settings
            illuminationWandUseCost = configuration.getFloat(
                "wandCost_illumination",
                "tools",
                illuminationWandUseCost,
                0.0F,
                1.0F,
                "Sets the quick-charge cost for one usage of the illumination wand");

            architectWandUseCost = configuration.getFloat(
                "wandCost_architect",
                "tools",
                architectWandUseCost,
                0.0F,
                1.0F,
                "Sets the quick-charge cost for one usage of the architect wand");

            exchangeWandUseCost = configuration.getFloat(
                "wandCost_exchange",
                "tools",
                exchangeWandUseCost,
                0.0F,
                1.0F,
                "Sets the quick-charge cost for one usage of the exchange wand");

            grappleWandUseCost = configuration.getFloat(
                "wandCost_grapple",
                "tools",
                grappleWandUseCost,
                0.0F,
                1.0F,
                "Sets the quick-charge cost for one usage of the grapple wand");

            exchangeWandMaxHardness = configuration.getFloat(
                "exchange_wand_max_hardness",
                "tools",
                exchangeWandMaxHardness,
                -1,
                50000,
                "Sets the max. hardness the exchange wand can swap !from!. If the block you're trying to \"mine\" with the conversion wand is higher than this number, it won't work. (-1 to disable this check)");

            capeChaosResistance = configuration.getFloat(
                "cape_chaosresistance",
                "tools",
                capeChaosResistance,
                0.0F,
                1.0F,
                "Sets the amount of damage reduction a player gets when being hit by a DE chaos-damage-related damagetype.");

            shouldChargedToolsRevert = configuration.getBoolean(
                "chargedCrystalToolsRevert",
                "tools",
                shouldChargedToolsRevert,
                "If this is set to true, charged crystals tools can revert back to their inert state.");

            revertStart = configuration.getInt(
                "chargedCrystalToolsRevertStart",
                "tools",
                revertStart,
                0,
                Integer.MAX_VALUE - 1,
                "Defines the minimum uses a user at least gets before it's trying to revert to an inert crystal tool.");

            revertChance = configuration.getInt(
                "chargedCrystalToolsRevertChance",
                "tools",
                revertChance,
                1,
                Integer.MAX_VALUE,
                "After 'chargedCrystalToolsRevertStart' uses, it will random.nextInt(chance) == 0 try and see if the tool gets reverted to its inert crystal tool.");

            evorsioEffectChance = configuration.getFloat(
                "evorsioAttunedWandEffectChance",
                "tools",
                evorsioEffectChance,
                0F,
                1F,
                "Defines the chance per mined block that the effect for holding an evorsio attuned resonating wand will fire.");

            discidiaStackCap = configuration.getInt(
                "discidiaDamageStackCap",
                "tools",
                discidiaStackCap,
                1,
                200,
                "Defines the amount of stacks you have to get against the same mob until you reach 100% of the damage multiplier.");

            discidiaStackMultiplier = configuration.getFloat(
                "discidiaDamageStackMultipler",
                "tools",
                discidiaStackMultiplier,
                0F,
                200F,
                "Defines the additional damage multiplier gradually increased by gaining attack-stacks against a mob. (Applied multiplier = damage * 1 + (thisConfigOption * (currentStacks / maxStacks)) )");

            grindstoneAddDustRecipes = configuration.getBoolean(
                "grindstoneAddOreToDustRecipes",
                "crafting",
                grindstoneAddDustRecipes,
                "Set this to false to prevent the lookup and registration of oreblock -> ore dust recipes on the grindstone.");

            // IMPORTANT: Liquid starlight crafting - Used by Entity classes
            craftingLiqCrystalGrowth = configuration.getBoolean(
                "liquidStarlightCrystalGrowth",
                "crafting",
                craftingLiqCrystalGrowth,
                "Set this to false to disable Rock/Celestial Crystal growing in liquid starlight.");

            craftingLiqCelestialCrystalForm = configuration.getBoolean(
                "liquidStarlightCelestialCrystalCluster",
                "crafting",
                craftingLiqCelestialCrystalForm,
                "Set this to false to disable crystal + stardust -> Celestial Crystal cluster forming");

            craftingLiqCrystalToolGrowth = configuration.getBoolean(
                "liquidStarlightCrystalToolGrowth",
                "crafting",
                craftingLiqCrystalToolGrowth,
                "Set this to false to disable Crystal Tool growth in liquid starlight");

            canCrystalGrowthYieldDuplicates = configuration.getBoolean(
                "canCrystalGrowthYieldDuplicates",
                "crafting",
                canCrystalGrowthYieldDuplicates,
                "Set this to false to disable the chance to get a 2nd crystal when growing a max-sized one in liquid starlight.");

            // IMPORTANT: Liquid starlight interactions - Used by BlockFluidLiquidStarlight
            liquidStarlightAquamarine = configuration.getBoolean(
                "liquidStarlightAquamarine",
                "crafting",
                liquidStarlightAquamarine,
                "Set this to false to disable that liquid starlight + lava occasionally/rarely produces aquamarine shale instead of sand.");

            liquidStarlightSand = configuration.getBoolean(
                "liquidStarlightSand",
                "crafting",
                liquidStarlightSand,
                "Set this to false to disable that liquid starlight + lava produces sand.");

            liquidStarlightIce = configuration.getBoolean(
                "liquidStarlightIce",
                "crafting",
                liquidStarlightIce,
                "Set this to false to disable that liquid starlight + water produces ice.");

            liquidStarlightInfusedWood = configuration.getBoolean(
                "liquidStarlightInfusedWood",
                "crafting",
                liquidStarlightInfusedWood,
                "Set this to false to disable the functionality that wood logs will be converted to infused wood when thrown into liquid starlight.");

            // Light network
            configuration.addCustomCategoryComment(
                "lightnetwork",
                "Maintenance options for the Starlight network. Use the integrity check when you did a bigger rollback or MC-Edited stuff out of the world. Note that it will only affect worlds that get loaded. So if you edited out something on, for example, dimension -76, be sure to go into that dimension with the maintenance options enabled to properly perform maintenance there.");

            performNetworkIntegrityCheck = configuration.getBoolean(
                "performNetworkIntegrityCheck",
                "lightnetwork",
                performNetworkIntegrityCheck,
                "NOTE: ONLY run this once and set it to false again afterwards, nothing will be gained by setting this to true permanently, just longer loading times. When set to true and the server started, this will perform an integrity check over all nodes of the starlight network whenever a world gets loaded, removing invalid ones in the process. This might, depending on network sizes, take a while. It'll leave a message in the console when it's done. After this check has been run, you might need to tear down and rebuild your starlight network in case something doesn't work anymore.");

            // Rendering
            maxEffectRenderDistance = configuration.getInt(
                "maxEffectRenderDistance",
                "rendering",
                maxEffectRenderDistance,
                1,
                512,
                "Defines how close to the position of a particle/floating texture you have to be in order for it to render.");

            maxEffectRenderDistanceSq = maxEffectRenderDistance * maxEffectRenderDistance;

            clientPreloadTextures = configuration.getBoolean(
                "preloadTextures",
                "rendering",
                clientPreloadTextures,
                "If set to 'true' the mod will preload most of the bigger textures during postInit. This provides a more fluent gameplay experience (as it doesn't need to load the textures when they're first needed), but increases loadtime.");

            particleAmount = configuration.getInt(
                "particleAmount",
                "rendering",
                particleAmount,
                0,
                2,
                "Sets the amount of particles/effects: 0 = minimal (only necessary particles will appear), 1 = lowered (most unnecessary particles will be filtered), 2 = all particles are visible");

            disableFestiveMapper = configuration.getBoolean(
                "disableFestiveBlockTextures",
                "rendering",
                disableFestiveMapper,
                "Set to true to disable the festive textures/block models.");

            // Worldgen
            marbleAmount = configuration.getInt(
                "generateMarbleAmount",
                "worldgen",
                marbleAmount,
                0,
                32,
                "Defines how many marble veins are generated per chunk. 0 = disabled");

            marbleVeinSize = configuration.getInt(
                "generateMarbleVeinSize",
                "worldgen",
                marbleVeinSize,
                1,
                32,
                "Defines how big generated marble veins are.");

            aquamarineAmount = configuration.getInt(
                "generateAquamarineAmount",
                "worldgen",
                aquamarineAmount,
                0,
                2048,
                "Defines how many aquamarine ores it'll attempt to generate in per chunk. 0 = disabled");

            constellationPaperRarity = configuration.getInt(
                "constellationPaperRarity",
                "worldgen",
                constellationPaperRarity,
                1,
                128,
                "Defines the rarity of the constellation paper item in loot chests.");

            constellationPaperQuality = configuration.getInt(
                "constellationPaperQuality",
                "worldgen",
                constellationPaperQuality,
                1,
                128,
                "Defines the quality of the constellation paper item in loot chests.");

            respectIdealDistances = configuration.getBoolean(
                "respectIdealStructureDistances",
                "worldgen",
                respectIdealDistances,
                "If this is set to true, the world generator will try and spawn structures more evenly distributed by their 'ideal' distance set in their config entries. WARNING: might add additional worldgen time.");

            String[] dimGenWhitelist = configuration.getStringList(
                "worldGenWhitelist",
                "worldgen",
                new String[] { "0" },
                "the Astral Sorcery-specific worldgen will only run in Dimension ID's listed here.");

            enableFlatGen = configuration.getBoolean(
                "enableFlatGen",
                "worldgen",
                enableFlatGen,
                "By default, Astral Sorcery does not generate structures or ores in Super-Flat worlds. If, for some reason, you wish to enable generation of structures and ores in a Super-Flat world, then set this value to true.");

            enableRetroGen = configuration.getBoolean(
                "enableRetroGen",
                "retrogen",
                enableRetroGen,
                "WARNING: Setting this to true, will check on every chunk load if the chunk has been generated depending on the current AstralSorcery version. If the chunk was then generated with an older version, the mod will try and do the worldgen that's needed from the last recorded version to the current version. DO NOT ENABLE THIS FEATURE UNLESS SPECIFICALLY REQUIRED. It might/will slow down chunk loading.");

            enablePatreonEffects = configuration.getBoolean(
                "enablePatreonEffects",
                "patreon",
                enablePatreonEffects,
                "Enables/Disables all patreon effects.");

            // Feature Toggles (from ConfigurationHandler)
            enableAlchemy = configuration
                .getBoolean("enableAlchemy", "features", enableAlchemy, "Enable alchemy features");

            enableConstellations = configuration
                .getBoolean("enableConstellations", "features", enableConstellations, "Enable constellation features");

            enableRituals = configuration
                .getBoolean("enableRituals", "features", enableRituals, "Enable ritual features");

            starlightTransmissionRange = configuration.getInt(
                "starlightTransmissionRange",
                "features",
                starlightTransmissionRange,
                64,
                1024,
                "Maximum range for starlight transmission");

            // Client Graphics (from ConfigurationHandler)
            fancyGraphics = configuration
                .getBoolean("fancyGraphics", "graphics", fancyGraphics, "Use fancy graphics settings");

            enableParticles = configuration
                .getBoolean("enableParticles", "graphics", enableParticles, "Enable particle effects");

            enableLightBeams = configuration
                .getBoolean("enableLightBeams", "graphics", enableLightBeams, "Enable starlight beam rendering");

            beamOpacity = configuration.getFloat(
                "beamOpacity",
                "graphics",
                beamOpacity,
                0.0F,
                1.0F,
                "Opacity of starlight beams (0.0 = transparent, 1.0 = opaque)");

            // Server Settings (from ConfigurationHandler)
            allowRitualsInSpawn = configuration.getBoolean(
                "allowRitualsInSpawn",
                "server",
                allowRitualsInSpawn,
                "Allow rituals to be performed in spawn protection area");

            maxRitualLinkDistance = configuration.getInt(
                "maxRitualLinkDistance",
                "server",
                maxRitualLinkDistance,
                16,
                256,
                "Maximum distance for ritual pedestal links");

            celestialCrystalGrowthMultiplier = configuration.getInt(
                "celestialCrystalGrowthMultiplier",
                "server",
                celestialCrystalGrowthMultiplier,
                10,
                1000,
                "Growth speed multiplier for celestial crystals (100 = normal)");

            // Debug Settings (from ConfigurationHandler)
            debugMode = configuration
                .getBoolean("debugMode", "debug", debugMode, "Enable debug mode (additional logging)");

            verboseLogging = configuration
                .getBoolean("verboseLogging", "debug", verboseLogging, "Enable verbose logging");

            enableDevTools = configuration
                .getBoolean("enableDevTools", "debug", enableDevTools, "Enable development tools");

            // Fill whitelists
            fillWhitelistIDs(dimWhitelist);
            fillWeakSkyRenders(weakSkyRenders);
            fillDimGenWhitelist(dimGenWhitelist);

        } catch (Exception e) {
            AstralSorcery.LOG.error("Astral Sorcery failed to load configuration!", e);
        } finally {
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }

    /**
     * Fill constellation sky dimension whitelist
     */
    private static void fillWhitelistIDs(String[] dimWhitelist) {
        List<Integer> out = new ArrayList<Integer>();
        for (String s : dimWhitelist) {
            if (s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.LOG
                    .warn("Error while reading config entry 'skySupportedDimensions': " + s + " is not a number!");
            }
        }
        constellationSkyDimWhitelist = new ArrayList<Integer>(out.size());
        constellationSkyDimWhitelist.addAll(out);
        Collections.sort(constellationSkyDimWhitelist);
    }

    /**
     * Fill weak sky renders whitelist
     */
    private static void fillWeakSkyRenders(String[] weakSkyRenders) {
        List<Integer> out = new ArrayList<Integer>();
        for (String s : weakSkyRenders) {
            if (s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.LOG.warn("Error while reading config entry 'weakSkyRenders': " + s + " is not a number!");
            }
        }
        weakSkyRendersWhitelist = new ArrayList<Integer>(out.size());
        weakSkyRendersWhitelist.addAll(out);
        Collections.sort(weakSkyRendersWhitelist);
    }

    /**
     * Fill dimension gen whitelist
     */
    private static void fillDimGenWhitelist(String[] dimGenWhitelist) {
        List<Integer> out = new ArrayList<Integer>();
        for (String s : dimGenWhitelist) {
            if (s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.LOG
                    .warn("Error while reading config entry 'worldGenWhitelist': " + s + " is not a number!");
            }
        }
        worldGenDimWhitelist = new ArrayList<Integer>(out.size());
        worldGenDimWhitelist.addAll(out);
        Collections.sort(worldGenDimWhitelist);
    }

}
