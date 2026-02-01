/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Command Handler - /astralsorcery and /as commands
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.structure.StructureBuilder;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Astral Sorcery Command Handler
 * <p>
 * Provides the following commands:
 * <ul>
 * <li>/astralsorcery help - Display help</li>
 * <li>/astralsorcery constellations [player] - List/discover constellations</li>
 * <li>/astralsorcery progress [player] [all|next] - Show/modify progression tier</li>
 * <li>/astralsorcery research [player] <research|all> - Set research</li>
 * <li>/astralsorcery reset [player] - Reset player progression</li>
 * <li>/astralsorcery exp [player] <amount> - Set perk experience</li>
 * <li>/astralsorcery attune [player] <constellation> - Attune to constellation</li>
 * <li>/astralsorcery maximize [player] - Unlock everything</li>
 * <li>/astralsorcery build <structure> - Build multiblock structure at player position</li>
 * </ul>
 * <p>
 * Aliases: /as, /astralsorcery
 * Permission level: 2 (OP only)
 */
public class CommandAstralSorcery extends CommandBase {

    private static final String[] COMMANDS = new String[] { "help", "constellations", "research", "progress",
        "reset", "exp", "attune", "maximize", "build" };

    private final List<String> cmdAliases = new ArrayList<String>();

    public CommandAstralSorcery() {
        this.cmdAliases.add("astralsorcery");
        this.cmdAliases.add("as");
    }

    @Override
    public String getCommandName() {
        return "astralsorcery";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/astralsorcery <action> [player] [arguments...]";
    }

    @Override
    public List<String> getCommandAliases() {
        return cmdAliases;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // OP level
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        // Build command doesn't use player names
        if (args.length > 0 && "build".equalsIgnoreCase(args[0])) {
            return false;
        }
        return index == 1;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, COMMANDS);
        } else if (args.length == 2) {
            String identifier = args[0].toLowerCase();
            // Build command - complete structure names
            if ("build".equals(identifier)) {
                List<String> names = new ArrayList<String>();
                names.add("altarattunement");
                names.add("altarconstellation");
                names.add("altartrait");
                names.add("altarbrilliance");
                names.add("starlightinfuser");
                names.add("infuser");
                names.add("collectorrelay");
                names.add("relay");
                names.add("celestialgateway");
                names.add("gateway");
                names.add("ritualpedestal");
                names.add("pedestal");
                names.add("ancientshrine");
                names.add("shrine");
                names.add("smallshrine");
                names.add("treasureshrine");
                names.add("smallruin");
                names.add("ruin");
                return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
            }
            // Tab complete player names for other commands
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer()
                .getAllUsernames());
        } else if (args.length == 3) {
            String identifier = args[0].toLowerCase();
            if ("constellations".equals(identifier)) {
                List<String> names = new ArrayList<String>();
                for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                    names.add(c.getUnlocalizedName());
                }
                names.add("all");
                return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
            } else if ("research".equals(identifier)) {
                List<String> names = new ArrayList<String>();
                for (ResearchProgression r : ResearchProgression.values()) {
                    names.add(r.name());
                }
                names.add("all");
                return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
            } else if ("progress".equals(identifier)) {
                List<String> progressNames = new ArrayList<String>();
                progressNames.add("all");
                progressNames.add("next");
                return getListOfStringsMatchingLastWord(args, progressNames.toArray(new String[0]));
            } else if ("attune".equals(identifier)) {
                List<String> names = new ArrayList<String>();
                for (IConstellation c : ConstellationRegistry.getMajorConstellations()) {
                    names.add(c.getUnlocalizedName());
                }
                return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§cNot enough arguments."));
            sender.addChatMessage(new ChatComponentText("§cType \"/astralsorcery help\" for help"));
            return;
        }

        String identifier = args[0];
        if ("help".equalsIgnoreCase(identifier)) {
            displayHelp(sender);
        } else if ("constellation".equalsIgnoreCase(identifier) || "constellations".equalsIgnoreCase(identifier)) {
            if (args.length == 1) {
                listConstellations(sender);
            } else if (args.length == 2) {
                listConstellations(sender, args[1]);
            } else if (args.length == 3) {
                addConstellations(sender, args[1], args[2]);
            }
        } else if ("research".equalsIgnoreCase(identifier) || "res".equalsIgnoreCase(identifier)) {
            if (args.length == 3) {
                modifyResearch(sender, args[1], args[2]);
            }
        } else if ("progress".equalsIgnoreCase(identifier) || "prog".equalsIgnoreCase(identifier)) {
            if (args.length <= 2) {
                showProgress(sender, args.length == 1 ? sender.getCommandSenderName() : args[1]);
            } else if (args.length == 3) {
                modifyProgress(sender, args[1], args[2]);
            }
        } else if ("reset".equalsIgnoreCase(identifier)) {
            if (args.length == 2) {
                wipeProgression(sender, args[1]);
            }
        } else if ("charge".equalsIgnoreCase(identifier) || "exp".equalsIgnoreCase(identifier)) {
            if (args.length == 3) {
                setExp(sender, args[1], args[2]);
            }
        } else if ("attune".equalsIgnoreCase(identifier)) {
            if (args.length == 3) {
                attuneToConstellation(sender, args[1], args[2]);
            }
        } else if ("maximize".equalsIgnoreCase(identifier)) {
            if (args.length == 2) {
                maxAll(sender, args[1]);
            }
        } else if ("build".equalsIgnoreCase(identifier)) {
            if (args.length == 2) {
                buildStruct(sender, args[1]);
            } else {
                sender.addChatMessage(new ChatComponentText("§cUsage: /astralsorcery build <structure>"));
                listStructures(sender);
            }
        }
    }

    // ==================== Command Implementations ====================

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery constellation§7 - lists all constellations"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery constellation [playerName]§7 - lists all discovered constellations of the specified player"));
        sender.addChatMessage(new ChatComponentText(
            "§a/astralsorcery constellation [playerName] <cName;all>§7 - player discovers the specified constellation or all"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery progress [playerName]§7 - displays progress information about the player"));
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery progress [playerName] <all>§7 - maximize progression"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery progress [playerName] <next>§7 - advance to next progression tier"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery research [playerName] <research;all>§7 - set/add research"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery reset [playerName]§7 - resets all progression-related data for that player"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery maximize [playerName]§7 - unlocks everything for that player"));
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery exp [playerName] <exp>§7 - sets the perk exp for a player"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery attune [playerName] <majorConstellationName>§7 - sets the attunement constellation for a player"));
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery build <structure>§7 - build multiblock structure at your position"));
    }

    private void listConstellations(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§cMajor \"Bright\" Constellations:"));
        for (IConstellation c : ConstellationRegistry.getMajorConstellations()) {
            sender.addChatMessage(new ChatComponentText("§7" + c.getUnlocalizedName()));
        }
        sender.addChatMessage(new ChatComponentText("§cMinor \"Faint\" Constellations:"));
        for (IConstellation c : ConstellationRegistry.getMinorConstellations()) {
            if (c instanceof IMajorConstellation) continue;
            sender.addChatMessage(new ChatComponentText("§7" + c.getUnlocalizedName()));
        }
    }

    private void listConstellations(ICommandSender sender, String otherPlayerName) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        PlayerProgress progress = tuple.progress;

        sender.addChatMessage(new ChatComponentText("§c" + otherPlayerName + " has discovered the constellations:"));
        Collection<String> known = progress.getKnownConstellations();
        if (known.isEmpty()) {
            sender.addChatMessage(new ChatComponentText("§c NONE"));
            return;
        }
        for (String s : known) {
            sender.addChatMessage(new ChatComponentText("§7" + s));
        }
    }

    private void addConstellations(ICommandSender sender, String otherPlayerName, String argument) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        EntityPlayer other = tuple.player;

        if ("all".equals(argument)) {
            Collection<IConstellation> constellations = ConstellationRegistry.getAllConstellations();
            if (!ResearchManager.discoverConstellations(constellations, other)) {
                sender.addChatMessage(new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new ChatComponentText("§aDiscovered all Constellations!"));
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        } else {
            IConstellation c = ConstellationRegistry.getConstellationByName(argument);
            if (c == null) {
                sender.addChatMessage(new ChatComponentText("§cUnknown constellation: " + argument));
                return;
            }
            if (!ResearchManager.discoverConstellation(c, other)) {
                sender.addChatMessage(new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new ChatComponentText("§aDiscovered constellation " + c.getUnlocalizedName() + "!"));
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        }
    }

    private void showProgress(ICommandSender sender, String otherPlayerName) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        PlayerProgress progress = tuple.progress;
        EntityPlayer other = tuple.player;

        sender.addChatMessage(new ChatComponentText("§aPlayer " + otherPlayerName + "'s research Data:"));
        sender.addChatMessage(new ChatComponentText("§aProgression tier: " + progress.getTierReached()
            .name()));

        IConstellation attuned = progress.getAttunedConstellation();
        sender.addChatMessage(
            new ChatComponentText("§aAttuned to: " + (attuned == null ? "<none>" : attuned.getUnlocalizedName())));

        sender.addChatMessage(new ChatComponentText("§aPerk-Exp: " + progress.getPerkExp() + " - As level: " + progress
            .getPerkLevel(other)));

        sender.addChatMessage(new ChatComponentText("§aUnlocked perks:"));
        for (AbstractPerk perk : progress.getAppliedPerks()) {
            sender.addChatMessage(new ChatComponentText("§7" + perk.getUnlocalizedName()));
        }

        sender.addChatMessage(new ChatComponentText("§aUnlocked research groups:"));
        StringBuilder sb = new StringBuilder();
        for (ResearchProgression rp : progress.getResearchProgression()) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(rp.name());
        }
        sender.addChatMessage(new ChatComponentText("§7" + sb.toString()));

        sender.addChatMessage(new ChatComponentText("§aUnlocked constellations:"));
        sb = new StringBuilder();
        for (String str : progress.getKnownConstellations()) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(str);
        }
        sender.addChatMessage(new ChatComponentText("§7" + sb.toString()));
    }

    private void modifyProgress(ICommandSender sender, String otherPlayerName, String argument) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        PlayerProgress prog = tuple.progress;
        EntityPlayer other = tuple.player;

        if ("all".equalsIgnoreCase(argument)) {
            if (!ResearchManager.maximizeTier(other)) {
                sender.addChatMessage(new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
            } else {
                sender.addChatMessage(new ChatComponentText("§aMaximized ProgressionTier for " + otherPlayerName + " !"));
            }
        } else if ("next".equalsIgnoreCase(argument)) {
            ProgressionTier tier = prog.getTierReached();
            if (!tier.hasNextTier()) {
                sender.addChatMessage(
                    new ChatComponentText("§aPlayer " + otherPlayerName + " has already reached the highest tier!"));
            } else {
                ProgressionTier next = tier.next();
                ResearchManager.giveProgressionIgnoreFail(other, next);
                sender.addChatMessage(
                    new ChatComponentText("§aPlayer " + otherPlayerName + " advanced to Tier " + next.name() + "!"));
            }
        }
    }

    private void modifyResearch(ICommandSender sender, String otherPlayerName, String research) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        EntityPlayerMP other = tuple.player;

        if ("all".equalsIgnoreCase(research)) {
            ResearchManager.forceMaximizeResearch(other);
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        } else {
            ResearchProgression pr = ResearchProgression.getByEnumName(research);
            if (pr == null) {
                sender.addChatMessage(new ChatComponentText("§cFailed! Unknown research: " + research));
            } else {
                ResearchManager.unsafeForceGiveResearch(other, pr);
                sender.addChatMessage(new ChatComponentText("§aSuccess!"));
            }
        }
    }

    private void wipeProgression(ICommandSender sender, String otherPlayerName) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        EntityPlayerMP other = tuple.player;

        ResearchManager.wipeKnowledge(other);
        sender.addChatMessage(new ChatComponentText("§aWiped " + otherPlayerName + "'s data!"));
    }

    private void setExp(ICommandSender sender, String otherPlayerName, String strCharge) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        EntityPlayer other = tuple.player;

        long chargeToSet;
        try {
            chargeToSet = Long.parseLong(strCharge);
        } catch (NumberFormatException exc) {
            sender.addChatMessage(
                new ChatComponentText("§cFailed! Alignment charge to set should be a number! " + strCharge));
            return;
        }

        if (ResearchManager.setExp(other, chargeToSet)) {
            sender.addChatMessage(new ChatComponentText("§aSuccess! Player charge has been set to " + chargeToSet));
        } else {
            sender.addChatMessage(
                new ChatComponentText("§cFailed! Player specified doesn't seem to have a research progress!"));
        }
    }

    private void attuneToConstellation(ICommandSender sender, String otherPlayerName, String majorConstellationStr) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        EntityPlayer other = tuple.player;

        // Get constellation and check if it's a major constellation
        IConstellation c = ConstellationRegistry.getConstellationByName(majorConstellationStr);
        if (c == null || !(c instanceof IMajorConstellation)) {
            sender.addChatMessage(
                new ChatComponentText("§cFailed! Given constellation name is not a (major) constellation! " + majorConstellationStr));
            sender.addChatMessage(new ChatComponentText("§cSee '/astralsorcery constellations' to get all constellations!"));
            return;
        }
        IMajorConstellation cst = (IMajorConstellation) c;

        if (ResearchManager.setAttunedConstellation(other, cst)) {
            sender.addChatMessage(new ChatComponentText("§aSuccess! Player has been attuned to " + cst.getUnlocalizedName()));
        } else {
            sender.addChatMessage(
                new ChatComponentText("§cFailed! Player specified doesn't seem to have the research progress necessary!"));
        }
    }

    private void maxAll(ICommandSender sender, String otherPlayerName) {
        PlayerProgressTuple tuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (tuple == null) {
            return;
        }
        EntityPlayer other = tuple.player;

        ResearchManager.forceMaximizeAll(other);
        sender.addChatMessage(new ChatComponentText("§aSuccess!"));
    }

    private void buildStruct(ICommandSender sender, String structureName) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("§cThis command can only be used by players!"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;

        boolean success = StructureBuilder.buildByName(player.worldObj, x, y, z, structureName);

        if (success) {
            sender.addChatMessage(new ChatComponentText("§aSuccessfully built structure: " + structureName));
        } else {
            sender.addChatMessage(new ChatComponentText("§cFailed to build structure: " + structureName));
            sender.addChatMessage(new ChatComponentText("§cUse /astralsorcery build to list available structures"));
        }
    }

    private void listStructures(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§aAvailable structures:"));
        sender.addChatMessage(new ChatComponentText("§7Altars:"));
        sender.addChatMessage(new ChatComponentText("  - altarattunement"));
        sender.addChatMessage(new ChatComponentText("  - altarconstellation"));
        sender.addChatMessage(new ChatComponentText("  - altartrait"));
        sender.addChatMessage(new ChatComponentText("  - altarbrilliance"));
        sender.addChatMessage(new ChatComponentText("§7Machines:"));
        sender.addChatMessage(new ChatComponentText("  - starlightinfuser (infuser)"));
        sender.addChatMessage(new ChatComponentText("  - collectorrelay (relay)"));
        sender.addChatMessage(new ChatComponentText("  - celestialgateway (gateway)"));
        sender.addChatMessage(new ChatComponentText("§7Ritual:"));
        sender.addChatMessage(new ChatComponentText("  - ritualpedestal (pedestal)"));
        sender.addChatMessage(new ChatComponentText("§7World Gen:"));
        sender.addChatMessage(new ChatComponentText("  - ancientshrine (shrine)"));
        sender.addChatMessage(new ChatComponentText("  - smallshrine"));
        sender.addChatMessage(new ChatComponentText("  - treasureshrine"));
        sender.addChatMessage(new ChatComponentText("  - smallruin (ruin)"));
    }

    // ==================== Helper Methods ====================

    private PlayerProgressTuple tryGetProgressWithMessages(ICommandSender sender, String otherPlayerName) {
        EntityPlayerMP other;
        try {
            other = getPlayer(sender, otherPlayerName);
        } catch (PlayerNotFoundException e) {
            sender.addChatMessage(new ChatComponentText("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return null;
        }
        PlayerProgress progress = ResearchManager.getProgress(other);
        if (!progress.isValid()) {
            sender.addChatMessage(new ChatComponentText("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return null;
        }
        return new PlayerProgressTuple(other, progress);
    }

    /**
     * Tuple class for player and progress
     */
    private static class PlayerProgressTuple {
        public final EntityPlayerMP player;
        public final PlayerProgress progress;

        public PlayerProgressTuple(EntityPlayerMP player, PlayerProgress progress) {
            this.player = player;
            this.progress = progress;
        }
    }

    @Override
    public int compareTo(Object o) {
        return this.getCommandName()
            .compareTo(((CommandBase) o).getCommandName());
    }
}
