/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base Item class for all AstralSorcery items
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.util.ASUtils;

/**
 * AstralBaseItem - Base class for all AstralSorcery items
 * <p>
 * Provides common functionality and all overridable Item methods.
 * All AstralSorcery items should extend this class.
 * <p>
 * Registration is handled by RegistryItems, not by this class.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public class AstralBaseItem extends Item {

    // ========== Constructors ==========

    /**
     * Default constructor
     */
    public AstralBaseItem() {
        super();
        this.setMaxStackSize(64);
    }

    /**
     * Constructor with max stack size
     *
     * @param maxStackSize Maximum stack size
     */
    public AstralBaseItem(int maxStackSize) {
        this();
        this.setMaxStackSize(maxStackSize);
    }

    // ========== Naming and Display ==========

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        // Delegate to default implementation
        // Subclasses like ItemCraftingComponent should override this for metadata variants
        return super.getUnlocalizedName(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // Default: no extra information
        // Subclasses can override to add custom tooltip
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return super.hasEffect(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return super.getColorFromItemStack(stack, renderPass);
    }

    // ========== Icons and Rendering ==========

    @Override
    public void registerIcons(IIconRegister register) {
        // Register icon using: "astralsorcery:textureName"
        // Get texture name that was set by setTextureName() in RegistryItems
        String textureName = this.getIconString();
        if (textureName != null && textureName.startsWith(Constants.MODID + ":")) {
            this.itemIcon = register.registerIcon(textureName);
        } else {
            // Fallback: use unlocalizedName without "item." prefix
            String unlocalized = this.getUnlocalizedName();
            if (unlocalized.startsWith("item.")) {
                unlocalized = unlocalized.substring(5); // Remove "item." prefix
            }
            this.itemIcon = register.registerIcon(Constants.MODID + ":" + unlocalized);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return super.getIconFromDamage(damage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        return super.getIconIndex(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return super.isFull3D();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering() {
        return super.shouldRotateAroundWhenRendering();
    }

    // ========== Interaction ==========

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        return super.itemInteractionForEntity(stack, player, target);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return super.onLeftClickEntity(stack, player, entity);
    }

    // ========== Combat and Durability ==========

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,
        EntityLivingBase player) {
        return super.onBlockDestroyed(stack, world, block, x, y, z, player);
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        return super.func_150893_a(stack, block);
    }

    @Override
    public boolean func_150897_b(Block block) {
        return super.func_150897_b(block);
    }

    @Override
    public boolean isDamageable() {
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack material) {
        return super.getIsRepairable(stack, material);
    }

    // ========== Use and Consumption ==========

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return super.getItemUseAction(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return super.getMaxItemUseDuration(stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        super.onPlayerStoppedUsing(stack, world, player, useCount);
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        return super.onEaten(stack, world, player);
    }

    // ========== Update and Events ==========

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, slot, isSelected);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
    }

    // ========== Container and Crafting ==========

    @Override
    public boolean hasContainerItem() {
        return super.hasContainerItem();
    }

    @Override
    public Item getContainerItem() {
        return super.getContainerItem();
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return super.doesContainerItemLeaveCraftingGrid(stack);
    }

    // ========== Subtypes and Metadata ==========

    @Override
    public boolean getHasSubtypes() {
        return super.getHasSubtypes();
    }

    @Override
    public int getMetadata(int damage) {
        return super.getMetadata(damage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, net.minecraft.creativetab.CreativeTabs tab, List<ItemStack> list) {
        super.getSubItems(item, tab, list);
    }

    // ========== Properties ==========

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return super.getItemStackLimit(stack);
    }

    @Override
    public boolean isItemTool(ItemStack stack) {
        return super.isItemTool(stack);
    }

    @Override
    public int getItemEnchantability() {
        return super.getItemEnchantability();
    }

    @Override
    public boolean isMap() {
        return super.isMap();
    }

    @Override
    public boolean getShareTag() {
        return super.getShareTag();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return super.getSpriteNumber();
    }

    // ========== Forge Extensions ==========

    /**
     * Can be used as beacon payment
     *
     * @param stack Item stack
     * @return true if usable for beacon
     */
    public boolean isBeaconPayment(ItemStack stack) {
        return false;
    }

    /**
     * Has custom entity
     *
     * @param stack Item stack
     * @return true if has custom entity
     */
    public boolean hasCustomEntity(ItemStack stack) {
        return false;
    }

    /**
     * Create custom entity
     *
     * @param world    World instance
     * @param location Entity location
     * @param stack    Item stack
     * @return Custom entity
     */
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        return null;
    }

    /**
     * Called when player drops item
     *
     * @param stack  Item stack
     * @param player Player
     * @return true to cancel drop
     */
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        return true;
    }

    /**
     * Can item edit blocks
     *
     * @return true if can edit
     */
    public boolean canItemEditBlocks() {
        return false;
    }

    // ========== Helper Methods ==========

    /**
     * Check if item stack is specific item
     *
     * @param stack Item stack
     * @param item  Target item
     * @return true if matches
     */
    protected boolean isItem(ItemStack stack, Item item) {
        return stack != null && stack.getItem() == item;
    }

    /**
     * Check if item stack is valid
     *
     * @param stack Item stack
     * @return true if valid
     */
    protected boolean isValid(ItemStack stack) {
        return stack != null && stack.stackSize > 0;
    }

    /**
     * Check if world is client
     *
     * @param world World instance
     * @return true if client
     */
    protected boolean isClient(World world) {
        return world.isRemote;
    }

    /**
     * Check if world is server
     *
     * @param world World instance
     * @return true if server
     */
    protected boolean isServer(World world) {
        return !world.isRemote;
    }

    // ========================================================================
    // Localization Helper Methods (Refactored for TST-style approach)
    // ========================================================================

    /**
     * Get localized text for this item.
     * <p>
     * Uses the StatCollector system for runtime localization.
     * Language files are loaded from assets/astralsorcery/lang/
     * <p>
     * <b>Usage:</b>
     *
     * <pre>
     * // In item class:
     * public class MyItem extends AstralBaseItem {
     *     public MyItem() {
     *         super();
     *         this.setUnlocalizedName("astralsorcery.myItem");
     *     }
     *
     *     // Override to add tooltip
     *     public void addInformation(...) {
     *         tooltip.add(getLocal("astralsorcery.myItem.tooltip"));
     *     }
     * }
     * </pre>
     *
     * @param key the localization key
     * @return the localized text
     * @see StatCollector#translateToLocal(String)
     */
    protected String getLocal(String key) {
        return ASUtils.tr(key);
    }

    /**
     * Get localized text with formatting.
     *
     * @param key  the localization key
     * @param args format arguments
     * @return the localized and formatted text
     * @see StatCollector#translateToLocalFormatted(String, Object...)
     */
    protected String getLocalFormatted(String key, Object... args) {
        return ASUtils.tr(key, args);
    }

    /**
     * Get localized tooltip for this item.
     * <p>
     * Helper method to get tooltip lines from localization.
     * Tooltip keys should be in format: {@code {unlocalizedName}.tooltip.{lineNumber}}
     * <p>
     * Example:
     * 
     * <pre>
     * // Language file:
     * item.astralsorcery.myItem.name=My Item
     * item.astralsorcery.myItem.tooltip.1=Line 1 of tooltip
     * item.astralsorcery.myItem.tooltip.2=Line 2 of tooltip
     *
     * // In code:
     * public void addInformation(...) {
     *     addTooltips(tooltip, 3); // Adds tooltip.1, tooltip.2, tooltip.3
     * }
     * </pre>
     *
     * @param tooltip       the tooltip list to add to
     * @param lineCount     number of tooltip lines to add
     * @param showShiftOnly true if tooltips only show when holding shift
     */
    @SideOnly(Side.CLIENT)
    protected void addTooltips(List<String> tooltip, int lineCount, boolean showShiftOnly) {
        // Check shift requirement
        if (showShiftOnly && !isShiftKeyDown()) {
            tooltip.add(getLocal("misc.moreInformation"));
            return;
        }

        // Get base unlocalizedName without "item." prefix
        String baseKey = this.getUnlocalizedName();
        if (baseKey.startsWith("item.")) {
            baseKey = baseKey.substring(5); // Remove "item."
        }

        // Add each tooltip line
        for (int i = 1; i <= lineCount; i++) {
            String key = baseKey + ".tooltip." + i;
            String localized = getLocal(key);
            // Only add if the key exists (not equal to the key itself)
            if (!localized.equals(key)) {
                tooltip.add(localized);
            }
        }
    }

    /**
     * Get localized tooltip for this item (always show).
     *
     * @param tooltip   the tooltip list to add to
     * @param lineCount number of tooltip lines to add
     */
    @SideOnly(Side.CLIENT)
    protected void addTooltips(List<String> tooltip, int lineCount) {
        addTooltips(tooltip, lineCount, false);
    }

    /**
     * Check if player is holding shift key.
     *
     * @return true if shift is pressed
     */
    @SideOnly(Side.CLIENT)
    protected boolean isShiftKeyDown() {
        return net.minecraft.client.gui.GuiScreen.isShiftKeyDown();
    }

    /**
     * Check if localization key exists.
     *
     * @param key the localization key
     * @return true if the key has a translation
     */
    protected boolean hasLocal(String key) {
        return ASUtils.canTranslate(key);
    }
}
