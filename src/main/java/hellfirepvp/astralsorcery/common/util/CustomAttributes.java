/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Custom attributes for 1.7.10 compatibility
 * Attributes that don't exist in vanilla 1.7.10:
 * - Armor
 * - Armor Toughness
 * - Reach Distance
 */
public class CustomAttributes {

    public static final IAttribute ARMOR = new RangedAttribute("generic.armor", 0.0D, 0.0D, 30.0D)
        .setDescription("Armor");
    public static final IAttribute ARMOR_TOUGHNESS = new RangedAttribute("generic.armorToughness", 0.0D, 0.0D, 20.0D)
        .setDescription("Armor Toughness");
    public static final IAttribute REACH_DISTANCE = new RangedAttribute("generic.reachDistance", 5.0D, 0.0D, 1024.0D)
        .setDescription("Reach Distance");

    public static final CustomAttributes INSTANCE = new CustomAttributes();

    private CustomAttributes() {}

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;

            // Register custom attributes to player
            if (player.getAttributeMap()
                .getAttributeInstance(ARMOR) == null) {
                player.getAttributeMap()
                    .registerAttribute(ARMOR);
            }
            if (player.getAttributeMap()
                .getAttributeInstance(ARMOR_TOUGHNESS) == null) {
                player.getAttributeMap()
                    .registerAttribute(ARMOR_TOUGHNESS);
            }
            if (player.getAttributeMap()
                .getAttributeInstance(REACH_DISTANCE) == null) {
                player.getAttributeMap()
                    .registerAttribute(REACH_DISTANCE);
            }
        }
    }
}
