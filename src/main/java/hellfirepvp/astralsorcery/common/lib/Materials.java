/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Custom material types for Astral Sorcery blocks
 * <p>
 * This class defines special material types used by the mod,
 * particularly for blocks that should not be pushed by pistons.
 *
 * @author HellFirePvP
 * @date 14.09.2016 / 12:11
 */
public class Materials {

    /**
     * A material that blocks movement but cannot be pushed by pistons
     * <p>
     * This is used for special blocks that should remain stationary
     * even when pistons attempt to move them.
     */
    public static final NoPushMaterial MATERIAL_NO_PUSH = new NoPushMaterial();

    /**
     * Custom material class that prevents piston movement
     * <p>
     * Blocks using this material cannot be pushed by pistons,
     * but still block entity movement (like a normal solid block).
     */
    public static class NoPushMaterial extends Material {

        private int mobilityFlag;

        /**
         * Creates a new NoPushMaterial with air color (invisible)
         */
        public NoPushMaterial() {
            super(MapColor.airColor);
            setNoPushMobility();
        }

        /**
         * Returns true to indicate this material blocks entity movement
         *
         * @return true - entities cannot move through this material
         */
        @Override
        public boolean blocksMovement() {
            return true;
        }

        /**
         * Sets the material mobility flag to prevent piston pushing
         *
         * @return this material instance for chaining
         */
        protected Material setNoPushMobility() {
            this.mobilityFlag = 1;
            return this;
        }

        /**
         * Gets the material mobility flag
         * <p>
         * A value of 1 indicates this material cannot be pushed.
         *
         * @return the mobility flag value
         */
        @Override
        public int getMaterialMobility() {
            return this.mobilityFlag;
        }

    }

}
