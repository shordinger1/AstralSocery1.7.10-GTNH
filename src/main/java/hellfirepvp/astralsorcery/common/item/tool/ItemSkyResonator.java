/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayLiquidSpring;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.IStructureAreaOfInfluence;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SkyCollectionHelper;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemSkyResonator
 * Created by HellFirePvP
 * Date: 17.01.2017 / 00:53
 *
 * 1.7.10: INBTModel implementation removed (ModelResourceLocation doesn't exist)
 */
public class ItemSkyResonator extends Item implements ISpecialInteractItem {

    private static Random rand = new Random();

    public ItemSkyResonator() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        // 1.7.10: Use tab == this.getCreativeTab() instead of isInCreativeTab()
        if (tab == this.getCreativeTab()) {
            list.add(new ItemStack(this));

            ItemStack enhanced;

            enhanced = new ItemStack(this);
            setEnhanced(enhanced);
            for (ResonatorUpgrade upgrade : ResonatorUpgrade.values()) {
                if (upgrade.obtainable()) {
                    setUpgradeUnlocked(enhanced, upgrade);
                }
            }
            list.add(enhanced);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (!isEnhanced(stack)) return;

        ResonatorUpgrade current = getCurrentUpgrade(null, stack);
        for (ResonatorUpgrade upgrade : getUpgrades(stack)) {
            tooltip.add(
                (upgrade.equals(current) ? EnumChatFormatting.AQUA : EnumChatFormatting.BLUE)
                    + I18n.format(upgrade.getUnlocalizedUpgradeName()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (!isEnhanced(stack)) {
            return super.getUnlocalizedName(stack);
        }
        return getCurrentUpgrade(null, stack).getUnlocalizedName();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player) {
        // 1.7.10: ActionResult doesn't exist, return ItemStack directly
        if (!worldIn.isRemote && player.isSneaking()) {
            cycleUpgrade(player, player.getCurrentEquippedItem());
        }
        return stack;
    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        ResonatorUpgrade upgr = getCurrentUpgrade(player, stack);
        return upgr == ResonatorUpgrade.AREA_SIZE
            && MiscUtils.getTileAt(world, at, IStructureAreaOfInfluence.class, false) != null;
    }

    @Override
    public boolean onRightClick(World world, BlockPos pos, EntityPlayer player, EnumFacing side, ItemStack stack) {
        ResonatorUpgrade upgr = getCurrentUpgrade(player, stack);
        if (upgr == ResonatorUpgrade.AREA_SIZE) {
            if (world.isRemote) {
                IStructureAreaOfInfluence aoe = MiscUtils.getTileAt(world, pos, IStructureAreaOfInfluence.class, false);
                if (aoe != null) {
                    playAoEDisplayEffect(aoe);
                }
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private void playAoEDisplayEffect(IStructureAreaOfInfluence aoe) {
        EffectHandler.getInstance()
            .requestSizePreviewFor(aoe);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // 1.7.10: No off-hand, keep isSelected as-is
        if (!worldIn.isRemote) {
            if (isSelected && entityIn instanceof EntityPlayerMP
                && getCurrentUpgrade((EntityPlayerMP) entityIn, stack) == ResonatorUpgrade.FLUID_FIELDS
                && getCurrentUpgrade((EntityPlayer) entityIn, stack).obtainable()) {
                double dstr = ConstellationSkyHandler.getInstance()
                    .getCurrentDaytimeDistribution(worldIn);
                if (dstr <= 1E-4) return;
                if (rand.nextFloat() < dstr && rand.nextInt(15) == 0) {

                    int oX = rand.nextInt(30) * (rand.nextBoolean() ? 1 : -1);
                    int oZ = rand.nextInt(30) * (rand.nextBoolean() ? 1 : -1);

                    BlockPos pos = new BlockPos(entityIn).add(oX, 0, oZ);
                    int topY = worldIn.getTopSolidOrLiquidBlock(pos.getX(), pos.getZ());
                    pos = new BlockPos(pos.getX(), topY, pos.getZ());
                    // 1.7.10: BlockPos.getDistance() doesn't exist, calculate manually
                    double dx = pos.getX() - WrapMathHelper.floor(entityIn.posX);
                    double dy = pos.getY() - WrapMathHelper.floor(entityIn.posY);
                    double dz = pos.getZ() - WrapMathHelper.floor(entityIn.posZ);
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > 75 * 75) {
                        return;
                    }

                    FluidRarityRegistry.ChunkFluidEntry at = FluidRarityRegistry
                        .getChunkEntry(worldIn.getChunkFromBlockCoords(pos.getX(), pos.getZ()));
                    // 1.7.10: tryDrain takes boolean simulate parameter
                    FluidStack display = at == null ? new FluidStack(FluidRegistry.WATER, 1) : at.tryDrain(1, false);
                    if (display == null || display.getFluid() == null) display = new FluidStack(FluidRegistry.WATER, 1);
                    PktPlayLiquidSpring pkt = new PktPlayLiquidSpring(
                        display,
                        new Vector3(pos).add(rand.nextFloat(), 0, rand.nextFloat()));
                    PacketChannel.CHANNEL
                        .sendToAllAround(pkt, PacketChannel.pointFromPos(worldIn, new BlockPos(entityIn), 32));
                }
            }
        }
    }

    // 1.7.10: INBTModel methods removed - ModelResourceLocation doesn't exist in 1.7.10
    // Model loading is handled differently in 1.7.10

    public static boolean isEnhanced(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        return NBTHelper.getPersistentData(stack)
            .getBoolean("enhanced");
    }

    public static ItemStack setEnhanced(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator)) return stack;
        NBTHelper.getPersistentData(stack)
            .setBoolean("enhanced", true);
        setUpgradeUnlocked(stack, ResonatorUpgrade.STARLIGHT);
        return stack;
    }

    public static boolean cycleUpgrade(@Nonnull EntityPlayer player, ItemStack stack) {
        if (!isEnhanced(stack)) return false;
        ResonatorUpgrade current = getCurrentUpgrade(player, stack);
        ResonatorUpgrade next = getNextSelectableUpgrade(player, stack);
        return next != null && !next.equals(current) && setCurrentUpgrade(player, stack, next);
    }

    @Nullable
    public static ResonatorUpgrade getNextSelectableUpgrade(@Nonnull EntityPlayer viewing, ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator)) return null;
        if (!isEnhanced(stack)) return null;
        ResonatorUpgrade current = getCurrentUpgrade(viewing, stack);
        int currentOrd = current.ordinal();
        int test = currentOrd;
        do {
            test++;
            test %= ResonatorUpgrade.values().length;
            ResonatorUpgrade testUpgrade = ResonatorUpgrade.values()[test];
            if (testUpgrade.obtainable() && testUpgrade.canSwitchTo(viewing, stack) && !testUpgrade.equals(current)) {
                return testUpgrade;
            }
        } while (test != currentOrd);
        return null;
    }

    public static boolean setCurrentUpgrade(EntityPlayer setting, ItemStack stack, ResonatorUpgrade upgrade) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        if (upgrade.obtainable() && upgrade.canSwitchTo(setting, stack)) {
            NBTHelper.getPersistentData(stack)
                .setInteger("selected_upgrade", upgrade.ordinal());
            return true;
        }
        return false;
    }

    public static ItemStack setCurrentUpgradeUnsafe(ItemStack stack, ResonatorUpgrade upgrade) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator)
            || !upgrade.obtainable()) return stack;
        NBTHelper.getPersistentData(stack)
            .setInteger("selected_upgrade", upgrade.ordinal());
        return stack;
    }

    @Nonnull
    public static ResonatorUpgrade getCurrentUpgrade(@Nullable EntityPlayer viewing, ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator))
            return ResonatorUpgrade.STARLIGHT; // Fallback
        if (!isEnhanced(stack)) return ResonatorUpgrade.STARLIGHT;
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        int current = cmp.getInteger("selected_upgrade");
        ResonatorUpgrade upgrade = ResonatorUpgrade.values()[WrapMathHelper
            .clamp(current, 0, ResonatorUpgrade.values().length - 1)];
        if (!upgrade.obtainable()) {
            return ResonatorUpgrade.STARLIGHT;
        }
        if (viewing != null) {
            if (!upgrade.canSwitchTo(viewing, stack)) {
                return ResonatorUpgrade.STARLIGHT;
            }
        }
        return upgrade;
    }

    public static ItemStack setUpgradeUnlocked(ItemStack stack, ResonatorUpgrade upgrade) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator)) return stack;
        if (!isEnhanced(stack)) return stack;
        if (upgrade.obtainable()) {
            upgrade.applyUpgrade(stack);
        }
        return stack;
    }

    public static boolean hasUpgrade(ItemStack stack, ResonatorUpgrade upgrade) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator)) return false;
        if (!isEnhanced(stack)) return false;
        if (!upgrade.obtainable()) return false;
        return upgrade.hasUpgrade(stack);
    }

    public static List<ResonatorUpgrade> getUpgrades(ItemStack stack) {
        if ((stack == null || stack.stackSize <= 0) || !(stack.getItem() instanceof ItemSkyResonator))
            return Lists.newArrayList();
        if (!isEnhanced(stack)) return Lists.newArrayList(ResonatorUpgrade.STARLIGHT);
        List<ResonatorUpgrade> upgrades = Lists.newLinkedList();
        for (ResonatorUpgrade ru : ResonatorUpgrade.values()) {
            if (ru.hasUpgrade(stack) && ru.obtainable()) {
                upgrades.add(ru);
            }
        }
        return upgrades;
    }

    public static enum ResonatorUpgrade {

        STARLIGHT("starlight", (p, s) -> true),
        FLUID_FIELDS("liquid", (p, s) -> ResearchManager.getProgress(p)
            .getTierReached()
            .isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)),
        AREA_SIZE("structure", (p, s) -> ResearchManager.getProgress(p)
            .getTierReached()
            .isThisLaterOrEqual(ProgressionTier.ATTUNEMENT));

        private final ResonatorUpgradeCheck check;
        private final String appendixUpgrade;

        private ResonatorUpgrade(String appendixUpgrade, ResonatorUpgradeCheck check) {
            this.check = check;
            this.appendixUpgrade = appendixUpgrade;
        }

        public String getUnlocalizedName() {
            return "item.itemskyresonator." + appendixUpgrade;
        }

        public String getUnlocalizedUpgradeName() {
            return "item.itemskyresonator.upgrade." + appendixUpgrade + ".name";
        }

        public boolean obtainable() {
            return true;
        }

        public boolean hasUpgrade(ItemStack stack) {
            int id = ordinal();
            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            if (cmp.hasKey("upgrades", Constants.NBT.TAG_LIST)) {
                // 1.7.10: getTagList() takes name and type parameters
                NBTTagList list = cmp.getTagList("upgrades", Constants.NBT.TAG_INT);
                // 1.7.10: Need to use reflection to access the private tagList field
                try {
                    java.lang.reflect.Field tagListField = NBTTagList.class.getDeclaredField("tagList");
                    tagListField.setAccessible(true);
                    java.util.List tagList = (java.util.List) tagListField.get(list);
                    for (int i = 0; i < tagList.size(); i++) {
                        Object nbtbase = tagList.get(i);
                        if (nbtbase instanceof NBTTagInt) {
                            Integer intId = ((NBTTagInt) nbtbase).func_150287_d();
                            if (intId == id) {
                                return true;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Reflection failed, return false
                    return false;
                }
            }
            return false;
        }

        public boolean canSwitchTo(@Nonnull EntityPlayer player, ItemStack stack) {
            return hasUpgrade(stack) && check.hasAccessToUpgrade(player, stack);
        }

        public void applyUpgrade(ItemStack stack) {
            if (hasUpgrade(stack)) return;

            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            if (!cmp.hasKey("upgrades", Constants.NBT.TAG_LIST)) {
                cmp.setTag("upgrades", new NBTTagList());
            }
            // 1.7.10: getTagList() takes name and type parameters
            NBTTagList list = cmp.getTagList("upgrades", Constants.NBT.TAG_INT);
            list.appendTag(new NBTTagInt(ordinal()));
        }

        @SideOnly(Side.CLIENT)
        public void playResonatorEffects() {
            switch (this) {
                case STARLIGHT:
                    playStarlightFieldEffect();
                    break;
                default:
                    break;
            }
        }

        @SideOnly(Side.CLIENT)
        private void playStarlightFieldEffect() {
            // 1.7.10: Use Guava's Optional
            Optional<Long> seedOpt = ConstellationSkyHandler.getInstance()
                .getSeedIfPresent(Minecraft.getMinecraft().theWorld);
            if (!seedOpt.isPresent()) return;

            float nightPerc = ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(Minecraft.getMinecraft().theWorld);
            if (nightPerc >= 0.05) {
                Color c = new Color(0, 6, 58);
                BlockPos center = new BlockPos(Minecraft.getMinecraft().thePlayer);
                int offsetX = center.getX();
                int offsetZ = center.getZ();

                for (int xx = -30; xx <= 30; xx++) {
                    for (int zz = -30; zz <= 30; zz++) {

                        int topY = Minecraft.getMinecraft().theWorld
                            .getTopSolidOrLiquidBlock(offsetX + xx, offsetZ + zz);
                        BlockPos top = new BlockPos(offsetX + xx, topY, offsetZ + zz);
                        // 1.7.10: getSkyNoiseDistributionClient returns Float directly, not Optional
                        Float opFOpt = SkyCollectionHelper
                            .getSkyNoiseDistributionClient(Minecraft.getMinecraft().theWorld, top);
                        if (opFOpt == null) continue;
                        float opF = opFOpt;

                        float fPerc = (float) Math.pow((opF - 0.4F) * 1.65F, 2);
                        if (opF >= 0.4F && rand.nextFloat() <= fPerc) {
                            if (rand.nextFloat() <= fPerc && rand.nextInt(6) == 0) {
                                EffectHelper
                                    .genericFlareParticle(
                                        top.getX() + rand.nextFloat(),
                                        top.getY() + 0.15,
                                        top.getZ() + rand.nextFloat())
                                    .scale(4F)
                                    .setColor(c)
                                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                                    .gravity(0.004)
                                    .setAlphaMultiplier(nightPerc * fPerc);
                                if (opF >= 0.8F && rand.nextInt(3) == 0) {
                                    EffectHelper
                                        .genericFlareParticle(
                                            top.getX() + rand.nextFloat(),
                                            top.getY() + 0.15,
                                            top.getZ() + rand.nextFloat())
                                        .scale(0.3F)
                                        .setColor(Color.WHITE)
                                        .gravity(0.01)
                                        .setAlphaMultiplier(nightPerc);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static interface ResonatorUpgradeCheck {

        public boolean hasAccessToUpgrade(@Nonnull EntityPlayer player, ItemStack stack);

    }

}
