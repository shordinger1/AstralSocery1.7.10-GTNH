/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Colored Lens Item - Tint crystal lenses with different colors
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Colored Lens Item
 * <p>
 * Tints crystal lenses with different colors:
 * - 7 color types (one for each dye color)
 * - Can be engraved (NBT flag)
 * - Entity interaction (tint entities)
 * - Block interaction (tint blocks)
 * - Strength-based effects
 * <p>
 * Features:
 * - Metadata-based colors
 * - Engraved state (NBT)
 * - Beam intensity system (0.0 - 1.0)
 * - Dispenser support
 * <p>
 * TODO:
 * - Implement engraving system
 * - Implement entity tinting
 * - Implement block tinting
 * - Add beam strength calculation
 * - Link with starlight network
 */
public class ItemColoredLens extends AstralBaseItem {

    private static final String TAG_ENGRAVED = "isEngraved";
    private static final String TAG_STRENGTH = "beamStrength";

    @SideOnly(Side.CLIENT)
    private IIcon iconLens;

    @SideOnly(Side.CLIENT)
    private IIcon iconGlassLens;

    public ItemColoredLens() {
        super();
        setHasSubtypes(true);
        setMaxStackSize(64);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        // All variants use the same unlocalized name
        // The color is only for visual rendering
        return super.getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        // Use centralized icon registration from ResourceConfig
        iconLens = IconHelper.registerIconFromConfig(register, "itemcoloredlens");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        // All variants use the same texture
        return iconLens;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        // Add non-engraved lenses
        for (LensColor color : LensColor.values()) {
            list.add(new ItemStack(item, 1, color.ordinal()));
        }
        // TODO: Add engraved variants (if needed in creative)
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        // TODO: Implement block tinting logic
        // LensColor color = LensColor.byMetadata(stack.getItemDamage());
        // float strength = getBeamStrength(stack);
        // tintBlock(world, x, y, z, color, strength);

        return true;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player,
        net.minecraft.entity.EntityLivingBase target) {
        // TODO: Implement entity tinting logic
        // LensColor color = LensColor.byMetadata(stack.getItemDamage());
        // float strength = getBeamStrength(stack);
        // tintEntity(target, color, strength);
        return true;
    }

    /**
     * Check if lens is engraved
     */
    public boolean isEngraved(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_ENGRAVED)) {
            return nbt.getBoolean(TAG_ENGRAVED);
        }
        return false;
    }

    /**
     * Set engraved state
     */
    public void setEngraved(ItemStack stack, boolean engraved) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setBoolean(TAG_ENGRAVED, engraved);
    }

    /**
     * Get beam strength (0.0 - 1.0)
     */
    public float getBeamStrength(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_STRENGTH)) {
            return nbt.getFloat(TAG_STRENGTH);
        }
        return 0.5F; // Default strength
    }

    /**
     * Set beam strength
     */
    public void setBeamStrength(ItemStack stack, float strength) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setFloat(TAG_STRENGTH, Math.max(0.0F, Math.min(1.0F, strength)));
    }

    /**
     * Lens color enum
     */
    public enum LensColor {

        WHITE, // 0
        ORANGE, // 1
        MAGENTA, // 2
        LIGHT_BLUE, // 3
        YELLOW, // 4
        LIME, // 5
        PINK // 6
        ;

        public static LensColor byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                return WHITE;
            }
            return values()[meta];
        }
    }
}
