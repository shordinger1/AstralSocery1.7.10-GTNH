/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileOwned - Base class for owned TileEntities
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;

/**
 * TileOwned - Owned TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Stores owner UUID for permission/access control</li>
 * <li>Used by altars, storage, and other owned blocks</li>
 * <li>Can check if owned by specific player</li>
 * <li>Can check if owned by "world" (public access)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>NBTTagCompound.getUniqueId() → Manual UUID read/write</li>
 * <li>NBTTagCompound.setUniqueId() → Manual UUID read/write</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // Check if player owns this tile
 * if (tile.getOwnerUUID()
 *     .equals(player.getUniqueID())) {
 *     // Player is owner
 * }
 *
 * // Set owner
 * tile.setOwner(player.getUniqueID());
 * </pre>
 */
public class TileOwned extends TileEntitySynchronized {

    /**
     * Special UUID indicating "world ownership" (public access)
     */
    public static UUID UUID_OWNER_WORLD = UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d2");

    protected UUID ownerUUID;

    /**
     * Get the owner UUID
     *
     * @return Owner UUID, or null if not owned
     */
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    /**
     * Set the owner UUID
     *
     * @param uuid Owner UUID
     */
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    /**
     * Check if owned by world (public access)
     *
     * @return true if owned by world
     */
    public boolean isWorldOwned() {
        return UUID_OWNER_WORLD.equals(ownerUUID);
    }

    /**
     * Check if owned by specific player
     *
     * @param playerUUID Player UUID to check
     * @return true if owned by this player
     */
    public boolean isOwnedBy(UUID playerUUID) {
        return ownerUUID != null && ownerUUID.equals(playerUUID);
    }

    /**
     * Read custom NBT
     * 1.7.10: Read UUID manually from NBT
     */
    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("owner")) {
            // 1.7.10: Read UUID from two longs (most and least significant bits)
            long most = compound.getLong("ownerMost");
            long least = compound.getLong("ownerLeast");
            this.ownerUUID = new UUID(most, least);
        }
    }

    /**
     * Write custom NBT
     * 1.7.10: Write UUID manually to NBT
     */
    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (ownerUUID != null) {
            // 1.7.10: Write UUID as two longs (most and least significant bits)
            compound.setLong("ownerMost", ownerUUID.getMostSignificantBits());
            compound.setLong("ownerLeast", ownerUUID.getLeastSignificantBits());
        }
    }

}
