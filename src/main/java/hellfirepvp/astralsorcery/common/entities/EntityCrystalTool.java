/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalSword;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalToolBase;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCrystalTool
 * Created by HellFirePvP
 * Date: 10.05.2017 / 17:42
 */
public class EntityCrystalTool extends EntityItem implements EntityStarlightReacttant {

    private static final AxisAlignedBB boxCraft = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);

    public static final int TOTAL_MERGE_TIME = 50 * 20;
    private int inertMergeTick = 0;

    public EntityCrystalTool(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCrystalTool(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public EntityCrystalTool(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (age + 5 >= this.lifespan) {
            age = 0;
        }

        if (Config.craftingLiqCrystalToolGrowth) {
            checkIncreaseConditions();
        }
    }

    private void checkIncreaseConditions() {
        if (worldObj.isRemote) { // 1.7.10: Use worldObj instead of getWorld()
            if (canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if (getProperties() == null) {
                setDead();
            }
            if (canCraft()) {
                inertMergeTick++;
                if (inertMergeTick >= TOTAL_MERGE_TIME && rand.nextInt(300) == 0) {
                    increaseSize();
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    @Nullable
    private ToolCrystalProperties getProperties() {
        // 1.7.10: Use stackSize == 0 instead of isEmpty()
        if (getEntityItem() == null || getEntityItem().stackSize == 0) return null;
        if (getEntityItem().getItem() instanceof ItemCrystalToolBase) {
            return ItemCrystalToolBase.getToolProperties(getEntityItem());
        }
        if (getEntityItem().getItem() instanceof ItemCrystalSword) {
            return ItemCrystalSword.getToolProperties(getEntityItem());
        }
        return null;
    }

    private void applyProperties(ToolCrystalProperties properties) {
        // 1.7.10: Use stackSize == 0 instead of isEmpty()
        if (getEntityItem() == null || getEntityItem().stackSize == 0) return;
        if (getEntityItem().getItem() instanceof ItemCrystalToolBase) {
            ItemCrystalToolBase.setToolProperties(getEntityItem(), properties);
        }
        if (getEntityItem().getItem() instanceof ItemCrystalSword) {
            ItemCrystalSword.setToolProperties(getEntityItem(), properties);
        }
    }

    private void increaseSize() {
        // 1.7.10: Use new BlockPos(entity) instead of getPosition()
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
        // Filter for crystal tool items
        boolean hasCrystalTool = false;
        for (Entity e : foundItems) {
            if (e instanceof EntityItem) {
                ItemStack stack = ((EntityItem) e).getEntityItem();
                if (stack != null && (stack.getItem() instanceof ItemCrystalToolBase
                    || stack.getItem() instanceof ItemCrystalSword)) {
                    hasCrystalTool = true;
                    break;
                }
            }
        }

        if (!hasCrystalTool) {
            CrystalProperties prop = getProperties();
            if (prop != null) {
                int max = CrystalProperties.getMaxSize(getEntityItem());
                int grow = rand.nextInt(250) + 100;
                max = Math.min(prop.getSize() + grow, max);
                int cut = Math.max(0, prop.getCollectiveCapability() - (rand.nextInt(10) + 10));
                applyProperties(
                    new ToolCrystalProperties(
                        max,
                        prop.getPurity(),
                        cut,
                        prop.getFracturation(),
                        prop.getSizeOverride()));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnCraftingParticles() {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        p.motion(
            rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.04 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1));
        p.gravity(0.01);
        p.scale(0.2F)
            .setColor(BlockCollectorCrystal.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
    }

    private boolean canCraft() {
        if (!isInLiquidStarlight(this)) return false;

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
        return foundEntities.size() <= 0;
    }
}
