/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Ulteria perk - Night vision perk
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Ulteria perk - Night vision perk (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Night vision effect</li>
 * <li>Darkness tolerance</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>TODO: Implement night vision potion effect</li>
 * </ul>
 */
public class PerkUlteria extends ConstellationPerk {

    // Night vision potion ID in 1.7.10
    private static final int NIGHT_VISION_ID = 16;

    public PerkUlteria(IConstellation constellation, int x, int y) {
        super("cst_ulteria", constellation, x, y);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // Apply night vision effect on both client and server
        if (player != null) {
            Potion nightVision = Potion.potionTypes[NIGHT_VISION_ID];
            if (nightVision != null) {
                // Apply infinite night vision (duration: 32767 - maximum for display)
                player.addPotionEffect(new PotionEffect(nightVision.id, 32767, 0, false));
                LogHelper.debug("Applied night vision effect to " + player.getCommandSenderName() + " on " + side);
            }
        }
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // Remove night vision effect when perk is removed
        if (player != null) {
            Potion nightVision = Potion.potionTypes[NIGHT_VISION_ID];
            if (nightVision != null) {
                player.removePotionEffect(nightVision.id);
                LogHelper.debug("Removed night vision effect from " + player.getCommandSenderName() + " on " + side);
            }
        }
    }

}
