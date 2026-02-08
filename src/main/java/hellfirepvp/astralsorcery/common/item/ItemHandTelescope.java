/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Hand Telescope Item - Constellation observation and discovery
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgressProperties;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Hand Telescope Item
 * <p>
 * Allows players to discover constellations during nighttime.
 * <p>
 * Features:
 * - Right-click to discover visible constellations
 * - Shows constellation discovery messages
 * - Updates player progress
 * - Works only at night with visible sky
 */
public class ItemHandTelescope extends AstralBaseItem {

    public ItemHandTelescope() {
        super(1); // Max stack size 1
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            // Client-side: play sound or effect
            return stack;
        }

        // Server-side: discover constellations
        LogHelper.debug("Telescope used by " + player.getCommandSenderName());

        // Check if it's night time
        ConstellationSkyHandler skyHandler = ConstellationSkyHandler.getInstance();
        if (!skyHandler.isNight(world)) {
            player.addChatMessage(new ChatComponentText("§cYou can only see constellations at night!"));
            return stack;
        }

        // Check if player can see sky
        if (!world.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ)) {
            player.addChatMessage(new ChatComponentText("§cYou need a clear view of the sky!"));
            return stack;
        }

        // Get currently visible constellations
        List<IConstellation> visibleConstellations = skyHandler.getVisibleConstellations(world);

        if (visibleConstellations.isEmpty()) {
            player.addChatMessage(new ChatComponentText("§eNo constellations are visible right now."));
            return stack;
        }

        // Get player progress
        PlayerProgress progress = PlayerProgressProperties.getProgress(player);

        // Discover all visible constellations
        int discoveredCount = 0;
        int alreadyKnownCount = 0;

        for (IConstellation constellation : visibleConstellations) {
            if (progress.hasConstellationDiscovered(constellation)) {
                alreadyKnownCount++;
            } else {
                // Discover the constellation
                boolean discovered = ResearchManager.discoverConstellation(constellation, player);
                if (discovered) {
                    discoveredCount++;
                    LogHelper.info(
                        "Player " + player.getCommandSenderName()
                            + " discovered constellation: "
                            + constellation.getUnlocalizedName());
                }
            }
        }

        // Send discovery messages
        if (discoveredCount > 0) {
            player.addChatMessage(
                new ChatComponentText(
                    "§aYou discovered " + discoveredCount
                        + " new constellation"
                        + (discoveredCount > 1 ? "s" : "")
                        + "!"));

            // List discovered constellations
            for (IConstellation constellation : visibleConstellations) {
                if (!progress.hasConstellationDiscovered(constellation)) {
                    continue; // Skip already known
                }
                // Show constellation name
                String name = constellation.getUnlocalizedName() + ".name";
                player.addChatMessage(new ChatComponentText("  §f- §6" + name));
            }

            // Check if player can now attune to a constellation
            checkAttunation(player, progress);
        } else if (alreadyKnownCount > 0) {
            player.addChatMessage(
                new ChatComponentText(
                    "§eYou see " + alreadyKnownCount
                        + " constellation"
                        + (alreadyKnownCount > 1 ? "s" : "")
                        + ", but you already know them all."));
        } else {
            player.addChatMessage(new ChatComponentText("§eNo new constellations to discover."));
        }

        return stack;
    }

    /**
     * Check if player can attune to a constellation
     */
    private void checkAttunation(EntityPlayer player, PlayerProgress progress) {
        // Player can attune if they know at least one major constellation
        // and haven't attuned yet
        if (progress.getAttunedConstellation() == null) {
            List<IMajorConstellation> majorConstellations = ConstellationRegistry.getMajorConstellations();

            for (IMajorConstellation constellation : majorConstellations) {
                if (progress.hasConstellationDiscovered(constellation)) {
                    player.addChatMessage(
                        new ChatComponentText("§eYou can now attune to a constellation using an Attunement Altar!"));
                    break;
                }
            }
        }
    }
}
