/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Usable Dust Item - Illumination and Nocturnal dust types
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Usable Dust Item
 * <p>
 * Two types of dust with different effects:
 * - ILLUMINATION (0): Creates light sparks or places light blocks
 * - NOCTURNAL (1): Creates nocturnal sparks
 * <p>
 * Features:
 * - Right-click in air: Spawn spark entity
 * - Right-click on block: Special interaction
 * - Dispenser support
 * <p>
 * TODO:
 * - Implement EntityIlluminationSpark
 * - Implement EntityNocturnalSpark
 * - Implement dispenser behavior (IBehaviorDispenseItem)
 * - Add block placement logic
 */
public class ItemUsableDust extends AstralBaseItem {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemUsableDust() {
        super();
        setHasSubtypes(true);
        setMaxStackSize(64);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        // For metadata variants, append the type name with period separator
        DustType type = DustType.byMetadata(stack.getItemDamage());
        return super.getUnlocalizedName() + "."
            + type.name()
                .toLowerCase();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        // For metadata variants, append the type name with period separator
        DustType type = DustType.byMetadata(stack.getItemDamage());
        // Build localization key directly: item.itemusabledust.type.name
        String key = "item.itemusabledust." + type.name()
            .toLowerCase();
        return net.minecraft.util.StatCollector.translateToLocal(key);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        // Use centralized icon registration from ResourceConfig
        icons = IconHelper.registerIconsFromConfig(register, "itemusabledust");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return IconHelper.getIcon(icons, damage);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (DustType type : DustType.values()) {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        DustType type = DustType.byMetadata(stack.getItemDamage());
        // TODO: Implement rightClickAir() for each dust type
        // For now, just consume the item
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        DustType type = DustType.byMetadata(stack.getItemDamage());
        // TODO: Implement rightClickBlock() for each dust type
        // For now, just consume the item
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        return true;
    }

    /**
     * Dust type enum
     */
    public enum DustType {

        ILLUMINATION, // 0 - Creates light
        NOCTURNAL // 1 - Night effects
        ;

        public static DustType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                return ILLUMINATION;
            }
            return values()[meta];
        }
    }
}
