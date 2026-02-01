/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Explosion-resistant item entity
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * EntityItemExplosionResistant - Explosion-resistant item entity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Immune to explosion damage</li>
 * <li>Extends EntityItemHighlighted for visual effects</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Same API for attackEntityFrom()</li>
 * <li>DamageSource.isExplosion() - same method</li>
 * </ul>
 */
public class EntityItemExplosionResistant extends EntityItemHighlighted {

    public EntityItemExplosionResistant(World worldIn) {
        super(worldIn);
    }

    public EntityItemExplosionResistant(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemExplosionResistant(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    /**
     * Override damage to ignore explosions
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return !source.isExplosion() && super.attackEntityFrom(source, amount);
    }

}
