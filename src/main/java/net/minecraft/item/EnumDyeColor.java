/*******************************************************************************
 * Compatibility Enum for EnumDyeColor (1.12.2) in 1.7.10
 * In 1.7.10, dyes are represented by int damage values (0-15)
 * This enum provides 1.12.2 API compatibility for 1.7.10
 ******************************************************************************/

package net.minecraft.item;

import hellfirepvp.astralsorcery.common.migration.IStringSerializable;

/**
 * Enum representing the 16 dye colors in Minecraft.
 * Maps to the damage values used in 1.7.10 ItemDye (0-15).
 */
public enum EnumDyeColor implements IStringSerializable {

    WHITE(15, "white", 15790320, "WHITE"),
    ORANGE(14, "orange", 15435844, "STAINED_GLASS_ORANGE"),
    MAGENTA(13, "magenta", 12801229, "STAINED_GLASS_MAGENTA"),
    LIGHT_BLUE(12, "light_blue", 6719955, "STAINED_GLASS_LIGHT_BLUE"),
    YELLOW(11, "yellow", 14602026, "STAINED_GLASS_YELLOW"),
    LIME(10, "lime", 4312372, "STAINED_GLASS_LIME"),
    PINK(9, "pink", 14188952, "STAINED_GLASS_PINK"),
    GRAY(8, "gray", 4408131, "STAINED_GLASS_GRAY"),
    SILVER(7, "silver", 11250603, "STAINED_GLASS_SILVER"),
    CYAN(6, "cyan", 2651799, "STAINED_GLASS_CYAN"),
    PURPLE(5, "purple", 8073150, "STAINED_GLASS_PURPLE"),
    BLUE(4, "blue", 2437522, "STAINED_GLASS_BLUE"),
    BROWN(3, "brown", 5320730, "STAINED_GLASS_BROWN"),
    GREEN(2, "green", 3887386, "STAINED_GLASS_GREEN"),
    RED(1, "red", 11743532, "STAINED_GLASS_RED"),
    BLACK(0, "black", 1973019, "STAINED_GLASS_BLACK");

    private final int meta;
    private final String name;
    private final int colorValue;
    private final String translationKey;

    EnumDyeColor(int meta, String name, int colorValue, String translationKey) {
        this.meta = meta;
        this.name = name;
        this.colorValue = colorValue;
        this.translationKey = translationKey;
    }

    /**
     * Get the damage/metadata value for this dye color (0-15)
     */
    public int getDyeDamage() {
        return this.meta;
    }

    /**
     * Get the name of this dye color
     */
    public String getUnlocalizedName() {
        return this.name;
    }

    /**
     * Get the color value (RGB) for this dye
     */
    public int getColorValue() {
        return this.colorValue;
    }

    /**
     * Get dye color from metadata value
     */
    public static EnumDyeColor byDyeDamage(int meta) {
        if (meta < 0 || meta >= values().length) {
            return WHITE;
        }
        // In 1.7.10, dye damage is 0-15 (0=black, 15=white)
        // We need to reverse the lookup
        for (EnumDyeColor color : values()) {
            if (color.meta == meta) {
                return color;
            }
        }
        return WHITE;
    }

    /**
     * Get dye color from name
     */
    public static EnumDyeColor byName(String name) {
        for (EnumDyeColor color : values()) {
            if (color.name.equalsIgnoreCase(name)) {
                return color;
            }
        }
        return WHITE;
    }

    @Override
    public String getName() {
        return this.name.toLowerCase();
    }
}
