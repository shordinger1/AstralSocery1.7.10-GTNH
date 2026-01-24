/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.BlockEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyVoidTrash
 * Created by HellFirePvP
 * Date: 24.11.2018 / 22:22
 */
public class KeyVoidTrash extends KeyPerk {

    private static final Random rand = new Random();
    private static String[] defaultDropList = new String[] { "minecraft:stone:0", "minecraft:dirt",
        "minecraft:cobblestone", "minecraft:gravel" };

    // 1.7.10: Store Item instances and metadata for comparison
    private static class ItemFilter {

        final Item item;
        final Integer metadata; // null means any metadata

        ItemFilter(Item item, Integer metadata) {
            this.item = item;
            this.metadata = metadata;
        }

        boolean matches(ItemStack stack) {
            if (stack == null || stack.getItem() != item) {
                return false;
            }
            if (metadata == null) {
                return true; // Any metadata matches
            }
            return stack.getItemDamage() == metadata;
        }
    }

    private static List<ItemFilter> dropFilter = Lists.newArrayList();
    private static float chanceOre = 0.0002F;

    public KeyVoidTrash(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                dropFilter.clear();

                String[] drops = cfg.getStringList(
                    "DropList",
                    getConfigurationSection(),
                    defaultDropList,
                    "The list of items to delete when dropped by a player with this perk. "
                        + "Damage/metadata value is optional and 'any' damage value is matched if omitted. "
                        + "Format: <modid>:<name>(:<metadata>)");
                chanceOre = cfg.getFloat(
                    "DropRareInstead",
                    getConfigurationSection(),
                    chanceOre,
                    0F,
                    1F,
                    "Chance that a voided drop will instead yield a "
                        + "valuable random ore out of the 'perk_void_trash_replacement' configured ore table.");

                for (String s : drops) {
                    String[] split = s.split(":");
                    if (split.length == 3) {
                        int dmg;
                        try {
                            dmg = Integer.parseInt(split[2]);
                        } catch (Exception ex) {
                            continue;
                        }
                        Item item = GameRegistry.findItem(split[0], split[1]);
                        if (item != null) {
                            dropFilter.add(new ItemFilter(item, dmg));
                        }
                    } else if (split.length == 2) {
                        Item item = GameRegistry.findItem(split[0], split[1]);
                        if (item != null) {
                            dropFilter.add(new ItemFilter(item, null));
                        }
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public void onDrops(BlockEvent.HarvestDropsEvent ev) {
        World world = ev.world;
        if (world.isRemote) {
            return;
        }

        EntityPlayer player = ev.harvester;
        if (player == null) {
            return;
        }

        PlayerProgress prog = ResearchManager.getProgress(player, Side.SERVER);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        float chance = PerkAttributeHelper.getOrCreateMap(player, Side.SERVER)
            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, chanceOre);
        List<ItemStack> drops = ev.drops;
        List<ItemStack> addedDrops = Lists.newArrayList();
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if ((stack == null || stack.stackSize <= 0)) {
                continue;
            }

            // 1.7.10: Check against stored ItemFilters
            boolean matches = false;
            for (ItemFilter filter : dropFilter) {
                if (filter.matches(stack)) {
                    matches = true;
                    break;
                }
            }

            if (matches) {
                iterator.remove();

                if (rand.nextFloat() < chance) {
                    ItemStack drop = OreTypes.PERK_VOID_TRASH_REPLACEMENT.getRandomOre(rand);
                    if (!(drop == null || drop.stackSize <= 0)) {
                        addedDrops.add(ItemUtils.copyStackWithSize(drop, 1));
                    }
                }
            }
        }
        drops.addAll(addedDrops);
    }

}
