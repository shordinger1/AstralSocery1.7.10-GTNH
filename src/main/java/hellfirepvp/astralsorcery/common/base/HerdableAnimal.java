/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.util.EntityUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: HerdableAnimal
 * Created by HellFirePvP
 * Date: 13.11.2016 / 16:43
 */
public interface HerdableAnimal<T extends EntityLivingBase> {

    static Deque<HerdableAnimal> registryHerdable = new LinkedList<>();
    static List<ResourceLocation> blacklistedEntities = new LinkedList<>();

    @Nullable
    public static <T extends EntityLivingBase> HerdableAnimal getHerdable(T entity) {
        // 1.7.10: Use EntityList.classToStringMapping to get entity registry name
        String entityString = (String) EntityList.classToStringMapping.get(entity.getClass());
        ResourceLocation name = entityString != null ? new ResourceLocation(entityString) : null;
        if (name != null && blacklistedEntities.contains(name)) {
            return null;
        }
        for (HerdableAnimal herd : registryHerdable) {
            if (herd.handles(entity)) {
                return herd;
            }
        }
        return null;
    }

    public static void init() {
        register(new Squid());

        // Register last! Least generic -> most generic...
        register(new GenericAnimal());
    }

    public static void register(HerdableAnimal herd) {
        registryHerdable.addLast(herd);
    }

    public <E extends EntityLivingBase> boolean handles(@Nonnull E entity);

    @Nonnull
    public List<ItemStack> getHerdingDropsTick(T entity, World world, Random rand, float herdingLuck);

    @Nonnull
    default <L extends EntityLiving> List<ItemStack> extractLootTable(L entity, World world, Random rand,
        float herdingLuck) {
        // 1.7.10: LootTable and LootContext don't exist
        // Loot tables are handled differently in 1.7.10 - drop items directly or use deprecated methods
        Object table = EntityUtils.getLootTable(entity);
        if (table == null) {
            return Lists.newArrayList();
        }
        // 1.7.10: No loot table generation, return empty list
        // Entities will drop items through their normal drop mechanism
        return Lists.newArrayList();
    }

    public static class GenericAnimal implements HerdableAnimal<EntityAnimal> {

        @Override
        public <E extends EntityLivingBase> boolean handles(@Nonnull E entity) {
            return EntityAnimal.class.isAssignableFrom(entity.getClass());
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityAnimal entity, World world, Random rand, float herdingLuck) {
            return extractLootTable(entity, world, rand, herdingLuck);
        }
    }

    public static class Squid implements HerdableAnimal<EntitySquid> {

        @Override
        public <E extends EntityLivingBase> boolean handles(@Nonnull E entity) {
            return EntitySquid.class.isAssignableFrom(entity.getClass());
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntitySquid entity, World world, Random rand, float herdingLuck) {
            return extractLootTable(entity, world, rand, herdingLuck);
        }
    }

    public static class HerdableAdapter implements ConfigDataAdapter<ConfigDataAdapter.DataSet.StringElement> {

        public static final HerdableAdapter INSTANCE = new HerdableAdapter();

        private HerdableAdapter() {}

        @Override
        public Iterable<ConfigDataAdapter.DataSet.StringElement> getDefaultDataSets() {
            return Collections.emptyList();
        }

        @Override
        public String getDataFileName() {
            return "herdable_animals_blacklist";
        }

        @Override
        public String getDescription() {
            return "Defines a list of animals that can not be used in an bootes ritual to gain drops from. List animals with their registry name "
                + "(e.g. parrots would be 'minecraft:parrot')";
        }

        @Nullable
        @Override
        public ConfigDataAdapter.DataSet.StringElement appendDataSet(String str) {
            blacklistedEntities.add(new ResourceLocation(str));
            return new DataSet.StringElement(str);
        }

        @Override
        public void resetRegistry() {
            blacklistedEntities.clear();
        }
    }

}
