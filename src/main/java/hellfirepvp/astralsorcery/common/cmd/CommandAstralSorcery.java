/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd;

import java.lang.reflect.Field;
import java.util.*;
import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;

import hellfirepvp.astralsorcery.common.auxiliary.StarlightNetworkDebugHandler;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.migration.LegacyDataMigration;
import hellfirepvp.astralsorcery.common.registry.RegistryStructures;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandAstralSorcery
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:39
 */
public class CommandAstralSorcery extends CommandBase {

    private static final String[] COMMANDS = new String[] { "help", "constellations", "research", "progress", "reset",
        "exp", "attune", "build", "maximize", "slnetwork", "migrate-data" };

    private List<String> cmdAliases = new ArrayList<>();

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
        return 2;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, COMMANDS);
        } else {
            String identifier = args[0].toLowerCase();
            if ("build".equals(identifier)) {
                Field[] fields = MultiBlockArrays.class.getDeclaredFields();
                List<String> names = new ArrayList<>(fields.length);

                for (Field f : fields) {
                    if (f.isAnnotationPresent(MultiBlockArrays.PasteBlacklist.class)) {
                        continue;
                    }
                    names.add(f.getName());
                }
                return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
            } else if (args.length == 2) {
                List<String> playerNames = new ArrayList<>();
                for (Object player : MinecraftServer.getServer()
                    .getConfigurationManager().playerEntityList) {
                    if (player instanceof EntityPlayerMP) {
                        playerNames.add(((EntityPlayerMP) player).getCommandSenderName());
                    }
                }
                return getListOfStringsMatchingLastWord(args, playerNames.toArray(new String[0]));
            } else if (args.length == 3) {
                switch (identifier) {
                    case "constellations": {
                        List<String> names = new ArrayList<>();
                        for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                            names.add(c.getUnlocalizedName());
                        }
                        names.add("all");
                        return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
                    }
                    case "research": {
                        List<String> names = new ArrayList<>();
                        for (ResearchProgression r : ResearchProgression.values()) {
                            names.add(r.name());
                        }
                        names.add("all");
                        return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
                    }
                    case "progress":
                        List<String> progressNames = new ArrayList<>();
                        progressNames.add("all");
                        progressNames.add("next");
                        return getListOfStringsMatchingLastWord(args, progressNames.toArray(new String[0]));
                    case "attune": {
                        List<String> names = new ArrayList<>();
                        for (IConstellation c : ConstellationRegistry.getMajorConstellations()) {
                            names.add(c.getUnlocalizedName());
                        }
                        return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
                    }
                    default:
                        break;
                }

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
        if (args.length >= 1) {
            String identifier = args[0];
            if ("help".equalsIgnoreCase(identifier)) {
                displayHelp(sender);
            } else if ("migrate-data".equalsIgnoreCase(identifier)) {
                migrateAllLegacyData(sender);
            } else if ("slnetwork".equalsIgnoreCase(identifier)) {
                tryEnterSLNetworkDebugMode(sender);
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
            } else if ("build".equalsIgnoreCase(identifier)) {
                if (args.length == 2) {
                    buildStruct(sender, args[1]);
                } else {
                    RegistryStructures.init(); // Reload
                }
            } else if ("maximize".equalsIgnoreCase(identifier)) {
                if (args.length == 2) {
                    maxAll(sender, args[1]);
                }
            }
        }
    }

    private void migrateAllLegacyData(ICommandSender sender) {
        LegacyDataMigration.migrateRockCrystalData(s -> sender.addChatMessage(new ChatComponentText(s)));

        sender.addChatMessage(new ChatComponentText("Data migration finished."));
    }

    private void tryEnterSLNetworkDebugMode(ICommandSender sender) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("This command can only be executed by a player!"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        // 1.7.10: Use capabilities.isCreativeMode instead of isCreative()
        if (!player.capabilities.isCreativeMode) {
            sender.addChatMessage(new ChatComponentText("§cYou have to be in creative-mode to use the debug mode!"));
            return;
        }
        StarlightNetworkDebugHandler.INSTANCE.awaitDebugInteraction(
            player,
            () -> sender.addChatMessage(new ChatComponentText("§cStarlight network debug-rightclick timed out.")));
        sender.addChatMessage(
            new ChatComponentText(
                "§aRightclick a block within 20 seconds to collect information about its starlight network activity."));
    }

    private void attuneToConstellation(ICommandSender sender, String otherPlayerName, String majorConstellationStr) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;

        IMajorConstellation cst = ConstellationRegistry.getMajorConstellationByName(majorConstellationStr);
        if (cst == null) {
            sender.addChatMessage(
                new ChatComponentText(
                    "§cFailed! Given constellation name is not a (major) constellation! " + majorConstellationStr));
            sender.addChatMessage(
                new ChatComponentText("§cSee '/astralsorcery constellations' to get all constellations!"));
            return;
        }

        if (ResearchManager.setAttunedConstellation(other, cst)) {
            sender.addChatMessage(
                new ChatComponentText("§aSuccess! Player has been attuned to " + cst.getUnlocalizedName()));
        } else {
            sender.addChatMessage(
                new ChatComponentText(
                    "§cFailed! Player specified doesn't seem to have the research progress necessary!"));
        }
    }

    private void setExp(ICommandSender sender, String otherPlayerName, String strCharge) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;

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

    private void modifyResearch(ICommandSender sender, String otherPlayerName, String research) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayerMP other = prTuple.key;

        if ("all".equalsIgnoreCase(research)) {
            ResearchManager.forceMaximizeResearch(other);
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        } else {
            ResearchProgression pr = ResearchProgression.getByEnumName(research);
            if (pr == null) {
                sender.addChatMessage(new ChatComponentText("§cFailed! Unknown research: " + research));
            } else {
                /*
                 * ProgressionTier pt = pr.getRequiredProgress();
                 * ResearchManager.giveProgressionIgnoreFail(other, pt);
                 */
                ResearchManager.unsafeForceGiveResearch(other, pr);
                sender.addChatMessage(new ChatComponentText("§aSuccess!"));
            }
        }
    }

    private void maxAll(ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;

        ResearchManager.forceMaximizeAll(other);
        sender.addChatMessage(new ChatComponentText("§aSuccess!"));
    }

    private void buildStruct(ICommandSender sender, String name) {
        BlockArray array;
        try {
            Field f = MultiBlockArrays.class.getDeclaredField(name);
            f.setAccessible(true);
            if (f.isAnnotationPresent(MultiBlockArrays.PasteBlacklist.class)) {
                sender.addChatMessage(
                    new ChatComponentText(
                        "§cFailed! You may not paste " + name
                            + ", as it may be unstable or may have other unwanted effects!"));
                return;
            }
            array = (BlockArray) f.get(null);
        } catch (NoSuchFieldException e) {
            sender.addChatMessage(new ChatComponentText("§cFailed! " + name + " doesn't exist!"));
            return;
        } catch (IllegalAccessException e) {
            return; // doesn't happen
        }
        EntityPlayer exec;
        try {
            exec = getCommandSenderAsPlayer(sender);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("§cFailed! Couldn't find you as player in the world!"));
            return;
        }
        MovingObjectPosition res = MiscUtils.rayTraceLook(exec, 60);
        if (res == null) {
            sender.addChatMessage(new ChatComponentText("§cFailed! Couldn't find the block you're looking at?"));
            return;
        }
        BlockPos hit;
        switch (res.typeOfHit) {
            case BLOCK:
                hit = new BlockPos(res.blockX, res.blockY, res.blockZ);
                break;
            case MISS:
            case ENTITY:
            default:
                sender.addChatMessage(new ChatComponentText("§cFailed! Couldn't find the block you're looking at?"));
                return;
        }
        sender.addChatMessage(new ChatComponentText("§aStarting to build " + name + " at " + hit.toString() + "!"));
        array.placeInWorld(exec.worldObj, hit);
        sender.addChatMessage(new ChatComponentText("§aBuilt " + name + "!"));
    }

    private void wipeProgression(ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayerMP other = prTuple.key;

        ResearchManager.wipeKnowledge(other);
        sender.addChatMessage(new ChatComponentText("§aWiped " + otherPlayerName + "'s data!"));
    }

    private void modifyProgress(ICommandSender sender, String otherPlayerName, String argument) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress prog = prTuple.value;
        EntityPlayer other = prTuple.key;
        if ("all".equalsIgnoreCase(argument)) {
            if (!ResearchManager.maximizeTier(other)) {
                sender.addChatMessage(
                    new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
            } else {
                sender
                    .addChatMessage(new ChatComponentText("§aMaximized ProgressionTier for " + otherPlayerName + " !"));
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

    private void showProgress(ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        sender.addChatMessage(new ChatComponentText("§aPlayer " + otherPlayerName + "'s research Data:"));

        sender.addChatMessage(
            new ChatComponentText(
                "§aProgression tier: " + progress.getTierReached()
                    .name()));
        sender.addChatMessage(
            new ChatComponentText(
                "§aAttuned to: " + (progress.getAttunedConstellation() == null ? "<none>"
                    : progress.getAttunedConstellation()
                        .getUnlocalizedName())));
        sender.addChatMessage(
            new ChatComponentText(
                "§aPerk-Exp: " + progress.getPerkExp() + " - As level: " + progress.getPerkLevel(other)));
        sender.addChatMessage(new ChatComponentText("§aUnlocked perks + unlock-level:"));
        for (AbstractPerk perk : progress.getAppliedPerks()) {
            sender.addChatMessage(new ChatComponentText("§7" + (perk.getUnlocalizedName() + ".name")));
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

    private void addConstellations(ICommandSender sender, String otherPlayerName, String argument) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;
        if ("all".equals(argument)) {
            Collection<IConstellation> constellations = ConstellationRegistry.getAllConstellations();
            if (!ResearchManager.discoverConstellations(constellations, other)) {
                sender.addChatMessage(
                    new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
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
                sender.addChatMessage(
                    new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new ChatComponentText("§aDiscovered constellation " + c.getUnlocalizedName() + "!"));
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        }
    }

    private void listConstellations(ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        sender.addChatMessage(new ChatComponentText("§c" + otherPlayerName + " has discovered the constellations:"));
        if (progress.getKnownConstellations()
            .size() == 0) {
            sender.addChatMessage(new ChatComponentText("§c NONE"));
            return;
        }
        for (String s : progress.getKnownConstellations()) {
            sender.addChatMessage(new ChatComponentText("§7" + s));
        }
    }

    private Tuple<EntityPlayerMP, PlayerProgress> tryGetProgressWithMessages(ICommandSender sender,
        String otherPlayerName) {
        EntityPlayerMP other;
        try {
            other = getPlayer(sender, otherPlayerName);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return null;
        }
        PlayerProgress progress = ResearchManager.getProgress(other);
        if (!progress.isValid()) {
            sender.addChatMessage(new ChatComponentText("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return null;
        }
        return new Tuple<>(other, progress);
    }

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery constellation§7 - lists all constellations"));
        sender.addChatMessage(
            new ChatComponentText(
                "§a/astralsorcery constellation [playerName]§7 - lists all discovered constellations of the specified player if he/she is online"));
        sender.addChatMessage(
            new ChatComponentText(
                "§a/astralsorcery constellation [playerName] <cName;all>§7 - player specified discovers the specified constellation or all or resets all"));
        sender.addChatMessage(
            new ChatComponentText(
                "§a/astralsorcery progress [playerName]§7 - displays progress information about the player (Enter no player to view your own)"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery progress [playerName] <all>§7 - maximize progression"));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery research [playerName] <research;all>§7 - set/add Research"));
        sender.addChatMessage(
            new ChatComponentText(
                "§a/astralsorcery reset [playerName]§7 - resets all progression-related data for that player."));
        sender.addChatMessage(
            new ChatComponentText(
                "§a/astralsorcery build [structure]§7 - builds the named structure wherever the player is looking at."));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery maximize [playerName]§7 - unlocks everything for that player."));
        sender.addChatMessage(
            new ChatComponentText("§a/astralsorcery exp [playerName] <exp>§7 - sets the perk exp for a player"));
        sender.addChatMessage(
            new ChatComponentText(
                "§a/astralsorcery attune [playerName] <majorConstellationName>§7 - sets the attunement constellation for a player"));
        sender.addChatMessage(
            new ChatComponentText(
                "§a/astralsorcery slnetwork§7 - Executing player enters StarlightNetwork debug mode for the next block"));
    }

    private void listConstellations(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§cMajor \"Bright\" Constellations:"));
        for (IMajorConstellation c : ConstellationRegistry.getMajorConstellations()) {
            sender.addChatMessage(new ChatComponentText("§7" + c.getUnlocalizedName()));
        }
        sender.addChatMessage(new ChatComponentText("§Weak \"Dim\" Constellations:"));
        for (IWeakConstellation c : ConstellationRegistry.getWeakConstellations()) {
            if (c instanceof IMajorConstellation) continue;
            sender.addChatMessage(new ChatComponentText("§7" + c.getUnlocalizedName()));
        }
        sender.addChatMessage(new ChatComponentText("§cMinor \"Faint\" Constellations:"));
        for (IMinorConstellation c : ConstellationRegistry.getMinorConstellations()) {
            sender.addChatMessage(new ChatComponentText("§7" + c.getUnlocalizedName()));
        }
    }

}
