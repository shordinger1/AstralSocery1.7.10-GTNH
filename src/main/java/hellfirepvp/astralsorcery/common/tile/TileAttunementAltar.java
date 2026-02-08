/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TileAttunementAltar - Attunement altar tile entity
 *
 * SKELETON VERSION - Complex effects and attunement logic commented with TODOs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TileAttunementAltar - Attunement altar (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Detects constellation patterns formed by attunement relays</li>
 * <li>Allows players to attune to Major constellations</li>
 * <li>Allows crystals to attune to Weak/Minor constellations</li>
 * <li>Requires night sky and multiblock structure</li>
 * </ul>
 * <p>
 * <b>1.7.10 Simplifications:</b>
 * <ul>
 * <li>No client-side camera flights (1.12.2 feature)</li>
 * <li>No orbital effects (requires complex EffectHandler)</li>
 * <li>No lightbeam effects (requires EffectHandler.lightbeam)</li>
 * <li>Simplified packet system</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>Client-side effects - camera flights, orbital effects</li>
 * <li>Packet system for attunement requests</li>
 * <li>Sound system integration</li>
 * <li>Structure matching system</li>
 * </ul>
 */
public class TileAttunementAltar extends TileEntity {

    private static final Random rand = new Random();

    /** Active constellation detected */
    private IConstellation activeConstellation = null;

    /** Sees sky flag */
    private boolean seesSky = false;

    /** Multiblock matches flag */
    private boolean hasMultiblock = false;

    /** Attunement mode: 0=idle, 1=player, 2=crystal */
    private int mode = 0;

    /** Active entity ID for player/crystal being attuned */
    private int activeEntityId = -1;

    /** Server sync tick */
    private int syncTick = 0;

    /** 1.7.10: Track ticks existed manually */
    private int ticksExisted = 0;

    public TileAttunementAltar() {}

    @Override
    public void updateEntity() {
        // 1.7.10: Increment ticks manually
        ticksExisted++;

        if (worldObj == null) return;

        if (!worldObj.isRemote) {
            // Server-side logic
            if (ticksExisted % 10 == 0) {
                if (activeConstellation == null) {
                    searchForConstellation();
                } else {
                    verifyConstellation();
                }
            }

            if ((ticksExisted & 15) == 0) {
                updateSkyState();
            }

            if (activeConstellation != null && hasMultiblock && isNight()) {
                checkForAttunements();
            }
        } else {
            // Client-side logic
            // TODO: Add client-side effects when EffectHandler is fully available
        }
    }

    /**
     * Check if it's currently night
     * 1.7.10: Simplified check using world time
     */
    private boolean isNight() {
        long time = worldObj.getWorldTime() % 24000L;
        return time >= 13000L && time <= 23000L; // Night time in 1.7.10
    }

    /**
     * Search for constellation pattern
     * TODO: Implement when structure matching system is ready
     * For now: simplified version just checks registered constellations
     */
    private void searchForConstellation() {
        // TODO: Implement structure matching for attunement relay positions
        // For now, just pick the first weak constellation as placeholder
        if (!ConstellationRegistry.getWeakConstellations()
            .isEmpty()) {
            IConstellation candidate = ConstellationRegistry.getWeakConstellations()
                .get(0);
            if (candidate != null && isNight()) {
                activeConstellation = candidate;
                LogHelper.debug(
                    "TileAttunementAltar at " + xCoord
                        + ","
                        + yCoord
                        + ","
                        + zCoord
                        + " found constellation: "
                        + candidate.getUnlocalizedName());
                markDirty();
            }
        }
    }

    /**
     * Verify active constellation is still present
     */
    private void verifyConstellation() {
        if (activeConstellation == null) return;

        // TODO: Verify constellation pattern still exists
        // For now, just check if it's night
        if (!isNight()) {
            activeConstellation = null;
            markDirty();
        }
    }

    /**
     * Check for entities that can attune
     */
    private void checkForAttunements() {
        if ((ticksExisted & 31) != 0) return; // Only check every 32 ticks

        if (activeConstellation instanceof hellfirepvp.astralsorcery.common.constellation.IMajorConstellation) {
            checkPlayerAttunement();
        }

        // TODO: Check for crystal attunement when item system is ready
        // checkCrystalAttunement();
    }

    /**
     * Check for player attunement
     * Updated to allow manual attunement by right-clicking
     */
    private void checkPlayerAttunement() {
        // Find nearby players
        java.util.List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(
            EntityPlayer.class,
            net.minecraft.util.AxisAlignedBB
                .getBoundingBox(xCoord - 2, yCoord - 1, zCoord - 2, xCoord + 3, yCoord + 3, zCoord + 3));

        for (EntityPlayer player : players) {
            PlayerProgress progress = ResearchManager.getProgress(player);
            if (progress == null) continue;

            // Auto-discover constellation for players who haven't discovered it yet
            if (!progress.hasConstellationDiscovered(activeConstellation)) {
                ResearchManager.discoverConstellation(activeConstellation, player);
                LogHelper.info(
                    "Player " + player.getCommandSenderName()
                        + " discovered constellation: "
                        + activeConstellation.getUnlocalizedName());
            }
        }
    }

    /**
     * Handle player right-click interaction
     * Allows player to attune to the active constellation
     */
    public void onRightClick(EntityPlayer player) {
        if (worldObj.isRemote) return;

        // Check requirements
        if (activeConstellation == null) {
            player.addChatMessage(new net.minecraft.util.ChatComponentText("§cNo constellation is currently active!"));
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§eWait for nighttime and ensure the altar can see the sky."));
            return;
        }

        if (!(activeConstellation instanceof hellfirepvp.astralsorcery.common.constellation.IMajorConstellation)) {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§cOnly Major constellations can be attuned!"));
            return;
        }

        if (!isNight()) {
            player.addChatMessage(new net.minecraft.util.ChatComponentText("§cYou can only attune at night!"));
            return;
        }

        if (!seesSky) {
            player.addChatMessage(new net.minecraft.util.ChatComponentText("§cThe altar cannot see the sky!"));
            return;
        }

        if (!hasMultiblock) {
            player.addChatMessage(new net.minecraft.util.ChatComponentText("§cThe altar structure is incomplete!"));
            return;
        }

        PlayerProgress progress = ResearchManager.getProgress(player);

        // Check if player has discovered this constellation
        if (!progress.hasConstellationDiscovered(activeConstellation)) {
            ResearchManager.discoverConstellation(activeConstellation, player);
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText(
                    "§aYou discovered the constellation: §6" + activeConstellation.getUnlocalizedName()));
        }

        // Attempt attunement
        hellfirepvp.astralsorcery.common.constellation.IMajorConstellation majorConstellation = (hellfirepvp.astralsorcery.common.constellation.IMajorConstellation) activeConstellation;

        boolean success = ResearchManager.setAttunedConstellation(player, majorConstellation);

        if (success) {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText(
                    "§aYou have attuned to: §6" + activeConstellation.getUnlocalizedName()));
            player.addChatMessage(new net.minecraft.util.ChatComponentText("§eYour perk tree has been reset."));
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§eYou can now use constellation-specific recipes!"));

            LogHelper.info(
                "Player " + player.getCommandSenderName()
                    + " attuned to constellation: "
                    + activeConstellation.getUnlocalizedName());
        } else {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText(
                    "§cFailed to attune! You may already be attuned to this constellation."));
        }
    }

    /**
     * Update sky visibility state
     */
    private void updateSkyState() {
        boolean canSee = worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord);
        if (canSee != seesSky) {
            seesSky = canSee;
            markDirty();
        }
    }

    /**
     * Get active constellation
     */
    public IConstellation getActiveConstellation() {
        return activeConstellation;
    }

    /**
     * Get current attunement mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * Set attunement mode
     */
    public void setMode(int mode) {
        this.mode = mode;
        markDirty();
    }

    /**
     * Check if sees sky
     */
    public boolean seesSky() {
        return seesSky;
    }

    /**
     * Check if has multiblock
     */
    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    /**
     * Set multiblock state
     */
    public void setHasMultiblock(boolean has) {
        this.hasMultiblock = has;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        seesSky = compound.getBoolean("seesSky");
        hasMultiblock = compound.getBoolean("hasMultiblock");
        mode = compound.getInteger("mode");
        activeEntityId = compound.getInteger("activeEntityId");
        syncTick = compound.getInteger("syncTick");

        if (compound.hasKey("activeConstellation")) {
            String constellationName = compound.getString("activeConstellation");
            activeConstellation = ConstellationRegistry.getConstellationByName(constellationName);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("seesSky", seesSky);
        compound.setBoolean("hasMultiblock", hasMultiblock);
        compound.setInteger("mode", mode);
        compound.setInteger("activeEntityId", activeEntityId);
        compound.setInteger("syncTick", syncTick);

        if (activeConstellation != null) {
            compound.setString("activeConstellation", activeConstellation.getUnlocalizedName());
        }
    }
}
