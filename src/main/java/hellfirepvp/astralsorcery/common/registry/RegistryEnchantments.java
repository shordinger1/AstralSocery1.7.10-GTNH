/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Enchantment registration handler
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.util.List;

import net.minecraft.enchantment.Enchantment;

import com.google.common.collect.Lists;

import hellfirepvp.astralsorcery.common.enchantment.EnchantmentBase;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentNightVision;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentPlayerWornTick;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentScorchingHeat;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Enchantment registry for Astral Sorcery
 *
 * Handles registration of all enchantments in the mod.
 *
 * IMPORTANT: All new enchantments should extend {@link EnchantmentBase}
 * or {@link EnchantmentPlayerWornTick} for equipment-based effects.
 *
 * 1.7.10 Notes:
 * - Enchantment IDs start from 70 to avoid conflicts with vanilla (0-66)
 * - Rarity is handled by weight parameter (1=rare, 10=common)
 * - EntityEquipmentSlot doesn't exist in 1.7.10
 * - Equipment detection handled through Baubles API or manual checking
 */
public class RegistryEnchantments {

    private static final List<Enchantment> ENCHANTMENTS_TO_REGISTER = Lists.newArrayList();

    // ========== Public Enchantment Instances ==========
    public static final EnchantmentNightVision ENCHANTMENT_NIGHT_VISION;
    public static final EnchantmentScorchingHeat ENCHANTMENT_SCORCHING_HEAT;

    static {
        // Initialize enchantments (auto-assign IDs starting from 70)
        ENCHANTMENT_NIGHT_VISION = new EnchantmentNightVision();
        ENCHANTMENT_SCORCHING_HEAT = new EnchantmentScorchingHeat();
    }

    /**
     * Pre-initialization: register all enchantments
     */
    public static void preInit() {
        LogHelper.entry("RegistryEnchantments.preInit");

        // Register all enchantments
        registerEnchantment(ENCHANTMENT_NIGHT_VISION);
        registerEnchantment(ENCHANTMENT_SCORCHING_HEAT);

        // Log registered enchantments
        LogHelper.info("Registered " + ENCHANTMENTS_TO_REGISTER.size() + " enchantments");

        LogHelper.exit("RegistryEnchantments.preInit");
    }

    /**
     * Register an enchantment
     *
     * @param enchantment The enchantment to register
     * @return The registered enchantment
     */
    public static Enchantment registerEnchantment(Enchantment enchantment) {
        if (enchantment == null) {
            throw new IllegalArgumentException("Attempted to register null enchantment!");
        }

        // Enchantments are automatically assigned IDs in EnchantmentBase constructor
        // No additional registration needed in 1.7.10

        // Track for later
        ENCHANTMENTS_TO_REGISTER.add(enchantment);

        LogHelper.debug("Registered enchantment: " + enchantment.getName());

        return enchantment;
    }

    /**
     * Get all registered enchantments
     *
     * @return List of all registered enchantments
     */
    public static List<Enchantment> getRegisteredEnchantments() {
        return Lists.newArrayList(ENCHANTMENTS_TO_REGISTER);
    }

    /**
     * Initialize enchantments after registration
     * Called during postInit
     */
    public static void init() {
        LogHelper.entry("RegistryEnchantments.init");

        // Initialize enchantments here (if needed)

        LogHelper.exit("RegistryEnchantments.init");
    }
}
