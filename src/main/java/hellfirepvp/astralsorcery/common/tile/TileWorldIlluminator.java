/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileWorldIlluminator - World illuminator that places light sources
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.LinkedList;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * TileWorldIlluminator - World illuminator TileEntity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Automatically places light sources in dark areas</li>
 * <li>Searches in radius around the illuminator</li>
 * <li>Can be boosted with wand</li>
 * <li>Custom color support</li>
 * </ul>
 * <p>
 * <b>1.7.10 Simplifications:</b>
 * <ul>
 * <li>Removed: DirectionalLayerBlockDiscoverer (complex 1.12.2 system)</li>
 * <li>Removed: BlockVolatileLight (using BlockFlareLight instead)</li>
 * <li>Simplified position discovery algorithm</li>
 * </ul>
 */
public class TileWorldIlluminator extends TileEntityTick {

    private static final Random rand = new Random();
    public static final int SEARCH_RADIUS = 32; // Reduced from 64 for performance
    public static final int MAX_POSITIONS_PER_LAYER = 100;

    /** Valid positions to place lights [yLayer][positions] */
    private LinkedList<BlockPos>[] validPositions = null;
    private boolean recalcRequested = false;
    private int ticksUntilNext = 180;
    private boolean playerPlaced = false;

    private int boost = 0;
    private int chosenColor = 4; // 4 = YELLOW (1.7.10 dye metadata)

    public TileWorldIlluminator() {
        super();
        // Initialize valid positions array (will be sized on first recalculation)
        this.validPositions = null;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!playerPlaced) return;

        if (!worldObj.isRemote) {
            if (validPositions == null) {
                recalculate();
            }

            // Try to place flares periodically
            if (rand.nextInt(3) == 0 && placeFlares()) {
                recalcRequested = true;
            }

            // Tick counters
            boost--;
            ticksUntilNext--;

            if (ticksUntilNext <= 0) {
                ticksUntilNext = boost > 0 ? 30 : 180;
                if (recalcRequested) {
                    recalcRequested = false;
                    recalculate();
                }
            }
        }
    }

    /**
     * Place flare lights at valid positions
     * 1.7.10: Simplified from original version
     * Removed: BlockVolatileLight (using BlockFlareLight instead)
     */
    private boolean placeFlares() {
        if (validPositions == null) {
            return false;
        }

        boolean needsRecalc = false;

        for (int layer = 0; layer < validPositions.length; layer++) {
            LinkedList<BlockPos> list = validPositions[layer];

            if (list == null || list.isEmpty()) {
                needsRecalc = true;
                continue;
            }

            // Pick random position
            int index = rand.nextInt(list.size());
            BlockPos at = list.remove(index);

            if (list.isEmpty()) {
                needsRecalc = true;
            }

            // Add some randomness to position
            int offsetX = rand.nextInt(5) - 2;
            int offsetY = rand.nextInt(13) - 6;
            int offsetZ = rand.nextInt(5) - 2;

            at = new BlockPos(at.getX() + offsetX, at.getY() + offsetY, at.getZ() + offsetZ);

            // Check if position is valid
            if (isValidLightPosition(at)) {
                // Place BlockFlareLight with chosen color
                // 1.7.10: chosenColor is already an int (0-15 dye metadata)
                // No need for getDyeDamage() - that's 1.12.2 API
                worldObj.setBlock(at.getX(), at.getY(), at.getZ(), BlocksAS.blockFlareLight, chosenColor, 3);

                // Occasionally spawn ambient flare entity
                if (rand.nextInt(4) == 0) {
                    // TODO: Re-enable when EntityFlare is fully implemented
                    // EntityFlare.spawnAmbient(worldObj, ...)
                }
            }
        }

        return needsRecalc;
    }

    /**
     * Check if a position is valid for placing a light
     * 1.7.10: Simplified light check
     */
    private boolean isValidLightPosition(BlockPos pos) {
        // Check bounds
        if (pos.getY() < 0 || pos.getY() >= 256) {
            return false;
        }

        // Check if chunk is loaded
        if (!worldObj.blockExists(pos.getX(), pos.getY(), pos.getZ())) {
            return false;
        }

        // Check if position is air
        if (!worldObj.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
            return false;
        }

        // Check if it's dark enough
        // 1.7.10: getBlockLightValue() instead of getLight()
        int lightValue = worldObj.getBlockLightValue(pos.getX(), pos.getY(), pos.getZ());

        // 1.7.10: CanCheck - simplified sky light check
        // Original: canSeeSky() == false && getLight() < 8 && getSkyLight() < 6
        // 1.7.10: Just check if it's dark (< 8)
        if (lightValue >= 8) {
            return false;
        }

        return true;
    }

    /**
     * Recalculate valid positions for placing lights
     * 1.7.10: Simplified version without DirectionalLayerBlockDiscoverer
     */
    private void recalculate() {
        int parts = Math.max(0, yCoord - 7);
        validPositions = new LinkedList[parts];

        for (int i = 1; i <= parts; i++) {
            float yPart = ((float) i) / ((float) parts);
            int yLevel = Math.round(yPart * (yCoord - 7));

            LinkedList<BlockPos> calcPositions = discoverApplicableBlocks(yLevel);

            // Repeat positions 4x for variety (simplified from original)
            LinkedList<BlockPos> repeated = new LinkedList<>();
            for (int j = 0; j < 4; j++) {
                repeated.addAll(calcPositions);
            }

            // Limit to max positions per layer for performance
            if (repeated.size() > MAX_POSITIONS_PER_LAYER) {
                // Shuffle and take first N
                java.util.Collections.shuffle(repeated);
                while (repeated.size() > MAX_POSITIONS_PER_LAYER) {
                    repeated.removeLast();
                }
            }

            validPositions[i - 1] = repeated;
        }

        LogHelper.debug("[TileWorldIlluminator] Recalculated positions: " + parts + " layers");
    }

    /**
     * Discover applicable blocks for placing lights
     * 1.7.10: Simplified position discovery
     */
    private LinkedList<BlockPos> discoverApplicableBlocks(int yLevel) {
        LinkedList<BlockPos> positions = new LinkedList<>();

        // Search in a spiral pattern around the illuminator
        int x = yCoord;
        int z = zCoord;
        int radius = SEARCH_RADIUS;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos pos = new BlockPos(x + dx, yLevel, z + dz);

                if (isValidLightPosition(pos)) {
                    positions.add(pos);
                }
            }
        }

        return positions;
    }

    /**
     * Set player placed flag - enables the illuminator
     */
    public void setPlayerPlaced() {
        this.playerPlaced = true;
        this.markForUpdate();
    }

    /**
     * Boost illuminator with wand
     * 
     * @param color Color to set
     */
    public void onWandUsed(int color) {
        // REMOVED: EnumDyeColor parameter - using int metadata instead (0-15)
        this.boost = 10 * 60 * 20; // 10 minutes at 20 ticks/sec
        this.chosenColor = color;
        this.markForUpdate();
    }

    // ========== NBT ==========

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("playerPlaced", this.playerPlaced);
        compound.setInteger("boost", this.boost);
        // 1.7.10: chosenColor is an int, not EnumDyeColor
        // No null check needed, and no getDyeDamage() call
        compound.setInteger("wandColor", chosenColor);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerPlaced = compound.getBoolean("playerPlaced");
        this.boost = compound.getInteger("boost");

        if (compound.hasKey("wandColor")) {
            int colorMeta = compound.getInteger("wandColor");
            this.chosenColor = colorMeta;
        }

        if (this.chosenColor < 0 || this.chosenColor > 15) {
            this.chosenColor = 4; // Default to YELLOW
        }
    }

    @Override
    protected void onFirstTick() {
        recalculate();
    }
}
