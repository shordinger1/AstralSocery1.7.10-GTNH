/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk tree - Manages constellation perk tree
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import java.util.*;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.root.RootPerk;

/**
 * Perk tree - Manages constellation perk tree (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Perk registration and management</li>
 * <li>Connection management between perks</li>
 * <li>Root perk tracking</li>
 * <li>Tree freezing to prevent further modification</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No ResourceLocation - use String registry names</li>
 * <li>No Forge event bus registration for perks</li>
 * <li>Simplified connection system</li>
 * </ul>
 */
public class PerkTree {

    public static final int PERK_TREE_VERSION = 1;
    public static final PerkTree PERK_TREE = new PerkTree();

    private static Map<String, AbstractPerk> perkMap = new HashMap<>();
    private boolean frozen = false;

    private List<PerkTreePoint<?>> treePoints = new LinkedList<>();
    private Map<AbstractPerk, Collection<AbstractPerk>> doubleConnections = new HashMap<>();
    private List<Connection> connections = new LinkedList<>();

    private Map<IConstellation, AbstractPerk> rootPerks = new HashMap<>();

    private PerkTree() {}

    /**
     * Register a root perk
     */
    public PointConnector registerRootPerk(RootPerk perk) {
        if (frozen) {
            throw new IllegalStateException("Cannot register perk: PerkTree-State already frozen!");
        }
        perkMap.put(perk.getRegistryName(), perk);
        rootPerks.put(perk.getConstellation(), perk);
        return PERK_TREE.setPoint(perk);
    }

    /**
     * Register a perk
     */
    public PointConnector registerPerk(AbstractPerk perk) {
        if (frozen) {
            throw new IllegalStateException("Cannot register perk: PerkTree-State already frozen!");
        }
        perkMap.put(perk.getRegistryName(), perk);
        return PERK_TREE.setPoint(perk);
    }

    /**
     * Get perk by registry name
     */
    public AbstractPerk getPerk(String key) {
        return perkMap.get(key);
    }

    /**
     * Get root perk for constellation
     */
    public AbstractPerk getRootPerk(IConstellation constellation) {
        return rootPerks.get(constellation);
    }

    /**
     * Set point in tree
     */
    private PointConnector setPoint(AbstractPerk perk) throws IllegalArgumentException {
        PerkTreePoint<?> offsetPoint = perk.getPoint();
        if (this.treePoints.contains(offsetPoint)) {
            throw new IllegalArgumentException(
                "Tried to register perk-point at already placed position: " + offsetPoint.getOffset()
                    .toString());
        }
        this.treePoints.add(offsetPoint);
        return new PointConnector(perk);
    }

    /**
     * Try to get connector for perk
     */
    public PointConnector tryGetConnector(AbstractPerk point) {
        if (point == null) return null;
        if (this.treePoints.contains(point.getPoint())) {
            return new PointConnector(point);
        }
        return null;
    }

    /**
     * Get connected perks
     */
    public Collection<AbstractPerk> getConnectedPerks(AbstractPerk perk) {
        return doubleConnections.getOrDefault(perk, new ArrayList<>());
    }

    /**
     * Get all perk points
     */
    public Collection<PerkTreePoint<?>> getPerkPoints() {
        return new ArrayList<>(this.treePoints);
    }

    /**
     * Get all connections (for rendering)
     */
    public Collection<Connection> getConnections() {
        return new ArrayList<>(this.connections);
    }

    /**
     * Clear caches
     */
    public void clearCache(cpw.mods.fml.relauncher.Side side) {
        for (PerkTreePoint<?> point : this.treePoints) {
            point.getPerk()
                .clearCaches(side);
        }
    }

    /**
     * Remove perk from tree
     */
    public void removePerk(AbstractPerk perk) {
        if (frozen) {
            throw new IllegalStateException("Cannot remove perk: PerkTree-State already frozen!");
        }
        if (perk instanceof RootPerk) {
            rootPerks.remove(((RootPerk) perk).getConstellation());
        }
        perkMap.remove(perk.getRegistryName());
        PerkTreePoint<?> point = perk.getPoint();
        this.treePoints.remove(point);
        new PointConnector(perk).disconnectAll();
    }

    /**
     * Freeze the tree to prevent further modifications
     */
    public void freeze() {
        this.frozen = true;
    }

    /**
     * Connection between two perks
     */
    public static class Connection {

        public final AbstractPerk from;
        public final AbstractPerk to;

        public Connection(AbstractPerk from, AbstractPerk to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Connection that = (Connection) o;
            return Objects.equals(from, that.from) && Objects.equals(to, that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    /**
     * Point connector - used to connect perks in the tree
     */
    public class PointConnector {

        private final AbstractPerk point;

        public PointConnector(AbstractPerk point) {
            this.point = point;
        }

        /**
         * Disconnect all connections
         */
        public boolean disconnectAll() {
            boolean removedAll = true;
            Collection<AbstractPerk> otherLinked = new LinkedList<>(doubleConnections.get(this.point));
            for (AbstractPerk other : otherLinked) {
                if (!disconnect(other)) {
                    removedAll = false;
                }
            }
            return removedAll;
        }

        /**
         * Disconnect from another perk
         */
        public boolean disconnect(AbstractPerk other) {
            if (other == null) {
                return false;
            }

            Collection<AbstractPerk> others = doubleConnections.get(this.point);
            if (others == null) {
                return false;
            }
            if (!others.remove(other)) {
                return false;
            }
            // Remove connection
            connections.removeIf(
                c -> (c.from.equals(other) && c.to.equals(point)) || (c.from.equals(point) && c.to.equals(other)));

            // Remove reverse connection
            Collection<AbstractPerk> reverse = doubleConnections.get(other);
            if (reverse != null) {
                reverse.remove(point);
            }
            return true;
        }

        /**
         * Connect to another perk
         */
        public PointConnector connect(AbstractPerk other) {
            if (other == null) {
                return this;
            }

            Collection<AbstractPerk> pointsTo = doubleConnections.computeIfAbsent(other, p -> new LinkedList<>());
            if (!pointsTo.contains(point)) {
                pointsTo.add(point);
            }
            pointsTo = doubleConnections.computeIfAbsent(point, p -> new LinkedList<>());
            if (!pointsTo.contains(other)) {
                pointsTo.add(other);
            }

            Connection connection = new Connection(point, other);
            Connection reverse = new Connection(other, point);
            if (!connections.contains(connection) && !connections.contains(reverse)) {
                connections.add(connection);
            }
            return this;
        }

        /**
         * Connect to another perk via connector
         */
        public PointConnector connect(PointConnector other) {
            return connect(other.point);
        }

    }

}
