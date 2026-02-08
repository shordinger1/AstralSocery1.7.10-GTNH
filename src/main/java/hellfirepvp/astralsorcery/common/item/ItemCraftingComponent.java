/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crafting Component Item - Multiple crafting materials in one item
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Crafting Component Item
 * <p>
 * Contains 6 different crafting materials:
 * - AQUAMARINE (0): Aquamarine gem
 * - STARMETAL_INGOT (1): Starmetal ingot
 * - STARDUST (2): Stardust
 * - GLASS_LENS (3): Glass lens
 * - RESO_GEM (4): Resonance gem
 * - PARCHMENT (5): Parchment
 * <p>
 * Only STARMETAL_INGOT is grindable.
 * STARDUST has special entity rendering.
 * <p>
 * Icons are configured in ResourceConfig.java
 */
public class ItemCraftingComponent extends AstralBaseItem {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemCraftingComponent() {
        super();
        setHasSubtypes(true);
        setMaxStackSize(64);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        MetaType type = MetaType.values()[meta];
        // For metadata variants, append the lowercase enum name
        return super.getUnlocalizedName() + "."
            + type.name()
                .toLowerCase();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = stack.getItemDamage();
        MetaType type = MetaType.values()[meta];
        // Build localization key directly: item.itemcraftingcomponent.type.name
        String key = "item.itemcraftingcomponent." + type.name()
            .toLowerCase();
        return net.minecraft.util.StatCollector.translateToLocal(key);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MetaType type : MetaType.values()) {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return IconHelper.getIcon(icons, damage);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        // Use centralized icon registration from ResourceConfig
        icons = IconHelper.registerIconsFromConfig(register, "itemcraftingcomponent");
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getItemDamage() == MetaType.STARDUST.ordinal();
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        if (itemstack.getItemDamage() == MetaType.STARDUST.ordinal()) {
            // TODO: Create EntityItemStardust for special rendering
            // For now, return null to use default entity
            return null;
        }
        return null;
    }

    /**
     * Check if this item is grindable (only STARMETAL_INGOT)
     */
    public boolean isGrindable(ItemStack stack) {
        return stack.getItemDamage() == MetaType.STARMETAL_INGOT.ordinal();
    }

    /**
     * Create ItemStack for this type
     */
    public ItemStack createStack(MetaType type, int count) {
        return new ItemStack(this, count, type.ordinal());
    }

    /**
     * Metadata type enum
     */
    public enum MetaType {
        AQUAMARINE, // 0
        STARMETAL_INGOT, // 1
        STARDUST, // 2
        GLASS_LENS, // 3
        RESO_GEM, // 4
        PARCHMENT // 5
    }
}
