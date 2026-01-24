/*******************************************************************************
 * Compatibility Enum for EnumPlayerModelParts (1.12.2) in 1.7.10
 * Represents player model parts (skin layers) for rendering
 ******************************************************************************/

package net.minecraft.client.model;

/**
 * Enum representing the different model parts of a player that can be shown/hidden.
 * In 1.7.10, this is used for skin layer customization.
 */
public enum EnumPlayerModelParts {

    CAPE(0, "cape"),
    JACKET(1, "jacket"),
    LEFT_SLEEVE(2, "left_sleeve"),
    RIGHT_SLEEVE(3, "right_sleeve"),
    LEFT_PANTS_LEG(4, "left_pants_leg"),
    RIGHT_PANTS_LEG(5, "right_pants_leg"),
    HAT(6, "hat");

    private final int partId;
    private final String partName;
    private static final EnumPlayerModelParts[] PART_LOOKUP = new EnumPlayerModelParts[values().length];

    static {
        for (EnumPlayerModelParts part : values()) {
            PART_LOOKUP[part.partId] = part;
        }
    }

    EnumPlayerModelParts(int partId, String partName) {
        this.partId = partId;
        this.partName = partName;
    }

    public int getPartId() {
        return this.partId;
    }

    public String getPartName() {
        return this.partName;
    }

    public static EnumPlayerModelParts fromPartId(int id) {
        if (id < 0 || id >= PART_LOOKUP.length) {
            return null;
        }
        return PART_LOOKUP[id];
    }
}
