/*******************************************************************************
 * 1.7.10 Compatibility Stub - Advancement System
 * Advancements were introduced in Minecraft 1.12.
 * This is a stub for 1.7.10 compatibility.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration.advancements;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

/**
 * Stub for ICriterionTrigger (1.12+) for 1.7.10 compatibility.
 */
public interface ICriterionTrigger<T extends ICriterionInstance> {

    ResourceLocation getId();

    void addListener(PlayerAdvancements playerAdvancements, Listener<T> listener);

    void removeListener(PlayerAdvancements playerAdvancements, Listener<T> listener);

    void removeAllListeners(PlayerAdvancements playerAdvancements);

    /**
     * Deserialize instance from JSON.
     * In 1.7.10, this is a compatibility stub.
     */
    T deserializeInstance(JsonObject json, JsonDeserializationContext context);

    /**
     * Stub for Listener (1.12+) for 1.7.10 compatibility.
     */
    public static interface Listener<T extends ICriterionInstance> {

        void grantCriterion(PlayerAdvancements playerAdvancements);

        T getCriterionInstance();
    }
}
