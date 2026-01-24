/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancements;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancements.instances.AltarRecipeInstance;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.migration.advancements.PlayerAdvancements;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarCraftTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 14:23
 */
public class AltarCraftTrigger extends ListenerCriterionTrigger<AltarRecipeInstance> {

    public static final ResourceLocation ID = new ResourceLocation(AstralSorcery.MODID, "altar_craft");

    public AltarCraftTrigger() {
        super(ID);
    }

    @Override
    public AltarRecipeInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return AltarRecipeInstance.deserialize(getId(), json);
    }

    public void trigger(EntityPlayerMP player, AbstractAltarRecipe recipe) {
        // 1.7.10: Use AdvancementManager instead of AdvancementManager.getAdvancements(player)
        PlayerAdvancements advancements = AdvancementManager.getAdvancements(player);
        Listeners<AltarRecipeInstance> listeners = this.listeners.get(advancements);
        if (listeners != null) {
            listeners.trigger(instance -> ((AltarRecipeInstance) instance).test(recipe));
        }
    }

}
