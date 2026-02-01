/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Potion registration handler
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.util.List;

import net.minecraft.potion.Potion;

import com.google.common.collect.Lists;

import hellfirepvp.astralsorcery.common.base.AstralBasePotion;
import hellfirepvp.astralsorcery.common.potion.PotionBleed;
import hellfirepvp.astralsorcery.common.potion.PotionCheatDeath;
import hellfirepvp.astralsorcery.common.potion.PotionDropModifier;
import hellfirepvp.astralsorcery.common.potion.PotionSpellPlague;
import hellfirepvp.astralsorcery.common.potion.PotionTimeFreeze;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Potion registry for Astral Sorcery
 *
 * Handles registration of all potions in the mod.
 *
 * IMPORTANT: All new potions should extend {@link AstralBasePotion}
 * rather than extending {@link net.minecraft.potion.Potion} directly.
 */
public class RegistryPotions {

    private static final List<Potion> POTIONS_TO_REGISTER = Lists.newArrayList();

    // ========== Public Potion Instances ==========
    public static final PotionBleed POTION_BLEED = PotionBleed.INSTANCE;
    public static final PotionTimeFreeze POTION_TIME_FREEZE = PotionTimeFreeze.INSTANCE;
    public static final PotionDropModifier POTION_DROP_MODIFIER = PotionDropModifier.INSTANCE;
    public static final PotionSpellPlague POTION_SPELL_PLAGUE = PotionSpellPlague.INSTANCE;
    public static final PotionCheatDeath POTION_CHEAT_DEATH = PotionCheatDeath.INSTANCE;

    /**
     * Pre-initialization: register all potions
     */
    public static void preInit() {
        LogHelper.entry("RegistryPotions.preInit");

        // Register all potions
        registerPotion(POTION_BLEED);
        registerPotion(POTION_TIME_FREEZE);
        registerPotion(POTION_DROP_MODIFIER);
        registerPotion(POTION_SPELL_PLAGUE);
        registerPotion(POTION_CHEAT_DEATH);

        // Log registered potions
        LogHelper.info("Registered " + POTIONS_TO_REGISTER.size() + " potions");

        LogHelper.exit("RegistryPotions.preInit");
    }

    /**
     * Register a potion
     *
     * @param potion The potion to register
     * @return The registered potion
     */
    public static Potion registerPotion(Potion potion) {
        if (potion == null) {
            throw new IllegalArgumentException("Attempted to register null potion!");
        }

        // Potions are automatically assigned IDs in AstralBasePotion constructor
        // No additional registration needed in 1.7.10

        // Track for later
        POTIONS_TO_REGISTER.add(potion);

        LogHelper.debug("Registered potion: " + potion.getName());

        return potion;
    }

    /**
     * Get all registered potions
     *
     * @return List of all registered potions
     */
    public static List<Potion> getRegisteredPotions() {
        return Lists.newArrayList(POTIONS_TO_REGISTER);
    }

    /**
     * Initialize potions after registration
     * Called during postInit
     */
    public static void init() {
        LogHelper.entry("RegistryPotions.init");

        // Initialize potions here (if needed)

        LogHelper.exit("RegistryPotions.init");
    }
}
