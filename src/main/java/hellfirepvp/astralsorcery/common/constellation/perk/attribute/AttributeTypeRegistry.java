/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Attribute type registry - Registry for perk attribute types
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import java.util.*;

/**
 * Attribute type registry - Registry for perk attribute types (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Attribute type registration</li>
 * <li>Type lookup by string</li>
 * <li>Type limit configuration</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without Forge event bus integration</li>
 * <li>No VanillaAttributeType for now</li>
 * </ul>
 */
public class AttributeTypeRegistry {

    // Attribute type constants
    public static final String ATTR_TYPE_MELEE_DAMAGE = "astralsorcery.meleeattackdamage";
    public static final String ATTR_TYPE_HEALTH = "astralsorcery.maxhealth";
    public static final String ATTR_TYPE_MOVESPEED = "astralsorcery.movespeed";
    public static final String ATTR_TYPE_ARMOR = "astralsorcery.armor";
    public static final String ATTR_TYPE_ATTACK_SPEED = "astralsorcery.attackspeed";
    public static final String ATTR_TYPE_REACH = "astralsorcery.reach";
    public static final String ATTR_TYPE_INC_PERK_EXP = "astralsorcery.expgain";
    public static final String ATTR_TYPE_INC_PERK_EFFECT = "astralsorcery.perkeffect";
    public static final String ATTR_TYPE_MINING_SPEED = "astralsorcery.miningspeed";
    public static final String ATTR_TYPE_RANGED_DAMAGE = "astralsorcery.rangeddamage";

    private static Map<String, PerkAttributeType> typeMap = new HashMap<>();

    /**
     * Private constructor
     */
    private AttributeTypeRegistry() {}

    /**
     * Register a perk attribute type
     *
     * @param type The type to register
     */
    public static void registerPerkType(PerkAttributeType type) {
        if (typeMap.putIfAbsent(type.getTypeString(), type) == null) {
            type.init();
        }
    }

    /**
     * Get attribute type by string
     *
     * @param typeStr The type string
     * @return The attribute type, or null if not found
     */
    public static PerkAttributeType getType(String typeStr) {
        return typeMap.get(typeStr);
    }

    /**
     * Get all registered types
     *
     * @return Collection of all types
     */
    public static Collection<PerkAttributeType> getTypes() {
        return new ArrayList<>(typeMap.values());
    }

}
