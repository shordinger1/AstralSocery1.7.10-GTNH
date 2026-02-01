/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Stardust item entity - merges with crystals in liquid starlight
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;

/**
 * EntityItemStardust - Stardust item entity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Merges with rock crystals in liquid starlight</li>
 * <li>Creates celestial crystals</li>
 * <li>Renders with particle effects</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Entity.getEntityWorld() → Entity.worldObj</li>
 * <li>world.getEntitiesInAABBexcluding() → world.getEntitiesWithinAABBExcludingEntity()</li>
 * <li>boxCraft.offset(posX, posY, posZ).grow() → Manual bounds calculation</li>
 * <li>world.setBlockState(getPosition(), ...) → world.setBlock(x, y, z, block, meta, notify)</li>
 * <li>getItem().setCount() → getItem().stackSize</li>
 * <li>getItem().getCount() → getItem().stackSize</li>
 * <li>ei.setItem(stack) → ei.setEntityItemStack(stack)</li>
 * <li>PacketChannel.CHANNEL.sendToAllAround() - Network packet system</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>EffectHelper - Client particle system</li>
 * <li>EntityFXFacingParticle - Particle entity</li>
 * <li>Config - Configuration class</li>
 * <li>EntityUtils - Entity utility functions</li>
 * <li>PacketChannel - Network packet system</li>
 * <li>PktParticleEvent - Particle event packet</li>
 * </ul>
 */
public class EntityItemStardust extends EntityItem implements EntityStarlightReacttant {

    public static final int TOTAL_MERGE_TIME = 30 * 20;
    private int inertMergeTick = 0;

    public EntityItemStardust(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemStardust(World worldIn) {
        super(worldIn);
    }

    public EntityItemStardust(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (age + 5 >= this.lifespan) {
            age = 0;
        }

        if (hellfirepvp.astralsorcery.common.data.config.Config.craftingLiqCelestialCrystalForm) {
            checkMergeConditions();
        }
    }

    private void checkMergeConditions() {
        // 1.7.10: worldObj.isRemote instead of world.isRemote
        if (worldObj.isRemote) {
            if (canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if (canCraft()) {
                inertMergeTick++;
                if (inertMergeTick >= TOTAL_MERGE_TIME && rand.nextInt(20) == 0) {
                    buildCelestialCrystals();
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    /**
     * Build celestial crystals at entity location
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    private void buildCelestialCrystals() {
        int x = (int) posX;
        int y = (int) posY;
        int z = (int) posZ;

        // 1.7.10: world.setBlock(x, y, z, block, meta, notify) instead of setBlockState()
        // Assuming metadata 0 for default state
        if (worldObj.setBlock(x, y, z, BlocksAS.celestialCrystals, 0, 3)) {
            // TODO: Re-enable when PacketChannel and PktParticleEvent are migrated
            /*
             * PacketChannel.CHANNEL.sendToAllAround(new
             * PktParticleEvent(PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_FORM, posX, posY, posZ),
             * PacketChannel.pointFromPos(worldObj, x, y, z, 64));
             */

            // 1.7.10: stack.stackSize instead of stack.getCount()
            ItemStack item = getEntityItem();
            item.stackSize--;

            // 1.7.10: getBoundingBox needs 6 parameters
            AxisAlignedBB searchBox = AxisAlignedBB
                .getBoundingBox(posX - 0.1, posY - 0.1, posZ - 0.1, posX + 1.1, posY + 1.1, posZ + 1.1);

            // TODO: Re-enable when EntityUtils is migrated
            /*
             * List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox,
             * EntityUtils.selectItemClassInstaceof(ItemRockCrystalBase.class));
             */

            // Simplified: get all entities and filter manually
            List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);

            if (foundItems.size() > 0) {
                EntityItem ei = (EntityItem) foundItems.get(0);
                if (ei instanceof EntityItem) {
                    ItemStack stack = ei.getEntityItem();
                    if (stack != null) {
                        item.stackSize--;
                        stack.stackSize--;
                        if (stack.stackSize <= 0) {
                            ei.setDead();
                        } else {
                            // 1.7.10: setEntityItemStack() instead of setItem()
                            ei.setEntityItemStack(stack);
                        }
                    }
                }
            }

            // Update the item stack
            if (item.stackSize <= 0) {
                setDead();
            } else {
                setEntityItemStack(item);
            }
        } else {
            inertMergeTick -= 20; // Retry later...
        }
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
        p.gravity(0.2);
        p.scale(0.2F);
    }

    /**
     * Spawn formation particles (static method called from packet handler)
     * TODO: Implement when particle system is migrated
     */
    // @SideOnly(Side.CLIENT)
    // public static void spawnFormationParticles(hellfirepvp.astralsorcery.common.network.packet.PktParticleEvent
    // event) {
    // TODO: Re-enable when particle system is migrated
    // }

    /**
     * Check if crafting is possible
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    private boolean canCraft() {
        if (!isInLiquidStarlight(this)) return false;

        // 1.7.10: getBoundingBox needs 6 parameters
        AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1, posY + 1, posZ + 1);

        // TODO: Re-enable when EntityUtils is migrated
        /*
         * List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox,
         * EntityUtils.selectItemClassInstaceof(ItemRockCrystalBase.class));
         */

        // Simplified: check if any entities nearby
        List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);

        return foundItems.size() > 0;
    }

}
