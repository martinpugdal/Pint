package dk.martinersej.pint.command;

import dk.martinersej.pint.command.map.*;
import dk.martinersej.pint.utils.command.Command;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class MapCommand extends Command implements CommandExecutor, TabCompleter {

    public MapCommand(JavaPlugin plugin) {
        super(plugin);
        addSubCommand(new MapCreateSubCommand(plugin));
        addSubCommand(new MapSetSchematicSubCommand(plugin));
        addSubCommand(new MapListSubCommand(plugin));
        addSubCommand(new MapSetPlayersSubCommand(plugin));
        addSubCommand(new MapSetActiveSubCommand(plugin));
        addSubCommand(new MapSpawnpointsSubCommand(plugin));
        addSubCommand(new MapRegionsSubCommand(plugin));
        addSubCommand(new MapInfoSubCommand(plugin));
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
                    commandSender.sendMessage("Korrekt brug: /" + command.getLabel() + " " + result.getSubCommand().getUsage(subCommand));
                }
                break;
            case NO_PERMISSION:
                commandSender.sendMessage("§cDu har ikke adgang til dette");
                break;
            case NO_SUB_COMMAND_FOUND:
                commandSender.sendMessage("Det er ikke en gyldig subkommando");
                commandSender.sendMessage("Gyldige subkommandoer:");
                for (SubCommand cmd : super.getSubCommands()) {
                    commandSender.sendMessage("- " + cmd.getUsage(cmd.getAliases()[0]) + " - " + cmd.getDescription());
                }
                break;
            case NO_CONSOLE:
                commandSender.sendMessage("§cDu kan ikke bruge denne kommando fra konsollen");
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return getAllowedSubCommands(commandSender, command, s, strings);
    }
}
