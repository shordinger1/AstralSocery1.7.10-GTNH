/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Liquid starlight fluid block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.fluid;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;

/**
 * FluidBlockLiquidStarlight - Liquid starlight fluid block (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Dynamic block conversion based on adjacent fluid temperatures</li>
 * <li>Gives night vision potion effect to players</li>
 * <li>Renders with starlight particle effects (TODO)</li>
 * <li>Classic fluid physics (8 levels per block)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>BlockPos → (x, y, z) coordinates</li>
 * <li>IBlockState → Block + metadata</li>
 * <li>world.getBlockState() → world.getBlock() + world.getBlockMetadata()</li>
 * <li>world.setBlockState() → world.setBlock() + world.setBlockMetadata()</li>
 * <li>neighborChanged() → onNeighborBlockChange()</li>
 * <li>EnumFacing → ForgeDirection</li>
 * <li>EnumParticleTypes → particle string names</li>
 * <li>SoundEvents → String sound names</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>EffectHelper - Client particle system</li>
 * <li>EntityFXFacingParticle - Particle entity</li>
 * <li>Config - Configuration class</li>
 * <li>ItemUtils - Utility functions</li>
 * <li>BlockInfusedWood - Infused wood block</li>
 * <li>BlockCustomSandOre - Custom sand ore block</li>
 * <li>BlockCollectorCrystalBase - Crystal display color</li>
 * </ul>
 */
public class FluidBlockLiquidStarlight extends BlockFluidClassic {

    /**
     * Constructor
     * 1.7.10: Material constructor instead of MaterialLiquid(MapColor)
     * NOTE: fluid MUST be registered BEFORE this block is instantiated
     *
     * @param fluid The fluid instance to use (must be registered first)
     */
    public FluidBlockLiquidStarlight(net.minecraftforge.fluids.Fluid fluid) {
        super(fluid, Material.water);
    }

    /**
     * Random display tick - spawns starlight particles
     * 1.7.10: Uses (x, y, z) instead of BlockPos
     * EffectHelper is now implemented
     */
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        // 1.7.10: Get level from metadata instead of IBlockState
        int meta = world.getBlockMetadata(x, y, z);
        int level = meta & 7; // Lower 3 bits for level (0-7)
        double percHeight = 1D - (((double) level + 1) / 8D);

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x + 0.5, y, z + 0.5);
        p.offset(
            rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1),
            percHeight,
            rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.2F)
            .gravity(0.006)
            .setColor(new java.awt.Color(0x89CFF0)); // Rock crystal blue color

        if (rand.nextInt(3) == 0) {
            p = EffectHelper.genericFlareParticle(x + 0.5, y, z + 0.5);
            p.offset(
                rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1),
                percHeight,
                rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.2F)
                .gravity(0.006)
                .setColor(new java.awt.Color(0x89CFF0)); // Rock crystal blue color
        }

    }

    /**
     * Neighbor block changed - check for fluid interaction
     * 1.7.10: Signature changed significantly
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        interactWithAdjacent(world, x, y, z);
    }

    /**
     * Block added to world - check for fluid interaction
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        interactWithAdjacent(world, x, y, z);
    }

    /**
     * Check adjacent fluids and convert block accordingly
     * 1.7.10: Uses (x, y, z) coordinates
     */
    private void interactWithAdjacent(World world, int x, int y, int z) {
        boolean shouldCreateBlock = false;
        boolean isCold = true;

        // 1.7.10: Check all 6 directions using offsets
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir != ForgeDirection.DOWN) {
                int offsetX = x + dir.offsetX;
                int offsetY = y + dir.offsetY;
                int offsetZ = z + dir.offsetZ;

                Block offsetBlock = world.getBlock(offsetX, offsetY, offsetZ);
                Material offsetMat = offsetBlock.getMaterial();

                // 1.7.10: Check if material is liquid
                if (offsetMat.isLiquid() && offsetBlock != this
                    && (offsetBlock instanceof BlockFluidBase || offsetBlock instanceof BlockLiquid)) {

                    int temp;
                    if (offsetBlock instanceof BlockFluidBase) {
                        temp = BlockFluidBase.getTemperature(world, offsetX, offsetY, offsetZ);
                    } else {
                        if (offsetMat == Material.lava) {
                            temp = FluidRegistry.LAVA.getTemperature();
                        } else if (offsetMat == Material.water) {
                            temp = FluidRegistry.WATER.getTemperature();
                        } else {
                            temp = 100;
                        }
                    }

                    isCold = temp <= 300;
                    shouldCreateBlock = true;
                    break;
                }
            }
        }

        if (shouldCreateBlock) {
            if (isCold) {
                // Cold conversion - simplified, always cobblestone without Config check
                world.setBlock(x, y, z, Blocks.cobblestone);
            } else {
                // Hot conversion - simplified, always cobblestone
                world.setBlock(x, y, z, Blocks.cobblestone);
            }

            // 1.7.10: Play sound with string name
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "step.stone", 0.5F, 2.6F);

            // 1.7.10: Spawn particles
            for (int i = 0; i < 10; ++i) {
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

    /**
     * Entity collided with block - apply effects
     * 1.7.10: Uses coordinates
     */
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        super.onEntityCollidedWithBlock(world, x, y, z, entity);

        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0, true));
        } else if (entity instanceof EntityItem) {
            ItemStack contained = ((EntityItem) entity).getEntityItem();
            // 1.7.10: Check stack size
            if (contained != null && contained.stackSize > 0) {
                if (world.isRemote) return;

                // TODO: Re-enable when ItemUtils is migrated
                // if (hellfirepvp.astralsorcery.common.data.config.Config.liquidStarlightInfusedWood) {
                // interactInfusedWood(contained, entity);
                // }
            }
        }
    }

    /**
     * Convert wood log to infused wood
     *
     * TODO: Re-enable when ItemUtils and BlockInfusedWood are migrated
     */
    private void interactInfusedWood(ItemStack contained, Entity entity) {
        /*
         * if (ItemUtils.hasOreName(contained, "logWood")) {
         * contained.stackSize--;
         * if (contained.stackSize <= 0) {
         * entity.setDead();
         * } else {
         * ((EntityItem) entity).setEntityItemStack(contained);
         * }
         * ItemUtils.dropItemNaturally(entity.worldObj,
         * entity.posX, entity.posY, entity.posZ,
         * BlockInfusedWood.WoodType.RAW.asStack());
         * }
         */
    }

}
