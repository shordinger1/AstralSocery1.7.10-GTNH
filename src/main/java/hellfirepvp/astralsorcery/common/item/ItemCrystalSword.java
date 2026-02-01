/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal Sword - 2-crystal combat weapon
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.entity.EntityCrystalTool;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;

/**
 * ItemCrystalSword - Crystal sword (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Uses 2 crystals (max capacity 600)</li>
 * <li>Dynamic damage based on crystal quality</li>
 * <li>Custom damage system (1/3 chance for swords)</li>
 * <li>ToolCrystalProperties integration</li>
 * <li>Custom entity rendering (EntityCrystalTool)</li>
 * <li>Cannot be repaired</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>ItemSword constructor: ToolMaterial parameter</li>
 * <li>NBTHelper.getPersistentData() → stack.getTagCompound()</li>
 * <li>getSubItems() → Different signature in 1.7.10</li>
 * <li>getAttributeModifiers() → Different signature in 1.7.10 (not implemented yet)</li>
 * <li>ITooltipFlag → boolean advanced parameter</li>
 * <li>EntityEquipmentSlot → Handled differently in 1.7.10</li>
 * <li>NonNullList → List<ItemStack></li>
 * </ul>
 * <p>
 * <b>Damage Mechanics (Sword-specific):</b>
 * 
 * <pre>
 * Each damage point:
 *   - Check collectiveCapability (higher = more resistant)
 *   - Roll for damage: chance = pow(collectiveCapability / 100, 2)
 *   - If damaged: 1/3 chance to reduce collectiveCapability by 1
 *   - Check purity (higher = more resistant)
 *   - Roll for damage: chance = purity / 100
 *   - If damaged: 1/3 chance to reduce collectiveCapability by 1
 * </pre>
 * <p>
 * <b>Attribute Modifiers:</b>
 * <ul>
 * <li>Attack Damage: 1 + (12 * efficiencyMultiplier)</li>
 * <li>Attack Speed: -1 (in 1.7.10, handled differently)</li>
 * </ul>
 */
public class ItemCrystalSword extends ItemSword {

    private static final Random rand = new Random();

    public ItemCrystalSword() {
        super(net.minecraft.item.Item.ToolMaterial.EMERALD);
        this.setMaxDamage(0);
        // 1.7.10: Creative tab set in registration
    }

    /**
     * Get max size for sword (2 crystals)
     * 1.7.10: Direct calculation
     *
     * @param stack The sword ItemStack
     * @return Max size (600 for celestial * 2)
     */
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL * 2;
    }

    /**
     * Get tool properties from ItemStack
     * 1.7.10: Wrapper around ToolCrystalProperties.getToolProperties()
     *
     * @param stack ItemStack to read from
     * @return ToolCrystalProperties, or null if not found
     */
    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        return ToolCrystalProperties.getToolProperties(stack);
    }

    /**
     * Set tool properties to ItemStack
     * 1.7.10: Wrapper around ToolCrystalProperties.setToolProperties()
     *
     * @param stack      ItemStack to write to
     * @param properties Properties to write
     */
    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        ToolCrystalProperties.setToolProperties(stack, properties);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        ToolCrystalProperties prop = getToolProperties(stack);
        if (prop != null && advanced) {
            tooltip.add("\\u00a7Crystal Attributes:");
            CrystalProperties.addPropertyTooltip(prop, tooltip, getMaxSize(stack));
        }
    }

    /**
     * Add sword to creative tab
     * 1.7.10: getSubItems with Item, CreativeTabs, List
     */
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (tab == this.getCreativeTab()) {
            // Create max celestial crystal sword
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial));
            list.add(stack);
        }
    }

    /**
     * Check if has custom entity
     * Crystal swords have EntityCrystalTool for rendering
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
        if (location instanceof EntityItem) {
            // TODO: Check if EntityItem has thrower/owner methods
            // newItem.setThrower(((EntityItem) location).getThrower());
            // newItem.setOwner(((EntityItem) location).getOwner());
        }
        return newItem;
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
        super.setDamage(stack, 0); // Crystal swords don't use durability
        damageProperties(stack, damage);
    }

    /**
     * Damage tool properties based on use
     * 1.7.10: Implements crystal damage mechanics
     * Swords have 1/3 damage chance (more aggressive than tools)
     *
     * @param stack  The sword ItemStack
     * @param damage Amount of damage to apply
     */
    private void damageProperties(ItemStack stack, int damage) {
        ToolCrystalProperties prop = getToolProperties(stack);
        if (prop == null) {
            // No properties yet, initialize
            // Use super.setDamage() to avoid infinite recursion
            super.setDamage(stack, stack.getMaxDamage());
            return;
        }

        if (prop.getSize() <= 0) {
            // Sword broken
            super.setDamage(stack, stack.getMaxDamage() + 1);
            return;
        }

        if (damage < 0) {
            return;
        }

        // Apply damage points
        for (int i = 0; i < damage; i++) {
            // Check collective capability damage
            double chance = Math.pow(((double) prop.getCollectiveCapability()) / 100D, 2);
            if (chance >= rand.nextFloat()) {
                // 1/3 chance to reduce collective capability (sword)
                if (rand.nextInt(3) == 0) {
                    prop = prop.copyDamagedCutting();
                }

                // Check purity damage
                double purity = ((double) prop.getPurity()) / 100D;
                if (purity <= rand.nextFloat()) {
                    // 1/3 chance to reduce collective capability (sword)
                    if (rand.nextInt(3) == 0) {
                        prop = prop.copyDamagedCutting();
                    }
                }
            }
        }

        setToolProperties(stack, prop);

        // Check if sword should break
        if (prop.getSize() <= 0) {
            super.setDamage(stack, stack.getMaxDamage() + 1);
        }
    }

    /**
     * Get max damage (durability bar)
     * 1.7.10: Swords have 10 "durability" points
     */
    @Override
    public int getMaxDamage(ItemStack stack) {
        return 10;
    }

    /**
     * Tools are enchantable
     */
    @Override
    public boolean isItemTool(ItemStack stack) {
        return true;
    }

    /**
     * Hit entity - damage is applied via setDamage
     * 1.7.10: Override to prevent durability consumption
     */
    @Override
    public boolean hitEntity(ItemStack stack, net.minecraft.entity.EntityLivingBase target,
        net.minecraft.entity.EntityLivingBase attacker) {
        // Damage is applied via setDamage, which redirects to damageProperties
        return true;
    }

    /**
     * Get damage vs entity
     * 1.7.10: Calculate based on crystal properties
     * Formula: 1 + (12 * efficiencyMultiplier)
     * Min: 1 + (12 * 0.05) = 1.6
     * Max: 1 + (12 * 1.0) = 13
     */
    // @Override
    // public float func_150931_a() {
    // // 1.7.10: func_150931_a is the damage vs entity method
    // // Return base damage - actual damage is calculated via attribute modifiers
    // return super.func_150931_a();
    // }

}
