/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base ItemBlock class for all AstralSorcery ItemBlocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * AstralBaseItemBlock - Base class for all AstralSorcery ItemBlocks
 * <p>
 * Provides common functionality for ItemBlocks including:
 * - Metadata support
 * - Custom placement logic
 * - Icon management
 * - Creative tab display
 * <p>
 * All AstralSorcery ItemBlocks should extend this class.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public class AstralBaseItemBlock extends ItemBlock {

    /**
     * Constructor
     * 
     * @param block Associated block
     */
    public AstralBaseItemBlock(Block block) {
        super(block);
        this.setUnlocalizedName(block.getUnlocalizedName());
    }

    /**
     * Set basic configuration
     * 
     * @return this, supports chaining
     */
    public AstralBaseItemBlock setBasicConfig() {
        CreativeTabs tab = this.field_150939_a.getCreativeTabToDisplayOn();
        if (tab != null) {
            this.setCreativeTab(tab);
        }

        this.setTextureName(this.field_150939_a.getUnlocalizedName());

        return this;
    }

    /**
     * Enable metadata support
     * 
     * @return this, supports chaining
     */
    public AstralBaseItemBlock setMetadataSupport() {
        this.setHasSubtypes(true);
        return this;
    }

    /**
     * Set full configuration with metadata
     * 
     * @return this, supports chaining
     */
    public AstralBaseItemBlock setFullConfig() {
        return this.setBasicConfig()
            .setMetadataSupport();
    }

    // ========== Metadata ==========

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack);
    }

    // ========== Creative Mode Display ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        this.field_150939_a.getSubBlocks(item, tab, list);
    }

    // ========== Icons ==========

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return super.getIconFromDamage(damage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        super.registerIcons(register);
    }

    // ========== Placement ==========

    /**
     * Check if block can be placed
     * 
     * @param stack  Item stack
     * @param player Player
     * @param world  World
     * @param x,     y, z Position
     * @param side   Side clicked
     * @return true if can place
     */
    protected boolean canPlaceBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
        int side) {
        return world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, player, stack);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!this.canPlaceBlockAt(stack, player, world, x, y, z, side)) {
            return false;
        }

        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    // ========== Helper Methods ==========

    /**
     * Clamp value to range
     * 
     * @param value Original value
     * @param min   Minimum
     * @param max   Maximum
     * @return Clamped value
     */
    protected int clamp(int value, int min, int max) {
        return MathHelper.clamp_int(value, min, max);
    }

    /**
     * Check if metadata is valid
     * 
     * @param metadata Metadata value
     * @return true if valid
     */
    protected boolean isValidMetadata(int metadata) {
        return metadata >= 0;
    }

    /**
     * Get metadata count
     * 
     * @return Supported metadata count
     */
    public int getMetadataCount() {
        return 1;
    }

    // ========== Hook Methods ==========

    /**
     * Create custom ItemStack
     * 
     * @param metadata Metadata
     * @param size     Stack size
     * @return Item stack
     */
    protected ItemStack createCustomItemStack(int metadata, int size) {
        return new ItemStack(this, size, metadata);
    }

    /**
     * Called after block placement
     * 
     * @param stack  Item stack
     * @param player Player
     * @param world  World
     * @param x,     y, z Position
     * @param side   Side
     */
    protected void onBlockPlaced(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        // Override in subclass if needed
    }

    // ========== Utility Methods ==========

    /**
     * Get display name for metadata
     * 
     * @param metadata Metadata
     * @return Display name
     */
    @SideOnly(Side.CLIENT)
    public String getDisplayName(int metadata) {
        ItemStack stack = new ItemStack(this, 1, metadata);
        return stack.getDisplayName();
    }

    /**
     * Check if can interact with block
     * 
     * @param player Player
     * @param world  World
     * @param x,     y, z Position
     * @return true if can interact
     */
    public boolean canInteractWith(EntityPlayer player, World world, int x, int y, int z) {
        return player.canPlayerEdit(x, y, z, 0, new ItemStack(this));
    }
}
