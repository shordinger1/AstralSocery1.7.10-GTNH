/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal entity - grows and forms in liquid starlight
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.*;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockGemCrystals;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * EntityCrystal - Crystal entity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Grows in liquid starlight over time</li>
 * <li>Forms gem clusters when combined with glowstone dust</li>
 * <li>Renders with highlight color and particles</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Entity.getEntityWorld() → Entity.worldObj</li>
 * <li>world.getEntitiesInAABBexcluding() → world.getEntitiesWithinAABBExcludingEntity()</li>
 * <li>boxCraft.offset(getPosition()) → boxCraft.getBoundingBox(x, y, z)</li>
 * <li>boxCraft.grow() → No equivalent, calculate bounds manually</li>
 * <li>world.setBlockState() → world.setBlock() + metadata</li>
 * <li>BlockPos → (x, y, z) coordinates</li>
 * <li>AxisAlignedBB.offset() → AxisAlignedBB.offset() - same method</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>EffectHelper - Client particle system</li>
 * <li>EntityFXFacingParticle - Particle entity</li>
 * <li>Config - Configuration class</li>
 * <li>EntityUtils - Entity utility functions</li>
 * <li>ItemUtils - Item utility functions</li>
 * <li>OreDictAlias - Ore dictionary aliases</li>
 * <li>ItemHighlighted - Highlighted item interface</li>
 * </ul>
 */
public class EntityCrystal extends EntityItemHighlighted implements EntityStarlightReacttant {

    // 1.7.10: AxisAlignedBB constructor is (x1, y1, z1, x2, y2, z2)
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
        // 1.7.10: Apply highlight color if item implements ItemHighlighted
        if (stack.getItem() instanceof ItemHighlighted) {
            applyColor(((ItemHighlighted) stack.getItem()).getHighlightColor(stack));
        }
    }

    /**
     * Entity update tick
     * 1.7.10: onUpdate() instead of onUpdate()
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        if (age + 5 >= this.lifespan) {
            age = 0;
        }

        if (hellfirepvp.astralsorcery.common.data.config.Config.craftingLiqCrystalGrowth) {
            checkIncreaseConditions();
        }
    }

    private void checkIncreaseConditions() {
        // 1.7.10: world.isRemote instead of world.isRemote
        if (worldObj.isRemote) {
            int mode = getCraftMode();
            if (mode == MODE_GROW) {
                spawnCraftingParticles();
            } else if (mode == MODE_GEM) {
                spawnFormParticles();
            }
        } else {
            ItemStack item = getEntityItem();
            if (CrystalProperties.getCrystalProperties(item) == null) {
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

    /**
     * Spawn gem cluster at crystal location
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    private void spawnGemCluster() {
        // 1.7.10: Get position as coordinates
        int x = (int) posX;
        int y = (int) posY;
        int z = (int) posZ;

        // Find glowstone dust nearby
        // 1.7.10: getEntitiesWithinAABBExcludingEntity with 2 params, then filter manually
        List<Entity> allEntities = worldObj
            .getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
        List<Entity> foundEntities = new java.util.ArrayList<Entity>();
        for (Entity entity : allEntities) {
            if (entity instanceof net.minecraft.entity.item.EntityItem) {
                net.minecraft.entity.item.EntityItem item = (net.minecraft.entity.item.EntityItem) entity;
                ItemStack stack = item.getEntityItem();
                if (ItemUtils.hasOreName(stack, "dustGlowstone")) {
                    foundEntities.add(entity);
                }
            }
        }
        if (foundEntities.size() == 1) {
            foundEntities.get(0)
                .setDead();
            this.setDead();
            // 1.7.10: setBlock() instead of setBlockState()
            worldObj.setBlock(x, y, z, BlocksAS.gemCrystals, BlockGemCrystals.CrystalStage.STAGE_0.getMetadata(), 3);
        }
    }

    /**
     * Increase crystal size
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    private void increaseSize() {
        int x = (int) posX;
        int y = (int) posY;
        int z = (int) posZ;

        // 1.7.10: setBlockToAir(x, y, z) instead of setBlockToAir(pos)
        worldObj.setBlockToAir(x, y, z);

        // TODO: Re-enable when EntityUtils is migrated
        /*
         * AxisAlignedBB searchBox = boxCraft.getBoundingBox(x, y, z);
         * // 1.7.10: grow() doesn't exist, calculate bounds manually
         * searchBox = AxisAlignedBB.getBoundingBox(
         * searchBox.minX - 0.1, searchBox.minY - 0.1, searchBox.minZ - 0.1,
         * searchBox.maxX + 0.1, searchBox.maxY + 0.1, searchBox.maxZ + 0.1);
         * List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox,
         * EntityUtils.selectItemClassInstaceof(ItemRockCrystalBase.class));
         */

        // Simplified: only grow if no other crystals nearby
        AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);

        if (foundItems.size() <= 0) {
            ItemStack stack = getEntityItem();
            CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
            if (prop == null) {
                setDead();
                return;
            }

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
                setEntityItemStack(stack);
                return;
            }

            if (hellfirepvp.astralsorcery.common.data.config.Config.canCrystalGrowthYieldDuplicates
                && prop.getSize() >= max
                && rand.nextInt(6) == 0) {
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

                // Drop duplicate crystal naturally
                ItemUtils.dropItemNaturally(worldObj, posX, posY, posZ, newStack);

                CrystalProperties.applyCrystalProperties(
                    stack,
                    new CrystalProperties(
                        rand.nextInt(300) + 100,
                        prop.getPurity(),
                        rand.nextInt(40) + 30,
                        prop.getFracturation(),
                        prop.getSizeOverride()));
                setEntityItemStack(stack);
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
                setEntityItemStack(stack);
            }
        }
    }

    /**
     * Spawn formation particles
     * 1.7.10: Uses posX/Y/Z instead of getPosition()
     */
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

    /**
     * Spawn crafting particles
     * 1.7.10: Uses posX/Y/Z instead of getPosition()
     */
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

    /**
     * Get crafting mode based on surroundings
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    private int getCraftMode() {
        if (!isInLiquidStarlight(this)) return -1;

        int x = (int) posX;
        int y = (int) posY;
        int z = (int) posZ;

        // Check if any entities nearby
        AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        List<Entity> foundEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);

        if (foundEntities.size() <= 0) {
            return MODE_GROW;
        }

        // Check if glowstone dust nearby for gem cluster formation
        // 1.7.10: getEntitiesWithinAABBExcludingEntity with 2 params, then filter manually
        List<Entity> allEntities = worldObj
            .getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
        foundEntities = new java.util.ArrayList<Entity>();
        for (Entity entity : allEntities) {
            if (entity instanceof net.minecraft.entity.item.EntityItem) {
                net.minecraft.entity.item.EntityItem item = (net.minecraft.entity.item.EntityItem) entity;
                ItemStack stack = item.getEntityItem();
                if (ItemUtils.hasOreName(stack, "dustGlowstone")) {
                    foundEntities.add(entity);
                }
            }
        }
        return foundEntities.size() == 1 ? MODE_GEM : -1;
    }

}
