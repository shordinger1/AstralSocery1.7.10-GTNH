/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Wand Base Item - Base class for all wands
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.block.BlockVanishing;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Wand Base Item
 * <p>
 * Base class for all wands in Astral Sorcery.
 * <p>
 * Features:
 * - Single item, not stackable
 * - No durability (infinite use)
 * - Constellation augmentation support
 * - Special interaction with blocks
 * - AEVITAS augment: Creates vanishing blocks
 * <p>
 * Subclasses:
 * - ItemArchitectWand - Place blocks remotely
 * - ItemExchangeWand - Exchange blocks
 * - ItemGrappleWand - Grappling hook
 * - ItemIlluminationWand - Place light blocks
 */
public class ItemWand extends AstralBaseItem {

    private static final String TAG_AUGMENT = "AugmentName";

    @SideOnly(Side.CLIENT)
    private IIcon iconWand;

    public ItemWand() {
        super();
        setMaxStackSize(1); // Only one wand per stack
        setMaxDamage(0); // No durability - infinite use
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        // Use centralized icon registration from ResourceConfig
        iconWand = IconHelper.registerIconFromConfig(register, "itemwand");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return iconWand;
    }

    // ========== Constellation Augmentation ==========

    /**
     * Check if wand has an augment
     *
     * @param stack The wand item stack
     * @return true if augmented
     */
    public boolean hasAugment(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey(TAG_AUGMENT);
    }

    /**
     * Get the wand's augment
     *
     * @param stack The wand item stack
     * @return The augment, or null if no augment
     */
    public WandAugment getAugment(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_AUGMENT)) {
            String augmentName = nbt.getString(TAG_AUGMENT);
            return WandAugment.getByName(augmentName);
        }
        return null;
    }

    /**
     * Get the wand's augment as constellation
     *
     * @param stack The wand item stack
     * @return The constellation, or null if no augment
     */
    public IConstellation getAugmentConstellation(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_AUGMENT)) {
            String augmentName = nbt.getString(TAG_AUGMENT);
            WandAugment wa = WandAugment.getByName(augmentName);
            if (wa != null) {
                // Find constellation matching the augment
                for (IMajorConstellation cst : ConstellationRegistry.getMajorConstellations()) {
                    if (wa.matchesConstellation(cst)) {
                        return cst;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Apply augment to wand
     *
     * @param stack   The wand item stack
     * @param augment The augment to apply
     */
    public void applyAugment(ItemStack stack, WandAugment augment) {
        if (augment == null) {
            return;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        // Store augment name
        nbt.setString(TAG_AUGMENT, augment.name());
    }

    /**
     * Remove augment from wand
     *
     * @param stack The wand item stack
     */
    public void removeAugment(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            nbt.removeTag(TAG_AUGMENT);
        }
    }

    // ========== Interaction ==========

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        // Check if item is in hand or offhand
        if (!isSelected && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack offhand = player.getCurrentEquippedItem();
            if (offhand != null && offhand == stack) {
                isSelected = true;
            }
        }

        if (!isSelected) {
            return;
        }

        // AEVITAS augment: Create vanishing blocks around player
        if (!world.isRemote && entity instanceof EntityPlayer && world.getTotalWorldTime() % 20 == 0) {
            WandAugment wa = getAugment(stack);
            if (wa == WandAugment.AEVITAS) {
                EntityPlayer player = (EntityPlayer) entity;
                int px = (int) Math.floor(player.posX);
                int py = (int) Math.floor(player.posY);
                int pz = (int) Math.floor(player.posZ);

                // Create vanishing blocks around player (3x3 area, 1 block below)
                for (int xx = -1; xx <= 1; xx++) {
                    for (int zz = -1; zz <= 1; zz++) {
                        int bx = px + xx;
                        int by = py - 1;
                        int bz = pz + zz;

                        // Check if position is valid and air
                        if (world.blockExists(bx, by, bz) && world.isAirBlock(bx, by, bz)) {
                            BlockVanishing vanishing = BlocksAS.blockVanishing;
                            if (vanishing != null) {
                                world.setBlock(bx, by, bz, vanishing, 0, 3);
                                LogHelper.debug(
                                    "Created vanishing block at " + bx + "," + by + "," + bz + " for AEVITAS wand");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Handle right-click in air
        if (world.isRemote) {
            return stack;
        }

        WandAugment wa = getAugment(stack);
        if (wa != null) {
            switch (wa) {
                case VICIO:
                    // Launch player forward
                    Vec3 look = player.getLookVec();
                    float power = 1.5F;
                    player.motionX += look.xCoord * power;
                    player.motionY += look.yCoord * power * 0.5 + 0.2; // Add slight upward boost
                    player.motionZ += look.zCoord * power;
                    player.fallDistance = 0;
                    LogHelper.info("Player " + player.getCommandSenderName() + " launched with VICIO wand");
                    player.addChatMessage(new ChatComponentText("Launched forward!"));
                    break;
                case ARMARA:
                    // Defensive stance - TODO: Implement later
                    player.addChatMessage(new ChatComponentText("Defensive stance not yet implemented"));
                    break;
                case AEVITAS:
                    player.addChatMessage(new ChatComponentText("Vanishing blocks created around you"));
                    break;
                default:
                    break;
            }
        }

        return stack;
    }

    // ========== Display ==========

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // Display augment information
        WandAugment wa = getAugment(stack);
        if (wa != null) {
            tooltip.add("\u00A7" + "9" + "Augment: " + "\u00A7" + "b" + wa.name());
            tooltip.add("\u00A7" + "7" + "Ability:");
            switch (wa) {
                case AEVITAS:
                    tooltip.add("Creates vanishing blocks");
                    break;
                case DISCIDIA:
                    tooltip.add("Attack damage bonus");
                    break;
                case VICIO:
                    tooltip.add("Right-click: Launch forward");
                    break;
                case ARMARA:
                    tooltip.add("Defensive stance");
                    break;
                case EVORSIO:
                    tooltip.add("Attack damage bonus");
                    break;
            }
        } else {
            tooltip.add("\u00A7" + "7" + "No augment applied");
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        // Show enchantment effect if augmented
        return hasAugment(stack);
    }

    // ========== Subclass Helpers ==========

    /**
     * Get creative tab for wand items
     * Override in subclasses to use specific creative tabs
     */
    @Override
    public Item setCreativeTab(CreativeTabs tab) {
        return super.setCreativeTab(tab);
    }

    /**
     * Check if player is sneaking
     * Helper method for subclasses
     */
    protected boolean isSneaking(EntityPlayer player) {
        return player.isSneaking();
    }

    /**
     * Check if world is client
     * Helper method for subclasses
     */
    protected boolean isClient(World world) {
        return world.isRemote;
    }

    /**
     * Check if world is server
     * Helper method for subclasses
     */
    protected boolean isServer(World world) {
        return !world.isRemote;
    }

    /**
     * Get or create NBT tag compound
     * Helper method for subclasses
     */
    protected NBTTagCompound getOrCreateNBT(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }
}
