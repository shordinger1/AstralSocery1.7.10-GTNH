/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Knowledge Fragment Item - Randomly generated knowledge carrier
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;

/**
 * Knowledge Fragment Item
 * <p>
 * Randomly generated knowledge carrier.
 * Can contain:
 * - Constellation information
 * - Research fragments
 * <p>
 * Features:
 * - Complex seed generation
 * - Client-side content parsing
 * - Weighted random selection
 * - Two types: CONSTELLATION and FRAGMENT
 * <p>
 * TODO:
 * - Implement seed generation algorithm
 * - Implement content parsing
 * - Link with research system
 * - Add weighted random system
 */
public class ItemKnowledgeFragment extends AstralBaseItem {

    private static final String TAG_SEED = "fragment_seed";
    private static final String TAG_TYPE = "fragment_type";

    public enum FragmentType {
        CONSTELLATION, // Contains constellation info
        FRAGMENT // Contains research fragment
    }

    public ItemKnowledgeFragment() {
        super(64); // Max stack size 64
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && !hasSeed(stack)) {
            // TODO: Generate complex seed
            // long seed = generateSeed(player, world);
            // setSeed(stack, seed);
        }
    }

    /**
     * Generate seed based on player, time, and world
     */
    private long generateSeed(EntityPlayer player, World world) {
        // TODO: Implement original seed generation
        // long baseRand = (((player.getEntityId() << 6) |
        // (System.currentTimeMillis() & 223)) << 16) |
        // world.getTotalWorldTime();
        // return baseRand;
        return System.currentTimeMillis();
    }

    /**
     * Get seed from NBT
     */
    public long getSeed(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_SEED)) {
            return nbt.getLong(TAG_SEED);
        }
        return 0;
    }

    /**
     * Set seed in NBT
     */
    public void setSeed(ItemStack stack, long seed) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setLong(TAG_SEED, seed);
    }

    /**
     * Get fragment type
     */
    public FragmentType getType(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_TYPE)) {
            int typeOrdinal = nbt.getInteger(TAG_TYPE);
            return FragmentType.values()[typeOrdinal];
        }
        return FragmentType.FRAGMENT; // Default
    }

    /**
     * Set fragment type
     */
    public void setType(ItemStack stack, FragmentType type) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(TAG_TYPE, type.ordinal());
    }

    /**
     * Check if has seed
     */
    public boolean hasSeed(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey(TAG_SEED);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // TODO: Return rarity based on fragment type
        return EnumRarity.uncommon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        FragmentType type = getType(stack);
        if (type == FragmentType.CONSTELLATION) {
            tooltip.add("§7Type: §eConstellation Fragment");
            tooltip.add("§8Contains constellation information");
        } else {
            tooltip.add("§7Type: §bResearch Fragment");
            tooltip.add("§8Contains research knowledge");
        }

        if (hasSeed(stack)) {
            tooltip.add("§7Seed: §8" + getSeed(stack));
        }

        // TODO: When constellation parsing is implemented:
        // tooltip.add("§7Constellation: §e" + parseConstellation(getSeed(stack)));
    }
}
