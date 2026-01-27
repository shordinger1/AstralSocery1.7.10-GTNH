/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet.registry;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.WeightedRandom;

import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantmentRegistry
 * Created by HellFirePvP
 * Date: 27.01.2018 / 17:43
 */
public class AmuletEnchantmentRegistry implements ConfigDataAdapter<WeightedAmuletEnchantment> {

    private static final Random rand = new Random();
    public static final AmuletEnchantmentRegistry INSTANCE = new AmuletEnchantmentRegistry();

    private static List<WeightedAmuletEnchantment> possibleEnchants = new LinkedList<>();

    private AmuletEnchantmentRegistry() {}

    @Override
    public Iterable<WeightedAmuletEnchantment> getDefaultDataSets() {
        List<WeightedAmuletEnchantment> enchantments = new LinkedList<>();
        // 1.7.10: Iterate through Enchantment.enchantmentsList instead of ForgeRegistries
        for (Enchantment e : Enchantment.enchantmentsList) {
            if (e != null) {
                // if (!e.isCurse()) { // Cause fck curses on this.
                // Enchantment.Rarity rarity = e.getRarity();
                // enchantments.add(new WeightedAmuletEnchantment(e, rarity == null ? 5 : rarity.getWeight()));
                // }
                enchantments.add(new WeightedAmuletEnchantment(e, 5));
            }
        }
        return enchantments;
    }

    @Nullable
    public static Enchantment getRandomEnchant() {
        if (possibleEnchants == null || possibleEnchants.isEmpty() ) {
            return null;
        }
        return WeightedRandom.getRandomItem(rand, possibleEnchants)
            .getEnchantment();
    }

    public static boolean canBeInfluenced(Enchantment ench) {
        for (WeightedAmuletEnchantment e : possibleEnchants) {
            if (e.getEnchantment()
                .equals(ench)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LoadPhase getLoadPhase() {
        return LoadPhase.INIT;
    }

    @Override
    public String getDataFileName() {
        return "amulet_enchantments";
    }

    @Override
    public String getDescription() {
        return "Defines a whitelist of which enchantments can be rolled and buffed by the enchantment-amulet. The higher the weight, the more likely that roll is selected."
            + "Format: <enchantment-registry-name>:<weight>";
    }

    @Nullable
    @Override
    public WeightedAmuletEnchantment appendDataSet(String str) {
        WeightedAmuletEnchantment ench = WeightedAmuletEnchantment.deserialize(str);
        if (ench == null) {
            return null;
        }
        possibleEnchants.add(ench);
        return ench;
    }

    @Override
    public void resetRegistry() {
        possibleEnchants.clear();
    }
}
