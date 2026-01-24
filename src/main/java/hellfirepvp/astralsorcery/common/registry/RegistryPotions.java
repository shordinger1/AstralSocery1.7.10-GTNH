/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.potion.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPotions
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 */
public class RegistryPotions {

    public static PotionCheatDeath potionCheatDeath;
    public static PotionBleed potionBleed;
    public static PotionSpellPlague potionSpellPlague;
    public static PotionDropModifier potionDropModifier;
    public static PotionTimeFreeze potionTimeFreeze;

    public static void init() {
        // In 1.7.10, potions are registered via static field initialization
        // No need for primer registration
        potionCheatDeath = new PotionCheatDeath();
        potionBleed = new PotionBleed();
        potionSpellPlague = new PotionSpellPlague();
        potionDropModifier = new PotionDropModifier();
        potionTimeFreeze = new PotionTimeFreeze();
    }

}
