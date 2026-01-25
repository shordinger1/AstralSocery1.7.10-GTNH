/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.models.base.ASCape;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectFactory;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectOctans;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerCapeEffects;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemComparator;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCape
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:08
 */
public class ItemCape extends ItemArmor implements ItemDynamicColor {

    private static final UUID OCTANS_UNWAVERING = UUID.fromString("845DB25C-C624-495F-8C9F-60210A958B6B");
    private Object objASCape = null;

    public ItemCape() {
        // 1.7.10: armorType 1 = chestplate
        super(RegistryItems.imbuedLeatherMaterial, -1, 1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    // Removed @Override - 1.7.10 compatibility
    public void getSubItems(CreativeTabs tab, ArrayList<ItemStack> items) {
        // 1.7.10 compatibility: Item.isInCreativeTab() doesn't exist, use tab == this.getCreativeTab() instead
        if (tab == this.getCreativeTab()) {
            items.add(new ItemStack(this));

            ItemStack stack;
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                if (c instanceof IMinorConstellation) continue;

                stack = new ItemStack(this);
                setAttunedConstellation(stack, c);
                items.add(stack);
            }
        }
    }

    // Removed @Override - different signature in 1.7.10
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip) {
        if (Mods.DRACONICEVOLUTION.isPresent()) {
            float perc = Config.capeChaosResistance;
            if (perc > 0) {
                int displayPerc = WrapMathHelper.floor(perc * 100);
                String out = I18n.format("misc.chaos.resistance", displayPerc + "%");
                if (perc >= 1) {
                    out = I18n.format("misc.chaos.resistance.max");
                }
                tooltip.add(EnumChatFormatting.DARK_PURPLE + out);
            }
        }
        IConstellation cst = getAttunedConstellation(stack);
        if (cst != null) {
            String n = cst.getUnlocalizedName();
            n = I18n.format(n);
            tooltip.add(EnumChatFormatting.BLUE + n);
        }
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);

        // 1.7.10: Use world parameter instead of getWorld()
        if (!world.isRemote) {
            CapeEffectOctans ceo = getCapeEffect(player, Constellations.octans);
            if (ceo != null && player.isInWater()) {
                NBTTagCompound perm = NBTHelper.getPersistentData(itemStack);
                perm.setInteger("AS_UpdateAttributes", itemRand.nextInt());
            }
        }
    }

    @Override
    // 1.7.10: getAttributeModifiers() takes ItemStack parameter
    public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<String, AttributeModifier> out = super.getAttributeModifiers(stack);
        if (stack != null) {
            IConstellation cst = getAttunedConstellation(stack);
            if (cst != null && cst.equals(Constellations.octans)) {
                CapeEffectOctans ceo = getCapeEffect(stack);
                if (ceo != null) {
                    EntityPlayer potentialCurrent = EventHandlerCapeEffects.currentPlayerInTick;
                    if (potentialCurrent != null && potentialCurrent.isInWater()) {
                        out.put(
                            SharedMonsterAttributes.knockbackResistance.getAttributeUnlocalizedName(),
                            new AttributeModifier(OCTANS_UNWAVERING, OCTANS_UNWAVERING.toString(), 500, 0));
                    }
                }
            }
        }
        return out;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (EventHandlerCapeEffects.inElytraCheck) {
            return; // It shouldn't damage the vicio cape by flying with it.
        }
        super.setDamage(stack, damage);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    // 1.7.10: getArmorModel uses int armorSlot instead of EntityEquipmentSlot
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        if (objASCape == null) {
            objASCape = new ASCape();
        }
        return (ModelBiped) objASCape;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    // 1.7.10: getArmorTexture uses int slot instead of EntityEquipmentSlot
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "astralsorcery:textures/models/as_cape.png";
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ItemComparator.compare(
            repair,
            ItemCraftingComponent.MetaType.STARDUST.asStack(),
            ItemComparator.Clause.ITEM,
            ItemComparator.Clause.META_STRICT);
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFF;
        IConstellation cst = getAttunedConstellation(stack);
        if (cst != null) {
            Color c = cst.getConstellationColor();
            return 0xFF000000 | c.getRGB();
        }
        return 0xFF000000;
    }

    @Nullable
    public static CapeArmorEffect getCapeEffect(@Nullable EntityPlayer entity) {
        if (entity == null) return null;
        // 1.7.10: armorInventory[2] = chestplate slot
        ItemStack stack = entity.inventory.armorInventory[2];
        IConstellation cst = getAttunedConstellation(stack);
        if (cst == null) {
            return null;
        }
        return getCapeEffect(stack);
    }

    @Nullable
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nullable EntityPlayer entity,
        @Nonnull IConstellation expectedConstellation) {
        if (entity == null) return null;
        // 1.7.10: armorInventory[2] = chestplate slot
        ItemStack stack = entity.inventory.armorInventory[2];
        IConstellation cst = getAttunedConstellation(stack);
        if (cst == null || !cst.equals(expectedConstellation)) {
            return null;
        }
        return getCapeEffect(stack);
    }

    @Nullable
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nonnull ItemStack stack) {
        IConstellation cst = getAttunedConstellation(stack);
        if (cst == null) {
            return null;
        }
        CapeEffectFactory<? extends CapeArmorEffect> call = CapeEffectRegistry.getArmorEffect(cst);
        if (call == null) {
            return null;
        }
        try {
            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            return (V) call.deserializeCapeEffect(cmp);
        } catch (Exception exc) {
            return null;
        }
    }

    @Nullable
    public static IConstellation getAttunedConstellation(@Nonnull ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemCape)) {
            return null;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        return IConstellation.readFromNBT(cmp);
    }

    public static void setAttunedConstellation(@Nonnull ItemStack stack, @Nonnull IConstellation cst) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemCape)) {
            return;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cst.writeToNBT(cmp);
    }

}
