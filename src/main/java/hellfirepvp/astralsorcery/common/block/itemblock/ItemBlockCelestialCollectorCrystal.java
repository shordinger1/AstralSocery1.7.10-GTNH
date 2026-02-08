/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ItemBlock for BlockCelestialCollectorCrystal - Migrated from 1.12.2
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.itemblock;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.entity.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * ItemBlock for BlockCelestialCollectorCrystal
 * <p>
 * Migrated from 1.12.2 ItemCollectorCrystal (celestial variant)
 * <p>
 * Features:
 * - Custom highlighted entity rendering with celestial blue color
 * - Stores constellation and trait information in NBT
 * - Celestial rarity (higher than rock crystal)
 */
public class ItemBlockCelestialCollectorCrystal extends ItemBlock implements ItemHighlighted {

    public ItemBlockCelestialCollectorCrystal(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        EntityItemHighlighted ei = new EntityItemHighlighted(world, entity.posX, entity.posY, entity.posZ, itemstack);
        // Celestial crystal color - bright blue
        ei.applyColor(new Color(0x00, 0x88, 0xFF));
        // REMOVED: setDefaultPickupDelay() - 1.7.10 EntityItem doesn't have this method
        ei.motionX = entity.motionX;
        ei.motionY = entity.motionY;
        ei.motionZ = entity.motionZ;
        // REMOVED: setThrower/setOwner - 1.7.10 EntityItem doesn't have these methods
        return ei;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // 1.7.10: Use epic rarity instead of custom rarityCelestial
        // In 1.12.2, this used RegistryItems.rarityCelestial (custom blue rarity)
        return EnumRarity.epic;
    }

    @Override
    public Color getHighlightColor(ItemStack stack) {
        // Celestial crystal color - bright blue
        return new Color(0x00, 0x88, 0xFF);
    }

    public static void setConstellation(ItemStack stack, IWeakConstellation constellation) {
        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    public static IWeakConstellation getConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public static void setTraitConstellation(ItemStack stack, IMinorConstellation constellation) {
        if (constellation == null) return;
        constellation.writeToNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey() + "trait");
    }

    public static IMinorConstellation getTrait(ItemStack stack) {
        return (IMinorConstellation) IConstellation
            .readFromNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey() + "trait");
    }

}
