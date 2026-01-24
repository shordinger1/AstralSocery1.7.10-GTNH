/*******************************************************************************
 * 1.7.10 Compatibility - Advancement System
 * Advancements were introduced in Minecraft 1.12.
 * This provides a base implementation for 1.7.10 compatibility.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancements;

import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.common.migration.advancements.ICriterionInstance;

/**
 * Base class for criterion instances in Astral Sorcery.
 * Provides common functionality for advancement criteria.
 */
public abstract class AbstractCriterionInstance implements ICriterionInstance {

    private final ResourceLocation id;

    public AbstractCriterionInstance(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }
}
