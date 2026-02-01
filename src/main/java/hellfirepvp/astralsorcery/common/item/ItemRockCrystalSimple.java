/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Rock Crystal Simple - Basic rock crystal item
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;

/**
 * Rock Crystal Simple
 * <p>
 * Basic rock crystal item without attunement.
 * <p>
 * Features:
 * - Size and purity stored in NBT
 * - Used as crafting component
 * - Can be attuned later
 * <p>
 * TODO:
 * - Implement crystal properties system
 * - Implement size/purity generation
 * - Link with attunement system
 */
public class ItemRockCrystalSimple extends AstralBaseItem {

    private static final String TAG_SIZE = "crystal_size";
    private static final String TAG_PURITY = "crystal_purity";

    public ItemRockCrystalSimple() {
        super(64); // Max stack size 64
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && !hasCrystalData(stack)) {
            // TODO: Generate random size and purity
            float size = generateSize();
            float purity = generatePurity();
            setCrystalData(stack, size, purity);
        }
    }

    /**
     * Generate random crystal size (0.0 - 1.0)
     */
    private float generateSize() {
        // TODO: Implement proper size generation
        return 0.5F + (float) Math.random() * 0.5F;
    }

    /**
     * Generate random crystal purity (0.0 - 1.0)
     */
    private float generatePurity() {
        // TODO: Implement proper purity generation
        return (float) Math.random();
    }

    /**
     * Check if has crystal data
     */
    public boolean hasCrystalData(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && (nbt.hasKey(TAG_SIZE) || nbt.hasKey(TAG_PURITY));
    }

    /**
     * Get crystal size
     */
    public float getSize(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_SIZE)) {
            return nbt.getFloat(TAG_SIZE);
        }
        return 0.5F; // Default size
    }

    /**
     * Get crystal purity
     */
    public float getPurity(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_PURITY)) {
            return nbt.getFloat(TAG_PURITY);
        }
        return 0.5F; // Default purity
    }

    /**
     * Set crystal data
     */
    public void setCrystalData(ItemStack stack, float size, float purity) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setFloat(TAG_SIZE, size);
        nbt.setFloat(TAG_PURITY, purity);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (hasCrystalData(stack)) {
            float size = getSize(stack);
            float purity = getPurity(stack);
            tooltip.add("§7Size: §e" + String.format("%.2f", size));
            tooltip.add("§7Purity: §b" + String.format("%.2f", purity));
        }
    }
}
