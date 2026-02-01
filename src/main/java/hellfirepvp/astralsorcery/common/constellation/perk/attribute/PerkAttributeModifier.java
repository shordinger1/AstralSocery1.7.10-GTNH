/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk attribute modifier - Attribute modifier with value and mode
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Perk attribute modifier - Attribute modifier with value and mode (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Three modes: ADDITION, ADDED_MULTIPLY, STACKING_MULTIPLY</li>
 * <li>Value calculation</li>
 * <li>Localized display strings</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Uses StatCollector instead of I18n</li>
 * <li>Uses EnumChatFormatting instead of TextFormatting</li>
 * <li>Simplified without PlayerProgress dependency</li>
 * </ul>
 */
public class PerkAttributeModifier {

    private static long counter = 0;

    private long id;
    protected final Mode mode;
    protected final String attributeType;
    protected float value;

    // Cannot be converted to anything else.
    private boolean absolute = false;

    /**
     * Create a new modifier
     *
     * @param type  The attribute type
     * @param mode  The modifier mode
     * @param value The modifier value
     */
    public PerkAttributeModifier(String type, Mode mode, float value) {
        this.id = counter;
        counter++;
        this.attributeType = type;
        this.mode = mode;
        this.value = value;
        initModifier();
    }

    /**
     * Get modifier ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set modifier ID
     */
    protected void setId(long id) {
        this.id = id;
    }

    /**
     * Initialize modifier
     * Override in subclasses for custom initialization
     */
    protected void initModifier() {}

    /**
     * Set absolute flag
     */
    protected void setAbsolute() {
        this.absolute = true;
    }

    /**
     * Multiply value
     */
    void multiplyValue(double multiplier) {
        if (mode == Mode.STACKING_MULTIPLY) {
            this.value = ((this.value - 1F) * ((float) multiplier)) + 1F;
        } else {
            this.value *= multiplier;
        }
    }

    /**
     * Get flat value
     */
    public final float getFlatValue() {
        return value;
    }

    /**
     * Get value for player
     */
    public float getValue(EntityPlayer player) {
        return getFlatValue();
    }

    /**
     * Get value for display (client-side)
     */
    @SideOnly(Side.CLIENT)
    public float getValueForDisplay(EntityPlayer player) {
        return getValue(player);
    }

    /**
     * Get mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Get attribute type
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * Resolve attribute type
     */
    @Nullable
    public PerkAttributeType resolveType() {
        // TODO: Implement AttributeTypeRegistry when ready
        return null;
    }

    /**
     * Get unlocalized attribute name
     */
    protected String getUnlocalizedAttributeName() {
        PerkAttributeType type;
        if ((type = resolveType()) != null) {
            return type.getUnlocalizedName();
        }
        return "???";
    }

    /**
     * Check if has display string
     */
    @SideOnly(Side.CLIENT)
    public boolean hasDisplayString() {
        PerkAttributeType type;
        if ((type = resolveType()) != null) {
            String unloc = type.getUnlocalizedName();
            String translated = StatCollector.translateToLocal(unloc);
            return !translated.equals(unloc);
        }
        return false;
    }

    /**
     * Get localized attribute value
     */
    @SideOnly(Side.CLIENT)
    public String getLocalizedAttributeValue() {
        return getMode().stringifyValue(getValueForDisplay(Minecraft.getMinecraft().thePlayer));
    }

    /**
     * Get localized modifier name
     */
    @SideOnly(Side.CLIENT)
    public String getLocalizedModifierName() {
        return StatCollector.translateToLocal(
            getMode().getUnlocalizedModifierName(getValueForDisplay(Minecraft.getMinecraft().thePlayer)));
    }

    /**
     * Get attribute display format
     */
    @SideOnly(Side.CLIENT)
    public String getAttributeDisplayFormat() {
        return StatCollector.translateToLocal("perk.modifier.format");
    }

    /**
     * Get localized display string
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    public String getLocalizedDisplayString() {
        if (!hasDisplayString()) {
            return null;
        }
        return String.format(
            getAttributeDisplayFormat(),
            getLocalizedAttributeValue(),
            getLocalizedModifierName(),
            StatCollector.translateToLocal(getUnlocalizedAttributeName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeModifier that = (PerkAttributeModifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    /**
     * Modifier mode enum
     */
    public static enum Mode {

        ADDITION,
        ADDED_MULTIPLY,
        STACKING_MULTIPLY;

        /**
         * Stringify value for display
         */
        public String stringifyValue(float number) {
            if (this == ADDITION) {
                String str = Integer.toString(Math.round(number));
                if (number > 0) {
                    str = "+" + str;
                }
                return str;
            } else {
                int nbr = Math.round(number * 100);
                return Integer.toString(Math.abs(this == STACKING_MULTIPLY ? 100 - nbr : nbr));
            }
        }

        /**
         * Get unlocalized modifier name based on value
         */
        public String getUnlocalizedModifierName(float number) {
            boolean positive;
            if (this == ADDITION) {
                positive = number > 0; // 0 would be kinda... weird as addition/subtraction modifier...
            } else {
                int nbr = Math.round(number * 100);
                positive = this == STACKING_MULTIPLY ? nbr > 100 : nbr > 0;
            }
            return getUnlocalizedModifierName(positive);
        }

        /**
         * Get unlocalized modifier name
         */
        public String getUnlocalizedModifierName(boolean positive) {
            String base = positive ? "perk.modifier.%s.add" : "perk.modifier.%s.sub";
            return String.format(base, name().toLowerCase());
        }

    }

}
