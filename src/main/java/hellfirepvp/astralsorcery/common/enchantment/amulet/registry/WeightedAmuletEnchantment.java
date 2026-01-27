/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.WeightedRandom;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WeightedAmuletEnchantment
 * Created by HellFirePvP
 * Date: 27.01.2018 / 17:44
 */
public class WeightedAmuletEnchantment extends WeightedRandom.Item implements ConfigDataAdapter.DataSet {

    private final Enchantment enchantment;

    public WeightedAmuletEnchantment(Enchantment ench, int weight) {
        super(weight);
        this.enchantment = ench;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    @Nonnull
    @Override
    public String serialize() {
        // In 1.7.10, use getName() instead of getRegistryName()
        return this.enchantment.getName() + ":" + itemWeight;
    }

    @Nullable
    public static WeightedAmuletEnchantment deserialize(String str) {
        String[] spl = str.split(":");
        if (spl.length < 3) {
            return null;
        }
        String domain = spl[0];
        String weight = spl[spl.length - 1];
        StringBuilder path = new StringBuilder();
        for (int i = 1; i < spl.length - 1; i++) {
            if ( path.isEmpty() ) {
                path.append(":"); // Cause that vanishes when splitting...
            }
            path.append(spl[i]);
        }
        // TODO find a better solution than hardcoding (duh)
        String registryName = domain + ":" + path.toString();
        if (registryName.equalsIgnoreCase("cofhcore:holding")) {
            AstralSorcery.log.info("Ignoring amulet enchantment 'cofhcore:holding' as it's prone to cause issues.");
            return null;
        }
        // see #1302
        if (domain.equalsIgnoreCase("dungeontactics")) {
            AstralSorcery.log.info(
                "Ignoring amulet enchantments for '" + registryName
                    + "' as dungeontactic's enchantments generated through the prism are prone to cause issues.");
            return null;
        }
        // In 1.7.10, find enchantment by iterating through enchantmentsList
        Enchantment ench = findEnchantmentByName(registryName);
        if (ench == null) {
            AstralSorcery.log
                .info("Ignoring whitelist entry " + str + " for amulet enchantments - Enchantment does not exist!");
            return null;
        }
        int w;
        try {
            w = Integer.parseInt(weight);
        } catch (NumberFormatException exc) {
            AstralSorcery.log.info(
                "Ignoring whitelist entry " + str
                    + " for amulet enchantments - last :-separated argument is not a number!");
            return null;
        }
        return new WeightedAmuletEnchantment(ench, w);
    }

    /**
     * Find an enchantment by its registry name in 1.7.10
     */
    private static Enchantment findEnchantmentByName(String registryName) {
        for (Enchantment ench : Enchantment.enchantmentsList) {
            if (ench != null && registryName.equals(ench.getName())) {
                return ench;
            }
        }
        return null;
    }

}
