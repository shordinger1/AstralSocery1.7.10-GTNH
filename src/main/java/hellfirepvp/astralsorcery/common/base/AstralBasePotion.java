/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base Potion class for all AstralSorcery potions
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * AstralBasePotion - Base class for all AstralSorcery potions (1.7.10)
 * <p>
 * <b>1.7.10 Potion API (IMPORTANT):</b>
 * <ul>
 * <li>Constructor: {@code Potion(id, isBadEffect, liquidColor)}</li>
 * <li>Attribute modifiers: {@code func_111184_a(IAttribute, String uuid, double amount, int operation)} - OBFUSCATED
 * NAME!</li>
 * <li>Icon index: {@link net.minecraft.potion.Potion#setIconIndex(int, int)}</li>
 * <li>Name: {@link net.minecraft.potion.Potion#setPotionName(String)}</li>
 * <li>Effectiveness: {@link net.minecraft.potion.Potion#setEffectiveness(double)}</li>
 * <li>Effect logic: {@link #performEffect(EntityLivingBase, int)}</li>
 * <li>Ready check: {@link #isReady(int, int)}</li>
 * </ul>
 * <p>
 * <b>Example Usage:</b>
 * 
 * <pre>
 * public class PotionCelestial extends AstralBasePotion {
 *     public static final PotionCelestial INSTANCE = new PotionCelestial();
 *
 *     public PotionCelestial() {
 *         super(false, 0x00FFFF); // Beneficial, cyan color
 *
 *         // Set properties
 *         setIconIndex(0, 0); // Icon position in potions.png
 *
 *         // Add attribute modifier (speed boost)
 *         // NOTE: Uses OBFUSCATED method name in 1.7.10!
 *         func_111184_a(
 *             SharedMonsterAttributes.movementSpeed,
 *             "7107DE5E-7CE8-4030-940E-514C1F160890",  // UUID
 *             0.2D,  // +20% speed
 *             2      // Operation 2 = multiply
 *         );
 *     }
 *
 *     {@literal @}Override
 *     protected String getPotionNameKey() {
 *         return "astralsorcery.potion.celestial";
 *     }
 *
 *     {@literal @}Override
 *     public void performEffect(EntityLivingBase entity, int amplifier) {
 *         // Custom effect logic runs on server
 *         if (!entity.worldObj.isRemote) {
 *             // Apply custom effect
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * <b>Attribute Modifier Operations:</b>
 * <ul>
 * <li>0: Add to base value (e.g., +4 health = +2 hearts)</li>
 * <li>1: Multiply base value (e.g., x1.5)</li>
 * <li>2: Multiply total value (e.g., x1.2 for +20%)</li>
 * </ul>
 * <p>
 * <b>Vanilla Potion Examples (from source):</b>
 * 
 * <pre>
 * // Speed: +20% movement speed
 * func_111184_a(SharedMonsterAttributes.movementSpeed, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2D, 2);
 *
 * // Slowness: -15% movement speed
 * func_111184_a(SharedMonsterAttributes.movementSpeed, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15D, 2);
 *
 * // Strength: +3 damage (multiplied by amplifier)
 * func_111184_a(SharedMonsterAttributes.attackDamage, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 3.0D, 2);
 *
 * // Weakness: -2 damage (subtract from base)
 * func_111184_a(SharedMonsterAttributes.attackDamage, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
 *
 * // Health Boost: +4 health (2 hearts)
 * func_111184_a(SharedMonsterAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0);
 * </pre>
 * <p>
 * All AstralSorcery potions should extend this class.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public abstract class AstralBasePotion extends Potion {

    /**
     * Next available potion ID
     * Starts from 32 to avoid conflicts with vanilla potions
     */
    private static int nextAvailableId = 32;

    /**
     * Potion type enum
     */
    public enum PotionType {
        /**
         * Instant effect (healing, damage) - applied once
         */
        INSTANT,
        /**
         * Continuous effect (poison, regeneration) - applied over time
         */
        CONTINUOUS,
        /**
         * Hybrid effect - custom logic
         */
        HYBRID
    }

    /**
     * Potion type
     */
    protected PotionType type = PotionType.CONTINUOUS;

    /**
     * Has status icon in inventory
     */
    protected boolean hasStatusIcon = true;

    /**
     * Custom liquidity modifier (affects potion duration)
     */
    protected double customLiquidity = 1.0D;

    /**
     * Constructor
     * <p>
     * Automatically assigns next available ID starting from 32
     *
     * @param isBadEffect Whether this is a bad effect (poison, weakness, etc.)
     * @param liquidColor Potion liquid color (0xRRGGBB format)
     */
    public AstralBasePotion(boolean isBadEffect, int liquidColor) {
        super(nextAvailableId++, isBadEffect, liquidColor);

        // Set default effectiveness
        this.setEffectiveness(isBadEffect ? 0.5D : 1.0D);
        this.customLiquidity = isBadEffect ? 0.5D : 1.0D;

        // Set potion name (subclasses must override getPotionNameKey())
        this.setPotionName(getPotionNameKey());
    }

    /**
     * Get potion name key for localization
     * <p>
     * Subclasses MUST override this method
     * <p>
     * Example: "astralsorcery.potion.celestial"
     *
     * @return Localization key (e.g., "potion.modname.potionname")
     */
    protected abstract String getPotionNameKey();

    /**
     * Set potion type
     * <p>
     * INSTANT: Effect applied immediately when potion is consumed
     * CONTINUOUS: Effect applied over time (default)
     * HYBRID: Custom behavior
     *
     * @param type Potion type
     * @return this for method chaining
     */
    protected AstralBasePotion setType(PotionType type) {
        this.type = type;
        return this;
    }

    /**
     * Set whether this potion has a status icon
     *
     * @param hasIcon true to show icon in inventory
     * @return this for method chaining
     */
    protected AstralBasePotion setHasStatusIcon(boolean hasIcon) {
        this.hasStatusIcon = hasIcon;
        return this;
    }

    /**
     * Set icon index in the potions texture
     * <p>
     * Texture is 256x256, each icon is 18x18
     * X: 0-13 (columns), Y: 0-13 (rows)
     *
     * @param x X index (column)
     * @param y Y index (row)
     * @return this for method chaining
     */
    protected AstralBasePotion setIconIndex(int x, int y) {
        super.setIconIndex(x, y);
        return this;
    }

    /**
     * Set potion effectiveness/liquidity
     * <p>
     * Affects how long the potion lasts
     * Higher value = shorter duration
     * Default: 1.0D
     *
     * @param effectiveness Effectiveness value
     * @return this for method chaining
     */
    protected AstralBasePotion setLiquidity(double effectiveness) {
        this.customLiquidity = effectiveness;
        this.setEffectiveness(effectiveness);
        return this;
    }

    // ========== Attribute Modifier Helper Methods ==========

    /**
     * Add an attribute modifier with auto-generated UUID
     * <p>
     * <b>WARNING:</b> Auto-generated UUID will be different each game session!
     * Use only for testing. For production, use specific UUIDs.
     *
     * @param attribute The attribute to modify (e.g., SharedMonsterAttributes.movementSpeed)
     * @param amount    Modifier amount
     * @param operation Operation: 0=add, 1=multiply_base, 2=multiply
     * @return this for method chaining
     */
    protected AstralBasePotion addAttributeModifier(IAttribute attribute, double amount, int operation) {
        // Generate random UUID (NOT recommended for production!)
        UUID uuid = UUID.randomUUID();
        return addAttributeModifier(attribute, uuid.toString(), amount, operation);
    }

    /**
     * Add an attribute modifier with specific UUID
     * <p>
     * Use fixed UUIDs for production potions to ensure consistency
     * <p>
     * <b>1.7.10 API:</b> Uses obfuscated method name {@code func_111184_a}
     *
     * @param attribute The attribute to modify
     * @param uuid      UUID string (use a generator to create unique UUIDs)
     * @param amount    Modifier amount
     * @param operation Operation: 0=add, 1=multiply_base, 2=multiply
     * @return this for method chaining
     */
    protected AstralBasePotion addAttributeModifier(IAttribute attribute, String uuid, double amount, int operation) {
        // 1.7.10 uses obfuscated method name
        this.func_111184_a(attribute, uuid, amount, operation);
        return this;
    }

    /**
     * Add movement speed modifier
     * <p>
     * Positive values = speed boost
     * Negative values = slowness
     *
     * @param amount    Modifier amount (e.g., 0.2 = +20% speed)
     * @param operation Operation (usually 2 for percentage)
     * @return this for method chaining
     */
    protected AstralBasePotion addMovementSpeedModifier(double amount, int operation) {
        return addAttributeModifier(SharedMonsterAttributes.movementSpeed, amount, operation);
    }

    /**
     * Add attack damage modifier
     * <p>
     * Positive values = strength
     * Negative values = weakness
     *
     * @param amount    Modifier amount (e.g., 0.5 = +50% damage with operation 2)
     * @param operation Operation (usually 2 for percentage)
     * @return this for method chaining
     */
    protected AstralBasePotion addAttackDamageModifier(double amount, int operation) {
        return addAttributeModifier(SharedMonsterAttributes.attackDamage, amount, operation);
    }

    /**
     * Add max health modifier
     * <p>
     * Positive values = extra hearts
     * Negative values = health reduction
     *
     * @param amount    Modifier amount (e.g., 4.0 = +2 hearts with operation 0)
     * @param operation Operation (usually 0 for absolute value)
     * @return this for method chaining
     */
    protected AstralBasePotion addMaxHealthModifier(double amount, int operation) {
        return addAttributeModifier(SharedMonsterAttributes.maxHealth, amount, operation);
    }

    /**
     * Add knockback resistance modifier
     *
     * @param amount    Modifier amount (0.0-1.0 range)
     * @param operation Operation (usually 0 for absolute value)
     * @return this for method chaining
     */
    protected AstralBasePotion addKnockbackResistanceModifier(double amount, int operation) {
        return addAttributeModifier(SharedMonsterAttributes.knockbackResistance, amount, operation);
    }

    // ========== Override Methods ==========

    /**
     * Check if potion is instant (applied immediately)
     *
     * @return true if instant effect
     */
    @Override
    public boolean isInstant() {
        return type == PotionType.INSTANT;
    }

    /**
     * Check if potion has status icon
     * Client-side only
     *
     * @return true if has icon
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasStatusIcon() {
        return hasStatusIcon;
    }

    /**
     * Apply potion effect to entity
     * <p>
     * Called each tick (or once for instant effects)
     * Override this to implement custom effect logic
     *
     * @param entity    The affected entity
     * @param amplifier Potion amplifier (0 = level 1, 1 = level 2, etc.)
     */
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        // Default: no effect (subclasses should override)
        // Attribute modifiers are applied automatically by Minecraft
    }

    /**
     * Check if potion effect is ready to be applied
     * <p>
     * Override this to control when performEffect is called
     * Default: every 20 ticks (1 second)
     *
     * @param duration  Current duration
     * @param amplifier Potion amplifier
     * @return true if effect should be applied
     */
    @Override
    public boolean isReady(int duration, int amplifier) {
        // Default: apply every 20 ticks (1 second) for continuous effects
        if (type == PotionType.INSTANT) {
            return true;
        }
        int interval = 20 >> amplifier; // Higher amplifier = more frequent
        return interval > 0 && duration % interval == 0;
    }

    // ========== Utility Methods ==========

    /**
     * Get next available potion ID
     * Useful for checking ID allocation
     *
     * @return Next ID that will be assigned
     */
    public static int getNextAvailableId() {
        return nextAvailableId;
    }

    /**
     * Reset ID counter
     * <p>
     * <b>WARNING:</b> Use with extreme caution!
     * Can cause ID conflicts if misused.
     *
     * @param startId New starting ID
     */
    public static void resetIdCounter(int startId) {
        nextAvailableId = startId;
    }

    /**
     * Get potion type as string
     *
     * @return Type name (lowercase)
     */
    protected String getTypeString() {
        return type.name()
            .toLowerCase();
    }

    /**
     * Check if this is a beneficial potion
     *
     * @return true if beneficial (not bad effect)
     */
    public boolean isBeneficial() {
        return !isBadEffect();
    }

    /**
     * Get formatted potion name with level
     *
     * @param amplifier Potion amplifier (0-127)
     * @return Localized name with level (e.g., "Potion of Celestial II")
     */
    public String getFormattedName(int amplifier) {
        String name = net.minecraft.util.StatCollector.translateToLocal(getPotionNameKey());
        if (amplifier > 0) {
            name += " " + net.minecraft.util.StatCollector.translateToLocal("enchantment.level." + (amplifier + 1));
        }
        return name;
    }
}
