/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.fluid;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.block.BlockInfusedWood;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidBlockLiquidStarlight
 * Created by HellFirePvP
 * Date: 14.09.2016 / 11:38
 */
public class FluidBlockLiquidStarlight extends BlockFluidClassic {

    public FluidBlockLiquidStarlight() {
        // 1.7.10: MapColor.SILVER doesn't exist, use clothColor
        super(BlocksAS.fluidLiquidStarlight, new MaterialLiquid(MapColor.silverColor));
        // 1.7.10: Fluid blocks don't have blockState/property system
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        // 1.7.10: randomDisplayTick signature uses int coordinates
        int level = world.getBlockMetadata(x, y, z);
        double percHeight = 1D - (((double) level + 1) / 8D);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x + 0.5, y, z + 0.5);
        p.offset(0, percHeight, 0);
        p.offset(
            rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1),
            0,
            rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.2F)
            .gravity(0.006)
            .setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        if (rand.nextInt(3) == 0) {
            p = EffectHelper.genericFlareParticle(x + 0.5, y, z + 0.5);
            p.offset(0, percHeight, 0);
            p.offset(
                rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1),
                0,
                rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.2F)
                .gravity(0.006)
                .setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        }
    }

    @Override
    // 1.7.10: onBlockAdded uses int coordinates
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        interactWithAdjacent(world, x, y, z);
    }

    private void interactWithAdjacent(World world, int x, int y, int z) {
        boolean shouldCreateBlock = false;
        boolean isCold = true;

        for (EnumFacing side : EnumFacing.values()) {
            if (side != EnumFacing.DOWN) {
                // 1.7.10: Manually calculate offsets based on direction
                int ox = x, oy = y, oz = z;
                switch (side) {
                    case UP:
                        oy++;
                        break;
                    case DOWN:
                        oy--;
                        break;
                    case NORTH:
                        oz--;
                        break;
                    case SOUTH:
                        oz++;
                        break;
                    case WEST:
                        ox--;
                        break;
                    case EAST:
                        ox++;
                        break;
                }
                Block offset = world.getBlock(ox, oy, oz);
                if (offset.getMaterial()
                    .isLiquid() && !(offset instanceof FluidBlockLiquidStarlight)
                    && (offset instanceof BlockFluidBase || offset instanceof BlockLiquid)) {
                    int temp = offset instanceof BlockFluidBase ? BlockFluidBase.getTemperature(world, ox, oy, oz)
                        : (offset.getMaterial() == Material.lava ? FluidRegistry.LAVA.getTemperature()
                            : offset.getMaterial() == Material.water ? FluidRegistry.WATER.getTemperature() : 100);
                    isCold = temp <= 300; // colder or equals water.
                    shouldCreateBlock = true;
                    break;
                }
            }
        }

        if (shouldCreateBlock) {
            if (isCold) {
                if (Config.liquidStarlightIce) {
                    world.setBlock(x.posX, x.posY, x.posZ, y, z, Blocks.ice, 0, 3);
                } else {
                    world.setBlock(x.posX, x.posY, x.posZ, y, z, Blocks.cobblestone, 0, 3);
                }
            } else {
                if (Config.liquidStarlightSand) {
                    if (Config.liquidStarlightAquamarine && world.rand.nextInt(900) == 0) {
                        world.setBlock(
                            x,
                            y,
                            z,
                            BlocksAS.customSandOre,
                            BlockCustomSandOre.OreType.AQUAMARINE.ordinal(),
                            3);
                    } else {
                        world.setBlock(x.posX, x.posY, x.posZ, y, z, Blocks.sand, 0, 3);
                    }
                } else {
                    world.setBlock(x.posX, x.posY, x.posZ, y, z, Blocks.cobblestone, 0, 3);
                }
            }

            // 1.7.10: playSoundEffect uses string name for sound
            world.playSoundEffect(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                "random.fizz", // Sound string for 1.7.10
                0.5F,
                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
            for (int i = 0; i < 10; ++i) {
                // 1.7.10: spawnParticle uses string name for particle type
                world.spawnParticle(
                    "largesmoke",
                    x + Math.random(),
                    y + Math.random(),
                    z + Math.random(),
                    0.0D,
                    0.0D,
                    0.0D);
            }
        }

    }

    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        return !world.getBlock(x, y, z)
            .getMaterial()
            .isLiquid() && super.displaceIfPossible(world, x, y, z);
    }

    // 1.7.10: isEntityInsideMaterial doesn't exist - removed

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        super.onEntityCollidedWithBlock(world, x, y, z, entity);

        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0, true));
        } else if (entity instanceof EntityItem) {
            ItemStack contained = ((EntityItem) entity).getEntityItem();
            if (!(contained == null || contained.stackSize <= 0)) {
                if (entity.worldObj.isRemote) return;

                if (Config.liquidStarlightInfusedWood) {
                    interactInfusedWood(contained, entity);
                }
            }
        }
    }

    private void interactInfusedWood(ItemStack contained, Entity entityIn) {
        if (ItemUtils.hasOreName(contained, "logWood")) {
            contained = ItemUtils.copyStackWithSize(contained, contained.stackSize - 1);
            if ((contained == null || contained.stackSize <= 0)) {
                entityIn.setDead();
            } else {
                ((EntityItem) entityIn).setEntityItemStack(contained);
            }
            ItemUtils.dropItemNaturally(
                entityIn.worldObj,
                entityIn.posX,
                entityIn.posY,
                entityIn.posZ,
                BlockInfusedWood.WoodType.RAW.asStack());
        }
    }
}
