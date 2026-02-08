/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileBore - Bore machine tile entity
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileBore - Automated drilling machine TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Automatically mines blocks below</li>
 * <li>Requires bore head block below to function</li>
 * <li>Stores mined items in internal inventory</li>
 * <li>Configurable mining speed and range</li>
 * </ul>
 * <p>
 * <b>Configuration:</b>
 * <ul>
 * <li>Mining speed: 1 block every 100 ticks (5 seconds)</li>
 * <li>Mining range: Up to 64 blocks below</li>
 * <li>Inventory: 27 slots (like a chest)</li>
 * </ul>
 */
public class TileBore extends TileEntityTick {

    // Mining configuration
    private static final int MINING_INTERVAL_TICKS = 100; // 5 seconds per block
    private static final int MAX_MINING_RANGE = 64; // Maximum blocks to mine below
    private static final int INVENTORY_SIZE = 27; // Same as a chest

    // Mining state
    private int miningProgress = 0;
    private int currentMiningY = -1; // Current Y level being mined
    private boolean isActive = false;

    // Item inventory for mined items
    private ItemStack[] inventory = new ItemStack[INVENTORY_SIZE];

    public TileBore() {
        super();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) {
            return; // Don't process on client
        }

        // Check if bore has a bore head below
        if (!hasBoreHeadBelow()) {
            isActive = false;
            return;
        }

        isActive = true;

        // Progress mining
        miningProgress++;

        if (miningProgress >= MINING_INTERVAL_TICKS) {
            miningProgress = 0;
            mineNextBlock();
        }
    }

    /**
     * Check if there's a bore head block below
     */
    private boolean hasBoreHeadBelow() {
        if (yCoord <= 0) {
            return false; // At bedrock
        }

        Block blockBelow = worldObj.getBlock(xCoord, yCoord - 1, zCoord);
        // Check if it's a bore head block (placeholder - replace with actual check)
        // For now, any solid block below will work
        return blockBelow != null && worldObj.getBlockMetadata(xCoord, yCoord - 1, zCoord) == 0;
    }

    /**
     * Mine the next block below
     */
    private void mineNextBlock() {
        // Find next block to mine
        int targetY = findNextBlockToMine();
        if (targetY == -1) {
            return; // Nothing to mine
        }

        Block block = worldObj.getBlock(xCoord, targetY, zCoord);
        int meta = worldObj.getBlockMetadata(xCoord, targetY, zCoord);

        // Check if block is breakable
        if (block == null || block.isAir(worldObj, xCoord, targetY, zCoord)) {
            return;
        }

        // Don't break unbreakable blocks
        float hardness = block.getBlockHardness(worldObj, xCoord, targetY, zCoord);
        if (hardness < 0 || hardness > 50F) {
            return; // Unbreakable or too hard
        }

        // Get drops
        List<ItemStack> drops = getBlockDrops(block, meta, targetY);

        // Remove block
        worldObj.setBlockToAir(xCoord, targetY, zCoord);

        // Add drops to inventory
        for (ItemStack drop : drops) {
            if (drop != null) {
                ItemStack remaining = addItemToInventory(drop);
                if (remaining != null && remaining.stackSize > 0) {
                    // Inventory full - spawn item in world
                    spawnItemInWorld(remaining);
                }
            }
        }

        markForUpdate();
        LogHelper.debug("Bore at " + xCoord + "," + yCoord + "," + zCoord + " mined block at Y=" + targetY);
    }

    /**
     * Find the next block to mine
     */
    private int findNextBlockToMine() {
        // If we have a current target, continue from there
        int startY = (currentMiningY != -1) ? currentMiningY - 1 : yCoord - 1;

        // Search for minable block
        for (int y = startY; y >= 0 && y >= yCoord - MAX_MINING_RANGE; y--) {
            Block block = worldObj.getBlock(xCoord, y, zCoord);
            if (block != null && !block.isAir(worldObj, xCoord, y, zCoord)) {
                float hardness = block.getBlockHardness(worldObj, xCoord, y, zCoord);
                if (hardness >= 0 && hardness <= 50F) {
                    currentMiningY = y;
                    return y;
                }
            }
        }

        // No blocks found to mine
        currentMiningY = -1;
        return -1;
    }

    /**
     * Get block drops
     */
    private List<ItemStack> getBlockDrops(Block block, int meta, int y) {
        List<ItemStack> drops = new ArrayList<>();

        // Try to get the block's item form
        ItemStack itemStack = new ItemStack(block, 1, meta);

        // Add to drops if valid
        if (itemStack != null && itemStack.getItem() != null) {
            drops.add(itemStack);
        }

        return drops;
    }

    /**
     * Add item to inventory
     *
     * @return Remaining items that couldn't fit
     */
    private ItemStack addItemToInventory(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return null;
        }

        // Try to merge with existing stacks
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].isItemEqual(stack)
                && ItemStack.areItemStackTagsEqual(inventory[i], stack)) {
                int space = inventory[i].getMaxStackSize() - inventory[i].stackSize;
                if (space > 0) {
                    int toAdd = Math.min(space, stack.stackSize);
                    inventory[i].stackSize += toAdd;
                    stack.stackSize -= toAdd;
                    if (stack.stackSize <= 0) {
                        return null;
                    }
                }
            }
        }

        // Try to add to empty slot
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = stack.copy();
                return null;
            }
        }

        // Inventory full
        return stack;
    }

    /**
     * Spawn item in world
     */
    private void spawnItemInWorld(ItemStack stack) {
        if (worldObj.isRemote || stack == null) {
            return;
        }

        float offsetX = worldObj.rand.nextFloat() * 0.8F + 0.1F;
        float offsetY = worldObj.rand.nextFloat() * 0.8F + 0.1F;
        float offsetZ = worldObj.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(
            worldObj,
            xCoord + offsetX,
            yCoord + 1 + offsetY,
            zCoord + offsetZ,
            stack.copy());

        entityItem.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem);
    }

    /**
     * Check if bore is currently active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Get mining progress (0-100)
     */
    public int getMiningProgress() {
        return (miningProgress * 100) / MINING_INTERVAL_TICKS;
    }

    /**
     * Get inventory (for GUI or automation)
     */
    public ItemStack[] getInventory() {
        return inventory;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("miningProgress", miningProgress);
        compound.setInteger("currentMiningY", currentMiningY);
        compound.setBoolean("isActive", isActive);

        // Save inventory
        NBTTagCompound inventoryTag = new NBTTagCompound();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                inventory[i].writeToNBT(itemTag);
                inventoryTag.setTag("slot" + i, itemTag);
            }
        }
        compound.setTag("inventory", inventoryTag);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        miningProgress = compound.getInteger("miningProgress");
        currentMiningY = compound.getInteger("currentMiningY");
        isActive = compound.getBoolean("isActive");

        // Load inventory
        if (compound.hasKey("inventory")) {
            NBTTagCompound inventoryTag = compound.getCompoundTag("inventory");
            inventory = new ItemStack[INVENTORY_SIZE];
            for (int i = 0; i < inventory.length; i++) {
                String key = "slot" + i;
                if (inventoryTag.hasKey(key)) {
                    inventory[i] = ItemStack.loadItemStackFromNBT(inventoryTag.getCompoundTag(key));
                }
            }
        }
    }

    @Override
    protected void onFirstTick() {
        if (!worldObj.isRemote) {
            LogHelper.debug("Bore initialized at " + xCoord + "," + yCoord + "," + zCoord);
        }
    }
}
