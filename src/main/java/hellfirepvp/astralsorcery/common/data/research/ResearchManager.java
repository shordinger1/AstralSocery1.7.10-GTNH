/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Research manager - Manages player research progression
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Research manager - Manages player research progression (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Player progress tracking</li>
 * <li>Constellation discovery</li>
 * <li>Research progression</li>
 * <li>Perk application/removal</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without network packets (for now)</li>
 * <li>Uses direct NBT save/load</li>
 * <li>No async IO thread</li>
 * </ul>
 */
public class ResearchManager {

    @SideOnly(Side.CLIENT)
    public static PlayerProgress clientProgress = new PlayerProgress();

    @SideOnly(Side.CLIENT)
    public static boolean clientInitialized = false;

    private static Map<UUID, PlayerProgress> playerProgressServer = new HashMap<>();

    /**
     * Get player progress
     *
     * @param player The player
     * @return The player progress
     */
    public static PlayerProgress getProgress(EntityPlayer player) {
        return getProgress(player, player.worldObj.isRemote ? Side.CLIENT : Side.SERVER);
    }

    /**
     * Get player progress for side
     *
     * @param player The player
     * @param side   The side
     * @return The player progress
     */
    public static PlayerProgress getProgress(EntityPlayer player, Side side) {
        if (side == Side.CLIENT) {
            return clientProgress;
        } else {
            return getProgressServer(player.getUniqueID());
        }
    }

    /**
     * Get server-side progress
     *
     * @param uuid The player UUID
     * @return The player progress
     */
    private static PlayerProgress getProgressServer(UUID uuid) {
        PlayerProgress progress = playerProgressServer.get(uuid);
        if (progress == null) {
            progress = new PlayerProgress();
            playerProgressServer.put(uuid, progress);
            LogHelper.debug("Created new PlayerProgress for " + uuid);
        }
        return progress;
    }

    /**
     * Discover constellation
     *
     * @param constellation The constellation
     * @param player        The player
     * @return true if discovered
     */
    public static boolean discoverConstellation(IConstellation constellation, EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.discoverConstellation(constellation.getUnlocalizedName());
        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Memorize constellation (seen but not discovered)
     *
     * @param constellation The constellation
     * @param player        The player
     * @return true if memorized
     */
    public static boolean memorizeConstellation(IConstellation constellation, EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.memorizeConstellation(constellation.getUnlocalizedName());
        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Set attuned constellation
     *
     * @param player        The player
     * @param constellation The constellation to attune
     * @return true if successful
     */
    public static boolean setAttunedConstellation(EntityPlayer player, IMajorConstellation constellation) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        if (constellation != null && !progress.getKnownConstellations()
            .contains(constellation.getUnlocalizedName())) {
            return false;
        }

        // Clear all perks first
        for (AbstractPerk perk : progress.getAppliedPerks()) {
            NBTTagCompound data = progress.getPerkData(perk);
            if (data != null) {
                perk.onRemovePerkServer(player, progress, data);
            }
        }
        progress.getAppliedPerks()
            .clear();
        progress.getUnlockedPerkData()
            .clear();

        progress.setExp(0);
        progress.setAttunedConstellation(constellation);

        // Apply root perk when attuning to constellation
        hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk root = hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree.PERK_TREE
            .getRootPerk(constellation);
        if (root != null) {
            NBTTagCompound data = new NBTTagCompound();
            root.onUnlockPerkServer(player, progress, data);
            progress.applyPerk(root, data);
        }

        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Apply perk
     *
     * @param player The player
     * @param perk   The perk to apply
     * @return true if successful
     */
    public static boolean applyPerk(EntityPlayer player, AbstractPerk perk) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasFreeAllocationPoint(player)) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        NBTTagCompound data = new NBTTagCompound();
        perk.onUnlockPerkServer(player, progress, data);
        progress.applyPerk(perk, data);

        perk.applyPerk(player, Side.SERVER);

        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Remove perk
     *
     * @param player The player
     * @param perk   The perk to remove
     * @return true if successful
     */
    public static boolean removePerk(EntityPlayer player, AbstractPerk perk) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        NBTTagCompound data = progress.getPerkData(perk);
        if (data == null) {
            return false;
        }

        progress.removePerk(perk);
        perk.removePerk(player, Side.SERVER);
        progress.removePerkData(perk);

        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Set perk data
     *
     * @param player       The player
     * @param perk         The perk
     * @param previousData Previous data
     * @param newData      New data
     * @return true if successful
     */
    public static boolean setPerkData(EntityPlayer player, AbstractPerk perk, NBTTagCompound previousData,
        NBTTagCompound newData) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;

        perk.modifyPerkServer(player, progress, newData);
        progress.applyPerk(perk, newData);

        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Modify perk experience
     *
     * @param player The player
     * @param exp    The experience to add
     * @return true if successful
     */
    public static boolean modifyExp(EntityPlayer player, double exp) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.modifyExp(exp, player);
        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Give research progression
     *
     * @param player The player
     * @param prog   The research progression
     * @return true if successful
     */
    public static boolean giveProgression(EntityPlayer player, ResearchProgression prog) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        ProgressionTier tier = prog.getRequiredProgress();
        if (!progress.getTierReached()
            .isThisLaterOrEqual(tier)) return false;

        if (progress.forceGainResearch(prog)) {
            savePlayerKnowledge(player);
            return true;
        }
        return false;
    }

    /**
     * Step progression tier
     *
     * @param player The player
     * @return true if successful
     */
    public static boolean stepTier(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        if (progress.stepTier()) {
            savePlayerKnowledge(player);
            return true;
        }
        return false;
    }

    /**
     * Grant free perk point
     *
     * @param player The player
     * @param token  The token
     * @return true if successful
     */
    public static boolean grantFreePerkPoint(EntityPlayer player, String token) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        if (!progress.grantFreeAllocationPoint(token)) {
            return false;
        }

        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Save player knowledge
     *
     * @param player The player
     */
    public static void savePlayerKnowledge(EntityPlayer player) {
        if (player.worldObj.isRemote) return;

        UUID uuid = player.getUniqueID();
        PlayerProgress progress = playerProgressServer.get(uuid);
        if (progress != null) {
            // TODO: Save to file
            LogHelper.debug("Saving PlayerProgress for " + player.getCommandSenderName());
        }
    }

    /**
     * Load player knowledge
     *
     * @param player The player
     */
    public static void loadPlayerKnowledge(EntityPlayer player) {
        if (player.worldObj.isRemote) return;

        UUID uuid = player.getUniqueID();
        PlayerProgress progress = playerProgressServer.get(uuid);
        if (progress == null) {
            progress = new PlayerProgress();
            playerProgressServer.put(uuid, progress);
            // TODO: Load from file
            LogHelper.debug("Loading PlayerProgress for " + player.getCommandSenderName());
        }
    }

    /**
     * Client-side: Receive progress from server
     *
     * @param progress The progress to receive
     */
    @SideOnly(Side.CLIENT)
    public static void receiveProgressFromServer(PlayerProgress progress) {
        clientProgress = progress;
        clientInitialized = true;
        LogHelper.debug("Received PlayerProgress from server");
    }

    /**
     * Clear server cache
     */
    public static void saveAndClearServerCache() {
        // TODO: Save all pending progress
        playerProgressServer.clear();
    }

    // ==================== Command Helper Methods ====================

    /**
     * Set perk experience directly (for commands)
     *
     * @param player The player
     * @param exp    The experience to set
     * @return true if successful
     */
    public static boolean setExp(EntityPlayer player, long exp) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.setExp((double) exp);
        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Discover multiple constellations at once
     *
     * @param constellations The constellations to discover
     * @param player         The player
     * @return true if successful
     */
    public static boolean discoverConstellations(java.util.Collection<IConstellation> constellations,
        EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        for (IConstellation c : constellations) {
            progress.discoverConstellation(c.getUnlocalizedName());
        }
        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Give progression ignoring tier requirements (for commands)
     *
     * @param player The player
     * @param tier   The tier to give
     */
    public static void giveProgressionIgnoreFail(EntityPlayer player, ProgressionTier tier) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return;

        progress.setTierReached(tier);
        savePlayerKnowledge(player);
    }

    /**
     * Maximize progression tier
     *
     * @param player The player
     * @return true if successful
     */
    public static boolean maximizeTier(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.setTierReached(ProgressionTier.BRILLIANCE);
        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Force give research (for commands)
     *
     * @param player The player
     * @param prog   The research progression
     */
    public static void unsafeForceGiveResearch(EntityPlayer player, ResearchProgression prog) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return;

        progress.forceGainResearch(prog);
        savePlayerKnowledge(player);
    }

    /**
     * Force maximize all research
     *
     * @param player The player
     */
    public static void forceMaximizeResearch(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return;

        for (ResearchProgression rp : ResearchProgression.values()) {
            progress.forceGainResearch(rp);
        }
        savePlayerKnowledge(player);
    }

    /**
     * Force maximize everything (tier, research, constellations)
     *
     * @param player The player
     */
    public static void forceMaximizeAll(EntityPlayer player) {
        // Maximize tier
        maximizeTier(player);

        // Maximize research
        forceMaximizeResearch(player);

        // Discover all constellations
        discoverConstellations(
            hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry.getAllConstellations(),
            player);
    }

    /**
     * Wipe all player knowledge (for reset command)
     *
     * @param player The player
     */
    public static void wipeKnowledge(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        playerProgressServer.remove(uuid);
        savePlayerKnowledge(player);
    }

}
