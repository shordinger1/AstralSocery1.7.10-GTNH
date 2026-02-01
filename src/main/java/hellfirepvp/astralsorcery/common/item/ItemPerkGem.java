/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk Gem Item - Contains random attribute modifiers
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
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
 * Perk Gem Item
 * <p>
 * Contains random attribute modifiers that can be applied to players.
 * <p>
 * Features:
 * - 3 constellation types: DAY, NIGHT, SKY (metadata-based)
 * - 3 gem quality levels (LESSER, GREATER, GREATER_CRYSTAL) - NBT-based
 * - Auto-generated attributes
 * - Research threshold requirement
 * - Attribute modifiers stored in NBT
 * <p>
 * TODO:
 * - Implement attribute generation
 * - Link with perk system
 * - Implement research threshold
 * - Add attribute modifiers
 */
public class ItemPerkGem extends AstralBaseItem {

    private static final String TAG_GEM_TYPE = "gem_type";
    private static final String TAG_ATTRIBUTES = "attributes";

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public enum GemType {
        LESSER, // Basic perks
        GREATER, // Advanced perks
        GREATER_CRYSTAL // Crystal-enhanced perks
    }

    public ItemPerkGem() {
        super(64); // Max stack size 64
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        ConstellationType type = ConstellationType.byMetadata(stack.getItemDamage());
        return super.getUnlocalizedName() + "." + type.getSuffix();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        // Use centralized icon registration from ResourceConfig
        icons = IconHelper.registerIconsFromConfig(register, "itemperkgem");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return IconHelper.getIcon(icons, damage);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && !hasGemType(stack)) {
            // TODO: Generate random attributes
            // GemType type = generateRandomType();
            // setGemType(stack, type);
            // generateAttributes(stack, type);
        }
    }

    /**
     * Get gem type
     */
    public GemType getGemType(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_GEM_TYPE)) {
            int typeOrdinal = nbt.getInteger(TAG_GEM_TYPE);
            return GemType.values()[typeOrdinal];
        }
        return GemType.LESSER; // Default
    }

    /**
     * Set gem type
     */
    public void setGemType(ItemStack stack, GemType type) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(TAG_GEM_TYPE, type.ordinal());
    }

    /**
     * Check if has gem type
     */
    public boolean hasGemType(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey(TAG_GEM_TYPE);
    }

    /**
     * Get attribute list
     */
    public NBTTagList getAttributes(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_ATTRIBUTES)) {
            return nbt.getTagList(TAG_ATTRIBUTES, 10); // 10 = NBTTagCompound
        }
        return new NBTTagList();
    }

    /**
     * Set attribute list
     */
    public void setAttributes(ItemStack stack, NBTTagList attributes) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setTag(TAG_ATTRIBUTES, attributes);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        GemType type = getGemType(stack);
        switch (type) {
            case GREATER_CRYSTAL:
                return EnumRarity.epic;
            case GREATER:
                return EnumRarity.rare;
            default:
                return EnumRarity.uncommon;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        GemType type = getGemType(stack);
        tooltip.add("§7Gem Type: §e" + type.name());

        NBTTagList attributes = getAttributes(stack);
        int count = attributes.tagCount();
        if (count > 0) {
            tooltip.add("§7Attributes (" + count + "):");
            for (int i = 0; i < Math.min(count, 5); i++) {
                NBTTagCompound attr = attributes.getCompoundTagAt(i);
                String name = attr.getString("name");
                String value = attr.getString("value");
                tooltip.add("  §b" + name + ": §f" + value);
            }
            if (count > 5) {
                tooltip.add("  §7... and " + (count - 5) + " more");
            }
        }
    }

    /**
     * Constellation type enum (metadata-based)
     */
    public enum ConstellationType {

        DAY(0, "day"),
        NIGHT(1, "night"),
        SKY(2, "sky");

        private final int metadata;
        private final String suffix;

        ConstellationType(int metadata, String suffix) {
            this.metadata = metadata;
            this.suffix = suffix;
        }

        public int getMetadata() {
            return metadata;
        }

        public String getSuffix() {
            return suffix;
        }

        public static ConstellationType byMetadata(int meta) {
            for (ConstellationType type : values()) {
                if (type.metadata == meta) {
                    return type;
                }
            }
            return DAY;
        }
    }
}
