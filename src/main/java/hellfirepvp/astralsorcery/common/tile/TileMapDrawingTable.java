/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.awt.*;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.starmap.ActiveStarMap;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.ItemInfusedGlass;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileMapDrawingTable
 * Created by HellFirePvP
 * Date: 18.03.2017 / 20:02
 */
public class TileMapDrawingTable extends TileSkybound {

    public static int RUN_TIME = 10 * 20;

    private ItemStack slotIn = null;
    private ItemStack slotGlassLens = null;

    private int runTick = 0;

    @Override
    protected void onFirstTick() {}

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) {
            playWorkEffects();
        } else {
            if (ConstellationSkyHandler.getInstance()
                .isNight(worldObj) && doesSeeSky()
                && !(slotGlassLens == null || slotGlassLens.stackSize <= 0)
                && slotGlassLens.getItem() instanceof ItemInfusedGlass) {
                ActiveStarMap map = ItemInfusedGlass.getMapEngravingInformations(slotGlassLens);
                if (map != null && !(slotIn == null || slotIn.stackSize <= 0)
                    && !hasParchment()
                    && ((slotIn.isItemEnchantable()
                        && map.tryApplyEnchantments(ItemUtils.copyStackWithSize(slotIn, slotIn.stackSize)))
                        || (slotIn.getItem() instanceof ItemPotion
                            && false /* PotionUtils not available in 1.7.10 */))) {
                    runTick++;
                    if (runTick > RUN_TIME) {
                        if (slotIn.isItemEnchantable()) {
                            if (slotIn.getItem() instanceof ItemBook
                                && map.tryApplyEnchantments(ItemUtils.copyStackWithSize(slotIn, slotIn.stackSize))) {
                                // Items.ENCHANTED_BOOK not available in 1.7.10
                                slotIn = new ItemStack(Items.book);
                            }
                            map.tryApplyEnchantments(slotIn);
                            // In 1.7.10, attemptDamageItem signature is (int damage, Random rand)
                            if (slotGlassLens.attemptDamageItem(1, rand)) {
                                slotGlassLens.stackSize -= 1;
                                // TODO: Add sound effect
                            }
                        } else if (false /* PotionUtils not available in 1.7.10 */) {
                            map.tryApplyPotionEffects(slotIn);

                            // Sound effect disabled - needs 1.7.10 sound string
                            if (rand.nextInt(3) == 0 && slotGlassLens.attemptDamageItem(1, rand)) {
                                slotGlassLens.stackSize -= 1;
                            }
                        }
                        runTick = 0;
                    }
                    markForUpdate();
                } else {
                    if (runTick > 0) {
                        runTick = 0;
                        markForUpdate();
                    }
                }
            } else {
                if (runTick > 0) {
                    runTick = 0;
                    markForUpdate();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playWorkEffects() {
        if (getPercRunning() <= 1E-4) return;
        Vector3 offset = new Vector3(-5.0 / 16.0, 1.505, -3.0 / 16.0);
        int random = rand.nextInt(12);
        if (random > 5) {
            offset.addX(24.0 / 16.0);
        }
        offset.addZ((random % 6) * (4.0 / 16.0));
        offset.add(rand.nextFloat() * 0.1, 0, rand.nextFloat() * 0.1)
            .add(xCoord, yCoord, zCoord);

        Color c;
        switch (random) {
            case 0:
                c = new Color(0xFF0800);
                break;
            case 1:
                c = new Color(0xFFCC00);
                break;
            case 2:
                c = new Color(0x6FFF00);
                break;
            case 3:
                c = new Color(0x00FCFF);
                break;
            case 4:
                c = new Color(0x0028FF);
                break;
            case 5:
                c = new Color(0xFF00FE);
                break;
            case 6:
                c = new Color(0xF07800);
                break;
            case 7:
                c = new Color(0xB4F000);
                break;
            case 8:
                c = new Color(0x01F000);
                break;
            case 9:
                c = new Color(0x007AF0);
                break;
            case 10:
                c = new Color(0x3900F0);
                break;
            case 11:
            default:
                c = new Color(0xf0007B);
                break;
        }

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(offset.getX(), offset.getY(), offset.getZ());
        p.scale(rand.nextFloat() * 0.1F + 0.15F)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.gravity(0.006F)
            .setMaxAge(rand.nextInt(30) + 35);
        p.setColor(c);

        if (rand.nextFloat() < getPercRunning()) {
            Vector3 center = new Vector3(this).add(0.5, 1, 0.5);

            AstralSorcery.proxy.fireLightning(worldObj, offset, center, c);
            p = EffectHelper.genericFlareParticle(offset.getX(), offset.getY(), offset.getZ());
            p.scale(rand.nextFloat() * 0.1F + 0.15F)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.gravity(0.004F)
                .setMaxAge(rand.nextInt(30) + 35);
            p.setColor(c);
            Vector3 mov = center.clone()
                .subtract(offset)
                .normalize()
                .multiply(0.05 * rand.nextFloat());
            p.motion(mov.getX(), mov.getY(), mov.getZ());
        }

        if (getPercRunning() > 0.1F) {
            if (rand.nextInt(3) == 0) {
                EffectHandler.getInstance()
                    .lightbeam(
                        offset.clone()
                            .addY(0.4 + rand.nextFloat() * 0.3),
                        offset,
                        0.2F)
                    .setColorOverlay(c);
            }
            if (rand.nextInt(4) == 0) {
                switch (rand.nextInt(3)) {
                    case 0:
                        c = new Color(0x0054C4);
                        break;
                    case 1:
                        c = new Color(0x7729CA);
                        break;
                    case 2:
                        c = new Color(0x0028FF);
                        break;
                    default:
                        break;
                }
                offset = new Vector3(this).add(rand.nextFloat(), 1, rand.nextFloat());
                EffectHandler.getInstance()
                    .lightbeam(
                        offset.clone()
                            .addY(1 + rand.nextFloat() * 0.4),
                        offset,
                        0.5F)
                    .setColorOverlay(c);
            }
        }

    }

    public int addParchment(int amt) {
        if (!(slotIn == null || slotIn.stackSize <= 0)) {
            if (slotIn.getItem() instanceof ItemCraftingComponent
                && slotIn.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()) {
                int current = slotIn.stackSize;
                if (current + amt <= 64) {
                    current += amt;
                    slotIn.stackSize = current;
                    markForUpdate();
                    return 0;
                } else {
                    int ret = (current + amt) - 64;
                    slotIn.stackSize = 64;
                    markForUpdate();
                    return ret;
                }
            }
            return amt;
        } else {
            slotIn = ItemCraftingComponent.MetaType.PARCHMENT.asStack();
            slotIn.stackSize = amt;
            markForUpdate();
            return 0;
        }
    }

    @Nonnull
    public ItemStack getSlotGlassLens() {
        return slotGlassLens;
    }

    @Nonnull
    public ItemStack getSlotIn() {
        return slotIn;
    }

    public float getPercRunning() {
        return ((float) runTick) / ((float) RUN_TIME);
    }

    public void putSlotIn(ItemStack stack) {
        this.slotIn = ItemUtils.copyStackWithSize(stack, 1);
        markForUpdate();
    }

    public void putGlassLens(ItemStack glassLens) {
        this.slotGlassLens = ItemUtils.copyStackWithSize(glassLens, Math.min(glassLens.stackSize, 1));
        markForUpdate();
    }

    public boolean hasParchment() {
        return !(slotIn == null || slotIn.stackSize <= 0) && slotIn.getItem() instanceof ItemCraftingComponent
            && slotIn.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()
            && slotIn.stackSize > 0;
    }

    public boolean hasUnengravedGlass() {
        return !(slotGlassLens == null || slotGlassLens.stackSize <= 0)
            && slotGlassLens.getItem() instanceof ItemInfusedGlass
            && ItemInfusedGlass.getMapEngravingInformations(slotGlassLens) == null;
    }

    public void dropContents() {
        if (!(slotIn == null || slotIn.stackSize <= 0)) {
            ItemUtils.dropItemNaturally(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, slotIn);
            slotIn = null;
        }
        if (!(slotGlassLens == null || slotGlassLens.stackSize <= 0)) {
            ItemUtils.dropItemNaturally(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, slotGlassLens);
            slotGlassLens = null;
        }
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.runTick = compound.getInteger("runTick");

        // In 1.7.10, use ItemStack.loadItemStackFromNBT()
        this.slotIn = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("slotIn"));
        this.slotGlassLens = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("slotGlassLens"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setInteger("runTick", this.runTick);

        // 1.7.10: Use NBTHelper.setStack() instead of writeToNBT()
        NBTHelper.setStack(compound, "slotIn", slotIn);
        NBTHelper.setStack(compound, "slotGlassLens", slotGlassLens);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(0.5, 0.5, 0.5);
    }

    public void tryEngraveGlass(List<DrawnConstellation> constellations) {
        if (hasParchment() && hasUnengravedGlass()) {
            getSlotIn().stackSize -= 1;
            ItemInfusedGlass.setMapEngravingInformations(getSlotGlassLens(), ActiveStarMap.compile(constellations));
            markForUpdate();
            Vector3 pos = new Vector3(this);
            PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.ENGRAVE_LENS, pos);
            PacketChannel.CHANNEL
                .sendToAllAround(ev, PacketChannel.pointFromPos(worldObj, new BlockPos(xCoord, yCoord, zCoord), 16.0));
        }
    }

    public boolean burnParchment() {
        if (hasParchment() && hasUnengravedGlass()) {
            getSlotIn().stackSize -= 1;
            markForUpdate();
            Vector3 pos = new Vector3(this);
            PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.BURN_PARCHMENT, pos);
            PacketChannel.CHANNEL
                .sendToAllAround(ev, PacketChannel.pointFromPos(worldObj, new BlockPos(xCoord, yCoord, zCoord), 16.0));
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void burnParchmentEffects(PktParticleEvent pktParticleEvent) {
        if (Minecraft.getMinecraft().theWorld == null) return;

        Vector3 offset = pktParticleEvent.getVec();
        // Sound effect disabled - needs 1.7.10 sound string
        // In 1.7.10, playSound has signature: (String name, double x, double y, double z, float volume, float pitch)
        // Minecraft.getMinecraft().theWorld.playSound(
        // "random.fizz", // TODO: correct sound string
        // offset.getX(),
        // offset.getY(),
        // offset.getZ(),
        // rand.nextFloat() * 0.5F + 1F,
        // rand.nextFloat() * 0.1F + 0.9F);

        offset.add(-0.2, 1.1, -0.2);
        for (int i = 0; i < 50; i++) {
            Vector3 at = offset.clone()
                .add(rand.nextFloat() * 1.4, 0, rand.nextFloat() * 1.4);
            // In 1.7.10, use particle name string instead of EnumParticleTypes
            Minecraft.getMinecraft().theWorld.spawnParticle(
                "flame",
                at.getX(),
                at.getY(),
                at.getZ(),
                rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        }

        for (int i = 0; i < 70; i++) {
            Vector3 at = offset.clone()
                .add(rand.nextFloat() * 1.4, 0, rand.nextFloat() * 1.4);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            p.gravity(0.004)
                .scale(rand.nextFloat() * 0.1F + 0.2F)
                .setMaxAge(rand.nextInt(20) + 20);
            p.motion(
                rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1));
            p.setColor(new Color(Color.HSBtoRGB(rand.nextFloat() * 360, 1F, 1F)));
        }

    }

    @SideOnly(Side.CLIENT)
    public static void engraveLensEffects(PktParticleEvent pktParticleEvent) {

    }
}
