/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Infused Glass Item - Carved star glass, records discovered constellations
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Infused Glass Item
 * <p>
 * Carved star glass that records discovered constellations.
 * <p>
 * Features:
 * - Two variants: normal (0) and engraved/active (1)
 * - Durability system
 * - Enchantment support
 * - NBT model switching
 * - Constellation recording
 * <p>
 * TODO:
 * - Implement constellation recording
 * - Implement durability protection
 * - Link with telescope system
 * - Add model switching
 */
public class ItemInfusedGlass extends AstralBaseItem {

    private static final String TAG_CONSTELLATIONS = "recorded_constellations";
    private static final int MAX_DURABILITY = 100;

    @SideOnly(Side.CLIENT)
    private IIcon iconNormal;

    @SideOnly(Side.CLIENT)
    private IIcon iconEngraved;

    public ItemInfusedGlass() {
        super(1); // Max stack size 1
        setHasSubtypes(true);
        setMaxDamage(MAX_DURABILITY);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        GlassType type = GlassType.byMetadata(stack.getItemDamage());
        return super.getUnlocalizedName() + "." + type.getSuffix();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        // Use centralized icon registration from ResourceConfig
        IIcon[] icons = IconHelper.registerIconsFromConfig(register, "iteminfusedglass");
        if (icons.length >= 2) {
            iconNormal = icons[0];
            iconEngraved = icons[1];
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        GlassType type = GlassType.byMetadata(damage);
        return type == GlassType.ENGRAVED ? iconEngraved : iconNormal;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        // TODO: Return custom use action
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000; // 1 hour (like telescope)
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        if (!world.isRemote) {
            // TODO: Check if constellation was discovered
            // TODO: Record constellation to NBT
        }
    }

    /**
     * Get recorded constellations
     */
    public NBTTagList getConstellations(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_CONSTELLATIONS)) {
            return nbt.getTagList(TAG_CONSTELLATIONS, 8); // 8 = String tag
        }
        return new NBTTagList();
    }

    /**
     * Add constellation
     */
    public void addConstellation(ItemStack stack, String constellation) {
        NBTTagList list = getConstellations(stack);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setString("constellation", constellation);
        list.appendTag(nbtTagCompound);

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setTag(TAG_CONSTELLATIONS, list);
    }

    /**
     * Get constellation count
     */
    public int getConstellationCount(ItemStack stack) {
        return getConstellations(stack).tagCount();
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack material) {
        // TODO: Check if repair material is valid
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        NBTTagList constellations = getConstellations(stack);
        int count = constellations.tagCount();

        if (count > 0) {
            tooltip.add("§7Constellations: §e" + count);
            // Add constellation names
            for (int i = 0; i < Math.min(count, 5); i++) {
                NBTTagCompound tag = constellations.getCompoundTagAt(i);
                String name = tag.getString("constellation");
                tooltip.add("  §b" + name);
            }
            if (count > 5) {
                tooltip.add("  §7... and " + (count - 5) + " more");
            }
        } else {
            tooltip.add("§7Constellations: §cNone");
        }
    }

    /**
     * Glass type enum
     */
    public enum GlassType {

        NORMAL(0, ""),
        ENGRAVED(1, "active");

        private final int metadata;
        private final String suffix;

        GlassType(int metadata, String suffix) {
            this.metadata = metadata;
            this.suffix = suffix;
        }

        public int getMetadata() {
            return metadata;
        }

        public String getSuffix() {
            return suffix;
        }

        public static GlassType byMetadata(int meta) {
            for (GlassType type : values()) {
                if (type.metadata == meta) {
                    return type;
                }
            }
            return NORMAL;
        }
    }
}
