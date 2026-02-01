/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Shifting Star Item - Remove or switch constellation attunement
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Shifting Star Item
 * <p>
 * Consumable item that:
 * - Removes constellation attunement (sneak + right click)
 * - Switches to a different constellation (right click)
 * - Preserves experience/progress
 * <p>
 * Features:
 * - Single use (consumed on use)
 * - Use animation
 * - Particle effects
 * - Experience preservation
 * - Two variants: normal and enhanced (metadata 0 and 1)
 * - Enhanced has 5 constellation attunements (metadata 2-6)
 * <p>
 * TODO:
 * - Implement constellation switching logic
 * - Link with constellation/attunement system
 * - Add particle effects
 * - Add experience handling
 * - Implement use animation
 */
public class ItemShiftingStar extends AstralBaseItem {

    @SideOnly(Side.CLIENT)
    private IIcon iconNormal;

    @SideOnly(Side.CLIENT)
    private IIcon iconEnhanced;

    @SideOnly(Side.CLIENT)
    private IIcon[] iconEnhancedConstellation;

    public ItemShiftingStar() {
        super(1); // Max stack size 1
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        StarType type = StarType.byMetadata(meta);
        // All enhanced variants use the "enhanced" suffix for localization
        String suffix = (type == StarType.NORMAL) ? "" : "enhanced";
        return super.getUnlocalizedName() + (suffix.isEmpty() ? "" : "." + suffix);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        // Use centralized icon registration from ResourceConfig
        IIcon[] allIcons = IconHelper.registerIconsFromConfig(register, "itemshiftingstar");

        // Split into individual icons for easier access
        if (allIcons.length >= 7) {
            iconNormal = allIcons[0];
            iconEnhanced = allIcons[1];
            iconEnhancedConstellation = new IIcon[5];
            System.arraycopy(allIcons, 2, iconEnhancedConstellation, 0, 5);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        StarType type = StarType.byMetadata(damage);
        switch (type) {
            case NORMAL:
                return iconNormal;
            case ENHANCED:
                return iconEnhanced;
            case ENHANCED_AEVITAS:
                return iconEnhancedConstellation[ConstellationType.AEVITAS.ordinal()];
            case ENHANCED_ARMARA:
                return iconEnhancedConstellation[ConstellationType.ARMARA.ordinal()];
            case ENHANCED_DISCIDIA:
                return iconEnhancedConstellation[ConstellationType.DISCIDIA.ordinal()];
            case ENHANCED_EVORSIO:
                return iconEnhancedConstellation[ConstellationType.EVORSIO.ordinal()];
            case ENHANCED_VICIO:
                return iconEnhancedConstellation[ConstellationType.VICIO.ordinal()];
            default:
                return iconNormal;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        boolean isSneaking = player.isSneaking();

        // TODO: Implement constellation switching/removal logic
        if (isSneaking) {
            // Remove attunement
        } else {
            // Switch attunement
        }

        // Consume the item
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }

        // TODO: Add particle effects
        // TODO: Play sound effects

        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32; // 1.6 seconds
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add("§7Right-click: §eSwitch constellation attunement");
        tooltip.add("§7Shift + Right-click: §eRemove constellation attunement");
        tooltip.add("§cConsumed on use");
    }

    /**
     * Star type enum
     */
    public enum StarType {

        NORMAL(0, ""),
        ENHANCED(1, "enhanced"),
        ENHANCED_AEVITAS(2, "aevitas"),
        ENHANCED_ARMARA(3, "armara"),
        ENHANCED_DISCIDIA(4, "discidia"),
        ENHANCED_EVORSIO(5, "evorsio"),
        ENHANCED_VICIO(6, "vicio");

        private final int metadata;
        private final String suffix;

        StarType(int metadata, String suffix) {
            this.metadata = metadata;
            this.suffix = suffix;
        }

        public int getMetadata() {
            return metadata;
        }

        public String getSuffix() {
            return suffix;
        }

        public static StarType byMetadata(int meta) {
            for (StarType type : values()) {
                if (type.metadata == meta) {
                    return type;
                }
            }
            return NORMAL;
        }
    }

    /**
     * Constellation type enum for enhanced stars
     */
    public enum ConstellationType {

        AEVITAS("aevitas"),
        ARMARA("armara"),
        DISCIDIA("discidia"),
        EVORSIO("evorsio"),
        VICIO("vicio");

        private final String suffix;

        ConstellationType(String suffix) {
            this.suffix = suffix;
        }

        public String getSuffix() {
            return suffix;
        }
    }
}
