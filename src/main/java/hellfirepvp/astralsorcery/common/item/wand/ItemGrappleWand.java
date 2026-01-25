/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.entities.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemGrappleWand
 * Created by HellFirePvP
 * Date: 30.06.2017 / 11:23
 */
public class ItemGrappleWand extends Item implements ItemAlignmentChargeConsumer {

    public ItemGrappleWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return ct == ChargeType.TEMP;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        ItemStack stack = itemStackIn;
        if ((stack == null || stack.stackSize <= 0) || worldIn.isRemote) {
            return stack;
        }
        if (drainTempCharge(player, Config.grappleWandUseCost, true)) {
            worldIn.spawnEntityInWorld(new EntityGrapplingHook(worldIn, player));
            drainTempCharge(player, Config.grappleWandUseCost, false);
        }
        return stack;
    }

}
