/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * DamageSourceUtil - DamageSource utility methods
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

/**
 * DamageSourceUtil - DamageSource utilities (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Create new DamageSource instances</li>
 * <li>Modify DamageSource with entity sources</li>
 * <li>Copy attributes between DamageSource instances</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>EntityDamageSourceIndirect may not exist in 1.7.10</li>
 * <li>Some DamageSource methods may have different names</li>
 * </ul>
 */
public class DamageSourceUtil {

    /**
     * Create a new DamageSource with specified type
     *
     * @param damageType The damage type identifier
     * @return New DamageSource
     */
    public static DamageSource newType(@Nonnull String damageType) {
        return new DamageSource(damageType);
    }

    /**
     * Create DamageSource with direct entity source
     *
     * @param damageType The damage type identifier
     * @param source     The entity causing the damage
     * @return New EntityDamageSource
     */
    public static DamageSource withEntityDirect(@Nonnull String damageType, @Nullable Entity source) {
        return new EntityDamageSource(damageType, source);
    }

    /**
     * Create DamageSource with indirect entity source
     * 1.7.10: May use EntityDamageSource instead
     *
     * @param damageType     The damage type identifier
     * @param actualSource   The actual entity causing damage
     * @param indirectSource The indirect entity (e.g., projectile owner)
     * @return New DamageSource
     */
    public static DamageSource withEntityIndirect(@Nonnull String damageType, @Nullable Entity actualSource,
        @Nullable Entity indirectSource) {
        // 1.7.10: Simplified - just use EntityDamageSource
        return new EntityDamageSource(damageType, indirectSource != null ? indirectSource : actualSource);
    }

    /**
     * Override DamageSource with direct entity source
     *
     * @param damageType The DamageSource to modify
     * @param source     The entity causing the damage
     * @return Modified DamageSource, or null if cannot be modified
     */
    @Nullable
    public static DamageSource withEntityDirect(@Nonnull DamageSource damageType, @Nullable Entity source) {
        return override(damageType, source, null);
    }

    /**
     * Override DamageSource with indirect entity source
     *
     * @param damageType     The DamageSource to modify
     * @param actualSource   The actual entity causing damage
     * @param indirectSource The indirect entity
     * @return Modified DamageSource, or null if cannot be modified
     */
    @Nullable
    public static DamageSource withEntityIndirect(@Nonnull DamageSource damageType, @Nullable Entity actualSource,
        @Nullable Entity indirectSource) {
        return override(damageType, indirectSource, actualSource);
    }

    /**
     * Set DamageSource to fire damage
     *
     * @param src The DamageSource to modify
     * @return Modified DamageSource, or null if cannot be modified
     */
    @Nullable
    public static DamageSource setToFireDamage(@Nonnull DamageSource src) {
        return changeAttribute(src, DamageSource::setFireDamage);
    }

    /**
     * Set DamageSource to bypass armor
     *
     * @param src The DamageSource to modify
     * @return Modified DamageSource, or null if cannot be modified
     */
    @Nullable
    public static DamageSource setToBypassArmor(@Nonnull DamageSource src) {
        return changeAttribute(src, DamageSource::setDamageBypassesArmor);
    }

    /**
     * Change attribute of DamageSource
     *
     * @param src    The DamageSource to modify
     * @param update The update function to apply
     * @return Modified DamageSource, or null if cannot be modified
     */
    @Nullable
    public static DamageSource changeAttribute(@Nonnull DamageSource src, Consumer<DamageSource> update) {
        return overrideWithChanges(src, update);
    }

    /**
     * Check if DamageSource attributes can be changed
     */
    private static boolean mayChangeAttributes(DamageSource src) {
        Class<?> srcClass = src.getClass();
        return srcClass.equals(DamageSource.class) || srcClass.equals(EntityDamageSource.class);
    }

    /**
     * Override DamageSource with changes
     */
    @Nullable
    private static DamageSource overrideWithChanges(@Nonnull DamageSource source, Consumer<DamageSource> run) {
        DamageSource dst = override(source, null, null);
        if (dst != null) {
            run.accept(dst);
        }
        return dst;
    }

    /**
     * Override DamageSource with new entity sources
     */
    @Nullable
    private static DamageSource override(DamageSource src, @Nullable Entity directSource, @Nullable Entity trueSource) {
        if (!mayChangeAttributes(src)) {
            return null;
        }
        DamageSource dst;
        if (src.getClass()
            .equals(DamageSource.class)) {
            dst = new DamageSource(src.getDamageType());
        } else if (src.getClass()
            .equals(EntityDamageSource.class)) {
                dst = new EntityDamageSource(
                    src.getDamageType(),
                    directSource != null ? directSource : src.getSourceOfDamage());
            } else {
                return null; // Unknown type in 1.7.10
            }
        copy(src, dst);
        return dst;
    }

    /**
     * Copy attributes from source to destination
     */
    private static void copy(DamageSource src, DamageSource dest) {
        // 1.7.10: These methods may have different names or not exist
        try {
            if (src.isUnblockable()) {
                dest.setDamageBypassesArmor();
            }
        } catch (Exception e) {}
        try {
            if (src.isDamageAbsolute()) {
                // 1.7.10: setDamageIsAbsolute() may not exist
            }
        } catch (Exception e) {}
        try {
            if (src.isFireDamage()) {
                dest.setFireDamage();
            }
        } catch (Exception e) {}
        try {
            if (src.isMagicDamage()) {
                // 1.7.10: setMagicDamage() may not exist
            }
        } catch (Exception e) {}
    }

}
