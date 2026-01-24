/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.knowledge;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.entities.EntityItemExplosionResistant;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.SoundHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemFragmentCapsule
 * Created by HellFirePvP
 * Date: 27.10.2018 / 18:53
 */
public class ItemFragmentCapsule extends Item implements ItemHighlighted {

    public ItemFragmentCapsule() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip) {
        tooltip.add(EnumChatFormatting.GRAY + I18n.format("misc.fragment.container.desc"));
        tooltip.add(EnumChatFormatting.GRAY + I18n.format("misc.fragment.container.open"));
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return new Color(0xCEEAFF);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityRelic;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // 1.7.10: Use world parameter instead of getWorld(), remove hand parameter
        if (!world.isRemote) {
            spawnFragment(player);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // 1.7.10: Use world parameter instead of getWorld(), remove hand parameter
        if (!world.isRemote) {
            spawnFragment(player);
        }
        return false;
    }

    private void spawnFragment(EntityPlayer player) {
        SoundHelper.playSoundAround(
            null /* TODO: SoundEvents - needs 1.7.10 sound string */,
            player.worldObj,
            new BlockPos(player),
            0.75F,
            3.5F);
        ItemStack frag = new ItemStack(ItemsAS.knowledgeFragment);
        ItemKnowledgeFragment.generateSeed(player, frag);
        player.inventory.mainInventory[player.inventory.currentItem] = frag;
    }

    @Override
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> items) {}

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityItemExplosionResistant e = new EntityItemExplosionResistant(
            world,
            location.posX,
            location.posY,
            location.posZ,
            itemstack);
        e.setDefaultPickupDelay();
        e.motionX = location.motionX;
        e.motionY = location.motionY;
        e.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            e.setThrower(((EntityItem) location).getThrower());
            e.setOwner(((EntityItem) location).getOwner());
        }
        return e;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 300;
    }

}
