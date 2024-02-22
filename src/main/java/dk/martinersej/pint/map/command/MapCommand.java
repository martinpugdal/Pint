package dk.martinersej.pint.map.command;

import dk.martinersej.pint.map.command.subcommand.*;
import dk.martinersej.pint.utils.command.Command;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MapCommand extends Command implements CommandExecutor, TabCompleter {

    public MapCommand(JavaPlugin plugin) {
        super(plugin);
        addSubCommand(new MapCreateSubCommand(plugin));
        addSubCommand(new MapDeleteSubCommand(plugin));
        addSubCommand(new MapSetSchematicSubCommand(plugin));
        addSubCommand(new MapListSubCommand(plugin));
        addSubCommand(new MapSetPlayersSubCommand(plugin));
        addSubCommand(new MapSetActiveSubCommand(plugin));
        addSubCommand(new MapSpawnpointsSubCommand(plugin));
        addSubCommand(new MapRegionsSubCommand(plugin));
        addSubCommand(new MapInfoSubCommand(plugin));
        addSubCommand(new MapTeleportSubCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        CommandResult result = super.execute(commandSender, strings);
        switch (result.getResult()) {
            case WRONG_USAGE:
                if (result.wrongUsageMessageIsPresent()) {
                    commandSender.sendMessage(result.getWrongUsageMessage());
                } else {
                    String subCommand = strings.length > 0 ? strings[0] : "";
                    commandSender.sendMessage("§cKorrekt brug: /" + command.getLabel() + " " + result.getSubCommand().getUsage(subCommand));
                }
                break;
            case NO_PERMISSION:
                commandSender.sendMessage("§cDu har ikke adgang til dette");
                break;
            case NO_SUB_COMMAND_FOUND:
                sendAllowedSubCommands(commandSender, strings);
                break;
            case SUB_COMMAND_NOT_EXISTS:
                commandSender.sendMessage("§cDenne subkommando findes ikke");
                sendAllowedSubCommands(commandSender, strings);
                break;
            case NO_CONSOLE:
                commandSender.sendMessage("§cDu kan ikke bruge denne kommando fra konsollen");
                break;
        }
        return true;
    }

    private void sendAllowedSubCommands(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage("§6Gyldige subkommandoer:");
        for (String cmdName : super.getAllowedSubCommands(commandSender, strings)) {
            SubCommand cmd = super.getSubCommandFromAlias(cmdName);
            commandSender.sendMessage("§8§m-§r §6" + cmd.getUsage(cmd.getAliases()[0]) + " §8§m-§r §e" + cmd.getDescription());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return getAllowedSubCommands(commandSender, strings);
    }
}
