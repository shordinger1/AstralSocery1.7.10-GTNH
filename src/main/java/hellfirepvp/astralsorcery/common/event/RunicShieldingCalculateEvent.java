/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RunicShieldingCalculateEvent
 * Created by HellFirePvP
 * Date: 17.11.2018 / 11:01
 */
public class RunicShieldingCalculateEvent extends Event {

    private final EntityPlayer player;
    private final DamageSource source;
    private final float amount;
    private float runicShielding;

    public RunicShieldingCalculateEvent(EntityPlayer player, DamageSource source, float amount) {
        this.player = player;
        this.source = source;
        this.amount = amount;
        this.runicShielding = 0;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public float getRunicShielding() {
        return runicShielding;
    }

    public void setRunicShielding(float runicShielding) {
        this.runicShielding = runicShielding;
    }

    public void addRunicShielding(float add) {
        this.runicShielding += add;
    }

}
