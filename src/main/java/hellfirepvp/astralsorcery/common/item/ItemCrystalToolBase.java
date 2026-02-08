/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal Tool Base - Base class for all crystal tools
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.entity.EntityCrystalTool;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.util.LocalizationHelper;

/**
 * ItemCrystalToolBase - Crystal tool base (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Tools use ToolCrystalProperties instead of durability</li>
 * <li>Tools can be damaged based on crystal quality</li>
 * <li>Mining speed affected by crystal collectiveCapability</li>
 * <li>Custom entity rendering (EntityCrystalTool)</li>
 * <li>Cannot be repaired in anvil</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>ItemTool constructor: float damage, ToolMaterial, Set&lt;Block&gt; effectiveBlocks</li>
 * <li>func_150893_a() = getStrVsBlock() for mining speed</li>
 * <li>damageVsEntity is private, handled via constructor</li>
 * <li>setCreativeTab() instead of setCreativeTab()</li>
 * </ul>
 * <p>
 * <b>Tool Properties:</b>
 * <ul>
 * <li>Stored in ItemStack NBT under "crystalProperties" tag</li>
 * <li>Read/Write via ToolCrystalProperties.getToolProperties/setToolProperties()</li>
 * <li>Includes size, purity, collectiveCapability, fractured, sizeOverride</li>
 * </ul>
 * <p>
 * <b>Damage Mechanics:</b>
 * 
 * <pre>
 * Each damage point:
 *   - Check collectiveCapability (higher = more resistant)
 *   - Roll for damage: chance = pow(collectiveCapability / 100, 2)
 *   - If damaged: 1/8 chance to reduce collectiveCapability by 1
 *   - Check purity (higher = more resistant)
 *   - Roll for damage: chance = purity / 100
 *   - If damaged: 1/8 chance to reduce collectiveCapability by 1
 * </pre>
 */
public abstract class ItemCrystalToolBase extends ItemTool {

    private static final Random rand = new Random();
    protected final int crystalCount;

    public ItemCrystalToolBase(int crystalCount) {
        // 1.7.10: ToolMaterial must be provided by subclass
        // Use EMERALD material with 0 effective blocks, will be overridden by subclasses
        this(crystalCount, net.minecraft.item.Item.ToolMaterial.EMERALD);
    }

    public ItemCrystalToolBase(int crystalCount, net.minecraft.item.Item.ToolMaterial material) {
        // 1.7.10: ItemTool constructor takes (float damage, ToolMaterial, Set<Block> effectiveBlocks)
        super(0F, material, (Set<Block>) null);
        this.crystalCount = crystalCount;
        this.setMaxDamage(0);
    }

    /**
     * Get crystal count for this tool
     *
     * @return Number of crystals used
     */
    public int getCrystalCount() {
        return crystalCount;
    }

    /**
     * Get max size for tool (based on crystal count)
     * 1.7.10: Direct calculation instead of CrystalProperties helper
     *
     * @param stack The tool ItemStack
     * @return Max size
     */
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL * getCrystalCount();
    }

    /**
     * Get tool properties from ItemStack
     * 1.7.10: Static wrapper around ToolCrystalProperties.getToolProperties()
     *
     * @param stack ItemStack to read from
     * @return ToolCrystalProperties, or null if not found
     */
    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        return ToolCrystalProperties.getToolProperties(stack);
    }

    /**
     * Set tool properties to ItemStack
     * 1.7.10: Static wrapper around ToolCrystalProperties.setToolProperties()
     *
     * @param stack      ItemStack to write to
     * @param properties Properties to write
     */
    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        ToolCrystalProperties.setToolProperties(stack, properties);
    }

    /**
     * Add tooltip information for crystal tools.
     * <p>
     * Shows crystal properties when advanced tooltips are enabled (F3+H).
     * Also shows general tooltip from language files.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // Add general tooltip from language files
        LocalizationHelper.addItemTooltip(stack, tooltip, 3, true);

        // Show crystal properties when advanced tooltips are enabled (F3+H)
        ToolCrystalProperties prop = ItemCrystalToolBase.getToolProperties(stack);
        if (prop != null && advanced) {
            tooltip.add("§7" + LocalizationHelper.tr("item.crystaltool.properties"));
            CrystalProperties.addPropertyTooltip(prop, tooltip, getMaxSize(stack));
        }
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        // 1.7.10: func_150893_a = getStrVsBlock()
        float baseSpeed = super.func_150893_a(stack, block);
        ToolCrystalProperties properties = ItemCrystalToolBase.getToolProperties(stack);
        if (properties != null) {
            return baseSpeed * properties.getEfficiencyMultiplier() * 2F;
        }
        return baseSpeed;
    }

    /**
     * Check if has custom entity
     * Crystal tools have EntityCrystalTool for rendering
     */
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    /**
     * Create custom entity for rendering
     * 1.7.10: Signature changed, uses coordinates instead of BlockPos
     */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityCrystalTool newItem = new EntityCrystalTool(
            world,
            location.posX,
            location.posY,
            location.posZ,
            itemstack);
        newItem.motionX = location.motionX;
        newItem.motionY = location.motionY;
        newItem.motionZ = location.motionZ;

        // 1.7.10: Set default pickup delay
        if (location instanceof net.minecraft.entity.item.EntityItem) {
            // TODO: Check if EntityItem has thrower/owner methods
            // newItem.setThrower(((EntityItem) location).getThrower());
            // newItem.setOwner(((EntityItem) location).getOwner());
        }
        return newItem;
    }

    @Override
    public boolean isItemTool(ItemStack stack) {
        return true;
    }

    /**
     * Tools cannot be repaired
     */
    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack material) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    /**
     * Set damage - redirects to damageProperties
     * 1.7.10: Override to implement custom damage logic
     */
    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0); // Crystal tools don't use durability
        damageProperties(stack, damage);
    }

    /**
     * Damage tool properties based on use
     * 1.7.10: Implements crystal damage mechanics
     *
     * @param stack  The tool ItemStack
     * @param damage Amount of damage to apply
     */
    protected void damageProperties(ItemStack stack, int damage) {
        ToolCrystalProperties prop = ItemCrystalToolBase.getToolProperties(stack);
        if (prop == null) {
            // No properties yet, initialize
            // Don't set vanilla damage since getMaxDamage() returns 0
            return;
        }

        if (prop.getSize() <= 0) {
            // Tool broken
            // Don't set vanilla damage since getMaxDamage() returns 0
            // Tool will be unusable due to size check elsewhere
            return;
        }

        if (damage < 0) {
            return;
        }

        // Apply damage points
        for (int i = 0; i < damage; i++) {
            // Check collective capability damage
            if (prop.shouldDamage(rand)) {
                // 1/8 chance to reduce collective capability (ItemCrystalToolBase)
                // 1/3 chance to reduce collectiveCapability (ItemCrystalSword)
                // Since we can't use instanceof ItemCrystalSword here (circular reference),
                // we use 1/8 for all tools. Swords handle their own damage logic.
                if (rand.nextInt(8) == 0) {
                    prop = prop.copyDamagedCutting();
                }

                // Check purity damage
                if (prop.shouldDamagePurity(rand)) {
                    if (rand.nextInt(8) == 0) {
                        prop = prop.copyDamagedCutting();
                    }
                }
            }

            // Check if tool should break early
            if (prop.getSize() <= 0) {
                break;
            }
        }

        ItemCrystalToolBase.setToolProperties(stack, prop);

        // Check if tool should break
        if (prop.getSize() <= 0) {
            // Tool broken - don't set vanilla damage
            // Tool will be unusable due to size <= 0
        }
    }

    /**
     * Get max damage (durability bar)
     * Return 0 to indicate this item doesn't use vanilla durability
     * Crystal tools use NBT-based fracturation instead
     * <p>
     * IMPORTANT: This prevents NEI from showing multiple durability variants
     */
    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
    }

    /**
     * Show durability bar
     * Crystal tools don't use vanilla durability, so never show the bar
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    // ========== Icon Registration (1.7.10) ==========

    /**
     * Register icons for crystal tools
     * In 1.7.10, ItemTool subclasses need to explicitly register icons
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        // Get the texture name that was set by setTextureName() in RegistryItems
        String iconString = this.getIconString();
        if (iconString != null) {
            this.itemIcon = register.registerIcon(iconString);
        } else {
            // Fallback: use unlocalizedName without "item." prefix
            String unlocalized = this.getUnlocalizedName();
            if (unlocalized.startsWith("item.")) {
                unlocalized = unlocalized.substring(5);
            }
            this.itemIcon = register.registerIcon("astralsorcery:" + unlocalized);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.IIcon getIconFromDamage(int damage) {
        return this.itemIcon;
    }

}
