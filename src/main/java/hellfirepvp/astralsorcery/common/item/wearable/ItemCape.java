/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Cape - Constellation-attuned armor piece
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.LocalizationHelper;

/**
 * Cape
 * <p>
 * A constellation-attuned cape that provides special effects.
 * <p>
 * Features:
 * - Worn in chest slot
 * - Attuned to a constellation
 * - Provides constellation-specific effects (TODO)
 * - Custom rendering (TODO)
 * - Colored based on constellation
 * <p>
 * Effects by constellation:
 * - Octans: Water breathing, knockback resistance
 * - Vicio: Elytra compatibility (TODO)
 * - Evorsio: Attack damage (TODO)
 * - And more...
 * <p>
 * TODO:
 * - Implement CapeEffect system
 * - Implement CapeEffectRegistry
 * - Implement constellation effects
 * - Implement custom cape rendering
 * - Implement attribute modifiers
 */
public class ItemCape extends ItemArmor {

    private static final String TAG_CONSTELLATION = "attuned_constellation";

    public ItemCape() {
        // TODO: Create imbuedLeatherMaterial
        super(net.minecraft.item.ItemArmor.ArmorMaterial.DIAMOND, 0, 1); // 1 = chest slot
        setMaxDamage(0); // No durability
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(net.minecraft.item.Item item, CreativeTabs tab, List<ItemStack> list) {
        if (tab == this.getCreativeTab()) {
            // Unattuned cape
            list.add(new ItemStack(item));

            // Attuned capes for each constellation
            // TODO: When ConstellationRegistry is implemented
            // for (Constellation c : ConstellationRegistry.getAllConstellations()) {
            // ItemStack stack = new ItemStack(item);
            // setConstellation(stack, c);
            // list.add(stack);
            // }
        }
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        // TODO: Apply constellation effects
        // Constellation c = getConstellation(stack);
        // if (c != null) {
        // CapeEffect effect = CapeEffectRegistry.getEffect(c);
        // if (effect != null) {
        // effect.onArmorTick(world, player, stack);
        // }
        // }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // Add general tooltip from language file
        LocalizationHelper.addItemTooltip(stack, tooltip, 2, false);

        // Show constellation attunement status
        IConstellation constellation = getConstellation(stack);
        if (constellation != null) {
            // Use localized constellation name
            String constelKey = "astralsorcery.constellation." + constellation.getUnlocalizedName();
            String constelName = LocalizationHelper.tr(constelKey);
            tooltip.add("§7Constellation: §e" + constelName);
            // TODO: Show effect description
        } else {
            tooltip.add("§7Constellation: §cNone");
            tooltip.add(LocalizationHelper.tr("item.itemcape.tooltip.attune"));
        }
    }

    public boolean getIsRepairable(ItemStack stack, ItemStack material) {
        // TODO: Check if material is stardust
        return false;
    }

    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        // TODO: Return custom cape texture
        return "astralsorcery:textures/models/cape.png";
    }

    /**
     * Get attuned constellation
     */
    public static IConstellation getConstellation(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return null;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        String constellationName = nbt.getString(TAG_CONSTELLATION);

        if (constellationName.isEmpty()) {
            return null;
        }

        // TODO: Lookup constellation from registry
        // return ConstellationRegistry.getByName(constellationName);
        return null;
    }

    /**
     * Set attuned constellation
     */
    public static void setConstellation(ItemStack stack, IConstellation constellation) {
        if (stack == null || constellation == null) {
            return;
        }

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound()
            .setString(TAG_CONSTELLATION, constellation.getUnlocalizedName());
    }

    /**
     * NOTE: CapeEffect System
     * <p>
     * Original version:
     * - CapeEffectRegistry manages constellation effects
     * - Each constellation has unique CapeArmorEffect
     * - Effects applied on armor tick
     * - Attribute modifiers applied based on constellation
     * <p>
     * In 1.7.10:
     * - TODO: Implement CapeEffect interface
     * - TODO: Implement CapeEffectRegistry
     * - TODO: Implement CapeArmorEffect base class
     * - TODO: Implement constellation-specific effects
     * - TODO: Implement attribute modifier system
     */
}
