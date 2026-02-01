/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * WorldGenerator registration handler
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.world.StructureGenAncientShrine;
import hellfirepvp.astralsorcery.common.world.StructureGenSmallShrine;
import hellfirepvp.astralsorcery.common.world.StructureGenTreasureShrine;
import hellfirepvp.astralsorcery.common.world.StructureGenSmallRuin;

/**
 * WorldGenerator registry for Astral Sorcery
 *
 * Handles registration of all world generators in the mod.
 *
 * IMPORTANT: All new world generators should extend
 * {@link hellfirepvp.astralsorcery.common.world.AstralBaseWorldGenerator}
 * rather than implementing {@link cpw.mods.fml.common.IWorldGenerator} directly.
 */
public class RegistryWorldGenerators {

    private static final List<IWorldGenerator> GENERATORS_TO_REGISTER = Lists.newArrayList();

    /**
     * Pre-initialization: register all world generators
     */
    public static void preInit() {
        LogHelper.entry("RegistryWorldGenerators.preInit");

        // Register structure generators
        // Weight 0-10: Structures (should generate early)
        registerWorldGenerator(new StructureGenAncientShrine(), 0);
        registerWorldGenerator(new StructureGenSmallShrine(), 1);
        registerWorldGenerator(new StructureGenTreasureShrine(), 2);
        registerWorldGenerator(new StructureGenSmallRuin(), 3);

        // Log registered generators
        LogHelper.info("Registered " + GENERATORS_TO_REGISTER.size() + " world generators");
        LogHelper.info("  - Ancient Shrine: 1 in 200 chunks (mountain biomes)");
        LogHelper.info("  - Small Shrine: 1 in 120 chunks (most biomes)");
        LogHelper.info("  - Treasure Shrine: 1 in 180 chunks (forest/plains)");
        LogHelper.info("  - Small Ruin: 1 in 80 chunks (most biomes)");

        LogHelper.exit("RegistryWorldGenerators.preInit");
    }

    /**
     * Register a world generator
     *
     * @param generator The world generator to register
     * @param weight    The generation weight (lower = earlier)
     * @return The registered generator
     */
    public static IWorldGenerator registerWorldGenerator(IWorldGenerator generator, int weight) {
        if (generator == null) {
            throw new IllegalArgumentException("Attempted to register null world generator!");
        }

        // Register the generator
        GameRegistry.registerWorldGenerator(generator, weight);

        // Track for later
        GENERATORS_TO_REGISTER.add(generator);

        LogHelper.debug("Registered world generator with weight " + weight);

        return generator;
    }

    /**
     * Register a world generator with default weight
     *
     * @param generator The world generator to register
     * @return The registered generator
     */
    public static IWorldGenerator registerWorldGenerator(IWorldGenerator generator) {
        return registerWorldGenerator(generator, 0);
    }

    /**
     * Get all registered world generators
     *
     * @return List of all registered world generators
     */
    public static List<IWorldGenerator> getRegisteredWorldGenerators() {
        return Lists.newArrayList(GENERATORS_TO_REGISTER);
    }

    /**
     * Initialize world generators after registration
     * Called during postInit
     */
    public static void init() {
        LogHelper.entry("RegistryWorldGenerators.init");

        // Initialize world generators here (if needed)

        LogHelper.exit("RegistryWorldGenerators.init");
    }
}
