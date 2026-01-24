/*******************************************************************************
 * Compatibility Enum for EntityEquipmentSlot (1.12.2) in 1.7.10
 * Represents equipment slots for entities (mainhand, offhand, armor slots)
 ******************************************************************************/

package net.minecraft.inventory;

/**
 * Enum representing equipment slots where items can be placed.
 * 1.7.10 compatibility version of the 1.12.2 EntityEquipmentSlot enum.
 */
public enum EntityEquipmentSlot {

    MAINHAND(EnumInventorySlotType.HAND, 0, "mainhand"),
    OFFHAND(EnumInventorySlotType.HAND, 1, "offhand"),
    FEET(EnumInventorySlotType.ARMOR, 0, "feet"),
    LEGS(EnumInventorySlotType.ARMOR, 1, "legs"),
    CHEST(EnumInventorySlotType.ARMOR, 2, "chest"),
    HEAD(EnumInventorySlotType.ARMOR, 3, "head");

    private final EnumInventorySlotType slotType;
    private final int index;
    private final String name;

    EntityEquipmentSlot(EnumInventorySlotType slotType, int index, String name) {
        this.slotType = slotType;
        this.index = index;
        this.name = name;
    }

    public EnumInventorySlotType getSlotType() {
        return this.slotType;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Get the armor slot index (0-3) for vanilla armor inventory
     */
    public int getArmorIndex() {
        if (this.slotType != EnumInventorySlotType.ARMOR) {
            return 0;
        }
        // 1.7.10 armor inventory order: 0=boots, 1=leggings, 2=chestplate, 3=helmet
        // Our enum: FEET=0, LEGS=1, CHEST=2, HEAD=3
        return this.index;
    }

    public static enum EnumInventorySlotType {
        HAND,
        ARMOR
    }
}
