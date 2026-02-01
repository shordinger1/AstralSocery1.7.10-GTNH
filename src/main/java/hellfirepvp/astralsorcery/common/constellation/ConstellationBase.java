/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationBase
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:07
 *
 * 1.7.10 Migration:
 * - Removed GameStages/CraftTweaker integration
 * - Removed Loader.instance().activeModContainer() (use direct modId)
 * - Simplified canDiscover() logic
 */
public abstract class ConstellationBase implements IConstellation {

    private List<StarLocation> starLocations = new ArrayList<StarLocation>(); // 31x31 locations are valid. 0-indexed.
    private List<StarConnection> connections = new ArrayList<StarConnection>(); // The connections between 2
                                                                                // tuples/stars in the constellation.
    private List<ItemHandle> signatureItems = new LinkedList<ItemHandle>();

    private final String name, simpleName;
    private final Color color;

    public ConstellationBase(String name) {
        this(name, IConstellation.major);
    }

    public ConstellationBase(String name, Color color) {
        this.simpleName = name;
        // 1.7.10: Directly set modId instead of using Loader
        this.name = "astralsorcery.constellation." + name;
        this.color = color;
    }

    public StarLocation addStar(int x, int y) {
        x %= (STAR_GRID_SIZE - 1); // 31x31
        y %= (STAR_GRID_SIZE - 1);
        StarLocation star = new StarLocation(x, y);
        if (!starLocations.contains(star)) {
            starLocations.add(star);
            return star;
        }
        return null;
    }

    public StarConnection addConnection(StarLocation star1, StarLocation star2) {
        if (star1.equals(star2)) return null;
        StarConnection sc = new StarConnection(star1, star2);
        if (!connections.contains(sc)) {
            connections.add(sc);
            return sc;
        }
        return null;
    }

    public ConstellationBase addSignatureItem(ItemHandle handle) {
        this.signatureItems.add(handle);
        return this;
    }

    @Override
    public List<ItemHandle> getConstellationSignatureItems() {
        return Collections.unmodifiableList(this.signatureItems);
    }

    @Override
    public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
        // 1.7.10: Simplified version without GameStages
        return true;
    }

    @Override
    public Color getConstellationColor() {
        return color;
    }

    @Override
    public List<StarLocation> getStars() {
        return Collections.unmodifiableList(starLocations);
    }

    @Override
    public List<StarConnection> getStarConnections() {
        return Collections.unmodifiableList(connections);
    }

    @Override
    public String getUnlocalizedName() {
        return name;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String toString() {
        return "Constellation={name:" + getUnlocalizedName() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstellationBase that = (ConstellationBase) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Major constellation - can be discovered at Attunement tier
     */
    public static class Major extends Weak implements IMajorConstellation {

        public Major(String name) {
            super(name);
        }

        public Major(String name, Color color) {
            super(name, color);
        }

        @Override
        public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
            // Major constellations require attunement
            return super.canDiscover(player, progress) && progress.wasOnceAttuned();
        }
    }

    /**
     * Weak constellation - base class
     */
    public static class Weak extends ConstellationBase implements IWeakConstellation {

        public Weak(String name) {
            super(name);
        }

        public Weak(String name, Color color) {
            super(name, color);
        }

        @Override
        public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
            // Weak constellations require attunement
            return super.canDiscover(player, progress) && progress.getTierReached()
                .isThisLaterOrEqual(ProgressionTier.ATTUNEMENT) && progress.wasOnceAttuned();
        }
    }

    /**
     * Special weak constellation with custom showup logic
     */
    public static abstract class WeakSpecial extends Weak {

        public WeakSpecial(String name) {
            super(name);
        }

        public WeakSpecial(String name, Color color) {
            super(name, color);
        }
    }

    /**
     * Minor constellation - tied to moon phases
     */
    public static class Minor extends ConstellationBase implements IMinorConstellation {

        private final List<MoonPhase> phases;

        public Minor(String name, MoonPhase... applicablePhases) {
            super(name);
            phases = new ArrayList<MoonPhase>(applicablePhases.length);
            for (MoonPhase ph : applicablePhases) {
                if (ph == null) {
                    throw new IllegalArgumentException(
                        "null MoonPhase passed to Minor constellation registration for " + name);
                }
                phases.add(ph);
            }
        }

        public Minor(String name, Color color, MoonPhase... applicablePhases) {
            super(name, color);
            phases = new ArrayList<MoonPhase>(applicablePhases.length);
            for (MoonPhase ph : applicablePhases) {
                if (ph == null) {
                    throw new IllegalArgumentException(
                        "null MoonPhase passed to Minor constellation registration for " + name);
                }
                phases.add(ph);
            }
        }

        @Override
        public List<MoonPhase> getShowupMoonPhases(long rSeed) {
            List<MoonPhase> shifted = new ArrayList<MoonPhase>(phases.size());
            for (MoonPhase mp : this.phases) {
                int index = mp.ordinal() + (((int) (rSeed % MoonPhase.values().length)) + MoonPhase.values().length);
                while (index >= MoonPhase.values().length) {
                    index -= MoonPhase.values().length;
                }
                index = Math.max(0, Math.min(index, MoonPhase.values().length - 1));
                MoonPhase offset = MoonPhase.values()[index];
                if (!shifted.contains(offset)) {
                    shifted.add(offset);
                }
            }
            return shifted;
        }

        @Override
        public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
            // Minor constellations require trait crafting tier
            return super.canDiscover(player, progress) && progress.wasOnceAttuned()
                && progress.getTierReached()
                    .isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT);
        }
    }

}
