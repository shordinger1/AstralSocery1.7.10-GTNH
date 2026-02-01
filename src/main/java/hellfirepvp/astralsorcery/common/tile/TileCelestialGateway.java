/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileEntity for Celestial Gateway Block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseTileEntity;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileEntity for Celestial Gateway
 * <p>
 * Handles:
 * - Multiblock structure detection
 * - Sky visibility checking
 * - Gateway registration in GatewayCache
 * - Display name for the gateway
 * - Player placement tracking
 * <p>
 * Ported from 1.12.2
 * <p>
 * TODO:
 * - Complete multiblock structure matching
 * - Implement GatewayCache integration
 * - Implement client-side effects
 */
public class TileCelestialGateway extends AstralBaseTileEntity {

    // 1.7.10: Track ticks manually (TileEntity doesn't expose it)
    private int ticksExisted = 0;

    // Structure matching state
    private boolean hasMultiblock = false;
    private boolean doesSeeSky = false;
    private boolean gatewayRegistered = false;

    // Display name (set by player)
    private String display = null;

    // UUID of player who placed this gateway
    private UUID placedBy = null;

    // Client-side effect objects
    @SideOnly(Side.CLIENT)
    private Object clientSphere = null;

    @Override
    public void updateEntity() {
        // 1.7.10: Increment ticks manually
        ticksExisted++;

        // Server-side logic
        if (isServerSide()) {
            // Periodically check sky visibility (every 16 ticks)
            if (ticksExisted % 16 == 0) {
                updateSkyState(canBlockSeeSky());
            }

            // Update multiblock state
            updateMultiblockState();

            // Register/unregister in GatewayCache
            updateGatewayRegistration();
        } else {
            // Client-side: play effects
            playEffects();
        }
    }

    /**
     * Check if the block can see the sky
     */
    private boolean canBlockSeeSky() {
        if (worldObj == null) {
            return false;
        }
        return worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord);
    }

    /**
     * Update sky visibility state
     */
    private void updateSkyState(boolean seeSky) {
        if (doesSeeSky != seeSky) {
            doesSeeSky = seeSky;
            markDirty();
            markForUpdate();
        }
    }

    /**
     * Update multiblock structure state
     * TODO: Implement actual structure matching
     */
    private void updateMultiblockState() {
        // TODO: Implement structure matching
        // For now, always false until structure system is implemented
        boolean matches = false; // structureMatch.matches(worldObj);

        if (matches != hasMultiblock) {
            hasMultiblock = matches;
            LogHelper.debug(
                "Gateway at [" + xCoord + ", " + yCoord + ", " + zCoord + "] multiblock state: " + hasMultiblock);
            markDirty();
            markForUpdate();
        }
    }

    /**
     * Update gateway registration in GatewayCache
     * TODO: Implement GatewayCache
     */
    private void updateGatewayRegistration() {
        if (gatewayRegistered) {
            // Should be active, check if still valid
            if (!hasMultiblock || !doesSeeSky) {
                // Remove from cache
                // GatewayCache cache = WorldCacheManager.getOrLoadData(worldObj,
                // WorldCacheManager.SaveKey.GATEWAY_DATA);
                // cache.removePosition(worldObj, xCoord, yCoord, zCoord);
                gatewayRegistered = false;
                LogHelper.debug("Gateway at [" + xCoord + ", " + yCoord + ", " + zCoord + "] unregistered");
            }
        } else {
            // Should be inactive, check if can activate
            if (hasMultiblock && doesSeeSky) {
                // Add to cache
                // GatewayCache cache = WorldCacheManager.getOrLoadData(worldObj,
                // WorldCacheManager.SaveKey.GATEWAY_DATA);
                // cache.offerPosition(worldObj, xCoord, yCoord, zCoord, display == null ? "" : display);
                gatewayRegistered = true;
                LogHelper.debug(
                    "Gateway at [" + xCoord
                        + ", "
                        + yCoord
                        + ", "
                        + zCoord
                        + "] registered: "
                        + (display == null ? "unnamed" : display));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        boolean active = hasMultiblock && doesSeeSky;
        setupGatewayUI(active);
        if (active) {
            playFrameParticles();
        }
    }

    @SideOnly(Side.CLIENT)
    private void setupGatewayUI(boolean active) {
        // TODO: Implement client-side effects
        // This includes the sphere effect and gateway UI
    }

    @SideOnly(Side.CLIENT)
    private void playFrameParticles() {
        // TODO: Implement frame particles
        // Spawn flare particles around the gateway frame
    }

    /**
     * Set the display name for this gateway
     */
    public void setGatewayName(String displayName) {
        this.display = displayName;
        markDirty();
    }

    /**
     * Set the UUID of the player who placed this gateway
     */
    public void setPlacedBy(UUID placedBy) {
        this.placedBy = placedBy;
        markDirty();
    }

    /**
     * Get the display name for this gateway
     */
    public String getDisplayName() {
        return hasCustomName() ? display : "";
    }

    /**
     * Check if this gateway has a custom name
     */
    public boolean hasCustomName() {
        return display != null && !display.isEmpty();
    }

    /**
     * Get the UUID of the player who placed this gateway
     */
    public UUID getPlacedBy() {
        return placedBy;
    }

    /**
     * Check if the multiblock structure is complete
     */
    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    /**
     * Check if the gateway can see the sky
     */
    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    /**
     * Check if the gateway is registered and active
     */
    public boolean isRegistered() {
        return gatewayRegistered;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        hasMultiblock = compound.getBoolean("mbState");
        doesSeeSky = compound.getBoolean("skyState");
        display = compound.getString("display");

        // Read UUID
        if (compound.hasKey("placer", 8)) { // 8 = UUID tag type
            long most = compound.getLong("placerMost");
            long least = compound.getLong("placerLeast");
            placedBy = new UUID(most, least);
        } else {
            placedBy = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("mbState", hasMultiblock);
        compound.setBoolean("skyState", doesSeeSky);
        compound.setString("display", display == null ? "" : display);

        // Write UUID
        if (placedBy != null) {
            compound.setLong("placerMost", placedBy.getMostSignificantBits());
            compound.setLong("placerLeast", placedBy.getLeastSignificantBits());
        }
    }

    @Override
    protected void readCustomNBT(NBTTagCompound compound) {
        // Custom NBT reading is handled in readFromNBT
    }

    @Override
    protected void writeCustomNBT(NBTTagCompound compound) {
        // Custom NBT writing is handled in writeToNBT
    }
}
