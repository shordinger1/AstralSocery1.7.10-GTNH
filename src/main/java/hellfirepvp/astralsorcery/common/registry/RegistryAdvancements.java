/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import static hellfirepvp.astralsorcery.common.lib.AdvancementTriggers.*;

import hellfirepvp.astralsorcery.common.advancements.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryAdvancements
 * Created by HellFirePvP
 * Date: 27.10.2018 / 10:54
 */
public class RegistryAdvancements {

    public static void init() {
        DISCOVER_CONSTELLATION = new DiscoverConstellationTrigger();
        ATTUNE_SELF = new AttuneSelfTrigger();
        ATTUNE_CRYSTAL = new AttuneCrystalTrigger();
        ALTAR_CRAFT = new AltarCraftTrigger();
        PERK_LEVEL = new PerkLevelTrigger();
    }

}
