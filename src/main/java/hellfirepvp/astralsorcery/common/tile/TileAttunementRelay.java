/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Attunement Relay - Collects and transmits starlight to altars
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * TileAttunementRelay - Attunement relay (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Collects starlight when placed with glass lens</li>
 * <li>Transmits starlight to linked altar</li>
 * <li>Part of attunement altar multiblock structure</li>
 * <li>Requires multiblock structure to function</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>TileInventoryBase → Manual inventory array</li>
 * <li>ItemStack.EMPTY → null checks</li>
 * <li>BlockPos → int xCoord, yCoord, zCoord</li>
 * <li>StructureMatcherPatternArray → Simplified placeholder</li>
 * <li>WorldCacheManager → Direct world checks</li>
 * <li>ConstellationSkyHandler → Simplified day/night check</li>
 * </ul>
 * <p>
 * <b>TODO:</b>
 * <ul>
 * <li>Implement StructureMatcherPatternArray for multiblock detection</li>
 * <li>Implement ConstellationSkyHandler for accurate starlight collection</li>
 * <li>Implement WorldCacheManager for optimized structure caching</li>
 * <li>Implement particle effects for client-side visualization</li>
 * </ul>
 */
public class TileAttunementRelay extends TileEntityTick {

    private static final float MAX_DST = (float) (Math.sqrt(Math.sqrt(2.0D) + 1) * 16.0D);

    // Inventory: 1 slot for glass lens
    private ItemStack[] inventory = new ItemStack[1];

    // Linked altar position
    private BlockPos linked = null;
    private float collectionMultiplier = 1F;

    // Multiblock and sky state
    private boolean canSeeSky = false;
    private boolean hasMultiblock = false;

    public TileAttunementRelay() {
        super();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        // Update sky visibility every 16 ticks
        if ((ticksExisted & 15) == 0) {
            updateSkyState();
        }

        ItemStack slotted = inventory[0];
        if (!worldObj.isRemote) {
            updateMultiblockState();

            if (slotted != null && slotted.stackSize > 0) {
                // Eject lens if blocked from above
                if (!worldObj.isAirBlock(xCoord, yCoord + 1, zCoord)) {
                    ItemStack in = inventory[0];
                    ItemStack out = ItemUtils.copyStackWithSize(in, in.stackSize);
                    ItemUtils.dropItemNaturally(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, out);
                    inventory[0] = null;
                }

                // Transmit starlight to linked altar
                if (hasGlassLens()) {
                    if (linked != null && worldObj.blockExists(linked.getX(), linked.getY(), linked.getZ())) {
                        TileAltar ta = MiscUtils
                            .getTileAt(worldObj, linked.getX(), linked.getY(), linked.getZ(), TileAltar.class, true);
                        if (ta == null) {
                            linked = null;
                            markForUpdate();
                        } else if (hasMultiblock && doesSeeSky()) {
                            // Check if night
                            long time = worldObj.getWorldTime() % 24000L;
                            boolean isNight = time >= 13000L && time <= 23000L;

                            if (isNight) {
                                int yLevel = yCoord;
                                if (yLevel > 40) {
                                    // Calculate starlight collection
                                    double coll = 2.0; // Base collection

                                    // Height bonus
                                    float dstr;
                                    if (yLevel > 120) {
                                        dstr = 1F;
                                    } else {
                                        dstr = (yLevel - 40) / 80F;
                                    }

                                    coll *= dstr;
                                    coll *= collectionMultiplier;

                                    // Transmit to altar
                                    int current = ta.getStarlightStored();
                                    int max = ta.getMaxStarlightStorage();
                                    int space = max - current;

                                    if (space > 0) {
                                        int toAdd = (int) Math.min(space, coll);
                                        ta.setStarlightStored(current + toAdd);

                                        // Sync altar
                                        ta.markForUpdate();

                                        // Occasionally log
                                        if (worldObj.getTotalWorldTime() % 200 == 0) {
                                            LogHelper.debug("Relay transmitted %d starlight to altar at [%d,%d,%d]",
                                                toAdd, linked.getX(), linked.getY(), linked.getZ());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TODO: Add client-side particle effects
            // if(!slotted.isEmpty() && hasMultiblock) {
            // if (hasGlassLens()) {
            // // Spawn particles...
            // }
            // }
        }
    }

    /**
     * Update multiblock structure state
     * TODO: Replace with StructureMatcherPatternArray when implemented
     */
    private void updateMultiblockState() {
        if (!hasGlassLens()) {
            hasMultiblock = false;
            return;
        }

        // TODO: Implement structure matching
        // For now, just check if marbles are placed in correct pattern
        boolean found = checkStructure();

        if (found != this.hasMultiblock) {
            LogHelper.debug(
                "Structure match updated: " + this.getClass()
                    .getName()
                    + " at ("
                    + xCoord
                    + ", "
                    + yCoord
                    + ", "
                    + zCoord
                    + ") ("
                    + this.hasMultiblock
                    + " -> "
                    + found
                    + ")");
            this.hasMultiblock = found;
            markForUpdate();
        }
    }

    /**
     * Simplified structure check
     * TODO: Replace with StructureMatcherPatternArray
     */
    private boolean checkStructure() {
        // TODO: Implement actual multiblock pattern matching
        // For now, return false (structure not complete)
        return false;
    }

    /**
     * Update sky visibility state
     */
    private void updateSkyState() {
        boolean seesSky = canBlockSeeTheSky(xCoord, yCoord, zCoord);
        if (canSeeSky != seesSky) {
            canSeeSky = seesSky;
            markForUpdate();
        }
    }

    /**
     * Check if this block can see the sky
     */
    private boolean canBlockSeeTheSky(int x, int y, int z) {
        // Check straight up to world height
        for (int checkY = y + 1; checkY < worldObj.getHeight(); checkY++) {
            if (!worldObj.isAirBlock(x, checkY, z)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if glass lens is in inventory
     */
    private boolean hasGlassLens() {
        ItemStack slotted = inventory[0];
        if (slotted == null || slotted.stackSize <= 0) {
            return false;
        }
        // Simple item comparison: check if it's a glass lens
        // ItemCraftingComponent.MetaType.GLASS_LENS has ordinal that matches metadata
        if (slotted.getItem() instanceof hellfirepvp.astralsorcery.common.item.ItemCraftingComponent) {
            // Check if metadata matches GLASS_LENS (assuming it's an enum ordinal)
            return slotted.getItemDamage()
                == hellfirepvp.astralsorcery.common.item.ItemCraftingComponent.MetaType.GLASS_LENS.ordinal();
        }
        return false;
    }

    /**
     * Update position data with linked altar
     */
    public void updatePositionData(BlockPos closestAltar, double dstSqOtherRelay) {
        this.linked = closestAltar;
        dstSqOtherRelay = Math.sqrt(dstSqOtherRelay);
        if (dstSqOtherRelay <= 1E-4) {
            collectionMultiplier = 1F;
        } else {
            collectionMultiplier = 1F - ((float) (Math.min(dstSqOtherRelay, MAX_DST) / MAX_DST));
        }
        markForUpdate();
    }

    /**
     * Check if relay can see sky
     */
    public boolean doesSeeSky() {
        return canSeeSky;
    }

    /**
     * Check if multiblock is complete
     */
    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    /**
     * Get linked altar position
     */
    public BlockPos getLinked() {
        return linked;
    }

    /**
     * Get inventory for external access
     */
    public ItemStack[] getInventory() {
        return inventory;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("seesSky", this.canSeeSky);
        compound.setBoolean("mbState", this.hasMultiblock);
        compound.setFloat("colMultiplier", this.collectionMultiplier);

        // Save inventory
        if (inventory[0] != null) {
            NBTTagCompound slotCompound = new NBTTagCompound();
            inventory[0].writeToNBT(slotCompound);
            compound.setTag("inventorySlot0", slotCompound);
        }

        // Save linked position
        if (this.linked != null) {
            NBTHelper.setBlockPos(compound, "linked", this.linked);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.canSeeSky = compound.getBoolean("seesSky");
        this.hasMultiblock = compound.getBoolean("mbState");
        this.collectionMultiplier = compound.getFloat("colMultiplier");

        // Load inventory
        if (compound.hasKey("inventorySlot0")) {
            inventory[0] = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("inventorySlot0"));
        } else {
            inventory[0] = null;
        }

        // Load linked position
        if (compound.hasKey("linked")) {
            this.linked = NBTHelper.readBlockPos(compound, "linked");
        } else {
            linked = null;
        }
    }

    @Override
    protected void onFirstTick() {
        // Initialize on first tick
    }
}
