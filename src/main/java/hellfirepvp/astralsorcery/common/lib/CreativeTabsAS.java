/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Creative tab for Astral Sorcery
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Creative tab for Astral Sorcery items
 *
 * Provides a dedicated creative tab with custom icon.
 */
public class CreativeTabsAS extends CreativeTabs {

    /**
     * Astral Sorcery creative tab instance
     */
    public static final CreativeTabs ASTRAL_SORCERY_TAB = new CreativeTabsAS(
        CreativeTabs.getNextID(),
        Constants.CREATIVE_TAB_NAME);

    /**
     * Icon item for the tab
     */
    private static Item iconItem;

    /**
     * Icon item metadata
     */
    private static int iconMeta;

    /**
     * Constructor
     *
     * @param id   The tab ID
     * @param name The tab name (unlocalized)
     */
    public CreativeTabsAS(int id, String name) {
        super(id, name);
    }

    /**
     * Set the icon item for this tab
     *
     * @param item The icon item
     */
    public static void setIconItem(Item item) {
        setIconItem(item, 0);
    }

    /**
     * Set the icon item with metadata for this tab
     *
     * @param item The icon item
     * @param meta The metadata
     */
    public static void setIconItem(Item item, int meta) {
        iconItem = item;
        iconMeta = meta;
    }

    @Override
    public Item getTabIconItem() {
        if (iconItem == null) {
            // Fallback to a default item
            // This will be replaced when we have actual items
            return Item.getItemFromBlock(net.minecraft.init.Blocks.bookshelf);
        }
        return iconItem;
    }

    @Override
    public ItemStack getIconItemStack() {
        if (iconItem == null) {
            // Fallback to a default item
            return new ItemStack(net.minecraft.init.Blocks.bookshelf);
        }
        return new ItemStack(iconItem, 1, iconMeta);
    }

    /**
     * Get the translated tab name
     *
     * @return The localized name
     */
    @Override
    public String getTranslatedTabLabel() {
        return Constants.MODNAME;
    }
}
