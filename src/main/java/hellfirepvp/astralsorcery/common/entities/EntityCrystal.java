/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.awt.*;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockGemCrystals;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCrystal
 * Created by HellFirePvP
 * Date: 08.12.2016 / 19:11
 */
public class EntityCrystal extends EntityItemHighlighted implements EntityStarlightReacttant {

    private static final AxisAlignedBB boxCraft = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);

    private static final int MODE_GROW = 0;
    private static final int MODE_GEM = 1;

    public static final int TOTAL_MERGE_TIME = 60 * 20;
    private int inertMergeTick = 0;

    public EntityCrystal(World worldIn) {
        super(worldIn);
    }

    public EntityCrystal(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCrystal(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        Item i = stack.getItem();
        if (i instanceof ItemHighlighted) {
            applyColor(((ItemHighlighted) i).getHightlightColor(stack));
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (age + 5 >= this.lifespan) {
            age = 0;
        }

        if (Config.craftingLiqCrystalGrowth) {
            checkIncreaseConditions();
        }
    }

    private void checkIncreaseConditions() {
        if (worldObj.isRemote) { // 1.7.10: Use worldObj instead of getWorld()
            int mode = getCraftMode();
            if (mode == MODE_GROW) {
                spawnCraftingParticles();
            } else if (mode == MODE_GEM) {
                spawnFormParticles();
            }
        } else {
            if (CrystalProperties.getCrystalProperties(getEntityItem()) == null) { // 1.7.10: Use getEntityItem()
                setDead();
                return;
            }
            int mode = getCraftMode();
            if (mode != -1) {
                inertMergeTick++;
                if (inertMergeTick >= TOTAL_MERGE_TIME && rand.nextInt(300) == 0) {
                    if (mode == MODE_GROW) {
                        increaseSize();
                    } else if (mode == MODE_GEM) {
                        spawnGemCluster();
                    }
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    private void spawnGemCluster() {
        // 1.7.10: Use new BlockPos(entity) instead of getPosition()
        BlockPos pos = new BlockPos(this);
        AxisAlignedBB searchBox = boxCraft.copy();
        searchBox.minX += pos.getX();
        searchBox.minY += pos.getY();
        searchBox.minZ += pos.getZ();
        searchBox.maxX += pos.getX();
        searchBox.maxY += pos.getY();
        searchBox.maxZ += pos.getZ();
        // 1.7.10: getEntitiesWithinAABBExcludingEntity instead of getEntitiesInAABBexcluding
        @SuppressWarnings("unchecked")
        List<Entity> foundEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);
        // Filter for glowstone dust items
        boolean hasGlowstone = false;
        for (Entity e : foundEntities) {
            if (e instanceof EntityItemHighlighted) {
                ItemStack stack = ((EntityItemHighlighted) e).getEntityItem();
                if (stack != null && ItemUtils.hasOreName(stack, OreDictAlias.ITEM_GLOWSTONE_DUST)) {
                    hasGlowstone = true;
                    e.setDead();
                    break;
                }
            }
        }
        if (hasGlowstone) {
            this.setDead();

            worldObj.setBlock(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                BlocksAS.gemCrystals,
                BlockGemCrystals.GrowthStageType.STAGE_0.ordinal(),
                3);
        }
    }

    private void increaseSize() {
        BlockPos pos = new BlockPos(this);
        worldObj.setBlockToAir(pos.getX(), pos.getY(), pos.getZ()); // 1.7.10: setBlockToAir takes x,y,z
        // 1.7.10: Use expand() instead of grow(), and offset coordinates manually
        AxisAlignedBB searchBox = boxCraft.copy();
        searchBox.minX += posX;
        searchBox.minY += posY;
        searchBox.minZ += posZ;
        searchBox.maxX += posX;
        searchBox.maxY += posY;
        searchBox.maxZ += posZ;
        searchBox = searchBox.expand(0.1, 0.1, 0.1);
        // 1.7.10: getEntitiesWithinAABBExcludingEntity instead of getEntitiesInAABBexcluding
        @SuppressWarnings("unchecked")
        List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);
        // Filter for rock crystal items`
        boolean hasRockCrystal = false;
        for (Entity e : foundItems) {
            if (e instanceof EntityItemHighlighted) {
                ItemStack stack = ((EntityItemHighlighted) e).getEntityItem();
                if (stack != null && stack.getItem() instanceof ItemRockCrystalBase) {
                    hasRockCrystal = true;
                    break;
                }
            }
        }
        if (!hasRockCrystal) {
            ItemStack stack = getEntityItem(); // 1.7.10: Use getEntityItem()
            CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
            int max = CrystalProperties.getMaxSize(stack);
            if (prop.getFracturation() > 0) {
                int frac = prop.getFracturation();
                int cut = prop.getCollectiveCapability();
                if (frac >= 90 && cut >= 100 && frac >= cut - 10 && rand.nextBoolean()) {
                    cut++;
                }
                int purity = prop.getPurity();
                if (frac >= 90 && purity >= 100 && frac >= purity - 10 && rand.nextBoolean()) {
                    purity++;
                }
                CrystalProperties newProp = new CrystalProperties(
                    prop.getSize(),
                    purity,
                    cut,
                    Math.max(0, frac - 25 - rand.nextInt(30)),
                    prop.getSizeOverride());
                CrystalProperties.applyCrystalProperties(stack, newProp);
                return;
            }
            if (Config.canCrystalGrowthYieldDuplicates && prop.getSize() >= max && rand.nextInt(6) == 0) {
                ItemStack newStack = (stack.getItem() instanceof ItemCelestialCrystal
                    || stack.getItem() instanceof ItemTunedCelestialCrystal)
                        ? ItemRockCrystalBase.createRandomCelestialCrystal()
                        : ItemRockCrystalBase.createRandomBaseCrystal();
                CrystalProperties newProp = new CrystalProperties(
                    rand.nextInt(100) + 20,
                    Math.min(prop.getPurity() + rand.nextInt(10), 100),
                    rand.nextInt(40) + 30,
                    0,
                    prop.getSizeOverride());
                CrystalProperties.applyCrystalProperties(newStack, newProp);
                ItemUtils.dropItemNaturally(worldObj, posX, posY, posZ, newStack); // 1.7.10: Use worldObj

                CrystalProperties.applyCrystalProperties(
                    stack,
                    new CrystalProperties(
                        rand.nextInt(300) + 100,
                        prop.getPurity(),
                        rand.nextInt(40) + 30,
                        prop.getFracturation(),
                        prop.getSizeOverride()));
            } else {
                int grow = rand.nextInt(90) + 40;
                max = Math.min(prop.getSize() + grow, max);
                CrystalProperties.applyCrystalProperties(
                    stack,
                    new CrystalProperties(
                        max,
                        prop.getPurity(),
                        prop.getCollectiveCapability(),
                        prop.getFracturation(),
                        prop.getSizeOverride()));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnFormParticles() {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        p.motion(
            rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
        p.gravity(0.04);
        p.scale(0.2F)
            .setColor(Color.YELLOW);
    }

    @SideOnly(Side.CLIENT)
    private void spawnCraftingParticles() {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        p.motion(
            rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
        p.gravity(0.01);
        p.scale(0.2F)
            .setColor(getHighlightColor());
    }

    private int getCraftMode() {
        if (!isInLiquidStarlight(this)) return -1;

        // 1.7.10: Use new BlockPos(entity) instead of getPosition()
        BlockPos pos = new BlockPos(this);
        AxisAlignedBB searchBox = boxCraft.copy();
        searchBox.minX += pos.getX();
        searchBox.minY += pos.getY();
        searchBox.minZ += pos.getZ();
        searchBox.maxX += pos.getX();
        searchBox.maxY += pos.getY();
        searchBox.maxZ += pos.getZ();
        // 1.7.10: getEntitiesWithinAABBExcludingEntity instead of getEntitiesInAABBexcluding
        @SuppressWarnings("unchecked")
        List<Entity> foundEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);
        if (foundEntities.size() <= 0) {
            return MODE_GROW;
        }

        // Filter for glowstone dust items
        boolean hasGlowstone = false;
        for (Entity e : foundEntities) {
            if (e instanceof EntityItemHighlighted) {
                ItemStack stack = ((EntityItemHighlighted) e).getEntityItem();
                if (stack != null && ItemUtils.hasOreName(stack, OreDictAlias.ITEM_GLOWSTONE_DUST)) {
                    hasGlowstone = true;
                    break;
                }
            }
        }
        return hasGlowstone ? MODE_GEM : -1;
    }

}
