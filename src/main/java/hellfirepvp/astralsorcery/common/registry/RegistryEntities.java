/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Entity registration handler
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.util.List;

import net.minecraft.entity.Entity;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.EntityRegistry;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.AstralBaseEntity;
import hellfirepvp.astralsorcery.common.entity.*;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Entity registry for Astral Sorcery
 *
 * Handles registration of all entities in the mod.
 * Based on TST entity registration
 *
 * IMPORTANT: All new entities should extend {@link AstralBaseEntity}
 * rather than extending {@link net.minecraft.entity.Entity} directly.
 */
public class RegistryEntities {

    private static int entityId = 0;
    private static final List<Class<? extends Entity>> ENTITIES_TO_REGISTER = Lists.newArrayList();

    /**
     * Pre-initialization: register all entities
     */
    public static void preInit() {
        LogHelper.entry("RegistryEntities.preInit");

        // Reset entity ID counter
        entityId = 0;

        // Register entities here
        // Item entities
        registerEntity(EntityItemHighlighted.class, "EntityItemHighlighted", 64, 3, true);
        registerEntity(EntityItemStardust.class, "EntityItemStardust", 64, 3, true);
        registerEntity(EntityItemExplosionResistant.class, "EntityItemExplosionResistant", 64, 3, true);

        // Tool entities
        registerEntity(EntityCrystalTool.class, "EntityCrystalTool", 64, 3, true);

        // Special entities
        registerEntity(EntityCrystal.class, "EntityCrystal", 64, 1, true);
        registerEntity(EntityFlare.class, "EntityFlare", 64, 1, true);
        registerEntity(EntityGrapplingHook.class, "EntityGrapplingHook", 64, 1, true);
        registerEntity(EntityObservatoryHelper.class, "EntityObservatoryHelper", 64, 1, false);
        // Note: EntityStarlightReacttant is an interface, not an entity class - skip registration

        // Skeleton entities - migrated but need further implementation
        registerEntity(EntityIlluminationSpark.class, "EntityIlluminationSpark", 64, 1, true);
        registerEntity(EntityLiquidSpark.class, "EntityLiquidSpark", 64, 1, true);
        registerEntity(EntityNocturnalSpark.class, "EntityNocturnalSpark", 64, 1, true);
        registerEntity(EntitySpectralTool.class, "EntitySpectralTool", 64, 1, true);
        registerEntity(EntityShootingStar.class, "EntityShootingStar", 64, 1, true);
        registerEntity(EntityStarburst.class, "EntityStarburst", 64, 1, true);

        // Log registered entities
        LogHelper.info("Registered " + ENTITIES_TO_REGISTER.size() + " entities");

        LogHelper.exit("RegistryEntities.preInit");
    }

    /**
     * Register an entity with the specified properties
     *
     * @param entityClass          The entity class to register
     * @param name                 The entity name
     * @param trackingRange        The tracking range in blocks
     * @param updateFrequency      The update frequency (ticks)
     * @param sendsVelocityUpdates Whether to send velocity updates to clients
     * @return The assigned entity ID
     */
    public static int registerEntity(Class<? extends Entity> entityClass, String name, int trackingRange,
        int updateFrequency, boolean sendsVelocityUpdates) {
        if (entityClass == null) {
            throw new IllegalArgumentException("Attempted to register null entity class!");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Entity name cannot be null or empty!");
        }

        // Register the entity
        EntityRegistry.registerModEntity(
            entityClass,
            name,
            entityId++,
            AstralSorcery.instance,
            trackingRange,
            updateFrequency,
            sendsVelocityUpdates);

        // Track for later
        ENTITIES_TO_REGISTER.add(entityClass);

        LogHelper.debug("Registered entity: " + name + " (ID: " + (entityId - 1) + ")");

        return entityId - 1;
    }

    /**
     * Register an entity with default properties
     *
     * @param entityClass The entity class to register
     * @param name        The entity name
     * @return The assigned entity ID
     */
    public static int registerEntity(Class<? extends Entity> entityClass, String name) {
        return registerEntity(entityClass, name, 64, 1, true);
    }

    /**
     * Register an entity with extended tracking range
     *
     * @param entityClass   The entity class to register
     * @param name          The entity name
     * @param trackingRange The tracking range in blocks
     * @return The assigned entity ID
     */
    public static int registerEntityWithTracking(Class<? extends Entity> entityClass, String name, int trackingRange) {
        return registerEntity(entityClass, name, trackingRange, 1, true);
    }

    /**
     * Get all registered entity classes
     *
     * @return List of all registered entity classes
     */
    public static List<Class<? extends Entity>> getRegisteredEntities() {
        return Lists.newArrayList(ENTITIES_TO_REGISTER);
    }

    /**
     * Initialize entities after registration
     * Called during postInit
     */
    public static void init() {
        LogHelper.entry("RegistryEntities.init");

        // Initialize entities here (e.g., spawn eggs, etc.)

        LogHelper.exit("RegistryEntities.init");
    }
}
